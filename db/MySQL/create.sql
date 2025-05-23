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
