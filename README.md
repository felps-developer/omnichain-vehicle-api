# 🚗 API de Gestão de Veículos e Clientes

API RESTful para gerenciamento de veículos e clientes com autenticação JWT, desenvolvida com Java 17 e Spring Boot 3.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Como Executar](#como-executar)
- [Testando no Swagger](#testando-no-swagger)
- [Endpoints da API](#endpoints-da-api)
- [Testes](#testes)

---

## 🎯 Sobre o Projeto

Sistema completo de gerenciamento de clientes e seus veículos, com autenticação JWT, validações customizadas, soft delete e auditoria automática.

### ✨ Funcionalidades Principais

- ✅ **CRUD Completo** de Clientes e Veículos
- ✅ **Autenticação JWT** (JSON Web Tokens)
- ✅ **Validações Customizadas**:
  - CPF (com verificação de dígitos)
  - Telefone (formato brasileiro com DDD)
  - Placa de veículo (formato antigo ABC1234 e Mercosul ABC1D23)
  - Email (padrão RFC 5322)
- ✅ **Soft Delete** (exclusão lógica)
- ✅ **Auditoria Automática** (created_at, updated_at, deleted_at)
- ✅ **Paginação e Filtros** em listagens
- ✅ **PATCH** para atualizações parciais
- ✅ **Cache com Caffeine** para melhor performance
- ✅ **Documentação Swagger/OpenAPI** interativa
- ✅ **Tratamento Global de Exceções**

---

## 🏗️ Arquitetura

### Package by Feature (Modular)

A aplicação utiliza **arquitetura modular** (Package by Feature) ao invés da tradicional separação por camadas.

**Vantagens:**
- ✅ Alta coesão - funcionalidades relacionadas ficam juntas
- ✅ Baixo acoplamento entre módulos
- ✅ Fácil manutenção e evolução
- ✅ Escalabilidade - adicionar novos módulos sem impactar existentes

### Estrutura Modular

```
src/main/java/com/fazpay/vehicle/
│
├── core/                   # Configurações centrais
│   ├── config/            # Security, OpenAPI, Cache
│   ├── security/          # JWT Provider & Filters
│   ├── exception/         # Exception Handler
│   └── validation/        # Validadores customizados (CPF, Telefone, Placa)
│
├── auth/                   # Módulo de Autenticação
│   ├── controller/        # Login, Register, Me
│   ├── service/           # Lógica de autenticação
│   └── dto/               # Request/Response DTOs
│
├── customer/              # Módulo de Clientes
│   ├── controller/        # REST endpoints
│   ├── service/           # Regras de negócio
│   ├── repository/        # Acesso ao banco
│   ├── model/             # Entidade JPA
│   └── dto/               # Request/Response DTOs
│
├── vehicle/               # Módulo de Veículos
│   ├── controller/        # REST endpoints
│   ├── service/           # Regras de negócio
│   ├── repository/        # Acesso ao banco
│   ├── model/             # Entidade JPA
│   └── dto/               # Request/Response DTOs
│
└── user/                  # Módulo de Usuários
    ├── model/             # Entidade JPA
    └── repository/        # Acesso ao banco
```

### Padrões de Projeto Utilizados

- **DTO Pattern** - Separação entre entidades e objetos de transferência
- **Repository Pattern** - Abstração do acesso a dados
- **Service Layer** - Lógica de negócio isolada dos controllers
- **Builder Pattern** - Construção de objetos complexos (via Lombok)
- **Strategy Pattern** - Validadores customizados
- **Exception Handler** - Tratamento centralizado de erros

---

## 🚀 Tecnologias

### Core
- **Java 17**
- **Spring Boot 3.2.0**
- **Maven**

### Security & Auth
- **Spring Security**
- **JWT (JSON Web Tokens)** - io.jsonwebtoken (JJWT)

### Database
- **Spring Data JPA** / **Hibernate**
- **MySQL 8.0**
- **H2** (testes)

### Utilities
- **Lombok** - Redução de boilerplate
- **Bean Validation** - Validações declarativas
- **Caffeine Cache** - Cache em memória

### Documentation & Testing
- **Swagger/OpenAPI 3** - Documentação interativa
- **JUnit 5** - Testes unitários
- **Mockito** - Mocks para testes
- **MockMvc** - Testes de integração

### DevOps
- **Docker** & **Docker Compose** - Banco de dados

---

## 📦 Pré-requisitos

- ☕ **Java 17** ou superior
- 📦 **Maven 3.6+**
- 🐳 **Docker** (para o banco de dados)

---

## 🚀 Como Executar

### 1️⃣ Clonar o repositório

```bash
git clone <seu-repositorio>
cd omnichain-vehicle-api
```

### 2️⃣ Iniciar o banco de dados (Docker)

```bash
docker-compose up -d
```

Isso iniciará o **MySQL na porta 3306** com as tabelas e dados já criados.

### 3️⃣ Compilar o projeto

```bash
mvn clean compile
```

### 4️⃣ Executar a aplicação

```bash
mvn spring-boot:run
```

✅ **API disponível em:** `http://localhost:8080`

---

## 🔍 Testando no Swagger

### 📚 Acessar a Documentação

Após iniciar a aplicação, acesse:

🔗 **Swagger UI:** http://localhost:8080/swagger-ui.html

### 🔐 Credenciais de Teste

O banco vem com um usuário pré-cadastrado para testes:

| Campo | Valor |
|-------|-------|
| **Username** | `admin` |
| **Senha** | `senha123` |
| **Email** | admin@fazpay.com |

### 📝 Passo a Passo no Swagger

1. **Abra o Swagger UI** (`http://localhost:8080/swagger-ui.html`)

2. **Faça Login:**
   - Expanda o endpoint `POST /api/v1/auth/login`
   - Clique em "Try it out"
   - Use o JSON abaixo:
   ```json
   {
     "username": "admin",
     "password": "senha123"
   }
   ```
   - Clique em "Execute"
   - **Copie o token** da resposta (campo `token`)

3. **Autentique no Swagger:**
   - Clique no botão **"Authorize" 🔓** (canto superior direito)
   - Cole o token no formato: `Bearer SEU_TOKEN_AQUI`
   - Clique em "Authorize"
   - Clique em "Close"

4. **Teste os Endpoints:**
   - Agora você pode testar todos os endpoints protegidos! 🎉
   - Exemplo: `GET /api/v1/clientes` para listar clientes
   - Exemplo: `POST /api/v1/veiculos` para criar um veículo

---



## 📋 Validações Implementadas

### CPF
- ✅ Valida formato com ou sem pontuação
- ✅ Verifica dígitos verificadores
- ✅ Rejeita sequências repetidas (111.111.111-11)

### Telefone
- ✅ Formato: `(XX) XXXX-XXXX` ou `(XX) 9XXXX-XXXX`
- ✅ Valida DDD brasileiro
- ✅ Aceita com ou sem formatação

### Placa de Veículo
- ✅ Formato antigo: `ABC1234`
- ✅ Formato Mercosul: `ABC1D23`
- ✅ Case insensitive

### Email
- ✅ Validação padrão RFC 5322
- ✅ Domínio obrigatório

---

## 📊 Banco de Dados

### Modelo de Dados

```
┌─────────────┐         ┌──────────────┐         ┌──────────────┐
│   users     │         │  clientes    │         │  veiculos    │
├─────────────┤         ├──────────────┤         ├──────────────┤
│ id (UUID)   │         │ id (UUID)    │◄────┐   │ id (UUID)    │
│ username    │         │ nome         │     └───│ cliente_id   │
│ email       │         │ cpf          │         │ placa        │
│ password    │         │ email        │         │ marca        │
│ created_at  │         │ telefone     │         │ modelo       │
│ updated_at  │         │ created_at   │         │ ano          │
└─────────────┘         │ updated_at   │         │ cor          │
                        │ deleted_at   │         │ created_at   │
                        └──────────────┘         │ updated_at   │
                                                 │ deleted_at   │
                                                 └──────────────┘
```

**Características:**
- ✅ Todos os IDs são **UUID** (maior segurança e escalabilidade)
- ✅ **Soft Delete** em clientes e veículos (deleted_at)
- ✅ **Auditoria automática** (created_at, updated_at)
- ✅ **Relacionamento** 1:N entre Cliente e Veículo

---

## 🧪 Testes

A aplicação possui **80 testes automatizados**:

### Testes Unitários (63 testes)

**Validadores (32 testes)**
- `CpfValidatorTest` - 10 testes
- `TelefoneValidatorTest` - 11 testes
- `PlacaValidatorTest` - 11 testes

**Services (30 testes)**
- `AuthServiceTest` - 6 testes
- `CustomerServiceTest` - 11 testes
- `VehicleServiceTest` - 13 testes

**Aplicação (1 teste)**
- `OmnichainVehicleApiApplicationTests` - Teste de contexto

### Testes de Integração (17 testes)

- `AuthControllerIntegrationTest` - 4 testes
- `CustomerControllerIntegrationTest` - 6 testes
- `VehicleControllerIntegrationTest` - 7 testes

### Executar os Testes

```bash
# Todos os testes
mvn test

# Apenas testes unitários
mvn test -Dtest="*ServiceTest,*ValidatorTest"

# Apenas testes de integração
mvn test -Dtest="*IntegrationTest"
```

---

## 📝 Exemplos de Uso

### Criar Cliente

```bash
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "11144477735",
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

### Atualização Parcial (PATCH)

```bash
# Atualizar apenas a cor do veículo
curl -X PATCH http://localhost:8080/api/v1/veiculos/{id} \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cor": "Azul"
  }'
```

---

## 🎯 Pontos Extras Implementados

✅ **Testes Unitários e Integração** (80 testes)  
✅ **Controle de Transações** (@Transactional)  
✅ **Logging Estruturado** (SLF4J + Lombok)  
✅ **Documentação Swagger** (OpenAPI 3)  
✅ **Sistema de Cache** (Caffeine)  
✅ **Validações Customizadas** (CPF, Telefone, Placa)  
✅ **PATCH** para atualizações parciais  
✅ **Soft Delete** (exclusão lógica)  
✅ **Auditoria Automática** (timestamps)  
✅ **UUID** como identificadores  

---

## 📚 Documentação Adicional

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **H2 Console (testes):** http://localhost:8080/h2-console

---

## 👨‍💻 Desenvolvido com

- ☕ Java 17
- 🍃 Spring Boot 3
- 🔐 JWT Authentication
- 🗄️ MySQL
- 🐳 Docker
- 📝 Swagger/OpenAPI

---

**🎉 Projeto pronto para produção!**
