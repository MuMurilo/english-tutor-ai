# 🤖 English Tutor AI

**Um tutor de inglês conversacional inteligente e adaptativo, alimentado pelo Google Gemini.**

O **English Tutor AI** é uma aplicação web full-stack que coloca o estudante em contato com um professor de inglês baseado em IA. Cada conversa é analisada em tempo real: erros gramaticais são registrados, conquistas de vocabulário são celebradas e um relatório pedagógico personalizado é gerado automaticamente conforme o estudante evolui.

---

## ✨ Funcionalidades

### 💬 Conversação Adaptativa
- Chat em tempo real com um tutor IA alimentado pelo **Google Gemini**.
- O tutor adapta vocabulário, complexidade de frases e estilo de ensino ao nível declarado do estudante: **Iniciante**, **Intermediário** ou **Avançado**.
- Se o estudante escrever em português (ou qualquer outro idioma), o tutor **gentilmente redireciona** para a prática em inglês.
- **Janela de contexto inteligente**: apenas as últimas 20 mensagens são enviadas ao modelo, mantendo o consumo de tokens baixo sem perder a continuidade da conversa.

### 📊 Dashboard Pedagógico
- **Relatório Didático com IA**: Um coordenador pedagógico alimentado pelo Gemini analisa os erros acumulados e as conquistas de vocabulário do estudante e produz um relatório estruturado com:
  - 📝 **Resumo geral** — uma visão encorajadora do progresso.
  - 🌟 **Pontos fortes** — o que o estudante já faz bem.
  - 🎯 **Pontos de melhoria** — aspectos gramaticais ou de vocabulário a praticar.
  - 📅 **Plano de estudos recomendado** — ações concretas e personalizadas.
- **Lista de erros**: Cada erro gramatical detectado é armazenado com a frase original, a forma correta e uma explicação didática em português.
- **Vocabulário consolidado**: Palavras e expressões usadas corretamente são celebradas e armazenadas.

### 🔒 Segurança
- Autenticação baseada em JWT (RS256 — par de chaves RSA).
- Todos os endpoints de chat e dashboard exigem um token bearer válido.
- Senhas são criptografadas com **bcrypt** antes do armazenamento.

### 🚀 Performance e Resiliência
- A análise de feedback é executada de forma **assíncrona** via `ManagedExecutor` — nunca bloqueia a resposta do tutor.
- Retry com backoff exponencial (até 3 tentativas, iniciando em 1,5 s) lida com erros `503` transitórios da API do Gemini de forma transparente.
- O modelo de IA e o tamanho da janela de contexto são **configuráveis** via `application.properties` — sem necessidade de recompilar.

### 🎨 Destaques de UX
- **Histórico paginado**: Apenas as últimas 20 mensagens são exibidas por padrão; um botão "Mostrar mensagens anteriores" carrega mensagens mais antigas em lotes de 20, sem perder a posição de rolagem.
- **Auto-scroll**: A tela de chat sempre rola automaticamente para a última mensagem ao enviar ou receber uma resposta, e ao carregar o histórico inicial.
- **Feedback instantâneo**: A mensagem do usuário aparece imediatamente na interface (atualização otimista), enquanto a resposta da IA carrega de forma assíncrona com uma animação de digitação.

---

## 🏗️ Arquitetura

```
project-speckit/
├── backend/       ← API REST em Quarkus (Java 21)
│   └── src/main/java/com/english/tutor/
│       ├── domain/           ← Entidades, Repositórios, Lógica de Domínio
│       ├── application/      ← Serviços de Casos de Uso
│       ├── infrastructure/   ← Cliente REST Gemini, Segurança
│       └── rest/             ← Controladores JAX-RS
└── frontend/      ← SPA Angular 22
    └── src/app/
        ├── core/services/    ← Serviços HTTP
        └── features/
            ├── login/        ← Página de Cadastro e Login
            ├── chat/         ← Página de Chat em Tempo Real
            └── dashboard/    ← Página de Dashboard de Progresso
```

