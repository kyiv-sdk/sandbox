package com.example.iyuro.socketstest;

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
    String data;
    int responseCode;

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
                    i++;
                    if (i == 340) {
                        Log.i("smth", String.valueOf(c));
                    }
                }

                data = textBuilder.toString();

                int nInd = data.indexOf('\n');
                if ((nInd > 0) && (nInd < data.length())) {
                    responseCode = Integer.parseInt(data.substring(9, 12));
                    Log.i("---MY_URL---", "response code=" + responseCode);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getData() {
        return data;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
