package com.admai.sdk.util;

import com.admai.sdk.util.log.LogUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by macmi001 on 16/7/4.
 * MD5
 */
public class MD5 {
    public static String getMD5(String content) {
        if (content == null)
            return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);

        } catch (NoSuchAlgorithmException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }
}
