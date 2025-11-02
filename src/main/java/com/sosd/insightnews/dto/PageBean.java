package com.sosd.insightnews.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页查询结果封装类
 */
@Data
public class PageBean<T> {
    private Long total; // 总记录数
    private List<T> items; // 当前页数据
}