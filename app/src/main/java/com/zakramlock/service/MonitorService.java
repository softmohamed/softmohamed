package com.zakramlock.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.zakramlock.UnlockComposer;
import com.zakramlock.config.Config;
import com.zakramlock.config.Launcher;
import com.zakramlock.config.ZakramDB;
import com.zakramlock.model.AppItem;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MonitorService extends IntentService {

    private static Thread mThread;
    private static final String TAG = MonitorService.class.getName() ;
    private Config conf;
    public MonitorService() {
        super("MonitorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (mThread != null)
                mThread.interrupt();
            conf = Config.getInstance(getApplicationContext());
            mThread = new MonitorThread(new UnlockComposer(getApplicationContext()));
            mThread.start();
        }
    }


    private class MonitorThread extends Thread {
        Launcher mListener;
        public MonitorThread(Launcher listener) {
            this.mListener = listener;
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
               ///Log.e(TAG, "Im running");
                try {
                    ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
                    PackageManager pm = getBaseContext().getPackageManager();
                    String mPackageName = null;

                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService("usagestats");
                        long time = System.currentTimeMillis();
                        // We get usage stats for the last 10 seconds
                        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
                        // Sort the stats by the last time used
                        if(stats != null) {
                            SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                            for (UsageStats usageStats : stats) {
                                mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                            }
                            if(!mySortedMap.isEmpty()) {
                                mPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                            }
                        }
                    }else {
                        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager
                                .getRunningTasks(1);
                        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                        ComponentName topActivity = runningTaskInfo.topActivity;
                        mPackageName =  topActivity.getPackageName();
                    }

                    PackageInfo foregroundAppPackageInfo = null;
                    try {
                        if (mPackageName != null) {
                            foregroundAppPackageInfo = pm.getPackageInfo(
                                    mPackageName, 0);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Exception in run method " + e);
                        e.printStackTrace();
                    }
                    if (foregroundAppPackageInfo != null) {
                        String s = foregroundAppPackageInfo.packageName;
                        if (s != null) {
                            AppItem itm = isLocked(mPackageName);
                            Log.e(TAG, "mPackageName "+ mPackageName +" UnlockComposer "+itm);
                            if(itm != null && !mPackageName.contains("com.zakramlock")){
                               /* if(conf.getAllowedPackage()!=null && conf.getAllowedPackage().equals(mPackageName))
                                    return; */

                                mListener.onUnlockLauncher(itm.getPackageName(), itm.getName());
                               // Log.i(TAG, "mPackageName  "+mPackageName + " MainActivity "+ itm.getName());
                            }else {
                                if( conf.getAllowedPackage()!= null && !conf.getAllowedPackage().equals(mPackageName) && !mPackageName.contains("com.zakramlock")){
                                    conf.setAllowedPackage(null);
                                }
                                //Log.i(TAG, "mPackageName  "+mPackageName);
                            }

                        }
                    }else {
                       // Log.e(TAG, "foregroundAppPackageInfo null");
                    }
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // good practice
                    mThread.interrupt();
                    this.interrupt();
                    return;
                }
                }
        }
    }

    /*@Override
    public void onDestroy() {
        startService(new Intent(this, MonitorService.class));
        super.onDestroy();
    }*/

    private AppItem isLocked(String packageName){
        AppItem locked = null;
        List<AppItem> lockedApp = ZakramDB.getLockedApps();
        for(int i= 0; i<lockedApp.size(); i++){
            if(lockedApp.get(i).getPackageName().equals(packageName)){
                locked = lockedApp.get(i);
                break;
            }
        }
        return locked;
    }
}
