package com.english.tutor.domain;

import java.util.List;

public interface AIService {
    String generateChatResponse(String systemPrompt, List<ChatMessage> recentHistory);
    String analyzeFeedback(String systemPrompt, String dialogue);
}
