package com.sosd.insightnews.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sosd.insightnews.dao.entity.NewsTopicAssociation;
import com.sosd.insightnews.dao.mapper.NewsTopicAssociationMapper;
import com.sosd.insightnews.service.NewsTopicAssociationService;
import org.springframework.stereotype.Service;

@Service
public class NewsTopicAssociationServiceImpl extends ServiceImpl<NewsTopicAssociationMapper, NewsTopicAssociation> implements NewsTopicAssociationService {
}
