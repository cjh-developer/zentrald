package com.zentrald.crypto;

/**
 * Holds the resolved password-encoding settings from crypto-config.xml.
 *
 * hashAlgorithm : "SHA-256" | "SHA-512"
 * encoding      : "HEX"     | "BASE64"
 */
public class CryptoSettings {

    private final String hashAlgorithm;
    private final String encoding;

    public CryptoSettings(String hashAlgorithm, String encoding) {
        this.hashAlgorithm = hashAlgorithm;
        this.encoding = encoding;
    }

    public String getHashAlgorithm() { return hashAlgorithm; }
    public String getEncoding()      { return encoding; }

    @Override
    public String toString() {
        return hashAlgorithm + " + " + encoding;
    }
}
