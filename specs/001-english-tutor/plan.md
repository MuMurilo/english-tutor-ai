# Plano de Implementação: Tutor de Inglês com IA

**Branch**: `001-english-tutor` | **Date**: 2026-06-18 | **Especificação**: [specs/001-english-tutor/spec.md](file:///D:/Users/muril/Estudos de IA/project-speckit/specs/001-english-tutor/spec.md)

**Entrada**: Especificação da funcionalidade de `specs/001-english-tutor/spec.md`

**Nota**: Este modelo foi elaborado e alinhado com o comando `/speckit-plan`.

## Resumo
A funcionalidade consiste em criar um aplicativo web monorepo composto de:
1.  **Backend (Java 23 + Quarkus 3)** que expõe APIs seguras para gerenciamento de usuários, mensagens de chat e armazenamento de feedbacks de erros e conquistas. O backend se integra de forma assíncrona ao Gemini da Google para gerar respostas dinâmicas e análises de progresso. A arquitetura segue estritamente os padrões DDD (Domain-Driven Design).
2.  **Frontend (Angular 22)** com uma interface web interativa e responsiva contendo telas de Login/Cadastro, Dashboard de progresso e Janela de Chat.

## Contexto Técnico

**Linguagem/Versão**: Java 23 (Backend) e TypeScript / HTML / CSS (Frontend)

**Dependências Principais**:
- **Backend (Quarkus)**: 
  * `quarkus-resteasy-reactive` (Endpoints REST reativos)
  * `quarkus-hibernate-orm-panache` (Acesso e persistência a dados usando o padrão Repository/Active Record do Panache no Hibernate)
  * `quarkus-jdbc-h2` (Banco de dados H2 embarcado)
  * `quarkus-rest-client-reactive` (Cliente HTTP reativo para comunicação com a API do Gemini)
  * `quarkus-smallrye-jwt` (Autenticação e geração de tokens JWT seguros)
- **Frontend (Angular)**: 
  * `@angular/common/http` (Consumo de APIs REST)
  * `@angular/router` (Navegação interna e guards de rotas protegidas)
  * `@angular/forms` (Formulários reativos para login/registro e entrada de chat)

**Armazenamento**: Banco de dados H2 gravado em arquivo local (`tutor-db.mv.db` na raiz de Estudos de IA).

**Testes**: 
- **Backend**: `quarkus-junit5`, `rest-assured` (para testes de endpoints e de integração) e `mockito-core` (para mockagem de serviços externos de IA).
- **Frontend**: `Jasmine` e `Karma` (testes unitários e de componentes nativos do Angular CLI).

**Plataforma Alvo**: Web Desktop e Mobile.

**Tipo de Projeto**: Web Application (Backend Web Service + Single Page Application Frontend).

**Metas de Desempenho**: Respostas do chat em < 4 segundos; Carregamento de dados de dashboard do H2 em < 1.5s.

**Restrições**: Sem conexões diretas a frameworks na camada de `domain` do Java; Logs injetados por interfaces abstratas na camada de negócio.

## Verificação da Constituição (Constitution Check)

*PORTÃO: PASSOU*

1.  **Regra de Domínio Isolado**: Respeitada. A pasta `domain/` conterá apenas classes Java puras, sem annotations do Quarkus/Panache/Hibernate.
2.  **Regra de TDD Rígido**: Respeitada. O fluxo de tarefas exige o desenvolvimento dos arquivos de testes (`*Test.java` e `*.spec.ts`) previamente e em estado de falha (Red).
3.  **Regra de Logs Abstratos**: Respeitada. Uma interface `LoggerService` será injetada no domínio e sua implementação residirá em `infrastructure/`.
4.  **Uso de Tecnologias**: Respeitado (Java 23, Quarkus, Angular, Maven, Jasmine/Karma e JUnit 5).

## Estrutura do Projeto

Para este MVP monorepo, criaremos duas pastas principais no diretório `project-speckit`:
*   `backend/` (Contém o projeto Maven/Quarkus)
*   `frontend/` (Contém o projeto Angular)

### Documentação (desta funcionalidade)

```text
specs/001-english-tutor/
├── plan.md              # Este arquivo (Plano de Implementação)
├── spec.md              # Especificação de Requisitos e Histórias de Usuário
└── tasks.md             # Lista de tarefas atômicas para execução (Fase 2)
```

### Código Fonte (raiz do repositório)

```text
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/english/tutor/
│   │   │   ├── domain/           # Entidades (User, ChatMessage, Feedback), Repositorios e Regras puras
│   │   │   ├── application/      # Casos de uso (Orquestração do Chat, Login, Análise de Feedbacks)
│   │   │   ├── infrastructure/   # Implementações JPA/Panache, Cliente HTTP do Gemini, LoggerServiceImpl
│   │   │   └── rest/             # Recursos JAX-RS / Endpoints do Quarkus (AuthResource, ChatResource, DashboardResource)
│   │   └── resources/
│   │       └── application.properties # Configuração H2 e Gemini API Keys
│   └── test/
│       └── java/com/english/tutor/
│           ├── domain/           # Testes unitários puros das entidades e lógica
│           └── rest/             # Testes de integração de endpoints utilizando REST-Assured
│
frontend/
├── package.json
├── src/
│   ├── index.html
│   ├── app/
│   │   ├── core/                 # Guards, interceptores HTTP e serviços de conexão (Auth, Chat)
│   │   ├── shared/               # Componentes comuns (cabeçalhos, alertas) e modelos de dados
│   │   ├── features/
│   │   │   ├── login/            # Tela de Login e Registro
│   │   │   ├── dashboard/        # Painel contendo cartões de erros e acertos
│   │   │   └── chat/             # Janela interativa do chat com o tutor
│   └── test.ts                   # Ponto de entrada de testes do Karma/Jasmine
```

**Decisão de Estrutura**: Estrutura Monorepo padrão com divisão rígida em 4 camadas de DDD no backend Java/Quarkus e divisão modular baseada em rotas (`features`) no frontend Angular.

## Controle de Complexidade (Complexity Tracking)

*Nenhuma violação aos limites de complexidade foi identificada.*
