package com.example.iyuro.socketstest.Messenger;

import android.os.Handler;

public class MessageHandler implements MessageListener{
    private static final MessageHandler ourInstance = new MessageHandler();
    private MessageListener messageListener;
    private Handler handler;
    private long connection;

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
        cppSendMessage(connection, message);
    }

    public void openConnection(){
        this.connection = cppOpenConnection("10.129.171.8", 4444);
    }

    public void closeConnection(){
        cppCloseConnection(connection);
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

    private native long cppOpenConnection(String host, int port);
    private native void cppSendMessage(long connection, String message);
    private native void cppCloseConnection(long obj);
}
