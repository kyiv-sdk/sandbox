package com.example.iyuro.ssl_chat.messenger;

import android.graphics.Bitmap;

import com.example.iyuro.ssl_chat.ChatManager;
import com.example.iyuro.ssl_chat.UserMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MessengerPresenter implements MessengerPresenterInterface {
    private final String AUDIOFILE_NAME_START = "audiorecordtest";
    private final String AUDIOFILE_EXTENSION = ".3gp";

    private MessengerViewInterface mViewInterface;
    private ArrayList<UserMessage> mMessageList;
    private String mDstId;

    private String mAbsolutePath = null;
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;
    private AudioHandler mAudioHandler;

    public MessengerPresenter(MessengerViewInterface mViewInterface, ArrayList<UserMessage> mMessageList, String dstID, String absolutePath) {
        this.mViewInterface = mViewInterface;
        this.mMessageList = mMessageList;
        this.mDstId = dstID;
        this.mAbsolutePath = absolutePath;

        this.mAudioHandler = new AudioHandler();
        this.mAudioHandler.setFileName(mAbsolutePath + AUDIOFILE_NAME_START + AUDIOFILE_EXTENSION);
    }

    @Override
    public void sendMessage(String msg) {
        if (msg.length() > 0) {
            mMessageList.add(new UserMessage(msg, false));
            mViewInterface.onMessagesListUpdate();
            ChatManager.getInstance().sendMessage(mDstId, msg);
        }
    }

    @Override
    public void makePhoto() {
        mViewInterface.startCameraActivity();
    }

    @Override
    public void sendPhoto(Bitmap bitmap) {
        mMessageList.add(new UserMessage(bitmap, false));
        mViewInterface.onMessagesListUpdate();
        ChatManager.getInstance().sendPhoto(mDstId, bitmap);
    }

    @Override
    public void sendAudio() {

        // TODO: change implementation from java to dynamics

        String newFilePath = mAbsolutePath + AUDIOFILE_NAME_START + String.valueOf(ChatManager.getNextFileID()) + AUDIOFILE_EXTENSION;

        if (copyFile(mAbsolutePath + AUDIOFILE_NAME_START + AUDIOFILE_EXTENSION,  newFilePath)) {
            mMessageList.add(new UserMessage(false, newFilePath));
            mViewInterface.onMessagesListUpdate();
            ChatManager.getInstance().sendAudio(mDstId, newFilePath);
        } else {
            mViewInterface.showToast("No audio file yet");
        }
    }

    @Override
    public void close() {
        mAudioHandler.close();
    }

    public boolean copyFile(String fromFileName, String toFileName){
        final File file = new File(fromFileName);
        if (file.length() <= 0){
            return false;
        }

        FileInputStream fis = null;
        byte[] bytesArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray);
            fis.close();

            File newfile = new File(toFileName);
            FileOutputStream fos = new FileOutputStream(newfile);
            fos.write(bytesArray);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void recordAudio(){
        mAudioHandler.onRecord(mStartRecording);
        if (mStartRecording) {
            mViewInterface.setBtnAudioRecordingText("Stop recording");
        } else {
            mViewInterface.setBtnAudioRecordingText("Start recording");
        }
        mStartRecording = !mStartRecording;
    }

    @Override
    public void playAudio(){
        mAudioHandler.onPlay(mStartPlaying);
        if (mStartPlaying) {
            mViewInterface.setBtnAudioListeningText("Stop playing");
        } else {
            mViewInterface.setBtnAudioListeningText("Start playing");
        }
        mStartPlaying = !mStartPlaying;
    }
}
