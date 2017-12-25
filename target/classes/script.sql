CREATE TABLE category
(
  id          INT AUTO_INCREMENT
    PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  category_id INT          NULL,
  CONSTRAINT fk_category_category1
  FOREIGN KEY (category_id) REFERENCES category (id)
)
  ENGINE = InnoDB;

CREATE INDEX fk_category_category1_idx
  ON category (category_id);

CREATE TABLE product
(
  id          INT AUTO_INCREMENT
    PRIMARY KEY,
  name        VARCHAR(45) NOT NULL,
  category_id INT         NOT NULL,
  CONSTRAINT fk_product_category
  FOREIGN KEY (category_id) REFERENCES category (id)
)
  ENGINE = InnoDB;

CREATE INDEX fk_product_category_idx
  ON product (category_id);


