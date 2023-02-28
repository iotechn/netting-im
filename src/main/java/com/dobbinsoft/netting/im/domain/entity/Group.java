package com.dobbinsoft.netting.im.domain.entity;

import com.dobbinsoft.netting.im.infrastructure.po.GroupPO;
import com.dobbinsoft.netting.im.infrastructure.po.GroupUserPO;
import com.dobbinsoft.netting.im.infrastructure.po.UserPO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author w.wei
 * @version 1.0
 * @description: Group
 * @date 2023/2/23
 */
@Getter
@Setter
public class Group {

    private Long id;

    private String name;

    private List<User> members;

    private String remark;

    public static final Group from(GroupPO groupPO, GroupUserPO groupUserPO, List<UserPO> userPOS) {
        Group group = new Group();
        group.setId(groupPO.getId());
        group.setName(groupPO.getName());
        group.setMembers(User.from(userPOS));
        if (groupUserPO != null) {
            group.setRemark(groupUserPO.getRemark());
        }
        return group;
    }

}
