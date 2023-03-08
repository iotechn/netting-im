package com.dobbinsoft.netting.server.cluster;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.base.utils.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
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
import java.util.Enumeration;

@Slf4j
@Singleton
public class ClusterHeartBeatPusher {

    @Inject
    private ClusterClientHandler clusterClientHandler;

    private Channel channel;

    private String clusterNodeJson;

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
            ClusterNode clusterNode = new ClusterNode();
            clusterNode.setHostAddress(addr.getHostAddress());
            clusterNode.setHostName(addr.getHostName());
            clusterNode.setWsServerPort(PropertyUtils.getPropertyInt("server.ws.port"));
            this.clusterNodeJson = JsonUtils.toJson(clusterNode);
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(clusterClientHandler);
            this.channel = b.bind(0).sync().channel();

            ChannelFuture channelFuture = this.channel.closeFuture();
            Integer period = PropertyUtils.getPropertyInt("server.cluster.heart-period");
            while (!Thread.interrupted()) {
                sendHeartBeat();
                Thread.sleep(period);
            }
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> log.info("[ClusterHeartBeat] shutdown!"));
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

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
            String data = datagramPacket.content().toString(StandardCharsets.UTF_8);
            ClusterNode clusterNode = JsonUtils.parse(data, ClusterNode.class);
            clusterNodeMapper.heartBeatCluster(clusterNode);
            log.info("[Received UDP Data]");
        }
    }

    public void sendHeartBeat() {
        // 向网段类所有机器广播发UDP
        this.channel.writeAndFlush(
                new DatagramPacket(
                        Unpooled.copiedBuffer(clusterNodeJson, CharsetUtil.UTF_8),
                        new InetSocketAddress(
                                PropertyUtils.getProperty("server.cluster.net-segment"), PropertyUtils.getPropertyInt("server.cluster.port")
                        )));
    }

}
