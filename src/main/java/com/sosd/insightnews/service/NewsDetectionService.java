package com.sosd.insightnews.service;

import com.sosd.insightnews.dao.entity.NewsDetection;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sosd.insightnews.dto.NewsDTO;

import java.util.List;

/**
 * 新闻检测服务接口
 */
public interface NewsDetectionService extends IService<NewsDetection> {
    /**
     * 上传文本新闻进行检测
     * @param content 新闻内容
     * @param userId 用户ID
     * @return NewsDTO
     */
    NewsDTO uploadTextNews(String content, String userId);

    /**
     * 上传文件新闻进行检测
     * @param filePath 文件路径
     * @param userId 用户ID
     * @return NewsDTO
     */
    NewsDTO uploadFileNews(String filePath, String userId);

    /**
     * 收藏新闻检测
     * @param newsId 新闻ID
     * @param userId 用户ID
     */
    void favoriteNews(Long newsId, String userId);

    /**
     * 点踩新闻检测
     * @param newsId 新闻ID
     * @param userId 用户ID
     */
    void dislikeNews(Long newsId, String userId);

    /**
     * 查看分析报告
     * @param newsId 新闻ID
     * @return NewsDTO
     */
    NewsDTO viewAnalysisReport(Long newsId);

    /**
     * 下载分析报告
     * @param newsId 新闻ID
     * @param format 格式（pdf/png）
     * @return 报告文件路径
     */
    String downloadAnalysisReport(Long newsId, String format);

    /**
     * 查看历史检测记录
     * @param userId 用户ID
     * @return 历史检测记录列表
     */
    List<NewsDTO> getHistoryDetections(String userId);
}