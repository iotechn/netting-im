package com.dobbinsoft.netting.server.domain.repository;

import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class TerminalRepository {

    private Map<String, Terminal> terminalMap = new ConcurrentHashMap<>();

    public Terminal findById(String id){
        return terminalMap.get(id);
    }

    public void save(Terminal terminal) {
        String id = terminal.getId();
        terminalMap.put(id, terminal);
    }

    public void remove(String id) {
        terminalMap.remove(id);
    }

}
