package com.dobbinsoft.netting.server.protocol;


import io.netty.buffer.ByteBuf;

public interface ProtocolWrapper {

    Object wrap(String message);

    Object wrapBytes(ByteBuf byteBuf);

}
