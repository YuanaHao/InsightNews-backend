package com.sosd.insightnews.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CommentsPageQueryDTO implements Serializable {

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页显示记录数
     */
    private Integer pageSize;
}