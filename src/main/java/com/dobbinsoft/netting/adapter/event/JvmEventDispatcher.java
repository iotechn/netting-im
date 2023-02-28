package com.dobbinsoft.netting.adapter.event;

import com.dobbinsoft.netting.im.application.event.AbstractEventHandler;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.values.BusinessUserId;
import com.dobbinsoft.netting.server.event.EventDispatcher;
import com.dobbinsoft.netting.server.event.IOEvent;
import com.google.inject.Singleton;
import io.netty.util.concurrent.Future;

import java.util.List;


/**
 * 消息通过Jvm直接调用的 事件转发器
 */
@Singleton
public class JvmEventDispatcher extends EventDispatcher {

    @Override
    public void dispatchToServer(IOEvent ioEvent, Terminal terminal) {
        int eventCode = ioEvent.eventCode();
        AbstractEventHandler abstractEventHandler = eventHandlerMap.get(eventCode);
        Future<List<String>> future = abstractEventHandler.handle(ioEvent, terminal == null ? null : terminal.getJwtToken());
        super.processEvent(future, terminal == null ? null : terminal.getChannel());
    }

    @Override
    public void dispatchToTerminal(IOEvent ioEvent, BusinessUserId businessUserId) {
        String userId = businessUserId.getUserId();
    }


}
