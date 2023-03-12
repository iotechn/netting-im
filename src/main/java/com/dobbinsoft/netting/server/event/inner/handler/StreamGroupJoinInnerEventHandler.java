package com.dobbinsoft.netting.server.event.inner.handler;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.server.domain.entity.StreamGroup;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.repository.StreamGroupRepository;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.dobbinsoft.netting.server.event.inner.StreamGroupJoinInnerEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class StreamGroupJoinInnerEventHandler extends AbstractInnerEventHandler<StreamGroupJoinInnerEvent>{

    @Inject
    private StreamGroupRepository streamGroupRepository;

    @Inject
    private TerminalRepository terminalRepository;

    @Override
    public void handle(StreamGroupJoinInnerEvent streamGroupJoinInnerEvent, Terminal terminal) {
        if (terminal != null) {
            StreamGroup streamGroup = streamGroupRepository.findByBusinessGroupId(streamGroupJoinInnerEvent.getBusinessGroupId());
            terminal.followStreamGroup(streamGroup);
        } else {
            log.error("[StreamGroup] terminal is empty, event={}", JsonUtils.toJson(streamGroupRepository));
        }
    }

    @Override
    public Class<StreamGroupJoinInnerEvent> eventClass() {
        return StreamGroupJoinInnerEvent.class;
    }
}
