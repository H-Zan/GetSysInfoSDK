package com.admai.getsysinfosdk.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;

public class EnlargementAnimation {
    private View mView;

    public EnlargementAnimation(View view) {
        this.mView = view;
    }

    public void startAnimation() {
        ScaleAnimation ani1 = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f,
                                                 Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                                 0.5f);
        ani1.setDuration(700);
        ani1.setFillAfter(true);
        ani1.setInterpolator(new AccelerateInterpolator());
        ani1.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                startAnimation2();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }

        });
        mView.startAnimation(ani1);
    }

    private void startAnimation2() {
        mView.post(new Runnable() {
            @Override
            public void run() {
                ScaleAnimation ani2 = new ScaleAnimation(1.4f, 0.8f, 1.4f,
                                                         0.8f, Animation.RELATIVE_TO_SELF, 0.5f,
                                                         Animation.RELATIVE_TO_SELF, 0.5f);
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
                ScaleAnimation ani3 = new ScaleAnimation(0.8f, 1.0f, 0.8f,
                                                         1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                                                         Animation.RELATIVE_TO_SELF, 0.5f);
                ani3.setDuration(300);
                ani3.setFillAfter(true);
                ani3.setInterpolator(new AccelerateInterpolator());
                mView.startAnimation(ani3);
            }
        });
    }
}
