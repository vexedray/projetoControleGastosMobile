INSERT INTO user (name, email, password, created_at) VALUES
  ('Rayssa Almeida', 'rayssa@email.com', 'senha123', NOW()),
  ('João Silva', 'joao@email.com', 'senha456', NOW());

INSERT INTO categories (name, description, created_at) VALUES
  ('Food', 'Food expenses', NOW()),
  ('Transport', 'Transport expenses', NOW()),
  ('Health', 'Health expenses', NOW());

INSERT INTO expense (description, amount, date, category_id, user_id) VALUES
  ('Lunch', 50.00, NOW(), 1, 1),
  ('Bus ticket', 20.00, NOW(), 2, 2),
  ('Dinner', 100.00, NOW(), 1, 2),
  ('Doctor appointment', 75.50, NOW(), 3, 1);