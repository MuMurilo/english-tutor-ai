# Plano de Correções — Fase 1: Segurança (Urgente)

Este plano divide os problemas críticos de segurança listados no [PROJECT_ANALYSIS_REPORT.md](file:///D:/Users/muril/Estudos%20de%20IA/project-speckit/docs/PROJECT_ANALYSIS_REPORT.md) em tarefas atômicas e rastreáveis.

## Tasks Mapeadas

- [x] **Task 1.1: Segurança da API Key do Gemini (S1 & S4)**
  - Mover o parâmetro de API Key de `@QueryParam("key")` para um cabeçalho HTTP (`x-goog-api-key`) no `GeminiClient.java`.
  - Configurar fail-fast na inicialização se `GEMINI_API_KEY` não estiver presente ou estiver configurada como `"mock-key"` no `application.properties`.
  
- [ ] **Task 1.2: Limpeza e Segurança de Arquivos Sensíveis (S2 & S3)**
  - Verificar se `privatekey.pem` e `tutor-db.mv.db` (ou outros arquivos do H2) foram commitados no repositório.
  - Remover esses arquivos do histórico do Git e garantir que estejam no `.gitignore`.
  
- [ ] **Task 1.3: Validações de Entrada e Segurança de Senha no Backend (S5, S7 & S8)**
  - Implementar limite de tamanho de mensagem no chat e sanitização de caracteres em `ChatResource.java`.
  - Adicionar validação de tamanho máximo de senha em `User.java` para evitar truncamento silencioso do BCrypt.
  - Corrigir a expressão de validação de e-mail em `User.java` para evitar formatos inválidos.

- [ ] **Task 1.4: Segurança de Rotas e Navegação no Frontend (S14)**
  - Criar Auth Guard no Angular.
  - Proteger as rotas `/chat` e `/dashboard` usando o Auth Guard em `app.routes.ts`.

- [ ] **Task 1.5: Interceptor de Autenticação e Tratamento de 401 no Frontend (S11 & S12)**
  - Criar um `AuthInterceptor` para anexar o token JWT automaticamente aos cabeçalhos de requisição de forma centralizada.
  - Remover lógica de `getHeaders()` duplicada em `chat.service.ts` e `dashboard.service.ts`.
  - Garantir que o `ErrorInterceptor` capture status `401` e redirecione o usuário para a tela de `/login`.

- [ ] **Task 1.6: Armazenamento Seguro e Validação do JWT (S9 & S10)**
  - Avaliar ou mitigar o armazenamento do JWT em `localStorage` (como usar cookies HttpOnly se o backend suportar, ou pelo menos implementar limpeza e tratamento robusto).
  - Implementar verificação segura e assinatura de token no decodificador de JWT no frontend.
