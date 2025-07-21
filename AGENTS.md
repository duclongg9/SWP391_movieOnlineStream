# Codex Agent Backend and SQL Instruction for "Online Film Service Platform"

## 0. Development Stack
- **IDE:** NetBeans
- **Technology:** Jakarta EE (Servlet + JSP)
- **Web Server:** Apache Tomcat
- **Language:** Java
- **Frontend:** JSP + JSTL (nếu cần)
- **Build Tool:** Ant (không dùng Maven)

## 1. User Flow Instructions (Luồng nghiệp vụ theo Use Case)

### UC01 - Đăng nhập/Đăng ký (User)
- Nhận request: email/password hoặc Google/Facebook token
- Kiểm tra:
  - Đăng nhập bằng SSO: redirect đến OAuth2 provider
  - Đăng ký: tạo user trong DB nếu chưa tồn tại
- Trả về JWT token để xác thực sau này
- Mọi mật khẩu đều được mã hóa (hash) khi lưu trữ

### UC03 - Nạp tiền & Quy đổi Point
- User chọn số tiền để nạp
- Redirect tới Payment Gateway (sandbox VNPay/Momo)
- Nhận kết quả từ Gateway (success/fail)
- Nếu thanh toán OK: ghi nhật ký giao dịch, quy đổi Point theo tỷ lệ tối đa của hệ thống (1 Point = 1000 VND chẳng hạn)

### UC04 - Mua gói xem phim
- Kiểm tra user đã đăng nhập
- Kiểm tra số Point >= giá gói phim
- Ghi giao dịch mua, trừ Point tương ứng
- Kết nối gói phim với user trong DB
- Các phim thuộc gói được mã hóa theo quyền user đã mua

### UC05 - Mua phim lẻ
- Giống UC04 nhưng áp dụng cho phim lẻ
- Nội dung phim được mã hóa tương ứng với key của khách hàng đã mua

### UC06 - Xem phim đã mua
- Kiểm tra user đã mua phim/gói phim hay chưa
- Gửi yêu cầu tới Streaming API để lấy link
- Streaming API trả về URL video đã được mã hóa (decrypt dựa theo gói)

### UC07 - Lịch sử giao dịch
- Truy vấn transaction table: nạp tiền, mua phim, mua gói

### UC08 - Tìm kiếm phim
- Nhận keyword: tên, thể loại, diễn viên
- Dùng LIKE/REGEXP truy vấn phim theo tiêu chí
- Có thể lọc theo thể loại (genre) để phù hợp sở thích người dùng

### UC02 (Admin) - Quản lý phim
- CRUD trên phim:
  - Thêm phim mới: insert record
  - Sửa: update theo ID
  - Xoá: soft delete bằng cách đặt `is_deleted = TRUE`

### UC03 (Admin) - Quản lý gói
- CRUD gói xem phim

### UC04 (Admin) - Quản lý user
- List all, khoá, mở khoá, xoá (xóa mềm qua `is_locked`)
- Có quyền thay đổi trạng thái hoạt động tài khoản

### UC06 (Admin) - Xem báo cáo doanh thu
- Truy vấn giao dịch theo ngày/tháng
- Tính tổng Point đã nạp/mua phim

## 2. Backend Structure (RESTful API)

### Auth
```
POST /api/auth/login
POST /api/auth/register
GET /api/auth/sso/google
GET /api/auth/sso/facebook
```

### User
```
GET /api/user/profile
PUT /api/user/profile
POST /api/user/change-password
```

### Payment
```
POST /api/payment/topup
GET /api/payment/callback?vnp_ResponseCode=00
```

### Point
```
GET /api/point/balance
POST /api/point/convert
```

### Purchase
```
POST /api/purchase/package/:id
POST /api/purchase/movie/:id
GET /api/purchase/history
```

### Movie
```
GET /api/movie/search?q=ten
GET /api/movie/:id
```

### Stream
```
GET /api/stream/url?movieId=xxx
POST /api/stream/watchlog
```

### Admin
```
POST /api/admin/movie
PUT /api/admin/movie/:id
DELETE /api/admin/movie/:id

POST /api/admin/package
PUT /api/admin/package/:id
DELETE /api/admin/package/:id

GET /api/admin/users
PATCH /api/admin/user/:id/lock
PATCH /api/admin/user/:id/unlock

GET /api/admin/report?from=dd-mm&to=dd-mm
```

## 3. SQL Table Design (core logic)

```sql
-- User Table
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255), -- đã hash trước khi lưu
  sso_provider ENUM('google','facebook') NULL,
  point_balance INT DEFAULT 0,
  is_locked BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Movie Table
CREATE TABLE movies (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255),
  genre VARCHAR(100),
  actor VARCHAR(255),
  price_point INT,
  is_deleted BOOLEAN DEFAULT FALSE,
  encrypted BOOLEAN DEFAULT TRUE
);

-- Thêm các bảng và dữ liệu mẫu như trước giữ nguyên...
```

## Ghi chú
- Tỷ lệ quy đổi Point có thể được đặt trong config (hoặc table settings)
- Trạng thái phim/gói: dùng cột `is_deleted` thay cho DELETE thật
- Xóa khách hàng là xóa mềm (chỉ đánh dấu)
- Khách hàng sẽ được kích hoạt tự động sau khi đăng ký thành công
- Admin có thể chỉnh sửa trạng thái người dùng (khóa/mở)
- Tất cả dữ liệu nhạy cảm như mật khẩu, token phải được **mã hóa/hash**
- Phim được **mã hóa theo gói hoặc mã người dùng**, chỉ được giải mã khi user có quyền xem
- Hệ thống hỗ trợ lọc phim theo **thể loại**, phục vụ mục đích phân loại và đề xuất
# Hướng dẫn thêm chức năng hiển thị phim trong dự án Java

