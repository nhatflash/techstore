# TechStore Chat Module

## Hệ thống Chat Realtime giữa Admin và Customer

Module chat được xây dựng với:
- **Backend**: Spring Boot 4 + WebSocket (STOMP) + PostgreSQL + Redis
- **Android**: Kotlin + Retrofit + OkHttp WebSocket + STOMP Protocol tự viết

---

## Tài khoản Admin mặc định

Khi chạy lần đầu, hệ thống tự động tạo tài khoản Admin:

```
Username: admin
Password: admin123
Email: admin@techstore.com
```

**Lưu ý:** Tài khoản này được tạo tự động bởi `DataInitializer.java` khi khởi động ứng dụng.

---

## Cài đặt và Chạy

### Backend (Spring Boot)

1. Đảm bảo PostgreSQL và Redis đang chạy
2. Cấu hình file `.env`:
   ```
   DB_USER=postgres
   DB_PASSWORD=123456
   JWT_SECRET=your_secret_key
   ```
3. Chạy backend:
   ```bash
   cd D:\techstore-chatmodule
   .\gradlew.bat bootRun
   ```
4. Backend sẽ chạy tại `http://localhost:8080`
5. WebSocket endpoint: `ws://localhost:8080/ws`

### Android App

1. Mở project `chat/` trong Android Studio
2. Sửa địa chỉ server trong `RetrofitClient.kt`:
   ```kotlin
   const val BASE_URL = "http://10.0.2.2:8080"  // Emulator
   const val WS_URL = "ws://10.0.2.2:8080/ws"
   ```
   Nếu test trên thiết bị thật, thay `10.0.2.2` bằng IP máy tính.
3. Build & Run

---

## Luồng sử dụng

### Customer (Khách hàng)
1. Đăng ký tài khoản mới (role tự động = Customer)
2. Đăng nhập → Tự động vào màn hình Chat
3. Gửi tin nhắn cho Admin
4. Xem trạng thái Online/Offline, Typing indicator
5. Logout → Xóa token cục bộ + ngắt WebSocket

### Admin
1. Đăng nhập bằng tài khoản `admin / admin123`
2. Vào màn hình Danh sách Rooms (hỗ trợ)
3. Tìm kiếm room theo tên/SĐT khách hàng
4. Nhấn vào room để chat với Customer
5. Xem số tin nhắn chưa đọc (badge đỏ)
6. Logout → Xóa token cục bộ + ngắt WebSocket

---

## API Endpoints

### Auth
- `POST /api/auth/sign-in` — Đăng nhập
- `POST /api/auth/sign-up` — Đăng ký (Customer)
- `POST /api/auth/refresh` — Refresh access token

### Chat (REST)
- `GET /api/chat/room` — Customer lấy room của mình
- `GET /api/chat/rooms` — Admin lấy danh sách rooms
- `GET /api/chat/rooms/search?q=` — Admin tìm kiếm room
- `GET /api/chat/rooms/{roomId}/messages` — Lấy lịch sử tin nhắn
- `POST /api/chat/rooms/{roomId}/read` — Đánh dấu đã đọc
- `GET /api/chat/rooms/{roomId}/unread-count` — Số tin chưa đọc
- `GET /api/chat/users/{username}/presence` — Lấy trạng thái online/lastSeen
- `GET /api/chat/messages/{messageId}/status` — Kiểm tra trạng thái đọc của tin nhắn

### WebSocket (STOMP)
- Endpoint: `/ws`
- Destinations:
  - `/app/chat.send` → Gửi tin nhắn
  - `/app/chat.typing` → Typing event
  - `/app/chat.read` → Mark as read event
  - `/topic/room.{roomId}` → Nhận tin nhắn mới
  - `/topic/room.{roomId}.typing` → Nhận typing indicator
  - `/topic/room.{roomId}.presence` → Nhận online/offline status + lastSeen
  - `/topic/room.{roomId}.read` → Nhận read event
  - `/topic/admin.rooms` → Admin nhận update danh sách rooms
  - `/topic/admin.presence` → Admin nhận trạng thái online/offline tất cả users

---

## Công nghệ sử dụng

### Backend
- Spring Boot 4.0.2 (Java 21)
- WebSocket + STOMP (Simple Broker)
- Spring Security + JWT
- PostgreSQL (JPA/Hibernate)
- Redis (online status, session)
- Swagger UI (`/swagger-ui`)

### Android
- Kotlin
- Retrofit 2.11 + OkHttp 4.12
- STOMP Protocol parser tự viết
- Coroutines + Flow
- ViewModel + LiveData
- Navigation Component
- Material Design 3

---

## Lưu ý

- **Logout không blacklist JWT**: Token được xóa phía client, tự hết hạn sau 30 phút
- **Online status**: Lưu trong Redis với TTL 30 phút, broadcast qua WebSocket
- **Last seen**: Lưu timestamp vào Redis khi user disconnect, hiển thị "Lần cuối: HH:mm" hoặc "Hôm qua HH:mm" hoặc "dd/MM/yyyy HH:mm"
- **Read receipt**: Click vào tin nhắn đã gửi để xem đối phương đã đọc chưa (không hiển thị icon mắt)
- **WebSocket auto-reconnect**: Exponential backoff (1s → 2s → 4s → max 30s)
- **Tin nhắn in-memory**: Android không lưu SQLite, chỉ giữ in-memory để đơn giản
- **Simple Broker**: Đủ cho MVP, nếu scale cần chuyển sang Redis-backed STOMP broker
- **HTTP Cleartext**: Android app đã cấu hình `network_security_config.xml` cho phép HTTP với localhost/emulator (10.0.2.2, 127.0.0.1). **Production nên dùng HTTPS!**

---

