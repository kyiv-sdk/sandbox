/*
 *  This file contains sample code that is licensed according to the BlackBerry Dynamics SDK terms and conditions.
 *  (c) 2017 BlackBerry Limited. All rights reserved.
 */

package com.good.mygoodsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.iyuro.ssl_chat.login.LoginActivity;
import com.example.iyuro.ssl_chat.login.LoginManager;
import com.example.iyuro.ssl_chat.pick_to_send.PickToSendActivity;
import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.icc.GDServiceListener;

import java.util.Map;


/* Skeleton - the entry point activity which will start authorization with Good Dynamics
 * and once done launch the application UI.
 */
public class Skeleton extends Activity implements GDStateListener {

	private static final String TAG = Skeleton.class.getSimpleName();

	private static boolean isServiceCalled = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "Skeleton onCreate()");
		
		GDAndroid.getInstance().activityInit(this);

//		SkeletonApplication app = (SkeletonApplication)getApplicationContext();
//		app.setCurrentActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		SkeletonApplication app = (SkeletonApplication)getApplicationContext();
		app.setCurrentActivity(null);
	}

	/*
	 * Activity specific implementation of GDStateListener. 
	 * 
	 * If a singleton event Listener is set by the application (as it is in this case) then setting 
	 * Activity specific implementations of GDStateListener is optional   
	 */
	@Override
	public void onAuthorized() {
		//If Activity specific GDStateListener is set then its onAuthorized( ) method is called when 
		//the activity is started if the App is already authorized 
		Log.i(TAG, "onAuthorized()");

		if (!isServiceCalled) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		} else {
//			isServiceCalled = false;
//			finish();
		}
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

	public static void setIsServiceCalled(Boolean value){
		isServiceCalled = value;
	}
}
