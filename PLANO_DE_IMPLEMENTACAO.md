# Plano de Implementação da API - Omnichain Vehicle (Revisado)

## 1. Visão Geral e Requisitos

Este documento detalha o plano de construção para uma API REST robusta para gerenciamento de veículos e clientes. Esta versão revisada incorpora feedback para um design mais limpo, incluindo soft delete, auditoria, e uma separação mais estrita de responsabilidades entre os módulos.

### 1.1. Requisitos Obrigatórios
- **RF01**: Utilizar Java 17.
- **RF02**: Utilizar o framework Spring Boot.
- **RF03**: Utilizar Hibernate para Mapeamento Objeto-Relacional (ORM).
- **RF04**: Utilizar MySQL como banco de dados.
- **RF05**: Implementar operações CRUD (Create, Read, Update, Delete) para **Veículos** e **Clientes**.
- **RF06**: Fornecer endpoints RESTful para todas as operações CRUD.
- **RF07**: Implementar autenticação e autorização com JSON Web Tokens (JWT).
- **RF08**: Implementar validações de entrada para os campos das entidades.
- **RF09**: Disponibilizar o projeto em um repositório Git público.
- **RF10**: Incluir um `README.md` com instruções detalhadas para execução.
- **RF11**: Fornecer os scripts DDL (Data Definition Language) para criação das tabelas.

### 1.2. Requisitos Opcionais (Pontos Extras)
- **PE01**: Implementar testes unitários e de integração.
- **PE02**: Garantir o controle de transações para consistência dos dados.
- **PE03**: Adicionar um mecanismo de logging para eventos importantes.
- **PE04**: Documentar a API com Swagger/OpenAPI.
- **PE05**: Implementar um sistema de cache para otimizar o desempenho.

## 2. Arquitetura Proposta

A arquitetura será **modular**, com responsabilidades bem definidas, e incorporará conceitos transversais como auditoria, logging e caching de forma centralizada.

### 2.1. Estrutura de Módulos
- **`core`**: Módulo para configurações globais (Spring Security, Exceptions, Web) e componentes compartilhados, como o `UserDetailsServiceImpl`.
- **`usuario`**: Módulo de acesso a dados. Sua única função é gerenciar a entidade `Usuario` e seu repositório. **Não possui controllers ou lógica de negócio complexa**.
- **`autenticacao`**: Módulo focado em autenticar credenciais, gerar/validar tokens JWT e fornecer informações do usuário logado (`/me`). **Não contém repositórios próprios de negócio**.
- **`cliente`**: Módulo dedicado a todas as funcionalidades da entidade `Cliente`.
- **`veiculo`**: Módulo dedicado a todas as funcionalidades da entidade `Veiculo`.

