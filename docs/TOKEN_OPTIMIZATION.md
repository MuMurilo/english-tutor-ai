# Relatório de Consumo de Tokens na Elaboração do Projeto

> **Contexto:** Este documento registra o consumo de tokens do **agente de IA Antigravity** durante a sessão de desenvolvimento assistido por IA que construiu este projeto — não o consumo de tokens do aplicativo English Tutor AI em si.

---

## O que é este relatório?

Durante o desenvolvimento do **English Tutor AI**, utilizamos o **Antigravity** (assistente de codificação IA da Google DeepMind) para projetar a arquitetura, escrever o código, configurar o ambiente e garantir a qualidade por meio de TDD rigoroso. Esse processo tem um custo: o agente consome tokens a cada interação para raciocinar, ler arquivos, gerar código e executar comandos.

Este relatório documenta esse custo de desenvolvimento para fins de transparência e aprendizado.

---

## 1. Modelos Utilizados no Desenvolvimento

Durante a sessão de desenvolvimento com o **Antigravity**, o sistema alterneu entre os seguintes modelos:

| Modelo | Papel na Sessão |
|--------|----------------|
| **Gemini Pro / Claude Sonnet (Thinking)** | Agente principal — raciocínio arquitetural, planejamento DDD, TDD, decisões de segurança JWT, análise de bugs complexos |
| **Gemini Flash** | Subagente de pesquisa — leituras rápidas de arquivos, buscas na web, tarefas com contexto reduzido |

---

## 2. Consumo Estimado de Tokens do Agente de Desenvolvimento

O agente reenvia todo o histórico a cada turno (contexto expansivo), o que acumula rapidamente.

| Tipo | Volume Estimado | Descrição |
|------|----------------|-----------|
| **Entrada (Input)** | ~300.000 tokens | System prompt (~15k/turno) + arquivos Java e Angular analisados + histórico acumulado da conversa + logs de execução de comandos |
| **Saída (Output)** | ~12.000 tokens | Código gerado, explicações em markdown, planos arquiteturais, argumentos de chamadas de ferramentas |
| **Total** | **~312.000 tokens** | Consumo total acumulado na sessão de desenvolvimento |

> **Nota:** A cada nova interação, todo o histórico anterior é reenviado ao modelo para garantir que o agente não "esqueça" as decisões de design anteriores. Isso é inerente ao funcionamento de LLMs com contexto longo.

---

## 3. Comparativo: Modelo Pro vs. Flash no Desenvolvimento

### Pode-se usar um modelo gratuito para desenvolver com o Antigravity?

**Sim.** Modelos como o **Gemini Flash** possuem cotas gratuitas generosas e suportam chamadas de ferramentas. Porém existem trade-offs:

| Métrica | Modelo Pro (usado) | Modelo Flash (gratuito) | Impacto |
|---------|-------------------|------------------------|---------|
| **Tempo total de desenvolvimento** | ~15 min | ~35 min (estimado) | O Flash exige mais ciclos de depuração |
| **Velocidade de resposta** | ~10–15 s/turno | ~3–5 s/turno | Flash é mais rápido, mas erra mais em lógicas complexas |
| **Turnos necessários** | ~10 turnos | ~22 turnos (estimado) | Mais tentativas para acertar mapeamento JWT, configurações de compilador, etc. |
| **Limitação de cota** | Sem limite (premium) | Rate-limit da API gratuita | Pausas forçadas para aguardar RPM expirar |

### Conclusão

O modelo **Pro** é recomendado para tarefas estruturais complexas (DDD, TDD, segurança JWT, configuração de ambiente). O **Flash gratuito** é excelente para prototipações rápidas, funcionalidades isoladas simples e refatorações de interface, onde a velocidade compensa a menor precisão cognitiva.

---

## 4. Separação de Contextos

É importante **não confundir** este relatório com o funcionamento do tutor em produção:

| Contexto | Quem consome tokens? | Para quê? |
|----------|---------------------|-----------|
| **Desenvolvimento** (este doc) | Agente Antigravity | Escrever código, planejar arquitetura, corrigir bugs, gerar documentação |
| **Produção (runtime do tutor)** | A aplicação English Tutor AI | Chamar a API Gemini para responder o estudante e analisar erros de linguagem |

As decisões de janela de contexto do código (`últimas 20 mensagens`, `últimas 4 para feedback`) são escolhas **arquiteturais** de qualidade e performance do tutor em produção — não são relacionadas ao custo de elaboração do projeto.
