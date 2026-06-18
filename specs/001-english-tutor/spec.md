# Especificação de Funcionalidade: Tutor de Inglês com IA

**Branch da Funcionalidade**: `001-english-tutor`

**Criado em**: 2026-06-18

**Status**: Rascunho

**Entrada**: Descrição do usuário: "Aplicativo de tutor de inglês usando IA (API do Gemini), com autenticação/login de usuário, banco H2 local, dashboard com histórico de erros/acertos e seleção de nível de inglês no cadastro."

## Cenários de Usuário & Testes *(obrigatório)*

<!--
  IMPORTANTE: As histórias de usuário foram priorizadas e pensadas como jornadas independentes e testáveis.
  Se implementarmos apenas a US1, já temos um MVP básico de autenticação funcional.
-->

### História de Usuário 1 - Autenticação e Cadastro com Nível de Inglês (Prioridade: P1)

Como novo estudante de inglês, quero me cadastrar no sistema informando e-mail, senha e meu nível atual de inglês (Iniciante, Intermediário ou Avançado), e posteriormente fazer login de forma segura, para que minha jornada e históricos fiquem salvos.

**Por que esta prioridade**: É a base do sistema. Sem as credenciais do usuário, não é possível rastrear sessões ou salvar feedbacks customizados.

**Teste Independente**: Acessar a tela de registro via navegador, cadastrar um usuário, ser redirecionado para a tela de login, informar as credenciais criadas e conseguir logar com sucesso, obtendo um token de autenticação.

**Cenários de Aceitação**:

1. **Dado** um usuário não cadastrado na página de registro, **Quando** ele preenche o formulário com um e-mail válido, senha forte e seleciona o nível "Iniciante", **Então** a conta é criada no banco de dados H2 e ele é redirecionado para o login.
2. **Dado** um usuário com cadastro ativo, **Quando** ele informa e-mail e senha corretos na página de login, **Então** o sistema gera uma sessão segura (JWT) e o direciona para a tela de Dashboard.
3. **Dado** um usuário tentando acessar a página de registro, **Quando** ele informa um e-mail que já existe no sistema, **Então** o sistema bloqueia o envio e exibe um alerta explicativo.

---

### História de Usuário 2 - Conversação Dinâmica com o Tutor (Prioridade: P1)

Como estudante logado, quero ter uma tela de chat para conversar em inglês com o tutor de IA, recebendo respostas adaptadas ao meu nível de inglês selecionado.

**Por que esta prioridade**: É a funcionalidade central do produto (o chat de aprendizado).

**Teste Independente**: Estar logado no sistema, abrir a tela de chat, digitar uma mensagem em inglês e receber uma resposta condizente com o nível de inglês cadastrado para o perfil do usuário.

**Cenários de Aceitação**:

1. **Dado** um usuário logado com nível "Iniciante", **Quando** ele envia "Hello, my name is John" na caixa de diálogo, **Depois** o tutor responde com uma linguagem simplificada e de fácil compreensão em inglês.
2. **Dado** um usuário logado com nível "Avançado", **Quando** ele envia uma mensagem sobre geopolítica ou negócios, **Depois** o tutor responde utilizando termos e estruturas gramaticais mais sofisticadas em inglês.
3. **Dado** que a API do Gemini esteja fora do ar ou sem cota, **Quando** o usuário envia uma mensagem, **Então** o sistema exibe uma mensagem amigável informando o erro de conexão e salvando a mensagem para reenvio.

---

### História de Usuário 3 - Feedback de Progresso e Dashboard (Prioridade: P2)

Como estudante de inglês, quero acessar uma tela de painel (Dashboard) que exiba cartões dinâmicos com meus erros gramaticais frequentes ("Pontos a Melhorar") e novos vocabulários dominados ("Conhecimentos Consolidados").

**Por que esta prioridade**: Garante a retenção do aprendizado ao dar visibilidade para o progresso do aluno.

**Teste Independente**: Após finalizar um chat com diálogos e cometer erros de propósito, recarregar a tela do Dashboard e visualizar os cartões atualizados com a análise dos erros gramaticais cometidos.

**Cenários de Aceitação**:

1. **Dado** um histórico de chat finalizado ou uma mensagem enviada, **Quando** o backend executa a análise em segundo plano enviando a conversa ao Gemini, **Então** os erros de gramática são extraídos e salvos na tabela de feedbacks.
2. **Dado** um usuário acessando o Dashboard, **Quando** a página carrega, **Então** o sistema busca no banco H2 e exibe a lista de feedbacks categorizados em duas colunas: "Pontos a Melhorar" (erros comuns) e "Conhecimentos Consolidados" (vocabulário dominado).

---

### Casos de Borda (Edge Cases)

- O que acontece se o usuário submeter mensagens mistas em português e inglês no chat?
  *O tutor deve responder gentilmente em inglês incentivando o uso da língua estudada, fornecendo a tradução do que o usuário enviou.*
- Como lidar com erros de conexão de rede durante a análise de feedback em background?
  *O backend Quarkus deve colocar a análise de feedback em uma fila de retentativas automáticas, para evitar perda dos dados do usuário.*

## Requisitos *(obrigatório)*

### Requisitos Funcionais

- **RF-001**: O sistema DEVE permitir a criação de novas contas com e-mail, senha e nível de inglês (Iniciante, Intermediário, Avançado).
- **RF-002**: O sistema DEVE autenticar o usuário através de tokens de sessão seguros (JWT).
- **RF-003**: O sistema DEVE persistir o histórico de conversas e os feedbacks de progresso de forma permanente no banco de dados H2.
- **RF-004**: O sistema DEVE integrar-se com a API do Gemini (Google AI Studio) de forma assíncrona/não bloqueante no backend.
- **RF-005**: O sistema DEVE obter uma análise da conversa de forma estruturada gerando feedbacks sobre erros gramaticais e acertos de vocabulário.
- **RF-006**: O sistema DEVE injetar a "memória de feedbacks passados" do usuário no prompt de sistema do tutor ao iniciar uma nova sessão de chat.
- **RF-007**: O sistema DEVE exibir as telas de Login, Cadastro, Dashboard de Progresso e Chat em uma aplicação web Angular de forma responsiva.

### Entidades Chave

- **User**: Representa o estudante (id, e-mail, senha criptografada, nível de inglês, data de cadastro).
- **ChatMessage**: Representa cada mensagem individual no chat (id, user_id, sender [USER/TUTOR], content, timestamp).
- **Feedback**: Representa a análise gerada pelo avaliador (id, user_id, type [ERROR/CONSOLIDATED], content, original_phrase, explanation, timestamp).

## Critérios de Sucesso *(obrigatório)*

### Resultados Mensuráveis

- **CS-001**: O tempo de resposta da API do tutor de chat no Angular não deve ultrapassar 4 segundos por mensagem.
- **CS-002**: O Dashboard deve carregar e renderizar os dados do banco H2 em menos de 1.5 segundos.
- **CS-003**: A taxa de assertividade da categorização de erros gramaticais extraídos pelo modelo deve ser alta (uso adequado de prompts estruturados em JSON).

## Premissas (Assumptions)

- O backend se conectará à API do Gemini utilizando uma chave de API (`GEMINI_API_KEY`) fornecida via variáveis de ambiente.
- O banco de dados H2 rodará em modo de arquivo local na pasta de Estudos de IA para evitar perda de dados se o servidor Quarkus for reiniciado.
- O foco inicial do MVP é em navegadores desktop e mobile modernos (Chrome, Firefox, Safari).
