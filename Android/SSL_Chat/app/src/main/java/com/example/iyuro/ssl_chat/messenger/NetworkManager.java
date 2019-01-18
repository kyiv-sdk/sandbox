package com.example.iyuro.ssl_chat.messenger;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.example.iyuro.ssl_chat.MainActivity;

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

    private final int BASIC_POST = 4545;
    private final int SSL_PORT = 5454;

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

    public void openConnection(String uniqueID){
        if (cppMessageHandler == -1) {
            this.cppMessageHandler = cppCreateMessageHandler("10.129.171.8", MainActivity.isSSLEnabled? SSL_PORT : BASIC_POST, MainActivity.isSSLEnabled);
            Log.i("--------MY_LOG--------", uniqueID);
            this.send(uniqueID);
        }
    }

    public boolean isConnectionClosed(){
        return cppMessageHandler == -1;
    }

    public void closeConnection(){
        cppDeleteMessageHandler(cppMessageHandler);
        cppMessageHandler = -1;
    }

    @Override
    public void onMessageReceive(final byte[] bytesData) {
        String strData = convertBytesToString(bytesData);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("--------MY_LOG--------", "post runned");
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

    private native long cppCreateMessageHandler(String host, int port, boolean isSSLEnabled);
    private native void cppSendMessage(long connection, String message);
    private native void cppDeleteMessageHandler(long obj);
}
