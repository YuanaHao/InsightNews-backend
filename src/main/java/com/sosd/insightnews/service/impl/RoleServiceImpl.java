package com.sosd.insightnews.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sosd.insightnews.auth.role.RoleType;
import com.sosd.insightnews.converter.PermissionConverter;
import com.sosd.insightnews.converter.RoleConverter;
import com.sosd.insightnews.dao.entity.Permission;
import com.sosd.insightnews.dao.entity.Role;
import com.sosd.insightnews.dao.entity.RolePermission;
import com.sosd.insightnews.dao.entity.UserRole;
import com.sosd.insightnews.dao.mapper.PermissionMapper;
import com.sosd.insightnews.dao.mapper.RoleMapper;
import com.sosd.insightnews.dao.mapper.RolePermissionMapper;
import com.sosd.insightnews.dao.mapper.UserRoleMapper;
import com.sosd.insightnews.domain.PermissionDo;
import com.sosd.insightnews.domain.RoleDo;
import com.sosd.insightnews.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * Save role and permissions
     * then assign role to user
     * @param roleDo
     * @param userId
     */
    @Override
    @Transactional
    public void assignRoleToUsers(RoleDo roleDo, String userId) {
        Role role = RoleConverter.roleDo2Role(roleDo);
        roleMapper.insert(role);

        for (PermissionDo permission : roleDo.getPermissions()) {
            Permission p = PermissionConverter.permissionDo2Permission(permission);
            permissionMapper.insert(p);
            RolePermission rp = new RolePermission();
            rp.setRoleid(role.getRoleid());
            rp.setPermissionid(p.getId());
            rolePermissionMapper.insert(rp);
        }

        String roleId = role.getRoleid();
        UserRole userRole = new UserRole();
        userRole.setUserid(userId);
        userRole.setRoleid(roleId);
        userRoleMapper.insert(userRole);
    }

    @Override
    public void assignUserToUsers(String userId) {
        UserRole userRole = new UserRole();
        userRole.setRoleid(RoleType.USER);
        userRole.setUserid(userId);
        userRoleMapper.insert(userRole);
    }

    @Override
    public RoleDo getRoleById(String roleId) {
        return getRoleById(roleId, new QueryOption());
    }

    @Override
    public RoleDo getRoleById(String roleId, QueryOption option) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleid, roleId);
        Role role = roleMapper.selectOne(queryWrapper);
        if (role == null) {
            return null;
        }
        RoleDo domain = RoleConverter.role2RoleDo(role);

        // wrapping domain by option
        check(option);
        if (option.withPermissions) {
            List<Permission> permissionsByRoleId = getPermissionsByRoleId(roleId);
            if (permissionsByRoleId == null) {
                return domain;
            }
            List<PermissionDo> permissionDos = PermissionConverter.permission2PermissionDo(permissionsByRoleId);
            domain.setPermissions(permissionDos);
        }
        return domain;
    }

    @Override
    public void updateUserRoleByRoleId(String beforeUserId, String afterUserId, String roleId) {
        // UserRole 表更新 userid
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserid, beforeUserId);
        wrapper.eq(UserRole::getRoleid, roleId);
        UserRole userRole = userRoleMapper.selectOne(wrapper);
        userRole.setUserid(afterUserId);
        userRoleMapper.updateById(userRole);
    }

    @Override
    public void deleteUserRoleByRoleId(String roleId) {
        // 删除 UserRole 表的 roleId
        // 解绑 user 和 role 的关系，不需要删除role 和 permission
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getRoleid, roleId);
        userRoleMapper.delete(userRoleWrapper);
    }

    @Override
    public List<String> getRoleIdsByTargetId(String targetId) {
        // 正则匹配Role 表中的 name
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Role::getRoleid, targetId);
        List<Role> roles = roleMapper.selectList(queryWrapper);
        List<String> roleIds = new ArrayList<>();
        for (Role role : roles) {
            roleIds.add(role.getRoleid());
        }
        return roleIds;
    }

    private List<Permission> getPermissionsByRoleId(String roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleid, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return null;
        }
        List<Integer> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionid).toList();
        return permissionMapper.selectBatchIds(permissionIds);
    }

    public static class QueryOption {
        boolean withPermissions = false;

        public QueryOption withPermissions() {
            this.withPermissions = true;
            return this;
        }
    }

    private void check(QueryOption option) {
        if (option == null) {
            throw new IllegalArgumentException("QueryOption is null");
        }
    }

}




