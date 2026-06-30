---
description: "Lista de tarefas atômicas para desenvolvimento do Tutor de Inglês"
---

# Tarefas: Tutor de Inglês com IA

**Entrada**: Documentos de especificação de `specs/001-english-tutor/spec.md` e `plan.md`

**Pré-requisitos**: plan.md (obrigatório), spec.md (obrigatório para histórias de usuário).

**Testes**: Os testes unitários e de integração são OBRIGATÓRIOS devido à regra de TDD Rígido da nossa Constituição.

**Organização**: Tarefas organizadas sequencialmente por fases para garantir o ciclo correto.

---

## Formato: `[ID] [P?] [Story] Descrição`
- **[P]**: Pode rodar em paralelo (arquivos ou pastas diferentes).
- **[Story]**: História de usuário associada (US1, US2, US3).

---

## Fase 1: Configuração Básica (Infraestrutura Compartilhada)

**Objetivo**: Inicialização física das pastas do projeto monorepo.

- [x] T001 Criar as pastas raiz `backend/` e `frontend/` na raiz do repositório `project-speckit/`.
- [x] T002 Inicializar o projeto Maven do Quarkus em `backend/` com as dependências básicas definidas no `plan.md`.
- [x] T003 Inicializar a aplicação Angular em `frontend/` utilizando o comando `ng new frontend --routing --style=css`.

---

## Fase 2: Fundacional (Pré-requisitos Bloqueantes)

**Objetivo**: Configurar segurança, banco de dados local H2 e estruturas base que sustentam a aplicação.

- [x] T004 Configurar conexão do H2 em arquivo e logs básicos no arquivo `backend/src/main/resources/application.properties`.
- [x] T005 Criar interfaces e repositórios base para as tabelas JPA no backend (sem implementar lógica).
- [x] T006 Configurar filtro de segurança JWT e criptografia de senhas no Quarkus para proteger endpoints.
- [x] T007 Configurar o roteamento padrão e o módulo HTTP Client no Angular em `frontend/src/app/core/`.

---

## Fase 3: História de Usuário 1 - Autenticação e Cadastro (P1) 🎯 MVP

**Objetivo**: Login, cadastro e escolha de nível de inglês funcionando de ponta a ponta.

### Testes da US1 (Escrever PRIMEIRO e garantir que FALHAM - Red) ⚠️

- [x] T008 [P] [US1] Criar teste unitário em JUnit 5 para as regras de validação de dados da entidade `User` (e-mail duplicado, formato de e-mail e nível de inglês válido) em `backend/src/test/java/.../domain/UserTest.java`.
- [x] T009 [P] [US1] Criar teste de integração JAX-RS com REST-Assured em `backend/src/test/java/.../rest/AuthResourceTest.java` para validar geração do token JWT.
- [x] T010 [P] [US1] Criar teste no Angular usando Vitest em `frontend/src/app/features/login/login.component.spec.ts` para verificar o formulário reativo.

### Implementação da US1 (Codificar para passar nos testes - Green)

- [x] T011 [US1] Criar a classe de domínio pura `User` em `backend/src/main/java/.../domain/User.java` e a interface de repositório correspondente.
- [x] T012 [US1] Criar a entidade JPA concreta e implementar a persistência de banco de dados e criptografia de senha em `backend/src/main/java/.../infrastructure/`.
- [x] T013 [US1] Criar a classe JAX-RS `AuthResource.java` em `backend/src/main/java/.../rest/` exposta nos caminhos `/api/auth/register` e `/api/auth/login`.
- [x] T014 [US1] Desenvolver o formulário de login, registro e seleção de nível de inglês (Iniciante, Intermediário, Avançado) em `frontend/src/app/features/login/`.

**Ponto de Controle**: A autenticação está funcional. É possível criar contas no H2 e logar.

---

## Fase 4: História de Usuário 2 - Conversação com o Tutor (P1) 🎯 MVP

