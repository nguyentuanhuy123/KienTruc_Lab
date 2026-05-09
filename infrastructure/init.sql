-- Movie Ticket System – Database Init
-- Chạy tự động khi postgres container khởi động lần đầu

CREATE TABLE IF NOT EXISTS users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username    VARCHAR(50)  UNIQUE NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS movies (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    genre           VARCHAR(50),
    duration_min    INT NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    total_seats     INT NOT NULL DEFAULT 100,
    available_seats INT NOT NULL DEFAULT 100,
    poster_url      VARCHAR(500),
    show_time       TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS bookings (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    movie_id        UUID NOT NULL,
    num_seats       INT NOT NULL,
    total_price     DECIMAL(10,2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    -- PENDING | CONFIRMED | FAILED | CANCELLED
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id      UUID NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    -- SUCCESS | FAILED
    payment_method  VARCHAR(50) DEFAULT 'SIMULATION',
    processed_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS event_logs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type      VARCHAR(50) NOT NULL,
    payload         TEXT NOT NULL,
    source_service  VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Seed data: một số phim mẫu
INSERT INTO movies (title, description, genre, duration_min, price, total_seats, available_seats, show_time)
VALUES
  ('Avengers: Endgame', 'Siêu anh hùng hợp lực chống Thanos', 'Action', 181, 85000, 120, 120, NOW() + INTERVAL '1 day'),
  ('Interstellar', 'Hành trình xuyên không gian tìm kiếm sự sống', 'Sci-Fi', 169, 90000, 100, 100, NOW() + INTERVAL '2 days'),
  ('The Dark Knight', 'Batman đối đầu Joker', 'Action', 152, 75000, 150, 150, NOW() + INTERVAL '3 days'),
  ('Inception', 'Xâm nhập vào giấc mơ', 'Thriller', 148, 80000, 80, 80, NOW() + INTERVAL '4 days'),
  ('Parasite', 'Ký sinh trùng – Phim Oscar người Hàn', 'Drama', 132, 70000, 60, 60, NOW() + INTERVAL '5 days')
ON CONFLICT DO NOTHING;
