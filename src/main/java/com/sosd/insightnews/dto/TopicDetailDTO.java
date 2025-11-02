package com.sosd.insightnews.dto;

import lombok.Data;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

@Data
public class TopicDetailDTO {

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

    /**
     * 关联新闻列表
     */
    private List<NewsDTO> news;
    
    private boolean isFavorited;
}
