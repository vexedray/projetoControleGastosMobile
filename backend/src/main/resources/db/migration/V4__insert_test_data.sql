INSERT INTO user (name, email, password, created_at) VALUES
  ('Rayssa Almeida', 'rayssa@email.com', 'senha123', NOW()),
  ('João Silva', 'joao@email.com', 'senha456', NOW());

INSERT INTO categories (name, description, created_at) VALUES
  ('Food', 'Food expenses', NOW()),
  ('Transport', 'Transport expenses', NOW());

INSERT INTO expense (value, date, category_id, user_id) VALUES
  (50.00, NOW(), 1, 1),
  (20.00, NOW(), 2, 2);