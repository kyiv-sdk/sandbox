package com.example.iyuro.socketstest;
import java.util.ArrayList;

public class NetworkManager implements NetworkExecutorListener{
    private static final NetworkManager ourInstance = new NetworkManager();
    private NetworkDataListener networkDataListener;

    private static int nextExecutorId = 0;
    private static ArrayList<NetworkExecutor> networkExecutorsList = new ArrayList<>();

    public void setNetworkDataListener(NetworkDataListener networkDataListener) {
        this.networkDataListener = networkDataListener;
    }

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    private NetworkManager() {
    }

    public void download(String url){
        NetworkExecutor networkExecutor = new NetworkExecutor(nextExecutorId++, this);
        networkExecutor.startDownloading(url);
        networkExecutorsList.add(networkExecutor);
    }

    @Override
    public void onDataReceive(final int id, final String data) {
        networkDataListener.onDataReceive(id, data);
        networkExecutorsList.removeIf(networkExecutor -> networkExecutor.getId() == id);
    }
}
