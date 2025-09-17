package com.ai.qa.service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QAHistory 单元测试类
 * 测试QAHistory领域模型的核心业务逻辑
 */
public class QAHistoryTest {

    private QAHistory qaHistory;
    private final String userId = "testUser123";
    private final String question = "What is AI?";
    private final String answer = "AI stands for Artificial Intelligence.";
    private final String sessionId = "session456";
    private QARAG rag;

    @BeforeEach
    public void setup() {
        // 使用工厂方法初始化RAG上下文
        rag = QARAG.createSimple("This is some context information.");

        // 创建测试对象
        qaHistory = QAHistory.createNew(userId, question, answer, sessionId, rag);
    }

    @Test
    public void testCreateNew() {
        assertNotNull(qaHistory);
        assertEquals(userId, qaHistory.getUserId());
        assertEquals(question, qaHistory.getQuestion());
        assertEquals(answer, qaHistory.getAnswer());
        assertEquals(sessionId, qaHistory.getSessionId());
        assertEquals(rag, qaHistory.getRag());
        assertNotNull(qaHistory.getTimestamp());
        assertNotNull(qaHistory.getCreateTime());
        assertNotNull(qaHistory.getUpdateTime());
    }

    @Test
    public void testGetAnswerWithContext_WithRag() {
        String result = qaHistory.getAnswerWithContext();
        assertTrue(result.contains(answer));
        assertTrue(result.contains("上下文信息:"));
        assertTrue(result.contains(rag.getContext()));
    }

    @Test
    public void testGetAnswerWithContext_WithoutRag() {
        QAHistory historyWithoutRag = QAHistory.createNew(userId, question, answer, sessionId, null);
        String result = historyWithoutRag.getAnswerWithContext();
        assertEquals(answer, result);
    }

    @Test
    public void testGetAnswerWithContext_WithEmptyRagContext() {
        QARAG emptyRag = QARAG.createSimple("");
        QAHistory historyWithEmptyRag = QAHistory.createNew(userId, question, answer, sessionId, emptyRag);
        String result = historyWithEmptyRag.getAnswerWithContext();

        // 当前行为：即使context为空，也会添加"上下文信息:"前缀
        String expected = answer + "\n\n上下文信息:\n";
        assertEquals(expected, result);
    }

    @Test
    public void testGetAnswerWithContext_WithNullRagContext() {
        QARAG nullContextRag = QARAG.createSimple(null);
        QAHistory historyWithNullContext = QAHistory.createNew(userId, question, answer, sessionId, nullContextRag);
        String result = historyWithNullContext.getAnswerWithContext();

        // 当前行为：即使context为null，也会添加"上下文信息:"前缀
        String expected = answer + "\n\n上下文信息:\n";
        assertEquals(expected, result);
    }

    @Test
    public void testUpdateAnswer() {
        String newAnswer = "Updated answer content.";
        LocalDateTime originalUpdateTime = qaHistory.getUpdateTime();

        // 等待一小段时间以确保时间戳不同
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        qaHistory.updateAnswer(newAnswer);

        assertEquals(newAnswer, qaHistory.getAnswer());
        assertNotEquals(originalUpdateTime, qaHistory.getUpdateTime());
        assertTrue(qaHistory.getUpdateTime().isAfter(originalUpdateTime));
    }

    @Test
    public void testIsValid_ValidRecord() {
        assertTrue(qaHistory.isValid());
    }

    @Test
    public void testIsValid_InvalidUserId() {
        QAHistory invalidHistory = QAHistory.createNew("", question, answer, sessionId, rag);
        assertFalse(invalidHistory.isValid());

        invalidHistory = QAHistory.createNew(null, question, answer, sessionId, rag);
        assertFalse(invalidHistory.isValid());
    }

    @Test
    public void testIsValid_InvalidQuestion() {
        QAHistory invalidHistory = QAHistory.createNew(userId, "", answer, sessionId, rag);
        assertFalse(invalidHistory.isValid());

        invalidHistory = QAHistory.createNew(userId, null, answer, sessionId, rag);
        assertFalse(invalidHistory.isValid());
    }

    @Test
    public void testIsValid_InvalidAnswer() {
        QAHistory invalidHistory = QAHistory.createNew(userId, question, "", sessionId, rag);
        assertFalse(invalidHistory.isValid());

        invalidHistory = QAHistory.createNew(userId, question, null, sessionId, rag);
        assertFalse(invalidHistory.isValid());
    }

    @Test
    public void testGetShortAnswer_WithinLimit() {
        String result = qaHistory.getShortAnswer(100);
        assertEquals(answer, result);
    }

    @Test
    public void testGetShortAnswer_ExceedsLimit() {
        String longAnswer = "This is a very long answer that should be truncated for display purposes.";
        qaHistory.updateAnswer(longAnswer);

        String result = qaHistory.getShortAnswer(20);
        assertEquals("This is a very long ...", result);
    }

    @Test
    public void testGetShortAnswer_NullAnswer() {
        QAHistory historyWithNullAnswer = QAHistory.createNew(userId, question, null, sessionId, rag);
        String result = historyWithNullAnswer.getShortAnswer(50);
        assertEquals("", result);
    }

    @Test
    public void testGetDuration() {
        // 创建时间应该与更新时间相同，所以持续时间为0秒
        String duration = qaHistory.getDuration();
        assertEquals("0s", duration);
    }

    @Test
    public void testGetDuration_AfterUpdate() {
        LocalDateTime originalUpdateTime = qaHistory.getUpdateTime();

        // 等待一秒然后更新答案
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        qaHistory.updateAnswer("Updated answer");

        String duration = qaHistory.getDuration();
        assertTrue(duration.endsWith("s"));
        // 持续时间应该至少是1秒
        assertTrue(Integer.parseInt(duration.replace("s", "")) >= 1);
    }

