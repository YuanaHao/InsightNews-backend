package com.sosd.insightnews.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("NewsTopicAssociation")
public class NewsTopicAssociation {

    @TableId
    private Long id;

    private Long topicId;

    private Long newsId;
}
