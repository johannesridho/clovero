CREATE TABLE IF NOT EXISTS hint (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  quiz_id INT(10) UNSIGNED,
  number INT(10),
  type VARCHAR(32),
  description VARCHAR(255),
  original_image_url VARCHAR(255),
  preview_image_url VARCHAR(255),
  audio_url VARCHAR(255),
  audio_duration INT(10),
  created_at BIGINT UNSIGNED NOT NULL,
  updated_at BIGINT UNSIGNED NOT NULL,
  UNIQUE (quiz_id, number),
  PRIMARY KEY (id),
  FOREIGN KEY (quiz_id) REFERENCES quiz(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
