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