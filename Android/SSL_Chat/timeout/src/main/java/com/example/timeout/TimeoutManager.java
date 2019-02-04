package com.example.timeout;

public class TimeoutManager {
    private static final TimeoutManager ourInstance = new TimeoutManager();

    private int time = 5; // seconds
    private static TimeoutCallback mTimeoutCallback = null;

    public static TimeoutManager getInstance(TimeoutCallback timeoutCallback) {
        mTimeoutCallback = timeoutCallback;
        return ourInstance;
    }

    private TimeoutManager() {
    }

}
