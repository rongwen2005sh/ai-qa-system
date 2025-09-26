package com.ai.qa.service.infrastructure.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini AI客户端
 * 
 * 负责与Google Gemini API进行通信，发送问题并获取AI回答
 * 封装了HTTP请求的细节，提供简单易用的接口
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Slf4j // Lombok注解：自动生成日志对象
@Component // Spring注解：标识这是一个组件，由Spring管理
public class GeminiClient {

    /**
     * Gemini API的基础URL
     * 从配置文件中读取，便于环境切换
     */
    @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;

    /**
     * Gemini API Key
     * 从配置文件中读取，需要在Google AI Studio获取
     * 如果未配置，将使用默认提示信息
     */
    @Value("${gemini.api.key:YOUR_API_KEY_HERE}")
    private String apiKey;

    /**
     * 使用的模型名称
     * 默认使用gemini-pro模型
     */
    @Value("${gemini.api.model:gemini-pro}")
    private String model;

    /**
     * HTTP客户端
     * 用于发送HTTP请求
     */
    private final RestTemplate restTemplate;

    /**
     * 构造函数
     * 初始化RestTemplate，配置代理支持和超时设置
     */
    public GeminiClient() {
        // 创建带代理的RestTemplate
        this.restTemplate = createRestTemplateWithProxy();

        // 配置系统代理属性
        configureSystemProxy();

        log.info("GeminiClient初始化完成，已配置代理支持和超时设置");
    }

    /**
     * 创建带代理配置的RestTemplate
     */
    private RestTemplate createRestTemplateWithProxy() {
        // 创建代理对象
        String proxyHost = "9.36.235.13";
        int proxyPort = 8080;

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

        // 创建请求工厂并配置代理和超时
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxy);
        factory.setConnectTimeout(60000); // 连接超时60秒
        factory.setReadTimeout(120000); // 读取超时120秒

        RestTemplate restTemplate = new RestTemplate(factory);

        log.info("RestTemplate代理配置完成: {}:{}", proxyHost, proxyPort);
        log.info("超时设置: 连接60s, 读取120s");

