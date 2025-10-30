package com.sosd.insightnews.config;

import cn.hutool.core.io.resource.ClassPathResource;
import com.github.houbb.sensitive.word.api.IWordDeny;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class WordDeny implements IWordDeny {
    @Override
    public List<String> deny() {
        List<String> denyWords = new ArrayList<>();
        try {
            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            Path denyWordsPath = projectRoot.resolve("myDenyWords.txt");
            if (!Files.exists(denyWordsPath)) {
                log.error("敏感词文件不存在：{}", denyWordsPath);
                return denyWords;
            }
            denyWords = Files.readAllLines(denyWordsPath, StandardCharsets.UTF_8);
        } catch (
                IOException ioException) {
            log.error("读取敏感词文件错误：{}", ioException);

        }
        return denyWords;
    }
}
