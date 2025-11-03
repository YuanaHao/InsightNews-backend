package com.sosd.insightnews.domain;

import lombok.Data;

import java.util.List;

@Data
public class Evidence {

    private String rawText;

    private Float score; // 文本片段可信度得分

    private List<String> evidence; // 文本片段证据分析
}
