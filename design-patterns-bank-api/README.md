# Design Patterns Bank API

API didática em Java 21 e Spring Boot que demonstra padrões de projeto GoF aplicados a operações bancárias.

## Padrões aplicados

| Padrão | Aplicação |
|---|---|
| Strategy | Depósito e saque usam estratégias diferentes por tipo de operação. |
| Facade | `TransactionFacade` coordena validação, busca da conta e execução da estratégia. |
| Repository | `AccountRepository` separa o serviço da forma de armazenamento. |
| Injeção de dependências | O Spring cria e conecta os componentes `@Service`, `@Repository` e `@Component`. |

O projeto usa o escopo singleton padrão dos beans do Spring. Não implementa um Singleton manual, porque o container já gerencia a instância dos componentes.

## Executar

Requisitos: JDK 21 e Maven.

```bash
mvn spring-boot:run
```

A API inicia em `http://localhost:8080`. A implementação usa um repositório em memória para manter o exemplo simples; os saldos voltam aos valores iniciais quando a aplicação reinicia.

## Endpoints

Consultar uma conta:

```http
GET /api/accounts/ACC-1001
```

Depositar:

```http
POST /api/accounts/ACC-1001/transactions
Content-Type: application/json

{
  "type": "DEPOSIT",
  "amount": 150.00
}
```

Sacar:

```http
POST /api/accounts/ACC-1001/transactions
Content-Type: application/json

{
  "type": "WITHDRAWAL",
  "amount": 50.00
}
```

Tipos aceitos: `DEPOSIT` e `WITHDRAWAL`. O sistema rejeita valores nulos, zero ou negativos, conta inexistente e saque acima do saldo.

## Estrutura

```
src/main/java/br/com/walter/designpatterns/
├── api/          # endpoints, DTOs e tratamento de erros
├── domain/       # conta e tipo de transação
├── repository/   # contrato e implementação em memória
├── service/      # fachada de transações
└── strategy/     # estratégias e resolução por tipo
```

## Referências

Projeto criado como entrega prática do desafio DIO **Design Patterns com Java: Dos Clássicos (GoF) ao Spring Framework**. Para estudo, compare as decisões com os exemplos de [Java Puro](https://github.com/digitalinnovationone/lab-padroes-projeto-java) e [Spring](https://github.com/digitalinnovationone/lab-padroes-projeto-spring).
