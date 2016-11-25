package com.admai.sdk.util.log;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ZAN on 16/9/9.
 */
public class LogFileUtil {
    private static final String TAG = LogFileUtil.class.getSimpleName();
    ;
    
    public LogFileUtil() {
    }
    //  LogFileUtil.save(mContext, saveMsg, savePath, fileName, true);

    public static String save(Context context, String saveMsg, String savePath, String fileName, boolean isCTN) throws Exception {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos = null;
        File file = null;

        try {
            byte[] saveMsgBytes = saveMsg.getBytes();
            if (messageDigest != null) {
                messageDigest.update(saveMsgBytes);
            }

            if (TextUtils.isEmpty(savePath)) {   //确保savePath存在
                savePath = getSavePath(context, savePath);
            }

            file = new File(savePath, fileName);
            fos = new FileOutputStream(file, isCTN);
            fos.write(saveMsgBytes, 0, saveMsgBytes.length);
            fos.flush();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    ;
                }

                fos = null;
            }

        }

        String path = null;
        if (null != file) {
            path = file.getPath();
        }

        return path;
    }

    public static String saveLogs(Context context, String saveMsg, String savePath, String fileName, boolean isCTN) throws Exception {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos = null;
        File file = null;

        try {
            byte[] saveMsgBytes = saveMsg.getBytes();
            if (messageDigest != null) {
                messageDigest.update(saveMsgBytes);
            }

            if (TextUtils.isEmpty(savePath)) {   //确保savePath存在
                savePath = getSavePath(context, savePath);
            }

            file = new File(savePath, fileName);
            fos = new FileOutputStream(file, isCTN);
            fos.write(saveMsgBytes, 0, saveMsgBytes.length);
            fos.flush();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    ;
                }

                fos = null;
            }

        }

        String path = null;
        if (null != file) {
            path = file.getPath();
        }

        return path;
    }

    public static String getSavePath(Context context, String savePath) {
        try {
            if (TextUtils.isEmpty(savePath)) {
                savePath = context.getCacheDir().getPath();
            }

            File file = new File(savePath);
            file.mkdirs();
            return savePath;
        } catch (Exception e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

