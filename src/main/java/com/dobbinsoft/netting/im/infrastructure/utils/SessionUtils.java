package com.dobbinsoft.netting.im.infrastructure.utils;

import com.dobbinsoft.netting.im.domain.entity.User;

/**
 * @author w.wei
 * @version 1.0
 * @description: SessionUtils
 * @date 2023/2/23
 */
public class SessionUtils {

    private static final ThreadLocal<User> userSession = new ThreadLocal<>();

    public static boolean hasLogin() {
        return userSession.get() != null;
    }

    public static void setUser(User user) {
        userSession.set(user);
    }

    public static User getUser() {
        return userSession.get();
    }

    public static void clear() {
        userSession.remove();
    }

}
