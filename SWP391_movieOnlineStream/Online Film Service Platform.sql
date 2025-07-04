Create database online_film;
use online_film;
CREATE TABLE users (
    user_id CHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role ENUM('User', 'Admin'),
    status BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wallet (
    wallet_id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    balance_point INT DEFAULT 0,
    last_recharged DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE transaction (
    transaction_id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    amount_vnd DECIMAL(12,2),
    point_earned INT,
    gateway_status VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE film (
    film_id CHAR(36) PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    genre VARCHAR(100),
    duration_min INT,
    price_point INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE package (
    package_id CHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    duration_days INT,
    price_point INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE package_film (
    id CHAR(36) PRIMARY KEY,
    package_id CHAR(36),
    film_id CHAR(36),
    FOREIGN KEY (package_id) REFERENCES package(package_id),
    FOREIGN KEY (film_id) REFERENCES film(film_id)
);

CREATE TABLE user_purchase (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    film_id CHAR(36),
    package_id CHAR(36),
    purchased_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expired_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (film_id) REFERENCES film(film_id),
    FOREIGN KEY (package_id) REFERENCES package(package_id)
);

CREATE TABLE promotion (
    promo_id CHAR(36) PRIMARY KEY,
    code VARCHAR(100) UNIQUE,
    discount_pct DECIMAL(5,2),
    apply_to ENUM('gói', 'phim'),
    target_id CHAR(36),
    valid_until DATETIME,
    FOREIGN KEY (target_id) REFERENCES film(film_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE watch_history (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    film_id CHAR(36),
    watched_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (film_id) REFERENCES film(film_id)
);