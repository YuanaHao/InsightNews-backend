package com.sosd.insightnews.util.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsCodeProperty {

        public String accessKeyID;
        public String accessKeySecret;
        public String signName;
        public String templateId;

    }
