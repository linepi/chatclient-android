package com.example.chat.ui;

public class User {
    private String userId;
    private String password;
    private String username;

    public User(String userId,String password, String username) {
        this.userId = userId;
        this.password = password;
        this.username = username;
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
