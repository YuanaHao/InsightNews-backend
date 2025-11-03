package com.sosd.insightnews.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.sosd.insightnews.dto.NewsDTO;


/**
 * 新闻优先级计算工具类
 * 用于计算新闻的推送优先级，考虑新闻的可信度、热度、时效性和多样性等因素
 */
public class NewsPriorityUtil {

    // 权重配置
    private static final double CREDIBILITY_WEIGHT = 0.50;
    private static final double POPULARITY_WEIGHT = 0.30;
    private static final double TIMELINESS_WEIGHT = 0.20;

    // 时间衰减系数（每小时衰减1%）
    private static final double TIME_DECAY_FACTOR = 0.01;


    /**
     * 对新闻列表进行优先级排序
     * @param newsList 新闻列表
     * @return 排序后的新闻列表
     */
    public static List<NewsDTO> prioritizeNews(List<NewsDTO> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            return newsList;
        }

        // 预处理：计算最大值
        final int maxLikes = newsList.stream().mapToInt(NewsDTO::getLikeCount).max().orElse(1);
        final int maxFavorite = newsList.stream().mapToInt(NewsDTO::getFavoriteCount).max().orElse(1);

        return newsList.stream()
                .sorted((a, b) -> Double.compare(
                        calculatePriority(b, maxLikes, maxFavorite),
                        calculatePriority(a, maxLikes, maxFavorite)))
                .collect(Collectors.toList());
    }

    /**
     * 计算单个新闻的优先级分数
     */
    private static double calculatePriority(NewsDTO news, 
                                          int maxLikes,
                                          int maxFavorite) {
        // 可信度得分
        double credibilityScore = Math.abs(news.getCredibility() - 0.5) * 2;

        // 热度得分（对数缩放）
        double likesScore = safeLogScale(news.getLikeCount(), maxLikes);
        double favoriteScore = safeLogScale(news.getFavoriteCount(), maxFavorite);
        double popularityScore = (likesScore + favoriteScore) / 2;

        // 时效性得分
        double timelinessScore = calculateTimeliness(news.getPublishDate());

        // 综合计算
        return CREDIBILITY_WEIGHT * credibilityScore
                + POPULARITY_WEIGHT * popularityScore
                + TIMELINESS_WEIGHT * timelinessScore;
    }

    /**
     * 对数缩放计算
     */
    private static double safeLogScale(int value, int maxValue) {
        if (maxValue == 0) return 0;
        return Math.log1p(value) / Math.log1p(maxValue);
    }

    /**
     * 计算时效性得分
     */
    private static double calculateTimeliness(String publishTime) {
        try {
            Date date = TimeUtil.df.parse(publishTime);
            if (date == null) {
                return 0.0; // 如果解析失败，返回最低时效性得分
            }
            LocalDateTime publishDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            long hours = Duration.between(publishDateTime, LocalDateTime.now()).toHours();
            return Math.exp(-TIME_DECAY_FACTOR * hours);
        } catch (Exception e) {
            return 0.0; // 发生异常时返回最低时效性得分
        }
    }
}