package com.dobbinsoft.netting.server.domain.repository;

import com.dobbinsoft.netting.base.ext.ReadWriteHashMap;
import com.dobbinsoft.netting.server.domain.entity.StreamGroup;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class StreamGroupRepository {

    public Map<String, StreamGroup> streamGroupMap = new ReadWriteHashMap<>();

    public void save(StreamGroup streamGroup) {
        streamGroupMap.put(streamGroup.getBusinessGroupId(), streamGroup);
    }

    public StreamGroup findByBusinessGroupId(String businessId) {
        return streamGroupMap.get(businessId);
    }

}
