package com.english.tutor.infrastructure.parser;

import com.english.tutor.domain.Feedback;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackParserTest {

    @Test
    public void shouldParseStructuredFeedbackCorrectly() {
        String jsonResponse = "{\n" +
                "  \"errors\": [\n" +
                "    {\n" +
                "      \"originalPhrase\": \"He don't like milk\",\n" +
                "      \"correctPhrase\": \"He doesn't like milk\",\n" +
                "      \"explanation\": \"Para terceira pessoa do singular, use doesn't em vez de don't.\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"consolidated\": [\n" +
                "    {\n" +
                "      \"originalPhrase\": \"Acquire\",\n" +
                "      \"correctPhrase\": \"To acquire new skills\",\n" +
                "      \"explanation\": \"Uso apropriado do verbo adquirir no contexto de habilidades.\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        List<Feedback> feedbacks = FeedbackParser.parse(jsonResponse, 42L);

        assertNotNull(feedbacks);
        assertEquals(2, feedbacks.size());

        Feedback errorFeedback = feedbacks.stream()
                .filter(f -> "ERROR".equals(f.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(errorFeedback);
        assertEquals(42L, errorFeedback.getUserId());
        assertEquals("He don't like milk", errorFeedback.getOriginalPhrase());
        assertEquals("He doesn't like milk", errorFeedback.getContent());
        assertEquals("Para terceira pessoa do singular, use doesn't em vez de don't.", errorFeedback.getExplanation());

        Feedback consolidatedFeedback = feedbacks.stream()
                .filter(f -> "CONSOLIDATED".equals(f.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(consolidatedFeedback);
        assertEquals(42L, consolidatedFeedback.getUserId());
        assertEquals("Acquire", consolidatedFeedback.getOriginalPhrase());
        assertEquals("To acquire new skills", consolidatedFeedback.getContent());
    }

    @Test
    public void shouldCleanMarkdownJsonBlockAndParse() {
        String markdownResponse = "```json\n" +
                "{\n" +
                "  \"errors\": [],\n" +
                "  \"consolidated\": [\n" +
                "    {\n" +
                "      \"originalPhrase\": \"Subtle\",\n" +
                "      \"correctPhrase\": \"A subtle nuance\",\n" +
                "      \"explanation\": \"Uso correto do termo.\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "```";

        List<Feedback> feedbacks = FeedbackParser.parse(markdownResponse, 42L);

        assertNotNull(feedbacks);
        assertEquals(1, feedbacks.size());
        assertEquals("Subtle", feedbacks.get(0).getOriginalPhrase());
    }
}
