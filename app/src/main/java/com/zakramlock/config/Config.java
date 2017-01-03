package com.zakramlock.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Devon 12/15/2016.
 */

public class Config {

    private SharedPreferences PREFS;
    private static Config instance;
    private static final String PREFS_NAME = "Zakram";
    private final String enableOnStartUpName = "enableOnStartUp";
    private final String securityMethodName = "securityMethod";
    private final String isFirstTime = "firstTime";

    private Config(Context context) {
        assert context != null;
        PREFS = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public static synchronized Config getInstance(Context context) {
        if (instance == null) instance = new Config(context);
        return instance;
    }

    public boolean isEnableOnStartUp() {
        return PREFS.getBoolean(enableOnStartUpName, true);
    }

    public void setEnableOnStartUp(boolean val) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putBoolean(enableOnStartUpName, val);
            editor.apply();
        }
    }

    public void setSecurityMethod(String sm) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putString(securityMethodName, sm);
            editor.apply();
        }
    }

    public String getSecurityMethod() {
        return PREFS.getString(securityMethodName, SecurityMethod.pattern.getSm());
    }

    public boolean isFirstTime() {
        return PREFS.getBoolean(isFirstTime, true);
    }

    public void setFirstTime(boolean ft) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putBoolean(isFirstTime, ft);
            editor.apply();
        }
    }

    public String getAllowedPackage() {
        return PREFS.getString("allowedPackage", null);
    }

    public void setAllowedPackage(String ap) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putString("allowedPackage", ap);
            editor.apply();
        }
    }

    public String getChange() {
        return PREFS.getString("change", null);
    }

    public void setChange(String ch) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putString("change", ch);
            editor.apply();
        }
    }

    public String getOldM() {
        return PREFS.getString("oldM", null);
    }

    public void setOldM(String ch) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putString("oldM", ch);
            editor.apply();
        }
    }


    public String getSecureQ() {
        return PREFS.getString("SecureQ", null);
    }

    public void setSecureQ(String ch) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putString("SecureQ", ch);
            editor.apply();
        }
    }

    public String getAnswerQ() {
        return PREFS.getString("AnswerQ", null);
    }


    public void setAnswerQ(String ch) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putString("AnswerQ", ch);
            editor.apply();
        }
    }

    public boolean isZakram() {
        return PREFS.getBoolean("isZakram", false);
    }


    public void setIsZakram(boolean ch) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putBoolean("isZakram", ch);
            editor.apply();
        }
    }

    public boolean isZakramOnPause() {
        return PREFS.getBoolean("pause", true);
    }


    public void setZakramOnPause(boolean pause) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putBoolean("pause", pause);
            editor.apply();
        }
    }

    public boolean isUpdateSecure() {
        return PREFS.getBoolean("updateSecure", false);
    }


    public void setUpdateSecure(boolean updateSecure) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putBoolean("updateSecure", updateSecure);
            editor.apply();
        }
    }

}
