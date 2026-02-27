package com.zentrald.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // JPA requires a no-arg constructor
    public User() {}

    public Long getId()                     { return id; }
    public String getUsername()             { return username; }
    public void setUsername(String v)       { this.username = v; }
    public String getName()                 { return name; }
    public void setName(String v)           { this.name = v; }
    public String getEmail()                { return email; }
    public void setEmail(String v)          { this.email = v; }
    public String getPassword()             { return password; }
    public void setPassword(String v)       { this.password = v; }
    public String getRole()                 { return role; }
    public void setRole(String v)           { this.role = v; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
}
