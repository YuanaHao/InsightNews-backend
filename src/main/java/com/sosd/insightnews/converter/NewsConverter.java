package com.sosd.insightnews.converter;

import com.sosd.insightnews.constant.RedisConstants;
import com.sosd.insightnews.context.UserContext;
import com.sosd.insightnews.dao.entity.NewsDetection;
import com.sosd.insightnews.dto.NewsDTO;
import com.sosd.insightnews.util.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class NewsConverter {

    @Autowired
    private static StringRedisTemplate stringRedisTemplate;

    public static NewsDTO NewsDetectionToNewsDTO(NewsDetection newsDetection) {
        NewsDTO newsDTO = new NewsDTO();
        BeanUtils.copyProperties(newsDetection, newsDTO);
        newsDTO.setCreationTime(TimeUtil.df.format(newsDetection.getCreationTime()));
        String userId = UserContext.getCurrentUser().getId();
        Long newsId = newsDetection.getId();

        // 获取用户点赞状态
        Boolean isLiked = stringRedisTemplate.opsForSet().isMember(RedisConstants.NEWS_LIKE_KEY + newsId, userId);
        // 获取用户点踩状态
        Boolean isDisliked = stringRedisTemplate.opsForSet().isMember(RedisConstants.NEWS_DISLIKE_KEY + newsId, userId);
        // 获取用户收藏状态
        Boolean isFavorite = stringRedisTemplate.opsForSet().isMember(RedisConstants.NEWS_FAVORITE_KEY + newsId, userId);

        // 设置点赞和点踩状态
        if (Boolean.TRUE.equals(isLiked)) {
            newsDTO.setIsDislike(1);
        } else if (Boolean.TRUE.equals(isDisliked)) {
            newsDTO.setIsDislike(2);
        } else {
            newsDTO.setIsDislike(0);
        }

        // 设置收藏状态
        newsDTO.setCollect(isFavorite != null && isFavorite);

        // 获取点赞数和收藏数
        String likeCount = stringRedisTemplate.opsForValue().get(RedisConstants.NEWS_LIKE_COUNT_KEY + newsId);
        String favoriteCount = stringRedisTemplate.opsForValue().get(RedisConstants.NEWS_FAVORITE_COUNT_KEY + newsId);
        newsDTO.setLikeCount(likeCount != null ? Integer.parseInt(likeCount) : 0);
        newsDTO.setFavoriteCount(favoriteCount != null ? Integer.parseInt(favoriteCount) : 0);

        return newsDTO;
    }
}
