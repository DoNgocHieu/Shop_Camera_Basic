# Đăng Quang Watch - Hệ thống quản lý cửa hàng đồng hồ

## Mô tả dự án

Đăng Quang Watch là một ứng dụng web quản lý cửa hàng đồng hồ được xây dựng bằng Java Spring Boot. Ứng dụng cung cấp các chức năng:

- **Quản lý sản phẩm**: Thêm, sửa, xóa đồng hồ
- **Quản lý danh mục**: Quản lý các hãng/danh mục đồng hồ
- **Đăng ký/Đăng nhập**: Hệ thống xác thực người dùng
- **Giỏ hàng**: Thêm sản phẩm vào giỏ hàng và quản lý
- **Thanh toán**: Đặt hàng và theo dõi đơn hàng
- **Quản trị**: Giao diện quản trị cho admin

## Công nghệ sử dụng

- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: H2 (development), MySQL (production)
- **Security**: Spring Security
- **Frontend**: Thymeleaf, Bootstrap 5, Font Awesome
- **Build Tool**: Maven

## Cấu trúc dự án

```
src/
├── main/
│   ├── java/
│   │   └── com/dangquang/watch/
│   │       ├── WatchStoreApplication.java
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   └── DataInitializer.java
│   │       ├── controller/
│   │       │   ├── HomeController.java
│   │       │   ├── AuthController.java
│   │       │   ├── CartController.java
│   │       │   ├── OrderController.java
│   │       │   └── admin/
│   │       ├── entity/
│   │       │   ├── User.java
│   │       │   ├── Category.java
│   │       │   ├── Watch.java
│   │       │   ├── CartItem.java
│   │       │   ├── Order.java
│   │       │   └── OrderItem.java
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       ├── application.properties
│       ├── static/
│       │   ├── css/
│       │   ├── js/
│       │   └── images/
│       └── templates/
│           ├── index.html
│           ├── shop.html
│           ├── auth/
│           ├── cart/
│           ├── order/
│           └── admin/
```

## Cài đặt và chạy

### Yêu cầu hệ thống

- Java 17 hoặc cao hơn
- Maven 3.6+
- IDE hỗ trợ Spring Boot (IntelliJ IDEA, Eclipse, VS Code)

### Hướng dẫn cài đặt

1. **Clone project hoặc tải về**
   ```bash
   git clone <repository-url>
   cd dong_ho_project
   ```

2. **Cài đặt dependencies**
   ```bash
   mvn clean install
   ```

3. **Chạy ứng dụng**
   ```bash
   mvn spring-boot:run
   ```

4. **Truy cập ứng dụng**
   - URL: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

### Tài khoản mặc định

Sau khi chạy ứng dụng, hệ thống sẽ tự động tạo:

**Admin:**
- Username: `admin`
- Password: `admin123`

**User:**
- Username: `user`
- Password: `user123`

## Chức năng chính

### Người dùng thông thường
- Xem danh sách sản phẩm
- Tìm kiếm, lọc sản phẩm
- Đăng ký/Đăng nhập
- Thêm sản phẩm vào giỏ hàng
- Đặt hàng và thanh toán
- Theo dõi lịch sử đơn hàng

### Quản trị viên
- Quản lý danh mục sản phẩm
- Quản lý đồng hồ (CRUD)
- Quản lý đơn hàng
- Cập nhật trạng thái đơn hàng

## API Endpoints

### Public
- `GET /` - Trang chủ
- `GET /shop` - Danh sách sản phẩm
- `GET /watch?id={id}` - Chi tiết sản phẩm
- `GET /login` - Trang đăng nhập
- `GET /register` - Trang đăng ký

### User (Authenticated)
- `GET /cart` - Giỏ hàng
- `POST /cart/add` - Thêm vào giỏ hàng
- `GET /order/checkout` - Thanh toán
- `POST /order/place` - Đặt hàng
- `GET /order/history` - Lịch sử đơn hàng

### Admin
- `GET /admin` - Dashboard quản trị
- `GET /admin/watches` - Quản lý đồng hồ
- `GET /admin/categories` - Quản lý danh mục
- `GET /admin/orders` - Quản lý đơn hàng

## Database Schema

### Users
- id, username, email, password, fullName, phoneNumber, address, role, enabled

### Categories
- id, name, description

### Watches
- id, name, brand, price, description, imageUrl, stockQuantity, active, category_id

### CartItems
- id, user_id, watch_id, quantity, price

### Orders
- id, user_id, orderDate, totalAmount, status, shippingAddress, phoneNumber, notes

### OrderItems
- id, order_id, watch_id, quantity, price

## Tính năng bảo mật

- Spring Security authentication
- Password encryption (BCrypt)
- Role-based authorization (USER, ADMIN)
- CSRF protection
- Session management

## Môi trường phát triển

### Database
- **Development**: H2 in-memory database
- **Production**: MySQL (cấu hình trong application.properties)

### Logging
- Spring Boot Logging
- Debug level cho package com.dangquang.watch

## Triển khai

### Chuẩn bị production
1. Thay đổi database sang MySQL trong `application.properties`
2. Cấu hình các biến môi trường
3. Build jar file: `mvn clean package`
4. Chạy: `java -jar target/watch-store-0.0.1-SNAPSHOT.jar`

## Đóng góp

1. Fork project
2. Tạo feature branch
3. Commit changes
4. Push to branch
5. Tạo Pull Request

## License

This project is licensed under the MIT License.

## Liên hệ

- **Tác giả**: Đăng Quang Watch Team
- **Email**: contact@dangquangwatch.com
- **Website**: https://dangquangwatch.com
