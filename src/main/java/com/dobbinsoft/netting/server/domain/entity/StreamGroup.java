package com.dobbinsoft.netting.server.domain.entity;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class StreamGroup {

    private String businessGroupId;

    private String name;

    private Set<Terminal> members = new ConcurrentSet<>();


}
