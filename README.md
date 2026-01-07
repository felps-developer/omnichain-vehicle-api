# 🚗 API de Gestão de Veículos e Clientes

API RESTful para gerenciamento de veículos e clientes com autenticação JWT.

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Estrutura do Projeto](#estrutura-do-projeto)

## 🎯 Sobre o Projeto

Sistema de gerenciamento de clientes e veículos com autenticação JWT, desenvolvido com Java 17 e Spring Boot 3.

### Funcionalidades

- ✅ CRUD completo de clientes
- ✅ CRUD completo de veículos
- ✅ Autenticação JWT
- ✅ Soft delete (exclusão lógica)
- ✅ Auditoria automática (created_at, updated_at)
- ✅ Validação de dados
- ✅ Paginação e filtros
- ✅ Cache com Caffeine
- ✅ Documentação Swagger/OpenAPI
- ✅ Tratamento global de exceções

## 🏗️ Arquitetura

### Decisões Arquiteturais

**Arquitetura Modular (Package by Feature)**

A aplicação foi organizada em módulos funcionais ao invés da tradicional separação por camadas (controller, service, repository). Esta abordagem traz:

- **Alta Coesão**: Cada módulo agrupa tudo relacionado a uma funcionalidade
- **Baixo Acoplamento**: Módulos independentes e fáceis de manter
- **Escalabilidade**: Facilita adicionar novos módulos sem afetar os existentes
- **Manutenibilidade**: Alterações ficam isoladas em cada módulo

### Módulos da Aplicação

```
src/main/java/com/fazpay/vehicle/
│
├── core/                   # Módulo central (configurações compartilhadas)
│   ├── config/            # SecurityConfig, OpenApiConfig, CacheConfig
│   ├── security/          # JwtTokenProvider, JwtAuthenticationFilter
│   └── exception/         # GlobalExceptionHandler, exceções customizadas
│
├── user/                   # Módulo de usuários
│   ├── model/             # User (entidade)
│   └── repository/        # UserRepository
│
├── auth/                   # Módulo de autenticação
│   ├── controller/        # AuthController (login, register, /me)
│   ├── service/           # AuthService (lógica de autenticação)
│   └── dto/               # LoginRequest, LoginResponse, UserInfoResponse
│
├── customer/              # Módulo de clientes
│   ├── controller/        # CustomerController (endpoints REST)
│   ├── service/           # CustomerService (regras de negócio)
│   ├── repository/        # CustomerRepository (acesso ao banco)
│   ├── model/             # Customer (entidade JPA)
│   └── dto/               # CustomerRequest, CustomerResponse
│
└── vehicle/               # Módulo de veículos
    ├── controller/        # VehicleController (endpoints REST)
    ├── service/           # VehicleService (regras de negócio)
    ├── repository/        # VehicleRepository (acesso ao banco)
    ├── model/             # Vehicle (entidade JPA)
    └── dto/               # VehicleRequest, VehicleResponse
```

### Padrões Utilizados

- **DTO Pattern**: Separação entre entidades e objetos de transferência
- **Repository Pattern**: Abstração do acesso a dados
- **Service Layer**: Lógica de negócio isolada
- **Exception Handler**: Tratamento centralizado de erros
- **Soft Delete**: Exclusão lógica com flag deleted_at

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** + JWT
- **Spring Data JPA** / Hibernate
- **MySQL 8.0**
- **Lombok**
- **Swagger/OpenAPI 3**
- **Caffeine Cache**
- **Maven**
- **Docker** (apenas banco de dados)

## 📦 Pré-requisitos

- **Java 17** ou superior
- **Maven 3.6+**
- **Docker** (para o banco de dados)

## 🚀 Como Executar

### 1. Clonar o repositório

```bash
git clone <seu-repositorio>
cd omnichain-vehicle-api
```

### 2. Subir o banco de dados (Docker)

```bash
docker-compose up -d mysql-vehicle-db
```

Isso iniciará apenas o MySQL na porta `3306` com as tabelas e dados de teste já criados.

### 3. Compilar o projeto

```bash
mvn clean compile
```

### 4. Executar a aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 📚 Documentação da API

Após iniciar a aplicação, acesse a documentação interativa:

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## 🔐 Autenticação

A API utiliza **JWT (JSON Web Tokens)** para autenticação.

### 1. Fazer Login

**POST** `/api/v1/auth/login`

```json
{
  "username": "admin",
  "password": "senha123"
}
```

**Resposta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### 2. Usar o Token

Adicione o token no header `Authorization` de todas as requisições protegidas:

```
Authorization: Bearer <seu-token>
```

### 👤 Usuário de Teste

O banco vem com um usuário pré-cadastrado:

| Username | Email | Senha |
|----------|-------|-------|
| `admin` | admin@fazpay.com | `senha123` |

**Importante:** O usuário autenticado pode criar, editar e visualizar todos os clientes e veículos. A autenticação serve para controlar o acesso à API.

## 🛣️ Endpoints da API

### Autenticação

| Método | Endpoint | Descrição | Autenticado |
|--------|----------|-----------|-------------|
| POST | `/api/v1/auth/login` | Fazer login | ❌ |
| POST | `/api/v1/auth/register` | Registrar usuário | ❌ |
| GET | `/api/v1/auth/me` | Ver usuário logado | ✅ |

### Clientes

| Método | Endpoint | Descrição | Autenticado |
|--------|----------|-----------|-------------|
| GET | `/api/v1/clientes` | Listar (paginado) | ✅ |
| GET | `/api/v1/clientes/all` | Listar todos | ✅ |
| GET | `/api/v1/clientes/{id}` | Buscar por UUID | ✅ |
| POST | `/api/v1/clientes` | Criar cliente | ✅ |
| PUT | `/api/v1/clientes/{id}` | Atualizar cliente | ✅ |
| DELETE | `/api/v1/clientes/{id}` | Deletar (soft delete) | ✅ |

### Veículos

| Método | Endpoint | Descrição | Autenticado |
|--------|----------|-----------|-------------|
| GET | `/api/v1/veiculos` | Listar (paginado) | ✅ |
| GET | `/api/v1/veiculos/all` | Listar todos | ✅ |
| GET | `/api/v1/veiculos/{id}` | Buscar por UUID | ✅ |
| GET | `/api/v1/veiculos/placa/{placa}` | Buscar por placa | ✅ |
| POST | `/api/v1/veiculos` | Criar veículo | ✅ |
| PUT | `/api/v1/veiculos/{id}` | Atualizar veículo | ✅ |
| DELETE | `/api/v1/veiculos/{id}` | Deletar (soft delete) | ✅ |

## 📁 Estrutura do Projeto

```
omnichain-vehicle-api/
│
├── src/main/java/com/fazpay/vehicle/
│   ├── OmnichainVehicleApiApplication.java    # Classe principal
│   │
│   ├── core/                                   # Núcleo da aplicação
│   │   ├── config/                            # Configurações (Security, OpenAPI, Cache)
│   │   ├── security/                          # JWT Provider e Filters
│   │   └── exception/                         # Tratamento de exceções
│   │
│   ├── user/                                   # Módulo de usuários
│   │   ├── model/User.java                   # Entidade usuário
│   │   └── repository/UserRepository.java    # Repositório
│   │
│   ├── auth/                                   # Módulo de autenticação
│   │   ├── controller/AuthController.java    # Endpoints login/register
│   │   ├── service/AuthService.java          # Lógica de autenticação
│   │   └── dto/                               # DTOs de requisição/resposta
│   │
│   ├── customer/                               # Módulo de clientes
│   │   ├── controller/                        # REST endpoints
│   │   ├── service/                           # Regras de negócio
│   │   ├── repository/                        # Acesso ao banco
│   │   ├── model/Customer.java               # Entidade cliente
│   │   └── dto/                               # DTOs
│   │
│   └── vehicle/                                # Módulo de veículos
│       ├── controller/                        # REST endpoints
│       ├── service/                           # Regras de negócio
│       ├── repository/                        # Acesso ao banco
│       ├── model/Vehicle.java                # Entidade veículo
│       └── dto/                               # DTOs
│
├── src/main/resources/
│   └── application.properties                 # Configurações da aplicação
│
├── sql/
│   └── ddl.sql                                # Schema do banco (UUID)
│
├── docker-compose.yml                         # MySQL em Docker
├── Dockerfile                                 # Build da aplicação (opcional)
└── pom.xml                                    # Dependências Maven
```

## 📊 Banco de Dados

### Tabelas Principais

**usuarios** - Autenticação
- `id` (UUID)
- `username` (único)
- `email` (único, validado)
- `password` (BCrypt)

**clientes** - Informações dos clientes
- `id` (UUID)
- `nome`, `cpf`, `email`, `telefone`
- `created_at`, `updated_at`, `deleted_at`

**veiculos** - Informações dos veículos
- `id` (UUID)
- `placa`, `marca`, `modelo`, `ano`, `cor`
- `cliente_id` (FK → clientes)
- `created_at`, `updated_at`, `deleted_at`

**Nota:** Todos os IDs utilizam **UUID** para maior segurança e escalabilidade.

## 🛠️ Tecnologias e Padrões

- **Spring Security + JWT**: Autenticação stateless
- **Spring Data JPA**: Acesso a dados simplificado
- **Hibernate**: ORM para mapeamento objeto-relacional
- **Lombok**: Redução de código boilerplate
- **Caffeine**: Cache em memória de alta performance
- **Swagger/OpenAPI**: Documentação automática
- **Bean Validation**: Validações declarativas

## 📝 Exemplos de Uso

### Criar Cliente

```bash
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "12345678901",
    "email": "joao@example.com",
    "telefone": "(11) 98765-4321"
  }'
```

### Criar Veículo

```bash
curl -X POST http://localhost:8080/api/v1/veiculos \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "placa": "ABC1D23",
    "marca": "Toyota",
    "modelo": "Corolla",
    "ano": 2023,
    "cor": "Prata",
    "clienteId": "UUID-DO-CLIENTE"
  }'
```

## 🧪 Testes

```bash
# Rodar todos os testes
mvn test

# Rodar teste específico
mvn test -Dtest=CustomerServiceTest
```

---



