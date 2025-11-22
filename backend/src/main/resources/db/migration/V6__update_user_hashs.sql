-- Fix user passwords with valid BCrypt hashes
UPDATE user SET password = '$2a$10$Qo6a486P2OWbQgvXTw59HuZzUIAolPlMYA2fh29f89IO2PDS/xYOC' WHERE email = 'rayssa@email.com';
UPDATE user SET password = '$2a$10$ED8Qp0fjAh/MEgSgY7g5MO4O5Ph5uC5TZ/OWFpbfjgRgbgmhAGaWW' WHERE email = 'joao@email.com';