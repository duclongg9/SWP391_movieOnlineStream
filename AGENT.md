# Agent Tổng Hợp – Online Film Service Platform (Servlet/JSP + HLS)

> **Mục tiêu**: Hợp nhất đặc tả từ dự án **MovieX (Next.js + Firebase)** vào nền tảng **Jakarta EE (Servlet/JSP)**, bổ sung streaming HLS, quản lý gói phim, công thức tính giá & nâng cấp gói.

---

## 0 · Stack & Công nghệ

| Lớp                 | Giải pháp                            |
| ------------------- | ------------------------------------ |
| **IDE**             | NetBeans                             |
| **Backend**         | Jakarta EE 10 (Servlet + JSP + JSTL) |
| **Web Server**      | Apache Tomcat 10                     |
| **Build**           | Ant                                  |
| **DB**              | MySQL 8 (InnoDB)                     |
| **Video**           | HLS (m3u8) + `hls.js` trên client    |
| **Auth**            | JWT + SSO (Google / Facebook)        |
| **Payment**         | VNPay, Momo (sandbox)                |
| **CDN & Streaming** | Origin + multi‑region CDN nodes      |

---

## 1 · Use Case Chính

| Mã              | Use Case                                                | Actor |
| --------------- | ------------------------------------------------------- | ----- |
| UC01            | Đăng nhập/Đăng ký (email + SSO)                         | User  |
| UC02            | Quản lý tài khoản                                       | User  |
| UC03            | Nạp tiền & Quy đổi Point                                | User  |
| UC04            | Mua gói xem phim                                        | User  |
| UC06            | Xem phim đã mua (HLS)                                   | User  |
| UC07            | Lịch sử giao dịch                                       | User  |
| UC08            | Tìm kiếm & Lọc phim                                     | User  |
| ADM‑01 → ADM‑06 | Quản trị nội dung, gói, người dùng, khuyến mãi, báo cáo | Admin |

---

## 2 · Mô Hình Dữ Liệu (SQL)

```sql
-- 2.1  Bảng người dùng
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email           VARCHAR(255) NOT NULL UNIQUE,
  username        VARCHAR(100) UNIQUE,
  full_name       VARCHAR(255),
  phone           VARCHAR(20),
  phone_verified  BOOLEAN DEFAULT FALSE,
  otp_code        VARCHAR(10),
  otp_expire      DATETIME,
  profile_pic     VARCHAR(512),
  sso_provider    ENUM('google') NOT NULL,
  password_hash   VARCHAR(255),               -- NULL cho user SSO, bắt buộc cho admin
  role            ENUM('USER','ADMIN') DEFAULT 'USER',
  point_balance   INT DEFAULT 0,
  is_locked       BOOLEAN DEFAULT FALSE,
  is_deleted      BOOLEAN DEFAULT FALSE,
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.2  Bảng phim
CREATE TABLE movies (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title       VARCHAR(255),
  genre       VARCHAR(100),
  cast        VARCHAR(255),
  video_path  VARCHAR(500),   -- URL HLS
  encrypted   BOOLEAN DEFAULT TRUE,
  is_deleted  BOOLEAN DEFAULT FALSE,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.3  Bảng gói phim
CREATE TABLE packages (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(120),
  description   TEXT,
  monthly_price INT,          -- giá / 30 ngày (Point)
  is_deleted    BOOLEAN DEFAULT FALSE,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.4  Liên kết phim ↔ gói (n‑n)
CREATE TABLE package_movies (
  package_id INT,
  movie_id   INT,
  PRIMARY KEY (package_id, movie_id),
  FOREIGN KEY (package_id) REFERENCES packages(id),
  FOREIGN KEY (movie_id)   REFERENCES movies(id)
);

-- 2.5  Gói đã mua của người dùng
CREATE TABLE user_packages (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT,
  package_id  INT,
  start_date  DATE,
  end_date    DATE,
  price_point INT,            -- điểm đã chi cho gói
  FOREIGN KEY (user_id)    REFERENCES users(id),
  FOREIGN KEY (package_id) REFERENCES packages(id)
);

-- 2.6  Lịch sử giao dịch (nạp / mua / nâng cấp)
CREATE TABLE transactions (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id      INT,
  type ENUM('TOPUP','BUY_PACKAGE','UPGRADE_PACKAGE') NOT NULL,
  amount_point INT,
  meta         JSON,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 2.7  Lịch sử xem
CREATE TABLE watch_history (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id          INT,
  movie_id         INT,
  watched_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  duration_watched INT DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (movie_id) REFERENCES movies(id)
);
```

