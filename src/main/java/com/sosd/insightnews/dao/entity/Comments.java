package com.sosd.insightnews.dao.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 评论留言表
 * @TableName Comments
 */
@Data
@TableName(value ="Comments")
public class Comments implements Serializable {


    /**
     * 记录id
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 关联话题
     */
    private Long topicId;

    /**
     * 父级ID(父级评论ID)
     */
    private Long parentId;

    /**
     * 业务状态：1 评论 2 回复
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 是否删除(0未删除 1已删除)
     */
    private Integer isDeleted;

}
