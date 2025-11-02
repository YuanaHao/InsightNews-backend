package com.sosd.insightnews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sosd.insightnews.constant.RedisConstants;
import com.sosd.insightnews.context.UserContext;
import com.sosd.insightnews.dao.entity.NewsDetection;
import com.sosd.insightnews.dao.entity.NewsTopicAssociation;
import com.sosd.insightnews.dao.entity.ScienceTopic;
import com.sosd.insightnews.dao.mapper.ScienceTopicMapper;
import com.sosd.insightnews.dto.*;
import com.sosd.insightnews.exception.http.BadRequestException;
import com.sosd.insightnews.service.*;
import com.sosd.insightnews.util.NewsPriorityUtil;
import com.sosd.insightnews.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sosd.insightnews.constant.RedisConstants.MAX_SEARCH_HISTORY;

/**
 * 科普话题服务实现类
 */
@Service
@Slf4j
public class ScienceTopicServiceImpl extends ServiceImpl<ScienceTopicMapper, ScienceTopic> implements ScienceTopicService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private NewsTopicAssociationService newsTopicAssociationService;

    @Autowired
    private NewsDetectionService newsDetectionService;

    @Autowired
    private ScienceTopicMapper scienceTopicMapper;

    @Override
    @Transactional
    public TopicDetailDTO getTopicById(Long topicId) {
        ScienceTopic topic = getById(topicId);
        if (topic == null) {
            return null;
        }
        // 增加浏览次数
        Double score = redisTemplate.opsForZSet().incrementScore(RedisConstants.TOPIC_VIEW_COUNT_KEY, String.valueOf(topicId), 1);
        log.info("话题{}浏览次数增加成功，当前浏览次数：{}", topicId, score);
        TopicDetailDTO detailDTO = new TopicDetailDTO();
        BeanUtils.copyProperties(topic, detailDTO);
        // 查询关联新闻列表
        LambdaQueryWrapper<NewsTopicAssociation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsTopicAssociation::getTopicId, topicId);
        List<NewsTopicAssociation> associations = newsTopicAssociationService.list(queryWrapper);
        List<Long> newsIds = associations.stream()
                .map(NewsTopicAssociation::getNewsId)
                .collect(Collectors.toList());
        if (!newsIds.isEmpty()) {
            // 批量查询新闻详情
            List<NewsDetection> newsList = newsDetectionService.listByIds(newsIds);
            List<NewsDTO> newsDetailList = newsList.stream().map(this::NewsDetectionToNewsDTO).collect(Collectors.toList());
            // 使用优先级排序
            List<NewsDTO> sortedNewsList = NewsPriorityUtil.prioritizeNews(newsDetailList);
            detailDTO.setNews(sortedNewsList);
        }

        // 从Redis获取关注人数
        String countKey = RedisConstants.TOPIC_FAVORITE_COUNT_KEY + topicId;
        Integer attentionNum = (Integer) redisTemplate.opsForValue().get(countKey);
        if (attentionNum != null && attentionNum > 0) {
            detailDTO.setAttentionNum(attentionNum);
        }

        // 检查当前用户是否收藏该话题
        String userId = UserContext.getCurrentUser().getId();
        String favoriteKey = RedisConstants.USER_FAVORITE_TOPICS_KEY + userId;
        Double favoriteScore = redisTemplate.opsForZSet().score(favoriteKey, topicId.toString());
        detailDTO.setFavorited(favoriteScore != null);
        return detailDTO;
    }

    @Override
    public List<TopicDTO> getHotTopics(String category) {
        // 从数据库中获取指定类别的话题
        LambdaQueryWrapper<ScienceTopic> queryWrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(ScienceTopic::getCategory, category);
        }
        List<ScienceTopic> allTopics = list(queryWrapper);
        log.info("从数据库中获取话题成功，话题数量：{}", allTopics.size());
        
        if (allTopics.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 从Redis获取所有话题的浏览次数记录，按照浏览次数降序排序
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(RedisConstants.TOPIC_VIEW_COUNT_KEY, 0, -1);
        log.info("获取所有话题的浏览次数记录成功，记录数：{}", typedTuples.size());
        
        // 如果Redis中有数据，将热门话题排在前面
        if (!typedTuples.isEmpty()) {
            // 获取Redis中的话题ID及其分数
            Map<Long, Double> topicScores = typedTuples.stream()
                    .collect(Collectors.toMap(
                            tuple -> Long.parseLong(Objects.requireNonNull(tuple.getValue()).toString()),
                            ZSetOperations.TypedTuple::getScore,
                            (a, b) -> b
                    ));
            
            // 将话题列表分为两部分：Redis中的热门话题和其他话题
            List<ScienceTopic> hotTopics = new ArrayList<>();
            List<ScienceTopic> normalTopics = new ArrayList<>();
            
            for (ScienceTopic topic : allTopics) {
                if (topicScores.containsKey(topic.getId())) {
                    hotTopics.add(topic);
                } else {
                    normalTopics.add(topic);
                }
            }
            
            // 对热门话题按照Redis中的score降序排序
            hotTopics.sort((a, b) -> Double.compare(
                    topicScores.getOrDefault(b.getId(), 0.0),
                    topicScores.getOrDefault(a.getId(), 0.0)
            ));
            
            // 合并热门话题和普通话题
            hotTopics.addAll(normalTopics);
            return hotTopics.stream().map(this::convertToDTO).collect(Collectors.toList());
        }
        
        // 如果Redis中没有数据，直接返回所有话题
        return allTopics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TopicDTO> searchTopics(String keyword, String userId) {
        if (keyword == null || keyword.isEmpty()) {
            log.error("搜索关键词为空");
            throw new BadRequestException("搜索关键词为空");
        }
        // 保存搜索历史
        saveSearchHistory(userId, keyword);
        // 搜索话题
        LambdaQueryWrapper<ScienceTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ScienceTopic::getTitle, keyword)
                .or()
                .like(ScienceTopic::getDescription, keyword);
        List<ScienceTopic> topics = list(queryWrapper);
        if (topics.isEmpty()) {
            log.error("搜索结果为空");
            return new ArrayList<>();
        }
        // 更新热门搜索
        for (ScienceTopic topic : topics) {
            redisTemplate.opsForZSet().incrementScore(RedisConstants.HOT_SEARCH_TOPIC_KEY, topic.getTitle(), 1);
        }
        return topics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> getHotSearchTopics() {
        Set<Object> topics = redisTemplate.opsForZSet()
                .reverseRange(RedisConstants.HOT_SEARCH_TOPIC_KEY, 0, RedisConstants.MAX_HOT_TOPICS - 1);
        if (Objects.requireNonNull(topics).isEmpty()) {
            log.error("热门搜索话题为空");
            return new ArrayList<>();
        }
        return topics.stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public List<String> getSearchHistory(String userId) {
        String key = RedisConstants.USER_SEARCH_HISTORY_KEY + userId;
        Set<Object> history = redisTemplate.opsForZSet().reverseRange(key, 0, MAX_SEARCH_HISTORY - 1);
        if (history == null || history.isEmpty()) {
            log.error("搜索历史为空");
            return new ArrayList<>();
        }
        return history.stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public void clearSearchHistory(String userId) {
        String key = RedisConstants.USER_SEARCH_HISTORY_KEY + userId;
        redisTemplate.delete(key);
    }

    @Override
    @Transactional
    public boolean toggleFavoriteTopic(Long topicId, String userId) {
        String favoriteKey = RedisConstants.USER_FAVORITE_TOPICS_KEY + userId;
        String countKey = RedisConstants.TOPIC_FAVORITE_COUNT_KEY + topicId;
        
        Double score = redisTemplate.opsForZSet().score(favoriteKey, topicId.toString());
        if (score == null) {
            // 添加收藏记录，使用当前时间戳作为score
            redisTemplate.opsForZSet().add(favoriteKey, topicId.toString(), System.currentTimeMillis());
            redisTemplate.opsForValue().increment(countKey);
            return true;
        } else {
            // 取消收藏
            redisTemplate.opsForZSet().remove(favoriteKey, topicId.toString());
            redisTemplate.opsForValue().decrement(countKey);
            return false;
        }
    }

    @Override
    public List<TopicDTO> getFavoriteTopics(String userId) {
        // 从Redis获取用户收藏的话题ID列表，按收藏时间倒序排序
        String favoriteKey = RedisConstants.USER_FAVORITE_TOPICS_KEY + userId;
        Set<Object> members = redisTemplate.opsForZSet().reverseRange(favoriteKey, 0, -1);
        if (members == null || members.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> topicIds = members.stream()
                .map(o -> Long.parseLong(o.toString()))
                .toList();

        // 查询话题详情
        List<ScienceTopic> topics = scienceTopicMapper.selectBatchTopics(topicIds);
        return topics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private void saveSearchHistory(String userId, String keyword) {
        String key = RedisConstants.USER_SEARCH_HISTORY_KEY + userId;
        // 使用当前时间戳作为分数，实现按时间排序
        redisTemplate.opsForZSet().add(key, keyword, System.currentTimeMillis());
        // 保持历史记录不超过最大限制
        redisTemplate.opsForZSet().removeRange(key, 0, -(MAX_SEARCH_HISTORY + 1));
    }

    private TopicDTO convertToDTO(ScienceTopic topic) {
        TopicDTO dto = new TopicDTO();
        BeanUtils.copyProperties(topic, dto);
        // 从Redis获取关注人数
        String countKey = RedisConstants.TOPIC_FAVORITE_COUNT_KEY + topic.getId();
        Integer attentionNum = (Integer) redisTemplate.opsForValue().get(countKey);
        if (attentionNum != null && attentionNum > 0) {
            dto.setAttentionNum(attentionNum);
        }
        return dto;
    }

    private NewsDTO NewsDetectionToNewsDTO(NewsDetection newsDetection){
        NewsDTO newsDTO = new NewsDTO();
        BeanUtils.copyProperties(newsDetection, newsDTO);
        newsDTO.setCreationTime(TimeUtil.df.format(newsDetection.getCreationTime()));
        String userId = UserContext.getCurrentUser().getId();
        // 查询是否点踩和收藏
//        LambdaQueryWrapper<FavoriteDislike> favoriteDislikeQueryWrapper = new LambdaQueryWrapper<>();
//        favoriteDislikeQueryWrapper.eq(FavoriteDislike::getOperatorId, userId);
//        favoriteDislikeQueryWrapper.eq(FavoriteDislike::getTargetId, newsDetection.getId());
//        favoriteDislikeQueryWrapper.eq(FavoriteDislike::getTargetType, "news");
//        List<FavoriteDislike> favoriteDislikeList = favoriteDislikeService.list(favoriteDislikeQueryWrapper);
//        if (favoriteDislikeList.isEmpty()) {
//            newsDTO.setIsDislike(0);
//            newsDTO.setIsCollect(0);
//            return newsDTO;
//        }
//        for (FavoriteDislike favoriteDislike : favoriteDislikeList) {
//            switch (favoriteDislike.getOperationType()) {
//                case "like":
//                    newsDTO.setIsDislike(1);
//                    break;
//                case "dislike":
//                    newsDTO.setIsDislike(2);
//                    break;
//                case "favorite":
//                    newsDTO.setIsCollect(1);
//                    break;
//                default:
//                    break;
//            }
//        }
        return newsDTO;
    }
}