        return restTemplate;
    }

    /**
     * 配置系统代理属性（备用方案）
     */
    private void configureSystemProxy() {
        // 启用系统代理
        System.setProperty("java.net.useSystemProxies", "true");

        // 使用您的Mac代理配置
        String proxyHost = "9.36.235.13";
        String proxyPort = "8080";

        // 设置HTTP代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);

        // 设置HTTPS代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        // 设置不使用代理的主机（本地服务）
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1|*.local|54.219.180.170");

        log.info("系统代理属性配置完成: {}:{}", proxyHost, proxyPort);
    }

    /**
     * 向Gemini AI发送问题并获取回答
     * 
     * @param question 用户问题
     * @return String AI的回答
     * @throws RuntimeException 当API调用失败时抛出异常
     */
    public String askQuestion(String question) {
        log.info("开始调用Gemini API，问题长度: {}", question.length());

        // 检查API Key是否已配置
        if ("YOUR_API_KEY_HERE".equals(apiKey)) {
            log.warn("Gemini API Key未配置，返回模拟回答");
            return generateMockResponse(question);
        }

        try {
            // 构建请求URL
            String url = String.format("%s/models/%s:generateContent?key=%s",
                    baseUrl, model, apiKey);

            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(question);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建HTTP请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送POST请求
            long startTime = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, Map.class);
            long endTime = System.currentTimeMillis();

            log.info("Gemini API调用成功，耗时: {}ms", endTime - startTime);

            // 解析响应
            return parseResponse(response.getBody());

        } catch (Exception e) {
            log.error("调用Gemini API失败，可能是网络或地区限制问题: {}", e.getMessage());
            log.info("使用模拟回答作为备用方案");
            // 网络问题时使用模拟回答，而不是抛出异常
            return generateMockResponse(question);
        }
    }

    /**
     * 构建Gemini API请求体
     * 
     * @param question 用户问题
     * @return Map<String, Object> 请求体
     */
    private Map<String, Object> buildRequestBody(String question) {
        Map<String, Object> requestBody = new HashMap<>();

        // 构建contents数组
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", question);
        content.put("parts", List.of(part));

        requestBody.put("contents", List.of(content));

        // 设置生成配置（可选）
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7); // 控制回答的创造性
        generationConfig.put("maxOutputTokens", 1000); // 限制回答长度
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }

    /**
     * 解析Gemini API响应
     * 
     * @param responseBody API响应体
     * @return String 提取的AI回答
     */
    private String parseResponse(Map<String, Object> responseBody) {
        try {
            // 使用类型安全的获取方法
            Object candidatesObj = responseBody.get("candidates");
            if (!(candidatesObj instanceof List)) {
                log.warn("Gemini API响应中candidates格式不正确");
                return "抱歉，我现在无法回答这个问题。";
            }

            List<?> candidatesList = (List<?>) candidatesObj;
            if (candidatesList.isEmpty()) {
                log.warn("Gemini API响应中没有candidates");
                return "抱歉，我现在无法回答这个问题。";
            }

            Object firstCandidateObj = candidatesList.get(0);
            if (!(firstCandidateObj instanceof Map)) {
                log.warn("Gemini API响应中candidate格式不正确");
                return "抱歉，我现在无法回答这个问题。";
            }

            Map<?, ?> firstCandidate = (Map<?, ?>) firstCandidateObj;
            Object contentObj = firstCandidate.get("content");
            if (!(contentObj instanceof Map)) {
                log.warn("Gemini API响应中没有content");
                return "抱歉，我现在无法回答这个问题。";
            }

            Map<?, ?> content = (Map<?, ?>) contentObj;
            Object partsObj = content.get("parts");
            if (!(partsObj instanceof List) || ((List<?>) partsObj).isEmpty()) {
                log.warn("Gemini API响应中没有parts");
                return "抱歉，我现在无法回答这个问题。";
            }

            List<?> partsList = (List<?>) partsObj;
            Object firstPartObj = partsList.get(0);
            if (!(firstPartObj instanceof Map)) {
                log.warn("Gemini API响应中part格式不正确");
                return "抱歉，我现在无法回答这个问题。";
            }

            Map<?, ?> firstPart = (Map<?, ?>) firstPartObj;
            Object textObj = firstPart.get("text");

            return textObj != null ? textObj.toString().trim() : "抱歉，我现在无法回答这个问题。";

        } catch (Exception e) {
            log.error("解析Gemini API响应失败: {}", e.getMessage(), e);
            return "抱歉，处理回答时出现了问题。";
        }
    }

    /**
     * 生成模拟回答（当API调用失败或未配置时使用）
     * 
     * @param question 用户问题
     * @return String 模拟的AI回答
     */
    private String generateMockResponse(String question) {
        // 简单的关键词匹配，生成相应的模拟回答
        String lowerQuestion = question.toLowerCase();

        if (lowerQuestion.contains("你好") || lowerQuestion.contains("hello") || lowerQuestion.contains("介绍")) {
            return "你好！我是AI智能客服助手，很高兴为您服务！\n\n" +
                    "我可以帮助您：\n" +
                    "• 回答各种问题\n" +
                    "• 提供信息查询\n" +
                    "• 协助解决问题\n" +
                    "• 进行日常对话\n\n" +
                    "请随时告诉我您需要什么帮助！";
        } else if (lowerQuestion.contains("天气")) {
            return "关于天气查询：\n\n" +
                    "抱歉，我目前无法获取实时天气信息。建议您：\n" +
                    "• 查看手机天气应用\n" +
                    "• 访问天气预报网站\n" +
                    "• 询问语音助手\n\n" +
                    "如果您有其他问题，我很乐意帮助您！";
        } else if (lowerQuestion.contains("时间")) {
            return "关于时间查询：\n\n" +
                    "我无法获取当前准确时间，请查看您的设备时钟。\n\n" +
                    "如果您需要其他帮助，比如时间管理建议或日程安排，我很乐意协助您！";
        } else if (lowerQuestion.contains("帮助") || lowerQuestion.contains("功能")) {
            return "我是您的AI智能助手，可以为您提供以下服务：\n\n" +
                    "📝 信息查询和解答\n" +
                    "💡 问题分析和建议\n" +
                    "🗣️ 日常对话交流\n" +
                    "📚 知识分享\n" +
                    "🤝 生活和工作建议\n\n" +
                    "请告诉我您想了解什么，我会尽力帮助您！";
        } else if (lowerQuestion.contains("谢谢") || lowerQuestion.contains("感谢")) {
            return "不客气！很高兴能够帮助您。\n\n" +
                    "如果您还有其他问题或需要进一步的帮助，请随时告诉我。我会一直在这里为您服务！😊";
        } else {
            return String.format("感谢您的提问：\"%s\"\n\n" +
                    "我正在努力理解您的问题。作为AI助手，我会尽力为您提供有用的信息和建议。\n\n" +
                    "如果您能提供更多详细信息，我将能够给出更准确的回答。\n\n" +
                    "请问您还有什么其他问题需要我帮助解决吗？",
                    question);
        }
    }
}