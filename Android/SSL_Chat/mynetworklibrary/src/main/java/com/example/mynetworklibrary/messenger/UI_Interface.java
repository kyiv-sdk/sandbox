package com.example.mynetworklibrary.messenger;

import android.graphics.Bitmap;

public interface UI_Interface {
    void onUsersListRefresh();
    void onNewMessage(String srcID, String message);
    void onNewPhotoMessage(String srcID, Bitmap bitmap);
}