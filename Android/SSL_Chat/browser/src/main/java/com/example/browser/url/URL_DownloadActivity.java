package com.example.browser.url;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.example.browser.R;


public class URL_DownloadActivity extends AppCompatActivity implements URL_NetworkDataInterface {
    static {
        System.loadLibrary("native-lib");
    }

    EditText editText;
    Button btn;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url__download);

        editText = findViewById(R.id.editTextURL);
        btn = findViewById(R.id.btnURL);
        webView = findViewById(R.id.webviewURL);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                makeRequest(request.getUrl().toString());
                return false;
            }
        });

        URL_NetworkManager.getInstance().setURLNetworkDataInterface(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest(editText.getText().toString());
            }
        });
    }

    @Override
    public void onDataReceive(int id, String unencodedHtml) {
        Log.i("---MY_URL---", unencodedHtml);

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
        URL_NetworkManager.getInstance().setURLNetworkDataInterface(null);
    }

    private void makeRequest(String request){
        URL_NetworkManager.getInstance().download(request);

        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(), 0);
        }
    }
}
