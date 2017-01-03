package com.zakramlock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zakramlock.config.Common;
import com.zakramlock.haibison.android.lockpattern.LockPatternActivity;
import com.zakramlock.haibison.android.lockpattern.utils.AlpSettings;

public class UnlockPattern extends AppCompatActivity {

    private static final int REQ_COMPARE_PATTERN = 1;
    private static final String  TAG = UnlockPattern.class.getName();
    private String  blockedPackageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlpSettings.Security.setAutoSavePattern(UnlockPattern.this,
                true);
        Intent intent = getIntent();
        blockedPackageName = intent.getStringExtra("package");
       zakramlock();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case  REQ_COMPARE_PATTERN : {
                // for failed must ask for secret answer
                switch (resultCode) {
                    case RESULT_OK:
                        startApp();
                        break;
                    case RESULT_CANCELED:
                        this.finish();
                        cancel();
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
        }
    }
    @Override
    public void onBackPressed() {
        this.onBackPressed();
        cancel();
    }

    private void cancel(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private void  zakramlock(){
        LockPatternActivity.IntentBuilder.newPatternComparator(getApplicationContext()).
                startForResult(UnlockPattern.this, REQ_COMPARE_PATTERN);
    }

    private void startApp(){
        this.sendBroadcast(new Intent().setAction(Common.ACTION_APPLICATION_PASSED)
                .putExtra(Common.EXTRA_PACKAGE_NAME,blockedPackageName));
        this.finish();
    }
}
