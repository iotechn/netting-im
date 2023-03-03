package com.dobbinsoft.netting.server.event.inner;

import com.dobbinsoft.netting.server.event.IOEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StreamGroupJoinInnerEvent implements IOEvent {

    private String businessUserId;

    private String businessGroupId;

    @Override
    public int eventCode() {
        return IOEvent.INNER_EVENT_GROUP_STREAM_JOIN;
    }
}
