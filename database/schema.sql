-- ============================================================
-- Spoorthy Engineering College — Event Management System
-- Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS spoorthy_ems;
USE spoorthy_ems;

-- ============================================================
-- 1. USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL,
    roll_number     VARCHAR(20)     NOT NULL,
    mobile          VARCHAR(15)     NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    role            ENUM('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_roll (roll_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 2. CLUBS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS clubs (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    club_name       VARCHAR(100)    NOT NULL,
    description     TEXT            NOT NULL,
    mentor_name     VARCHAR(100)    NOT NULL,
    mentor_email    VARCHAR(150)    NOT NULL,
    logo_url        VARCHAR(500)    DEFAULT NULL,
    created_by      BIGINT          DEFAULT NULL,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_clubs_name (club_name),
    CONSTRAINT fk_clubs_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 3. EVENTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS events (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    event_name          VARCHAR(200)    NOT NULL,
    club_id             BIGINT          NOT NULL,
    event_date          DATE            NOT NULL,
    event_time          TIME            NOT NULL,
    location            VARCHAR(200)    NOT NULL,
    description         TEXT            NOT NULL,
    guest_speaker       VARCHAR(150)    DEFAULT NULL,
    registration_link   VARCHAR(500)    NOT NULL,
    prize_allocation    JSON            DEFAULT NULL,
    status              ENUM('UPCOMING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'UPCOMING',
    created_by          BIGINT          DEFAULT NULL,
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_events_club (club_id),
    INDEX idx_events_status (status),
    INDEX idx_events_date (event_date),
    CONSTRAINT fk_events_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    CONSTRAINT fk_events_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. EVENT REGISTRATIONS TABLE (junction: users <-> events)
-- ============================================================
CREATE TABLE IF NOT EXISTS event_registrations (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    event_id        BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    registered_at   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_registration (event_id, user_id),
    INDEX idx_reg_event (event_id),
    INDEX idx_reg_user (user_id),
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SEED DATA — Default Admin User
-- Password: admin123 (BCrypt hash)
-- ============================================================
INSERT INTO users (name, email, roll_number, mobile, password_hash, role)
VALUES (
    'Admin User',
    'admin@spoorthy.edu.in',
    'ADMIN001',
    '9999999999',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
) ON DUPLICATE KEY UPDATE name = name;
