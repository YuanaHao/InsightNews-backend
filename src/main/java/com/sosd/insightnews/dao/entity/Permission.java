package com.sosd.insightnews.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限表
 * @TableName Permission
 */
@TableName(value ="Permission")
@Data
public class Permission {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 权限操作
     */
    private String operation;

    /**
     * 操作对象
     */
    private String target;

}