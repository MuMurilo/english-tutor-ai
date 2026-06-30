package com.english.tutor.application;

import com.english.tutor.domain.*;
import com.english.tutor.infrastructure.GeminiClient;
import com.english.tutor.infrastructure.GeminiRequest;
import com.english.tutor.infrastructure.GeminiResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

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
    @RestClient
    GeminiClient geminiClient;

    @ConfigProperty(name = "gemini.api.key")
    String apiKey;

    @ConfigProperty(name = "gemini.model", defaultValue = "gemini-3.5-flash")
    String modelName;

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
        List<GeminiRequest.Content> conversation = new ArrayList<>();
        
        int startIdx = Math.max(0, history.size() - 20);
        List<ChatMessage> recentHistory = history.subList(startIdx, history.size());
        for (ChatMessage msg : recentHistory) {
            String role = "USER".equalsIgnoreCase(msg.getSender()) ? "user" : "model";
            conversation.add(new GeminiRequest.Content(role, msg.getContent()));
        }

        // 5. Chamar o Gemini
        GeminiRequest request = new GeminiRequest(systemPrompt, conversation);
        String tutorResponseText = "Hello! I am having some issues connecting to my core brain right now, but let's keep practicing English!";
        
        try {
            GeminiResponse response = geminiClient.generateContent(modelName, apiKey, request);
            if (response != null && response.candidates != null && !response.candidates.isEmpty()) {
                GeminiResponse.Candidate candidate = response.candidates.get(0);
                if (candidate.content != null && candidate.content.parts != null && !candidate.content.parts.isEmpty()) {
                    tutorResponseText = candidate.content.parts.get(0).text;
                }
            }
        } catch (Exception e) {
            if (e instanceof jakarta.ws.rs.WebApplicationException) {
                jakarta.ws.rs.WebApplicationException wae = (jakarta.ws.rs.WebApplicationException) e;
                try {
                    String errorBody = wae.getResponse().readEntity(String.class);
                    LOG.error("GEMINI API ERROR BODY: " + errorBody);
                } catch (Exception ex) {
                    // Ignore
                }
            }
            LOG.error("GEMINI API ERROR: " + e.getMessage(), e);
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
