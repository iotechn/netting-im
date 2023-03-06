package com.dobbinsoft.netting.server.domain.repository;

import com.dobbinsoft.netting.base.utils.StringUtils;
import com.dobbinsoft.netting.server.cluster.ClusterNode;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class TerminalRepository {

    /**
     * Index By ChannelID
     */
    private Map<String, Terminal> terminalMap = new ConcurrentHashMap<>();

    /**
     * Index By BusinessUserId
     */
    private Map<String, Terminal> businessUserIdMap = new ConcurrentHashMap<>();

    public Terminal findById(String id){
        return terminalMap.get(id);
    }

    public Terminal findByBusinessUserId(String businessUserId) {
        return businessUserIdMap.get(businessUserId);
    }

    public void save(Terminal terminal) {
        String id = terminal.getId();
        terminalMap.put(id, terminal);
        if (StringUtils.isNotEmpty(terminal.getBusinessUserId())) {
            Terminal terminalExist = businessUserIdMap.get(terminal.getBusinessUserId());
            if (terminalExist != null && terminal.getChannel() != terminalExist.getChannel()) {
                terminalExist.getChannel().disconnect();
            }
            businessUserIdMap.put(terminal.getBusinessUserId(), terminal);
        }
    }

    public void remove(String id) {
        terminalMap.remove(id);
    }

    public void remove(Terminal terminal) {
        terminalMap.remove(terminal.getId());
        if (StringUtils.isNotEmpty(terminal.getBusinessUserId())) {
            businessUserIdMap.remove(terminal.getBusinessUserId());
        }
    }

}
