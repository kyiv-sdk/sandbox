package com.example.iyuro.socketstest;
import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

public class NetworkManager implements NetworkExecutorListener{
    private static final NetworkManager ourInstance = new NetworkManager();
    private NetworkDataListener networkDataListener;
    private Handler handler;

    private static int nextExecutorId = 0;
    private static ArrayList<NetworkExecutor> networkExecutorsList = new ArrayList<>();

    public void setNetworkDataListener(NetworkDataListener networkDataListener) {
        this.networkDataListener = networkDataListener;
    }

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    private NetworkManager() {
        handler = new Handler();
    }

    public void download(String request){
        try {
            if ((request.length() > 4) && (!request.substring(0, 4).toLowerCase().equals("http"))) {
                request = "http://" + request;
            }
            URL url = new URL(request);
            NetworkExecutor networkExecutor = new NetworkExecutor(nextExecutorId++, this);
            String protocol = url.getProtocol().toUpperCase();
            String host = url.getHost();
            int port = url.getPort();
            if (port < 0) {
                if (protocol.equals("HTTP")){
                    port = 80;
                } else {
                    port = 443;
                }
            }
            Log.i("---MY_URL---", url.toString());
            Log.i("---MY_URL---", url.getProtocol());
            networkExecutor.startDownloading(protocol, host, port);
            networkExecutorsList.add(networkExecutor);
        } catch (Exception e) {
            e.printStackTrace();
            networkDataListener.onDataReceive(-1, "Wrong URL! Please, try again :)");
        }
    }

    private void closeNetworkExecutor(int id){
        NetworkExecutor networkExecutorToRemove = null;
        for (NetworkExecutor executor : networkExecutorsList){
            if (executor.getId() == id){
                networkExecutorToRemove = executor;
                break;
            }
        }

        if (networkExecutorToRemove != null){
            networkExecutorsList.remove(networkExecutorToRemove);
            networkExecutorToRemove.closeDownloading();
        }
    }

    @Override
    public void onDataReceive(final int id, final byte[] bytesData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = new String(bytesData, "UTF-8");

                    networkDataListener.onDataReceive(id, data);
                    closeNetworkExecutor(id);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
