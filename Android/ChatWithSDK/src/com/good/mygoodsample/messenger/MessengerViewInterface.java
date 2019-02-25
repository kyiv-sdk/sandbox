package com.good.mygoodsample.messenger;

public interface MessengerViewInterface {
    void onMessagesListUpdate();
    void startCameraActivity();
    void showToast(String text);

    void setBtnAudioListeningText(String text);
    void setBtnAudioRecordingText(String text);
}
