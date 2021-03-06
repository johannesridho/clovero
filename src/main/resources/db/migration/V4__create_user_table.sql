CREATE TABLE IF NOT EXISTS user (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  line_id VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(255),
  state VARCHAR(255),
  point INT(10) UNSIGNED DEFAULT 0,
  current_category_id INT(10) UNSIGNED,
  current_quiz_id INT(10) UNSIGNED,
  current_hint_number INT(10) UNSIGNED DEFAULT 0,
  user_current_answer VARCHAR(255),
  current_quiz_wrong_answer_count INT(10) UNSIGNED DEFAULT 0,
  current_quiz_remaining_point INT(10) UNSIGNED DEFAULT 0,
  current_round_max_point INT(10) UNSIGNED DEFAULT 0,
  current_round_point INT(10) UNSIGNED DEFAULT 0,
  current_round_total_quiz_solved INT(10) UNSIGNED DEFAULT 0,
  created_at BIGINT UNSIGNED NOT NULL,
  updated_at BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (current_category_id) REFERENCES category(id),
  FOREIGN KEY (current_quiz_id) REFERENCES quiz(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
