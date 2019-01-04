package com.example.iyuro.socketstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements NetworkDataListener {
    static {
        System.loadLibrary("native-lib");
    }

    EditText editText;
    Button btn;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btn = findViewById(R.id.btn);
        webView = findViewById(R.id.webview);

        NetworkManager.getInstance().setNetworkDataListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String request = editText.getText().toString();
                NetworkManager.getInstance().download(request);
            }
        });
    }

    @Override
    public void onDataReceive(int id, String unencodedHtml) {
        Log.i("---MY_URL---", unencodedHtml);

        int nInd = unencodedHtml.indexOf('\n');
        if ((nInd > 0) && (nInd < unencodedHtml.length())) {
            String responseCode = unencodedHtml.substring(0, nInd);
            Log.i("---MY_URL---", "response code=" + responseCode);
        }

        int startIndex = unencodedHtml.indexOf("<!");
        if ((startIndex > 0) && (startIndex < unencodedHtml.length())) {
            String clearHTML = unencodedHtml.substring(startIndex);
            String encodedHtml = Base64.encodeToString(clearHTML.getBytes(),
                    Base64.NO_PADDING);
            webView.loadData(encodedHtml, "text/html", "base64");
        } else {
            webView.loadData(unencodedHtml, "text/html", "base64");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().setNetworkDataListener(null);
    }
}