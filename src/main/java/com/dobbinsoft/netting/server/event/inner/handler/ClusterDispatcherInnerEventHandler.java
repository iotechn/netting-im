package com.dobbinsoft.netting.server.event.inner.handler;

import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.dobbinsoft.netting.server.event.inner.ClusterDispatcherInnerEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;

@Singleton
public class ClusterDispatcherInnerEventHandler extends AbstractInnerEventHandler<ClusterDispatcherInnerEvent> {

    @Inject
    private TerminalRepository terminalRepository;

    @Override
    public void handle(ClusterDispatcherInnerEvent clusterDispatcherInnerEvent, Channel channel) {
        Terminal terminal = terminalRepository.findByBusinessUserId(clusterDispatcherInnerEvent.getBusinessUserId());
        if (terminal != null) {
            terminal.getChannel().writeAndFlush(terminal.getProtocolWrapper().wrap(clusterDispatcherInnerEvent.getIoEvent()));
        }

    }

    @Override
    public Class<ClusterDispatcherInnerEvent> eventClass() {
        return ClusterDispatcherInnerEvent.class;
    }

}
