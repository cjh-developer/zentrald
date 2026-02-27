-- ============================================================
-- Zentrald — Database Schema
-- Database: MySQL 8.x
-- Encoding: utf8mb4 (full Unicode, emoji support)
-- ============================================================

CREATE DATABASE IF NOT EXISTS zentrald
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE zentrald;

-- ------------------------------------------------------------
-- Table: users
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '사용자 PK',
    username   VARCHAR(50)  NOT NULL                   COMMENT '로그인 아이디 (unique)',
    name       VARCHAR(100) NOT NULL                   COMMENT '실명',
    email      VARCHAR(100) NOT NULL                   COMMENT '이메일 (unique)',
    password   VARCHAR(255) NOT NULL                   COMMENT '비밀번호 — BCrypt 인코딩',
    role       VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER' COMMENT '권한 (e.g. ROLE_USER, ROLE_ADMIN)',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',

    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email    (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
