# Relatório de Tokens e Modelo de IA

## Visão Geral

Este documento detalha as estratégias de otimização de tokens adotadas no **English Tutor AI** e explica como cada escolha de design impacta o custo e a qualidade das respostas da API do Google Gemini.

---

## Estratégias de Otimização Implementadas

### 1. Janela de Contexto de Chat (20 Mensagens)

**Arquivo:** [`backend/src/main/java/com/english/tutor/application/ChatService.java`](../backend/src/main/java/com/english/tutor/application/ChatService.java)

```java
int startIdx = Math.max(0, history.size() - 20);
List<ChatMessage> recentHistory = history.subList(startIdx, history.size());
```

**Por quê 20?**  
O Gemini mantém o contexto conversacional de forma eficaz com as últimas 20 trocas. Enviar o histórico completo (que pode crescer indefinidamente) causaria custos exponencialmente maiores sem ganho pedagógico proporcional. Uma sessão de 100 mensagens com contexto completo custaria ~5× mais do que com janela de 20 mensagens.

---

### 2. Contexto Focado para Feedback (4 Mensagens)

**Arquivo:** [`backend/src/main/java/com/english/tutor/application/ChatService.java`](../backend/src/main/java/com/english/tutor/application/ChatService.java)

```java
int fbStartIdx = Math.max(0, updatedHistory.size() - 4);
List<ChatMessage> feedbackHistory = updatedHistory.subList(fbStartIdx, updatedHistory.size());
feedbackService.analyzeFeedbackAsync(userId, feedbackHistory);
```

**Por quê apenas 4 mensagens para o feedback?**  
O analisador de feedback precisa apenas da **última mensagem do usuário** para identificar erros. As 3 mensagens anteriores servem apenas para fornecer contexto mínimo de qual pergunta o usuário estava respondendo. Enviar o histórico completo causaria:
1. Análise de mensagens antigas já processadas (duplicação de feedbacks).
2. Análise de frases do próprio tutor como se fossem do estudante.
3. Custos desnecessários de tokens.

---

### 3. Instrução Explícita no Prompt (Análise da Última Mensagem Apenas)

**Arquivo:** [`backend/src/main/java/com/english/tutor/application/FeedbackService.java`](../backend/src/main/java/com/english/tutor/application/FeedbackService.java)

```java
dialogueBuilder.append("\nAnalise APENAS a última fala dita pelo estudante (USER) no diálogo acima.");
```

```java
"ATENÇÃO CRÍTICA: Você deve analisar EXCLUSIVAMENTE a última frase dita pelo estudante (USER) no diálogo. " +
"Ignore completamente todas as frases ditas pelo Tutor (TUTOR) e as frases mais antigas do USER."
```

**Por que reforçar no prompt?**  
Mesmo com apenas 4 mensagens no contexto, sem instrução explícita o modelo poderia analisar frases do tutor ou trechos de mensagens anteriores. A instrução dupla (no contexto e no system prompt) garante que apenas a última contribuição do estudante seja avaliada.

---

### 4. Modelo Configurável via Propriedade

**Arquivo:** [`backend/src/main/resources/application.properties`](../backend/src/main/resources/application.properties)

```properties
gemini.model=gemini-3.1-flash-lite
```

**Injeção no código:**
```java
@ConfigProperty(name = "gemini.model", defaultValue = "gemini-3.5-flash")
String modelName;
```

**Por que isso é importante?**  
Permite trocar o modelo sem recompilar a aplicação. Em desenvolvimento local você pode usar modelos mais baratos; em produção com maior qualidade exigida, pode subir para `gemini-2.5-flash` ou `gemini-2.5-pro` apenas alterando o `application.properties`.

---

### 5. Feedback Assíncrono (Sem Impacto na Latência do Chat)

**Arquivo:** [`backend/src/main/java/com/english/tutor/application/FeedbackService.java`](../backend/src/main/java/com/english/tutor/application/FeedbackService.java)

```java
managedExecutor.runAsync(() -> {
    // chamada à API do Gemini para análise...
});
```

**Benefício de performance:**  
A chamada de análise de feedback ocorre em paralelo, após a resposta do tutor ter sido enviada ao usuário. O estudante recebe a resposta do tutor em ~1–3 segundos, enquanto o feedback é processado nos bastidores (2–5 segundos adicionais).

