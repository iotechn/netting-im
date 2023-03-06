package com.dobbinsoft.netting.server.event.inner;

import com.dobbinsoft.netting.server.event.IOEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClusterDispatcherInnerEvent implements IOEvent {

    private String businessUserId;

    private String ioEvent;

    @Override
    public int eventCode() {
        return IOEvent.INNER_EVENT_CLUSTER_DISPATCHER;
    }
}
