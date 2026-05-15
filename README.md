# Space-Based Architecture - Hướng dẫn cài đặt và chạy

## Kiến trúc theo sơ đồ

```
CLIENT-request (HTTP REST)
       │ read / write
       ▼
    PU-BE (Processing Unit Backend)
     │         │
  post-write  read
     │         │
     ▼         ▼
   RED'S (Redis In-Memory Data Grid)
     │                 ▲
   save/message        │ sync
     │                 │
     ▼                 │
 MessageGrid ──────────┤
  (write queue)        │
     │             MessageGrid
     ▼             (read queue)
 service-write        │
     │             service-read
     └──► DATABASE ◄──┘
         (H2 / PostgreSQL)
```

---

## BƯỚC 1: Cài đặt công cụ thủ công

### 1.1 Java 17 JDK
- Tải tại: https://adoptium.net/temurin/releases/?version=17
- Chọn: Windows x64, JDK, .msi
- Cài đặt → kiểm tra: `java -version`

### 1.2 Maven
- Tải tại: https://maven.apache.org/download.cgi
- Giải nén vào `C:\tools\maven`
- Thêm vào PATH: `C:\tools\maven\bin`
- Kiểm tra: `mvn -version`

### 1.3 Redis (RED'S - In-Memory Data Grid)
**Windows:**
- Tải Redis cho Windows: https://github.com/microsoftarchive/redis/releases
- Tải file `Redis-x64-3.0.504.msi`
- Cài đặt → Redis tự chạy như Windows Service
- Kiểm tra: mở `redis-cli.exe` → gõ `PING` → phải trả về `PONG`

**Hoặc dùng Docker (khuyến nghị):**
```bash
docker run -d --name redis -p 6379:6379 redis:latest
```

### 1.4 IntelliJ IDEA
- Community (miễn phí): https://www.jetbrains.com/idea/download/
- Ultimate (trả phí, có Spring support tốt hơn)

---

## BƯỚC 2: Mở project trong IntelliJ

1. **File → Open** → chọn thư mục `space-based-architecture`
2. IntelliJ tự detect `pom.xml` → click **"Load Maven Project"**
3. Đợi IntelliJ tải dependencies (lần đầu mất 2-5 phút)
4. Vào **File → Project Structure → Project**:
   - SDK: chọn Java 17
   - Language level: 17
5. Click **OK**

---

## BƯỚC 3: Cấu hình Redis trong IntelliJ

Mở file: `src/main/resources/application.properties`

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

Đảm bảo Redis đang chạy trước khi start app.

---

## BƯỚC 4: Chạy ứng dụng

### Cách 1: Qua IntelliJ
1. Mở file `SpaceBasedApplication.java`
2. Click nút **▶ Run** (góc trái class)
3. Hoặc chuột phải → **Run 'SpaceBasedApplication'**

### Cách 2: Qua Terminal
```bash
cd space-based-architecture
mvn spring-boot:run
```

### Cách 3: Build JAR rồi chạy
```bash
mvn clean package -DskipTests
java -jar target/space-based-architecture-1.0.0.jar
```

---

## BƯỚC 5: Test API

### Dùng curl hoặc Postman

**1. Health Check:**
```bash
curl http://localhost:8080/api/health
```

**2. Ghi dữ liệu (CLIENT-request → PU-BE → RED'S → DB):**
```bash
curl -X POST http://localhost:8080/api/write \
  -H "Content-Type: application/json" \
  -d '{"key": "user:1", "value": "Nguyen Van A"}'
```

**3. Đọc dữ liệu (CLIENT-request → PU-BE → RED'S):**
```bash
curl http://localhost:8080/api/read/user:1
```

**4. Seed demo data:**
```bash
curl -X POST http://localhost:8080/api/demo
```

**5. Xem H2 Database:**
- Truy cập: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:spacedb`
- Username: `sa`, Password: (để trống)

---

## BƯỚC 6: Kiểm tra Redis trực tiếp

Mở `redis-cli`:
```bash
# Xem tất cả keys trong Space
keys space:*

# Xem giá trị của một key
get space:user:1

# Monitor real-time
MONITOR
```

---

## Cấu trúc thư mục

```
src/main/java/com/spacebased/
├── SpaceBasedApplication.java          ← Entry point
├── controller/
│   └── ClientRequestController.java   ← CLIENT-request (REST API)
├── processingunit/
│   └── PuBe.java                      ← PU-BE (Processing Unit)
├── space/
│   └── RedisSpace.java                ← RED'S (In-Memory Data Grid)
├── messagegrid/
│   └── MessageGrid.java               ← Message Grid (async queue)
├── service/
│   ├── ServiceRead.java               ← service-read
│   └── ServiceWrite.java              ← service-write
├── repository/
│   └── DataEntryRepository.java       ← Database access
├── model/
│   ├── DataEntry.java                 ← Entity / Domain model
│   └── ClientRequestDTO.java          ← Request DTO
└── config/
    ├── RedisConfig.java               ← Redis configuration
    └── AsyncConfig.java               ← Thread pool config
```

---

## Troubleshooting

### Lỗi: Cannot connect to Redis
```
Error: Unable to connect to Redis; nested exception...
```
→ Đảm bảo Redis đang chạy: `redis-cli ping`

### Lỗi: Port 8080 đã bị dùng
→ Đổi port trong `application.properties`: `server.port=8081`

### Lỗi: Java version không đúng
→ File → Project Structure → chọn đúng Java 17 SDK
