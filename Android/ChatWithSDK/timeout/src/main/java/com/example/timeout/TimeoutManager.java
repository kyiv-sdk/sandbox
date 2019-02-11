package com.example.timeout;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class TimeoutManager {
    private static volatile TimeoutManager ourInstance = null;

    private TimeoutService mService;
    private boolean mBound = false;
    private Context mContext;
    private TimeoutCallback mTimeoutCallback;
    private boolean mNeedToCheckTimeOut = true;

    public static TimeoutManager getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (TimeoutManager.class) {
                if (ourInstance == null) {
                    ourInstance = new TimeoutManager(context);
                }
            }
        }
        return ourInstance;
    }

    private TimeoutManager(Context context) {
        this.mContext = context;

        Intent intent = new Intent(context, TimeoutService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter(TimeoutService.ACTION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(testReceiver, filter);
    }

    public void close(){
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(testReceiver);
        if (mBound) {
            mContext.unbindService(mConnection);
            mBound = false;
        }
    }

    public void setTimeoutCallback(TimeoutCallback timeoutCallback){
        this.mTimeoutCallback = timeoutCallback;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            TimeoutService.LocalBinder binder = (TimeoutService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            resetTimer();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            if (result.equals("TimeOut")){
                if (mTimeoutCallback != null) {
                    mTimeoutCallback.onTimeOut();
                }
            }
        }
    };

    public void resetTimer() {
        if (mBound) {
            mService.resetTimer();
        }
    }

    public boolean isTimeOut() {
        if (mService != null && mNeedToCheckTimeOut) {
            return mService.isTimeOut();
        }
        return false;
    }

    public void setNeedToCheckTimeOut(boolean mNeedToCheckTimeOut) {
        this.mNeedToCheckTimeOut = mNeedToCheckTimeOut;
    }
}
