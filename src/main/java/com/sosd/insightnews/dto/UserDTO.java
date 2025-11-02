package com.sosd.insightnews.dto;

import lombok.Data;

@Data
public class UserDTO {

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
     * qq邮箱
     */
    private String email;

}
