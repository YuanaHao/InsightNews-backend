package com.sosd.insightnews.service;

import com.sosd.insightnews.dao.entity.Comments;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sosd.insightnews.dto.CommentsDTO;
import com.sosd.insightnews.dto.CommentsPageQueryDTO;
import com.sosd.insightnews.dto.CommentsVO;
import com.sosd.insightnews.dto.PageBean;

import java.util.List;

/**
 * 评论留言服务接口
 */
public interface CommentsService extends IService<Comments> {

    void postComment(CommentsDTO commentsDTO,Long topicId);

    PageBean<CommentsVO> getCommentsByTopicId(CommentsPageQueryDTO queryDTO, Long topicId);

    boolean toggleLikeComment(Long commentId, String userId);

    void deleteComment(Long commentId, String userId);

    List<CommentsVO> getCommentReplies(Long commentId);
}