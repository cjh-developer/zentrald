package com.zentrald.repository;

import com.zentrald.model.UserInfo;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @deprecated Replaced by {@link UserRepository} (Spring Data JPA).
 *             Kept for reference only — not registered as a Spring bean.
 */
public class UserStore {

    private final Map<String, UserInfo> store = new HashMap<>();

    public UserStore(PasswordEncoder passwordEncoder) {
        // Default test account: admin / admin@example.com / 1234
        store.put("admin", new UserInfo("admin", "admin@example.com", passwordEncoder.encode("1234")));
    }

    public Optional<UserInfo> findByUsername(String username) {
        return Optional.ofNullable(store.get(username));
    }

    public Optional<UserInfo> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public boolean updatePassword(String username, String newEncodedPassword) {
        UserInfo user = store.get(username);
        if (user == null) return false;
        user.setEncodedPassword(newEncodedPassword);
        return true;
    }
}
