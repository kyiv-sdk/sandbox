package com.good.mygoodsample.messenger;

import android.graphics.Bitmap;

public interface MessengerPresenterInterface {
    void sendMessage(String msg);
    void sendPhoto(Bitmap bitmap);
    void sendAudio();
    void makePhoto();
    void recordAudio();
    void playAudio();
    void close();
}
