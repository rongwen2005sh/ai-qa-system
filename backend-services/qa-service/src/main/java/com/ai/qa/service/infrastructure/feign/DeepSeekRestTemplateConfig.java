package com.ai.qa.service.infrastructure.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class DeepSeekRestTemplateConfig {

    @Bean("deepseekRestTemplate")
    public RestTemplate deepseekRestTemplate() {
        // 创建不配置代理的 RestTemplate
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000); // 30秒连接超时
        factory.setReadTimeout(60_000); // 60秒读取超时

        log.info("DeepSeek 专用 RestTemplate 已创建（无代理，连接超时: 30s，读取超时: 60s）");
        return new RestTemplate(factory);
    }
}