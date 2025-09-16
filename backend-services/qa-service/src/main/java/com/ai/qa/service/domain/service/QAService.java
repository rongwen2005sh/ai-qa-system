package com.ai.qa.service.domain.service;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.model.QARAG;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.feign.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * QA领域服务
 * 处理问答相关的核心业务逻辑，协调领域对象和外部依赖
 */
@Service
@Transactional
public class QAService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private QAHistoryRepo qaHistoryRepo;

    /**
     * 处理用户问题并返回答案
     * 完整的问答处理流程：获取用户信息 → 生成RAG上下文 → 生成答案 → 保存历史
     *
     * @param userId 用户ID
     * @param question 用户问题
     * @param sessionId 会话ID
     * @return AI生成的答案
     */
    public String processQuestion(Long userId, String question, String sessionId) {
        // 1. 获取用户信息
        String userInfo = getUserInfo(userId);

        // 2. 生成RAG上下文（这里简化处理，实际应该调用RAG服务）
        QARAG rag = generateRAGContext(question, userInfo);

        // 3. 生成答案（这里简化处理，实际应该调用AI模型）
        String answer = generateAnswer(question, rag);

        // 4. 保存问答历史
        saveQAHistory(userId.toString(), question, answer, sessionId, rag);

        return answer;
    }

    /**
     * 获取用户信息
     * 通过Feign客户端调用用户服务
     *
     * @param userId 用户ID
     * @return 用户信息字符串
     */
    private String getUserInfo(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (Exception e) {
            // 记录日志并返回降级信息
            return "用户信息获取失败: " + e.getMessage();
        }
    }

    /**
     * 生成RAG上下文
     * 模拟RAG检索过程，实际应调用向量数据库
     *
     * @param question 用户问题
     * @param userInfo 用户信息
     * @return RAG上下文对象
     */
    private QARAG generateRAGContext(String question, String userInfo) {
        // 这里简化实现，实际应该调用向量数据库检索相关文档
        List<String> relevantDocs = List.of(
                "用户信息: " + userInfo,
                "相关问题: " + question,
                "系统知识: 这是一个AI问答系统"
        );
        return QARAG.createFromDocuments(relevantDocs, "internal");
    }

    /**
     * 生成答案
     * 模拟AI模型生成回答的过程
     *
     * @param question 用户问题
     * @param rag RAG上下文
     * @return 生成的答案
     */
    private String generateAnswer(String question, QARAG rag) {
        // 这里简化实现，实际应该调用AI模型生成答案
        return "根据您的查询 '" + question + "'，我找到以下信息:\n" +
                rag.getContext() + "\n\n请问还需要什么帮助吗？";
    }

    /**
     * 保存问答历史
     *
     * @param userId 用户ID
     * @param question 用户问题
     * @param answer AI回答
     * @param sessionId 会话ID
     * @param rag RAG上下文
     */
    private void saveQAHistory(String userId, String question, String answer,
                               String sessionId, QARAG rag) {
        QAHistory history = QAHistory.createNew(userId, question, answer, sessionId, rag);
        qaHistoryRepo.save(history);
    }

    /**
     * 获取用户的问答历史
     *
     * @param userId 用户ID
     * @return 用户的问答历史列表
     */
    public List<QAHistory> getUserHistory(String userId) {
        return qaHistoryRepo.findHistoryByUserId(userId);
    }

    /**
     * 获取会话的问答历史
     *
     * @param sessionId 会话ID
     * @return 会话的问答历史列表
     */
    public List<QAHistory> getSessionHistory(String sessionId) {
        return qaHistoryRepo.findHistoryBySession(sessionId);
    }

    /**
     * 根据ID获取问答记录
     *
     * @param id 记录ID
     * @return QA历史记录
     * @throws RuntimeException 如果记录不存在
     */
    public QAHistory getHistoryById(String id) {
        return qaHistoryRepo.findHistoryById(id)
                .orElseThrow(() -> new RuntimeException("问答记录不存在: " + id));
    }

    /**
     * 删除问答记录
     *
     * @param id 要删除的记录ID
     */
    public void deleteHistory(String id) {
        qaHistoryRepo.deleteById(id);
    }

    /**
     * 获取用户问答统计
     *
     * @param userId 用户ID
     * @return 用户的问答记录数量
     */
    public long getUserHistoryCount(String userId) {
        return qaHistoryRepo.countByUserId(userId);
    }

    /**
     * 批量删除会话历史
     *
     * @param sessionId 会话ID
     */
    public void clearSessionHistory(String sessionId) {
        List<QAHistory> sessionHistory = getSessionHistory(sessionId);
        sessionHistory.forEach(history ->
                deleteHistory(history.getId().toString()));
    }
}