package com.zakramlock.config;

/**
 * Created by Devon 12/15/2016.
 */

public enum SecurityMethod {
    pattern("Pattern"),
    pin("Pin"),
    face("Face"),
    pasword("Password"),
    voice("Voice");

    private String sm;

    public String getSm() {
        return this.sm;
    }

    SecurityMethod(String sm) {
        this.sm = sm;
    }
}
