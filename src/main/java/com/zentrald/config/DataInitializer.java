package com.zentrald.config;

import com.zentrald.crypto.CustomPasswordEncoder;
import com.zentrald.entity.User;
import com.zentrald.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures the default admin account exists and its password is encoded
 * with the currently configured algorithm (from crypto-config.xml).
 *
 * <p>On every startup, the admin account is deleted and recreated so that
 * the stored password always reflects the active encoding scheme.
 * This is intentional for development; remove or guard this behaviour in production.
 *
 * <p>Default credentials: {@code admin / 1234}
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Log the active encoding scheme for visibility
        if (passwordEncoder instanceof CustomPasswordEncoder cpe) {
            log.info("Active password encoder: {}", cpe);
        }

        // Delete and recreate admin so the password is always re-encoded
        // with the current crypto-config.xml settings.
        userRepository.findByUsername("admin")
                .ifPresent(existing -> {
                    userRepository.delete(existing);
                    log.info("Existing admin account removed for re-encoding.");
                });

        User admin = new User();
        admin.setUsername("admin");
        admin.setName("관리자");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setRole("ROLE_USER");
        userRepository.save(admin);

        log.info("Admin account initialized. (username=admin, password=1234)");
    }
}
