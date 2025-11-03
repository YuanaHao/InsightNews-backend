package com.sosd.insightnews.domain;

import lombok.Data;

import java.util.List;

@Data
public class RoleDo {
    private Integer id;
    private String roleid; //角色名称
    private String desc;

    private List<PermissionDo> permissions;
}
