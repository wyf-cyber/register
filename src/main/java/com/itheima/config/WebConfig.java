package com.itheima.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 配置CORS，允许来自 5173 端口的请求
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")  // 允许访问的路径
                .allowedOrigins("http://localhost:5173")  // 允许的前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 允许的请求方式
                .allowedHeaders("*")  // 允许的请求头
                .allowCredentials(true)  // 允许发送凭证（cookies等）
                .maxAge(3600);  // 缓存预检请求的时间，单位为秒
    }
}