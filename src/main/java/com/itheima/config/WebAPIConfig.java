package com.itheima.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "webapi")     // 从配置文件中读取api的id和密钥
public class WebAPIConfig {
    private String appId;
    private String appSecret;
    private String geminiApiKey;
}