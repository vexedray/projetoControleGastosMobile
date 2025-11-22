-- Update user passwords to BCrypt hash
-- BCrypt hash of 'senha123': $2a$10$N9qo8uLOickgx2ZMRZoMye6Y.zVXdnXVf6jGj9i.3nJQ3VXQK4YF2
-- BCrypt hash of 'senha456': $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi

UPDATE user SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMye6Y.zVXdnXVf6jGj9i.3nJQ3VXQK4YF2' WHERE email = 'rayssa@email.com';
UPDATE user SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi' WHERE email = 'joao@email.com';
