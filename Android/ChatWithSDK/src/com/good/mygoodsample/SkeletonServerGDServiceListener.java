/*
 * This file contains sample code that is licensed according to the BlackBerry Dynamics SDK terms and conditions.
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */

package com.good.mygoodsample;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.good.gd.icc.GDService;
import com.good.gd.icc.GDServiceException;
import com.good.gd.icc.GDServiceListener;
import com.good.mygoodsample.pick_to_send.PickToSendActivity;

public class SkeletonServerGDServiceListener implements GDServiceListener {
	
	private static SkeletonServerGDServiceListener _instance = null;
	private static final String TAG = SkeletonServerGDServiceListener.class.getSimpleName();

	private Context mContext;

	synchronized static SkeletonServerGDServiceListener getInstance() {
		if(_instance == null) {
			_instance = new SkeletonServerGDServiceListener();
		}
		return _instance;
	}

	void setCurrentContext(Context context) {
		if (context != null) {
			mContext = context;
		}
	}
    
    @Override
	public void onMessageSent(String application, String requestID, String[] attachments)
	{
		Log.d(TAG, "Message sent.\n");
	}

    @Override
    public void onReceivingAttachments(String application, int numberOfAttachments, String requestID) {
        Log.d(TAG, "onReceivingAttachments number of attachments: " + numberOfAttachments + " for requestID: " + requestID + "\n");
    }
    
    @Override
    public void onReceivingAttachmentFile(String application, String path, long size, String requestID) {
        Log.d(TAG, "onReceivingAttachmentFile attachment: " + path + " size: " + size + " for requestID: " + requestID + "\n");
    }

	@Override
	public void onReceiveMessage(String application, String service,
			String version, String method, Object params, String[] attachments,
			String requestID) {
		Log.d(TAG, "+ SkeletonServer.onReceiveMessage application=" + application);
		
		String message = "";
		String files[] = null;

		if (params != null){
			if(params instanceof String){
				message = (String)params;
			}
		}

		if (attachments == null) {
			Log.d(TAG, "+ SkeletonServer.onReceiveMessage attachments null");
		} else {
			Log.d(TAG, "+ SkeletonServer.onReceiveMessage attachments length=" + attachments.length);
			if (attachments.length > 0){
				message += "\nAttachments received:";
				for (String attachment : attachments) {
					message += "\n" + attachment;
				}
			}
		}
		
		Log.d(TAG, "+ SkeletonServer.onReceiveMessage message=" + message);

		if (application != null && service != null && version != null && method != null) {
			if (application.equalsIgnoreCase("com.good.gd.example.services.greetings.client")) {
				Log.d(TAG, "+ SkeletonServer.onReceiveMessage - from greetings client");
				if (attachments != null && attachments.length != 0){
					files = new String[attachments.length];
					for (int i = 0; i < files.length; i++){
						files[i] = attachments[i];
					}
				}

				if (service.equalsIgnoreCase("sendPhoto")) {
					Log.d(TAG, "+ SkeletonServer.onReceiveMessage - test service");
					if (files != null) {
						showPickToSendActivity(application, requestID, files[0]);
						Log.e(TAG, "+ SkeletonServer.onReceiveMessage - service has been successfully handled");
					} else {
						Log.e(TAG, "+ SkeletonServer.onReceiveMessage - _currentActivity == null");
					}
				} else {
					Log.d(TAG, "+ SkeletonServer.onReceiveMessage - service not found");
				}
			} else {
				Log.d(TAG, "+ SkeletonServer.onReceiveMessage - unknown app");
			}
		}
		Log.d(TAG, "- SkeletonServer.onReceiveMessage");
	}

	public static void bringToFront(String application) {
		try {
			GDService.bringToFront(application);
		} catch (GDServiceException e) {
			e.printStackTrace();
		}
	}

	public void showPickToSendActivity(final String application, final String requestID, final String fileName){
		Log.i(TAG, "showPickToSendActivity() IN");

		Intent intent = new Intent(mContext, PickToSendActivity.class);
		intent.putExtra("isSSLEnabled", true);
		intent.putExtra("application", application);
		intent.putExtra("requestID", requestID);
		intent.putExtra("fileName", fileName);
        intent.putExtra("isAppRunningByUser", Skeleton.isAppRunningByUser());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);

		Log.i(TAG, "showPickToSendActivity() OUT");
	}
}