package com.sosd.insightnews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sosd.insightnews.constant.RedisConstants;
import com.sosd.insightnews.context.UserContext;
import com.sosd.insightnews.dao.entity.Comments;
import com.sosd.insightnews.dao.mapper.CommentsMapper;
import com.sosd.insightnews.domain.UserDo;
import com.sosd.insightnews.dto.CommentsDTO;
import com.sosd.insightnews.dto.CommentsPageQueryDTO;
import com.sosd.insightnews.dto.CommentsVO;
import com.sosd.insightnews.dto.PageBean;
import com.sosd.insightnews.exception.http.BadRequestException;
import com.sosd.insightnews.service.CommentsService;
import com.sosd.insightnews.service.UserService;
import com.sosd.insightnews.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论留言服务实现类
 */
@Service
@Slf4j
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public void postComment(CommentsDTO commentsDTO, Long topicId) {
        Comments comment = new Comments();
        BeanUtils.copyProperties(commentsDTO, comment);
        comment.setTopicId(topicId);
        
        // 设置用户ID
        String userId = UserContext.getCurrentUser().getId();
        comment.setUserId(userId);
        
        // 设置评论状态（1：评论 2：回复）
        if (comment.getParentId() == 0) {
            comment.setStatus(1);
        } else {
            comment.setStatus(2);
        }
        comment.setIsDeleted(0);
        save(comment);
    }

    @Override
    public PageBean<CommentsVO> getCommentsByTopicId(CommentsPageQueryDTO queryDTO, Long topicId) {
        // 创建PageBean对象
        PageBean<CommentsVO> pageBean = new PageBean<>();
        
        // 创建MyBatis-Plus分页对象
        Page<Comments> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        
        // 查询所有顶级评论
        LambdaQueryWrapper<Comments> rootQuery = new LambdaQueryWrapper<>();
        rootQuery.eq(Comments::getTopicId, topicId)
                .eq(Comments::getStatus, 1)
                .eq(Comments::getIsDeleted, 0)
                .orderByDesc(Comments::getCreatedAt);
        
        Page<Comments> commentPage = page(page, rootQuery);
        List<Comments> rootComments = commentPage.getRecords();
        List<CommentsVO> result = new ArrayList<>();

        for (Comments rootComment : rootComments) {
            CommentsVO commentVO = new CommentsVO();
            BeanUtils.copyProperties(rootComment, commentVO);
            
            // 从Redis获取点赞数
            String countKey = RedisConstants.COMMENT_LIKE_COUNT_KEY + rootComment.getId();
            String likeKey = RedisConstants.COMMENT_LIKE_KEY + rootComment.getId();
            String count = stringRedisTemplate.opsForValue().get(countKey);
            commentVO.setLikeCount(count != null ? Integer.parseInt(count) : 0);
            // 判断当前用户是否点赞
            String userId = UserContext.getCurrentUser().getId();
            Boolean isLiked = stringRedisTemplate.opsForSet().isMember(likeKey, userId);
            commentVO.setLike(Boolean.TRUE.equals(isLiked));
            
            // 设置用户信息
            UserDo user = userService.getUserById(rootComment.getUserId());
            if (user != null) {
                commentVO.setUsername(user.getName());
                commentVO.setUserImg(user.getAvatar());
            }
            
            // 格式化时间
            if (rootComment.getCreatedAt() != null) {
                commentVO.setCreatedAt(TimeUtil.df.format(rootComment.getCreatedAt()));
            }
            
            // 获取最新的一条子评论
            LambdaQueryWrapper<Comments> childQuery = new LambdaQueryWrapper<>();
            childQuery
                    .eq(Comments::getParentId, rootComment.getId())
                    .eq(Comments::getIsDeleted, 0)
                    .orderByDesc(Comments::getCreatedAt)
                    .last("LIMIT 1");
            
            List<Comments> childComments = list(childQuery);
            List<CommentsVO> children = new ArrayList<>();
            
            for (Comments childComment : childComments) {
                CommentsVO childVO = new CommentsVO();
                BeanUtils.copyProperties(childComment, childVO);
                
                // 从Redis获取子评论点赞数
                String childCountKey = RedisConstants.COMMENT_LIKE_COUNT_KEY + childComment.getId();
                String childLikeKey = RedisConstants.COMMENT_LIKE_KEY + childComment.getId();
                String childCount = stringRedisTemplate.opsForValue().get(childCountKey);
                childVO.setLikeCount(childCount != null ? Integer.parseInt(childCount) : 0);
                // 判断当前用户是否点赞
                String childUserId = UserContext.getCurrentUser().getId();
                Boolean childIsLiked = stringRedisTemplate.opsForSet().isMember(childLikeKey, childUserId);
                childVO.setLike(Boolean.TRUE.equals(childIsLiked));

                // 设置子评论用户信息
                UserDo childUser = userService.getUserById(childComment.getUserId());
                if (childUser != null) {
                    childVO.setUsername(childUser.getName());
                    childVO.setUserImg(childUser.getAvatar());
                }
                
                // 格式化子评论时间
                if (childComment.getCreatedAt() != null) {
                    childVO.setCreatedAt(TimeUtil.df.format(childComment.getCreatedAt()));
                }

                childVO.setChildren(new ArrayList<>());
                children.add(childVO);
            }
            
            commentVO.setChildren(children);
            result.add(commentVO);
        }
        
        // 设置分页信息
        pageBean.setTotal(commentPage.getTotal());
        pageBean.setItems(result);
        
        return pageBean;
    }
    
    @Override
    public List<CommentsVO> getCommentReplies(Long parentId) {
        LambdaQueryWrapper<Comments> childQuery = new LambdaQueryWrapper<>();
        childQuery
                .eq(Comments::getParentId, parentId)
                .eq(Comments::getIsDeleted, 0)
                .orderByDesc(Comments::getCreatedAt);
        List<Comments> childComments = list(childQuery);
        List<CommentsVO> result = new ArrayList<>();
        for (int i = 0; i < childComments.size(); i++) {
            Comments childComment = childComments.get(i);
            CommentsVO childVO = new CommentsVO();
            BeanUtils.copyProperties(childComment, childVO);
            // 从Redis获取点赞数
            String countKey = RedisConstants.COMMENT_LIKE_COUNT_KEY + childComment.getId();
            String likeKey = RedisConstants.COMMENT_LIKE_KEY + childComment.getId();
            String count = stringRedisTemplate.opsForValue().get(countKey);
            childVO.setLikeCount(count != null ? Integer.parseInt(count) : 0);
            // 判断当前用户是否点赞
            String userId = UserContext.getCurrentUser().getId();
            Boolean isLiked = stringRedisTemplate.opsForSet().isMember(likeKey, userId);
            childVO.setLike(Boolean.TRUE.equals(isLiked));
            // 设置用户信息
            UserDo user = userService.getUserById(childComment.getUserId());
            if (user != null) {
                childVO.setUsername(user.getName());
                childVO.setUserImg(user.getAvatar());
            }
            // 格式化时间
            if (childComment.getCreatedAt() != null) {
                childVO.setCreatedAt(TimeUtil.df.format(childComment.getCreatedAt()));
            }
            // 递归查询子评论
            List<CommentsVO> children = getCommentReplies(childComment.getId());
            childVO.setChildren(children);
            result.add(childVO);
            }
        return result;
    }

    @Override
    @Transactional
    public boolean toggleLikeComment(Long commentId, String userId) {
        String likeKey = RedisConstants.COMMENT_LIKE_KEY + commentId;
        String countKey = RedisConstants.COMMENT_LIKE_COUNT_KEY + commentId;
        
        Boolean member = stringRedisTemplate.opsForSet().isMember(likeKey, userId);
        if (Boolean.FALSE.equals(member)) {
            // 添加点赞记录
            stringRedisTemplate.opsForSet().add(likeKey, userId);
            stringRedisTemplate.opsForValue().increment(countKey);
            return true;
        } else {
            // 取消点赞
            stringRedisTemplate.opsForSet().remove(likeKey, userId);
            stringRedisTemplate.opsForValue().decrement(countKey);
            return false;
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String userId) {
        // 获取评论信息
        Comments comment = getById(commentId);
        if (comment == null) {
            throw new BadRequestException("评论不存在");
        }

        // 验证用户权限
        if (!comment.getUserId().equals(userId)) {
            throw new BadRequestException("无权删除该评论");
        }

        // 如果是父评论，删除所有子评论
        if (comment.getStatus() == 1) {
            LambdaQueryWrapper<Comments> childQuery = new LambdaQueryWrapper<>();
            childQuery.eq(Comments::getParentId, commentId)
                    .eq(Comments::getIsDeleted, 0);
            List<Comments> childComments = list(childQuery);
            // 批量软删除子评论
            childComments.forEach(comments -> {
                comments.setIsDeleted(1);
                String likeKey = RedisConstants.COMMENT_LIKE_KEY + comments.getId();
                String countKey = RedisConstants.COMMENT_LIKE_COUNT_KEY + comments.getId();
                // 删除用户的点赞记录
                stringRedisTemplate.opsForSet().remove(likeKey, userId);
                // 减少点赞计数
                String count = stringRedisTemplate.opsForValue().get(countKey);
                if (count != null && Integer.parseInt(count) > 0) {
                    stringRedisTemplate.opsForValue().decrement(countKey);
                }
            });
            updateBatchById(childComments);
        }
        // 软删除当前评论
        comment.setIsDeleted(1);
        String likeKey = RedisConstants.COMMENT_LIKE_KEY + commentId;
        String countKey = RedisConstants.COMMENT_LIKE_COUNT_KEY + commentId;
        // 删除用户的点赞记录
        stringRedisTemplate.opsForSet().remove(likeKey, userId);
        // 减少点赞计数
        String count = stringRedisTemplate.opsForValue().get(countKey);
        if (count != null && Integer.parseInt(count) > 0) {
            stringRedisTemplate.opsForValue().decrement(countKey);
        }
        updateById(comment);
    }
}