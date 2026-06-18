package com.english.tutor.domain;

import java.util.List;

public interface ChatMessageRepository {
    void save(ChatMessage message);
    List<ChatMessage> findByUserId(Long userId);
}