### Camadas do Backend (Isolamento de Domínio)

| Camada | Responsabilidade |
|--------|-----------------|
| `domain` | Entidades puras (`User`, `ChatMessage`, `Feedback`), lógica de domínio (`FeedbackParser`, `TutorPromptBuilder`) e interfaces de repositório |
| `application` | Orquestra objetos de domínio e integrações externas (`ChatService`, `FeedbackService`, `DashboardService`, `AuthService`) |
| `infrastructure` | Detalhes técnicos: cliente REST para o Gemini (`GeminiClient`), implementações Panache JPA, segurança JWT |
| `rest` | Controladores JAX-RS finos — delega imediatamente aos serviços de aplicação |

---

## 🔗 Referência da API REST

URL Base: `http://localhost:8080`

### Autenticação — `/api/auth`

| Método | Caminho | Auth | Descrição |
|--------|---------|------|-----------|
| `POST` | `/api/auth/register` | Público | Cadastrar novo usuário |
| `POST` | `/api/auth/login` | Público | Autenticar e receber JWT |

**Corpo do cadastro:**
```json
{
  "email": "estudante@exemplo.com",
  "password": "senha123",
  "englishLevel": "BEGINNER"
}
```
`englishLevel` deve ser um dos valores: `BEGINNER` · `INTERMEDIATE` · `ADVANCED`

**Resposta do login:**
```json
{ "token": "<JWT>" }
```

---

### Chat — `/api/chat`

Todos os endpoints exigem `Authorization: Bearer <JWT>`.

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `GET` | `/api/chat/history` | Retorna o histórico completo de mensagens do usuário autenticado |
| `POST` | `/api/chat/send` | Envia uma mensagem e recebe a resposta do tutor |

**Corpo do envio:**
```json
{ "content": "Hello, can you help me practice?" }
```

**Resposta — um objeto `ChatMessage`:**
```json
{
  "id": 42,
  "userId": 7,
  "sender": "TUTOR",
  "content": "Of course! What would you like to talk about?",
  "timestamp": "2026-06-18T14:30:00"
}
```

---

### Dashboard — `/api/dashboard`

Todos os endpoints exigem `Authorization: Bearer <JWT>`.

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `GET` | `/api/dashboard/feedback` | Lista todos os feedbacks armazenados (erros + vocabulário) |
| `GET` | `/api/dashboard/report` | Gera e retorna o relatório pedagógico com IA |

**Resposta do relatório:**
```json
{
  "summary": "Você está progredindo muito bem! Continue praticando!",
  "strengths": ["Ótimo uso de saudações e expressões básicas"],
  "weaknesses": ["Atenção ao uso do auxiliar do/does em perguntas"],
  "actionPlan": "Pratique 10 minutos por dia focando em perguntas com 'do you'."
}
```

---

## 🪙 Uso de Tokens e Otimização de Custos

Um dos principais objetivos de design deste projeto é **minimizar o consumo de tokens da API do Gemini** sem comprometer a qualidade do ensino.

| Estratégia | Como funciona | Impacto |
|------------|--------------|---------|
| **Limite da janela de contexto** | Apenas as últimas 20 mensagens do banco são montadas no payload enviado ao Gemini por requisição de chat | ~60–80% menos tokens em sessões longas |
| **Análise de feedback assíncrona** | A chamada de feedback é disparada após a resposta do tutor ser retornada — nunca bloqueia o loop de eventos | Sem latência adicional |
| **Contexto focado para feedback** | Apenas as últimas 4 mensagens são enviadas ao analisador de feedback; o prompt instrui o modelo a analisar **apenas a última mensagem do USER** | ~75% menos tokens vs. histórico completo |
| **Modelo leve por padrão** | O app usa por padrão o `gemini-3.1-flash-lite` — um modelo pequeno, rápido e de baixo custo | ~5× mais barato vs. Pro |
| **Modelo configurável** | Troque o modelo alterando `gemini.model` em `application.properties` sem recompilar | Flexibilidade total |
| **Banco em memória para testes** | Testes usam `jdbc:h2:mem:testdb` — evita locks de arquivo e acelera o CI | Execução de testes mais rápida |

