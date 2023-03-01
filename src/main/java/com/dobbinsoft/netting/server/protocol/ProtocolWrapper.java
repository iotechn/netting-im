package com.dobbinsoft.netting.server.protocol;


@FunctionalInterface
public interface ProtocolWrapper {

    Object wrap(String message);

}
