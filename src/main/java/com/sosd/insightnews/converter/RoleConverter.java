package com.sosd.insightnews.converter;


import com.sosd.insightnews.dao.entity.Role;
import com.sosd.insightnews.domain.RoleDo;

import java.util.List;
import java.util.stream.Collectors;

public class RoleConverter {

    public static RoleDo role2RoleDo(Role role) {
        RoleDo roleDo = new RoleDo();
        roleDo.setId(role.getId());
        roleDo.setRoleid(role.getRoleid());
        roleDo.setDesc(role.getDescription());
        return roleDo;
    }

    public static List<RoleDo> role2RoleDo(List<Role> roles) {
        return roles.stream().map(RoleConverter::role2RoleDo).collect(Collectors.toList());
    }

    public static Role roleDo2Role(RoleDo role) {
        Role roleDo = new Role();
        roleDo.setId(role.getId());
        roleDo.setRoleid(role.getRoleid());
        roleDo.setDescription(role.getDesc());
        return roleDo;
    }
}
