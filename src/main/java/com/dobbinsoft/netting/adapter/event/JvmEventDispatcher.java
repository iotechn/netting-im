package com.dobbinsoft.netting.adapter.event;

import com.dobbinsoft.netting.im.application.event.AbstractEventHandler;
import com.dobbinsoft.netting.server.cluster.objects.ClusterNode;
import com.dobbinsoft.netting.server.cluster.ClusterNodeMapper;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.dobbinsoft.netting.server.event.EventDispatcher;
import com.dobbinsoft.netting.server.event.IOEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.util.concurrent.Future;

import java.util.List;


/**
 * 消息通过Jvm直接调用的 事件转发器
 */
@Singleton
public class JvmEventDispatcher extends EventDispatcher {

    @Inject
    private TerminalRepository terminalRepository;

    @Inject
    private ClusterNodeMapper clusterNodeMapper;

    @Override
    public void dispatchToServer(IOEvent ioEvent, Terminal terminal) {
        int eventCode = ioEvent.eventCode();
        AbstractEventHandler abstractEventHandler = eventHandlerMap.get(eventCode);
        Future<List<String>> future = abstractEventHandler.handle(ioEvent, terminal == null ? null : terminal.getJwtToken());
        super.processEvent(future, terminal);
    }

    @Override
    public void dispatchToTerminal(IOEvent ioEvent, String businessUserId) {
        Terminal terminal = terminalRepository.findByBusinessUserId(businessUserId);
        terminal.getChannel().writeAndFlush(terminal.getProtocolWrapper().wrap(ioEvent.toMessage()));
    }

    @Override
    public boolean dispatchToTerminal(String ioEvent, String businessUserId) {
        Terminal terminal = terminalRepository.findByBusinessUserId(businessUserId);
        if (terminal != null) {
            terminal.getChannel().writeAndFlush(terminal.getProtocolWrapper().wrap(ioEvent));
            return true;
        } else {
            // 1. 路由到其他节点
            ClusterNode clusterNode = clusterNodeMapper.get(businessUserId);
            if (clusterNode != null) {
                return clusterNode.dispatchToTerminal(ioEvent, businessUserId);
            }
            // 2. Terminal确实不在线
            return false;
        }
    }


}
