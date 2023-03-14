package com.dobbinsoft.netting.im.domain.entity;

import com.dobbinsoft.netting.IOC;
import com.dobbinsoft.netting.im.domain.enums.UserRelationType;
import com.dobbinsoft.netting.im.infrastructure.mapper.UserFieldMapper;
import com.dobbinsoft.netting.im.infrastructure.mapper.UserMapper;
import com.dobbinsoft.netting.im.infrastructure.mapper.UserRelationMapper;
import com.dobbinsoft.netting.im.infrastructure.po.UserFieldPO;
import com.dobbinsoft.netting.im.infrastructure.po.UserPO;
import com.dobbinsoft.netting.im.infrastructure.po.UserRelationPO;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author w.wei
 * @version 1.0
 * @description: User
 * @date 2023/2/23
 */
@Getter
@Setter
public class User extends BaseEntity {

    private String businessUserId;

    private String userSecret;

    private Map<String, String> extFields;

    private List<User> friends;

    public Map<String, String> getExtFields() {
        if (this.entityStatus == Status.Transient && this.extFields == null) {
            synchronized (this) {
                if (this.extFields == null) {
                    UserFieldMapper userFieldMapper = IOC.INSTANCE.getInstance(UserFieldMapper.class);
                    List<UserFieldPO> byBusinessUserId = userFieldMapper.findByBusinessUserId(this.businessUserId);
                    Map<String, String> extFields = byBusinessUserId.stream().collect(Collectors.toMap(UserFieldPO::getFieldName, UserFieldPO::getFieldValue));
                    this.extFields = extFields;
                }
            }
        }
        return extFields;
    }

    public List<User> getFriends() {
        if (this.entityStatus == Status.Transient && this.friends == null) {
            synchronized (this) {
                if (this.friends == null) {
                    UserRelationMapper userRelationMapper = IOC.INSTANCE.getInstance(UserRelationMapper.class);
                    List<UserRelationPO> relations = userRelationMapper.findRelations(this.businessUserId, UserRelationType.FRIEND.ordinal());
                    if (relations.isEmpty()) {
                        this.friends = Collections.EMPTY_LIST;
                    }
                    UserMapper userMapper = IOC.INSTANCE.getInstance(UserMapper.class);
                    List<String> targetBusinessUserIds = relations.stream().map(UserRelationPO::getTargetBusinessUserId).collect(Collectors.toList());
                    List<UserPO> byBusinessIds = userMapper.findByBusinessIds(targetBusinessUserIds);
                    List<User> friends = byBusinessIds.stream().map(User::from).collect(Collectors.toList());
                    this.friends = friends;
                }
            }
        }
        return friends;
    }

    public static User from(UserPO po) {
        if (po == null) {
            return null;
        }
        User user = new User();
        user.setBusinessUserId(po.getBusinessUserId());
        user.setUserSecret(po.getUserSecret());
        return user;
    }

    public static List<User> from(List<UserPO> pos) {
        return pos.stream().map(User::from).collect(Collectors.toList());
    }

}
