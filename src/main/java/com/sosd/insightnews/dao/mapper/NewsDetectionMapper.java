package com.sosd.insightnews.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sosd.insightnews.dao.entity.NewsDetection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 新闻检测表 Mapper 接口
 */
@Mapper
public interface NewsDetectionMapper extends BaseMapper<NewsDetection> {
}