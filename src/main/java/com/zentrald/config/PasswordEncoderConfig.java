package com.zentrald.config;

import com.zentrald.crypto.CryptoConfigLoader;
import com.zentrald.crypto.CryptoSettings;
import com.zentrald.crypto.CustomPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

/**
 * Loads password-encoding settings from the external XML config file
 * and exposes a {@link PasswordEncoder} bean accordingly.
 *
 * <p>The config file path is resolved via the {@code crypto.config.path}
 * property (default: {@code classpath:crypto-config.xml}).
 * Use a {@code file:} prefix to point to a path outside the JAR, e.g.
 * {@code crypto.config.path=file:/etc/zentrald/crypto-config.xml}
 */
@Configuration
public class PasswordEncoderConfig {

    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderConfig.class);

    @Value("${crypto.config.path:classpath:crypto-config.xml}")
    private String cryptoConfigPath;

    private final ResourceLoader resourceLoader;

    public PasswordEncoderConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Resource resource = resourceLoader.getResource(cryptoConfigPath);

        if (!resource.exists()) {
            throw new IllegalStateException(
                    "crypto-config.xml not found at: " + cryptoConfigPath);
        }

        try {
            CryptoSettings settings = CryptoConfigLoader.load(resource.getInputStream());
            log.info("Password encoding configured: {}", settings);
            return new CustomPasswordEncoder(settings);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read crypto-config.xml from: " + cryptoConfigPath, e);
        }
    }
}
