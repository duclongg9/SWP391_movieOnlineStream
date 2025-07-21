-- Schema for Online Film Service Platform
DROP DATABASE IF EXISTS online_film;
CREATE DATABASE IF NOT EXISTS online_film DEFAULT CHARACTER SET utf8mb4;
USE online_film;

-- User Table (Thêm role cho admin)
CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE,
  full_name VARCHAR(255),
  phone VARCHAR(20),
  phone_verified BOOLEAN DEFAULT FALSE,
  otp_code VARCHAR(10),
  otp_expire DATETIME,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  profile_pic VARCHAR(512),
  sso_provider ENUM('google','facebook') NULL,
  point_balance INT DEFAULT 0,
  is_locked BOOLEAN DEFAULT FALSE,
  is_deleted BOOLEAN DEFAULT FALSE,
  role ENUM('user','admin') DEFAULT 'user',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Movie Table
CREATE TABLE IF NOT EXISTS movies (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255),
  genre VARCHAR(100),
  actor VARCHAR(255),
  video_path VARCHAR(255),
  price_point INT,
  description TEXT,
  duration_min INT,
  is_deleted BOOLEAN DEFAULT FALSE,
  encrypted BOOLEAN DEFAULT TRUE
);

-- Wallet Table (Hợp nhất vào users.point_balance nếu có thể, nhưng giữ nếu cần log recharge)
CREATE TABLE IF NOT EXISTS wallet (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  balance_point INT DEFAULT 0,
  last_recharged DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Transaction Table (Thêm type cho phân biệt nạp/mua)
CREATE TABLE IF NOT EXISTS transaction (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  type ENUM('topup','purchase') DEFAULT 'topup',
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
  is_deleted BOOLEAN DEFAULT FALSE,
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

-- Promotion Table (Sửa foreign key linh hoạt: target_id general, dùng apply_to phân biệt)
CREATE TABLE IF NOT EXISTS promotion (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(100) UNIQUE,
  discount_pct DECIMAL(5,2),
  apply_to ENUM('goi', 'phim'),
  target_type ENUM('package','movie'),
  target_id INT,
  valid_until DATETIME
);

-- Watch History Table (Thêm duration_watched cho báo cáo)
CREATE TABLE IF NOT EXISTS watch_history (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  film_id INT,
  watched_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  duration_watched INT DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (film_id) REFERENCES movies(id)
);

-- Sample data
INSERT INTO movies(title, genre, actor, video_path, price_point, description, duration_min) VALUES
  ('The Northman','Action','Alexander Skarsgård','/videos/northman.mp4',50,'','0'),
  ('Doctor Strange in the Multiverse of Madness','Fantasy','Benedict Cumberbatch','/videos/drstrange.mp4',60,'','0');

INSERT INTO users (username, full_name, phone, phone_verified, email, password, profile_pic, role, point_balance, is_deleted)
VALUES
    ('admin','Administrator','0900000000',1,'admin@example.com','0192023a7bbd73250516f069df18b500',NULL,'admin',0,0),
    ('user1','User One','0900000001',0,'user1@example.com','0192023a7bbd73250516f069df18b500',NULL,'user',0,0);

-- Sample data for wallet
INSERT INTO wallet (user_id, balance_point, last_recharged)
VALUES
  (1,100,NOW()),
  (2,200,NOW());

-- Sample data for transaction
INSERT INTO transaction (user_id, amount_vnd, point_earned, gateway_status)
VALUES
  (1,100000,100,'SUCCESS'),
  (2,200000,200,'SUCCESS');

-- Sample data for package
INSERT INTO package (name, description, duration_days, price_point)
VALUES
  ('Basic Package','Access to basic movies',30,80),
  ('Premium Package','Access to all movies',30,150);

-- Sample data for package_film
INSERT INTO package_film (package_id, film_id)
VALUES
  (1,1),
  (2,1),
  (2,2);

-- Sample data for user_purchase
INSERT INTO user_purchase (user_id, film_id, package_id, expired_at)
VALUES
  (1,NULL,1, DATE_ADD(NOW(), INTERVAL 30 DAY)),
  (2,2,NULL, DATE_ADD(NOW(), INTERVAL 1 DAY));

-- Sample data for promotion
INSERT INTO promotion (code, discount_pct, apply_to, target_type, target_id, valid_until)
VALUES
  ('PROMO10',10.0,'goi','package',1, DATE_ADD(NOW(), INTERVAL 60 DAY));

-- Sample data for watch_history
INSERT INTO watch_history (user_id, film_id)
VALUES
  (1,1),
  (2,2);