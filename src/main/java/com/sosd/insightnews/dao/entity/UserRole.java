package com.sosd.insightnews.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色表
 * @TableName UserRole
 */
@TableName(value ="UserRole")
@Data
public class UserRole {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户Id
     */
    private String userid;

    /**
     * 角色Id
     */
    private String roleid;
}