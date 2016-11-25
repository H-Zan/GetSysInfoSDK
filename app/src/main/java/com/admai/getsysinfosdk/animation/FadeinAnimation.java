package com.admai.getsysinfosdk.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class FadeinAnimation {
    private View mView;

    public FadeinAnimation(View view) {
        this.mView = view;
    }

    public void startAnimation() {
        AlphaAnimation ani1 = new AlphaAnimation(0f, 1.0f);
        ani1.setDuration(700);
        ani1.setFillAfter(true);
        ani1.setInterpolator(new AccelerateInterpolator());
        ani1.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
//                startAnimation2();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        this.mView.startAnimation(ani1);
    }

    private void startAnimation2() {
        mView.post(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation ani2 = new AlphaAnimation(0.8f, 0.2f);
                ani2.setDuration(500);
                ani2.setFillAfter(true);
                ani2.setInterpolator(new AccelerateInterpolator());
                ani2.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        startAnimation3();
                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                    }

                    @Override
                    public void onAnimationStart(Animation arg0) {
                    }

                });
                mView.startAnimation(ani2);
            }
        });
    }

    private void startAnimation3() {
        mView.post(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation ani3 = new AlphaAnimation(0.2f, 1.0f);
                ani3.setDuration(300);
                ani3.setFillAfter(true);
                ani3.setInterpolator(new AccelerateInterpolator());
                mView.startAnimation(ani3);
            }
        });
    }
}
