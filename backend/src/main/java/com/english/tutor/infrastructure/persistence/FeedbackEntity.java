package com.english.tutor.infrastructure.persistence;

import com.english.tutor.domain.Feedback;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String type; // "ERROR" ou "CONSOLIDATED"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "original_phrase", columnDefinition = "TEXT")
    private String originalPhrase;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public FeedbackEntity() {
    }

    // Métodos de Mapeamento (Mapper)
    public static FeedbackEntity fromDomain(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        FeedbackEntity entity = new FeedbackEntity();
        entity.setId(feedback.getId());
        entity.setUserId(feedback.getUserId());
        entity.setType(feedback.getType());
        entity.setContent(feedback.getContent());
        entity.setOriginalPhrase(feedback.getOriginalPhrase());
        entity.setExplanation(feedback.getExplanation());
        entity.setTimestamp(feedback.getTimestamp());
        return entity;
    }

    public Feedback toDomain() {
        return new Feedback(this.id, this.userId, this.type, this.content, this.originalPhrase, this.explanation, this.timestamp);
    }

    // Getters e Setters
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
