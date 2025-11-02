package com.sosd.insightnews.service;

import com.sosd.insightnews.domain.RoleDo;
import com.sosd.insightnews.service.impl.RoleServiceImpl;

import java.util.List;

public interface RoleService {

    void assignRoleToUsers(RoleDo role, String userId);

    void assignUserToUsers(String userId);

    RoleDo getRoleById(String roleId);

    RoleDo getRoleById(String roleId, RoleServiceImpl.QueryOption option);

    void updateUserRoleByRoleId(String beforeUserId, String afterUserId, String roleId);

    void deleteUserRoleByRoleId(String roleId);

    List<String> getRoleIdsByTargetId(String targetId);
}
