package com.good.mygoodsample.url;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.widget.GDEditText;
import com.good.gd.widget.GDWebView;
import com.good.mygoodsample.R;

import java.util.Map;


public class URL_DownloadActivity extends AppCompatActivity implements URL_NetworkDataInterface, GDStateListener {
    static {
        System.loadLibrary("native-lib");
    }

    private static final String TAG = URL_DownloadActivity.class.getSimpleName();

    private GDEditText editText;
    private Button btn;
    private GDWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url__download);

        GDAndroid.getInstance().activityInit(this);

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
    public void onDataReceive(int id, String unencodedHtml, String encoding) {
        Log.i("---MY_URL---", unencodedHtml);

        webView.loadData(unencodedHtml, "text/html", encoding);
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

    @Override
    public void onAuthorized() {
        //If Activity specific GDStateListener is set then its onAuthorized( ) method is called when
        //the activity is started if the App is already authorized
        Log.i(TAG, "onAuthorized()");
    }

    @Override
    public void onLocked() {
        Log.i(TAG, "onLocked()");
    }

    @Override
    public void onWiped() {
        Log.i(TAG, "onWiped()");
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
        Log.i(TAG, "onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
        Log.i(TAG, "onUpdatePolicy()");
    }

    @Override
    public void onUpdateServices() {
        Log.i(TAG, "onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.i(TAG, "onUpdateEntitlements()");
    }
}
