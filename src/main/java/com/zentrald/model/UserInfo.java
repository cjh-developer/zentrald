package com.zentrald.model;

public class UserInfo {

    private final String username;
    private final String email;
    private String encodedPassword;

    public UserInfo(String username, String email, String encodedPassword) {
        this.username = username;
        this.email = email;
        this.encodedPassword = encodedPassword;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getEncodedPassword() { return encodedPassword; }
    public void setEncodedPassword(String encodedPassword) { this.encodedPassword = encodedPassword; }
}
