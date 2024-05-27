package com.example.chat.ui;


import com.example.chat.R;

public class ChatRoomList {
    private String chatRoomName;
    private int chatRoomImage;
    private String password;
    private int user_num;
    public ChatRoomList(String chatRoomName) {
        this.chatRoomName = chatRoomName;
        this.chatRoomImage = R.drawable.img;
        this.password = null;
    }
    public ChatRoomList(String chatRoomName, String password) {
        this.chatRoomName = chatRoomName;
        this.chatRoomImage = R.drawable.img;
        this.password = password;
    }

    public int getUser_num() {
        return user_num;
    }

    public void setUser_num(int user_num) {
        this.user_num = user_num;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public int getChatRoomImage() {
        return chatRoomImage;
    }

    public void setChatRoomImage(int chatRoomImage) {
        this.chatRoomImage = chatRoomImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
