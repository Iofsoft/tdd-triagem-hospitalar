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

- `GET /api/v1/atendimentos`: Lista todos os atendimentos.
- `GET /api/v1/atendimentos/{id}`: Busca um atendimento pelo id.
- `POST /api/v1/atendimentos`: Abertura de atendimento com CPF.
- `POST /api/v1/atendimentos/{id}/triagem`: Registro de triagem inicial com sinais vitais e classificacao de risco.
- `PATCH /api/v1/atendimentos/{id}/consulta`: Inicio da consulta medica.
- `POST /api/v1/atendimentos/{id}/medicoes`: Reavaliacao e adicao de medicoes de sinais vitais.
- `PATCH /api/v1/atendimentos/{id}/classificacao-risco`: Reclassificacao de risco com justificativa.
- `PATCH /api/v1/atendimentos/{id}/prescricao`: Registro de conduta clinica e prescricao.
- `PATCH /api/v1/atendimentos/{id}/finalizacao`: Conclusao do atendimento apos a consulta.
- `PATCH /api/v1/atendimentos/{id}/cancelamento`: Cancelamento de atendimento com justificativa.

## Front-end

O front-end fica na pasta `frontend` e é feito só com HTML, CSS e JavaScript. Ele usa a API do back-end para tudo: cadastro, login (com token JWT) e todas as ações do atendimento.

### Como executar

1. Na raiz do projeto, suba o back-end:
   - Windows: `mvnw.cmd spring-boot:run`
   - Linux/macOS: `./mvnw spring-boot:run`
2. Espere aparecer `Started TddTriagemHospitalarApplication`. A API fica em `http://localhost:8080`.
3. Abra o arquivo `frontend/index.html` no navegador (dois cliques no arquivo).
4. Clique em **Criar conta**, preencha nome, sobrenome, e-mail e senha e depois entre com o e-mail e a senha.

### O que dá para fazer

- Abrir um atendimento pelo CPF do paciente.
- Ver a fila de atendimentos ordenada pelo risco (vermelho primeiro) e o tempo de espera de cada paciente.
- Abrir um atendimento e seguir o fluxo: triagem, início da consulta, novas medições, reclassificação de risco, prescrição, finalização ou cancelamento.
- Receber avisos com som de chamada nas ações e quando um paciente passa do tempo máximo de espera do risco (vermelho: imediato, amarelo: 60 min, verde: 120 min).

As mensagens de erro que aparecem na tela vêm das regras do domínio, pela API.

### Observações

- A tabela de usuários é recriada sempre que o back-end reinicia, então é preciso criar a conta de novo depois de reiniciar.
- Se aparecer erro ao abrir ou listar atendimentos depois de atualizar o projeto, apague o arquivo `database.db` da raiz para ele ser criado de novo com todas as colunas.
