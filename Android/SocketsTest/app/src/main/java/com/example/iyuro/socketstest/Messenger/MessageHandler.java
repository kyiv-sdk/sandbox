package com.example.iyuro.socketstest.Messenger;

import android.os.Handler;

public class MessageHandler implements MessageListener{
    static {
        System.loadLibrary("native-lib");
    }

    private static final MessageHandler ourInstance = new MessageHandler();
    private MessageListener messageListener;
    private Handler handler;
    private long cppMessageHandler;

    public MessageHandler() {
        this.handler = new Handler();
    }

    public static MessageHandler getInstance() {
        return ourInstance;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void send(String message){
        cppSendMessage(cppMessageHandler, message);
    }

    public void openConnection(){
        this.cppMessageHandler = cppCreateMessageHandler("10.129.171.8", 4444);
    }

    public void closeConnection(){
        cppDeleteMessageHandler(cppMessageHandler);
    }

    @Override
    public void onMessageReceive(final byte[] bytesData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                messageListener.onMessageReceive(bytesData);
            }
        });
    }

    private native long cppCreateMessageHandler(String host, int port);
    private native void cppSendMessage(long connection, String message);
    private native void cppDeleteMessageHandler(long obj);
}
