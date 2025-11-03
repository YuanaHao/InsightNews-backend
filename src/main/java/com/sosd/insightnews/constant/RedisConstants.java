package com.sosd.insightnews.constant;

public class RedisConstants {
    public static final String LOGIN_USER_KEY = "login:userId:";
    public static final String VERIFY_CODE = "verify:phone:";
    public static final String UPLOAD_IMG_NEWS = "upload:img:newsId";
    public static final String UPLOAD_VIDEO_NEWS = "upload:video:newsId";
    public static final Long VERIFY_CODE_TTL= 3L;
    public static final Long LOGIN_USER_TTL = 36000L;

    // 科普话题相关常量
    public static final String TOPIC_VIEW_COUNT_KEY = "topic:view:count";
    public static final String USER_SEARCH_HISTORY_KEY = "user:search:history:";
    public static final String HOT_SEARCH_TOPIC_KEY = "hot:search:topic";
    public static final int MAX_SEARCH_HISTORY = 9;
    public static final int MAX_HOT_TOPICS = 10;

    // 点赞和收藏相关常量
    public static final String COMMENT_LIKE_KEY = "comment:like:";
    public static final String COMMENT_LIKE_COUNT_KEY = "comment:like:count:";
    public static final String USER_FAVORITE_TOPICS_KEY = "topic:favorite:userId:";
    public static final String TOPIC_FAVORITE_COUNT_KEY = "topic:favorite:count:";

    // 新闻检测相关常量
    public static final String NEWS_LIKE_KEY = "news:like:";
    public static final String NEWS_LIKE_COUNT_KEY = "news:like:count:";
    public static final String NEWS_DISLIKE_KEY = "news:dislike:";
    public static final String NEWS_FAVORITE_KEY = "news:favorite:";
    public static final String NEWS_FAVORITE_COUNT_KEY = "news:favorite:count:";
}
