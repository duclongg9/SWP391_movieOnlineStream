-- Schema for Online Film Service Platform
CREATE DATABASE IF NOT EXISTS online_film DEFAULT CHARACTER SET utf8mb4;
USE online_film;

-- User Table
CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  sso_provider ENUM('google','facebook') NULL,
  point_balance INT DEFAULT 0,
  is_locked BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Movie Table
CREATE TABLE IF NOT EXISTS movies (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255),
  genre VARCHAR(100),
  actor VARCHAR(255),
  price_point INT,
  description TEXT,
  duration_min INT,
  is_deleted BOOLEAN DEFAULT FALSE,
  encrypted BOOLEAN DEFAULT TRUE
);

-- Wallet Table
CREATE TABLE IF NOT EXISTS wallet (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  balance_point INT DEFAULT 0,
  last_recharged DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Transaction Table
CREATE TABLE IF NOT EXISTS transaction (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  amount_vnd DECIMAL(12,2),
  point_earned INT,
  gateway_status VARCHAR(100),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Package Table
CREATE TABLE IF NOT EXISTS package (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  description TEXT,
  duration_days INT,
  price_point INT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Package-Film Mapping
CREATE TABLE IF NOT EXISTS package_film (
  id INT PRIMARY KEY AUTO_INCREMENT,
  package_id INT,
  film_id INT,
  FOREIGN KEY (package_id) REFERENCES package(id),
  FOREIGN KEY (film_id) REFERENCES movies(id)
);

-- User Purchase Table
CREATE TABLE IF NOT EXISTS user_purchase (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  film_id INT,
  package_id INT,
  purchased_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  expired_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (film_id) REFERENCES movies(id),
  FOREIGN KEY (package_id) REFERENCES package(id)
);

-- Promotion Table
CREATE TABLE IF NOT EXISTS promotion (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(100) UNIQUE,
  discount_pct DECIMAL(5,2),
  apply_to ENUM('goi', 'phim'),
  target_id INT,
  valid_until DATETIME,
  FOREIGN KEY (target_id) REFERENCES movies(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Watch History Table
CREATE TABLE IF NOT EXISTS watch_history (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  film_id INT,
  watched_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (film_id) REFERENCES movies(id)
);

-- Sample data
INSERT INTO movies(title, genre, actor, price_point, description, duration_min) VALUES
  ('The Northman','Action','Alexander Skarsgård',50,'','0'),
  ('Doctor Strange in the Multiverse of Madness','Fantasy','Benedict Cumberbatch',60,'','0');