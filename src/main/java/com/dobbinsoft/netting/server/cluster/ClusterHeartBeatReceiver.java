package com.dobbinsoft.netting.server.cluster;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
@Singleton
public class ClusterHeartBeatReceiver {

    @Inject
    private ClusterServerHandler clusterServerHandler;

    public void doServer() {
        // 使用一个线程同步和建立新
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(clusterServerHandler);

            b.bind(PropertyUtils.getPropertyInt("server.cluster.port"))
                    .sync().channel().closeFuture().await();
            log.info("[ClusterServer] shutdown!");
        }catch (Exception e) {
            log.error("[ClusterServer] 异常", e);
            System.exit(0);
        } finally {
            group.shutdownGracefully();
        }
    }

    @Singleton
    @ChannelHandler.Sharable
    public static class ClusterServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

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

}
