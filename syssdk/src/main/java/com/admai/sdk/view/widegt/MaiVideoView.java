package com.admai.sdk.view.widegt;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.VideoView;

import com.admai.sdk.util.log.L;

import java.io.Serializable;

/**
 * Created by ZAN on 16/8/25.
 */
public class MaiVideoView extends VideoView implements Serializable {
    private static final String TAG = MaiVideoView.class.getSimpleName();
    private Context mContext;

    private int mAdw;
    private int mAdh;
    private boolean isFullScreen = false;
    private int mPosition;

    public MaiVideoView(Context context) {
        super(context);
        mContext = context;
        createMediaController();
    }

    public MaiVideoView(Context context, int w, int h) {
        super(context);
        mContext = context;

            mAdw = w;
            mAdh = h;
        createMediaController();

    }

    public MaiVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        createMediaController();

    }

    public MaiVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        createMediaController();

    }

    public void setSize(int w, int h) {
        mAdw = w;
        mAdh = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //        setMeasuredDimension(w1, h1);
        L.e(TAG, "onMeasure: " + mAdh + "wwwwww" + mAdw);
        //
        //        if (mAdw >= mAdh) {
        //            setMeasuredDimension(mAdw, mAdh);
        //        }else{
        //            setMeasuredDimension(mAdh, mAdw);
        //        }
//        if (isFullScreen) {
//            mAdw = MaiManager.screenwidth;
//            mAdh = MaiManager.screenheight;
//            setMeasuredDimension(mAdh, mAdw);
//        } else {
//        }
        if (mAdw > 0&&mAdh>0) {
            setMeasuredDimension(mAdw, mAdh);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //触摸事件
        return super.onTouchEvent(ev);
    }



    public void fullScreen(boolean isFullScreen) {

        this.isFullScreen=  isFullScreen;
        if (isFullScreen) {
//            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);



//            final ViewGroup parent = (ViewGroup)this.getParent();
//
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//            //            parent.removeView(this);
//            builder.setView(this);
//            this.setZOrderOnTop(true);
//            AlertDialog alertDialog = builder.create();
//
////            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            alertDialog.show();
//            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//
//                }
//            });
//            this.start();
        }


    }

    public  void getPosition(int position){
         mPosition=position;
        this.seekTo(position);
    }


    private void createMediaController(){

        MediaController mediaController = new MediaController(mContext);
        this.setMediaController(mediaController);

    }



}
