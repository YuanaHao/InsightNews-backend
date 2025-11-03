package com.sosd.insightnews.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDo {
    private String id;
    private String phone;
    private String name;
    private String avatar;
    private String gender;
    private String region;
    private String profile;
    private String openid;
    private String email;
    private Date updateTime;

    // optional:
    private List<RoleDo> roles;
    private List<PermissionDo> permissions;
}
