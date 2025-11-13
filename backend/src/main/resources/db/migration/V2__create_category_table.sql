CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    color VARCHAR(7),
    icon VARCHAR(50),
    created_at DATETIME NOT NULL
);