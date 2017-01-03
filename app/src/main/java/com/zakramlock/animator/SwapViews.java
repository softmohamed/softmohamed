package com.zakramlock.animator;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * Created by Devon 12/14/2016.
 */

public final class SwapViews implements Runnable {
    private boolean mIsFirstView;
    LinearLayout layout1;
    LinearLayout layout2;

    public SwapViews(boolean isFirstView, LinearLayout layout1, LinearLayout layout2) {
        mIsFirstView = isFirstView;
        this.layout1 = layout1;
        this.layout2 = layout2;
    }

    public void run() {
        final float centerX = layout1.getWidth() / 2.0f;
        final float centerY = layout1.getHeight() / 2.0f;
        Flip3dAnimation rotation;

        if (mIsFirstView) {
            layout1.setVisibility(View.GONE);
            layout1.setClickable(false);
            layout1.setEnabled(false);

            layout2.setVisibility(View.VISIBLE);
            layout2.setClickable(true);
            layout2.setEnabled(true);
            layout2.bringToFront();
            layout2.requestFocus();

            rotation = new Flip3dAnimation(-90, 0, centerX, centerY);
        } else {
            layout2.setVisibility(View.GONE);
            layout2.setClickable(false);
            layout2.setEnabled(false);

            layout1.setVisibility(View.VISIBLE);
            layout1.setClickable(true);
            layout1.setEnabled(true);
            layout1.bringToFront();
            layout1.requestFocus();

            rotation = new Flip3dAnimation(90, 0, centerX, centerY);
        }

        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new DecelerateInterpolator());

        if (mIsFirstView) {
            layout2.startAnimation(rotation);
        } else {
            layout1.startAnimation(rotation);
        }
    }
}