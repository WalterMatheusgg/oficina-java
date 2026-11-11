<div align="center">

# Conta Clara
### Orçamento pessoal por voz, com confirmação antes de registrar

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

</div>

## O que é

Conta Clara é uma API de orçamento pessoal que recebe comandos de voz em português, transcreve o áudio, interpreta a intenção e consulta ou prepara movimentações financeiras. O projeto nasceu do desafio de Spring AI da DIO, mas ganhou um fluxo próprio: uma despesa sugerida pela IA fica como rascunho e só entra no histórico depois de confirmação explícita.

O objetivo foi tratar a IA como uma interface para casos de uso da aplicação. Ela não acessa o banco diretamente nem pode confirmar um lançamento por conta própria.

## Fluxo de voz

1. O cliente envia um áudio em MP3, M4A, WAV ou WebM.
2. O modelo de transcrição converte a fala em texto.
3. O Spring AI encaminha a intenção para ferramentas limitadas da aplicação.
4. A ferramenta consulta o resumo do mês ou cria um rascunho de despesa.
5. A pessoa revisa e confirma o rascunho por um endpoint REST.
6. A resposta pode ser sintetizada em áudio MP3.

As movimentações e os rascunhos ficam no PostgreSQL. A confirmação usa bloqueio da linha do rascunho e grava o lançamento e seu vínculo na mesma transação. Repetir a confirmação retorna a movimentação já criada, sem duplicá-la.

## O que a API faz

- Recebe áudio e devolve transcrição, resposta, código do rascunho e áudio opcional em Base64.
- Permite lançar receitas ou despesas manualmente como rascunho.
- Exige confirmação explícita antes de salvar um rascunho no histórico.
- Registra limites mensais por categoria.
- Calcula receitas, despesas, saldo e percentual do orçamento utilizado.
- Sinaliza categorias a partir de 80% do limite e quando ultrapassam 100%.
- Usa valores monetários em `BigDecimal`, com até duas casas decimais.

## Organização

- `domain`: movimentações, rascunhos, limites e contratos de repositório.
- `application`: casos de uso para confirmação, consulta e orçamento mensal.
- `infrastructure.persistence`: adaptadores JPA, PostgreSQL e Flyway.
- `api`: endpoints REST, validação de entrada e respostas de erro.
- `ai`: transcrição, ChatClient, ferramentas permitidas e síntese de voz.

## Executar localmente

Requisitos: JDK 21, Maven, Docker Compose e uma chave da API OpenAI.

Na pasta do projeto:

```bash
docker compose -f compose.yml up -d db
export OPENAI_API_KEY="sua-chave"
mvn spring-boot:run
```

A API inicia em `http://localhost:8081`. O PostgreSQL usa a porta `5433`; as tabelas são criadas pelo Flyway. As configurações locais podem ser substituídas por `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. O arquivo `.env.example` lista os nomes das variáveis; não coloque chaves reais no Git.

## Exemplos

Criar um rascunho manual:

```bash
curl -X POST http://localhost:8081/api/drafts \
  -H "Content-Type: application/json" \
  -d '{"description":"Compra no mercado","amount":82.50,"type":"EXPENSE","category":"FOOD"}'
```

A resposta inclui o identificador do rascunho. Confirme somente depois de revisar os dados:

```bash
curl -X POST http://localhost:8081/api/drafts/ID_DO_RASCUNHO/confirm
```

Consultar lançamentos do mês:

```bash
curl "http://localhost:8081/api/transactions?month=2026-11"
curl "http://localhost:8081/api/summary/2026-11"
```

Definir limite mensal para alimentação:

```bash
curl -X PUT http://localhost:8081/api/budgets/2026-11/FOOD \
  -H "Content-Type: application/json" \
  -d '{"limit":900.00}'
```

Enviar um áudio. A resposta contém o texto reconhecido, a resposta do assistente e, quando solicitado, o áudio em Base64:

```bash
curl -X POST "http://localhost:8081/api/assistant/voice?includeSpeech=false" \
  -F "file=@./meu-comando.m4a"
```

Exemplo de comando: “Anota uma despesa de 82 reais no mercado.” A API prepara um rascunho e retorna seu código; para concluir, o cliente chama o endpoint de confirmação.

## Testes

```bash
mvn verify
```

Os testes de integração usam PostgreSQL e cobrem confirmação idempotente, alertas de orçamento, validação de valores, consulta mensal e rejeição de arquivos fora dos formatos aceitos. O GitHub Actions sobe um PostgreSQL de teste e executa a suíte a cada alteração neste projeto.

## Decisões e limites

- A IA só recebe ferramentas para preparar despesas e consultar o resumo mensal. Nenhuma ferramenta confirma um lançamento.
- A transcrição e a geração de voz enviam dados ao provedor configurado. Não use dados financeiros reais ou identificáveis neste projeto de portfólio.
- Esta versão é um protótipo de estudo para uma única pessoa. Ainda não inclui autenticação, segregação entre usuários, exclusão de dados, limites operacionais do provedor ou fila durável.
- O endpoint de voz pode gerar custo de transcrição, modelo de chat e síntese. Use `includeSpeech=false` para não solicitar áudio de resposta.

## Contexto

Projeto de portfólio desenvolvido por Walter Matheus para o desafio **Desenvolvendo sua API Inteligente com Reconhecimento de Fala e Spring Boot**, da DIO, a partir do repositório de estudos de Spring da [Digital Innovation One](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/tree/main/05-spring-ai). A implementação acrescenta confirmação de rascunhos, orçamento por categoria e cobertura de testes.
