package com.english.tutor.application;

import com.english.tutor.domain.AIService;
import com.english.tutor.domain.ChatMessage;
import com.english.tutor.domain.Feedback;
import com.english.tutor.domain.FeedbackRepository;
import com.english.tutor.infrastructure.parser.FeedbackParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.util.List;

@ApplicationScoped
public class FeedbackService {

    @Inject
    FeedbackRepository feedbackRepository;

    @Inject
    AIService aiService;

    @Inject
    ManagedExecutor managedExecutor;

    private static final org.jboss.logging.Logger LOG = org.jboss.logging.Logger.getLogger(FeedbackService.class);

    public void analyzeFeedbackAsync(Long userId, List<ChatMessage> conversationHistory) {
        if (conversationHistory == null || conversationHistory.isEmpty()) {
            return;
        }

        // Executar de forma assíncrona usando o ManagedExecutor do Quarkus (não bloqueante)
        managedExecutor.runAsync(() -> {
            try {
                // 1. Formatar histórico da conversa em texto para o Gemini
                StringBuilder dialogueBuilder = new StringBuilder("Analise a seguinte conversa entre um estudante de inglês (USER) e o Tutor (TUTOR):\n\n");
                for (ChatMessage msg : conversationHistory) {
                    dialogueBuilder.append(msg.getSender()).append(": ").append(msg.getContent()).append("\n");
                }
                dialogueBuilder.append("\nAnalise APENAS a última fala dita pelo estudante (USER) no diálogo acima. As mensagens anteriores servem apenas como contexto para entender a conversa.");

                // 2. Definir o prompt de sistema do analisador com restrição estrita às frases do USER
                String systemPrompt = "Você é um analisador linguístico especializado em ensino de inglês. " +
                        "Analise o diálogo fornecido e identifique os erros gramaticais e acertos/novas palavras do estudante (USER). " +
                        "ATENÇÃO CRÍTICA: Você deve analisar EXCLUSIVAMENTE a última frase dita pelo estudante (USER) no diálogo. " +
                        "Ignore completamente todas as frases ditas pelo Tutor (TUTOR) e as frases mais antigas do USER. " +
                        "Nunca inclua em 'originalPhrase' expressões, termos ou frases ditas pelo TUTOR ou de mensagens anteriores do USER. " +
                        "Responda EXCLUSIVAMENTE em formato JSON com o seguinte layout:\n" +
                        "{\n" +
                        "  \"errors\": [\n" +
                        "    {\n" +
                        "      \"originalPhrase\": \"frase incorreta dita pelo aluno na última mensagem\",\n" +
                        "      \"correctPhrase\": \"correção da frase em inglês\",\n" +
                        "      \"explanation\": \"explicação didática do erro em português\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"consolidated\": [\n" +
                        "    {\n" +
                        "      \"originalPhrase\": \"palavra ou expressão em inglês bem utilizada pelo aluno na última mensagem\",\n" +
                        "      \"correctPhrase\": \"exemplo curto de uso em inglês\",\n" +
                        "      \"explanation\": \"significado e contexto da expressão em português\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n" +
                        "Se não houver erros ou vocabulários novos na última mensagem, retorne os respectivos arrays vazios. Não acrescente explicações fora do JSON.";

                // 3. Chamar o serviço de IA com mecanismo de retry
                String rawJson = null;
                int retries = 3;
                int delayMs = 1500;
                for (int i = 0; i < retries; i++) {
                    try {
                        rawJson = aiService.analyzeFeedback(systemPrompt, dialogueBuilder.toString());
                        break;
                    } catch (Exception ex) {
                        if (i == retries - 1) {
                            throw ex;
                        }
                        LOG.warn("Tentativa " + (i + 1) + " de análise de feedback falhou. Tentando novamente em " + delayMs + "ms... Erro: " + ex.getMessage());
                        try {
                            Thread.sleep(delayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw ex;
                        }
                        delayMs *= 2;
                    }
                }

                if (rawJson != null && !rawJson.trim().isEmpty()) {
                    // 4. Fazer parse do JSON e salvar na base de dados
                    List<Feedback> feedbacks = FeedbackParser.parse(rawJson, userId);
                    if (!feedbacks.isEmpty()) {
                        saveFeedbacks(feedbacks);
                    }
                }
            } catch (Exception e) {
                LOG.error("Erro na análise de feedback em segundo plano", e);
            }
        });
    }

    @Transactional
    public void saveFeedbacks(List<Feedback> feedbacks) {
        for (Feedback f : feedbacks) {
            feedbackRepository.save(f);
        }
    }
}
