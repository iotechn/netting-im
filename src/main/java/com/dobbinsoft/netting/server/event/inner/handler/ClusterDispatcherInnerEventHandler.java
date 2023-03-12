package com.dobbinsoft.netting.server.event.inner.handler;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.dobbinsoft.netting.server.event.inner.ClusterDispatcherInnerEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class ClusterDispatcherInnerEventHandler extends AbstractInnerEventHandler<ClusterDispatcherInnerEvent> {

    @Inject
    private TerminalRepository terminalRepository;

    @Override
    public void handle(ClusterDispatcherInnerEvent clusterDispatcherInnerEvent, Terminal nullTerminal) {
        String businessUserId = clusterDispatcherInnerEvent.getBusinessUserId();
        Terminal terminal = terminalRepository.findByBusinessUserId(businessUserId);
        if (terminal != null) {
            terminal.getChannel().writeAndFlush(terminal.getProtocolWrapper().wrap(clusterDispatcherInnerEvent.getIoEvent()));
        } else {
            log.error("[ClusterDispatcher] terminal is null, event={}", JsonUtils.toJson(clusterDispatcherInnerEvent));
        }

    }

    @Override
    public Class<ClusterDispatcherInnerEvent> eventClass() {
        return ClusterDispatcherInnerEvent.class;
    }

}
