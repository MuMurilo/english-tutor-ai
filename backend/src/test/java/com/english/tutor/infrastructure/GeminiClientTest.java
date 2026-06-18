package com.english.tutor.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeminiClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldDeserializeGeminiResponseCorrectly() throws Exception {
        String json = "{\n" +
                "  \"candidates\": [\n" +
                "    {\n" +
                "      \"content\": {\n" +
                "        \"parts\": [\n" +
                "          {\n" +
                "            \"text\": \"Hello! I am your English tutor. How can I help you today?\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"role\": \"model\"\n" +
                "      },\n" +
                "      \"finishReason\": \"STOP\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        GeminiResponse response = objectMapper.readValue(json, GeminiResponse.class);

        assertNotNull(response);
        assertNotNull(response.candidates);
        assertEquals(1, response.candidates.size());
        assertNotNull(response.candidates.get(0).content);
        assertNotNull(response.candidates.get(0).content.parts);
        assertEquals(1, response.candidates.get(0).content.parts.size());
        assertEquals("Hello! I am your English tutor. How can I help you today?", 
                     response.candidates.get(0).content.parts.get(0).text);
    }
}
