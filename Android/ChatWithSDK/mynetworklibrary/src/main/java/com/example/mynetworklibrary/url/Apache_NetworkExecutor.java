package com.example.mynetworklibrary.url;

import android.os.AsyncTask;

import com.good.gd.apache.http.HttpEntity;
import com.good.gd.apache.http.HttpResponse;
import com.good.gd.apache.http.client.methods.HttpGet;
import com.good.gd.apache.http.util.EntityUtils;
import com.good.gd.net.GDHttpClient;

public class Apache_NetworkExecutor extends URL_NetworkExecutor{

    public Apache_NetworkExecutor(int id, URL_NetworkExecutorInterface urlNetworkExecutorInterface) {
        super(id, urlNetworkExecutorInterface);
    }

    public void onSuccessDownload(final byte[] bytesData) {
        URLNetworkExecutorInterface.onDataReceive(id, bytesData, false);
    }

    public void startDownloading(String protocol, String host, int port){

        String urlString = protocol + "://"  + host + ":" + port;

        DownloadWebPageTask downloadWebPageTask = new DownloadWebPageTask();
        downloadWebPageTask.execute(urlString);
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        DownloadWebPageTask() {
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                onSuccessDownload(result.getBytes());
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            try {

                GDHttpClient httpclient = new GDHttpClient();

                HttpGet request = new HttpGet(url);

                HttpResponse response = null;

                response = httpclient.execute(request);

                HttpEntity responseEntity = response.getEntity();

                if (null != responseEntity) {
                    return EntityUtils.toString(responseEntity);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
