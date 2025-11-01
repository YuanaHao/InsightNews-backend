package com.sosd.insightnews.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 角色权限表
 * @TableName RolePermission
 */
@TableName(value ="RolePermission")
@Data
public class RolePermission {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色Id
     */
    private String roleid;

    /**
     * 权限Id
     */
    private Integer permissionid;
}