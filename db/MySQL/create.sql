CREATE DATABASE IF NOT EXISTS Sistema;

USE Sistema;

CREATE TABLE Usuario (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         nome VARCHAR(100) NOT NULL,
                         login VARCHAR(100) NOT NULL UNIQUE,
                         senha VARCHAR(150) NOT NULL,
                         role ENUM('ADMIN', 'TESTER', 'GUEST') NOT NULL DEFAULT 'GUEST',
                         PRIMARY KEY (id)
);

INSERT INTO Usuario (nome, login, senha, role) VALUES ('Administrador', 'admin', 'admin', 'ADMIN');

INSERT INTO Usuario (nome, login, senha, role) VALUES ('Testador', 'tester', 'tester', 'TESTER');


CREATE TABLE Projeto (
                         id INT NOT NULL AUTO_INCREMENT,
                         nome VARCHAR(255) NOT NULL,
                         descricao TEXT,
                         criadoEm DATETIME NOT NULL,
                         PRIMARY KEY (id)
);

CREATE TABLE Projeto_Usuario (
                                 projeto_id INT NOT NULL,
                                 usuario_id BIGINT NOT NULL,
                                 PRIMARY KEY (projeto_id, usuario_id),
                                 FOREIGN KEY (projeto_id) REFERENCES Projeto(id) ON DELETE CASCADE,
                                 FOREIGN KEY (usuario_id) REFERENCES Usuario(id) ON DELETE CASCADE
);

CREATE TABLE Estrategia (
                            id INT NOT NULL AUTO_INCREMENT,
                            nome VARCHAR(255) NOT NULL,
                            descricao TEXT,
                            dicas TEXT,
                            PRIMARY KEY (id)
);

CREATE TABLE Imagem (
                        id INT NOT NULL AUTO_INCREMENT,
                        estrategia_id INT NOT NULL,
                        url VARCHAR(1024) NOT NULL, -- Increased URL length
                        descricao VARCHAR(255),
                        PRIMARY KEY (id),
                        FOREIGN KEY (estrategia_id) REFERENCES Estrategia(id) ON DELETE CASCADE
);

CREATE TABLE Sessao (
                        id INT NOT NULL AUTO_INCREMENT,
                        titulo VARCHAR(255) NOT NULL,
                        descricao TEXT,
                        testador_id BIGINT NOT NULL,
                        estrategia_id INT NOT NULL,
                        projeto_id INT NOT NULL, -- <<< NEW COLUMN
                        status ENUM('CRIADA', 'EM_ANDAMENTO', 'FINALIZADA', 'CANCELADA') NOT NULL DEFAULT 'CRIADA',
                        criadoEm DATETIME NOT NULL,
                        inicioEm DATETIME NULL,
                        finalizadoEm DATETIME NULL,
                        PRIMARY KEY (id),
                        FOREIGN KEY (testador_id) REFERENCES Usuario(id) ON DELETE RESTRICT, -- Or ON DELETE SET NULL if appropriate
                        FOREIGN KEY (estrategia_id) REFERENCES Estrategia(id) ON DELETE RESTRICT, -- Or ON DELETE SET NULL
                        FOREIGN KEY (projeto_id) REFERENCES Projeto(id) ON DELETE CASCADE -- If a project is deleted, its sessions are deleted
);