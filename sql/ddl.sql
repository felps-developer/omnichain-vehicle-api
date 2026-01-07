-- Database Creation
CREATE DATABASE IF NOT EXISTS vehicle_api_db;
USE vehicle_api_db;

-- Customers Table (with audit and soft delete)
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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

-- Users Table (for authentication with relationship to customer)
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    cliente_id BIGINT UNIQUE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    INDEX idx_username (username)
);

-- Vehicles Table (with audit and soft delete)
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
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    INDEX idx_placa (placa),
    INDEX idx_cliente_id (cliente_id)
);

-- Sample Data for Testing

-- Insert sample customers
INSERT INTO clientes (nome, cpf, email, telefone) VALUES
('João Silva', '12345678901', 'joao.silva@example.com', '(11) 98765-4321'),
('Maria Santos', '98765432109', 'maria.santos@example.com', '(21) 97654-3210'),
('Pedro Oliveira', '45678912301', 'pedro.oliveira@example.com', '(31) 96543-2109');

-- Insert sample users (password: 'password123' encrypted with BCrypt)
-- Note: You should generate these passwords using BCryptPasswordEncoder
-- Example: new BCryptPasswordEncoder().encode("password123")
INSERT INTO usuarios (username, password, cliente_id) VALUES
('joao', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1),
('maria', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2),
('pedro', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 3);

-- Insert sample vehicles
INSERT INTO veiculos (placa, marca, modelo, ano, cor, cliente_id) VALUES
('ABC1234', 'Toyota', 'Corolla', 2022, 'Prata', 1),
('XYZ5678', 'Honda', 'Civic', 2021, 'Preto', 1),
('DEF9012', 'Volkswagen', 'Gol', 2023, 'Branco', 2),
('GHI3456', 'Chevrolet', 'Onix', 2020, 'Vermelho', 3);