### 2.2. Estrutura de Diretórios
```
omnichain-vehicle-api/
├── src/
│   ├── main/
│   │   ├── java/com/fazpay/vehicle/
│   │   │   ├── VehicleApiApplication.java
│   │   │   │
│   │   │   ├── core/                      # ⚙️ MÓDULO CORE
│   │   │   │   ├── config/
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── exception/
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   └── security/
│   │   │   │       ├── UserDetailsServiceImpl.java // Movido para cá
│   │   │   │       └── JwtTokenProvider.java
│   │   │   │
│   │   │   ├── usuario/                   # 🧑 MÓDULO DE DADOS DO USUÁRIO
│   │   │   │   ├── model/
│   │   │   │   │   └── Usuario.java
│   │   │   │   └── repository/
│   │   │   │       └── UsuarioRepository.java
│   │   │   │
│   │   │   ├── autenticacao/              # 🔐 MÓDULO DE AUTENTICAÇÃO (SEM REPOSITORY)
│   │   │   │   ├── controller/
│   │   │   │   │   └── AuthController.java
│   │   │   │   └── service/
│   │   │   │       └── AuthService.java
│   │   │   │
│   │   │   ├── cliente/                    # 👤 MÓDULO DE CLIENTES
│   │   │   │   ├── model/
│   │   │   │   │   └── Cliente.java
│   │   │   │   ├── ... (controller, service, repository, dto)
│   │   │   │
│   │   │   └── veiculo/                   # 🚗 MÓDULO DE VEÍCULOS
│   │   │       ├── model/
│   │   │       │   └── Veiculo.java
│   │   │       └── ... (controller, service, repository, dto)
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

### 2.3. Containerização com Docker
A configuração do `Dockerfile` e `docker-compose.yml` permanece a mesma, garantindo um ambiente de desenvolvimento e execução consistente.

### 2.4. Conceitos Transversais

#### 2.4.1. Auditoria e Soft Delete
- **Auditoria**: As entidades `Cliente` e `Veiculo` terão campos de auditoria gerenciados pelo JPA:
  - `created_at`: Preenchido na criação (`@CreationTimestamp`).
  - `updated_at`: Atualizado a cada modificação (`@UpdateTimestamp`).
- **Soft Delete**: A exclusão será lógica para preservar o histórico.
  - Um campo `deleted_at` (Timestamp) será adicionado às entidades.
  - A anotação `@SQLDelete` será usada na entidade para transformar um `DELETE` em um `UPDATE` que define a data de exclusão.
  - A anotação `@Where(clause = "deleted_at IS NULL")` garantirá que todas as consultas busquem apenas registros não excluídos.

#### 2.4.2. Controle Transacional
- **Estratégia**: A anotação `@Transactional` do Spring será aplicada nos métodos da camada de **Serviço** (`ClienteService`, `VeiculoService`).
- **Garantia**: Isso garante que operações de escrita (criação, atualização, exclusão) sejam atômicas. Se ocorrer um erro, a transação inteira sofre rollback, mantendo a consistência dos dados.

#### 2.4.3. Logging
- **Ferramenta**: Utilização do SLF4J com Logback (padrão do Spring Boot).
- **Estratégia**: Logs serão registrados em pontos críticos da aplicação.

#### 2.4.4. Caching
- **Ferramenta**: Spring Cache com um provider em memória como o Caffeine.
- **Estratégia**: O cache será habilitado com `@EnableCaching` e aplicado seletivamente em métodos de consulta que não mudam com frequência.

## 3. Modelo de Dados e Endpoints

### 3.1. Script DDL (MySQL) Revisado
```sql
CREATE DATABASE IF NOT EXISTS vehicle_api_db;
USE vehicle_api_db;

-- Tabela de clientes com auditoria e soft delete
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- Tabela de usuários para autenticação com relação ao cliente
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    cliente_id BIGINT UNIQUE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- Tabela de veículos com auditoria e soft delete
CREATE TABLE veiculos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(7) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    ano INT NOT NULL,
    cor VARCHAR(30) NOT NULL,
    cliente_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);
