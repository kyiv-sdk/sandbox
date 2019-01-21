package com.example.iyuro.ssl_chat.messenger;

import android.graphics.Bitmap;

public class UserMessage {
    private String message;
    private boolean sender; // if 0 -> current, if 1 -> server
    private Bitmap image;

    public UserMessage(String message, Boolean sender) {
        this.message = message;
        this.sender = sender;
        this.image = null;
    }

    public UserMessage(Bitmap image, Boolean sender) {
        this.message = null;
        this.sender = sender;
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSender() {
        return sender;
    }

    public void setSender(boolean sender) {
        this.sender = sender;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
