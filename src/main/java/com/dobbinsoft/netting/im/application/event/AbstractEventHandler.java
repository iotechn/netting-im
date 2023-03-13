package com.dobbinsoft.netting.im.application.event;

import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.event.IOEvent;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public abstract class AbstractEventHandler<T extends IOEvent> {

    public static final DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(10);

    public abstract Class<T> eventClass();

    public abstract int eventCode();

    /**
     * 业务处理完成后，可以直接传递一些事件 给 "事件产生者Terminal"。
     * 这样设计可以减少集群间的路由。
     * @param t
     * @param jwtToken
     * @return
     */
    public abstract Future<List<String>> handle(T t, Terminal jwtToken);

}
