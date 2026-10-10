# TDD Triagem Hospitalar

Sistema de gerenciamento do fluxo de triagem e atendimento hospitalar desenvolvido com Spring Boot, aplicando Domain-Driven Design (DDD) e Test-Driven Development (TDD).

## Tecnologias

- Java 21
- Spring Boot 3.4
- SQLite via JDBC nativo
- JUnit 5, AssertJ e Mockito

## Arquitetura e Decisões de Design

- **Dominio Rico**: Entidades (`Atendimento`), Value Objects (`Cpf`, `MedicaoSinaisVitais`) e Enums de estado sem dependencias de frameworks.
- **Isolamento**: Ausencia de JPA/Hibernate e Lombok nas classes de negocio e aplicacao.
- **Persistencia**: Interface de repositorio na camada de aplicacao implementada com JDBC e SQLite.

## Execucao dos Testes

O projeto utiliza tags JUnit (`UnitTest`, `Functional`, `TDD`) organizadas em suites:

- **TddTests**: Testes desenvolvidos durante os ciclos de TDD.
- **FunctionalTests**: Testes baseados em tecnicas funcionais (particionamento de equivalencia e valores-limite).
- **UnitTests**: Execucao conjunta de todos os testes unitarios do sistema.

Para executar todos os testes via Maven:

```bash
./mvnw test
```

## Endpoints da API REST

A API expoe o ciclo de vida do atendimento em `/api/v1/atendimentos`:

- `POST /api/v1/atendimentos`: Abertura de atendimento com CPF.
- `POST /api/v1/atendimentos/{id}/triagem`: Registro de triagem inicial com sinais vitais e classificacao de risco.
- `PATCH /api/v1/atendimentos/{id}/consulta`: Inicio da consulta medica.
- `POST /api/v1/atendimentos/{id}/medicoes`: Reavaliacao e adicao de medicoes de sinais vitais.
- `PATCH /api/v1/atendimentos/{id}/classificacao-risco`: Reclassificacao de risco com justificativa.
- `PATCH /api/v1/atendimentos/{id}/prescricao`: Registro de conduta clinica e prescricao.
- `PATCH /api/v1/atendimentos/{id}/finalizacao`: Conclusao do atendimento apos a consulta.
- `PATCH /api/v1/atendimentos/{id}/cancelamento`: Cancelamento de atendimento com justificativa.
