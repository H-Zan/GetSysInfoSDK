package com.admai.sdk.view;

/**
 * Created by macmi001 on 16/7/7.
 * 开屏广告
 */
public class MaiSplash {
//    private static final String TAG = MaiSplash.class.getSimpleName();
//
//    private RelativeLayout image_RelativeLayout;
//
//    //    private static volatile  MaiSplash sMaiSplash;
//    //
//    //    public static MaiSplash getInstance(Context context,String maiID) {
//    //        if (sMaiSplash == null) {
//    //
//    //            synchronized (MaiSplash.class) {
//    //                if (sMaiSplash == null) {
//    //                    sMaiSplash = new MaiSplash(context,maiID);
//    //                }
//    //            }
//    //        }
//    //        return sMaiSplash;
//    //    }
//
//    public void setStayTime(int stayTime) {
//        if (maiAdapter != null) {
//            maiAdapter.setStayTime(stayTime);
//        }
//    }
//
//    public void isCloseBySelf(boolean closeBySelf) {
//        if (maiAdapter != null) {
//            maiAdapter.setCloseByself(closeBySelf);
//        }
//    }
//
//
//    public void setTargetActivity(Class<?> targetActivity) {
//        if (maiAdapter != null) {
//            maiAdapter.setTargetActivity(targetActivity);
//        }
//    }
//
//    public void setIntent(Intent intent) {
//        if (maiAdapter != null) {
//            maiAdapter.setIntent(intent);
//        }
//    }
//
//
//    @NotProguard
//    public MaiSplash(Context context, String maiID) {
//        super(context, maiID);
//        this.setGravity(Gravity.CENTER);
//        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        LayoutParams p2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        this.setLayoutParams(p);
//
//        image_RelativeLayout = new RelativeLayout(context);
//        image_RelativeLayout.setLayoutParams(p2);
//        this.addView(image_RelativeLayout);
//
//        init(context, maiID);
//    }
//
//
//    private void init(Context context, String maiID) {
//        MaiManager.getInstance(context);
//        maiAdapter = new MaiAdapter(context);
//        maiAdapter.SUCCESS_SHOW = 601;
//        maiAdapter.LANDINGPAGE = 602;
//        maiAdapter.USER_CLOSED = 603;
//        maiRequest = MaiManager.getRequest(new MaiRequest());
////        maiRequest.m_adw = MaiManager.screenwidth;
////        maiRequest.m_adh = MaiManager.screenheight;
////        maiRequest.m_adw = 640;       //屏幕 宽度
////        maiRequest.m_adh = 1136;      //屏幕 高度
//        maiRequest.m_stype = MaiStype.Opening_advertising;
//        maiRequest.m_int = "0";
//        maiRequest.l = maiID;
//        maiAdapter.init(context, maiRequest, image_RelativeLayout);
//    }
//
////    @NotProguard
////    public void setADSizeTest(int width, int height,int all) { //如果固定高度 all 为-1， 与屏幕比例  就填总  height 填比重
////        if (all == -1) {
////            // 判断 类型
////            maiRequest.m_adw = width >= 0 ? width : width == -1 ? MaiManager.screenwidth : width == -2 ? -2 : 0;
////            maiRequest.m_adh = height >= 0 ? height : height == -1 ? MaiManager.screenheight : height == -2 ? -2 : 0;
////            //            maiRequest.m_adw = width;
////            //            maiRequest.m_adh = height;
////        }else if (all>0){
////            maiRequest.m_adw = width >= 0 ? width : width == -1 ? MaiManager.screenwidth : width == -2 ? -2 : 0;
////            maiRequest.m_adh=  MaiManager.screenheight/all*height;
////        }
////    }
//
//    @Override
//    public void stop() {
//        if (maiAdapter != null) {
//            maiAdapter.stopSplash();
//            LogUtil.D(TAG, "Stop");
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        image_RelativeLayout = null;
//    }
//
//
//    @NotProguard
//    public void setADSize(int width, int height) {
//
//        if (width > 0 || height > 0) {
//            maiRequest.m_adw = width;
//            maiRequest.m_adh = height;
//        }else {
//            LogUtil.E("Please", "set ADSize > 0 !");
//        }
//    }
}

