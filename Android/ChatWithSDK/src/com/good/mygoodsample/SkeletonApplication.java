package com.good.mygoodsample;

import android.app.Application;
import android.util.Log;

import com.good.gd.GDAndroid;
import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceListener;

public class SkeletonApplication extends Application {

	private static final String TAG = SkeletonApplication.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();

		GDAndroid.getInstance().setGDStateListener(new GDEventListener());

		GDServiceListener serv = SkeletonServerGDServiceListener.getInstance();

		Log.i(TAG , "SkeletonApplication::onCreate() service Listener = " + serv + "\n");

		if(serv!=null){
			try {
				GDService.setServiceListener(serv);
				((SkeletonServerGDServiceListener) serv).setCurrentContext(this);
			} catch (GDServiceException e) {
				Log.e(TAG , "SkeletonApplication::onCreate()  Error Setting GDServiceListener --" + e.getMessage()  + "\n");
			}
		}
	}
}
