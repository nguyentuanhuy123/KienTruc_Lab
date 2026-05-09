-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Movies table
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    available_seats INT NOT NULL DEFAULT 100,
    genre VARCHAR(50),
    show_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    seats INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Event log table (bonus)
CREATE TABLE IF NOT EXISTS event_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    payload TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Seed movies
INSERT INTO movies (title, description, duration_minutes, price, available_seats, genre, show_time) VALUES
('Avengers: Endgame', 'The Avengers assemble once more', 181, 120000, 50, 'Action', NOW() + INTERVAL '1 day'),
('Interstellar', 'A team of explorers travel through a wormhole', 169, 100000, 80, 'Sci-Fi', NOW() + INTERVAL '2 days'),
('The Dark Knight', 'Batman vs Joker in Gotham City', 152, 90000, 60, 'Action', NOW() + INTERVAL '3 days');
