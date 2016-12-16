package com.admai.getsysinfosdk;

import android.os.Bundle;
import android.app.Activity;

import com.admai.sdk.mai.MaiAdManager;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MaiAdManager instance = MaiAdManager.getInstance();
		instance.init(this);
		instance.isTest(true);
		
		
		MaiAdManager.getInstance().DebugModeMine(true);
	}
	
}
