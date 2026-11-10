<div align="center">

# ☕ Projetos Java — Fundamentos, Desafios e APIs
### Java • Swing • Spring Boot • Design Patterns

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

</div>

## Sobre

Este repositório reúne meus exercícios de Java e projetos feitos enquanto estudo desenvolvimento back-end. A API Pulso é o projeto principal: nela, aplico padrões de projeto a operações bancárias e exploro um fluxo orientado a eventos.

## Projetos

| Projeto | O que você encontra |
|---|---|
| [Pulso Banking API](design-patterns-bank-api/) | API com depósitos, saques, histórico de operações, eventos Spring e padrões Strategy, Facade e Repository |
| [Desafio.java](Desafio.java) | Calculadora com menu Swing: temperatura, salários, área, volume, energia e combustível |
| [ListarArquivos.java](ListarArquivos.java) | Listagem de arquivos e ordenação por data de modificação |
| [Person.java](Person.java) | Coleta de alturas, contagens e médias usando JOptionPane |
| [jogoRio.java](jogoRio.java) | Desafio de lógica da travessia de lobo, ovelha e repolho |
| [Index.java](Index.java) | Exercício simples de repetição |

## Executar

Tenha um JDK instalado. Para executar um dos exercícios na raiz:

```bash
git clone https://github.com/WalterMatheusgg/oficina-java.git
cd oficina-java
javac jogoRio.java
java jogoRio
```

Para iniciar a API, instale também o Maven:

```bash
cd design-patterns-bank-api
mvn spring-boot:run
```

A API inicia em `http://localhost:8080`. O [README do Pulso](design-patterns-bank-api/README.md) tem exemplos de requisição, arquitetura e limites da implementação.

## Conceitos em prática

Estruturas condicionais, laços, métodos, classes, `Scanner`, `JOptionPane`, arquivos, APIs REST, padrões GoF, injeção de dependências e eventos do Spring.

## Próximas evoluções

- Persistir contas e movimentações em banco de dados.
- Adicionar testes automatizados para as regras e endpoints.
- Tratar entradas inválidas e cancelamento nos exercícios Swing.

## Autor

**Walter Matheus Soares da Silva** · [GitHub](https://github.com/WalterMatheusgg)  
Veja também o projeto [NEXUS CRM](https://github.com/WalterMatheusgg/NEXUS).
