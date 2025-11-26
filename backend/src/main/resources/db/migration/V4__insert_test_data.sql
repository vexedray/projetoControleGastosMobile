-- Insert test user with BCrypt password
-- Rayssa: senha123
INSERT INTO user (name, email, password, created_at) VALUES
  ('Rayssa Almeida', 'rayssa@email.com', '$2a$10$Qo6a486P2OWbQgvXTw59HuZzUIAolPlMYA2fh29f89IO2PDS/xYOC', NOW());

-- Insert categories for Rayssa (user_id 1)
INSERT INTO categories (name, description, user_id, created_at) VALUES
  ('Food', 'Food expenses', 1, NOW()),
  ('Transport', 'Transport expenses', 1, NOW()),
  ('Health', 'Health expenses', 1, NOW()),
  ('Entertainment', 'Entertainment expenses', 1, NOW()),
  ('Shopping', 'Shopping expenses', 1, NOW());

-- Insert expenses for Rayssa (user_id 1, categories 1-5)
INSERT INTO expense (description, amount, date, category_id, user_id) VALUES
  ('Lunch at restaurant', 50.00, DATE_SUB(NOW(), INTERVAL 1 DAY), 1, 1),
  ('Grocery shopping', 150.00, DATE_SUB(NOW(), INTERVAL 2 DAY), 1, 1),
  ('Uber to work', 25.00, DATE_SUB(NOW(), INTERVAL 1 DAY), 2, 1),
  ('Movie tickets', 60.00, DATE_SUB(NOW(), INTERVAL 3 DAY), 4, 1),
  ('Pharmacy', 45.50, DATE_SUB(NOW(), INTERVAL 2 DAY), 3, 1),
  ('New shoes', 200.00, DATE_SUB(NOW(), INTERVAL 4 DAY), 5, 1);
