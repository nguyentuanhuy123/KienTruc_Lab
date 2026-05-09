# 🎬 Movie Ticket System – Event-Driven Architecture

## Kiến trúc hệ thống

```
Frontend (React) → API Gateway → User/Movie/Booking Service
                                        ↓ Publish Event
                              RabbitMQ (Message Broker)
                                        ↓ Consume
                              Payment Service → Publish PAYMENT_COMPLETED
                                        ↓ Consume
                              Notification Service → Log thông báo
```

## Danh sách Event

| Event | Publisher | Consumer |
|-------|-----------|----------|
| USER_REGISTERED | User Service | (log) |
| BOOKING_CREATED | Booking Service | Payment Service |
| PAYMENT_COMPLETED | Payment Service | Notification + Booking Status |
| BOOKING_FAILED | Payment Service | Notification + Booking Status |

## Chạy hệ thống (Docker Compose)

```bash
# 1. Build và chạy toàn bộ hệ thống
docker-compose up --build

# 2. Hoặc chạy từng bước
docker-compose up -d postgres rabbitmq   # Infrastructure trước
docker-compose up -d user-service movie-service booking-service payment-notification-service
docker-compose up -d api-gateway frontend
```

## Ports

| Service | Port | URL |
|---------|------|-----|
| Frontend | 8085 | http://localhost:8085 |
| API Gateway | 8080 | http://localhost:8080 |
| User Service | 8081 | http://localhost:8081 |
| Movie Service | 8082 | http://localhost:8082 |
| Booking Service | 8083 | http://localhost:8083 |
| Payment+Notif | 8084 | http://localhost:8084 |
| RabbitMQ UI | 15672 | http://localhost:15672 (guest/guest) |

## API Endpoints

### User Service
```
POST /api/users/register  → { username, email, password, fullName }
POST /api/users/login     → { username, password }
```

### Movie Service
```
GET  /api/movies          → Danh sách phim
POST /api/movies          → Thêm phim mới
GET  /api/movies/{id}     → Chi tiết phim
```

### Booking Service
```
POST /api/bookings              → { userId, username, movieId, movieTitle, seats, pricePerSeat }
GET  /api/bookings              → Tất cả booking
GET  /api/bookings/user/{id}    → Booking của user
```

## Chạy trên LAN (không dùng Docker)

Mỗi người chạy service trên máy của mình:

```bash
# 1 máy chạy RabbitMQ + PostgreSQL
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3-management
docker run -d -p 5432:5432 -e POSTGRES_USER=movieuser -e POSTGRES_PASSWORD=moviepass -e POSTGRES_DB=moviedb postgres:15

# Người 2 - User Service
SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.x.x:5432/moviedb \
SPRING_RABBITMQ_HOST=192.168.x.x \
mvn spring-boot:run -f user-service/pom.xml

# Người 3 - Movie Service
SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.x.x:5432/moviedb \
mvn spring-boot:run -f movie-service/pom.xml

# Người 4 - Booking Service
SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.x.x:5432/moviedb \
SPRING_RABBITMQ_HOST=192.168.x.x \
mvn spring-boot:run -f booking-service/pom.xml

# Người 5 - Payment + Notification
SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.x.x:5432/moviedb \
SPRING_RABBITMQ_HOST=192.168.x.x \
mvn spring-boot:run -f payment-notification-service/pom.xml

# Người 1 - Frontend
cd frontend && REACT_APP_API_URL=http://192.168.x.x:8080 npm start
```

## Kịch bản Demo

1. Mở http://localhost:8085 → Đăng ký tài khoản → Xem log **USER_REGISTERED**
2. Đăng nhập → Chọn phim → Đặt vé → Xem log **BOOKING_CREATED**
3. Xem log Payment Service: **PAYMENT_COMPLETED** hoặc **BOOKING_FAILED** (random)
4. Xem log Notification: "User X đã đặt đơn #123 thành công!"
5. Vào tab "Đơn của tôi" → Nhấn 🔄 Làm mới → Xem trạng thái cập nhật

## Xem Event Log (RabbitMQ Management UI)

1. Mở http://localhost:15672 (guest/guest)
2. Vào tab **Queues** để xem các queue đang có
3. Vào từng queue → **Get messages** để đọc message
