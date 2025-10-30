package com.sosd.insightnews.controller;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.sosd.insightnews.context.UserContext;
import com.sosd.insightnews.domain.R;
import com.sosd.insightnews.dto.*;
import com.sosd.insightnews.service.CommentsService;
import com.sosd.insightnews.service.ScienceTopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic")
@Slf4j
public class TopicController {

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private ScienceTopicService scienceTopicService;

    @Autowired
    private SensitiveWordBs sensitiveWordBs;

    /**
     * 查看话题详情
     * @param topicId 话题ID
     * @return 话题详情
     */
    @GetMapping("/{topicId}")
    public R<TopicDetailDTO> getTopicDetail(@PathVariable Long topicId) {
        log.info("查看话题详情, topicId:{}", topicId);
        TopicDetailDTO topic = scienceTopicService.getTopicById(topicId);
        return R.ok("成功查看话题详情",topic);
    }

    /**
     * 获取热点话题列表
     * @param category 分类
     * @return 热点话题列表
     */
    @GetMapping("")
    public R<List<TopicDTO>> getHotTopics(@RequestParam(required = false) String category) {
        log.info("获取热点话题列表");
        List<TopicDTO> topics = scienceTopicService.getHotTopics(category);
        return R.ok("成功获取热点话题列表",topics);
    }

    /**
     * 搜索话题
     * @param keyword 关键词
     * @return 话题列表
     */
    @GetMapping("/search")
    public R<List<TopicDTO>> searchTopics(@RequestParam String keyword) {
        String userId = UserContext.getCurrentUser().getId();
        log.info("搜索话题, keyword:{}, userId:{}", keyword, userId);
        List<TopicDTO> topics = scienceTopicService.searchTopics(keyword, userId);
        return R.ok("成功搜索话题",topics);
    }

    /**
     * 获取热门搜索话题
     * @return 热门话题标题列表
     */
    @GetMapping("/hot/search")
    public R<List<String>> getHotSearchTopics() {
        log.info("获取热门搜索话题");
        List<String> topics = scienceTopicService.getHotSearchTopics();
        return R.ok("成功获取热门搜索话题",topics);
    }

    /**
     * 获取用户搜索历史
     * @return 搜索历史列表
     */
    @GetMapping("/search/history")
    public R<List<String>> getSearchHistory() {
        String userId = UserContext.getCurrentUser().getId();
        log.info("获取用户搜索历史, userId:{}", userId);
        List<String> history = scienceTopicService.getSearchHistory(userId);
        return R.ok("成功获取用户搜索历史",history);
    }

    /**
     * 清除用户搜索历史
     * @return 操作结果
     */
    @DeleteMapping("/delete/search")
    public R<String> clearSearchHistory() {
        String userId = UserContext.getCurrentUser().getId();
        log.info("清除用户搜索历史, userId:{}", userId);
        scienceTopicService.clearSearchHistory(userId);
        return R.ok("清除搜索历史成功", null);
    }

    /**
     * 添加评论/回复
     *
     * @param commentsDTO 评论/回复信息
     * @return 操作结果
     */
    @PostMapping("/comment/add/{topicId}")
    public R<String> addComment(@RequestBody @Validated CommentsDTO commentsDTO, @PathVariable Long topicId) {
        String username = UserContext.getCurrentUser().getName();
        log.info("用户{}评论:{}", username, commentsDTO.getComment());
        boolean contains = sensitiveWordBs.contains(commentsDTO.getComment());
        if (contains) {
            return R.fail("请友善发言");
        }
        commentsService.postComment(commentsDTO, topicId);
        return R.ok("评论添加成功", null);
    }

    /**
     * 根据话题ID查询评论列表
     *
     * @param queryDTO 查询参数
     * @return 评论列表
     */
    @GetMapping("/comment/page/{topicId}")
    public R<PageBean<CommentsVO>> getCommentList(@Validated CommentsPageQueryDTO queryDTO, @PathVariable Long topicId) {
        log.info("查询评论列表, topicId:{}", topicId);
        PageBean<CommentsVO> commentList = commentsService.getCommentsByTopicId(queryDTO, topicId);
        return R.ok("查询评论列表成功",commentList);
    }

    /**
     * 删除评论/回复
     * @param commentId 评论/回复ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{commentId}")
    public R<String> deleteComment(@PathVariable Long commentId) {
        String userId = UserContext.getCurrentUser().getId();
        log.info("删除评论, commentId:{}, userId:{}", commentId, userId);
        commentsService.deleteComment(commentId, userId);
        return R.ok("删除评论成功");
    }

    /**
     * 切换评论点赞状态
     * @param commentId 评论/回复ID
     * @return 操作结果
     */
    @PostMapping("/comment/toggle-like/{commentId}")
    public R<String> toggleLikeComment(@PathVariable Long commentId) {
        String userId = UserContext.getCurrentUser().getId();
        log.info("切换评论点赞状态, commentId:{}, userId:{}", commentId, userId);
        boolean isLiked = commentsService.toggleLikeComment(commentId, userId);
        return R.ok(isLiked ? "点赞成功" : "取消点赞成功");
    }

    /**
     * 获取评论的回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    @GetMapping("/comment/replies/{commentId}")
    public R<List<CommentsVO>> getCommentReplies(@PathVariable Long commentId) {
        log.info("获取评论回复列表, commentId:{}", commentId);
        List<CommentsVO> replies = commentsService.getCommentReplies(commentId);
        return R.ok("获取评论回复列表成功", replies);
    }

    /**
     * 收藏/取消收藏话题
     * @param topicId 话题ID
     * @return 操作结果
     */
    @PostMapping("/favorite/{topicId}")
    public R<String> toggleFavoriteTopic(@PathVariable Long topicId) {
        String userId = UserContext.getCurrentUser().getId();
        log.info("收藏/取消收藏话题, topicId:{}, userId:{}", topicId, userId);
        boolean isFavorited = scienceTopicService.toggleFavoriteTopic(topicId, userId);
        return R.ok(isFavorited ? "收藏成功" : "取消收藏成功");
    }
}
