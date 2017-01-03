package com.zakramlock;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.zakramlock.config.Common;
import com.zakramlock.config.Config;
import com.zakramlock.config.Launcher;
import com.zakramlock.config.SecurityMethod;
import com.zakramlock.pin.PinUnlockActivity;

/**
 * Created by Devon 12/16/2016.
 */

public class UnlockComposer implements Launcher {

    private Handler handler;
    private Context mContext;
    private Config conf;
    private final static String TAG = UnlockComposer.class.getName();
    public UnlockComposer(Context context) {
        mContext = context;
        handler = new Handler(Looper.getMainLooper());
        conf = Config.getInstance(context.getApplicationContext());
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Common.ACTION_APPLICATION_PASSED)){
                    String allowedPackage = intent
                            .getStringExtra(Common.EXTRA_PACKAGE_NAME);
                    if(allowedPackage != null){
                        conf.setAllowedPackage(allowedPackage);
                      //  Log.e("postDelay", "allowedPackage "+allowedPackage);
                        if(!allowedPackage.equals(conf.getAllowedPackage()))
                            handler.postDelayed(new DisableAllowedPackage(), 1000 * 60);
                    }

                }
            }
        }, new IntentFilter(Common.ACTION_APPLICATION_PASSED));
    }

    @Override
    public void onUnlockLauncher(String packageName, String activityName) {
        synchronized (this) {
            if (conf.getAllowedPackage() != null && conf.getAllowedPackage().equals(packageName) )
                return;

                conf.setIsZakram(false);
                Intent i = null;
                if (SecurityMethod.pattern.getSm().equals(conf.getSecurityMethod())) {
                    i = new Intent(mContext, UnlockPattern.class);
                }else if(SecurityMethod.pin.getSm().equals(conf.getSecurityMethod())){
                    i = new Intent(mContext, PinUnlockActivity.class);
                }
                if(i != null){
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("package", packageName);
                    i.putExtra("activity", activityName);
                    mContext.startActivity(i);
                }
        }
    }

    private class DisableAllowedPackage implements Runnable {
        public void run() {
            conf.setAllowedPackage(null);
        }
    }
}
