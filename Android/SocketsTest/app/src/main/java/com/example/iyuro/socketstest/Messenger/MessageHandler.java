package com.example.iyuro.socketstest.Messenger;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageHandler implements MessageListener{
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sourceID", 0);
            jsonObject.put("message", message);

            cppSendMessage(cppMessageHandler, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
