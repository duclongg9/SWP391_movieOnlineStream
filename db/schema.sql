-- ========================================================
-- Online-Film Platform · full schema & seed · 2025-07-22
-- ========================================================

DROP DATABASE IF EXISTS online_film;
CREATE DATABASE online_film DEFAULT CHARACTER SET utf8mb4;
USE online_film;

-- 1. USERS  ───────────────────────────────────────────────
CREATE TABLE users (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  email            VARCHAR(255) NOT NULL UNIQUE,
  username         VARCHAR(100) UNIQUE,
  full_name        VARCHAR(255),
  phone            VARCHAR(20),
  phone_verified   BOOLEAN       DEFAULT FALSE,
  otp_code         VARCHAR(10),
  otp_expire       DATETIME,
  profile_pic      VARCHAR(512),
  sso_provider     ENUM('google') NOT NULL,        -- user chỉ đăng nhập Google
  password_hash    VARCHAR(255),                   -- NULL cho USER SSO, bắt buộc ADMIN
  role             ENUM('USER','ADMIN') DEFAULT 'USER',
  point_balance    INT            DEFAULT 0,
  is_locked        BOOLEAN        DEFAULT FALSE,
  is_deleted       BOOLEAN        DEFAULT FALSE,
  created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- 2. MOVIES  ──────────────────────────────────────────────
CREATE TABLE movies (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  title            VARCHAR(255)  NOT NULL,
  genre            VARCHAR(100),
  cast             VARCHAR(255),
  video_path       VARCHAR(512),                     -- HLS (.m3u8) URL
  description      TEXT,
  duration_min     INT,
  encrypted        BOOLEAN        DEFAULT TRUE,
  is_deleted       BOOLEAN        DEFAULT FALSE,
  created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- 3. PACKAGES  ────────────────────────────────────────────
CREATE TABLE packages (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(120)  NOT NULL,
  description      TEXT,
  monthly_price    INT           NOT NULL,           -- đơn vị: Point / 30 ngày
  is_deleted       BOOLEAN       DEFAULT FALSE,
  created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- 3a. PACKAGE_MOVIES (n‒n)  ──────────────────────────────
CREATE TABLE package_movies (
  package_id       INT,
  movie_id         INT,
  PRIMARY KEY (package_id, movie_id),
  FOREIGN KEY (package_id) REFERENCES packages(id),
  FOREIGN KEY (movie_id)   REFERENCES movies(id)
);

-- 4. USER_PACKAGES (gói đã mua)  ─────────────────────────
CREATE TABLE user_packages (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  user_id          INT,
  package_id       INT,
  start_date       DATE,
  end_date         DATE,
  price_point      INT,                                -- ghi nhận khi mua
  FOREIGN KEY (user_id)    REFERENCES users(id),
  FOREIGN KEY (package_id) REFERENCES packages(id)
);

-- 5. TRANSACTIONS (top-up / mua / nâng cấp)  ─────────────
CREATE TABLE transactions (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  user_id          INT,
  type             ENUM('TOPUP','BUY_PACKAGE','UPGRADE_PACKAGE') NOT NULL,
  amount_point     INT,                                 -- + / − điểm
  meta             JSON,
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 6. PROMOTION (mã giảm gói)  ───────────────────────────
CREATE TABLE promotion (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  code             VARCHAR(100) UNIQUE,
  discount_pct     DECIMAL(5,2) CHECK (discount_pct BETWEEN 0 AND 100),
  target_package_id INT,
  valid_until      DATETIME,
  FOREIGN KEY (target_package_id) REFERENCES packages(id)
);

-- 7. WATCH_HISTORY  ──────────────────────────────────────
CREATE TABLE watch_history (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  user_id          INT,
  movie_id         INT,
  watched_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  duration_watched INT DEFAULT 0,      -- giây đã xem
  FOREIGN KEY (user_id)  REFERENCES users(id),
  FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- ========================================================
-- SAMPLE DATA
-- ========================================================

-- ADMIN (đăng nhập thủ công) + USER SSO
INSERT INTO users (email, username, full_name, sso_provider, password_hash, role, point_balance)
VALUES
  ('admin@example.com','admin','Administrator','google',
   '$2a$10$NHmg6V9NEPYjdfYgbn6the5aElERAvsJWGn9Fq5XStHAtxSVwH9.6','ADMIN',0),
  ('user1@example.com','user1','User One','google',
   NULL,'USER',120);

-- MOVIES
INSERT INTO movies (title, genre, cast, video_path, duration_min, description)
VALUES
  ('The Northman','Action','Alexander Skarsgård','/videos/northman.m3u8',137,'Viking revenge saga'),
  ('Doctor Strange in the Multiverse of Madness','Fantasy','Benedict Cumberbatch',
   '/videos/drstrange.m3u8',126,'Marvel Phase 4 adventure');

-- PACKAGES
INSERT INTO packages (name, description, monthly_price)
VALUES
  ('Basic','Access to basic catalogue',80),
  ('Premium','Access to all movies',150);

-- PACKAGE_MOVIES mapping
INSERT INTO package_movies (package_id, movie_id) VALUES
  (1,1),                 -- Basic → The Northman
  (2,1),(2,2);           -- Premium → cả 2 phim

-- USER_PACKAGES (user1 mua Basic hôm nay)
INSERT INTO user_packages (user_id, package_id, start_date, end_date, price_point)
VALUES
  (2,1,CURDATE(),DATE_ADD(CURDATE(), INTERVAL 30 DAY),80);

-- TRANSACTIONS
INSERT INTO transactions (user_id, type, amount_point, meta) VALUES
  (2,'TOPUP',+120, JSON_OBJECT('gateway','Momo')),
  (2,'BUY_PACKAGE',-80, JSON_OBJECT('package','Basic'));

-- WATCH_HISTORY
INSERT INTO watch_history (user_id, movie_id, duration_watched) VALUES
  (2,1,3600);

-- PROMOTION
INSERT INTO promotion (code, discount_pct, target_package_id, valid_until)
VALUES
  ('PROMO10',10.0,1, DATE_ADD(NOW(), INTERVAL 60 DAY));