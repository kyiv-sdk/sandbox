package com.example.browser.url;
import android.os.Handler;

import com.example.browser.parser.RequestParser;
import com.example.browser.parser.ResponseParser;

import java.util.ArrayList;

public class NetworkManager implements NetworkExecutorInterface {
    private static final NetworkManager ourInstance = new NetworkManager();
    private NetworkDataInterface networkDataInterface;
    private Handler handler;

    private static int nextExecutorId = 0;
    private static ArrayList<NetworkExecutor> networkExecutorsList = new ArrayList<>();

    public void setNetworkDataInterface(NetworkDataInterface networkDataInterface) {
        this.networkDataInterface = networkDataInterface;
    }

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    private NetworkManager() {
        handler = new Handler();
    }

    public void download(String request){
        NetworkExecutor networkExecutor = new NetworkExecutor(nextExecutorId++, this);

        RequestParser requestParser = new RequestParser();
        requestParser.parse(request);
        if (requestParser.isValidRequest()) {
            networkExecutor.startDownloading(requestParser.getProtocol(), requestParser.getHost(), requestParser.getPort());
        } else {
            String response = "Wrong request";
            networkDataInterface.onDataReceive(networkExecutor.getId(), response);
        }
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
    public void onDataReceive(final int id, final byte[] bytesData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ResponseParser responseParser = new ResponseParser();
                responseParser.parse(bytesData);
                String data = responseParser.getData();

                int responseCode = responseParser.getResponseCode();
                if (responseCode == 301) {
                    int locationIndex = data.indexOf("Location:");
                    int contentTypeIndex = data.indexOf("Content-Type:");

                    String newRequest = data.substring(locationIndex + 10, contentTypeIndex - 2);
                    download(newRequest);
                } else {
                    networkDataInterface.onDataReceive(id, data);
                    closeNetworkExecutor(id);
                }
            }
        });
    }
}