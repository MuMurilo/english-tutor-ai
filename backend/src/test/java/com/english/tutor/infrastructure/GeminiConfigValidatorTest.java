package com.english.tutor.infrastructure;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GeminiConfigValidatorTest {

    private final GeminiConfigValidator validator = new GeminiConfigValidator();

    @Test
    public void shouldAllowMockKeyInDevProfile() {
        // Dev profile with mock-key should not throw exception
        assertDoesNotThrow(() -> validator.validate("mock-key", List.of("dev")));
        assertDoesNotThrow(() -> validator.validate("  mock-key  ", List.of("dev")));
        assertDoesNotThrow(() -> validator.validate(null, List.of("dev")));
        assertDoesNotThrow(() -> validator.validate("", List.of("dev")));
    }

    @Test
    public void shouldAllowMockKeyInTestProfile() {
        // Test profile with mock-key should not throw exception
        assertDoesNotThrow(() -> validator.validate("mock-key", List.of("test")));
        assertDoesNotThrow(() -> validator.validate(null, List.of("test")));
    }

    @Test
    public void shouldBlockMockKeyInProdProfile() {
        // Prod profile with mock-key should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> validator.validate("mock-key", List.of("prod")));
        assertThrows(IllegalStateException.class, () -> validator.validate("", List.of("prod")));
        assertThrows(IllegalStateException.class, () -> validator.validate(null, List.of("prod")));
    }

    @Test
    public void shouldAllowValidKeyInProdProfile() {
        // Prod profile with valid key should not throw exception
        assertDoesNotThrow(() -> validator.validate("valid-api-key-12345", List.of("prod")));
    }
}
