package com.example.chat.ui;

import java.util.Date;

public class Message {
    private String text;
    private boolean isSent;
    private Date sentTime;
    private String username;

    public Message(String username, String text, boolean isSent, Date sentTime) {
        this.username = username;
        this.text = text;
        this.isSent = isSent;
        this.sentTime = sentTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public boolean isSent() {
        return isSent;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }
}
