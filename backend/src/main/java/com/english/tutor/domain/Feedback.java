package com.english.tutor.domain;

import java.time.LocalDateTime;

public class Feedback {
    private Long id;
    private Long userId;
    private String type; // "ERROR" ou "CONSOLIDATED"
    private String content;
    private String originalPhrase;
    private String explanation;
    private LocalDateTime timestamp;

    public Feedback() {
    }

    public Feedback(Long id, Long userId, String type, String content, String originalPhrase, String explanation, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.originalPhrase = originalPhrase;
        this.explanation = explanation;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOriginalPhrase() {
        return originalPhrase;
    }

    public void setOriginalPhrase(String originalPhrase) {
        this.originalPhrase = originalPhrase;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
