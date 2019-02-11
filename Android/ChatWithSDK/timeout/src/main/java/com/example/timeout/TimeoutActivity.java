package com.example.timeout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
public class TimeoutActivity extends AppCompatActivity implements TimeoutCallback{

    TimeoutManager timeoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeoutManager = TimeoutManager.getInstance(this);
        timeoutManager.resetTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timeoutManager.setTimeoutCallback(this);
        if (timeoutManager.isTimeOut()){
            onTimeOut();
        } else{
            timeoutManager.resetTimer();
        }
        timeoutManager.setNeedToCheckTimeOut(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeoutManager.setTimeoutCallback(null);
    }

    @Override
    public void onUserInteraction(){
        timeoutManager.resetTimer();
    }

    @Override
    public void onTimeOut() {
        timeoutManager.setNeedToCheckTimeOut(false);
    }
}
