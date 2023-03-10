package com.dobbinsoft.netting.im.domain.entity;

import com.dobbinsoft.netting.im.infrastructure.po.UserPO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author w.wei
 * @version 1.0
 * @description: User
 * @date 2023/2/23
 */
@Getter
@Setter
public class User {

    private String businessUserId;

    private String userSecret;

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
