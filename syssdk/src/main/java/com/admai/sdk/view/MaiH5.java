package com.admai.sdk.view;

/**
 * Created by macmi001 on 16/7/7.
 */
public class MaiH5 {
//    private static final String TAG = MaiH5.class.getSimpleName();;
//
//    private RelativeLayout image_RelativeLayout;
//
//    private Button button_close;
//    private Context mContext;
//
//
//    public MaiH5(Context context) {
//        super(context);
//
//
//        //硬件加速 + 去掉标题栏
////        setHardWare(context);
//        //初始化界面
//        initView(context);
//
//        //初始化其他类
//        init(context);
//    }
//
//    //
//    private void initView(Context context) {
//
//        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        this.setLayoutParams(p);
//
//        image_RelativeLayout = new RelativeLayout(context);
//        image_RelativeLayout.setLayoutParams(p);
//        this.addView(image_RelativeLayout);
//
//        button_close = new Button(context);
//        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        button_close.setLayoutParams(lp);
//        button_close.setText("关闭");
//        button_close.setTextColor(Color.WHITE);
//        button_close.getBackground().setAlpha(0);
//        button_close.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                maiListener.onSkip();
//            }
//        });
//        this.addView(button_close);
//    }
//
//    //
//    private void init(Context context) {
//        L.e(TAG, "init");
//        maiAdapter = new MaiAdapter(context);
////        maiAdapter.SUCCESS_SHOW = 401;
////        maiAdapter.LANDINGPAGE = 402;
//        maiRequest = MaiManager.getRequest(new MaiRequest());
//        maiRequest.m_adw = 320;
//        maiRequest.m_adh = 50;
//        maiRequest.m_stype = MaiStype.H5_advertising;
//        maiRequest.m_int = "0";
//
//        maiAdapter.init(context, maiRequest, image_RelativeLayout);
//        mContext=context;
//    }
//
//
//
//    /**
//     * 设置开启硬件加速
//     * 去掉标题栏
//     */
////    public static void setHardWare(Context context) {
////
////        if (context instanceof Activity) {
////
////
////
////            if (Build.VERSION.SDK_INT >= 11) {
//////                ((Activity) context).getWindow().setFlags(16777216, 16777216);
////                ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
////                L.e(TAG, "MaiH5: 已开启硬件加速");
////            }
////            // 去掉标题栏
////            ((Activity) context).requestWindowFeature(Window.FEATURE_NO_TITLE);
////
////        }
////
////    }
//
//    // TODO: 16/8/23   return webview
//    public WebView getWebView(){
//        if (maiAdapter == null) {
//           maiAdapter =new MaiAdapter(mContext);
//        }
//
//        return maiAdapter.getWebView();
//    }
//
//    @Override
//    public void stop() {
//        super.stop();
//        if (maiAdapter != null) {
//            WebView webView = maiAdapter.getWebView();
//            L.e(TAG, "maih5.stop: ");
//            webView.destroy();
//        }
//
//    }
}
