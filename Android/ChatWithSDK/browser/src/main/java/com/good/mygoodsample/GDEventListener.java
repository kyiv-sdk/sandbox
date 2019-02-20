/*
 *  This file contains sample code that is licensed according to the BlackBerry Dynamics SDK terms and conditions.
 *  (c) 2017 BlackBerry Limited. All rights reserved.
 */

package com.good.mygoodsample;

import com.good.gd.GDStateListener;

import java.util.Map;

class GDEventListener implements GDStateListener {

	private static final String TAG = GDEventListener.class.getSimpleName();

	@Override
	public void onAuthorized() {

	}

	@Override
	public void onLocked() {

	}

	@Override
	public void onWiped() {

	}

	@Override
	public void onUpdateConfig(Map<String, Object> settings) {
	
	}

	@Override
	public void onUpdatePolicy(Map<String, Object> policyValues) {

	}

	@Override
	public void onUpdateServices() {

	}

    @Override
    public void onUpdateEntitlements() {

    }
}