```

### 3.2. Definição dos Endpoints da API

#### Módulo de Autenticação
| Método | Endpoint             | Protegido | Descrição                                               |
|--------|----------------------|-----------|---------------------------------------------------------|
| `POST` | `/api/v1/auth/login` | Não       | Autentica um usuário e retorna um token JWT.              |
| `GET`  | `/api/v1/auth/me`    | Sim       | Retorna os dados do cliente associado ao usuário logado. |

#### Módulo de Clientes
| Método | Endpoint                | Protegido | Descrição                                                                               |
|--------|-------------------------|-----------|-----------------------------------------------------------------------------------------|
| `GET`  | `/api/v1/clientes`      | Sim       | Lista clientes com paginação. Suporta filtros por `nome` e `dataCriacao`. |
| `GET`  | `/api/v1/clientes/all`  | Sim       | Lista **todos** os clientes não excluídos, sem paginação.                                 |
| `GET`  | `/api/v1/clientes/{id}` | Sim       | Busca um cliente não excluído por seu ID.                                               |
| `POST` | `/api/v1/clientes`      | Sim       | Cria um novo cliente.                                                                   |
| `PUT`  | `/api/v1/clientes/{id}` | Sim       | Atualiza os dados de um cliente.                                                        |
| `DELETE`| `/api/v1/clientes/{id}` | Sim       | **Exclui logicamente** um cliente (Soft Delete).                                        |

#### Módulo de Veículos

| Método | Endpoint                | Protegido | Descrição                                                                               |

|--------|-------------------------|-----------|-----------------------------------------------------------------------------------------|

| `GET`  | `/api/v1/veiculos`      | Sim       | Lista veículos com paginação. Suporta filtros por `marca`, `modelo` e `cor`.              |

| `GET`  | `/api/v1/veiculos/all`  | Sim       | Lista **todos** os veículos não excluídos, sem paginação.                                 |

| `GET`  | `/api/v1/veiculos/{id}` | Sim       | Busca um veículo não excluído por seu ID.                                               |

| `POST` | `/api/v1/veiculos`      | Sim       | Cria um novo veículo, associando-o a um cliente existente.                              |

| `PUT`  | `/api/v1/veiculos/{id}` | Sim       | Atualiza os dados de um veículo.                                                        |

| `DELETE`| `/api/v1/veiculos/{id}` | Sim       | **Exclui logicamente** um veículo (Soft Delete).                                        |





## 4. Plano de Desenvolvimento (Sprints)



### Sprint 0: Configuração do Ambiente

- **Objetivo**: Estruturar o projeto e o ambiente Docker.

1.  Inicializar projeto Spring Boot e configurar `pom.xml`.

2.  Criar a estrutura de diretórios dos módulos.

3.  Adicionar `Dockerfile` e `docker-compose.yml` e validar a comunicação API-Banco.

4.  Configurar o repositório Git.



### Sprint 1: Módulo de Autenticação e Segurança

- **Objetivo**: Implementar o fluxo de autenticação e o endpoint `/me`.

1.  Implementar as entidades `Usuario` e `Cliente` com a relação entre elas.

2.  Implementar `UserDetailsServiceImpl` no módulo `core.security`.

3.  Configurar `SecurityConfig` para proteger os endpoints.

4.  Implementar o `AuthController` com os endpoints `/login` e `/me`.

5.  Adicionar **logging** para o processo de login.



### Sprint 2: Módulo de Clientes com Filtros

- **Objetivo**: Entregar o CRUD de `Cliente` com as novas opções de listagem.

1.  Implementar a entidade `Cliente` com auditoria e **soft delete**.

2.  Implementar o `ClienteRepository` usando Specifications ou Querydsl para os filtros dinâmicos.

3.  Implementar o `ClienteService` e o `ClienteController` com os 3 endpoints de listagem (`/clientes`, `/clientes/all`, `/clientes/{id}`).

4.  Garantir o **controle transacional** (`@Transactional`).

5.  Criar testes unitários e de integração para os filtros e a lógica de soft delete.



### Sprint 3: Módulo de Veículos e Cache

- **Objetivo**: Entregar o CRUD de `Veiculo` com filtros e implementar caching.

1.  Implementar a entidade `Veiculo` com auditoria e **soft delete**, e a relação com `Cliente`.

2.  Implementar o `VeiculoRepository` utilizando Specifications ou Querydsl para filtros dinâmicos (por `marca`, `modelo`, `cor`).

3.  Implementar o `VeiculoService` e o `VeiculoController` com todos os endpoints definidos.

4.  Garantir o **controle transacional** em todas as operações de escrita do serviço.

5.  Habilitar o cache do Spring (`@EnableCaching`) e aplicar **caching** (`@Cacheable`) em métodos de consulta de baixa volatilidade (ex: `GET /veiculos/{id}`).

6.  Criar testes unitários e de integração para os filtros, a lógica de soft delete e o CRUD de veículos.



### Sprint 4: Finalização e Documentação

- **Objetivo**: Polir a aplicação, documentar e preparar para entrega.

1.  Configurar e integrar **Swagger/OpenAPI** para documentar todos os endpoints da API, incluindo DTOs e respostas de erro.

2.  Escrever o `README.md` com instruções detalhadas sobre:

    - Pré-requisitos (Java, Maven, Docker).

    - Como configurar e executar a aplicação (localmente e com Docker).

    - Como executar os testes.

    - Exemplos de uso da API (cURL ou Postman).

3.  Incluir o script DDL final e limpo na raiz do repositório ou em uma pasta `sql/`.

4.  Revisar todos os níveis de **logging** para garantir que são informativos e não expõem dados sensíveis.

5.  Validar o funcionamento do **caching** e do **soft delete** em toda a aplicação.

6.  Realizar uma rodada final de testes de ponta a ponta (end-to-end) para todos os fluxos da API.
