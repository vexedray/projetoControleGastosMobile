CREATE TABLE expense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    date DATETIME NOT NULL,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);