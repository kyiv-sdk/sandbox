package com.example.timeout;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class TimeoutService extends Service {
    private final IBinder mBinder = new LocalBinder();

    private ServiceHandler mServiceHandler;
    private LocalBroadcastManager mLocalBroadcastManager;
    public static final String ACTION = "TIMEOUT_SERVICE_ACTION";
    private int time = 5; // seconds

    private boolean isTimeOut = false;

    private CountDownTimer mCountDownTimer;

    public final class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
        }
    }

    public TimeoutService() {
        mCountDownTimer = new CountDownTimer(time * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i("--------MY_LOG--------", "Timer: seconds remaining: " + millisUntilFinished / 1000 + " " + isTimeOut);
            }

            public void onFinish() {
                Intent intent = new Intent(ACTION);
                intent.putExtra("result", "TimeOut");
                notifyUser(intent);
                isTimeOut = true;
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServiceHandler = new ServiceHandler();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        TimeoutService getService() {
            return TimeoutService.this;
        }
    }

    public void resetTimer(){
        mCountDownTimer.cancel();
        mCountDownTimer.start();
        isTimeOut = false;
    }

    private void notifyUser(final Intent intent){
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                mLocalBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }
}
