package com.english.tutor.domain;

import java.util.List;

public class TutorPromptBuilder {

    public static String buildSystemPrompt(String englishLevel, List<Feedback> pastFeedbacks) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a friendly and encouraging AI English tutor. Always respond in English. ");
        prompt.append("If the student writes to you in Portuguese or any language other than English, do NOT reply in that language. ")
              .append("Instead, always respond in English, gently reminding them that you can only speak English and helping them translate or rephrase their request in English. ");
        
        // Adapt according to English level
        String level = englishLevel != null ? englishLevel.toUpperCase() : "BEGINNER";
        switch (level) {
            case "BEGINNER":
                prompt.append("Speak in very simple English. Use short sentences, basic vocabulary, and simple tenses. ")
                      .append("Avoid complex grammar, relative clauses, or idioms.");
                break;
            case "ADVANCED":
                prompt.append("Speak in sophisticated, rich, and advanced English. Use complex sentence structures, ")
                      .append("idioms, and phrasal verbs. Engage the student in deep, high-level discussions.");
                break;
            case "INTERMEDIATE":
            default:
                prompt.append("Speak in standard conversational English. Use common vocabulary and standard ")
                      .append("grammatical structures. Use moderate-length sentences and encourage natural conversation flow.");
                break;
        }

        // Inject past feedback if available
        if (pastFeedbacks != null && !pastFeedbacks.isEmpty()) {
            boolean hasErrors = false;
            for (Feedback f : pastFeedbacks) {
                if ("ERROR".equalsIgnoreCase(f.getType())) {
                    hasErrors = true;
                    break;
                }
            }

            if (hasErrors) {
                prompt.append("\n\nErros anteriores do aluno para você monitorar e ajudar a corrigir:\n");
                for (Feedback f : pastFeedbacks) {
                    if ("ERROR".equalsIgnoreCase(f.getType())) {
                        prompt.append(String.format(" - Frase errada: \"%s\". Forma correta: \"%s\". Explicação: %s\n",
                                f.getOriginalPhrase(), f.getContent(), f.getExplanation()));
                    }
                }
            }
        }

        return prompt.toString();
    }
}
