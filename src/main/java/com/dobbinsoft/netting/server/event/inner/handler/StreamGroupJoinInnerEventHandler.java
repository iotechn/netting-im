package com.dobbinsoft.netting.server.event.inner.handler;

import com.dobbinsoft.netting.server.domain.entity.StreamGroup;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.repository.StreamGroupRepository;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.dobbinsoft.netting.server.event.inner.StreamGroupJoinInnerEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;

@Singleton
public class StreamGroupJoinInnerEventHandler extends AbstractInnerEventHandler<StreamGroupJoinInnerEvent>{

    @Inject
    private StreamGroupRepository streamGroupRepository;

    @Inject
    private TerminalRepository terminalRepository;

    @Override
    public void handle(StreamGroupJoinInnerEvent streamGroupJoinInnerEvent, Channel channel) {
        Terminal terminal = terminalRepository.findByBusinessUserId(streamGroupJoinInnerEvent.getBusinessUserId());
        StreamGroup streamGroup = streamGroupRepository.findByBusinessGroupId(streamGroupJoinInnerEvent.getBusinessGroupId());
        terminal.followStreamGroup(streamGroup);
    }

    @Override
    public Class<StreamGroupJoinInnerEvent> eventClass() {
        return StreamGroupJoinInnerEvent.class;
    }
}
