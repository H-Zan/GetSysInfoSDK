package com.admai.sdk.view;

/**
 * Created by macmi001 on 16/7/7.
 * 插屏广告
 */
public class MaiInsert  {
//    private static final String TAG = MaiInsert.class.getSimpleName();
//
//    private RelativeLayout image_RelativeLayout;
//
//
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
//    @NotProguard
//    public MaiInsert(Context context,String maiID) {
//        super(context,maiID);
//        this.setGravity(Gravity.CENTER);
//
//        LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        LayoutParams p2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        p2.addRule(RelativeLayout.CENTER_IN_PARENT);
//        this.setLayoutParams(p);
//
//        image_RelativeLayout = new RelativeLayout(context);
//        image_RelativeLayout.setLayoutParams(p2);
//        this.addView(image_RelativeLayout);
//
////        //  插屏状态下的按钮
////        button_close = new ImageButton(context);
////        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
////        lp.addRule(RelativeLayout.ALIGN_PARENT_END);
////        button_close.setLayoutParams(lp);
////        button_close.setImageBitmap(MaiUtils.loadAssetsBitmap(context, "com.adchina.mraid.assets/mraid_close.png"));
////        button_close.getBackground().setAlpha(0);
////        button_close.setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                maiListener.onClose();
////            }
////        });
////        image_RelativeLayout.addView(button_close);
//
//        init(context,maiID);
//    }
//
//    private void init(Context context,String maiID) {
//        MaiManager.getInstance(context);
//        maiAdapter = new MaiAdapter(context);
//        maiAdapter.SUCCESS_SHOW = 301;
//        maiAdapter.LANDINGPAGE = 302;
//        maiAdapter.USER_CLOSED = 308;
//        maiAdapter.setAdType("insert");
//        maiRequest = MaiManager.getRequest(new MaiRequest());
//        maiRequest.m_stype = MaiStype.Insert_advertising;
//        maiRequest.m_int = "0";
//        maiRequest.l=maiID;
//        maiAdapter.init(context, maiRequest, image_RelativeLayout);
////        button_close.bringToFront();
//    }
//
//    @NotProguard
//    public void setADSize(int width, int height) {
////        maiRequest.m_adw = width >= 0 ? width : width == -1 ? MaiManager.screenwidth : width == -2 ? -2 : 0;
////        maiRequest.m_adh = height >= 0 ? height : height == -1 ? MaiManager.screenheight : height == -2 ? -2 : 0;
//
//        if (width > 0 || height > 0) {
//            maiRequest.m_adw = width;
//            maiRequest.m_adh = height;
//        }else {
//            LogUtil.E("Please", "set ADSize > 0 !");
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        image_RelativeLayout=null;
//    }
}
