package com.good.mygoodsample;

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
	private Skeleton _currentActivity = null;

	@Override
	public void onCreate() {
		super.onCreate();

		GDAndroid.getInstance().setGDStateListener(new GDEventListener());

//		GDAndroid.getInstance().applicationInit(this);
//		// Register broadcast receiver to get GD state action updates.
//		registerGDStateReceiver();

		GDServiceListener serv = SkeletonServerGDServiceListener.getInstance();

		Log.i(TAG , "SkeletonApplication::onCreate() service Listener = " + serv + "\n");

		if(serv!=null){
			//Set the Service Listener to get requests from clients
			try {
				GDService.setServiceListener(serv);
				((SkeletonServerGDServiceListener) serv).setCurrentContext(this);
			} catch (GDServiceException e) {
				Log.e(TAG , "SkeletonApplication::onCreate()  Error Setting GDServiceListener --" + e.getMessage()  + "\n");
			}
		}
	}

	void setCurrentActivity(Skeleton activity) {
		_currentActivity = activity;
	}

	private BroadcastReceiver _gdStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String stateAction = intent.getAction();

			// We are only interested in 2 GD state events. All other will be just logged to logcat.
			Log.d(TAG, "######################## SkeletonApplication Server onReceive " + stateAction + "\n");
			switch (stateAction) {
				case GDStateAction.GD_STATE_AUTHORIZED_ACTION :
					if(_currentActivity != null) {
//                        _currentActivity.showAuthorizedUI();
					}
					break;
				case GDStateAction.GD_STATE_LOCKED_ACTION:
					if(_currentActivity != null) {
//                        _currentActivity.showNotAuthorizedUI();
					}
					break;
			}
		}
	};

	private void registerGDStateReceiver() {
		IntentFilter intentFilter = new IntentFilter();

		// Register either all state actions or only particular one per one Broadcast receiver.
		// State action can be then received from the Broadcast Intent.
		intentFilter.addAction(GDStateAction.GD_STATE_AUTHORIZED_ACTION);
		intentFilter.addAction(GDStateAction.GD_STATE_LOCKED_ACTION);
		intentFilter.addAction(GDStateAction.GD_STATE_WIPED_ACTION);
		intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_POLICY_ACTION);
		intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_SERVICES_ACTION);
		intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_CONFIG_ACTION);
		intentFilter.addAction(GDStateAction.GD_STATE_UPDATE_ENTITLEMENTS_ACTION);

		GDAndroid.getInstance().registerReceiver(_gdStateReceiver, intentFilter);
	}
}
