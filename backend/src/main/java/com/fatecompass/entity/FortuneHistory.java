package com.fatecompass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 算命歷史記錄實體類
 */
@Entity
@Table(name = "FC_FORTUNE_HISTORY")
public class FortuneHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fortune_seq")
    @SequenceGenerator(name = "fortune_seq", sequenceName = "FC_FORTUNE_SEQ", allocationSize = 1)
    @Column(name = "HISTORY_ID")
    private Long historyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "FORTUNE_TYPE", nullable = false, length = 20)
    private FortuneType fortuneType;
    
    @NotBlank(message = "輸入數據不能為空")
    @Column(name = "INPUT_DATA", length = 1000)
    private String inputData;
    
    @Column(name = "RESULT_DATA", length = 2000)
    private String resultData;
    
    @Column(name = "SCORE")
    private Integer score;
    
    @Column(name = "ANALYSIS", length = 2000)
    private String analysis;
    
    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
    
    // 枚舉：算命類型
    public enum FortuneType {
        BAZI("生辰八字"),
        NAME("姓名算命"),
        DAILY("每日運勢"),
        ZODIAC("生肖運勢");
        
        private final String description;
        
        FortuneType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 構造函數
    public FortuneHistory() {}
    
    public FortuneHistory(User user, FortuneType fortuneType, String inputData, String resultData) {
        this.user = user;
        this.fortuneType = fortuneType;
        this.inputData = inputData;
        this.resultData = resultData;
    }
    
    // Getter和Setter方法
    public Long getHistoryId() {
        return historyId;
    }
    
    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public FortuneType getFortuneType() {
        return fortuneType;
    }
    
    public void setFortuneType(FortuneType fortuneType) {
        this.fortuneType = fortuneType;
    }
    
    public String getInputData() {
        return inputData;
    }
    
    public void setInputData(String inputData) {
        this.inputData = inputData;
    }
    
    public String getResultData() {
        return resultData;
    }
    
    public void setResultData(String resultData) {
        this.resultData = resultData;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public String getAnalysis() {
        return analysis;
    }
    
    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 