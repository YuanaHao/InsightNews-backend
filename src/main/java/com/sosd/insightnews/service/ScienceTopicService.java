package com.sosd.insightnews.service;

import com.sosd.insightnews.dao.entity.ScienceTopic;
import com.sosd.insightnews.dto.TopicDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sosd.insightnews.dto.TopicDetailDTO;

import java.util.List;

/**
 * 科普话题服务接口
 */
public interface ScienceTopicService extends IService<ScienceTopic> {
    
    /**
     * 根据话题ID查询话题详情
     * @param topicId 话题ID
     * @return 话题详情
     */
    TopicDetailDTO getTopicById(Long topicId);

    /**
     * 获取热点话题列表
     * @return 热点话题列表
     */
    List<TopicDTO> getHotTopics(String category);

    /**
     * 搜索话题
     * @param keyword 关键词
     * @param userId 用户ID
     * @return 话题列表
     */
    List<TopicDTO> searchTopics(String keyword, String userId);

    /**
     * 获取热门搜索话题
     * @return 热门话题标题列表
     */
    List<String> getHotSearchTopics();

    /**
     * 获取用户搜索历史
     * @param userId 用户ID
     * @return 搜索历史列表
     */
    List<String> getSearchHistory(String userId);

    /**
     * 清除用户搜索历史
     * @param userId 用户ID
     */
    void clearSearchHistory(String userId);

    boolean toggleFavoriteTopic(Long topicId, String userId);

    List<TopicDTO> getFavoriteTopics(String userId);
}