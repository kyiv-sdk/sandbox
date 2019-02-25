package com.good.mygoodsample;

import android.graphics.Bitmap;

public interface UI_Interface {
    void onUsersListRefresh();
    void onNewChatMessage(String srcID, String message);
    void onNewPhotoMessage(String srcID, Bitmap bitmap);
    void onNewAudioMessage(String srcID, String filePath);
    void onConnectionClosed();
}
