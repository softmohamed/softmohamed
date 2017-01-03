package com.zakramlock;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.azoft.carousellayoutmanager.DefaultChildSelectionListener;
import com.orm.SugarContext;
import com.zakramlock.adapter.AppsViewAdapter;
import com.zakramlock.adapter.RecycleViewAdapter;
import com.zakramlock.animator.DisplayNextView;
import com.zakramlock.animator.Flip3dAnimation;
import com.zakramlock.config.Common;
import com.zakramlock.config.Config;
import com.zakramlock.config.SecurityMethod;
import com.zakramlock.config.ZakramDB;
import com.zakramlock.haibison.android.lockpattern.LockPatternActivity;
import com.zakramlock.haibison.android.lockpattern.utils.AlpSettings;
import com.zakramlock.model.AppItem;
import com.zakramlock.pin.PinUnlockActivity;
import com.zakramlock.pin.SetPinActivity;
import com.zakramlock.service.LockServiceBinder;
import com.zakramlock.service.MonitorService;

import java.util.ArrayList;
import java.util.List;

import haibison.android.underdogs.Nullable;


public class ZakraM extends AppCompatActivity {

    private GridView gridView;
    private TextView noLockedAppMsg;
    private TextView updateSecureMethod;
    private List<AppItem> lockedAppItems;
    private List<AppItem> noLockedAppItems;
    private LinearLayout settingsLayout;
    private LinearLayout methodSecurityLayout;
    private LinearLayout settings_page;
    private LinearLayout lock_app_page;
    private LinearLayout general_layout;
    private LinearLayout privacy_layout;
    private ImageView setting_action;
    private ImageView general_action;
    private TextView methodSecurityText;
    private RecyclerView recycle_view_horizontal;
    private RecycleViewAdapter recycleViewAdapter;
    private AppsViewAdapter lockedAppsViewAdapter;
    private Switch enableOntartup;
    private RadioGroup securityMethodsRadio;

    private RadioButton radioPattern;
    private RadioButton radioPin;

    private boolean isFirstImage = true;
    private static final int REQ_CREATE_PATTERN = 1;
    private static final int REQ_SECURE_QUESTION = 10001;
    private static final int REQ_COMPARE_PATTERN = 1001;
    private static final int REQ_USAGE_STATUS = 11;
    private static final String FIRST_LOGIN = "firstLogin";
    private Config conf ;
    private SharedPreferences appSettings;

    private static final String TAG = ZakraM.class.getName();


    private  void enabledSlide(boolean l, boolean s){
        lock_app_page.setEnabled(l);
        lock_app_page.setClickable(l);

        settings_page.setClickable(s);
        settings_page.setEnabled(s);
    }

    private boolean hasApp(List<AppItem> l, String appP){
        boolean has = false;
        for(int o = 0; o< l.size(); o++){
            if(l.get(o).getPackageName().equals(appP)){
                has = true;
                break;
            }
        }
        return has;
    }

