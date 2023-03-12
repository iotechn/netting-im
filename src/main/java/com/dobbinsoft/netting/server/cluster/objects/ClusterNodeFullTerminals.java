package com.dobbinsoft.netting.server.cluster.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ClusterNodeFullTerminals {

    private ClusterNode clusterNode;

    private Set<String> terminalBusinessUserIds;

}
