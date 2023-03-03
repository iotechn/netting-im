package com.dobbinsoft.netting.server.protocol;

import com.google.inject.Singleton;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@Singleton
public class WebsocketProtocolWrapper implements ProtocolWrapper {
    @Override
    public Object wrap(String message) {
        return new TextWebSocketFrame(message);
    }

    @Override
    public Object wrapBytes(ByteBuf byteBuf) {
        return new BinaryWebSocketFrame(byteBuf);
    }
}
