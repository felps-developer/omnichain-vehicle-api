-- Database Creation
CREATE DATABASE IF NOT EXISTS vehicle_api_db;
USE vehicle_api_db;

-- Customers Table (with audit and soft delete)
CREATE TABLE clientes (
    id VARCHAR(36) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_cpf (cpf),
    INDEX idx_email (email)
);

-- Users Table (for authentication - no relationship with customers)
CREATE TABLE usuarios (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Vehicles Table (with audit and soft delete)
CREATE TABLE veiculos (
    id VARCHAR(36) PRIMARY KEY,
    placa VARCHAR(7) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    ano INT NOT NULL,
    cor VARCHAR(30) NOT NULL,
    cliente_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    INDEX idx_placa (placa),
    INDEX idx_cliente_id (cliente_id)
);

-- Sample Data for Testing

-- Insert sample customers (with UUID)
INSERT INTO clientes (id, nome, cpf, email, telefone) VALUES
(UUID(), 'João Silva', '12345678901', 'joao.silva@example.com', '(11) 98765-4321'),
(UUID(), 'Maria Santos', '98765432109', 'maria.santos@example.com', '(21) 97654-3210'),
(UUID(), 'Pedro Oliveira', '45678912301', 'pedro.oliveira@example.com', '(31) 96543-2109');

-- Insert sample user
-- Password: 'senha123' (BCrypt hash with 10 rounds)
-- Hash: $2a$10$DXRKt4diF/lelVPndfbyUeXu2u.So7KOObqPKFowuopH7IMiW3Btm
INSERT INTO usuarios (id, username, email, password) VALUES
(UUID(), 'admin', 'admin@fazpay.com', '$2a$10$DXRKt4diF/lelVPndfbyUeXu2u.So7KOObqPKFowuopH7IMiW3Btm');

-- Insert sample vehicles (requires getting cliente IDs first)
INSERT INTO veiculos (id, placa, marca, modelo, ano, cor, cliente_id)
SELECT UUID(), 'ABC1234', 'Toyota', 'Corolla', 2022, 'Prata', id FROM clientes WHERE cpf = '12345678901'
UNION ALL
SELECT UUID(), 'XYZ5678', 'Honda', 'Civic', 2021, 'Preto', id FROM clientes WHERE cpf = '12345678901'
UNION ALL
SELECT UUID(), 'DEF9012', 'Volkswagen', 'Gol', 2023, 'Branco', id FROM clientes WHERE cpf = '98765432109'
UNION ALL
SELECT UUID(), 'GHI3456', 'Chevrolet', 'Onix', 2020, 'Vermelho', id FROM clientes WHERE cpf = '45678912301';