    private void addShortcut() {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(), ZakraM.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.name_app));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.ic_launcher));
        addIntent.putExtra("duplicate", false);
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);

        SharedPreferences.Editor prefEditor = appSettings.edit();
        prefEditor.putBoolean("shortcut", true);
        prefEditor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakram);
        SugarContext.init(this);

        gridView = (GridView) findViewById(R.id.grid_view_app);
        noLockedAppMsg = (TextView) findViewById(R.id.no_locked_app_found);

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        conf = Config.getInstance(getApplicationContext());

        appSettings = getSharedPreferences("APP_NAME", MODE_PRIVATE);
        // Make sure you only run addShortcut() once, not to create duplicate shortcuts.
        if(!appSettings.getBoolean("shortcut", false)) {
            addShortcut();
        }
        conf.setUpdateSecure(false);
        conf.setChange(null);

        lockedAppItems = ZakramDB.getLockedApps();
        //Log.e(TAG, lockedAppItems.toString());

        noLockedAppItems  = new ArrayList<AppItem>();
        List<ResolveInfo> installedApps = getPackageManager()
                .queryIntentActivities(mainIntent, 0);

        for(int i=0; i<installedApps.size(); i++){
            if(!installedApps.get(i).activityInfo.loadLabel(getPackageManager()).toString().equals(getString(R.string.name_app))){
                AppItem app = new AppItem(installedApps.get(i).activityInfo.loadLabel(getPackageManager()).toString(),
                        installedApps.get(i).activityInfo.name, installedApps.get(i).activityInfo.packageName,
                        installedApps.get(i).activityInfo.loadIcon(getPackageManager()));

                if(app.getPackageName().equals("com.android.vending")){
                    if(!hasApp(lockedAppItems, app.getPackageName())){
                        lockedAppItems.add(app);
                        ZakramDB.lockApp(app);
                    }
                }
                // com.android.packageinstaller

                if(app.getPackageName().equals("com.android.packageinstaller")){
                    if(!hasApp(lockedAppItems, app.getPackageName())){
                        lockedAppItems.add(app);
                        ZakramDB.lockApp(app);
                    }
                }

                noLockedAppItems.add(app);
            }
        };

        lockedAppsViewAdapter = new AppsViewAdapter(getApplicationContext(), lockedAppItems);
        gridView.setAdapter(lockedAppsViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ZakraM.this, R.style.dialog));
                alertDialogBuilder.setTitle(R.string.name_app);
                alertDialogBuilder
                        .setMessage(getString(R.string.unlock_app_msg, lockedAppItems.get(position).getLabel()))
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                noLockedAppItems.add(lockedAppItems.get(position));
                                recycleViewAdapter.notifyDataSetChanged();
                                ZakramDB.UnlockApp(lockedAppItems.get(position));

                                lockedAppItems.remove(position);
                                lockedAppsViewAdapter.notifyDataSetChanged();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton( R.string.no,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        if(lockedAppItems.size() == 0){

            gridView.setVisibility(View.GONE);
            noLockedAppMsg.setVisibility(View.VISIBLE);
        }

        settings_page = (LinearLayout) findViewById(R.id.settings_page);
        lock_app_page = (LinearLayout) findViewById(R.id.lock_app_page);
        //lock_app_page.setVisibility(View.GONE);
        settings_page.setVisibility(View.GONE);

        ImageView toLeft = (ImageView) findViewById(R.id.toleft);
        toLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFirstImage) {
                    applyRotation(0, 90);
                    isFirstImage = !isFirstImage;
                    enabledSlide(false, true);
                } else {
                    applyRotation(0, -90);
                    isFirstImage = !isFirstImage;
                    enabledSlide(true, false);
                }
            }
        });

        ImageView toRight = (ImageView) findViewById(R.id.toright);
        toRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFirstImage) {
                    applyRotation(0, 90);
                    isFirstImage = !isFirstImage;
                    enabledSlide(false, true);
                } else {
                    applyRotation(0, -90);
                    isFirstImage = !isFirstImage;
                    enabledSlide(true, false);
                }
            }
        });

        settingsLayout = (LinearLayout) findViewById(R.id.setting_layout);
        setting_action = (ImageView) findViewById(R.id.setting_action);
        setting_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(settingsLayout.getVisibility() == View.GONE){
                    settingsLayout.setVisibility(View.VISIBLE);
                }else {
                    settingsLayout.setVisibility(View.GONE);
                }
            }
        });
        general_layout = (LinearLayout) findViewById(R.id.general_layout);
        general_action = (ImageView) findViewById(R.id.general_action);
        general_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(general_layout.getVisibility() == View.GONE){
                    general_layout.setVisibility(View.VISIBLE);
                }else {
                    general_layout.setVisibility(View.GONE);
                }
            }
        });


        methodSecurityLayout = (LinearLayout) findViewById(R.id.security_method_layout);
        methodSecurityText = (TextView) findViewById(R.id.security_action);
        methodSecurityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(methodSecurityLayout.getVisibility() == View.GONE){
                    methodSecurityLayout.setVisibility(View.VISIBLE);
                }else {
                    methodSecurityLayout.setVisibility(View.GONE);
                }
            }
        });

        // https://www.tutorialspoint.com/android/android_drag_and_drop.htm
        recycle_view_horizontal = (RecyclerView) findViewById(R.id.recycle_view_horizontal);

        //https://github.com/binaryroot/CarouselView  == https://android-arsenal.com/details/1/3991#!description
        //https://github.com/Azoft/CarouselLayoutManager == integrated
        //https://www.codeproject.com/articles/146145/android-3d-carousel
        // http://evgeni-shafran.blogspot.com/2011/08/tutorial-custom-gallery-circular-and.html

        for(int i=0; i< lockedAppItems.size() ; i++){
            AppItem itm = lockedAppItems.get(i);
            for(int j = 0; j< noLockedAppItems.size(); j++){
                AppItem jtm = noLockedAppItems.get(j);
                if(itm.getLabel().equals(jtm.getLabel())){
                    noLockedAppItems.remove(j);
                    break;
                }
            }
        }

        recycleViewAdapter = new RecycleViewAdapter(noLockedAppItems);
        initRecyclerView(recycle_view_horizontal, new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true), recycleViewAdapter);

        enableOntartup = (Switch) findViewById(R.id.enable_on_startup);
        enableOntartup.setChecked(conf.isEnableOnStartUp());
        enableOntartup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                conf.setEnableOnStartUp(b);
            }
        });

        securityMethodsRadio = (RadioGroup) findViewById(R.id.security_methods_radio);
        radioPattern  = (RadioButton) findViewById(R.id.method_pattern);
        radioPin  = (RadioButton) findViewById(R.id.method_pin);
        checkSecureMethod(conf.getSecurityMethod());

        privacy_layout = (LinearLayout) findViewById(R.id.privacy_layout);
        TextView privacy_terms = (TextView) findViewById(R.id.privacy_terms);

        TextView privacy_terms_msg = (TextView) findViewById(R.id.privacy_terms_msg);
        privacy_terms_msg.setText(Html.fromHtml(getString(R.string.privacy_terms_text)));
        privacy_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privacy_layout.getVisibility() == View.GONE){
                    privacy_layout.setVisibility(View.VISIBLE);
                }else {
                    privacy_layout.setVisibility(View.GONE);
                }
            }
        });
        securityMethodsRadio.setOnCheckedChangeListener(radioMethodListener);
        TextView version = (TextView) findViewById(R.id.version);
         version.setText(getString(R.string.version, BuildConfig.VERSION_NAME));

        updateSecureMethod = (TextView)findViewById(R.id.updateSecureMethod);
        updateSecureMethod.setText(Html.fromHtml(getString(R.string.update_msg, conf.getSecurityMethod())));
        updateSecureMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conf.setUpdateSecure(true);
                defineSecureLevel();
            }
        });
        //startMonitor();
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            requestUsageStatsPermission();
        }else {
            security();
        }

    }
    RadioGroup.OnCheckedChangeListener  radioMethodListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(i);
            conf.setChange("change");
            conf.setUpdateSecure(false);
            String from = conf.getSecurityMethod();
            String to = checkedRadioButton.getText().toString();
            conf.setSecurityMethod(to);
            conf.setOldM(from);
            defineSecureLevel();
        }
    };
    private void checkSecureMethod(String mth){
        switch (mth){
            case "Pin"  : radioPin.setChecked(true);break;
            case "Pattern"  : radioPattern.setChecked(true);break;
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
       if(!Common.isMyServiceRunning(LockServiceBinder.class, getApplicationContext())){
            startMonitor();
        }
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onStop() {

        getDelegate().onStop();

        if(!Common.isMyServiceRunning(LockServiceBinder.class, getApplicationContext())){
            startMonitor();
        }
        if(!conf.isZakram())
            finish();
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy(){

       if(!Common.isMyServiceRunning(LockServiceBinder.class, getApplicationContext())){
            startMonitor();
        }
        if(!conf.isZakram())
            finish();
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected  void onPause(){
        if(!Common.isMyServiceRunning(LockServiceBinder.class, getApplicationContext())){
            startMonitor();
        }
        if(!conf.isZakram())
            finish();
        Log.e(TAG, "onPause");
        super.onPause();
    }

    private void security(){
        //if(ZakramDB.getRessources().size() == 0 || ZakramDB.getRessources().get(0).isFirst()){
        if(appSettings.getBoolean(FIRST_LOGIN, true)){
            defineSecureLevel();
        }else {
            if(!Common.isMyServiceRunning(LockServiceBinder.class, getApplicationContext())){
                startMonitor();
            }
            zakramlock();
        }
    }
    private void startMonitor(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent monitor = new Intent(ZakraM.this, LockServiceBinder.class);
               // Intent monitor = new Intent("com.zakramlock.service.LockServiceBinder").setPackage("com.zakramlock.service");
                startService(monitor);
           }
        }).start();
    }

    private void requestUsageStatsPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            tryAllowAccessToStatsManager();
        }else {
            security();
        }
    }

    private void tryAllowAccessToStatsManager(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialog));
        alertDialogBuilder.setTitle(R.string.name_app);
        alertDialogBuilder
                .setMessage(R.string.enable_stat_manager)
                .setCancelable(false)
                .setPositiveButton(R.string.allow,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        conf.setIsZakram(true);
                        dialog.dismiss();
                        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), REQ_USAGE_STATUS);
                    }
                })
                .setNegativeButton(R.string.do_it_myself,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                        ZakraM.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }
    private void secureQuestion(){
        Intent intent = new Intent(this, SecretQuestions.class);
        startActivityForResult(intent, REQ_SECURE_QUESTION);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_SECURE_QUESTION :{
                switch (resultCode){
                    case RESULT_OK: {
                        SharedPreferences.Editor prefEditor = appSettings.edit();
                        prefEditor.putBoolean(FIRST_LOGIN, false);
                        prefEditor.apply();
                        prefEditor.commit();
                        //conf.setFirstTime(false);
                        conf.setSecurityMethod(SecurityMethod.pattern.getSm());
                        //startMonitor();
                        conf.setIsZakram(false);
                        break;
                    }
                    case RESULT_CANCELED: {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialog));
                        alertDialogBuilder.setTitle(R.string.name_app);
                        alertDialogBuilder
                                .setMessage(R.string.mandatory_secure_question_msg)
                                .setCancelable(false)
                                .setPositiveButton(R.string.try_again,new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.dismiss();
                                        secureQuestion();
                                    }
                                })
                                .setNegativeButton(R.string.quit,new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.dismiss();
                                        ZakraM.this.finish();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        break;
                    }
                }
                break;
            }
            case REQ_CREATE_PATTERN: {
                switch (resultCode){
                    case RESULT_OK: {
                        if(conf.isUpdateSecure()){
                            Toast.makeText(getApplicationContext() , conf.getSecurityMethod()+" Updated", Toast.LENGTH_SHORT).show();
                        }else  if(conf.getChange() == null){
                            secureQuestion();
                        }
                        conf.setChange(null);
                        updateSecureMethod.setText(Html.fromHtml(getString(R.string.update_msg, conf.getSecurityMethod())));
                        conf.setUpdateSecure(false);
                        break;
                    }
                    case RESULT_CANCELED: {
                        if(conf.isUpdateSecure()){
                            Toast.makeText(getApplicationContext() , conf.getSecurityMethod()+" Not Updated", Toast.LENGTH_SHORT).show();
                        }else if(conf.getChange() == null){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialog));
                            alertDialogBuilder.setTitle(R.string.name_app);
                            alertDialogBuilder
                                    .setMessage(R.string.pattern_required)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.try_again,new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.dismiss();
                                            defineSecureLevel();
                                        }
                                    })
                                    .setNegativeButton(R.string.quit,new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.dismiss();
                                            ZakraM.this.finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }else if(conf.getChange() != null){
                            securityMethodsRadio.setOnCheckedChangeListener(null);
                            conf.setSecurityMethod(conf.getOldM());
                            checkSecureMethod(conf.getOldM());
                            securityMethodsRadio.setOnCheckedChangeListener(radioMethodListener);
                        }
                        conf.setChange(null);
                        updateSecureMethod.setText(Html.fromHtml(getString(R.string.update_msg, conf.getSecurityMethod())));
                        conf.setUpdateSecure(false);
                        break;
                    }
                }
                break;
            }
            case  REQ_COMPARE_PATTERN : {
                switch (resultCode) {
                    case RESULT_OK:
                       // startMonitor();
                        conf.setIsZakram(false);
                        break;
                    case RESULT_CANCELED:
                        this.finish();
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        zakramlock();
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        zakramlock();
                        break;
                }
                break;
            }
            case REQ_USAGE_STATUS: {
                if(!hasUsageStatsPermission(getApplicationContext())){
                    tryAllowAccessToStatsManager();
                }else{
                    conf.setIsZakram(false);
                    security();
                }
                break;
            }
        }
    }

     private void defineSecureLevel(){
         conf.setIsZakram(true);
         if(SecurityMethod.pattern.getSm().equals(conf.getSecurityMethod())){
             definePattern();
         }else if(SecurityMethod.pin.getSm().equals(conf.getSecurityMethod())){
             definePin();
         }
     }
    private  void definePattern(){
        AlpSettings.Security.setAutoSavePattern(getApplicationContext(), true);
        LockPatternActivity.IntentBuilder
                .newPatternCreator(getApplicationContext()).setLayout(R.layout.lpv)
                .startForResult(ZakraM.this, REQ_CREATE_PATTERN);
    }

    private void definePin(){
        Intent intent = new Intent(this, SetPinActivity.class);
       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQ_CREATE_PATTERN);
    }
    private  void zakramlock(){
        conf.setIsZakram(true);
        if(SecurityMethod.pattern.getSm().equals(conf.getSecurityMethod())){
            LockPatternActivity.IntentBuilder.newPatternComparator(getApplicationContext()).startForResult(ZakraM.this, REQ_COMPARE_PATTERN);
        }else if(SecurityMethod.pin.getSm().equals(conf.getSecurityMethod())){
            Intent intent = new Intent(ZakraM.this, PinUnlockActivity.class);
           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, REQ_COMPARE_PATTERN);
        }
    }

    private void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager, final RecycleViewAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(7);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new CenterScrollListener());

        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
                final int position = recyclerView.getChildLayoutPosition(v);
                //Toast.makeText(ZakraM.this, noLockedAppItems.get(position).getLabel(), Toast.LENGTH_SHORT).show();
                gridView.setVisibility(View.VISIBLE);
                noLockedAppMsg.setVisibility(View.GONE);
                lockedAppItems.add(noLockedAppItems.get(position));
                lockedAppsViewAdapter.notifyDataSetChanged();
               // Log.e(TAG ,noLockedAppItems.get(position).toString());
                ZakramDB.lockApp(noLockedAppItems.get(position));
                noLockedAppItems.remove(position);
                adapter.notifyDataSetChanged();
            }
        }, recyclerView, layoutManager);

        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {

                }
            }
        });
    }

    private void applyRotation(float start, float end) {

        final float centerX = lock_app_page.getWidth() / 2.0f;
        final float centerY = lock_app_page.getHeight() / 2.0f;

        final Flip3dAnimation rotation =
                new Flip3dAnimation(start, end, centerX, centerY);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(isFirstImage, lock_app_page, settings_page));
        if (isFirstImage)
        {
            lock_app_page.startAnimation(rotation);
        } else {
            settings_page.startAnimation(rotation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_msg_p1));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_msg));
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
