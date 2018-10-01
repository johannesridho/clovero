CREATE TABLE IF NOT EXISTS quiz (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  answer VARCHAR(255),
  description VARCHAR(255),
  point INT(10) UNSIGNED,
  category_id INT(10) UNSIGNED,
  created_at BIGINT UNSIGNED NOT NULL,
  updated_at BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
