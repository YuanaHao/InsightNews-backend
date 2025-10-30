package com.sosd.insightnews.auth.permission;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class PermissionTargetUtil {

    private static final Joiner STRING_JOINER = Joiner.on("+").skipNulls();
    private static final Splitter STRING_SPLITTER = Splitter.on("+").omitEmptyStrings().trimResults();

    // 修改用户信息权限
    public static String buildModifyUserTarget(String userId) {
        return userId;
    }

}
