# Plano de Correções — Fase 1 (Urgente) & Fase 2 (Arquitetura)

Este plano divide os problemas críticos de segurança e de arquitetura listados no [PROJECT_ANALYSIS_REPORT.md](file:///D:/Users/muril/Estudos%20de%20IA/project-speckit/docs/PROJECT_ANALYSIS_REPORT.md) em tarefas atômicas e rastreáveis.

## Fase 1: Segurança (Urgente) - Status de Execução

- [x] **Task 1.1: Segurança da API Key do Gemini (S1 & S4)**
  - Mover o parâmetro de API Key de `@QueryParam("key")` para um cabeçalho HTTP (`x-goog-api-key`) no `GeminiClient.java`.
  - Configurar fail-fast na inicialização se `GEMINI_API_KEY` não estiver presente ou estiver configurada como `"mock-key"` no `application.properties`.
  
- [x] **Task 1.2: Limpeza e Segurança de Arquivos Sensíveis (S2 & S3)**
  - Verificar se `privatekey.pem` e `tutor-db.mv.db` (ou outros arquivos do H2) foram commitados no repositório.
  - Remover esses arquivos do histórico do Git e garantir que estejam no `.gitignore`. (Verificado: os arquivos sensíveis estão corretamente ignorados no `.gitignore` raiz e do backend, e não constam no histórico de commits do Git).
  
- [x] **Task 1.3: Validações de Entrada e Segurança de Senha no Backend (S5, S7 & S8)**
  - Implementar limite de tamanho de mensagem no chat e sanitização de caracteres em `ChatResource.java`.
  - Adicionar validação de tamanho máximo de senha em `User.java` para evitar truncamento silencioso do BCrypt.
  - Corrigir a expressão de validação de e-mail em `User.java` para evitar formatos inválidos.

- [ ] **Task 1.4: Segurança de Rotas e Navegação no Frontend (S14)**
  - Criar `authGuard` funcional no Angular.
  - Proteger as rotas `/chat` e `/dashboard` usando o `authGuard` em `app.routes.ts`.

- [ ] **Task 1.5: Interceptor de Autenticação e Tratamento de 401 no Frontend (S11 & S12)**
  - Criar um `authInterceptor` para anexar o token JWT automaticamente aos cabeçalhos de requisição de forma centralizada.
  - Remover lógica de `getHeaders()` duplicada em `chat.service.ts` e `dashboard.service.ts`.
  - Garantir que o `errorInterceptor` capture status `401`, execute logout do `AuthService` e redirecione o usuário para a tela de `/login`.

- [ ] **Task 1.6: Armazenamento Seguro, Decodificação e Validação do JWT (S9 & S10)**
  - Mover a decodificação de JWT e obtenção do `email` e `englishLevel` do usuário para o `AuthService` para eliminar a duplicação do `extractUserInfo()` em `chat.ts` e `dashboard.ts`.
  - Tornar a decodificação do JWT robusta, com verificações de integridade estrutural.

## Fase 2: Arquitetura & DDD (Organização e Desacoplamento)

- [ ] **Task 2.1: Desacoplamento do Domínio do Backend (A1 & A2)**
  - Mover a biblioteca Jackson (`ObjectMapper` e anotações `@JsonIgnoreProperties`) da camada de `domain` para as camadas adequadas (infraestrutura ou parser específico fora do domínio).
  - Criar uma interface/port (ex: `AIService`) no domínio e fazer com que a aplicação utilize a interface ao invés de injetar diretamente `GeminiClient` de infraestrutura.
  - Mover DTOs de domínio/application para a camada REST (`DidacticReportDto`).

- [ ] **Task 2.2: Isolamento da Camada REST e Remoção de Código Morto (A3 & A6)**
  - Modificar o `DashboardResource.java` para injetar o respectivo service e evitar chamadas diretas ao `FeedbackRepository`.
  - Remover o arquivo de boilerplate `GreetingResource.java` e seus testes correspondentes.

- [ ] **Task 2.3: Centralização de Configurações, Modelos e Componentes no Frontend (A7, A10, A11 & A13)**
  - Criar um arquivo de configuração centralizado para a API (ex: `environment.ts` ou injetar via provider).
  - Centralizar interfaces de modelo (`ChatMessage`, `Feedback`, `DidacticReport`) em uma pasta compartilhada `src/app/core/models/` ou similar.
  - Criar um `SidebarComponent` compartilhado em uma nova pasta `src/app/shared/components/` para eliminar a duplicação massiva de HTML e CSS da sidebar.
