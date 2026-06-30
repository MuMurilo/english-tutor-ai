<!-- SPECKIT START -->
Para obter contexto adicional sobre as tecnologias a serem usadas, estrutura do projeto,
comandos de shell e outras informações importantes, leia o plano atual.
<!-- SPECKIT END -->

# Diretrizes de Entregáveis e Padrão de Comportamento

Você deve sempre seguir este procedimento estruturado para todas as tarefas e implementações no projeto:

## 1. Planejamento e Definição de Tasks
- Antes de iniciar qualquer alteração, mapeie as tarefas necessárias em passos atômicos, claros e bem definidos.
- Sempre que pertinente, crie ou atualize o arquivo de tarefas (`tasks.md`) ou registre explicitamente no início do processo o plano de ação detalhado.

## 2. Ciclo de Desenvolvimento com Commits Incrementais
- Faça commits no Git a cada alteração relevante ou após a conclusão de cada task lógica.
- Use mensagens de commit claras e semânticas (padrão Conventional Commits, ex: `feat: ...`, `fix: ...`, `refactor: ...`, `test: ...`).
- Evite acumular múltiplas tarefas em um único commit gigante.

## 3. Garantias de Execução (Definição de Pronto)
- Toda task concluída precisa de garantias de que foi executada com sucesso.
- **Escrita de Testes Dedicados:** Qualquer alteração de lógica de código ou comportamento deve ser acompanhada de testes unitários ou de integração que simulem e validem especificamente o novo comportamento introduzido (cobrindo tanto cenários de sucesso quanto cenários de falha). Não confie em testes legados gerais se a lógica interna mudou; estenda a cobertura de testes para testar a nova lógica de forma explícita.
- Valide o código executando testes automatizados, validando a compilação/lint, rodando o servidor de desenvolvimento, ou criando scripts temporários de teste.
- Apresente os resultados dessa validação (logs de sucesso, outputs de teste) como evidência da conclusão da task.

## 4. Apresentação e Links de Entregáveis
- Ao reportar a conclusão do trabalho, liste claramente todos os entregáveis produzidos.
- Crie links markdown absolutos usando o protocolo `file:///` para todos os arquivos modificados ou criados (ex: [tasks.md](file:///D:/Users/muril/Estudos%20de%20IA/project-speckit/tasks.md)), sem envolver o link em crases (backticks).
- Exiba um resumo ou log dos commits Git realizados.
