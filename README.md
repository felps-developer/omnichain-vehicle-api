# Omnichain Vehicle API

REST API for vehicle and customer management with JWT authentication.

## 📋 Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Project Structure](#project-structure)

## ✨ Features

- ✅ Customer CRUD operations
- ✅ Vehicle CRUD operations
- ✅ JWT Authentication
- ✅ Soft Delete for customers and vehicles
- ✅ Audit fields (created_at, updated_at)
- ✅ Data validation
- ✅ Pagination and filters
- ✅ Cache (Caffeine)
- ✅ Global exception handling
- ✅ Swagger/OpenAPI documentation
- ✅ Docker support

## 🚀 Technologies

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT
- **Spring Data JPA** / Hibernate
- **MySQL 8.0**
- **Lombok**
- **Swagger/OpenAPI 3**
- **Caffeine Cache**
- **Maven**
- **Docker & Docker Compose**

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0** (or use Docker)
- **Docker & Docker Compose** (optional, recommended)

## 💻 Installation

### 1. Clone the repository

```bash
git clone https://github.com/your-username/omnichain-vehicle-api.git
cd omnichain-vehicle-api
```

### 2. Configure the database

If running MySQL locally, create the database:

```sql
CREATE DATABASE vehicle_api_db;
```

Update `src/main/resources/application.properties` with your database credentials if needed.

### 3. Build the project

```bash
mvn clean install
```

## 🏃 Running the Application

### Option 1: Run with Docker Compose (Recommended)

```bash
docker-compose up -d
```

This will start:
- MySQL database on port `3306`
- API application on port `8080`

### Option 2: Run locally with Maven

Ensure MySQL is running, then:

```bash
mvn spring-boot:run
```

### Option 3: Run the JAR

```bash
mvn clean package
java -jar target/omnichain-vehicle-api-0.0.1-SNAPSHOT.jar
```

## 📖 API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON specification:

```
http://localhost:8080/v3/api-docs
```

## 🔐 Authentication

### 1. Register (Optional)

**POST** `/api/v1/auth/register`

Request body:
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "newuser"
}
```

### 2. Login

**POST** `/api/v1/auth/login`

Request body:
```json
{
  "username": "admin",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

### 3. Use the token

Add the token to the `Authorization` header for protected endpoints:

```
Authorization: Bearer <your-token>
```

### 🔑 Test Users

The database comes pre-seeded with test users. **Any authenticated user can create, edit, and view customers and vehicles.**

| Username | Email | Password |
|----------|-------|----------|
| `admin` | admin@fazpay.com | `password123` |
| `user1` | user1@example.com | `password123` |
| `user2` | user2@example.com | `password123` |

**Note**: 
- All passwords are: `password123`
- There is **no relationship** between users and customers/vehicles
- Authentication is only for access control (prevent unauthenticated access)
- Any logged-in user has full access to all customers and vehicles

## 🛣️ API Endpoints

### Authentication

| Method | Endpoint | Description | Protected |
|--------|----------|-------------|-----------|
| POST | `/api/v1/auth/login` | Authenticate user | ❌ |
| POST | `/api/v1/auth/register` | Register new user | ❌ |
| GET | `/api/v1/auth/me` | Get current user info | ✅ |

### Customers

| Method | Endpoint | Description | Protected |
|--------|----------|-------------|-----------|
| GET | `/api/v1/clientes` | List customers (paginated) | ✅ |
| GET | `/api/v1/clientes/all` | List all customers | ✅ |
| GET | `/api/v1/clientes/{id}` | Get customer by ID | ✅ |
| POST | `/api/v1/clientes` | Create customer | ✅ |
| PUT | `/api/v1/clientes/{id}` | Update customer | ✅ |
| DELETE | `/api/v1/clientes/{id}` | Delete customer (soft) | ✅ |

### Vehicles

| Method | Endpoint | Description | Protected |
|--------|----------|-------------|-----------|
| GET | `/api/v1/veiculos` | List vehicles (paginated) | ✅ |
| GET | `/api/v1/veiculos/all` | List all vehicles | ✅ |
| GET | `/api/v1/veiculos/{id}` | Get vehicle by ID | ✅ |
| GET | `/api/v1/veiculos/placa/{placa}` | Get vehicle by license plate | ✅ |
| POST | `/api/v1/veiculos` | Create vehicle | ✅ |
| PUT | `/api/v1/veiculos/{id}` | Update vehicle | ✅ |
| DELETE | `/api/v1/veiculos/{id}` | Delete vehicle (soft) | ✅ |

## 📝 Example Requests

### Create a Customer

```bash
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Carlos Souza",
    "cpf": "11122233344",
    "email": "carlos@example.com",
    "telefone": "(11) 91234-5678"
  }'
```

### Create a Vehicle

```bash
curl -X POST http://localhost:8080/api/v1/veiculos \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "placa": "ABC1D23",
    "marca": "Ford",
    "modelo": "Fiesta",
    "ano": 2022,
    "cor": "Azul",
    "clienteId": 1
  }'
```

### List Vehicles with Filters

```bash
curl "http://localhost:8080/api/v1/veiculos?marca=Toyota&cor=Prata&page=0&size=10" \
  -H "Authorization: Bearer <your-token>"
```

## 🧪 Testing

### Run all tests

```bash
mvn test
```

### Run specific test class

```bash
mvn test -Dtest=CustomerServiceTest
```

## 📁 Project Structure

```
omnichain-vehicle-api/
├── src/
│   ├── main/
│   │   ├── java/com/fazpay/vehicle/
│   │   │   ├── VehicleApiApplication.java
│   │   │   ├── core/                    # Core configurations
│   │   │   │   ├── config/
│   │   │   │   ├── exception/
│   │   │   │   └── security/
│   │   │   ├── user/                    # User module
│   │   │   │   ├── model/
│   │   │   │   └── repository/
│   │   │   ├── auth/                    # Authentication module
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   └── dto/
│   │   │   ├── customer/                # Customer module
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   └── vehicle/                 # Vehicle module
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── model/
│   │   │       └── dto/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── sql/
│   └── ddl.sql                          # Database DDL script
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## 🔧 Configuration

### Database Configuration

Edit `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vehicle_api_db
spring.datasource.username=root
spring.datasource.password=root
```

### JWT Configuration

```properties
jwt.secret=mySecretKeyForJWTTokenGenerationWithAtLeast256BitsLength12345
jwt.expiration=86400000
```

### Cache Configuration

```properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=10m
```

## 🐳 Docker

### Build Docker image

```bash
docker build -t omnichain-vehicle-api .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

### Stop services

```bash
docker-compose down
```

## 📊 Database Schema

See `sql/ddl.sql` for the complete database schema.

### Main Tables

- **usuarios**: User credentials for authentication (with email validation)
- **clientes**: Customer information with soft delete  
- **veiculos**: Vehicle information with soft delete

### Usuario Table Structure

```sql
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Purpose:**
- ✅ Authentication and access control only
- ✅ No relationship with customers or vehicles
- ✅ Any authenticated user can manage all data

**Validations:**
- ✅ Username must be unique
- ✅ Email must be unique and valid format
- ✅ Password encrypted with BCrypt
- ✅ Email validation on registration

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0.

## 👤 Author

**FazPay Team**

- Email: support@fazpay.com

## 📞 Support

For support, email support@fazpay.com or open an issue in the repository.

---

Made with ❤️ by FazPay

