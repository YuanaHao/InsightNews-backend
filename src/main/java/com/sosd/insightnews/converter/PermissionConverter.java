package com.sosd.insightnews.converter;


import com.sosd.insightnews.dao.entity.Permission;
import com.sosd.insightnews.domain.PermissionDo;

import java.util.List;
import java.util.stream.Collectors;

public class PermissionConverter {

    public static PermissionDo permission2PermissionDo(Permission permission) {
        PermissionDo permissionDo = new PermissionDo();
        permissionDo.setId(permission.getId());
        permissionDo.setOperation(permission.getOperation());
        permissionDo.setTarget(permission.getTarget());
        return permissionDo;
    }

    public static List<PermissionDo> permission2PermissionDo(List<Permission> permissionList) {
        return permissionList.stream().map(PermissionConverter::permission2PermissionDo).collect(Collectors.toList());
    }

    public static Permission permissionDo2Permission(PermissionDo permission) {
        Permission permissionDo = new Permission();
        permissionDo.setId(permission.getId());
        permissionDo.setOperation(permission.getOperation());
        permissionDo.setTarget(permission.getTarget());
        return permissionDo;
    }
}
