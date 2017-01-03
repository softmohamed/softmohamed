package com.zakramlock.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.orm.SugarContext;
import com.zakramlock.UnlockComposer;
import com.zakramlock.config.Config;
import com.zakramlock.config.Launcher;
import com.zakramlock.config.ZakramDB;
import com.zakramlock.model.AppItem;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class LockServiceBinder extends Service {

    private static final String TAG = LockServiceBinder.class.getName() ;
    private Thread dThread;
    public final static boolean threadIsTerminate = false;
    private Config conf;
    private Launcher mLauncher;


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e(TAG, "onDestroy1");
        //startService(new Intent("com.zakramlock.service.LockServiceBinder").setPackage("com.zakramlock.service"));
        startService(new Intent(this, LockServiceBinder.class));
        //startForeground();
    }

    private Runnable checkDataRunnable = new Runnable() {

        @Override
        public void run() {
            while (!threadIsTerminate) {
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
                        //    Log.e(TAG, "mPackageName "+ mPackageName +" UnlockComposer "+itm);
                            if(itm != null && !mPackageName.contains("com.zakramlock")){
                               /* if(conf.getAllowedPackage()!=null && conf.getAllowedPackage().equals(mPackageName))
                                    return; */
                                unlockPackage(itm.getPackageName(), itm.getName());
                                continue;
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
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    // good practice
                    dThread.interrupt();
                    e.printStackTrace();
                    return;
                }
            }
            }
        };


    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    // if(SugarContext.getSugarContext() == null){
            SugarContext.init(getApplicationContext());
        conf = Config.getInstance(getApplicationContext());
        mLauncher = new UnlockComposer(getApplicationContext());
        Log.e(TAG, "onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onStartCommand");
        dThread = new Thread(checkDataRunnable);
        dThread.start();
        flags = Service.START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private void unlockPackage(String mPackage, String mActivity){
        mLauncher.onUnlockLauncher(mPackage, mActivity);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return false;
    }

//com.android.packageinstaller
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
