# 🤝 Guia de Contribuição

Obrigado por querer contribuir com o **English Tutor AI**! Siga este guia para garantir uma experiência tranquila.

---

## 🔧 Configuração do Ambiente

Consulte o [README.md](README.md) para o guia completo de instalação. Resumo:

1. Gere o par de chaves JWT RSA.
2. Configure `GEMINI_API_KEY` como variável de ambiente.
3. Inicie o backend com `./mvnw quarkus:dev`.
4. Inicie o frontend com `npm start`.

---

## 📋 Fluxo de Trabalho

1. **Crie uma issue** antes de começar qualquer trabalho de maior porte.
2. **Faça um fork** do repositório.
3. **Crie uma branch** a partir de `main`:
   ```bash
   git checkout -b feature/minha-funcionalidade
   # ou
   git checkout -b fix/descricao-do-bug
   ```
4. **Implemente** suas mudanças com testes.
5. **Garanta** que todos os testes passam:
   ```bash
   # Backend
   ./mvnw test

   # Frontend
   npm test
   ```
6. **Commit** seguindo o padrão Conventional Commits:
   ```
   feat: adicionar nova funcionalidade X
   fix: corrigir bug Y no componente Z
   docs: atualizar documentação da API
   test: adicionar testes para o serviço X
   refactor: melhorar estrutura do ChatService
   ```
7. **Abra um Pull Request** descrevendo as mudanças e referenciando a issue relacionada.

---

## 🧪 Padrões de Teste

### Backend (JUnit 5 + REST Assured)
- Testes unitários em `src/test/java/.../domain/` e `.../infrastructure/`.
- Testes de integração em `src/test/java/.../rest/` — usam banco H2 em memória (perfil `%test`).
- Nenhum teste deve chamar a API real do Gemini (use mocks).

### Frontend (Vitest + Angular Testing)
- Testes unitários para cada componente e serviço.
- Use classes mock para todos os services (ex: `MockChatService`, `MockDashboardService`).
- Certifique-se de que o mock inclui **todos** os métodos públicos do serviço real.

---

## 🎨 Padrões de Código

### Java (Backend)
- Siga a arquitetura em camadas: **domain → application → infrastructure/rest**.
- Camada `domain` não deve ter dependências de frameworks.
- Novos serviços vão em `application/`, clientes externos em `infrastructure/`.
- Use `@ApplicationScoped` para serviços, `@Transactional` em operações de escrita.
- Documente métodos públicos com Javadoc quando a lógica não for óbvia.

### TypeScript (Frontend)
- Use `inject()` em vez de injeção via construtor.
- Sempre chame `this.cdr.detectChanges()` após operações assíncronas (Zoneless).
- Nomes de arquivos em `kebab-case`; classes em `PascalCase`.
- Extraia lógica de negócio para services, nunca coloque regras nos componentes.

---

## 🔒 Segurança

- **Nunca** faça commit de arquivos `.pem`, `.env` ou chaves de API.
- O `.gitignore` já exclui esses arquivos — confirme antes de fazer push.
- Se suspeitar de uma chave exposta, revogue-a imediatamente no Google AI Studio.

---

## 📁 Arquivos que NÃO devem ser commitados

Os seguintes arquivos/diretórios estão excluídos pelo `.gitignore` e **nunca** devem ser adicionados manualmente:

- `backend/src/main/resources/privatekey.pem`
- `backend/src/main/resources/publickey.pem`
- `backend/*.mv.db` e `backend/*.trace.db`
- `frontend/node_modules/`
- `backend/target/`
- `frontend/dist/`
