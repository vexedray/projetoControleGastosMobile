CREATE TABLE expense (
    id LONG AUTO_INCREMENT PRIMARY KEY,
    value DECIMAL(10,2) NOT NULL,
    date DATETIME NOT NULL,
    category_id LONG,
    user_id LONG,
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id)
);