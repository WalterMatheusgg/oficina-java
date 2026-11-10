<div align="center">

# ☕ Projetos Java — Fundamentos, Desafios e Design Patterns
### Lógica • Java • Swing • Spring Boot • Padrões de Projeto

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

</div>

## 🎯 Sobre

Repositório educacional com exercícios independentes em Java e um projeto de API Spring Boot que demonstra padrões de projeto aplicados a operações bancárias.

## 📚 Projetos

| Projeto | Conteúdo |
|---|---|
| [Design Patterns Bank API](design-patterns-bank-api/) | API bancária com Strategy, Facade, Repository e injeção de dependências do Spring |
| [Desafio.java](Desafio.java) | Calculadora com menu Swing: temperatura, salários, área, volume, energia e combustível |
| [ListarArquivos.java](ListarArquivos.java) | Listagem de arquivos e ordenação por data de modificação |
| [Person.java](Person.java) | Coleta de alturas, contagens e médias usando JOptionPane |
| [jogoRio.java](jogoRio.java) | Desafio de lógica da travessia de lobo, ovelha e repolho |
| [Index.java](Index.java) | Exercício simples de repetição |

## ⚙️ Como executar

Tenha um JDK instalado. Para executar os exercícios na raiz:

```bash
git clone https://github.com/WalterMatheusgg/oficina-java.git
cd oficina-java
javac jogoRio.java
java jogoRio
```

Para iniciar a API Spring Boot, instale também o Maven:

```bash
cd design-patterns-bank-api
mvn spring-boot:run
```

A API disponibiliza exemplos e instruções de uso no [README do projeto](design-patterns-bank-api/README.md). A implementação de contas é em memória e reinicia com a aplicação.

## 🧠 Conceitos exercitados

Estruturas `if/else` e `switch`, laços `for`/`while`, métodos, classes, `Scanner`, `JOptionPane`, arquivos, API REST, padrões GoF e injeção de dependências.

## 🔧 Pontos para evolução

- Tratar cancelamento e entradas numéricas inválidas nos exercícios Swing.
- Evitar caminhos fixos de máquina.
- Criar testes automatizados para os projetos.

## 👨‍💻 Autor

**Walter Matheus Soares da Silva** · [GitHub](https://github.com/WalterMatheusgg)  
Veja também o projeto empresarial [**NEXUS CRM**](https://github.com/WalterMatheusgg/NEXUS).
