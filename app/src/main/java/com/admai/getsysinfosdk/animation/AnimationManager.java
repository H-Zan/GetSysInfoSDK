package com.admai.getsysinfosdk.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;

public class AnimationManager {
    /**
     * 在所有切换动画中随机选择动画
     */
    public static final int ANY_ANIMATION = 0;
    /**
     * 跑进动画
     */
    public static final int RUNIN_ANIMATION = 1;
    /**
     * 掉落动画
     */
    public static final int FALLDOWN_ANIMATION = 2;
    /**
     * 淡入动画
     */
    public static final int FADEIN_ANIMATION = 4;
    /**
     * 放大动画
     */
    public static final int ENLARGEMENT_ANIMATION = 8;
    private static ArrayList<Integer> mAnimations;

    public static void startAnimation(View aView) {
//        if (!AdManager.isAnimation())
//            return;
        initialAnimations();
        if (mAnimations.size() == 0)
            return;
        int i = ((int) (Math.random() * 10000)) % mAnimations.size();

        switch (mAnimations.get(i).intValue()) {
            case RUNIN_ANIMATION:
                startRuninAnimation(aView);
//                break;
            case FALLDOWN_ANIMATION:
                startFalldownAnimation(aView);
//                break;
            case ENLARGEMENT_ANIMATION:
                startEnlargementAnimation(aView);
//                break;
            case FADEIN_ANIMATION:
                startFadeinAnimation(aView);
                break;
            default:
                break;
        }
    }

    static void initialAnimations() {
        //随机动画
        if (mAnimations == null) {
            mAnimations = new ArrayList<Integer>();
            // int anis = AdManager.getAnimations();
            int anis = 0;
            // if (anis <= 0)
            anis = RUNIN_ANIMATION | FALLDOWN_ANIMATION | FADEIN_ANIMATION | ENLARGEMENT_ANIMATION;
            if ((anis & AnimationManager.RUNIN_ANIMATION) > 0) {
                mAnimations.add(AnimationManager.RUNIN_ANIMATION);
            }
            if ((anis & AnimationManager.FALLDOWN_ANIMATION) > 0) {
                mAnimations.add(AnimationManager.FALLDOWN_ANIMATION);
            }
            if ((anis & AnimationManager.FADEIN_ANIMATION) > 0) {
                mAnimations.add(AnimationManager.FADEIN_ANIMATION);
            }
            if ((anis & AnimationManager.ENLARGEMENT_ANIMATION) > 0) {
                mAnimations.add(AnimationManager.ENLARGEMENT_ANIMATION);
            }
        }
    }


    public static void startRuninAnimation(final View aView) {
        try {
            // 水平向左移动碰撞
            MoveAndBounceAnimation ani = new MoveAndBounceAnimation(
                    MoveAndBounceAnimation.Orientation.EHorizontal, new int[]{
                    aView.getWidth() != 0 ? aView.getWidth() : aView
                            .getContext().getResources()
                            .getDisplayMetrics().widthPixels, -60, 50,
                    -40, 30, 0});
            // 垂直向下移动碰撞
            ani.setDuration(1000);
            ani.setFillAfter(true);
            ani.setInterpolator(new AccelerateInterpolator());
            aView.startAnimation(ani);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static void startFalldownAnimation(final View aView) {
        try {
            // 垂直向下移动碰撞
            MoveAndBounceAnimation ani = new MoveAndBounceAnimation(
                    MoveAndBounceAnimation.Orientation.EVertical,
                    new int[]{
                            -(aView.getHeight() != 0 ? aView.getHeight()
                                    : (int) (aView.getContext().getResources()
                                    .getDisplayMetrics().widthPixels * 3.0f / 20)),
                            40, -30, 20, -10, 0}); //down up down up down  -:up>down +:down>up
            ani.setDuration(1000);
            ani.setFillAfter(true);
            ani.setInterpolator(new AccelerateInterpolator());
            aView.startAnimation(ani);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startFadeinAnimation(final View aView) {
        FadeinAnimation animation = new FadeinAnimation(aView);
        animation.startAnimation();
    }

    public static void startEnlargementAnimation(final View aView) {
        EnlargementAnimation ani = new EnlargementAnimation(aView);
        ani.startAnimation();
    }
}
