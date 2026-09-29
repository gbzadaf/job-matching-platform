# Job Matching Platform

Backend REST de uma plataforma de recrutamento (estilo LinkedIn/Indeed) que conecta **candidatos** e **vagas** com um sistema de **matching por skills** — o candidato descobre exatamente qual a % de compatibilidade com uma vaga e quais competências ainda precisa desenvolver.

Projeto construído do zero como estudo aprofundado de backend Java, seguindo boas práticas de arquitetura, segurança e testes usadas no mercado.

---

## Destaques técnicos

- **Matching candidato-vaga** com percentual de compatibilidade, skills correspondentes e skills faltantes
- **Autenticação stateless com JWT** (Spring Security) e autorização por *role* (`CANDIDATE` / `RECRUITER`)
- **Migrations versionadas com Flyway** — schema 100% rastreável, sem `ddl-auto: update`
- **UUID como chave primária** em todas as entidades
- Arquitetura em camadas (Controller → Service → Repository) com **DTOs dedicados** (nunca expõe entidades JPA na API)
- Tratamento de erros centralizado (`@RestControllerAdvice`) com respostas padronizadas
- Paginação nativa em todos os endpoints de listagem
- Documentação interativa via **Swagger/OpenAPI**
- **Testes unitários** (JUnit 5 + Mockito) cobrindo a lógica de negócio mais crítica
- Fluxo de trabalho em Git com branches por feature, Pull Requests e squash merge

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL |
| Migrations | Flyway |
| Segurança | Spring Security + JWT (JJWT) |
| Documentação | springdoc-openapi (Swagger UI) |
| Testes | JUnit 5, Mockito, AssertJ |
| Build | Maven |
| Utilitários | Lombok |

---

## Arquitetura

```
com.gabrielf.job_matching_platform/
├── config/          → configurações gerais (OpenAPI, etc.)
├── controller/      → endpoints REST
├── dto/
│   ├── request/     → payloads de entrada (com Bean Validation)
│   └── response/    → payloads de saída (records, nunca expõem entidades)
├── model/           → entidades JPA
│   └── enums/
├── repository/      → interfaces Spring Data JPA
├── service/         → regras de negócio
├── security/        → JWT, filtros, autenticação
└── exception/       → exceções customizadas + handler global
```

**Fluxo de uma requisição:** `Controller` (tradução HTTP) → `Service` (regra de negócio, autorização, transações) → `Repository` (acesso a dados) → `PostgreSQL`.

---

## Como rodar localmente

### Pré-requisitos
- Java 21+
- Maven
- PostgreSQL rodando localmente

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/gbzadaf/job-matching-platform.git
cd job-matching-platform

# 2. Crie o banco de dados
psql -U postgres -c "CREATE DATABASE jobmatching_dev;"

# 3. Ajuste as credenciais em src/main/resources/application-dev.yml
#    (usuário, senha e porta do seu PostgreSQL local)

# 4. Rode a aplicação (o Flyway cria o schema automaticamente)
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Documentação interativa (Swagger)

```
http://localhost:8080/swagger-ui/index.html
```

Clique em **Authorize** e cole o token JWT (obtido via `/api/v1/auth/login`) para testar os endpoints protegidos direto pela interface.

---

## Fluxo de autenticação

1. **Registrar** um usuário (`CANDIDATE` ou `RECRUITER`) via `POST /api/v1/users/register`
2. **Login** via `POST /api/v1/auth/login` → recebe um token JWT
3. Enviar o token em todas as requisições protegidas:
   ```
   Authorization: Bearer {token}
   ```

O usuário autenticado é sempre extraído do token — nenhum endpoint aceita um `userId`/`candidateId`/`recruiterId` manipulável no corpo da requisição (proteção contra IDOR).

---

## Principais endpoints

| Método | Rota | Descrição | Autenticação |
|---|---|---|---|
| `POST` | `/api/v1/users/register` | Registra um novo usuário | Pública |
| `POST` | `/api/v1/auth/login` | Autentica e retorna um JWT | Pública |
| `GET` | `/api/v1/jobs` | Lista vagas abertas (paginado) | Pública |
| `POST` | `/api/v1/jobs` | Cria uma vaga | `RECRUITER` |
| `PATCH` | `/api/v1/jobs/{id}/status` | Abre/fecha uma vaga | Autenticado |
| `POST` | `/api/v1/candidates` | Cria o perfil do candidato autenticado | `CANDIDATE` |
| `PUT` | `/api/v1/candidates/{id}` | Atualiza um perfil de candidato | Autenticado |
| `POST` | `/api/v1/applications` | Candidata-se a uma vaga | `CANDIDATE` |
| `GET` | `/api/v1/applications/job/{jobId}` | Lista candidaturas de uma vaga (paginado) | Autenticado |
| `GET` | `/api/v1/matching/candidate/{candidateId}` | Ranking de vagas por compatibilidade | Autenticado |
| `GET` | `/api/v1/matching/candidate/{candidateId}/job/{jobId}` | Compatibilidade com uma vaga específica | Autenticado |

Lista completa e testável em `/swagger-ui/index.html`.

### Exemplo de resposta — Matching

```json
{
  "jobId": "5a37e0c6-bbe4-4727-94cc-8eef91a8e23a",
  "jobTitle": "Desenvolvedor Java Pleno",
  "totalRequiredSkills": 4,
  "matchedSkillsCount": 3,
  "matchPercentage": 75.0,
  "matchedSkills": ["Java", "Spring Boot", "PostgreSQL"],
  "missingSkills": ["Docker"]
}
```

---

## Testes

```bash
./mvnw test
```

Cobre unitariamente (Mockito, sem banco/contexto Spring) a lógica de negócio de maior risco:

- **Matching** — cálculo de compatibilidade, casos de borda (vaga sem requisitos, candidato sem skills), ordenação por ranking
- **Autenticação** — senha nunca persistida em texto puro, prevenção de e-mail duplicado
- **Autorização por role** — só `CANDIDATE` cria perfil de candidato, só `RECRUITER` cria vaga
- **Regra de candidatura** — resolução correta do perfil de candidato a partir do usuário autenticado, prevenção de candidatura duplicada

---

## Possíveis evoluções

- Autorização de posse granular em todos os endpoints de escrita (hoje implementada só em parte)
- Testes de integração com Testcontainers
- Refresh tokens
- CORS configurado para um front-end real
- Cache (Redis) e busca full-text (Elasticsearch) para o catálogo de vagas
- Matching com pesos por senioridade e IA/embeddings semânticos

---

