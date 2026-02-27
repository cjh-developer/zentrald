package com.zentrald.crypto;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Map;

/**
 * Reads crypto-config.xml and returns a validated {@link CryptoSettings} instance.
 *
 * <p>Accepted values (case-insensitive):
 * <pre>
 *   hash-algorithm : SHA-256 | SHA256 | SHA-512 | SHA512
 *   encoding       : HEX     | BASE64
 * </pre>
 */
public class CryptoConfigLoader {

    /** Normalize user-supplied algorithm names to JCA standard names. */
    private static final Map<String, String> ALGORITHM_ALIASES = Map.of(
            "SHA256",  "SHA-256",
            "SHA-256", "SHA-256",
            "SHA512",  "SHA-512",
            "SHA-512", "SHA-512"
    );

    private CryptoConfigLoader() {}

    public static CryptoSettings load(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Disable external entity processing (XXE prevention)
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();

            String rawAlgorithm = getText(doc, "hash-algorithm");
            String rawEncoding  = getText(doc, "encoding");

            String hashAlgorithm = normalizeAlgorithm(rawAlgorithm);
            String encoding      = normalizeEncoding(rawEncoding);

            return new CryptoSettings(hashAlgorithm, encoding);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse crypto-config.xml: " + e.getMessage(), e);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private static String getText(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() == 0 || nodes.item(0) == null) {
            throw new IllegalArgumentException("Missing <" + tagName + "> element in crypto-config.xml");
        }
        return nodes.item(0).getTextContent().trim();
    }

    private static String normalizeAlgorithm(String raw) {
        String normalized = ALGORITHM_ALIASES.get(raw.toUpperCase().replace(" ", ""));
        if (normalized == null) {
            throw new IllegalArgumentException(
                    "Invalid <hash-algorithm> value: '" + raw + "'. Allowed: SHA-256, SHA-512");
        }
        return normalized;
    }

    private static String normalizeEncoding(String raw) {
        String upper = raw.toUpperCase().trim();
        if (!upper.equals("HEX") && !upper.equals("BASE64")) {
            throw new IllegalArgumentException(
                    "Invalid <encoding> value: '" + raw + "'. Allowed: HEX, BASE64");
        }
        return upper;
    }
}
