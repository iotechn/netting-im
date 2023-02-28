package com.dobbinsoft.netting.server.domain.values;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessUserId {

    private Integer machineId;

    private String userId;

    @Override
    public String toString() {
        return machineId + "|" + userId;
    }
}
