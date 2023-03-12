package com.dobbinsoft.netting.server.event;

import com.dobbinsoft.netting.base.utils.CollectionUtils;
import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.StringUtils;
import com.dobbinsoft.netting.im.application.event.AbstractEventHandler;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.event.inner.handler.AbstractInnerEventHandler;
import com.dobbinsoft.netting.server.event.inner.handler.AuthorizedInnerEventHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author w.wei
 * @version 1.0
 * @description: 事件路由器，用于转发到合适位置
 * @date 2023/2/22
 */
@Slf4j
public abstract class EventDispatcher {

    protected static Map<Integer, AbstractEventHandler> eventHandlerMap = new ConcurrentHashMap<>();

    public static void register(AbstractEventHandler handler) {
        eventHandlerMap.put(handler.eventCode(), handler);
    }

    public abstract void dispatchToServer(IOEvent ioEvent, Terminal terminal);

    public abstract void dispatchToTerminal(IOEvent ioEvent, String businessUserId);

    public abstract boolean dispatchToTerminal(String ioEvent, String businessUserId);

    public Class getEventClass(Integer eventCode) {
        AbstractEventHandler abstractEventHandler = eventHandlerMap.get(eventCode);
        if (abstractEventHandler != null) {
            return abstractEventHandler.eventClass();
        }
        return null;
    }

    protected void processEvent(Future<List<String>> future, Terminal terminal) {
        if (future != null) {
            future.addListener(f -> {
                try {
                    List<String> events = (List<String>) f.getNow();
                    if (CollectionUtils.isNotEmpty(events)) {
                        for (String event : events) {
                            String[] eventCodeAndBody = StringUtils.getHeadAndBody(event);
                            int code = Integer.parseInt(eventCodeAndBody[0]);
                            if (code < 0) {
                                // 内置事件 通过InnerEvent 的 eventCode 找到对应的处理器
                                AbstractInnerEventHandler handler = AuthorizedInnerEventHandler.getHandler(code);
                                if (handler != null) {
                                    IOEvent ioEvent = (IOEvent) JsonUtils.parse(eventCodeAndBody[1], handler.eventClass());
                                    handler.handle(ioEvent, terminal == null ? null : terminal.getChannel());
                                } else {
                                    log.warn("[Inner Event Process] event code not support: {}", code);
                                }
                            } else if (code > 0){
                                // 直接转发Event
                                terminal.getChannel().writeAndFlush(terminal.getProtocolWrapper().wrap(event));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("[Inner Event Process] error", e);
                }
            });
        }
    }

}
