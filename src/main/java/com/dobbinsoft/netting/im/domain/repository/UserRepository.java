package com.dobbinsoft.netting.im.domain.repository;

import com.dobbinsoft.netting.im.domain.entity.User;
import com.dobbinsoft.netting.im.infrastructure.mapper.UserMapper;
import com.dobbinsoft.netting.im.infrastructure.po.UserPO;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UserRepository {

    @Inject
    private UserMapper userMapper;

    public User findById(Long userId) {
        UserPO po = userMapper.findById(userId);
        return User.from(po);
    }

    public User findByBusinessUserId(String businessUserId) {
        UserPO po = userMapper.findByBusinessId(businessUserId);
        return User.from(po);
    }

}
