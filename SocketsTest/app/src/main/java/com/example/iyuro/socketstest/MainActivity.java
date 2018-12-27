package com.example.iyuro.socketstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NetworkDataListener {
    static {
        System.loadLibrary("native-lib");
    }

    EditText editText;
    Button btn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btn = findViewById(R.id.btn);
        textView = findViewById(R.id.textView);

        NetworkSingleton.getInstance().setNetworkDataListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkSingleton.getInstance().download(editText.getText().toString());
            }
        });
    }


    public void showText(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(s);
            }
        });
    }

    @Override
    public void OnDataReceive(String data) {
        textView.setText(data);
        NetworkSingleton.getInstance().endDownloading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkSingleton.getInstance().setNetworkDataListener(null);
    }
}