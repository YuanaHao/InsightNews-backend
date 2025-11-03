package com.sosd.insightnews.domain;

import lombok.Data;


import java.util.Date;
import java.util.List;

@Data
public class DetectionAnalysis {

    /**
     * 主键
     */
    private Long id;

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
     * 可信度
     */
    private Float credibility;

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
    private List<Evidence> evidenceChain; // 假设这是一个 JSON 数组的字符串表示
}
