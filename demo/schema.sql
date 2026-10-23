-- ============================================================
-- PASSO 1: rode este script no MySQL (Workbench, DBeaver ou terminal)
-- ============================================================
CREATE DATABASE IF NOT EXISTS meu_db DEFAULT CHARACTER SET utf8;
USE meu_db;

-- Usuário(s) administrador(es) que fazem o primeiro login
    CREATE TABLE IF NOT EXISTS usuarios (
        id         INT AUTO_INCREMENT PRIMARY KEY,
        email      VARCHAR(150) NOT NULL UNIQUE,
        senha_hash VARCHAR(255) NOT NULL            -- HASH BCrypt, nunca a senha pura
    );

-- Pessoas cadastradas pela Tela de Cadastro
CREATE TABLE IF NOT EXISTS cadastros (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    endereco   VARCHAR(150) NOT NULL,
    cep        VARCHAR(9)   NOT NULL,
    numero     VARCHAR(10)  NOT NULL,
    telefone   VARCHAR(20)  NOT NULL,
    observacao TEXT,
    email      VARCHAR(150) NOT NULL UNIQUE,    -- UNIQUE: não permite email repetido
    senha_hash VARCHAR(255) NOT NULL,           -- HASH BCrypt, nunca a senha pura
    criado_em  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET = utf8;

-- ============================================================
-- PASSO 3 (depois de rodar o GerarHash.java):
-- cole aqui o INSERT que o GerarHash imprimiu no console. Exemplo:
--
-- INSERT INTO usuarios (email, senha_hash)
-- VALUES ('admin@email.com', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx');
-- ============================================================
