package com.admai.sdk.util.log;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.admai.sdk.util.MaiUtils;

/**
 * Created by ZAN on 16/9/9.
 */
public class LogUtil {
    private static final String TAG = "AdMai_Log";  //TAG 为 AdMai_Log

    protected static boolean mIsLogAllowed = false;
    private static Context mContext;
    protected static boolean ifMaiSelf = false;
    private static boolean saveLogOrNot = false;
    protected static boolean mIsShowError = false;

    public static boolean isShowError() {
        return mIsLogAllowed && mIsShowError;
    }

    public static void setLogAllowed(boolean isAllowed) {
        mIsLogAllowed = isAllowed;
        mIsLogAllowed = isAllowed;
        if (!isAllowed) {
            Log.w(TAG, getMsg("log closed..."));
        }
    }

    public static boolean isLogAllow() {
        return mIsLogAllowed;
    }


    public static void D(String var0, String var1) {
        if (mIsLogAllowed) {
            Log.d(TAG, getMsg(var0,var1));
        }
    }

    public static void I(String var0, String var1) {
        if (mIsLogAllowed) {
            Log.i(TAG, getMsg(var0,var1));
        }

    }

    public static void W(String var0, String var1) {
        if (mIsLogAllowed) {
            Log.w(TAG, getMsg(var0,var1));
        }

    }

    public static void E(String var0, String var1) {
        if (mIsLogAllowed) {
            Log.e(TAG, getMsg(var0,var1));
        }

    }

    public static void MaiD(String var0, String var1) {
        if (ifMaiSelf) {
            Log.d(TAG, getMsg(var0,var1));
        }

    }

    public static void MaiD(String var0, String var1, Throwable var2) {
        if (ifMaiSelf) {
            Log.d(TAG, getMsg(var0,var1), var2);
        }

    }

    public static void MaiI(String var0, String var1) {
        if (ifMaiSelf) {
            Log.i(TAG, getMsg(var0,var1));
        }

    }

    public static void MaiW(String var0, String var1) {
        if (ifMaiSelf) {
            Log.w(TAG, getMsg(var0,var1));
            saveLog((Context) null, "warn:" + var1, 2);
        }

    }

    public static void MaiE(String var0, String var1) {
        if (ifMaiSelf) {
            Log.e(TAG, getMsg(var0,var1));
            saveLog((Context) null, "error:" + var1, 2);
        }

    }

    public static void MaiE(String var0, String var1, Throwable var2) {
        if (ifMaiSelf) {
            Log.e(TAG, getMsg(var0,var1), var2);
            saveLog((Context) null, "error:" + var1 + var2, 2);
        }

    }

    public static void saveLog(Context context, String saveMsg, int savePlace) {     //存储log
        L.e("savePath1");
        try {
            if (!saveLogOrNot) {
                return;
            }

            L.e("savePath2" + mContext + context);

            if (mContext == null) {
                if (context == null) {
                    return;
                }
                L.e("savePath3");

                mContext = context;
            }

            if (!"mounted".equals(Environment.getExternalStorageState())) {      //内存卡没有
                savePlace = 0;
            }

            String savePath = null;

            switch (savePlace) {
                case 0:
                    savePath = mContext.getCacheDir().getPath();
                    break;
                case 1:
                    savePath = mContext.getExternalCacheDir().getPath();
                    break;
                case 2:
                    savePath = Environment.getExternalStorageDirectory().getPath();
            }

            L.e("savePlace" + savePlace + savePath);

            if (TextUtils.isEmpty(savePath)) {
                //                g("Ad_Android_SDK", "path of log is invalid, change to cache path");
                savePath = mContext.getCacheDir().getPath();
            }

            saveMsg = "\n" + MaiUtils.getCurrentTime() + "   " + saveMsg;
            String fileName = "mai_log.txt";
            String path = LogFileUtil.save(mContext, saveMsg, savePath, fileName, true);
            L.e("savePath" + path);

        } catch (Exception e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
        }

    }


    private static String getMsg(Object... msgs) {
        if (msgs == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Object msg : msgs) {
            builder.append(" -- ");
            builder.append(msg);
        }
        return builder.toString();
    }


}
