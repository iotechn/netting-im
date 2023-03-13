package com.dobbinsoft.netting.server.cluster.objects;

import com.dobbinsoft.netting.base.utils.JwtUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.server.event.inner.ClusterDispatcherInnerEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.HashMap;

@Getter
@Setter
@Slf4j
@EqualsAndHashCode(exclude = {"expireIn", "channel"})
public class ClusterNode {

    private String hostName;

    private String hostAddress;

    private Integer wsServerPort;

    /**
     * The node will be removed from zhe clusters.
     */
    private Long expireIn;

    private Channel channel;

    static EventLoopGroup group;

    public boolean dispatchToTerminal(String ioEvent, String businessUserId) {
        ClusterDispatcherInnerEvent innerEvent = new ClusterDispatcherInnerEvent();
        innerEvent.setIoEvent(ioEvent);
        innerEvent.setBusinessUserId(businessUserId);
        HashMap<String, String> body = new HashMap<>();
        body.put("businessUserId", businessUserId);
        innerEvent.setSign(JwtUtils.createHMAC256(new HashMap<>(), body, 15, PropertyUtils.getProperty("server.cluster.sign-key")));
        channel.writeAndFlush(new TextWebSocketFrame(innerEvent.toMessage()));
        return true;
    }

    @Override
    public String toString() {
        return hostName + "-" + hostAddress + "-" + wsServerPort;
    }

    public void init() {
        if (channel == null) {
            synchronized (this) {
                if (channel == null) {
                    group = new NioEventLoopGroup(1);
                    try {
                        URI uri = new URI("ws://" + hostAddress + ":" + wsServerPort + "/ws/terminal");
                        WebSocketClientHandler handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory
                                .newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));

                        Bootstrap b = new Bootstrap();
                        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                p.addLast(new HttpClientCodec())
                                        .addLast(new HttpObjectAggregator(PropertyUtils.getPropertyInt("server.ws.max-content-length")))
                                        .addLast(handler);
                            }
                        });
                        Channel ch = b.connect(uri.getHost(), wsServerPort).sync().channel();
                        handler.handshakeFuture().sync();
                        this.channel = ch;
                    } catch (Exception e) {
                        log.error("[ClusterNode] WS Client 异常", e);
                    } finally {
//                        group.shutdownGracefully();
                    }
                }
            }
        }
    }

    public static class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

        private ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        private final WebSocketClientHandshaker handshaker;
        private ChannelPromise handshakeFuture;

        public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
            this.handshaker = handshaker;
        }

        public ChannelFuture handshakeFuture() {
            return handshakeFuture;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            handshakeFuture = ctx.newPromise();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            handshaker.handshake(ctx.channel());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            log.info("WebSocket Client 链接失败!");
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            Channel ch = ctx.channel();
            if (!handshaker.isHandshakeComplete()) {
                try {
                    handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                    log.info("WebSocket Client connected!");
                    handshakeFuture.setSuccess();
                } catch (WebSocketHandshakeException e) {
                    log.info("WebSocket Client failed to connect");
                    handshakeFuture.setFailure(e);
                }
                return;
            }

            if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.getStatus()
                        + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
            }

            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame) {
                // TODO 这里的消息是要转发给终端的，但是理论上不会到这里，因为转发到终端，一定会走他自己的路由
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
//                ctx.channel().writeAndFlush(textFrame.text());
                log.info("WebSocket Client received message: " + textFrame.text());
            } else if (frame instanceof PongWebSocketFrame) {
                log.info("WebSocket Client received pong");
            } else if (frame instanceof CloseWebSocketFrame) {
                log.info("WebSocket Client received closing");
                if (group != null) {
                    group.shutdownGracefully();
                }
                ch.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("[ClusterNode] As Client 异常", cause);
            if (!handshakeFuture.isDone()) {
                handshakeFuture.setFailure(cause);
            }
            if (group != null) {
                group.shutdownGracefully();
            }
            ctx.close();
        }

    }

}
