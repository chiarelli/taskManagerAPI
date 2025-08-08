# ✅ Sistema de Gerenciamento de Tarefas API

Este projeto foi desenvolvido como parte de um desafio técnico para criação de uma API REST para o gerenciamento de projetos e tarefas de equipes, permitindo organização pessoal e colaboração entre usuários.

O objetivo é implementar um sistema com DDD (Domain-Driven Design) e boas práticas de arquitetura, que atenda a regras de negócio específicas e esteja preparado para evoluir em funcionalidades futuras como autenticação e dashboards.

## 🧠 Regras de Negócio

- Prioridade de Tarefas

- Cada tarefa deve ter uma prioridade atribuída: `baixa`, `média` ou `alta`.

- A prioridade não pode ser alterada após a criação.

- Restrições de Remoção de Projetos

- Projetos não podem ser removidos se houver tarefas pendentes.

- Caso haja tentativa, retornar erro sugerindo concluir ou remover as tarefas primeiro.

- Histórico de Atualizações

- Toda alteração em uma tarefa (status, descrição, comentários) deve ser registrada.

- O histórico contém: campo modificado, data/hora e usuário responsável.

- Limite de Tarefas por Projeto

- Cada projeto pode ter no máximo 20 tarefas.

- Exceder o limite retorna erro.

- Relatórios de Desempenho

- Endpoints para gerar métricas como número médio de tarefas concluídas por usuário nos últimos 30 dias.

- Acesso restrito a usuários com função Gerente.

- Comentários nas Tarefas

- Usuários podem adicionar comentários.

- Comentários também são registrados no histórico da tarefa.

## 🛠 Tecnologias Utilizadas

- Java 21

- Spring Boot 3.5.x

- Spring Data JPA

- MongoDB

- Maven

- SpringDoc OpenAPI (Swagger)

- Jakarta Bean Validation

- Docker + Docker Compose

- Testcontainers para testes de integração

## 📦 Entidades Principais

#### 👤 Usuário
id: UUID

nome: String (obrigatório)

email: String (obrigatório, único)

papel: Enum (usuario, gerente)

#### 📁 Projeto
id: UUID

nome: String (obrigatório)

descricao: String

tarefas: Lista de tarefas (máx. 20)

#### ✅ Tarefa
id: UUID

titulo: String (obrigatório)

descricao: String

prioridade: Enum (`baixa`, `média`, `alta`)

status: Enum (`pendente`, `em andamento`, `concluída`)

dataVencimento: LocalDate

comentarios: Lista de comentários

historico: Lista de alterações

## 🔄 Endpoints Principais (Swagger)

A API possui documentação interativa disponível via Swagger:
🔗 http://localhost:8080/swagger-ui/index.html

#### 📁 Projetos
`GET /api/v1/projetos` – Listar todos os projetos do usuário

`POST /api/v1/projetos` – Criar um novo projeto

`DELETE /api/v1/projetos/{id}` – Remover um projeto (respeitando regras de negócio)

#### ✅ Tarefas

`GET /api/v1/projetos/{projetoId}/tarefas` – Listar tarefas de um projeto

`POST /api/v1/projetos/{projetoId}/tarefas` – Criar nova tarefa (máx. 20 por projeto)

`PUT /api/v1/tarefas/{id}` – Atualizar status ou detalhes de uma tarefa

`DELETE /api/v1/tarefas/{id}` – Remover tarefa

`POST /api/v1/tarefas/{id}/comentarios` – Adicionar comentário

#### 📊 Relatórios (Apenas para Gerentes)

`GET /api/v1/relatorios/tarefas-concluidas` – Média de tarefas concluídas por usuário nos últimos 30 dias

## ▶️ Como Executar o Projeto

1. Clonar o repositório
```bash
git clone https://github.com/chiarelli/taskManagerAPI.git
cd taskManagerAPI
```

2. Subir o banco de dados via Docker
```bash
docker-compose up -d
```

3. Executar os testes
```bash
./mvnw test
```

4. Rodar a aplicação
```bash
./mvnw -pl spring-boot-run spring-boot:run
```

## ✅ Status Atual

- [x] Estrutura inicial com Spring Boot e DDD

- [x] Entidades Usuário, Projeto e Tarefa implementadas

- [x] Documentação via Swagger

- [x] Integração com MongoDB

- [x] Testes automatizados (80%+ cobertura)

- [ ] Autenticação e Autorização de usuários via serviço externo

- [ ] Implementação de Relatórios de Desempenho

- [ ] Execução em ambiente Docker

---

### 👨‍💻 Autor

Feito por Raphael Mathias Chiarelli Gomes durante o curso de Java Web Developer na COTI Informática.

📬 chiarelli.rm@gmail.com 🔗 github.com/chiarelli