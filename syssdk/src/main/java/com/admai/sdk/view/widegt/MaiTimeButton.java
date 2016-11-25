package com.admai.sdk.view.widegt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Button;

import com.admai.sdk.mai.MaiCloseTypeListener;
import com.admai.sdk.type.MaiStype;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogUtil;

/**
 * Created by ZAN on 16/9/5.
 */
public class MaiTimeButton extends Button {
    private static final String TAG = MaiTimeButton.class.getSimpleName();
    
    private int num;
    private MaiCloseTypeListener mMaiCloseTypeListener;
    private Context mContext;
    private int mType;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private Runnable mRunnable;

    public MaiTimeButton(Context context, int _num, MaiCloseTypeListener maiCloseTypeListener, int type) {
        super(context);
        mContext = context;
        num = _num;
        mMaiCloseTypeListener = maiCloseTypeListener;
        mType = type;
        if (_num > 0) {
            MaiTimeButton.this.setText(num + "s");
            MaiTimeButton.this.setTextColor(Color.GRAY);
        }
        if (_num == -1) {
            MaiTimeButton.this.setText("跳过");
            MaiTimeButton.this.setTextColor(Color.BLACK);
        }
        MaiTimeButton.this.getBackground().setAlpha(0);
    }


    public MaiTimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaiTimeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void start(final boolean isSkipeBySelf, final Class<?> targetActivity, final Intent intent) {

        if (num != -1) {
            MaiTimeButton.this.setClickable(false);
            MaiTimeButton.this.setEnabled(false);
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    num--;
                    L.e(TAG, "run: " + num);
                    if (num > 0) {
                        handler.postDelayed(this, 1000);
                        MaiTimeButton.this.setText(num + "s");
                    }
                    if (num == 0 && !isSkipeBySelf) {

                        MaiTimeButton.this.setTextColor(Color.BLACK);
                        MaiTimeButton.this.setText("跳过");
                        MaiTimeButton.this.setClickable(true);
                        MaiTimeButton.this.setEnabled(true);

                        handler.removeCallbacks(this);
                    }
                    if (num == 0 && isSkipeBySelf) {

                        if (mType == MaiStype.Insert_advertising || mType == MaiStype.Fixed_advertising) {
                            if (mMaiCloseTypeListener != null) {
                                mMaiCloseTypeListener.closeBySelf();
                                L.e(TAG, Thread.currentThread().getName());
                            }
                        }

                        if (mType == MaiStype.Opening_advertising) { //自动跳转

                            if (mContext instanceof Activity) {
                                if (intent == null && targetActivity != null) {
                                    Intent maiIntent = new Intent(mContext, targetActivity);
                                    mContext.startActivity(maiIntent);
                                    ((Activity) mContext).finish();
                                }
                                if (intent != null && targetActivity == null) {
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).finish();
                                }

                                if (mMaiCloseTypeListener != null) {
                                    mMaiCloseTypeListener.closeBySelf();
                                    L.e(TAG, Thread.currentThread().getName());
                                }
                            } else {
                                LogUtil.E(" MaiSplash", "Context Not Instance Of Activity , Please Check The Place Where Init MaiSplash !");
                            }
                        }

                        handler.removeCallbacks(this);
                    }

                }
            };
            handler.postDelayed(mRunnable, 1000);
        }

    }

    public synchronized void stop() {
        MaiTimeButton.this.setTextColor(Color.BLACK);
        MaiTimeButton.this.setText("跳过");
        MaiTimeButton.this.setClickable(true);
        MaiTimeButton.this.setEnabled(true);
        handler.removeCallbacks(mRunnable);
    }

    public synchronized void destroy() {
        handler.removeCallbacks(mRunnable);
    }
}