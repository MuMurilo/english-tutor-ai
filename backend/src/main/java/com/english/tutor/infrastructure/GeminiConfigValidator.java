package com.english.tutor.infrastructure;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import java.util.List;

@ApplicationScoped
public class GeminiConfigValidator {

    private static final Logger LOGGER = Logger.getLogger(GeminiConfigValidator.class);

    @ConfigProperty(name = "gemini.api.key")
    String apiKey;

    void onStart(@Observes StartupEvent ev) {
        List<String> activeProfiles = getActiveProfiles();
        LOGGER.info("Validando configuração da API do Gemini para os profiles: " + activeProfiles);
        validate(apiKey, activeProfiles);
    }

    void validate(String key, List<String> activeProfiles) {
        if (key == null || key.trim().isEmpty() || "mock-key".equalsIgnoreCase(key.trim())) {
            String errorMsg = "A variável de ambiente GEMINI_API_KEY não está definida ou usa o mock-key padrão.";
            
            if (activeProfiles != null && (activeProfiles.contains("dev") || activeProfiles.contains("test"))) {
                LOGGER.warn("WARNING: " + errorMsg + " A integração real com a API do Gemini falhará, mas o servidor continuará rodando para testes locais.");
            } else {
                LOGGER.fatal("CRITICAL ERROR: " + errorMsg + " A aplicação não pode ser iniciada em produção.");
                throw new IllegalStateException("CRITICAL ERROR: " + errorMsg);
            }
        } else {
            LOGGER.info("Configuração da API do Gemini validada com sucesso.");
        }
    }

    List<String> getActiveProfiles() {
        return ConfigUtils.getProfiles();
    }
}
