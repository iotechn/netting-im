package com.dobbinsoft.netting.server.cluster;

import com.dobbinsoft.netting.base.ext.ReadWriteHashMap;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.server.cluster.objects.ClusterNode;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
public class ClusterNodeMapper {

    /**
     * Index By BusinessUserId for Machine
     */
    private Map<String, ClusterNode> businessUserIdClusterNodeMap = new ConcurrentHashMap<>();

    /**
     * The cached clusterNode.
     * To avoid many the same objects be in the memory.
     */
    private Map<ClusterNode, ClusterNode> clusterNodes = new ReadWriteHashMap<>();

    public ClusterNodeMapper() {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                Set<ClusterNode> set = clusterNodes.keySet();
                for (ClusterNode clusterNode : set) {
                    Long expireIn = clusterNode.getExpireIn();
                    if (expireIn < System.currentTimeMillis()) {
                        clusterNodes.remove(clusterNode);
                        log.info("[Cluster Clear] Node offline: {}", clusterNode);
                        Set<String> businessUserIds = new HashSet<>();
                        businessUserIdClusterNodeMap.forEach((k, v) -> {
                            if (v == clusterNode) {
                                businessUserIds.add(k);
                            }
                        });
                        for (String businessUserId : businessUserIds) {
                            businessUserIdClusterNodeMap.remove(businessUserId);
                        }
                    }
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "Cluster-Clear-Thread").start();
    }

    public void heartBeatCluster(ClusterNode clusterNode) {
        ClusterNode clusterNodeExist = putClusterNodeIfNotAbsent(clusterNode);
        Integer heartPeriod = PropertyUtils.getPropertyInt("server.cluster.heart-period");
        clusterNodeExist.setExpireIn(System.currentTimeMillis() + (heartPeriod * 2));
    }

    public void put(String businessUserId, ClusterNode clusterNode) {
        clusterNode = putClusterNodeIfNotAbsent(clusterNode);
        businessUserIdClusterNodeMap.put(businessUserId, clusterNode);
    }

    public void remove(String businessUserId, ClusterNode clusterNode) {
        ClusterNode clusterNodeExist = get(businessUserId);
        if (clusterNodeExist != null) {
            if (clusterNodeExist.equals(clusterNode)) {
                businessUserIdClusterNodeMap.remove(businessUserId);
            }
        }
    }

    public ClusterNode get(String businessUserId) {
        return businessUserIdClusterNodeMap.get(businessUserId);
    }

    private ClusterNode putClusterNodeIfNotAbsent(ClusterNode clusterNode) {
        ClusterNode clusterNodeFromCache = clusterNodes.get(clusterNode);
        if (clusterNodeFromCache == null) {
            // New Node Join
            clusterNode.init();
            clusterNodes.put(clusterNode, clusterNode);
        } else {
            clusterNode = clusterNodeFromCache;
        }
        return clusterNode;
    }


}
