package com.sosd.insightnews.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sosd.insightnews.dao.entity.ScienceTopic;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 科普话题表 Mapper 接口
 */
@Mapper
public interface ScienceTopicMapper extends BaseMapper<ScienceTopic> {

    List<ScienceTopic> selectBatchTopics(List<Long> topicIds);
}