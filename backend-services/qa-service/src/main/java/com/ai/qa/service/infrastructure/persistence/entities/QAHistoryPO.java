package com.ai.qa.service.infrastructure.persistence.entities;

import javax.persistence.*; // 使用 javax.persistence 而不是 jakarta
import lombok.Data;
import java.time.LocalDateTime;

/**
 * QA历史记录持久化对象（Persistent Object）
 * 对应数据库中的qa_history表
 * 使用JPA注解进行ORM映射
 */
@Data
@Entity
@Table(name = "qa_history_rw")
public class QAHistoryPO {

    /**
     * 主键ID，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 用户问题，最大长度2000字符
     */
    @Column(name = "question", length = 2000)
    private String question;

    /**
     * AI回答，最大长度4000字符
     */
    @Column(name = "answer", length = 4000)
    private String answer;

    /**
     * 问答时间戳
     */
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    /**
     * 会话ID，用于关联同一会话中的多条问答记录
     */
    @Column(name = "session_id")
    private String sessionId;

    /**
     * 记录创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 持久化前的回调方法，自动设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    /**
     * 更新前的回调方法，自动更新更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}