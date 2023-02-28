package com.dobbinsoft.netting.im.domain.repository;

import com.dobbinsoft.netting.im.domain.entity.Group;
import com.dobbinsoft.netting.im.infrastructure.mapper.GroupMapper;
import com.dobbinsoft.netting.im.infrastructure.mapper.GroupUserMapper;
import com.dobbinsoft.netting.im.infrastructure.mapper.UserMapper;
import com.dobbinsoft.netting.im.infrastructure.po.GroupPO;
import com.dobbinsoft.netting.im.infrastructure.po.GroupUserPO;
import com.dobbinsoft.netting.im.infrastructure.po.UserPO;
import com.dobbinsoft.netting.im.infrastructure.utils.SessionUtils;
import com.google.inject.Guice;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author w.wei
 * @version 1.0
 * @description: GroupRepository
 * @date 2023/2/23
 */
@Singleton
public class GroupRepository {

    @Inject
    private GroupMapper groupMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private GroupUserMapper groupUserMapper;

    public Group findById(Long groupId) {
        GroupPO groupPO = groupMapper.findById(groupId);
        List<GroupUserPO> groupUsers = groupUserMapper.findByGroupId(groupId);
        Map<Long, GroupUserPO> groupUserMap = groupUsers.stream().collect(Collectors.toMap(GroupUserPO::getUserId, Function.identity()));
        List<UserPO> userPos = userMapper.findByIds(groupUserMap.keySet());
        if (SessionUtils.hasLogin()) {
            return Group.from(groupPO, groupUserMap.get(SessionUtils.getUser().getId()), userPos);
        } else {
            return Group.from(groupPO, null, userPos);
        }
    }

}
