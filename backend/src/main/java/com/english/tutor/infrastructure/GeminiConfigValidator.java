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

    @ConfigProperty(name = "quarkus.profile")
    String profile;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Validando configuração da API do Gemini para o profile: " + profile);
        if (apiKey == null || apiKey.trim().isEmpty() || "mock-key".equalsIgnoreCase(apiKey.trim())) {
            String errorMsg = "A variável de ambiente GEMINI_API_KEY não está definida ou usa o mock-key padrão.";
            
            if ("dev".equalsIgnoreCase(profile) || "test".equalsIgnoreCase(profile)) {
                LOGGER.warn("WARNING: " + errorMsg + " A integração real com a API do Gemini falhará, mas o servidor continuará rodando para testes locais.");
            } else {
                LOGGER.fatal("CRITICAL ERROR: " + errorMsg + " A aplicação não pode ser iniciada em produção.");
                throw new IllegalStateException("CRITICAL ERROR: " + errorMsg);
            }
        } else {
            LOGGER.info("Configuração da API do Gemini validada com sucesso.");
        }
    }
}
