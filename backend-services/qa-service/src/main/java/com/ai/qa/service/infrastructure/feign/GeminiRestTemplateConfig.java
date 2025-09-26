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
public class GeminiRestTemplateConfig {

    @Bean("geminiRestTemplate")
    public RestTemplate geminiRestTemplate() {
        // String proxyHost = "9.36.235.13";
        String proxyHost = "138.68.60.160";
        int proxyPort = 8080;

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxy);
        factory.setConnectTimeout(60_000); // 60 s
        factory.setReadTimeout(120_000); // 120 s

        log.info("Gemini 专用 RestTemplate 已创建，代理：{}:{}", proxyHost, proxyPort);
        return new RestTemplate(factory);
    }
}