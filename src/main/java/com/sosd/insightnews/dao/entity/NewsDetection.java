package com.sosd.insightnews.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 新闻检测表
 * @TableName NewsDetection
 */
@TableName(value ="NewsDetection")
@Data
public class NewsDetection {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private Long id;

    /**
     * 网址
     */
    private String url;

    /**
     * 上传用户
     */
    private String userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 新闻来源
     */
    private String source;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 新闻类型
     */
    private String newsType;

    /**
     * 可信度
     */
    private Integer credibility;

    /**
     * 新闻发布时间
     */
    private Date publishDate;

    /**
     * 创建时间
     */
    private Date creationTime;

    /**
     * 返回文本
     */
    private String responseText;

    /**
     * 证据链文本集合
     */
    private String evidenceChain; // 假设这是一个 JSON 数组的字符串表示
}