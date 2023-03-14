package com.dobbinsoft.netting.im.domain.repository;

import com.dobbinsoft.netting.base.utils.CollectionUtils;
import com.dobbinsoft.netting.im.domain.entity.BaseEntity;
import com.dobbinsoft.netting.im.domain.entity.User;
import com.dobbinsoft.netting.im.infrastructure.mapper.UserMapper;
import com.dobbinsoft.netting.im.infrastructure.po.UserFieldPO;
import com.dobbinsoft.netting.im.infrastructure.po.UserPO;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class UserRepository {

    @Inject
    private UserMapper userMapper;

    public User newInstance() {
        User user = new User();
        user.setEntityStatus(BaseEntity.Status.Transient);
        return user;
    }

    public User findByBusinessUserId(String businessUserId) {
        UserPO po = userMapper.findByBusinessId(businessUserId);
        return User.from(po);
    }

    /**
     *
     * @param users
     * @return success items
     */
    public List<String> saveUserBatch(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.EMPTY_LIST;
        }
        List<String> businessIds = users.stream().map(User::getBusinessUserId).collect(Collectors.toList());
        List<UserPO> byBusinessIds = userMapper.findByBusinessIds(businessIds);
        // main table
        List<UserPO> toUpdateList = new ArrayList<>();
        List<UserPO> toInsertList = new ArrayList<>();
        // sub table
        List<String> extFieldDeleteList = new ArrayList<>();
        List<String> relationDeleteList = new ArrayList<>();

        // result
        List<String> result = new ArrayList<>();
        for (User user : users) {
            if (user.getExtFields() != null) {
                extFieldDeleteList.add(user.getBusinessUserId());
            }
            if (user.getFriends() != null) {
                relationDeleteList.add(user.getBusinessUserId());
            }

            boolean exist = false;
            for (UserPO userPO : byBusinessIds) {
                if (userPO.getBusinessUserId().equals(user.getBusinessUserId())) {
                    exist = true;
                    if (!userPO.getUserSecret().equals(user.getUserSecret())) {
                        userPO.setUserSecret(user.getUserSecret());
                        toUpdateList.add(userPO);
                    }
                    result.add(userPO.getBusinessUserId());
                    break;
                }
            }
            if (!exist) {
                UserPO po = new UserPO();
                po.setBusinessUserId(user.getBusinessUserId());
                po.setUserSecret(user.getUserSecret());
                result.add(user.getBusinessUserId());
                toInsertList.add(po);
            }
        }
        if (!toUpdateList.isEmpty()) {
            userMapper.updateBatch(toUpdateList);
        }
        if (!toInsertList.isEmpty()) {
            userMapper.insertBatch(toInsertList);
        }
        return result;
    }

    public Integer deleteUserBatch(List<String> businessUserIds) {
        return userMapper.deleteBatch(businessUserIds);
    }
}
