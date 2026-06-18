package com.english.tutor.application;

import com.english.tutor.domain.Feedback;
import com.english.tutor.domain.FeedbackRepository;
import com.english.tutor.infrastructure.GeminiClient;
import com.english.tutor.infrastructure.GeminiRequest;
import com.english.tutor.infrastructure.GeminiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DashboardService {

    @Inject
    FeedbackRepository feedbackRepository;

    @Inject
    @RestClient
    GeminiClient geminiClient;

    @ConfigProperty(name = "gemini.api.key")
    String apiKey;

    @ConfigProperty(name = "gemini.model", defaultValue = "gemini-3.5-flash")
    String modelName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DidacticReportDto generateReport(Long userId) {
        List<Feedback> feedbacks = feedbackRepository.findByUserId(userId);
        if (feedbacks == null || feedbacks.isEmpty()) {
            return new DidacticReportDto(
                "Ainda não há dados suficientes para gerar seu relatório pedagógico. Converse mais com o tutor de inglês no chat para coletarmos análises da sua conversação!",
                new ArrayList<>(),
                new ArrayList<>(),
                "Continue praticando frases e diálogos no chat!"
            );
        }

        // 1. Definir o prompt de sistema do coordenador pedagógico
        String systemPrompt = "Você é um coordenador pedagógico de inglês e tutor especialista. " +
                "Sua tarefa é analisar a lista de acertos e erros de conversação de um estudante de inglês " +
                "e criar um relatório didático estruturado em formato JSON. " +
                "O JSON deve seguir rigorosamente esta estrutura:\n" +
                "{\n" +
                "  \"summary\": \"Resumo geral amigável e encorajador da performance em 2 a 3 frases em português.\",\n" +
                "  \"strengths\": [\n" +
                "    \"Ponto forte 1 em português (ex: bom vocabulário para saudações)\",\n" +
                "    \"Ponto forte 2 em português\"\n" +
                "  ],\n" +
                "  \"weaknesses\": [\n" +
                "    \"Ponto de melhoria 1 em português (ex: atentar-se à pontuação em perguntas)\",\n" +
                "    \"Ponto de melhoria 2 em português\"\n" +
                "  ],\n" +
                "  \"actionPlan\": \"Um plano de ação amigável e direto com dicas de estudos focadas em português para o estudante.\"\n" +
                "}\n" +
                "Não adicione nenhuma explicação antes ou depois do JSON. Retorne apenas o objeto JSON.";

        // 2. Formatar os feedbacks para a chamada
        StringBuilder promptBuilder = new StringBuilder("Aqui está a lista de feedbacks do histórico de aprendizado do estudante:\n\n");
        for (Feedback f : feedbacks) {
            if ("ERROR".equalsIgnoreCase(f.getType())) {
                promptBuilder.append(String.format("- Erro: \"%s\" (Forma Correta: \"%s\"). Explicação pedagógica: %s\n", 
                    f.getOriginalPhrase(), f.getContent(), f.getExplanation()));
            } else {
                promptBuilder.append(String.format("- Acerto: \"%s\" (Exemplo: \"%s\"). Significado/Contexto: %s\n", 
                    f.getOriginalPhrase(), f.getContent(), f.getExplanation()));
            }
        }
        promptBuilder.append("\nCom base no histórico acima, gere o relatório didático em formato JSON.");

        try {
            GeminiRequest request = new GeminiRequest(systemPrompt, promptBuilder.toString());
            GeminiResponse response = geminiClient.generateContent(modelName, apiKey, request);

            if (response != null && response.candidates != null && !response.candidates.isEmpty()) {
                GeminiResponse.Candidate candidate = response.candidates.get(0);
                if (candidate.content != null && candidate.content.parts != null && !candidate.content.parts.isEmpty()) {
                    String rawText = candidate.content.parts.get(0).text;
                    String cleanedJson = cleanJson(rawText);
                    return objectMapper.readValue(cleanedJson, DidacticReportDto.class);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório didático via Gemini: " + e.getMessage());
        }

        // Fallback caso a API falhe ou ocorra erro de parsing
        return new DidacticReportDto(
            "Tivemos uma pequena oscilação para gerar seu relatório pedagógico personalizado agora. Mas notei que você está praticando frases e diálogos diariamente! Continue assim.",
            List.of("Esforço contínuo e dedicação aos estudos no chat"),
            List.of("Revisão de pontuação simples e capitalização"),
            "Tente gerar o relatório pedagógico novamente em alguns minutos clicando no botão de atualizar."
        );
    }

    private String cleanJson(String rawText) {
        if (rawText == null) return "{}";
        String trimmed = rawText.trim();
        if (trimmed.startsWith("```")) {
            int firstLineEnd = trimmed.indexOf("\n");
            if (firstLineEnd != -1) {
                trimmed = trimmed.substring(firstLineEnd).trim();
            }
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3).trim();
            }
        }
        return trimmed;
    }
}
