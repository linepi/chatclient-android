package com.example.chat.ui;

public class Friend {
    private String id;
    private String name;
    private String avatar;

    public Friend(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }
}
