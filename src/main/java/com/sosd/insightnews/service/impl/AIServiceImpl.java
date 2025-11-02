package com.sosd.insightnews.service.impl;

import com.sosd.insightnews.service.AIService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class AIServiceImpl implements AIService {

    @Override
    public String generateTitle(String content) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName("gpt-4o-mini").build();
        return model.generate("为以下文本生成一个标题：" + content);
    }

}
