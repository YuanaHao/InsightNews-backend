package com.sosd.insightnews.context;


import com.sosd.insightnews.domain.UserDo;

public class UserContext {

    private static final ThreadLocal<UserDo> userThreadLocal = new ThreadLocal<>();

    public static void clear() {
        userThreadLocal.remove();
    }

    public static UserDo getCurrentUser() {
        return userThreadLocal.get();
    }

    public static void setCurrentUser(UserDo user) {
        userThreadLocal.set(user);
    }

}
