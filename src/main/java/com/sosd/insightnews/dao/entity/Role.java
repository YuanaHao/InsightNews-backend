package com.sosd.insightnews.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色表
 * @TableName Role
 */
@TableName(value ="Role")
@Data
public class Role {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色名称
     */
    private String roleid;

    /**
     * 备注
     */
    private String description;
}