### Orçamento Estimado de Tokens por Troca de Mensagem

| Chamada | Tokens de entrada (aprox.) | Tokens de saída (aprox.) |
|---------|---------------------------|--------------------------|
| Resposta do tutor (contexto de 20 mensagens) | ~1.500 – 3.500 | ~150 – 400 |
| Análise de feedback (contexto de 4 mensagens) | ~600 – 900 | ~200 – 500 |
| Relatório do dashboard (todos os feedbacks) | ~800 – 2.000 | ~300 – 600 |

> Os valores variam conforme o tamanho da conversa, o nível de inglês e a quantidade de feedbacks acumulados.

### Modelos Disponíveis

| Modelo | Velocidade | Custo Relativo | Recomendado Para |
|--------|-----------|----------------|-----------------|
| `gemini-3.1-flash-lite` | ⚡ Muito rápido | 💰 Mais barato | Produção com alto volume (padrão) |
| `gemini-2.5-flash` | ⚡ Rápido | 💰💰 Moderado | Melhor qualidade de resposta |
| `gemini-2.5-pro` | 🐢 Mais lento | 💰💰💰 Caro | Máxima qualidade pedagógica |

---

## 🛠️ Stack Tecnológica

### Backend

| Tecnologia | Versão | Papel |
|------------|--------|-------|
| Java | 21 | Linguagem |
| Quarkus | 3.36.3 | Runtime e injeção de dependências |
| Hibernate ORM + Panache | via Quarkus BOM | Persistência JPA |
| H2 | 2.4.x | Banco de dados SQL embarcado |
| SmallRye JWT | via Quarkus BOM | Autenticação JWT RS256 |
| MicroProfile REST Client | via Quarkus BOM | Cliente HTTP tipado para a API do Gemini |
| Jackson | via Quarkus BOM | Serialização JSON |
| REST Assured | via Quarkus BOM | Asserções HTTP em testes de integração |
| JUnit 5 | via Quarkus BOM | Testes unitários e de integração |

### Frontend

| Tecnologia | Versão | Papel |
|------------|--------|-------|
| Angular | 22 | Framework SPA |
| TypeScript | ~6.0 | Linguagem |
| Detecção de Mudanças Zoneless | Angular 22 | Renderização assíncrona eficiente |
| Angular Reactive Forms | — | Gerenciamento de estado de formulários |
| Vitest | 4.x | Testes unitários |
| CSS Vanilla | — | Sistema de design dark glassmorphic |

### Externo

| Serviço | Propósito |
|---------|-----------|
| Google Gemini API | Modelo de linguagem IA (tutor + analisador de feedback + gerador de relatório) |

---

## 📋 Pré-requisitos

