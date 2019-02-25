package com.good.mygoodsample;

import android.graphics.Bitmap;

public class UserMessage {
    private String message;
    private boolean sender; // if 0 -> current, if 1 -> server
    private Bitmap image;
    private String filePath;

    public UserMessage(String message, Boolean sender) {
        this.message = message;
        this.sender = sender;
        this.image = null;
        this.filePath = null;
    }

    public UserMessage(Bitmap image, Boolean sender) {
        this.message = null;
        this.sender = sender;
        this.image = image;
        this.filePath = null;
    }

    public UserMessage(Boolean sender, String filePath) {
        this.message = null;
        this.sender = sender;
        this.image = null;
        this.filePath = filePath;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
