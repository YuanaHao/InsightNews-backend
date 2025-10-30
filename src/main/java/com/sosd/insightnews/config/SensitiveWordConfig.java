package com.sosd.insightnews.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensitiveWordConfig {

    @Autowired
    private WordDeny myWordDeny;

    /**
     * 初始化引导类
     *
     * @return 初始化引导类
     * @since 1.0.0
     */
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
//                .wordAllow(WordAllows.chains(WordAllows.defaults(), myWordAllow)) // 设置多个敏感词，系统默认和自定义
//                .wordDeny(WordDenys.chains(WordDenys.defaults(), myWordDeny))     // 设置多个敏感词，系统默认和自定义
                .wordDeny(WordDenys.chains(myWordDeny))     // 自定义
                .ignoreCase(true)           // 忽略大小写
                .ignoreWidth(true)          // 忽略半角圆角
                .ignoreNumStyle(true)       // 忽略数字的写法
                .ignoreChineseStyle(true)   // 忽略中文的书写格式
                .ignoreEnglishStyle(true)   // 忽略英文的书写格式
                .ignoreRepeat(true)         // 忽略重复词
//                .enableNumCheck(true)       // 是否启用数字检测。默认连续 8 位数字认为是敏感词
//                .enableEmailCheck(true)     // 是有启用邮箱检测
//                .enableUrlCheck(true)       // 是否启用链接检测
                .init();
        return sensitiveWordBs;
    }
}


