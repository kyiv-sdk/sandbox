package com.example.iyuro.socketstest;
import android.os.Handler;

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

    public void download(String url){
        NetworkExecutor networkExecutor = new NetworkExecutor(nextExecutorId++, this);
        networkExecutor.startDownloading(url);
        networkExecutorsList.add(networkExecutor);
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
    public void onDataReceive(final int id, final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                networkDataListener.onDataReceive(id, data);
                closeNetworkExecutor(id);
            }
        });
    }
}
