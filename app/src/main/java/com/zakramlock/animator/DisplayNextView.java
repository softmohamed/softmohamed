package com.zakramlock.animator;

import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * Created by Devon 12/14/2016.
 */

public class DisplayNextView  implements Animation.AnimationListener {
    private boolean mCurrentView;
    LinearLayout layout1;
    LinearLayout layout2;

    public DisplayNextView(boolean currentView, LinearLayout layout1, LinearLayout layout2) {
        mCurrentView = currentView;
        this.layout1 = layout1;
        this.layout2 = layout2;
    }

    public void onAnimationStart(Animation animation) {
    }

    public void onAnimationEnd(Animation animation) {
        layout1.post(new SwapViews(mCurrentView, layout1, layout2));
    }

    public void onAnimationRepeat(Animation animation) {
    }
}
