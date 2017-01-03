package com.zakramlock.pin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.kbeanie.pinscreenlibrary.storage.PINPreferences;
import com.kbeanie.pinscreenlibrary.views.PinEntrySetupListener;
import com.kbeanie.pinscreenlibrary.views.PinView;
import com.zakramlock.R;
import com.zakramlock.config.Config;

public class SetPinActivity extends AppCompatActivity implements PinEntrySetupListener {

    private PinView pinView;
    private static final String TAG = SetPinActivity.class.getName();
    private PINPreferences preferences;
    private String oldPin = null;
    private boolean changed = false;
    private Config conf;
    private NativeExpressAdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        conf = Config.getInstance(getApplicationContext());
        adView = (NativeExpressAdView)findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .build();
        adView.loadAd(request);
        preferences = new PINPreferences(this);
        if(conf.isUpdateSecure()){
            oldPin = preferences.getPIN();
        }
        clearPIN();
        pinView = (PinView) findViewById(R.id.pinView);
        pinView.setModeSetup(this);
    }

    private void clearPIN() {
        preferences.clearPIN();
    }

    @Override
    protected void onResume() {

        if (adView != null) {
        adView.resume();
        }
        super.onResume();
        if(!changed && conf.isUpdateSecure()){
            preferences.setPIN(oldPin);
        }

    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }

        super.onPause();
        if(!changed &&  conf.isUpdateSecure()){
            preferences.setPIN(oldPin);
        }
    }

    @Override
    protected void onDestroy() {

        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
        if(!changed && conf.isUpdateSecure()){
            preferences.setPIN(oldPin);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Use this hook instead of onBackPressed(), because onBackPressed() is not available in API 4.
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(conf.isUpdateSecure()){
                preferences.setPIN(oldPin);
            }
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }//if

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPinEntered(String pin) {
       // Log.e(TAG, " onPinEntered "+pin);
    }

    @Override
    public void onPinConfirmed(String pin) {
       // Log.e(TAG, " onPinConfirmed "+pin);
    }

    @Override
    public void onPinMismatch() {
       // Log.e(TAG, " onPinMismatch ");
    }

    @Override
    public void onPinSet(String pin) {
        setResult(RESULT_OK);
        preferences.setPIN(pin);
        changed = true;
        finish();
    }
}
