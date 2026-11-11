<div align="center">

# Conta Clara
### Orçamento pessoal por voz, com confirmação antes de registrar

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

</div>

## O que o projeto faz

Conta Clara é uma API de orçamento pessoal que entende comandos de voz em português. Ela transcreve o áudio, interpreta o pedido e pode consultar o resumo mensal ou preparar um rascunho de despesa. A resposta pode ser devolvida em texto e áudio.

O projeto foi desenvolvido para o desafio **Desenvolvendo sua API Inteligente com Reconhecimento de Fala e Spring Boot**, da DIO. A implementação mantém a IA como uma interface para casos de uso: o modelo não acessa o banco diretamente.

## Melhoria implementada

Uma despesa solicitada por voz não é gravada imediatamente. A API cria um rascunho que a pessoa pode revisar e confirmar por outro endpoint. A confirmação é transacional e idempotente: se a requisição for repetida, o mesmo lançamento é retornado sem duplicar a despesa.

Também implementei limites mensais por categoria e um resumo que indica quando os gastos chegam a 80% ou ultrapassam o limite. Essas decisões deixam o fluxo mais seguro para dados financeiros e dão uma função prática ao orçamento.

## Fluxo principal

1. Enviar um áudio em MP3, M4A, WAV ou WebM.
2. Transcrever a fala para texto.
3. Interpretar a intenção com o Spring AI e chamar uma ferramenta limitada da aplicação.
4. Receber uma resposta e, quando for um pedido de despesa, o código do rascunho.
5. Revisar e confirmar o rascunho.
6. Consultar a movimentação e o resumo do mês.

## Tecnologias

- Java 21 e Spring Boot 3.5
- Spring AI: ChatClient, tool calling, transcrição e síntese de voz
- Spring Web, Bean Validation e Spring Data JPA
- PostgreSQL 16 e Flyway
- Maven, Docker Compose e JUnit

## Executar localmente

Requisitos: JDK 21, Maven, Docker Compose e uma chave da API OpenAI. Na pasta `conta-clara-api`:

```bash
docker compose -f compose.yml up -d db
export OPENAI_API_KEY="sua-chave"
mvn spring-boot:run
```

A API inicia em `http://localhost:8081`. O banco local usa a porta `5433`; o Flyway cria as tabelas na primeira inicialização. Para configurar outra conexão, use `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. O arquivo `.env.example` mostra os nomes das variáveis. Não coloque uma chave real no Git.

## Testar o fluxo principal

Com a aplicação e o PostgreSQL em execução, envie um áudio com um comando claro, como “Anota uma despesa de 82 reais no mercado”:

```bash
curl -X POST "http://localhost:8081/api/assistant/voice?includeSpeech=false" \
  -F "file=@./meu-comando.m4a"
```

A resposta inclui a transcrição, a mensagem do assistente e o campo `draftId`. Revise o conteúdo e confirme usando o identificador retornado:

```bash
curl -X POST http://localhost:8081/api/drafts/ID_DO_RASCUNHO/confirm
```

Depois consulte as movimentações e o resumo do mês. Troque `2026-11` pelo mês desejado:

```bash
curl "http://localhost:8081/api/transactions?month=2026-11"
curl "http://localhost:8081/api/summary/2026-11"
```

Para incluir a resposta falada em MP3, remova `includeSpeech=false`. O JSON retornará o áudio em Base64.

Também é possível preparar um rascunho sem usar IA:

```bash
curl -X POST http://localhost:8081/api/drafts \
  -H "Content-Type: application/json" \
  -d '{"description":"Compra no mercado","amount":82.50,"type":"EXPENSE","category":"FOOD"}'
```

## Testes automatizados

Com PostgreSQL e `OPENAI_API_KEY` configurados:

```bash
mvn verify
```

A suíte contém cinco testes de integração com PostgreSQL. Ela cobre confirmação sem duplicidade, alerta de orçamento, validação de valores, consulta mensal e rejeição de formatos de áudio não aceitos. O GitHub Actions sobe um banco de teste e executa `mvn verify). Os testes não chamam os serviços pagos da OpenAI.

## O que aprendi

Durante o desafio, pratiquei como integrar o Spring AI a casos de uso existentes sem dar ao modelo acesso direto à persistência. Também aprofundei o uso de tool calling, transcrição e síntese de voz, além de aplicar confirmação explícita, transações e idempotência em um fluxo que altera dados financeiros.

## Limites do projeto

Esta é uma API de estudo e portfólio para uso individual. Ainda não possui autenticação, separação de dados por usuário ou fila durável. O áudio é enviado ao provedor configurado para transcrição e síntese, e as chamadas podem ter custo. Não envie dados financeiros reais ou identificáveis.

## Contexto

Projeto de portfólio desenvolvido por Walter Matheus com base no desafio da DIO e no [repositório de estudos de Spring da Digital Innovation One](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/tree/main/05-spring-ai). O fluxo de confirmação, os limites por categoria e os testes foram implementados como evoluções do projeto.
