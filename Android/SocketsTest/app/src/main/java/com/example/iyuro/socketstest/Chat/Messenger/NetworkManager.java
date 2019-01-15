package com.example.iyuro.socketstest.Chat.Messenger;

import android.os.Handler;
import android.webkit.WebResourceResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NetworkManager implements RawNetworkInterface {
    static {
        System.loadLibrary("native-lib");
    }

    private static final NetworkManager ourInstance = new NetworkManager();
    private NetworkInterface networkInterface;
    private Handler handler;
    private long cppMessageHandler;

    public NetworkManager() {
        this.handler = new Handler();
        this.cppMessageHandler = -1;
    }

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void send(String message){
        cppSendMessage(cppMessageHandler, message);
    }

    public void openConnection(){
        if (cppMessageHandler == -1) {
            this.cppMessageHandler = cppCreateMessageHandler("10.129.171.8", 4444);
        }
    }

    public void closeConnection(){
        cppDeleteMessageHandler(cppMessageHandler);
    }

    @Override
    public void onMessageReceive(final byte[] bytesData) {
        String strData = convertBytesToString(bytesData);
        handler.post(new Runnable() {
            @Override
            public void run() {
                networkInterface.onMessageReceive(strData);
            }
        });
    }

    private String convertBytesToString(final byte[] bytesData){
        String data = "";
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesData);

            WebResourceResponse webResourceResponse = new WebResourceResponse("text/html", "utf-8", byteArrayInputStream);

            InputStream inputStream = webResourceResponse.getData();

            StringBuilder textBuilder = new StringBuilder();

            Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())));
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }

            data = textBuilder.toString();

        } catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    private native long cppCreateMessageHandler(String host, int port);
    private native void cppSendMessage(long connection, String message);
    private native void cppDeleteMessageHandler(long obj);
}
