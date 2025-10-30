package com.sosd.insightnews.auth.role;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class RoleUtil {

    private static final Joiner STRING_JOINER = Joiner.on("+").skipNulls();
    private static final Splitter STRING_SPLITTER = Splitter.on("+").omitEmptyStrings().trimResults();

    public static String buildUserSelfRoleName(String userId) {
        return STRING_JOINER.join(RoleType.UserSelf, userId);
    }

}
