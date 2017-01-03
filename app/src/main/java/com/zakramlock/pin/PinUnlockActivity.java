package com.zakramlock.pin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.kbeanie.pinscreenlibrary.storage.PINPreferences;
import com.kbeanie.pinscreenlibrary.views.PinEntryAuthenticationListener;
import com.kbeanie.pinscreenlibrary.views.PinView;
import com.zakramlock.R;
import com.zakramlock.config.Common;
import com.zakramlock.config.Config;
import com.zakramlock.haibison.android.lockpattern.LockPatternActivity;

public class PinUnlockActivity extends AppCompatActivity implements PinEntryAuthenticationListener {
    private static  final String TAG =PinUnlockActivity.class.getName();
    private PinView pinView;
    private String blockedPackageName;
    private String blockedActivityName;
    private boolean fromService = false;
    private NativeExpressAdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        Config config = Config.getInstance(getApplicationContext());
        adView = (NativeExpressAdView)findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .build();
        adView.loadAd(request);


        Intent i = getIntent();
        /*
        * i.putExtra("package", packageName);
                i.putExtra("activity", activityName);
        * */
        PINPreferences preferences = new PINPreferences(this);
        if(i.hasExtra("package") && i.hasExtra("activity") ){
            fromService = true;
            blockedPackageName = i.getStringExtra("package");
            blockedActivityName = i.getStringExtra("activity");
        }
        pinView = (PinView) findViewById(R.id.pinView);
        pinView.setModeAuthenticate(this);
        View vv = (View) findViewById(R.id.view_recovery);
        if(config.isZakram()){
            vv.setVisibility(View.VISIBLE);
            vv.setEnabled(true);
        }else {
            vv.setVisibility(View.GONE);
            vv.setEnabled(false);
        }

        Button recovery = (Button) findViewById(R.id.button_recovery_pin);
        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Config conf = Config.getInstance(getApplicationContext());

                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.question_diag, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(PinUnlockActivity.this, R.style.dialog));
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.answer);
                userInput.setTextColor(getResources().getColor(R.color.dotbaseColor));
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle(getString(R.string.secure_question_msg))
                        .setMessage(conf.getSecureQ())
                        .setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                    }
                                })
                        .setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.dismiss();
                                    }
                                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!userInput.getText().toString().isEmpty()){
                            if(userInput.getText().toString().equals(conf.getAnswerQ())){
                                alertDialog.dismiss();
                                setResult(Activity.RESULT_OK);
                                finish();
                            }else {
                                userInput.setBackgroundColor(getResources().getColor(R.color.alp_42447968_lock_pattern_view_error_light));
                            }
                        }else {
                            userInput.setBackgroundColor(getResources().getColor(R.color.alp_42447968_lock_pattern_view_error_light));
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onPinCorrect() {
        if (fromService) {
            Intent i = new Intent();
            i.setAction(Common.ACTION_APPLICATION_PASSED);
            i.putExtra(Common.EXTRA_PACKAGE_NAME,blockedPackageName);
            Log.e(TAG, "blockedPackageName "+ blockedPackageName);
            this.sendBroadcast(i);
            setResult(RESULT_OK);
            this.finish();
        }else {
            setResult(RESULT_OK);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        this.onBackPressed();
        if(adView != null){
            adView.destroy();
        }
        if (fromService) {
            cancel();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        if (adView != null) {
            adView.pause();
        }
        super.onPause();


    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (adView != null) {
            adView.resume();
        }

        super.onResume();

    }

    private void cancel(){
        Log.e(TAG, "cancel");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPinWrong() {
        Log.e("piun", "onPinWrong");
       // finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Use this hook instead of onBackPressed(), because onBackPressed() is not available in API 4.
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }//if

        return super.onKeyDown(keyCode, event);
    }
}
