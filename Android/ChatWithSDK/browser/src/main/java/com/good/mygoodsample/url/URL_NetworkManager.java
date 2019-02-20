package com.good.mygoodsample.url;

import com.example.mynetworklibrary.url.Apache_NetworkExecutor;
import com.example.mynetworklibrary.url.URL_NetworkExecutor;
import com.example.mynetworklibrary.url.URL_NetworkExecutorInterface;
import com.good.mygoodsample.parser.RequestParser;
import com.good.mygoodsample.parser.ResponseParser;

import java.util.ArrayList;

public class URL_NetworkManager implements URL_NetworkExecutorInterface {
    private static final URL_NetworkManager ourInstance = new URL_NetworkManager();
    private URL_NetworkDataInterface URLNetworkDataInterface;

    private static int nextExecutorId = 0;
    private static ArrayList<URL_NetworkExecutor> URLNetworkExecutorsList = new ArrayList<>();

    public void setURLNetworkDataInterface(URL_NetworkDataInterface URLNetworkDataInterface) {
        this.URLNetworkDataInterface = URLNetworkDataInterface;
    }

    public static URL_NetworkManager getInstance() {
        return ourInstance;
    }

    private URL_NetworkManager() {}

    public void download(String request){
        URL_NetworkExecutor URLNetworkExecutor = new Apache_NetworkExecutor(nextExecutorId++, this);

        RequestParser requestParser = new RequestParser();
        requestParser.parse(request);

        if (requestParser.isValidRequest()) {
            URLNetworkExecutor.startDownloading(requestParser.getProtocol(), requestParser.getHost(), requestParser.getPort());
        } else {
            String response = "Wrong request";
            URLNetworkDataInterface.onDataReceive(URLNetworkExecutor.getId(), response, "byte64");
        }

        URLNetworkExecutorsList.add(URLNetworkExecutor);
    }

    private void closeNetworkExecutor(int id){
        URL_NetworkExecutor URLNetworkExecutorToRemove = null;

        for (URL_NetworkExecutor executor : URLNetworkExecutorsList){
            if (executor.getId() == id){
                URLNetworkExecutorToRemove = executor;
                break;
            }
        }

        if (URLNetworkExecutorToRemove != null){
            URLNetworkExecutorsList.remove(URLNetworkExecutorToRemove);
            URLNetworkExecutorToRemove.closeDownloading();
        }
    }

    @Override
    public void onDataReceive(final int id, final byte[] bytesData, boolean needToParse) {
        String data = null;
        String encoding = null;

        if (bytesData.length == 0){
            URLNetworkDataInterface.onDataReceive(id, "Wrong request", "byte64");
            closeNetworkExecutor(id);
            return;
        }

        if (needToParse) {

            ResponseParser responseParser = new ResponseParser();
            responseParser.parse(bytesData);
            data = responseParser.getWebPage();

            int responseCode = responseParser.getResponseCode();
            if (responseCode == 301) {
                int locationIndex = data.indexOf("Location:");
                int contentTypeIndex = data.indexOf("Content-Type:");

                String newRequest = data.substring(locationIndex + 10, contentTypeIndex - 2);
                download(newRequest);
                return;

            } else {
                encoding = "byte64";
            }

        } else {
            encoding = "URL";
            data = new String(bytesData);
        }

        URLNetworkDataInterface.onDataReceive(id, data, encoding);
        closeNetworkExecutor(id);
    }
}