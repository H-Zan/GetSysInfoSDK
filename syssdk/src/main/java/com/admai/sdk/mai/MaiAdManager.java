package com.admai.sdk.mai;

import android.app.Activity;
import android.content.Context;

import com.admai.sdk.str.MaiPath;
import com.admai.sdk.type.MaiLType;
import com.admai.sdk.util.MaiCrashHandler;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogSSUtil;
import com.admai.sdk.util.log.LogUtil;
import com.admai.sdk.util.persistance.SharePreferencePersistance;

/**
 * Created by ZAN on 2016/9/17.
 */
//@NotProguard
public class MaiAdManager {

    private static volatile MaiAdManager sMaiAdManager;
    private MaiManager mMaiManager;


    public static MaiAdManager getInstance() {
        if (sMaiAdManager == null) {

            synchronized (MaiAdManager.class) {
                if (sMaiAdManager == null) {
                    sMaiAdManager = new MaiAdManager();
                }
            }
        }
        return sMaiAdManager;
    }

    public MaiAdManager() {

    }

    public void init(Context context) {
        if (context != null) {
    
            if (context instanceof Activity) {
                //保证打开之后有一次抓取位置    
                SharePreferencePersistance share = new SharePreferencePersistance();
                share.putString(context,"HasLocation","None");
                share.putString(context,"NetworkType","None");
                share.putString(context,"WifiSSID","None");
                mMaiManager = MaiManager.getInstance(context);
                mMaiManager.getSysInfoandSendLogs();
//                LogSSUtil.getInstance().saveLogs(MaiLType.APP_START, " APP Start! ", 0, "none");
                mMaiManager.initFirst();
                if (!isInitAplct && !MaiCrashHandler.getInstance().isInit) {
                    //            L.e("MaiAdManager: isInit:", MaiCrashHandler.getInstance().isInit);
                    MaiCrashHandler.getInstance().init();
                    MaiCrashHandler.getInstance().isInit=false;
                }
    
                if (!isInitAplct) {
                    LogUtil.E("Please initAplct() "," First !");
                }
            }else{
                throw new RuntimeException("admai : Please Init This In Lunch Activity");
            }
        } else {
            throw new RuntimeException("admai : Context Can Not Be Null");
        }
    }
    
    
    private boolean isInitAplct=false;
    
    public void initAplct(String defaultMaiID) {   //crashHandler  should at applaction oncreate last  but if not init at aplct, judge
        MaiCrashHandler.getInstance().init();
        isInitAplct=true;
        LogSSUtil.getInstance().setAdId(defaultMaiID);
    }

    public void initAplct() {   //crashHandler  should at applaction oncreate last  but if not init at aplct, judge
        MaiCrashHandler.getInstance().init();
        isInitAplct=true;
    }

    /**
     * @param _cat app 应用的分类编号   ：必要
     * @NotProguard
     */
    public void setcat(String _cat) {
        if (mMaiManager != null) {
            mMaiManager.setcat(_cat);
        } else {
            LogUtil.E("Please", " init first !");
        }
    }

    /**
     * @param _mkt 应用商店的编号
     * @NotProguard
     */

    public void setmkt(String _mkt) {
        if (mMaiManager != null) {
            mMaiManager.setmkt(_mkt);
        } else {
            LogUtil.E("Please", " init first !");
        }
    }

    /**
     * @param _mkt_app app 在上述应用商店内的编号
     * @NotProguard
     */

    public void setMktApp(String _mkt_app) {
        if (mMaiManager != null) {
            mMaiManager.setmkt_app(_mkt_app);
        } else {
            LogUtil.E("Please", " init first !");
        }
    }

    /**
     * @param _mkt_cat app 在上述应用商店内的分类编号
     * @NotProguard
     */

    public void setMktCat(String _mkt_cat) {
        if (mMaiManager != null) {

            mMaiManager.setmkt_cat(_mkt_cat);
        } else {
            LogUtil.E("Please", " init first !");
        }
    }

    /**
     * @param _mkt_tag 在上述应用商店内的标签(英文或中文 UTF8- R urlencode 编码) 多个标签使用半角逗号分隔
     * @NotProguard
     */
    public void setMktTag(String _mkt_tag) {
        if (mMaiManager != null) {
            mMaiManager.setmkt_tag(_mkt_tag);
        } else {
            LogUtil.E("Please", " init first !");
        }
    }

    public void setType(boolean hasImage, boolean hasFlash, boolean hasHtml, boolean hasVideo) {
        if (mMaiManager != null) {
            mMaiManager.setType(hasImage, hasFlash, hasHtml, hasVideo);
        } else {
            LogUtil.E("Please", " init first !");
        }
    }

    public void isTest(boolean isTest) {
        MaiPath.isTest(isTest);
    }
    public void setTestMode(boolean isTest,int testMode) {
        MaiPath.setTestMode(isTest,testMode);
    }

    //debugMode
    public void setDebugMode(boolean isDebug) {
        LogUtil.setLogAllowed(isDebug);
    }

    public void DebugModeMine(boolean isDebug) {
        L.sDebug = isDebug;
    }
    
    public static final String MAI_VERSION = "1.0.1";
    
    public String getSdkVersion() {
        return MAI_VERSION;
    }
    
    public void forWeather(String errorInfo) {
        LogSSUtil.getInstance().saveLogs(MaiLType.MAI_WEATHER, errorInfo, 0, "none");
    }

    public void destroy() {
        if (mMaiManager != null) {
            mMaiManager.destroy();
            LogUtil.D("MaiAdManger", "Destroy");
        } else {
            LogUtil.E("Please", "init first !");
        }
    }
}
