package com.english.tutor.infrastructure;

import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {
    public List<Content> contents = new ArrayList<>();
    public SystemInstruction systemInstruction;
    public GenerationConfig generationConfig = new GenerationConfig();

    public GeminiRequest() {}

    public GeminiRequest(String systemPrompt, String userMessage) {
        this.systemInstruction = new SystemInstruction(systemPrompt);
        this.contents.add(new Content("user", userMessage));
    }

    public GeminiRequest(String systemPrompt, List<Content> conversationHistory) {
        this.systemInstruction = new SystemInstruction(systemPrompt);
        this.contents.addAll(conversationHistory);
    }

    public static class Content {
        public List<Part> parts = new ArrayList<>();
        public String role; // "user" or "model"

        public Content() {}

        public Content(String role, String text) {
            this.role = role;
            this.parts.add(new Part(text));
        }
    }

    public static class SystemInstruction {
        public List<Part> parts = new ArrayList<>();

        public SystemInstruction() {}

        public SystemInstruction(String text) {
            this.parts.add(new Part(text));
        }
    }

    public static class Part {
        public String text;

        public Part() {}

        public Part(String text) {
            this.text = text;
        }
    }

    public static class GenerationConfig {
        public Double temperature = 0.7;
        public Integer maxOutputTokens = 1000;
    }
}
