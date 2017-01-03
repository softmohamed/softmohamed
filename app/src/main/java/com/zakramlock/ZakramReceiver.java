package com.zakramlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orm.SugarContext;
import com.zakramlock.config.Common;
import com.zakramlock.config.Config;
import com.zakramlock.service.LockServiceBinder;
import com.zakramlock.service.MonitorService;

public class ZakramReceiver extends BroadcastReceiver {
    public ZakramReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Config  conf = Config.getInstance(context);
        SugarContext.init(context);
        if(conf.isEnableOnStartUp()){
            if(!Common.isMyServiceRunning(MonitorService.class, context))
                startMonitor(context);
        }
    }

    private void startMonitor(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent monitor = new Intent(context, LockServiceBinder.class);
               // Intent monitor = new Intent("com.zakramlock.service.LockServiceBinder").setPackage("com.zakramlock.service");
                context.startService(monitor);
            }
        }).start();
    }
}
