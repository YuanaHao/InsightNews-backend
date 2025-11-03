package com.sosd.insightnews.domain;

import lombok.Data;

@Data
public class PermissionDo {
    private Integer id;
    private String operation;
    private String target;
}
