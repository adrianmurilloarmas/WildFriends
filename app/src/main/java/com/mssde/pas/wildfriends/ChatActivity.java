package com.mssde.pas.wildfriends;


import android.app.Activity;
import android.os.Bundle;

import java.util.Date;

public class ChatActivity {
    private String messageText;

    private String messageUser;

    private long messageTime;

    public ChatActivity(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    //@Override
    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
    }*/
}