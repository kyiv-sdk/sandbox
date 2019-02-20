package com.good.mygoodsample.parser;

import android.util.Log;
import android.webkit.WebResourceResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ResponseParser {
    private String rawData;
    private int responseCode;
    private String webPage;

    public void parse(final byte[] bytesData){
        String result = "";
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesData);

            WebResourceResponse webResourceResponse = new WebResourceResponse("text/html", "utf-8", byteArrayInputStream);

            InputStream inputStream = webResourceResponse.getData();

            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                int i = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }

                rawData = textBuilder.toString();

                int nInd = rawData.indexOf('\n');
                if ((nInd > 0) && (nInd < rawData.length())) {
                    responseCode = Integer.parseInt(rawData.substring(9, 12));
                    Log.i("---MY_URL---", "response code=" + responseCode);
                }

                String unencodedHtml = new String(bytesData);
                int startIndex = unencodedHtml.indexOf("<!");
                if ((startIndex > 0) && (startIndex < unencodedHtml.length())) {
                    this.webPage = unencodedHtml.substring(startIndex);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getRawData() {
        return rawData;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getWebPage() {
        return webPage;
    }
}
