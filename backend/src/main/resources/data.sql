-- Script de inicialização do banco de dados

-- Limpar dados existentes para recriar corretamente
DELETE FROM gastos WHERE id > 0;
DELETE FROM categories WHERE id > 0;

-- Inserção de categorias de exemplo
INSERT INTO categories (nome, descricao, created_at) VALUES 
('Alimentação', 'Gastos com comida e bebida', NOW()),
('Transporte', 'Gastos com transporte público, combustível, etc.', NOW()),
('Lazer', 'Gastos com entretenimento e diversão', NOW()),
('Saúde', 'Gastos com medicamentos e consultas', NOW()),
('Educação', 'Gastos com cursos, livros, etc.', NOW()),
('Casa', 'Gastos com moradia e utilidades', NOW()),
('Outros', 'Outros tipos de gastos', NOW());

-- Inserção de dados de exemplo para teste (valor, data e category_id)
INSERT INTO gastos (valor, data, category_id) VALUES 
(25.50, '2024-10-01 12:30:00', 1),
(15.00, '2024-10-01 08:15:00', 2),
(45.75, '2024-10-02 19:45:00', 3),
(120.00, '2024-10-03 20:00:00', 1),
(8.50, '2024-10-03 07:30:00', 2),
(80.00, '2024-10-04 14:20:00', 4),
(35.20, '2024-10-05 10:00:00', 5);