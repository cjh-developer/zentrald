-- ============================================================
-- Zentrald — Initial / Seed Data
-- ============================================================
--
-- NOTE: The password column must contain a value encoded with
--       the combination configured in crypto-config.xml.
--
--   For development:
--     DataInitializer.java auto-creates and re-encodes the admin
--     account on every startup — no need to run this file.
--
--   For production / manual setup:
--     1. Check crypto-config.xml to see the active hash + encoding.
--     2. Generate the encoded value for your chosen password.
--        Example (Java):
--          MessageDigest md = MessageDigest.getInstance("SHA-512");
--          byte[] hash = md.digest("your-password".getBytes(StandardCharsets.UTF_8));
--          String encoded = HexFormat.of().formatHex(hash);  // or Base64.getEncoder()...
--     3. Replace the password value below and run this script.
--
-- Default test credential: admin / 1234
--   SHA-512 + HEX encoded value of "1234":
--   d404559f602eab6fd602ac7680dacbfaadd13630335e951f097af3900e9de176b6db28512f2e000b9d04fba5133e8b1c6e8df59db3a8ab9d60be4b97cc9e81db
-- ============================================================

USE zentrald;

INSERT INTO users (username, name, email, password, role)
VALUES (
    'admin',
    '관리자',
    'admin@example.com',
    'd404559f602eab6fd602ac7680dacbfaadd13630335e951f097af3900e9de176b6db28512f2e000b9d04fba5133e8b1c6e8df59db3a8ab9d60be4b97cc9e81db',
    'ROLE_USER'
)
ON DUPLICATE KEY UPDATE id = id;
