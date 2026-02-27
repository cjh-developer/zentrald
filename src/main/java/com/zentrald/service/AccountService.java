package com.zentrald.service;

import com.zentrald.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Finds a username by matching both name and email. */
    public Optional<String> findUsernameByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmail(name, email)
                .map(user -> user.getUsername());
    }

    /** Returns true if a user with the given username exists. */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /** Encodes the new password and persists the updated credential. */
    public boolean resetPassword(String username, String newPassword) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}
