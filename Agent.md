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
