package com.sosd.insightnews.dto;

import lombok.Data;

@Data
public class NewsDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 可信度
     */
    private Integer credibility;

    /**
     * 新闻发布时间
     */
    private String publishDate;

    /**
     * 创建时间
     */
    private String creationTime;

    /**
     * 是否点踩
     */
    private Integer isDislike; // 0 未点踩 1 点 2 踩

    /**
     * 是否收藏
     */
    private boolean isCollect; // 0 未收藏 1 收藏

    /**
     * 点赞人数
     */
    private Integer likeCount;

    /**
     * 收藏人数
     */
    private Integer favoriteCount;


}