File này tổng hợp các bước chính để thêm tính năng phát phim (stream HLS) vào dự án Java hiện tại, dựa trên phần mẫu ở thư mục `movie_test`.

## 1. Thêm trường `video_path` cho bảng phim
- Mở file SQL tạo bảng `movies` và bổ sung cột `video_path` (URL tới file HLS/m3u8).
- Trường này lưu đường dẫn HLS cố định, ví dụ `https://cdn.example.com/movies/abc/index.m3u8`.
- Khi tạo hoặc cập nhật phim trong Admin, cần lưu giá trị này vào DB qua `MovieDAO`.

## 2. Cập nhật `MovieAdminController`
- Trong phương thức tạo/sửa phim, lấy tham số `videoPath` từ form và gán vào đối tượng `Movie`.
- Gọi `MovieDAO.insert()` hoặc `update()` để lưu `videoPath` xuống DB.

## 3. Tạo API lấy URL stream
- Thêm Servlet `StreamController` (nếu chưa có) với endpoint `/api/stream/url`.
- Kiểm tra user đã mua phim hoặc gói chưa (`PurchaseDAO.hasAccessToFilm`). Nếu chưa, trả về lỗi 403.
- Nếu hợp lệ, trả về JSON chứa link HLS đã lưu trong `video_path`.
- Ví dụ request: `GET /api/stream/url?movieId=5`.

```java
out.write("{\"url\":\"" + movie.getVideoPath() + "\"}");
```

## 4. Giao diện phát phim (Frontend)
- Sử dụng component `VideoPlayer` ở `movie_test` (dùng thư viện `hls.js`).
- Khi mở trang xem phim, gọi API `/api/stream/url?movieId=...` để lấy link.
- Gửi link đó vào `VideoPlayer` để phát.

## 5. Kiểm tra quyền truy cập
- Mọi request tới API stream cần xác thực token đăng nhập.
- Chỉ cho phép xem phim nếu user đã mua phim/gói tương ứng.

## 6. Ghi nhật ký xem phim
- Tùy yêu cầu, có thể thêm API `/api/stream/watchlog` để lưu lại thời gian xem, phục vụ gợi ý hoặc thống kê.


4 · Business Rules

Kiểm quyền stream: Chỉ trả video_path khi:

User đã mua phim lẻ HOẶC

User có gói chứa phim còn hiệu lực.

Tính giá gói

Tổng tiền = monthly_price × số_tháng

Nâng cấp gói

Giá_phải_trả = Giá_gói_mới − (Số_ngày_còn lại × Giá_gói_cũ / 30)

Soft‑delete: is_deleted = TRUE cho phim/gói thay vì xoá cứng.

Mã hoá Video: Phim được mã hoá theo key gói/user; giải mã phía CDN/Streaming khi hợp lệ.

Ảnh đại diện (Profile Picture): Nếu users.profile_pic NULL nhưng sso_provider là 'google' hoặc 'facebook', frontend sẽ lấy URL avatar (picture claim của ID Token OAuth2 hoặc Graph API) để hiển thị, đồng thời có thể đẩy job async ghi lại vào DB. Nếu cả hai trường NULL ⇒ hiển thị placeholder mặc định. hoá Video**: Phim được mã hoá theo key gói/user; giải mã phía CDN/Streaming khi hợp lệ.

5 · Luồng Quản trị Gói Phim

Danh sách gói → Tạo mới / Sửa.

Trong form gói, checkbox đa lựa chọn danh sách phim (AJAX search).

Lưu packages + bảng package_movies.

Tự tính monthly_price khi admin nhập giá từng phim hoặc nhập tay.

6 · Frontend JSP

Trang movie.jsp nhúng <video id="player" controls> + script hls.js.

Gọi API /api/stream/url?movieId=..., lấy JSON {url} → load vào Hls instance.

7 · Bước Triển khai

DB Migration: thêm video_path, tạo bảng packages, package_movies, user_packages, transactions.

DAO Layer: MovieDAO, PackageDAO, TransactionDAO.

Servlet: StreamController, PackageController, AdminPackageController.

JSP: giao diện chọn phim cho gói & player.

Payment IPN: servlet nhận callback, update transactions, cộng point.

CDN Config: đồng bộ HLS với hashed token.

8 · Bảo mật & Hiệu năng

Hash mật khẩu (BCrypt), HTTPS bắt buộc.

Tất cả API bảo vệ JWT filter.

Sử dụng Cache-Control: max-age=... cho HLS segment.

Thêm chỉ số INDEX cho trường tìm kiếm (title, genre).
9 · Đồng bộ & Hiển thị Avatar

9.1 Luồng Login SSO

Servlet callback (/api/auth/sso/callback) lấy id_token → verify.

Trích picture claim.

Ghi vào session

session.setAttribute("avatarUrl", claims.get("picture"));

Nếu cột users.profile_pic NULL ⇢ cập nhật một lần:

UserDAO.updateProfilePic(userId, claims.get("picture"));
// SQL: UPDATE users SET profile_pic=? 
//       WHERE id=? AND profile_pic IS NULL LIMIT 1;

9.2 AvatarFilter (JEE Filter)

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

Frontend JSP hiển thị: <img src="${avatarUrl}" class="avatar"/>

9.3 Kiểm thử

Tình huống

DB profile_pic

Token picture

Kết quả hiển thị

DB sau login

New SSO user

NULL

Có

Avatar từ token

Cập nhật URL

Trở lại SSO

Có

Có

Avatar DB (hoặc session)

Giữ nguyên

Email/Password

NULL

Không

Placeholder

Không đổi

Tip: Có thể bật CRON định kỳ làm “avatar hygiene” để kiểm tra URL hỏng và refetch.

