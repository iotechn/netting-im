package com.dobbinsoft.netting.im.application.event.handler;

import com.dobbinsoft.netting.im.application.event.AbstractEventHandler;
import com.dobbinsoft.netting.im.application.event.IMEventCodes;
import com.dobbinsoft.netting.im.application.event.event.GroupCreateEvent;
import com.dobbinsoft.netting.im.domain.entity.Group;
import com.dobbinsoft.netting.im.domain.repository.GroupRepository;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.util.concurrent.Future;

import java.util.List;

@Singleton
public class GroupCreateEventHandler extends AbstractEventHandler<GroupCreateEvent> {

    @Inject
    private GroupRepository groupRepository;

    @Override
    public Class<GroupCreateEvent> eventClass() {
        return GroupCreateEvent.class;
    }

    @Override
    public int eventCode() {
        return IMEventCodes.CREATE_GROUP;
    }

    @Override
    public Future<List<String>> handle(GroupCreateEvent groupCreateEvent, Terminal terminal) {
        Group group = new Group();
        group.setName(groupCreateEvent.getName());
//        group.setMembers();
        return null;
    }
}
