package com.dobbinsoft.netting.server;

/**
 * Netty 连接层代码
 * 连接层只能依赖 adapter ，不能直接依赖im，这样才能使业务和连接解耦
 */