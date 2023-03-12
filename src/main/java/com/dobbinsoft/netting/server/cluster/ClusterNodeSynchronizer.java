package com.dobbinsoft.netting.server.cluster;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.base.utils.StringUtils;
import com.dobbinsoft.netting.server.cluster.objects.ClusterNode;
import com.dobbinsoft.netting.server.cluster.objects.ClusterNodeEvent;
import com.dobbinsoft.netting.server.cluster.objects.ClusterNodeFullTerminals;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

@Slf4j
@Singleton
public class ClusterNodeSynchronizer {

    @Inject
    private ClusterClientHandler clusterClientHandler;

    private static volatile Channel channel;

    private static volatile String clusterNodeJson;

    public static volatile ClusterNode clusterNode;


    public void init() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            String network = PropertyUtils.getProperty("network");
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress addr = InetAddress.getLocalHost();
            outer: while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) {
                        if (!inetAddress.getHostAddress().equals("127.0.0.1")) {
                            if (StringUtils.isEmpty(network) || network.equals(networkInterface.getName())) {
                                addr = inetAddress;
                                break outer;
                            }
                        }
                    }
                }
            }
            ClusterNode newClusterNode = new ClusterNode();
            newClusterNode.setHostAddress(addr.getHostAddress());
            newClusterNode.setHostName(addr.getHostName());
            newClusterNode.setWsServerPort(PropertyUtils.getPropertyInt("server.ws.port"));
            clusterNodeJson = getMulticastBody(newClusterNode);
            clusterNode = newClusterNode;
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(clusterClientHandler);
            channel = b.bind(PropertyUtils.getPropertyInt("server.cluster.port")).sync().channel();

            ChannelFuture channelFuture = channel.closeFuture();
            Integer period = PropertyUtils.getPropertyInt("server.cluster.heart-period");
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> log.info("[ClusterHeartBeat] shutdown!"));
            while (!Thread.interrupted()) {
                sendEvent(new ClusterNodeEvent(ClusterNodeEvent.EVENT_STARTUP));
                sendHeartBeat();
                Thread.sleep(period);
            }
        } catch (Exception e) {
            log.error("[ClusterClient] 异常", e);
        } finally {
            group.shutdownGracefully();
        }
    }

    @Singleton
    @ChannelHandler.Sharable
    public static class ClusterClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        @Inject
        private ClusterNodeMapper clusterNodeMapper;

        @Inject
        private TerminalRepository terminalRepository;

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
            // TODO UDP 最大长度的分包问题
            String data = datagramPacket.content().toString(StandardCharsets.UTF_8);
            String[] headAndBody = StringUtils.getHeadAndBody(data);
            Class<?> clazz = Class.forName(headAndBody[0]);
            Object obj = JsonUtils.parse(data, clazz);
            if (obj instanceof ClusterNode) {
                clusterNodeMapper.heartBeatCluster((ClusterNode) obj);
            } else if (obj instanceof ClusterNodeEvent) {
                // 将自身所有的Terminal同步给局域网
                ClusterNodeEvent clusterNodeEvent = (ClusterNodeEvent) obj;
                if (clusterNodeEvent.getEvent() == ClusterNodeEvent.EVENT_STARTUP) {
                    Set<String> userIds = terminalRepository.businessUserIds();
                    ClusterNodeFullTerminals clusterNodeFullTerminals = new ClusterNodeFullTerminals();
                    clusterNodeFullTerminals.setClusterNode(ClusterNodeSynchronizer.clusterNode);
                    clusterNodeFullTerminals.setTerminalBusinessUserIds(userIds);
                    ClusterNodeSynchronizer.multicast(getMulticastBody(clusterNodeFullTerminals));
                } else if (clusterNodeEvent.getEvent() == ClusterNodeEvent.TERMINAL_AUTHORIZED) {
                    clusterNodeMapper.put(clusterNodeEvent.getBusinessUserId(), clusterNodeEvent.getClusterNode());
                } else if (clusterNodeEvent.getEvent() == ClusterNodeEvent.TERMINAL_DISCONNECTED) {
                    clusterNodeMapper.remove(clusterNodeEvent.getBusinessUserId(), clusterNodeEvent.getClusterNode());
                }
            } else if (obj instanceof ClusterNodeFullTerminals) {
                ClusterNodeFullTerminals clusterNodeFullTerminals = (ClusterNodeFullTerminals) obj;
                for (String terminalBusinessUserId : clusterNodeFullTerminals.getTerminalBusinessUserIds()) {
                    clusterNodeMapper.put(terminalBusinessUserId, clusterNodeFullTerminals.getClusterNode());
                }
            }
        }
    }


    /**
     * Server 中其他事件
     * @param event
     */
    public void sendEvent(ClusterNodeEvent event) {
        Boolean clusterServer = PropertyUtils.getPropertyBoolean("server.cluster");
        if (clusterServer) {
            multicast(getMulticastBody(event));
        }
    }

    private void sendHeartBeat() {
        multicast(ClusterNodeSynchronizer.clusterNodeJson);
    }


    /**
     * TODO 改为真正的组播
     * @param body
     */
    private static void multicast(String body) {
        List<String> clusterList = getClusterList();
        for (String cluster : clusterList) {
            if (cluster.equals(clusterNode.getHostAddress())) {
                continue;
            }
            channel.writeAndFlush(
                    new DatagramPacket(
                            Unpooled.copiedBuffer(body, CharsetUtil.UTF_8),
                            new InetSocketAddress(cluster, PropertyUtils.getPropertyInt("server.cluster.port"))));
        }
    }

    private static volatile List<String> clusterListCached = null;
    private static List<String> getClusterList() {
        if (clusterListCached == null) {
            synchronized (ClusterNodeSynchronizer.class) {
                if (clusterListCached == null) {
                    // 向网段类所有机器广播发UDP
                    String clusters = PropertyUtils.getProperty("server.cluster.clusters");
                    String[] clusterArray = clusters.split(",");
                    List<String> clusterList = new ArrayList<>();

                    for (String cluster : clusterArray) {
                        if (cluster.contains("*")) {
                            for (int i = 1; i < 255; i++) {
                                clusterList.add(cluster.replace("*", "" + i));
                            }
                        } else {
                            clusterList.add(cluster);
                        }
                    }
                    clusterListCached = clusterList;
                }
            }
        }
        return clusterListCached;
    }

    public static String getMulticastBody(Object obj) {
        return obj.getClass().getName() + "|" + JsonUtils.toJson(obj);
    }

}
