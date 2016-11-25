package com.admai.sdk.mai;

import android.content.Context;

/**
 * Created by ZAN on 16/9/13.
 */
public class MaiPlatformController {
    private  static final String TAG = MaiPlatformController.class.getSimpleName();;
    private  static volatile MaiPlatformController sMaiPlatformController;
    private Context mContext;

    public MaiPlatformController() {
    }



    public static MaiPlatformController getInstance() {
        if (sMaiPlatformController == null) {

            synchronized (MaiPlatformController.class) {
                if (sMaiPlatformController == null) {

                    sMaiPlatformController = new MaiPlatformController();
                }
            }
        }
        return sMaiPlatformController;
    }

    public Context getAppContext() {
        return (mContext == null) ? null : mContext.getApplicationContext();
    }

    public synchronized void init(final Context context) {      // 2 init
        mContext = context;
//        MaiCrashHandler.getInstance().init();
    }

}
