package com.example.timeout;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class TimeoutService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private ServiceHandler mServiceHandler;
    private LocalBroadcastManager mLocalBroadcastManager;
    public static final String ACTION = "TIMEOUT_SERVICE_ACTION";
    private int time = 5; // seconds

    // Define how the handler will process messages
    public final class ServiceHandler extends Handler {
        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message message) {
            // ...
            // When needed, stop the service with
            // stopSelf();
        }
    }

    public TimeoutService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        TimeoutService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TimeoutService.this;
        }
    }

    /** method for clients */
    public int getRandomNumber() {
        return 5;
    }

    public void startTimer(){
        try {
            TimeUnit.SECONDS.sleep(time);

            mServiceHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Send broadcast out with action filter and extras
                    Intent intent = new Intent(ACTION);
                    intent.putExtra("result", "baz");
                    mLocalBroadcastManager.sendBroadcast(intent);
                    // If desired, stop the service
//                    stopSelf();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
