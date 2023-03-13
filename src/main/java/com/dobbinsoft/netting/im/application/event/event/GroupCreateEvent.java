package com.dobbinsoft.netting.im.application.event.event;

import com.dobbinsoft.netting.im.application.event.IMEventCodes;
import com.dobbinsoft.netting.im.application.event.IMEventPermissionKeys;
import com.dobbinsoft.netting.server.event.IOEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupCreateEvent implements IOEvent {

    private String name;

    private List<String> businessUserIds;

    @Override
    public int eventCode() {
        return IMEventCodes.CREATE_GROUP;
    }

    @Override
    public String permissionKey() {
        return IMEventPermissionKeys.GROUP_CREATE;
    }
}