## 3 · API Design (REST)

| Phân hệ     | Method & Path                             | Mô tả ngắn                       |
| ----------- | ----------------------------------------- | -------------------------------- |
| **Auth**    | `POST /api/auth/login`                    | Đăng nhập (email/JWT)            |
|             | `GET  /api/auth/sso/google`               | OAuth2 flow                      |
| **User**    | `GET /api/user/profile`                   | Trả profile                      |
| **Payment** | `POST /api/payment/topup`                 | Tạo yêu cầu VNPay/Momo           |
| **Point**   | `GET /api/point/balance`                  | Số dư                            |
| **Package** | `POST /api/purchase/package/{id}`         | Mua gói                          |
|             | `POST /api/purchase/package/{id}/upgrade` | Nâng cấp gói                     |
| **Movie**   | `GET /api/movie/{id}`                     | Chi tiết phim                    |
| **Stream**  | `GET /api/stream/url?movieId=`            | Trả HLS URL (kiểm quyền)         |
| **Admin**   | `POST /api/admin/package`                 | CRUD gói; tick chọn phim cho gói |

---

## 4 · Business Rules

1. **Kiểm quyền stream**: Chỉ trả `video_path` khi:
   - User có gói chứa phim còn hiệu lực.
2. **Tính giá gói**
   - *Tổng tiền* = `monthly_price × số_tháng` (1 tháng = 30 ngày)
3. **Nâng cấp gói**
   - `Giá_phải_trả = (monthly_price_new × số_tháng_mới) − (Số_ngày_còn_lại × monthly_price_old / 30)`
   - Nếu kết quả âm ⇒ 0 (không thu).
4. **Soft‑delete**: `is_deleted = TRUE` cho phim/gói thay vì xoá cứng.
5. **Mã hoá Video**: Phim được mã hoá theo key gói/user; giải mã phía CDN/Streaming khi hợp lệ.
6. **Ảnh đại diện (Profile Picture)**: Nếu `users.profile_pic` **NULL** nhưng `sso_provider` là `'google'` hoặc `'facebook'`, frontend sẽ lấy URL avatar (`picture` claim của ID Token OAuth2 hoặc Graph API) để hiển thị, đồng thời có thể đẩy job async ghi lại vào DB. Nếu cả hai trường NULL ⇒ hiển thị placeholder mặc định. hoá Video\*\*: Phim được mã hoá theo key gói/user; giải mã phía CDN/Streaming khi hợp lệ.

---

## 5 · Luồng Quản trị Gói Phim

1. **Danh sách gói** → **Tạo mới / Sửa**.
2. Trong form gói, checkbox đa lựa chọn danh sách phim (AJAX search).
3. Lưu `packages` + bảng `package_movies`.
4. Tự tính `monthly_price` khi admin nhập giá từng phim hoặc nhập tay.

---

## 6 · Frontend JSP

- Trang **movie.jsp** nhúng `<video id="player" controls>` + script `hls.js`.
- Gọi API `/api/stream/url?movieId=...`, lấy JSON `{url}` → load vào Hls instance.

---

## 7 · Bước Triển khai

