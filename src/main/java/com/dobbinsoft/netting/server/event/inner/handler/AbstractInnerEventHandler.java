package com.dobbinsoft.netting.server.event.inner.handler;

import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.event.IOEvent;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractInnerEventHandler<T extends IOEvent> {

    private static Map<Integer, AbstractInnerEventHandler> innerEventHandlerMap = new HashMap<>();

    public static void register(Integer eventCode, AbstractInnerEventHandler handler) {
        innerEventHandlerMap.put(eventCode, handler);
        log.info("[Inner Event] register code={},handler={}", eventCode, handler.getClass().getSimpleName());
    }

    public static AbstractInnerEventHandler getHandler(int code) {
        return innerEventHandlerMap.get(code);
    }

    public abstract void handle(T t, Terminal terminal);

    public abstract Class<T> eventClass();

}
