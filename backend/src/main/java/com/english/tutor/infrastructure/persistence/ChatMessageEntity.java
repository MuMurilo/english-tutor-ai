package com.english.tutor.infrastructure.persistence;

import com.english.tutor.domain.ChatMessage;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String sender; // "USER" ou "TUTOR"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ChatMessageEntity() {
    }

    // Métodos de Mapeamento (Mapper)
    public static ChatMessageEntity fromDomain(ChatMessage message) {
        if (message == null) {
            return null;
        }
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setId(message.getId());
        entity.setUserId(message.getUserId());
        entity.setSender(message.getSender());
        entity.setContent(message.getContent());
        entity.setTimestamp(message.getTimestamp());
        return entity;
    }

    public ChatMessage toDomain() {
        return new ChatMessage(this.id, this.userId, this.sender, this.content, this.timestamp);
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
