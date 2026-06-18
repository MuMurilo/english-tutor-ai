# Arquitetura do Backend

## Visão Geral

O backend do **English Tutor AI** segue os princípios de **Arquitetura em Camadas com Isolamento de Domínio**. O objetivo é garantir que a lógica de negócio central seja independente de frameworks, bancos de dados e APIs externas.

---

## Camadas

```
rest/          ← Controladores HTTP (JAX-RS)
    ↓
application/   ← Casos de Uso (Serviços)
    ↓
domain/        ← Entidades, Lógica de Negócio, Interfaces de Repositório
    ↑
infrastructure/ ← Implementações Técnicas (JPA, REST Client, JWT)
```

### `domain/` — Núcleo da Aplicação

Contém apenas lógica de negócio pura. **Sem dependências de frameworks.**

| Arquivo | Responsabilidade |
|---------|-----------------|
| `User.java` | Entidade com validação de e-mail, senha (bcrypt) e nível de inglês |
| `ChatMessage.java` | Entidade representando uma mensagem (USER ou TUTOR) com timestamp |
| `Feedback.java` | Entidade representando um feedback linguístico (ERROR ou CONSOLIDATED) |
| `FeedbackParser.java` | Parseia o JSON retornado pelo Gemini em uma lista de objetos `Feedback` |
| `TutorPromptBuilder.java` | Constrói o system prompt personalizado do tutor baseado no nível do estudante e erros anteriores |
| `UserRepository.java` | Interface do repositório de usuários |
| `ChatMessageRepository.java` | Interface do repositório de mensagens |
| `FeedbackRepository.java` | Interface do repositório de feedbacks |

### `application/` — Casos de Uso

Orquestra o domínio com integrações externas. Coordena o fluxo de dados entre camadas.

| Arquivo | Responsabilidade |
|---------|-----------------|
| `AuthService.java` | Registro de usuários, hash de senha, login, geração e assinatura de JWT |
| `ChatService.java` | Recebe mensagem do usuário, salva no BD, chama Gemini, salva resposta, dispara feedback assíncrono |
| `FeedbackService.java` | Analisa a última mensagem do usuário de forma assíncrona via Gemini; retry com backoff exponencial |
| `DashboardService.java` | Gera o relatório pedagógico completo consultando todos os feedbacks acumulados |
| `DidacticReportDto.java` | DTO de transferência do relatório: summary, strengths, weaknesses, actionPlan |

### `infrastructure/` — Detalhes Técnicos

Implementa as interfaces do domínio e fornece adaptadores para sistemas externos.

| Arquivo/Pasta | Responsabilidade |
|--------------|-----------------|
| `GeminiClient.java` | Interface `@RegisterRestClient` para a API do Gemini (MicroProfile REST Client) |
| `GeminiRequest.java` | Monta o payload JSON da requisição (system instruction + conversation turns) |
| `GeminiResponse.java` | Desserializa a resposta do Gemini (candidates → content → parts → text) |
| `persistence/` | Implementações Panache dos repositórios do domínio |
| `security/` | Configuração JWT e filtros de segurança |

### `rest/` — Controladores HTTP

Controladores finos que apenas validam a entrada, extraem dados do JWT e delegam ao `application/`.

| Arquivo | Endpoint | Método |
|---------|----------|--------|
| `AuthResource.java` | `/api/auth/register` e `/api/auth/login` | POST |
| `ChatResource.java` | `/api/chat/history` e `/api/chat/send` | GET, POST |
| `DashboardResource.java` | `/api/dashboard/feedback` e `/api/dashboard/report` | GET |

---

## Fluxo de uma Mensagem de Chat

```
Frontend → POST /api/chat/send
              │
              ▼
         ChatResource.send()
              │ extrai userId e englishLevel do JWT
              ▼
         ChatService.sendMessage()
              ├─ salva USER message (H2)
              ├─ carrega últimas 20 msgs (contexto)
              ├─ TutorPromptBuilder.buildSystemPrompt()
              │     └─ personaliza por nível + erros anteriores
              ├─ GeminiClient.generateContent(modelName, apiKey, request)
              │     └─ POST https://generativelanguage.googleapis.com/...
              ├─ salva TUTOR message (H2)
              ├─ retorna TUTOR message → HTTP 200
              └─ [async] FeedbackService.analyzeFeedbackAsync()
                    ├─ últimas 4 mensagens
                    ├─ GeminiClient.generateContent() [com retry]
                    ├─ FeedbackParser.parse(rawJson)
                    └─ feedbackRepository.save(feedbacks)
```

---

## Segurança JWT

- **Algoritmo**: RS256 (RSA + SHA-256)
- **Emissor**: `https://tutor.english.com/issuer`
- **Expiração**: 24 horas
- **Claims customizados**: `userId` (Long), `englishLevel` (String), `upn` (e-mail)
- **Chave privada**: `privatekey.pem` — usada para assinar tokens (AuthService)
- **Chave pública**: `publickey.pem` — usada para verificar tokens (SmallRye JWT)

Todos os endpoints de `/api/chat/*` e `/api/dashboard/*` exigem role `USER` via `@RolesAllowed("USER")`.

---

## Banco de Dados

O banco H2 é usado como banco embarcado. O Hibernate gerencia o schema automaticamente (`database.generation=update`).

### Tabelas

**`users`**
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT (PK) | Auto-incremento |
| email | VARCHAR (UNIQUE) | E-mail do usuário |
| password | VARCHAR | Hash bcrypt |
| english_level | VARCHAR | BEGINNER / INTERMEDIATE / ADVANCED |

**`chat_messages`**
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT (PK) | Auto-incremento |
| user_id | BIGINT (FK) | Referência ao usuário |
| sender | VARCHAR | USER ou TUTOR |
| content | TEXT | Conteúdo da mensagem |
| timestamp | TIMESTAMP | Data e hora da mensagem |

**`feedbacks`**
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT (PK) | Auto-incremento |
| user_id | BIGINT (FK) | Referência ao usuário |
| type | VARCHAR | ERROR ou CONSOLIDATED |
| original_phrase | TEXT | Frase original do estudante |
| content | TEXT | Forma correta ou exemplo de uso |
| explanation | TEXT | Explicação didática em português |
| timestamp | TIMESTAMP | Quando o feedback foi gerado |

### Perfis de Banco

| Perfil | URL | Uso |
|--------|-----|-----|
| `dev` (padrão) | `jdbc:h2:file:./tutor-db` | Desenvolvimento local (dados persistem) |
| `test` | `jdbc:h2:mem:testdb` | Testes automatizados (isolado, descartado após testes) |
