package com.sosd.insightnews.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户表
 * @TableName User
 */
@TableName(value ="User")
@Data
public class User {
    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * Phone
     */
    private String phone;

    /**
     * 昵称
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private String gender;

    /**
     * 地区
     */
    private String region;

    /**
     * 简介
     */
    private String profile;

    /**
     * 微信OpenId
     */
    private String openId;

    /**
     * QQ邮箱
     */
    private String email;

    private Date createTime;
    /**
     * 最后修改时间
     */
    private Date updateTime;
}