---

### 6. Retry com Backoff Exponencial

**Arquivo:** [`backend/src/main/java/com/english/tutor/application/FeedbackService.java`](../backend/src/main/java/com/english/tutor/application/FeedbackService.java)

```java
int retries = 3;
int delayMs = 1500;
for (int i = 0; i < retries; i++) {
    try {
        response = geminiClient.generateContent(modelName, apiKey, request);
        break;
    } catch (Exception ex) {
        // ...
        Thread.sleep(delayMs);
        delayMs *= 2; // 1.5s → 3s → 6s
    }
}
```

**Por que isso ajuda no consumo de tokens?**  
Erros `503` da API (comuns no tier gratuito sob alta carga) fazem a requisição falhar. Sem retry, o usuário perderia o feedback completamente e, em algumas implementações, poderia reenviar a mensagem (dobrando os tokens gastos). O retry automático garante que o feedback seja salvo sem interação adicional do usuário.

---

## Orçamento Estimado de Tokens

### Por Troca de Mensagem (Chat)

| Componente | Tokens de Entrada | Tokens de Saída | Frequência |
|-----------|-------------------|-----------------|------------|
| System prompt (TutorPromptBuilder) | ~200–400 | — | Cada mensagem |
| Contexto de 20 mensagens | ~800–2.800 | — | Cada mensagem |
| Resposta do tutor | — | ~150–400 | Cada mensagem |
| **Total por mensagem** | **~1.000–3.200** | **~150–400** | |

### Por Análise de Feedback (Assíncrono)

| Componente | Tokens de Entrada | Tokens de Saída | Frequência |
|-----------|-------------------|-----------------|------------|
| System prompt do analisador | ~250 | — | Cada mensagem do usuário |
| Contexto de 4 mensagens | ~200–600 | — | Cada mensagem do usuário |
| Resposta JSON com feedbacks | — | ~100–500 | Cada mensagem do usuário |
| **Total por feedback** | **~450–850** | **~100–500** | |

### Por Relatório do Dashboard

| Componente | Tokens de Entrada | Tokens de Saída | Frequência |
|-----------|-------------------|-----------------|------------|
| System prompt do coordenador | ~150 | — | Sob demanda |
| Lista completa de feedbacks | ~500–1.800 | — | Sob demanda |
| Relatório JSON | — | ~300–600 | Sob demanda |
| **Total por relatório** | **~650–1.950** | **~300–600** | |

---

## Comparativo de Modelos

| Modelo | Velocidade | Input (por 1M tokens) | Output (por 1M tokens) | Uso Recomendado |
|--------|-----------|----------------------|----------------------|----------------|
| `gemini-3.1-flash-lite` | ⚡ ~1s | ~$0.075 | ~$0.30 | Padrão — alto volume, baixo custo |
| `gemini-2.5-flash` | ⚡ ~1-2s | ~$0.15 | ~$0.60 | Melhor qualidade de ensino |
| `gemini-2.5-pro` | 🐢 ~3-5s | ~$1.25 | ~$10.00 | Máxima qualidade pedagógica |

> Preços aproximados baseados no tier pago. O tier gratuito tem limites de RPM e TPM.

---

## Configuração para Diferentes Cenários

### Desenvolvimento Local (padrão)
```properties
gemini.model=gemini-3.1-flash-lite
```

### Maior Qualidade de Ensino
```properties
gemini.model=gemini-2.5-flash
```

### Máxima Qualidade (produção premium)
```properties
gemini.model=gemini-2.5-pro
```

---

## Isolamento de Banco para Testes

**Arquivo:** [`backend/src/main/resources/application.properties`](../backend/src/main/resources/application.properties)

```properties
# Desenvolvimento — banco em arquivo (persiste dados entre reinicializações)
quarkus.datasource.jdbc.url=jdbc:h2:file:./tutor-db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE

# Testes — banco em memória (isolado, sem locks de arquivo)
%test.quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
```

**Por que isso é crítico?**  
Sem o perfil `%test`, os testes de integração tentariam abrir o mesmo arquivo de banco que o servidor de desenvolvimento já mantém com um lock. Isso causava falhas aleatórias na suite de testes. O banco em memória é criado e destruído a cada execução de testes, garantindo isolamento completo.
