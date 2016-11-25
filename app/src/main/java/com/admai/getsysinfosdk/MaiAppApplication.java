package com.admai.getsysinfosdk;

import android.app.Application;

import com.admai.sdk.mai.MaiAdManager;

/**
 * hh
 * Created by ZAN on 16/11/25.
 */
public class MaiAppApplication extends Application {
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		MaiAdManager.getInstance().initAplct();
	}
	
}
