package com.dobbinsoft.netting.im.domain.entity;

import com.dobbinsoft.netting.im.infrastructure.po.UserPO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author w.wei
 * @version 1.0
 * @description: User
 * @date 2023/2/23
 */
@Getter
@Setter
public class User {

    private Long id;

    public static User from(UserPO po) {
        return new User();
    }

    public static List<User> from(List<UserPO> pos) {
        return new ArrayList<>();
    }

}
