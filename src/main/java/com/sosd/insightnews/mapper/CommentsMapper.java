package com.sosd.insightnews.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sosd.insightnews.dao.entity.Comments;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论留言表 Mapper 接口
 */
@Mapper
public interface CommentsMapper extends BaseMapper<Comments> {
}