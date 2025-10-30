package com.sosd.insightnews.controller;

import com.sosd.insightnews.context.UserContext;
import com.sosd.insightnews.dto.NewsDTO;
import com.sosd.insightnews.dto.NewsDetectionReq;
import com.sosd.insightnews.service.AIService;
import com.sosd.insightnews.service.NewsDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detection")
public class DetectionController {

    @Autowired
    private AIService aiService;

    @Autowired
    private NewsDetectionService newsDetectionService;
    //每次检测完新闻都需要用redis存储该用户的历史检测记录，key为userId，value为NewsDetection实体的id，过期时间为30天，按照时间降序排序

    // 上传新闻链接/文本 —— 先创建一个NewsDetection实体，填入id和userId，如果是文本，就填content，如果是url就填url
    // 可信度需要请求aiService的detectNews方法，这里是通过http请求到另外一台服务器的服务，证据链需要从返回的json格式中的analysis字段
    // 结合原文本，搜索analysis中的text_segments字段，找到对应的文本，进行高亮的处理标签，填入NewsDetection实体的证据链字段
    @PostMapping("/upload/text")
    public NewsDTO uploadText(@RequestBody NewsDetectionReq newsDetectionReq) {
        String userId = UserContext.getCurrentUser().getId();
        return newsDetectionService.uploadTextNews(newsDetectionReq.getContent(), userId);
    }

    @PostMapping("/upload/file")
    public NewsDTO uploadFile(@RequestParam("filePath") String filePath) {
        String userId = UserContext.getCurrentUser().getId();
        return newsDetectionService.uploadFileNews(filePath, userId);
    }

    @PostMapping("/favorite/{newsId}")
    public void favoriteNews(@PathVariable Long newsId) {
        String userId = UserContext.getCurrentUser().getId();
        newsDetectionService.favoriteNews(newsId, userId);
    }

    @PostMapping("/dislike/{newsId}")
    public void dislikeNews(@PathVariable Long newsId) {
        String userId = UserContext.getCurrentUser().getId();
        newsDetectionService.dislikeNews(newsId, userId);
    }

    @GetMapping("/report/{newsId}")
    public NewsDTO viewReport(@PathVariable Long newsId) {
        return newsDetectionService.viewAnalysisReport(newsId);
    }

    @GetMapping("/report/download/{newsId}")
    public String downloadReport(@PathVariable Long newsId, @RequestParam("format") String format) {
        return newsDetectionService.downloadAnalysisReport(newsId, format);
    }

    @GetMapping("/history")
    public List<NewsDTO> getHistory() {
        String userId = UserContext.getCurrentUser().getId();
        return newsDetectionService.getHistoryDetections(userId);
    }

    // 上传新闻文件（视频/图片）—— 先创建一个NewsDetection实体，填入id和userId，如果是文件，content就填filePath
    // 可信度需要请求aiService的detectNews方法，这里是通过http请求到另外一台服务器的服务，但是返回的是html。
    // 最后使用html——pdf变成pdf文件上传OSS服务器，然后返回pdf的url填入NewsDetection实体的证据链字段

    // 收藏新闻检测 —— redis存储

    // 查看分析报告 —— 去数据库查找，返回NewsDetection实体中的证据链字段

    // 下载分析报告 非文本类型使用html——pdf，下载pdf文件，其他类型使用md——pdf，下载pdf文件

    // 下载分析报告图片 非文本类型使用html——png，下载png文件，其他类型使用md——png，下载png文件

    // 查看历史检测 —— 去redis中查找检测的id记录，返回List<NewsDTO>

    // 点踩新闻检测 —— redis存储操作信息

}
