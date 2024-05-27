package com.example.chat.ui;

import com.example.chat.R;

import java.util.List;

public class ChatRoom {
    private String chatRoomId;
    private String name;
    private int image;
    private List<User> users;
    private List<Message> messages;

    public ChatRoom(String chatRoomId, String name, List<User> users, List<Message> messages) {
        this.chatRoomId = chatRoomId;
        this.name = name;
        this.users = users;
        this.messages = messages;
        this.image = R.drawable.img;
    }

    // Getters and setters
    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
