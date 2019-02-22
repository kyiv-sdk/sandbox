package com.good.mygoodsample;

import android.app.Application;

import com.good.gd.GDAndroid;

public class SkeletonApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		//Singleton AppEvent listener is set in Application class so it receives events independently of Activity lifecycle
		GDAndroid.getInstance().setGDStateListener(new GDEventListener());
		
		//Also set GDService/GDServiceClient listeners in Application class if AppKinetics Service framework is used in the app
	}
}
