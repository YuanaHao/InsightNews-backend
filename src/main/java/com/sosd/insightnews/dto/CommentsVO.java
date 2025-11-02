package com.sosd.insightnews.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CommentsVO implements Serializable {

    /**
     * 记录id
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 关联话题
     */
    private Long topicId;

    /**
     * 父级ID(父级评论ID)
     */
    private Long parentId;

    /**
     * 业务状态：1 评论 2 回复
     */
    private Integer status;

    /**
     * 点赞数，图中评论有点赞数据，所以这里添加点赞数字段
     */
    private Integer likeCount;

    // 用户是否点赞
    private boolean isLike;

    /**
     * 创建人：这里，sql查询时，直接把用户名放在这个字段了
     */
    private String username;

    /**
     * 创建时间
     */
    private String createdAt;

    // 用户头像
    private String userImg;

    // 子评论列表
    private List<CommentsVO> children;
}


