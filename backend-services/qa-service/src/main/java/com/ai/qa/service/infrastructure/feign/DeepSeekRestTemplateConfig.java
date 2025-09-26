package com.ai.qa.service.infrastructure.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
@Configuration
public class DeepSeekRestTemplateConfig {

    @Bean("deepseekRestTemplate")
    public RestTemplate deepseekRestTemplate() {
        // 可以复用你原来的代理配置，或者根据是否需要代理进行调整
        String proxyHost = "9.36.235.13"; // 如果需要代理
        int proxyPort = 8080;

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxy);
        factory.setConnectTimeout(60_000);
        factory.setReadTimeout(120_000);

        log.info("DeepSeek 专用 RestTemplate 已创建，代理：{}:{}", proxyHost, proxyPort);
        return new RestTemplate(factory);
    }
}