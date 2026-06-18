package com.english.tutor.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChatTest {

    @Test
    public void shouldBuildSystemPromptForBeginnerWithoutFeedback() {
        String prompt = TutorPromptBuilder.buildSystemPrompt("BEGINNER", new ArrayList<>());
        
        assertNotNull(prompt);
        assertTrue(prompt.contains("BEGINNER") || prompt.contains("simple English"), "Prompt should contain beginner instructions");
        assertTrue(prompt.contains("short sentences"), "Prompt should mention short sentences");
        assertFalse(prompt.contains("Erros anteriores do aluno"), "Should not contain feedback section when empty");
    }

    @Test
    public void shouldBuildSystemPromptForAdvancedWithoutFeedback() {
        String prompt = TutorPromptBuilder.buildSystemPrompt("ADVANCED", new ArrayList<>());
        
        assertNotNull(prompt);
        assertTrue(prompt.toLowerCase().contains("advanced") || prompt.contains("sophisticated"), "Prompt should contain advanced instructions");
        assertTrue(prompt.contains("complex sentence"), "Prompt should mention complex sentences");
    }

    @Test
    public void shouldBuildSystemPromptWithPastFeedback() {
        List<Feedback> feedbacks = new ArrayList<>();
        Feedback f1 = new Feedback(1L, 1L, "ERROR", "I goes to school", "I go to school", "Verbo conjugado incorretamente para primeira pessoa.", LocalDateTime.now());
        Feedback f2 = new Feedback(2L, 1L, "CONSOLIDATED", "Subtle", "A subtle nuance", "Uso correto do adjetivo sutil.", LocalDateTime.now());
        feedbacks.add(f1);
        feedbacks.add(f2);

        String prompt = TutorPromptBuilder.buildSystemPrompt("INTERMEDIATE", feedbacks);

        assertNotNull(prompt);
        assertTrue(prompt.contains("Erros anteriores do aluno") || prompt.contains("Past errors"), "Prompt should list past errors to address");
        assertTrue(prompt.contains("I goes to school"), "Prompt should contain the wrong sentence");
        assertTrue(prompt.contains("I go to school"), "Prompt should contain the correct explanation/phrase");
    }
}
