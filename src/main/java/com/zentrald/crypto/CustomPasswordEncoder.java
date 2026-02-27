package com.zentrald.crypto;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

/**
 * A configurable {@link PasswordEncoder} that combines a hash algorithm with
 * a binary-to-text encoding scheme.
 *
 * <p>Pipeline:
 * <pre>
 *   plaintext  →  [ SHA-256 | SHA-512 ]  →  [ HEX | BASE64 ]  →  stored string
 * </pre>
 *
 * <p>The algorithm and encoding are loaded from {@code crypto-config.xml} at startup.
 * Changing the XML and restarting the application switches the encoding scheme;
 * all existing stored passwords must then be re-encoded.
 *
 * <p><b>Security note:</b> SHA-256/SHA-512 are fast, unsalted hash functions.
 * For user-facing passwords, BCrypt / Argon2 / scrypt are recommended in production.
 */
public class CustomPasswordEncoder implements PasswordEncoder {

    private final String hashAlgorithm;   // "SHA-256" | "SHA-512"
    private final String encoding;        // "HEX"     | "BASE64"

    public CustomPasswordEncoder(CryptoSettings settings) {
        this.hashAlgorithm = settings.getHashAlgorithm();
        this.encoding      = settings.getEncoding();
    }

    // ── PasswordEncoder ────────────────────────────────────────────────────────

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] hashed = hash(rawPassword.toString());
        return encodeBytes(hashed);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }

    // ── Internals ──────────────────────────────────────────────────────────────

    private byte[] hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hash algorithm unavailable: " + hashAlgorithm, e);
        }
    }

    private String encodeBytes(byte[] bytes) {
        return switch (encoding) {
            case "HEX"    -> HexFormat.of().formatHex(bytes);
            case "BASE64" -> Base64.getEncoder().encodeToString(bytes);
            default       -> throw new IllegalStateException("Unknown encoding: " + encoding);
        };
    }

    // ── Info ───────────────────────────────────────────────────────────────────

    public String getHashAlgorithm() { return hashAlgorithm; }
    public String getEncoding()      { return encoding; }

    @Override
    public String toString() {
        return "CustomPasswordEncoder[" + hashAlgorithm + " + " + encoding + "]";
    }
}
