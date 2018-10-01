CREATE TABLE quiz_solver (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id INT(10) UNSIGNED NOT NULL,
    quiz_id INT(10) UNSIGNED NOT NULL,
    created_at BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (quiz_id) REFERENCES quiz(id),
    UNIQUE (user_id, quiz_id)
) ENGINE = InnoDB DEFAULT CHARSET=utf8
