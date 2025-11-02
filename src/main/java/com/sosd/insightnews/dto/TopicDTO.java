package com.sosd.insightnews.dto;

import lombok.Data;


@Data
public class TopicDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 总结
     */
    private String description;

    /**
     * 分类
     */
    private String category;

    /**
     * 话题封面
     */
    private String topicCover;

    /**
     * 关注人数
     */
    private Integer attentionNum;
}
