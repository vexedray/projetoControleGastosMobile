-- Script para resetar o banco de dados
-- Execute este script no MySQL antes de rodar a aplicação

DROP DATABASE IF EXISTS expense_control;
CREATE DATABASE expense_control;
USE expense_control;

-- O Flyway irá criar as tabelas automaticamente
