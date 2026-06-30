package com.english.tutor.application;

import com.english.tutor.domain.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ChatService {

    private static final Logger LOG = Logger.getLogger(ChatService.class);

    @Inject
    ChatMessageRepository chatMessageRepository;

    @Inject
    FeedbackRepository feedbackRepository;

    @Inject
    FeedbackService feedbackService;

    @Inject
    AIService aiService;

    public List<ChatMessage> getChatHistory(Long userId) {
        return chatMessageRepository.findByUserId(userId);
    }

    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
        return message;
    }

    public ChatMessage sendMessage(Long userId, String englishLevel, String userContent) {
        // 1. Salvar a mensagem do usuário imediatamente
        ChatMessage userMsg = new ChatMessage(null, userId, "USER", userContent, LocalDateTime.now());
        saveMessage(userMsg);

        // 2. Obter feedbacks passados do estudante para personalizar o prompt
        List<Feedback> feedbacks = feedbackRepository.findByUserId(userId);

        // 3. Montar o prompt de sistema personalizado por nível e dificuldades anteriores
        String systemPrompt = TutorPromptBuilder.buildSystemPrompt(englishLevel, feedbacks);

        // 4. Carregar histórico do banco para manter contexto no Gemini (limitar aos últimos 20 para economizar tokens)
        List<ChatMessage> history = chatMessageRepository.findByUserId(userId);
        int startIdx = Math.max(0, history.size() - 20);
        List<ChatMessage> recentHistory = history.subList(startIdx, history.size());

        // 5. Chamar o serviço de IA
        String tutorResponseText = "Hello! I am having some issues connecting to my core brain right now, but let's keep practicing English!";
        try {
            tutorResponseText = aiService.generateChatResponse(systemPrompt, recentHistory);
        } catch (Exception e) {
            LOG.error("AIService error: " + e.getMessage(), e);
            // Log do erro e fallback amigável conforme requisitos
            tutorResponseText = "I had a small connection glitch, but please repeat what you said. Let's continue practicing!";
        }

        // 6. Salvar e retornar a resposta do tutor
        ChatMessage tutorMsg = new ChatMessage(null, userId, "TUTOR", tutorResponseText, LocalDateTime.now());
        saveMessage(tutorMsg);

        // Disparar análise de feedback assíncrona com o histórico de mensagens atualizado (limitar às últimas 4 para economia de tokens e contexto)
        List<ChatMessage> updatedHistory = chatMessageRepository.findByUserId(userId);
        int fbStartIdx = Math.max(0, updatedHistory.size() - 4);
        List<ChatMessage> feedbackHistory = updatedHistory.subList(fbStartIdx, updatedHistory.size());
        feedbackService.analyzeFeedbackAsync(userId, feedbackHistory);

        return tutorMsg;
    }
}
