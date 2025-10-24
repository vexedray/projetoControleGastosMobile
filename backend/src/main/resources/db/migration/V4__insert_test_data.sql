-- Inserts para tabela user
INSERT INTO user (nome, email, senha, created_at) VALUES
  ('Rayssa Almeida', 'rayssa@email.com', 'senha123', NOW()),
  ('João Silva', 'joao@email.com', 'senha456', NOW());

-- Inserts para tabela category
INSERT INTO category (nome, descricao, created_at) VALUES
  ('Alimentação', 'Gastos com comida', NOW()),
  ('Transporte', 'Gastos com transporte', NOW());

-- Inserts para tabela expense
INSERT INTO expense (valor, data, category_id, user_id) VALUES
  (50.00, NOW(), 1, 1),
  (20.00, NOW(), 2, 2),
  (100.00, NOW(), 1, 2);