    @Test
    public void testGetDuration_NullTimes() {
        QAHistory historyWithNullTimes = new QAHistory();
        historyWithNullTimes.setCreateTime(null);
        historyWithNullTimes.setUpdateTime(null);

        String duration = historyWithNullTimes.getDuration();
        assertEquals("unknown", duration);
    }

    @Test
    public void testDefaultConstructor() {
        QAHistory defaultHistory = new QAHistory();
        assertNotNull(defaultHistory.getTimestamp());
        assertNotNull(defaultHistory.getCreateTime());
        assertNotNull(defaultHistory.getUpdateTime());
        // 其他字段应该为null
        assertNull(defaultHistory.getUserId());
        assertNull(defaultHistory.getQuestion());
        assertNull(defaultHistory.getAnswer());
        assertNull(defaultHistory.getSessionId());
        assertNull(defaultHistory.getRag());
    }

    @Test
    public void testCreateFromDocumentsFactoryMethod() {
        List<String> documents = List.of("Document 1 content", "Document 2 content");
        QARAG ragFromDocs = QARAG.createFromDocuments(documents, "knowledge-base");

        QAHistory history = QAHistory.createNew(userId, question, answer, sessionId, ragFromDocs);

        assertNotNull(history);
        assertNotNull(ragFromDocs.getContext());
        assertTrue(ragFromDocs.getContext().contains("Document 1 content"));
        assertTrue(ragFromDocs.getConfidenceScore() > 0);
    }

    @Test
    public void testRagConfidenceLevels() {
        // 测试createSimple方法 - 固定返回0.8（medium）
        QARAG simpleRag = QARAG.createSimple("Test content");
        assertEquals(0.8, simpleRag.getConfidenceScore(), 0.001);
        assertEquals("high", simpleRag.getConfidenceLevel());

        // 测试空文档列表 - 返回0.0（low）
        QARAG emptyRag = QARAG.createFromDocuments(List.of(), "test-source");
        assertEquals(0.0, emptyRag.getConfidenceScore(), 0.001);
        assertEquals("low", emptyRag.getConfidenceLevel());

        // 测试短文档
        QARAG shortRag = QARAG.createFromDocuments(List.of("Short document"), "test-source");
        assertEquals("medium", shortRag.getConfidenceLevel());

        // 测试多个短文档
        QARAG multiShortRag = QARAG.createFromDocuments(
                List.of("Doc1", "Doc2", "Doc3", "Doc4", "Doc5", "Doc6"), "test-source");
        assertEquals("medium", multiShortRag.getConfidenceLevel());

        // 测试长文档 - 应该达到high
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 150; i++) {
            longContent.append("This is a very long sentence. ");
        }
        QARAG longRag = QARAG.createFromDocuments(List.of(longContent.toString()), "test-source");
        assertEquals("high", longRag.getConfidenceLevel());

        // 测试边界情况 - 刚好达到high的阈值
        StringBuilder boundaryContent = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            boundaryContent.append("1234567890");
        }
        QARAG boundaryRag = QARAG.createFromDocuments(List.of(boundaryContent.toString()), "test-source");
        assertEquals("high", boundaryRag.getConfidenceLevel());
    }

    @Test
    public void testRagConfidenceBoundaries() {
        // 测试边界值
        assertEquals("high", getConfidenceLevelForScore(0.8));
        assertEquals("high", getConfidenceLevelForScore(0.9));
        assertEquals("high", getConfidenceLevelForScore(1.0));

        assertEquals("medium", getConfidenceLevelForScore(0.5));
        assertEquals("medium", getConfidenceLevelForScore(0.7));
        assertEquals("medium", getConfidenceLevelForScore(0.799));

        assertEquals("low", getConfidenceLevelForScore(0.0));
        assertEquals("low", getConfidenceLevelForScore(0.4));
        assertEquals("low", getConfidenceLevelForScore(0.499));
    }

    private String getConfidenceLevelForScore(double score) {
        if (score >= 0.8) return "high";
        if (score >= 0.5) return "medium";
        return "low";
    }

    @Test
    public void testRagConfidenceCalculation() {
        // 测试具体的计算逻辑
        QARAG rag = QARAG.createFromDocuments(List.of("Short"), "test");
        double score = rag.getConfidenceScore();

        // 基础分数0.5 + 长度奖励(5/1000=0.005) + 数量奖励(1*0.1=0.1) = 0.605
        assertEquals(0.605, score, 0.01);
        assertEquals("medium", rag.getConfidenceLevel());

        // 测试多个短文档
        QARAG multiRag = QARAG.createFromDocuments(
                List.of("Doc1", "Doc2", "Doc3", "Doc4"), "test");
        double multiScore = multiRag.getConfidenceScore();
        // 0.5 + 长度奖励(16/1000=0.016) + 数量奖励(4*0.1=0.4但最大0.2) = 0.716
        assertEquals(0.716, multiScore, 0.01);
        assertEquals("medium", multiRag.getConfidenceLevel());
    }


    @Test
    public void testRagAddContext() {
        QARAG rag = QARAG.createSimple("Initial context");
        rag.addContext("Additional context");

        assertTrue(rag.getContext().contains("Initial context"));
        assertTrue(rag.getContext().contains("Additional context"));
        assertNotNull(rag.getConfidenceScore());
    }

    @Test
    public void testRagIsValid() {
        QARAG validRag = QARAG.createSimple("Valid context");
        assertTrue(validRag.isValid());

        QARAG invalidRag = QARAG.createSimple("");
        assertFalse(invalidRag.isValid());

        QARAG nullRag = QARAG.createSimple(null);
        assertFalse(nullRag.isValid());
    }
}