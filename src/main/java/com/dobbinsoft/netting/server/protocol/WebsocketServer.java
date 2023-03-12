package com.dobbinsoft.netting.server.protocol;

import com.dobbinsoft.netting.adapter.event.JvmEventDispatcher;
import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.base.utils.StringUtils;
import com.dobbinsoft.netting.server.cluster.ClusterNodeSynchronizer;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.dobbinsoft.netting.server.event.IOEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class WebsocketServer {

    @Inject
    private NettyWebSocketTextServerHandler nettyWebSocketTextServerHandler;

    @Inject
    private NettyWebSocketBinaryServerHandler nettyWebSocketBinaryServerHandler;



    public void doServer() {
        //开启第一个线程用于接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //第二个线程组用于编解码
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ChannelFuture f = new ServerBootstrap().group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            pipeline.addLast("codec-http", new HttpServerCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(PropertyUtils.getPropertyInt("server.ws.max-content-length")));
                            pipeline.addLast("ws-protocol", new WebSocketServerProtocolHandler("/ws/terminal"));
                            pipeline.addLast("ws-event", nettyWebSocketTextServerHandler);
                            pipeline.addLast("ws-binary", nettyWebSocketBinaryServerHandler);
                        }
                    })
                    .bind(PropertyUtils.getPropertyInt("server.ws.port")).sync();
            //监听服务器关闭监听
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("[HttpWebServer] 异常", e);
            System.exit(0);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Singleton
    @ChannelHandler.Sharable
    public static class NettyWebSocketTextServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

        @Inject
        // The impl of eventDispatcher can be customized. You can use rpc replace jvm call.
        // 此处 eventDispatcher 可以被定制，你可以使用rpc来代替jvm调用
        private JvmEventDispatcher eventDispatcher;

        @Inject
        private WebsocketProtocolWrapper websocketProtocolWrapper;

        @Inject
        private TerminalRepository terminalRepository;

        @Inject
        private ClusterNodeSynchronizer clusterNodeSynchronizer;

        // 读取客户端发送的请求报文
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            String text = msg.text();
            String[] textArray = StringUtils.getHeadAndBody(text);
            int code = Integer.parseInt(textArray[0]);
            Class<? extends IOEvent> eventClass = eventDispatcher.getEventClass(code);
            if (eventClass != null) {
                IOEvent ioEvent = JsonUtils.parse(textArray[1], eventClass);
                Terminal terminal = terminalRepository.findById(ctx.channel().id().asLongText());
                eventDispatcher.dispatchToServer(ioEvent, terminal);
            } else {
                log.info("[Terminal WS] Invalid event code: {}", code);
            }
        }
        // 当web客户端连接后，触发该方法
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            // ctx.channel().id() 表示唯一的值
            Channel channel = ctx.channel();
            Terminal terminal = new Terminal(channel.id().asLongText());
            terminal.setChannel(channel);
            terminal.setProtocolWrapper(websocketProtocolWrapper);
            terminalRepository.save(terminal);
            log.info("[Terminal Ws] Connected id=" + terminal.getId());
            // TODO 增加鉴权时间限制，第一个包必须是鉴权
        }
        // 客户端离线
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            // ctx.channel().id() 表示唯一的值
            String terminalId = ctx.channel().id().asLongText();

            Boolean clusterServer = PropertyUtils.getPropertyBoolean("server.cluster");
            if (clusterServer) {
                Terminal terminal = terminalRepository.findById(terminalId);
                eventDispatcher.dispatchToServer(disconnectInnerEvent, terminal);
            }
            terminalRepository.remove(terminalId);
            log.info("[Terminal Ws] Disconnected id=" + terminalId);
        }
        // 处理异常
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("[Terminal Ws] Exception", cause);
            terminalRepository.remove(ctx.channel().id().asLongText());
            ctx.channel().close();

            Boolean clusterServer = PropertyUtils.getPropertyBoolean("server.cluster");
            if (clusterServer) {

            }
        }
    }

    @Singleton
    @ChannelHandler.Sharable
    public static class NettyWebSocketBinaryServerHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

        @Inject
        private TerminalRepository terminalRepository;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame binaryWebSocketFrame) throws Exception {
            Terminal terminal = terminalRepository.findById(ctx.channel().id().asLongText());
            ByteBuf content = binaryWebSocketFrame.content();
            terminal.sendStream(content);
        }
    }

}
