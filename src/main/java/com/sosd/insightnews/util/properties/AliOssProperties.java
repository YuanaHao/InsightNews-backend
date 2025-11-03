package com.sosd.insightnews.util.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alioss")
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    // 定义默认的单个文件最大限制 5MB 5MB = 5 * 1024 * 1024 byte
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

}