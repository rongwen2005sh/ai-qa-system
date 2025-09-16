package com.ai.qa.service.domain.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * RAG（检索增强生成）领域模型
 * 负责管理检索到的上下文信息，为AI回答提供相关背景知识
 */
@Getter
@Setter
public class QARAG {

    /**
     * 检索到的上下文信息
     */
    private String context;

    /**
     * 相关的文档列表
     */
    private List<String> relevantDocuments;

    /**
     * 置信度分数，表示检索结果的相关性程度
     */
    private Double confidenceScore;

    /**
     * 数据来源，如：知识库、文档库等
     */
    private String source;

    /**
     * 私有构造函数，确保通过工厂方法创建对象
     */
    private QARAG() {
        // 防止直接实例化
    }

    /**
     * 从文档列表创建新的RAG上下文
     *
     * @param documents 相关文档列表
     * @param source 数据来源标识
     * @return 配置好的QARAG实例
     */
    public static QARAG createFromDocuments(List<String> documents, String source) {
        QARAG rag = new QARAG();
        rag.relevantDocuments = documents;
        rag.source = source;
        rag.context = String.join("\n", documents);
        rag.confidenceScore = calculateConfidence(documents);
        return rag;
    }

    /**
     * 创建简单的RAG上下文
     *
     * @param context 上下文内容
     * @return 配置好的QARAG实例
     */
    public static QARAG createSimple(String context) {
        QARAG rag = new QARAG();
        rag.context = context;
        rag.confidenceScore = 0.8; // 默认置信度
        rag.source = "manual";
        return rag;
    }

    /**
     * 计算置信度分数（简化版）
     * 基于文档数量和内容长度进行评估
     *
     * @param documents 文档列表
     * @return 置信度分数，范围0.0-1.0
     */
    private static Double calculateConfidence(List<String> documents) {
        if (documents == null || documents.isEmpty()) return 0.0;

        // 简单的置信度计算：基于文档数量和内容长度
        double baseScore = 0.5;
        double lengthBonus = Math.min(documents.stream()
                .mapToInt(String::length)
                .sum() / 1000.0, 0.3);
        double countBonus = Math.min(documents.size() * 0.1, 0.2);

        return Math.min(baseScore + lengthBonus + countBonus, 1.0); // 确保不超过1.0
    }

    /**
     * 获取上下文信息
     *
     * @return 上下文内容，如果为空则返回空字符串
     */
    public String getContext() {
        return context != null ? context : "";
    }

    /**
     * 添加上下文信息
     *
     * @param additionalContext 额外的上下文内容
     */
    public void addContext(String additionalContext) {
        if (context == null) {
            context = additionalContext;
        } else {
            context += "\n" + additionalContext;
        }
        // 添加新内容后重新计算置信度
        this.confidenceScore = calculateConfidence(List.of(context));
    }

    /**
     * 判断RAG上下文是否有效
     *
     * @return true如果上下文存在且非空，否则false
     */
    public boolean isValid() {
        return context != null && !context.trim().isEmpty();
    }

    /**
     * 获取置信度等级
     *
     * @return 置信度等级描述
     */
    public String getConfidenceLevel() {
        if (confidenceScore == null) return "unknown";
        if (confidenceScore >= 0.8) return "high";
        if (confidenceScore >= 0.5) return "medium";
        return "low";
    }
}