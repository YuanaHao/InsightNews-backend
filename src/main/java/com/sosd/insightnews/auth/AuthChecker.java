package com.sosd.insightnews.auth;

import com.sosd.insightnews.auth.permission.PermissionOps;
import com.sosd.insightnews.auth.role.RoleType;
import com.sosd.insightnews.context.UserContext;
import org.springframework.stereotype.Component;

@Component("authChecker")
public class AuthChecker {

    public boolean isAdmin() {
        return UserContext.getCurrentUser().getRoles().stream().anyMatch(role ->
                role.getRoleid().equals(RoleType.ADMIN)
        );
    }

    public boolean isUser() {
        return UserContext.getCurrentUser().getRoles().stream().anyMatch(role ->
                role.getRoleid().equals(RoleType.USER)
        );
    }

    public boolean hasModifyUserPermission(String userId) {
        return UserContext.getCurrentUser().getPermissions().stream().anyMatch(permission ->
                permission.getOperation().equals(PermissionOps.MODIFY_USER)
                        && permission.getTarget().equals(userId)
        );
    }


}