1. **DB Migration**: thêm `video_path`, tạo bảng `packages`, `package_movies`, `user_packages`, `transactions`.
2. **DAO Layer**: MovieDAO, PackageDAO, TransactionDAO.
3. **Servlet**: `StreamController`, `PackageController`, `AdminPackageController`.
4. **JSP**: giao diện chọn phim cho gói & player.
5. **Payment IPN**: servlet nhận callback, update `transactions`, cộng point.
6. **CDN Config**: đồng bộ HLS với hashed token.

---

## 8 · Bảo mật & Hiệu năng

- Hash mật khẩu (BCrypt), HTTPS bắt buộc.
- Tất cả API bảo vệ JWT filter.
- Sử dụng `Cache-Control: max-age=...` cho HLS segment.
- Thêm chỉ số `INDEX` cho trường tìm kiếm (title, genre).

---

## 9 · Đồng bộ & Hiển thị Avatar

### 9.1 Luồng Login SSO

1. Servlet callback (`/api/auth/sso/callback`) lấy `id_token` → verify.
2. Trích `picture` claim.
3. Ghi vào **session**
   ```java
   session.setAttribute("avatarUrl", claims.get("picture"));
   ```
4. Nếu cột `users.profile_pic` **NULL** ⇢ cập nhật một lần:
   ```java
   UserDAO.updateProfilePic(userId, claims.get("picture"));
   // SQL: UPDATE users SET profile_pic=? 
   //       WHERE id=? AND profile_pic IS NULL LIMIT 1;
   ```

### 9.2 `AvatarFilter` (JEE Filter)

```java
public class AvatarFilter implements Filter {
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpSession session = ((HttpServletRequest) req).getSession(false);
    String avatar = session != null ? (String) session.getAttribute("avatarUrl") : null;
    if (avatar == null && session != null) {
        User u = (User) session.getAttribute("authUser");
        avatar = u != null ? u.getProfilePic() : null;
    }
    if (avatar == null) avatar = req.getServletContext().getContextPath() + "/assets/img/avatar-placeholder.png";
    req.setAttribute("avatarUrl", avatar);
    chain.doFilter(req, res);
  }
}
```

- Frontend JSP hiển thị: `<img src="${avatarUrl}" class="avatar"/>`

### 9.3 Kiểm thử

| Tình huống     | DB `profile_pic` | Token `picture` | Kết quả hiển thị         | DB sau login |
| -------------- | ---------------- | --------------- | ------------------------ | ------------ |
| New SSO user   | NULL             | Có              | Avatar từ token          | Cập nhật URL |
| Trở lại SSO    | Có               | Có              | Avatar DB (hoặc session) | Giữ nguyên   |
| Email/Password | NULL             | Không           | Placeholder              | Không đổi    |

> **Tip**: Có thể bật CRON định kỳ làm “avatar hygiene” để kiểm tra URL hỏng và refetch.

---

**© 2025 Online Film Team** – Phát triển & tùy biến theo nhu cầu.

---

## 10 · Chính sách Đăng nhập & Quyền truy cập

- **Người dùng**: Chỉ **đăng nhập qua Google OAuth2 (SSO)**; không còn trang đăng ký (`/signup`). Lần đầu login sẽ tự tạo hồ sơ trong bảng `users`.
- **Admin**: Đăng nhập thủ công (email/password) tại `/admin/login`; chỉ `role = 'ADMIN'` mới yêu cầu `password_hash`.
- **API**: Đã gỡ bỏ route `/api/auth/signup`. Servlet `/api/auth/sso/callback` kiêm luôn nhiệm vụ "đăng ký" lần đầu.
- **Schema**: Cột `password_hash` `NULL` cho user thường; `NOT NULL` cho admin.
- **Session/AuthFilter**: Nếu URL bắt đầu `/admin`, dùng ManualAuthenticator; ngược lại, buộc SSO token.
- **Frontend**: Trang login hiển thị duy nhất nút **"Sign in with Google"** cho khách; liên kết `/admin/login` riêng cho admin.

