package com.admai.getsysinfosdk.browser;

import android.app.Activity;
import android.os.Bundle;

import com.admai.getsysinfosdk.R;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        WebViewUtils.openPageInSDK(this,"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png","");
    }
}
