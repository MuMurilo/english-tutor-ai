package com.english.tutor.infrastructure;

import com.english.tutor.domain.AIService;
import com.english.tutor.domain.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GeminiAIService implements AIService {

    @Inject
    @RestClient
    GeminiClient geminiClient;

    @ConfigProperty(name = "gemini.api.key")
    String apiKey;

    @ConfigProperty(name = "gemini.model", defaultValue = "gemini-3.5-flash")
    String modelName;

    private static final org.jboss.logging.Logger LOG = org.jboss.logging.Logger.getLogger(GeminiAIService.class);

    @Override
    public String generateChatResponse(String systemPrompt, List<ChatMessage> recentHistory) {
        List<GeminiRequest.Content> conversation = new ArrayList<>();
        for (ChatMessage msg : recentHistory) {
            String role = "USER".equalsIgnoreCase(msg.getSender()) ? "user" : "model";
            conversation.add(new GeminiRequest.Content(role, msg.getContent()));
        }

        GeminiRequest request = new GeminiRequest(systemPrompt, conversation);
        try {
            GeminiResponse response = geminiClient.generateContent(modelName, apiKey, request);
            if (response != null && response.candidates != null && !response.candidates.isEmpty()) {
                GeminiResponse.Candidate candidate = response.candidates.get(0);
                if (candidate.content != null && candidate.content.parts != null && !candidate.content.parts.isEmpty()) {
                    return candidate.content.parts.get(0).text;
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
            throw new RuntimeException("Falha na chamada da API do Gemini", e);
        }
        throw new RuntimeException("Resposta vazia da API do Gemini");
    }

    @Override
    public String analyzeFeedback(String systemPrompt, String dialogue) {
        GeminiRequest request = new GeminiRequest(systemPrompt, dialogue);
        try {
            GeminiResponse response = geminiClient.generateContent(modelName, apiKey, request);
            if (response != null && response.candidates != null && !response.candidates.isEmpty()) {
                GeminiResponse.Candidate candidate = response.candidates.get(0);
                if (candidate.content != null && candidate.content.parts != null && !candidate.content.parts.isEmpty()) {
                    return candidate.content.parts.get(0).text;
                }
            }
        } catch (Exception e) {
            LOG.error("GEMINI FEEDBACK API ERROR: " + e.getMessage(), e);
            throw new RuntimeException("Falha na chamada de feedback da API do Gemini", e);
        }
        throw new RuntimeException("Resposta vazia de feedback da API do Gemini");
    }
}
