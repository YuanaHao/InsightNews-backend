package com.sosd.insightnews.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 科普话题表
 * @TableName ScienceTopic
 */
@TableName(value ="ScienceTopic")
@Data
public class ScienceTopic {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * 发布时间
     */
    private Date publishTime;

    /**
     * 话题封面
     */
    private String topicCover;
}