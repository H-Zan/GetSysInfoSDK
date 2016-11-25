package com.admai.getsysinfosdk.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class MoveAndBounceAnimation extends Animation {
    public enum Orientation {
        EHorizontal, EVertical
    }

    private Orientation mOrientation;
    private int mPoints[];
    private Camera mCamera;
    private int mIndex = 0;
    private float mPoint = 0;
    private int mPathLength = 0;
    private float mCurrentPathLength = 0;
    private int[] mExpandedPoints;

    @SuppressWarnings("null")
    public MoveAndBounceAnimation(Orientation aOrientation, int aPoints[]) throws Exception {
        mOrientation = aOrientation;
        mPoints = aPoints;
        if (aPoints == null && aPoints.length == 0) throw new Exception("aPoints---");
        mPathLength = 0;
        mCurrentPathLength = 0;
        mExpandedPoints = new int[aPoints.length];
        for (int i = 0; i < aPoints.length - 1; i++) {
            mPathLength += Math.abs(aPoints[i] - aPoints[i + 1]);
            mExpandedPoints[i + 1] = mPathLength;
        }
        mPoint = aPoints[0];
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        int op = 1;
        final Camera camera = mCamera;
        mCurrentPathLength = mPathLength * interpolatedTime;
        if (mIndex > mPoints.length - 1)
            return;
        if (mCurrentPathLength >= mExpandedPoints[mIndex]) {
            mIndex++;
            if (mIndex > mPoints.length - 1)
                return;
        }
        op = mPoints[mIndex - 1] > mPoints[mIndex] ? -1 : 1;
        final Matrix matrix = t.getMatrix();
        camera.save();
        mPoint = mPoints[mIndex - 1]
                + op
                * (mPathLength * interpolatedTime - mExpandedPoints[mIndex - 1]);
        switch (mOrientation) {
            case EHorizontal:
                camera.translate(mPoint, 0.0f, 0.0f);
                break;
            case EVertical:
                camera.translate(0.0f, mPoint * -1, 0.0f);
                break;
        }
        camera.getMatrix(matrix);
        camera.restore();
    }
}