| Ferramenta | Versão mínima |
|------------|--------------|
| Java JDK | 21 |
| Maven | 3.9 (ou use `./mvnw`) |
| Node.js | 20 |
| npm | 10 |
| Chave de API do Google Gemini | Chave gratuita em [Google AI Studio](https://aistudio.google.com/app/apikey) |

---

## 🚀 Início Rápido

### 1. Clonar o repositório

```bash
git clone https://github.com/<seu-usuario>/english-tutor-ai.git
cd english-tutor-ai
```

### 2. Gerar o Par de Chaves JWT

O backend usa JWT assimétrico RS256. Gere o par de chaves uma única vez:

**Linux / macOS / Git Bash:**
```bash
openssl genrsa -out backend/src/main/resources/privatekey.pem 2048
openssl rsa -in backend/src/main/resources/privatekey.pem \
            -pubout -out backend/src/main/resources/publickey.pem
```

**Windows (PowerShell com OpenSSL instalado):**
```powershell
openssl genrsa -out backend\src\main\resources\privatekey.pem 2048
openssl rsa -in backend\src\main\resources\privatekey.pem `
            -pubout -out backend\src\main\resources\publickey.pem
```

> ⚠️ **Nunca faça commit desses arquivos `.pem`.** Eles já estão excluídos pelo `.gitignore`.

### 3. Configurar a Chave da API do Gemini

**Linux / macOS:**
```bash
export GEMINI_API_KEY="sua-chave-aqui"
```

**Windows — sessão atual:**
```powershell
$env:GEMINI_API_KEY = "sua-chave-aqui"
```

**Windows — persistir entre sessões:**
```powershell
[Environment]::SetEnvironmentVariable("GEMINI_API_KEY", "sua-chave-aqui", "User")
```

### 4. Iniciar o Backend

```bash
cd backend
./mvnw quarkus:dev
```

A API estará disponível em `http://localhost:8080`.  
O Quarkus Dev UI estará em `http://localhost:8080/q/dev`.

**Windows com JAVA_HOME personalizado:**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
$env:GEMINI_API_KEY = [Environment]::GetEnvironmentVariable("GEMINI_API_KEY", "User")
.\mvnw.cmd quarkus:dev
```

### 5. Iniciar o Frontend

```bash
cd frontend
npm install
npm start
```

Navegue para **http://localhost:4200**, cadastre-se, escolha seu nível de inglês e comece a praticar!

---

## ⚙️ Configuração

Todos os parâmetros ajustáveis estão em `backend/src/main/resources/application.properties`.

```properties
# Modelo de IA — altere sem precisar recompilar
# Opções: gemini-3.1-flash-lite, gemini-2.5-flash, gemini-2.5-pro, etc.
gemini.model=gemini-3.1-flash-lite

# Chave da API — lida de variável de ambiente (nunca coloque aqui diretamente)
gemini.api.key=${GEMINI_API_KEY:mock-key}

# Banco de dados
quarkus.datasource.jdbc.url=jdbc:h2:file:./tutor-db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
%test.quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1

# CORS — permite o servidor de desenvolvimento Angular
quarkus.http.cors.origins=http://localhost:4200
```

---

## 🧪 Executando os Testes

### Backend — 15 testes JUnit (unitários + integração)

```bash
cd backend

# Linux / macOS
./mvnw test

# Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
.\mvnw.cmd test
```

Saída esperada: `Tests run: 15, Failures: 0, Errors: 0, Skipped: 0  BUILD SUCCESS`

| Classe de Teste | Escopo | O que cobre |
|----------------|--------|-------------|
| `UserTest` | Unitário | Validação da entidade User (regras de senha, enum de nível) |
| `ChatTest` | Unitário | Entidade ChatMessage |
| `FeedbackParserTest` | Unitário | Parsing JSON → lista de Feedback |
| `GeminiClientTest` | Unitário | Declaração da interface do cliente REST |
| `AuthResourceTest` | Integração | Fluxo completo de cadastro + login |
| `DashboardResourceTest` | Integração | Endpoints do dashboard com JWT autenticado |
| `GreetingResourceTest` | Integração | Endpoint de health check |

### Frontend — 10 testes Vitest

```bash
cd frontend
npm test
```

Saída esperada: `Test Files 4 passed (4) · Tests 10 passed (10)`

---

## 📁 Estrutura do Projeto

```
project-speckit/
├── .gitignore
├── README.md
├── LICENSE
│
├── backend/
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/
│       │   ├── java/com/english/tutor/
│       │   │   ├── domain/
│       │   │   │   ├── User.java
│       │   │   │   ├── ChatMessage.java
│       │   │   │   ├── Feedback.java
│       │   │   │   ├── FeedbackParser.java        ← JSON do Gemini → lista de Feedback
│       │   │   │   ├── TutorPromptBuilder.java    ← Constrói prompts de sistema personalizados
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── ChatMessageRepository.java
│       │   │   │   └── FeedbackRepository.java
│       │   │   ├── application/
│       │   │   │   ├── AuthService.java           ← Cadastro, login, assinatura JWT
│       │   │   │   ├── ChatService.java           ← Orquestra Gemini + BD + feedback
│       │   │   │   ├── FeedbackService.java       ← Análise linguística assíncrona + retry
│       │   │   │   ├── DashboardService.java      ← Geração de relatório pedagógico com IA
│       │   │   │   └── DidacticReportDto.java     ← DTO do relatório do dashboard
│       │   │   ├── infrastructure/
│       │   │   │   ├── GeminiClient.java          ← Interface MicroProfile REST Client
│       │   │   │   ├── GeminiRequest.java         ← Construtor de payload de requisição
│       │   │   │   └── GeminiResponse.java        ← Desserializador de resposta
│       │   │   └── rest/
│       │   │       ├── AuthResource.java          ← POST /api/auth/register|login
│       │   │       ├── ChatResource.java          ← GET|POST /api/chat/history|send
│       │   │       └── DashboardResource.java     ← GET /api/dashboard/feedback|report
│       │   └── resources/
│       │       ├── application.properties
│       │       ├── privatekey.pem  (⚠️ não commitado — gerar localmente)
│       │       └── publickey.pem   (⚠️ não commitado — gerar localmente)
│       └── test/
│           └── java/com/english/tutor/
│               ├── domain/
│               ├── infrastructure/
│               └── rest/
│
└── frontend/
    ├── package.json
    ├── angular.json
    └── src/app/
        ├── app.routes.ts                      ← Rotas com lazy loading
        ├── core/services/
        │   ├── auth.service.ts
        │   ├── chat.service.ts
        │   └── dashboard.service.ts
        └── features/
            ├── login/                         ← UI de cadastro + login
            ├── chat/                          ← Interface de chat
            └── dashboard/                    ← Dashboard de progresso
```

---

## 🔄 Fluxo de Dados

```
Usuário digita mensagem no Angular
          │
          ▼
POST /api/chat/send  (autenticado via JWT)
          │
          ▼
ChatService.sendMessage()
  ├─ Salva mensagem USER no H2
  ├─ Carrega as últimas 20 mensagens (janela de contexto)
  ├─ Monta prompt de sistema personalizado (TutorPromptBuilder)
  ├─ Chama Gemini API → gera resposta do tutor
  ├─ Salva mensagem TUTOR no H2
  ├─ Retorna resposta para o frontend
  └─ [assíncrono] FeedbackService.analyzeFeedbackAsync()
                ├─ Últimas 4 mensagens
                ├─ Chama Gemini API → analisa última mensagem do USER
                ├─ Faz parse do JSON (FeedbackParser)
                └─ Salva registros de Feedback no H2

Usuário abre Dashboard → GET /api/dashboard/report
          │
          ▼
DashboardService.generateReport()
  ├─ Carrega todos os feedbacks do usuário
  ├─ Chama Gemini API → gera relatório pedagógico
  └─ Retorna DidacticReportDto para o frontend
```

---

## 🤝 Contribuindo

1. Faça um fork deste repositório.
2. Crie uma branch de feature: `git checkout -b feature/minha-nova-funcionalidade`
3. Faça suas alterações com testes — garanta que `mvnw test` e `npm test` passem.
4. Commit com mensagem convencional: `git commit -m "feat: adicionar minha funcionalidade"`
5. Push: `git push origin feature/minha-nova-funcionalidade`
6. Abra um Pull Request.

---

## 📜 Licença

Este projeto está licenciado sob a **Licença MIT** — veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 🙏 Agradecimentos

- [Google Gemini](https://deepmind.google/technologies/gemini/) — Modelo de linguagem IA que alimenta o tutor.
- [Quarkus](https://quarkus.io/) — Supersonic Subatomic Java.
- [Angular](https://angular.dev/) — O framework moderno para aplicações web.
