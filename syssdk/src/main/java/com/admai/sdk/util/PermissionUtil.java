package com.admai.sdk.util;

import android.content.Context;
import android.content.pm.PackageManager;

import com.admai.sdk.util.log.L;

public class PermissionUtil {

    public static boolean hasPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                L.e("lssPermission", "lack of permission: " + permission);
                return false;
            }
        }
        return true;
    }

}
