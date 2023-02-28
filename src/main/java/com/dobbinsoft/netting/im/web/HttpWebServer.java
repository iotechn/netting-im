package com.dobbinsoft.netting.im.web;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.im.exception.ImErrorCode;
import com.dobbinsoft.netting.im.exception.ImException;
import com.dobbinsoft.netting.im.web.vo.WebResult;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Singleton
@Slf4j
public class HttpWebServer {

    @Inject
    private HttpWebHandler httpWebHandler;

    public void doServer() {
        //开启第一个线程用于接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //第二个线程组用于编解码
        EventLoopGroup workGroup = new NioEventLoopGroup();
        //第三个线程组用于处理业务
        DefaultEventLoopGroup businessWorkGroup = new DefaultEventLoopGroup(20);
        try {
            ChannelFuture f = new ServerBootstrap().group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            pipeline.addLast("codec-http", new HttpServerCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            pipeline.addLast(businessWorkGroup, "im-business", httpWebHandler);
                        }
                    })
                    .bind(PropertyUtils.getPropertyInt("server.im-api.port")).sync();
            //监听服务器关闭监听
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("[HttpWebServer] 异常", e);
            System.exit(0);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            businessWorkGroup.shutdownGracefully();
        }
    }

    @Singleton
    @ChannelHandler.Sharable
    public static class HttpWebHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Inject
        private HttpServiceRouter httpServiceRouter;


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
            WebResult webResult;
            try {
                HttpMethod httpMethod = httpRequest.method();
                String uri = httpRequest.uri();
                if (uri.equals("/im/token")) {
                    if (httpMethod != HttpMethod.GET) {
                        webResult = WebResult.error(ImErrorCode.HTTP_NOT_SUPPORT);
                    } else {
                        // 走获取token逻辑
                        HttpHeaders headers = httpRequest.headers();
                        String clientId = headers.get("clientId");
                        String clientSecret = headers.get("clientSecret");
                        // TODO token 获取业务逻辑
                        String token = "";
                        webResult = WebResult.success(token);
                    }

                } else if (uri.equals("/im/openapi")){
                    if (httpMethod != HttpMethod.POST) {
                        webResult = WebResult.error(ImErrorCode.HTTP_NOT_SUPPORT);
                    } else {
                        // 走开放API逻辑
                        try {
                            HttpHeaders headers = httpRequest.headers();
                            String group = headers.get("group");
                            String method = headers.get("method");
                            // Body
                            ByteBuf content = httpRequest.content();
                            byte[] bytes = new byte[content.readableBytes()];
                            content.readBytes(bytes);
                            String body = new String(bytes, StandardCharsets.UTF_8);
                            // Business Call
                            Object call = httpServiceRouter.call(group, method, body);
                            webResult = WebResult.success(call);
                        } catch (ImException e) {
                            webResult = WebResult.fail(e);
                        }
                    }
                } else {
                    webResult = WebResult.error(ImErrorCode.API_NOT_EXIST);
                }
            } catch (Exception e) {
                log.error("[Netty Web Server] 异常", e);
                webResult = WebResult.error(ImErrorCode.SERVICE_ERROR);
            }
            // 回复信息给浏览器
            ByteBuf byteBuf = Unpooled.copiedBuffer(JsonUtils.toJson(webResult), CharsetUtil.UTF_8);
            // 构造一个http响应体，即HttpResponse
            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            // 设置响应头信息
            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf8");
            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            // 将响应体写入到通道中
            ctx.writeAndFlush(defaultFullHttpResponse);
        }
    }

}
