CREATE TABLE expense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(10,2) NOT NULL,
    data DATETIME NOT NULL,
    category_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES user(id)
);