**Objetivo**: Envio de mensagens de chat e geração de respostas adaptadas pelo Gemini.

### Testes da US2 (Escrever PRIMEIRO e garantir que FALHAM - Red) ⚠️

- [x] T015 [P] [US2] Criar teste unitário JUnit em `backend/src/test/.../domain/ChatTest.java` para validar a construção de prompts adaptados por nível do aluno.
- [x] T016 [P] [US2] Criar teste mockado em `backend/src/test/.../infrastructure/GeminiClientTest.java` para validar a desserialização das respostas do Gemini.
- [x] T017 [P] [US2] Criar teste do Angular em `frontend/src/app/features/chat/chat.spec.ts` para validar o fluxo de envio e listagem das mensagens do chat.

### Implementação da US2 (Codificar para passar nos testes - Green)

- [x] T018 [US2] Criar entidade de domínio `ChatMessage` no backend.
- [x] T019 [US2] Desenvolver o cliente HTTP reativo utilizando `quarkus-rest-client-reactive` em `infrastructure/` para se comunicar com a API do Gemini.
- [x] T020 [US2] Criar a classe JAX-RS `ChatResource.java` com métodos GET (histórico de chat do usuário) e POST (enviar mensagem e receber resposta do tutor).
- [x] T021 [US2] Criar a interface de chat (balões de mensagens dinâmicos e input de texto) em `frontend/src/app/features/chat/`.

**Ponto de Controle**: O chat está funcional. O aluno consegue conversar em inglês com a IA.

---

## Fase 5: História de Usuário 3 - Feedback de Progresso e Dashboard (P2)

**Objetivo**: Coleta em background de erros gramaticais e acertos, e exibição no Dashboard.

### Testes da US3 (Escrever PRIMEIRO e garantir que FALHAM - Red) ⚠️

- [x] T022 [P] [US3] Criar teste unitário JUnit no backend para validação do parse de JSON estruturado retornado pelo Gemini com a lista de erros e vocabulários.
- [x] T023 [P] [US3] Criar teste de integração com REST-Assured para rota GET `/api/dashboard/feedback`.

### Implementação da US3 (Codificar para passar nos testes - Green)

- [x] T024 [US3] Criar a entidade de domínio `Feedback` e repositório correspondente no backend.
- [x] T025 [US3] Implementar o serviço assíncrono em `application/` que faz uma chamada em segundo plano ao Gemini para obter a análise gramatical e vocabulário estruturado, persistindo no banco H2.
- [x] T026 [US3] Criar a classe JAX-RS `DashboardResource.java` expondo a rota `/api/dashboard/feedback`.
- [x] T027 [US3] Criar os cartões e tabelas de exibição de erros frequentes e acertos consolidados em `frontend/src/app/features/dashboard/`.

---

## Fase N: Polimento & Aspectos Transversais

**Objetivo**: Interligação de contextos e tratamento geral de exceções.

- [x] T028 Injetar a "memória de feedbacks de erros frequentes" do usuário no prompt inicial do chat para que o tutor foque naquelas dificuldades.
- [x] T029 Implementar no Angular um interceptador HTTP para tratar erros globais (como API do Gemini sem cota ou indisponível), exibindo uma mensagem didática ao aluno.
- [x] T030 Limpeza geral de logs simples no backend e validação de imports nas camadas DDD.

---

## Dependências e Ordem de Execução

1.  **Fase 1 (Setup)** é pré-requisito obrigatório de todo o projeto.
2.  **Fase 2 (Fundacional)** bloqueia o desenvolvimento das histórias de usuário.
3.  **História de Usuário 1 (Autenticação)** deve ser implementada antes do Chat e Dashboard para garantir o contexto do usuário logado.
4.  **História de Usuário 2 (Chat)** e **História de Usuário 3 (Dashboard)** podem prosseguir juntas após o login estar funcional, mas o feedback do Dashboard depende do histórico gerado no Chat.
