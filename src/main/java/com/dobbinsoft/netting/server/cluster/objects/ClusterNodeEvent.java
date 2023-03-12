package com.dobbinsoft.netting.server.cluster.objects;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterNodeEvent {

    public static final int EVENT_STARTUP = 1;

    public static final int TERMINAL_AUTHORIZED = 2;

    public static final int TERMINAL_DISCONNECTED = 3;

    public ClusterNodeEvent(int event) {
        this.event = event;
    }

    public Integer event;

    private String businessUserId;

    private ClusterNode clusterNode;

}
