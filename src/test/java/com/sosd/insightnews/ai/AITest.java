package com.sosd.insightnews.ai;

import com.sosd.insightnews.InsightNewsApplication;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {InsightNewsApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AITest {

    protected RestTemplate restTemplate = (new TestRestTemplate()).getRestTemplate();

    @Test
    void testhtmlAI() {
//        String apiKey = System.getenv("4de82e2a-93a9-4e35-bf0f-d9445be5d52e");
//        ArkService service = ArkService.builder().apiKey(apiKey).build();
//        System.out.println("\n----- standard request -----");
//        final List<ChatMessage> messages = new ArrayList<>();
//        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
//        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
//        messages.add(systemMessage);
//        messages.add(userMessage);
//
//        BotChatCompletionRequest chatCompletionRequest = BotChatCompletionRequest.builder()
//                // 将<YOUR_BOT_ID>替换为您的应用ID
//                .model("bot-20250302173419-hlppn")
//                .messages(messages)
//                .build();
//
//        BotChatCompletionResult chatCompletionResult =  service.createBotChatCompletion(chatCompletionRequest);
//        chatCompletionResult.getChoices().forEach(
//                choice -> System.out.println(choice.getMessage().getContent())
//        );
//        // the references example
//        chatCompletionResult.getReferences().forEach(
//                ref -> System.out.println(ref.getUrl())
//        );

    }

    @Test
    void testTextAI() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName("gpt-4o-mini").build();
        String answer = model.generate("为以下文本生成一个标题：" + "自1月23日起，武汉市进入封锁状态，770名在武汉三所高中学习的新疆学生被拘留在学校，禁止与外界接触，并被限制从事宗教活动。");
        System.out.println(answer);
    }

}
