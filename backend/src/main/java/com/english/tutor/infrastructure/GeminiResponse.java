package com.english.tutor.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {
    public List<Candidate> candidates;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        public Content content;
        public String finishReason;
        public Integer index;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        public List<Part> parts;
        public String role;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        public String text;
    }
}
