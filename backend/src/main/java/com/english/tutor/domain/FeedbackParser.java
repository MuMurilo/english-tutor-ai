package com.english.tutor.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(FeedbackParser.class.getName());

    public static List<Feedback> parse(String rawJson, Long userId) {
        List<Feedback> feedbacks = new ArrayList<>();
        if (rawJson == null || rawJson.trim().isEmpty()) {
            return feedbacks;
        }

        try {
            String cleanJson = cleanMarkdown(rawJson);
            FeedbackDto dto = objectMapper.readValue(cleanJson, FeedbackDto.class);

            if (dto.errors != null) {
                for (FeedbackItem item : dto.errors) {
                    feedbacks.add(new Feedback(
                            null,
                            userId,
                            "ERROR",
                            item.correctPhrase,
                            item.originalPhrase,
                            item.explanation,
                            LocalDateTime.now()
                    ));
                }
            }

            if (dto.consolidated != null) {
                for (FeedbackItem item : dto.consolidated) {
                    feedbacks.add(new Feedback(
                            null,
                            userId,
                            "CONSOLIDATED",
                            item.correctPhrase,
                            item.originalPhrase,
                            item.explanation,
                            LocalDateTime.now()
                    ));
                }
            }

        } catch (Exception e) {
            // Se falhar o parse estruturado por alguma variação do LLM, loga ou ignora silenciosamente
            LOGGER.warning("Erro ao fazer parse do feedback do Gemini: " + e.getMessage());
        }

        return feedbacks;
    }

    private static String cleanMarkdown(String text) {
        String cleaned = text.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeedbackDto {
        public List<FeedbackItem> errors;
        public List<FeedbackItem> consolidated;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeedbackItem {
        public String originalPhrase;
        public String correctPhrase;
        public String explanation;
    }
}
