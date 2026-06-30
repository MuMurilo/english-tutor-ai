package com.english.tutor.infrastructure;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class GeminiConfigValidator {

    private static final Logger LOGGER = Logger.getLogger(GeminiConfigValidator.class);

    @ConfigProperty(name = "gemini.api.key")
    String apiKey;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Validando configuração da API do Gemini...");
        if (apiKey == null || apiKey.trim().isEmpty() || "mock-key".equalsIgnoreCase(apiKey.trim())) {
            String errorMsg = "CRITICAL ERROR: A variável de ambiente GEMINI_API_KEY não está definida ou está configurada com a chave mock-key padrão. A aplicação não pode ser iniciada de forma segura.";
            LOGGER.fatal(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        LOGGER.info("Configuração da API do Gemini validada com sucesso.");
    }
}
