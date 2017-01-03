package com.zakramlock.config;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Devon 12/15/2016.
 */

public class Common {

    public static final String ACTION_APPLICATION_PASSED = "com.zakramlock.Lock.APP_PASSED";
    public static final String EXTRA_PACKAGE_NAME = "com.zakramlock.extra.PACKAGE_NAME";

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
