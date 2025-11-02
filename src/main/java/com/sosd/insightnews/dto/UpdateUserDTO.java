package com.sosd.insightnews.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {

    private String name;
    private String gender;
    private String avatar;
    private String region;
    private String profile;
    private String email;

}
