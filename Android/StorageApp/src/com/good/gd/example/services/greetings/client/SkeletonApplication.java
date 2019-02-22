package com.good.gd.example.services.greetings.client;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateAction;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceListener;

public class SkeletonApplication extends Application {

	private static final String TAG = SkeletonApplication.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();

		GDAndroid.getInstance().setGDStateListener(new GDEventListener());
	}
}
