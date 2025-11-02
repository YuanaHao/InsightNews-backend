package com.sosd.insightnews.dto;

import lombok.Data;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentsDTO implements Serializable {

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 父级ID(父级评论ID)
     */
    private Long parentId;

    /**
     * 业务状态：1 评论 2 回复
     */
    private Integer status;
}