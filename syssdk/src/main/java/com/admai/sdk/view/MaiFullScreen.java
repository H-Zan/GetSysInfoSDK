package com.admai.sdk.view;

/**
 * Created by macmi001 on 16/7/7.
 * 全屏广告
 * <p>
 * Because of MaiView extends RelativeLayout
 * MaiFullScreen extends MaiView -（RelativeLayout）
 * So MaiFullScreen 可以设置大小 ，可以添加 Veiw.
 */
public class MaiFullScreen {  //implements MaiCloseTypeListener
//    private static final String TAG = MaiFullScreen.class.getSimpleName();
//    //广告图片的父布局
//    private RelativeLayout image_RelativeLayout;
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
//    public MaiFullScreen(Context context, String maiID) {
//        super(context, maiID);
//
//        //大小
//        //        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        LayoutParams p2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        //给MaiFullScreen设置大小
//        this.setLayoutParams(p);
//
//        //广告图片的父布局也要 match parent
//        image_RelativeLayout = new RelativeLayout(context);
//        image_RelativeLayout.setLayoutParams(p2);
//        //把广告图片的父布局添加到MaiFullScreen中
//        this.addView(image_RelativeLayout);
//
//        //        //新建一个关闭的button
//        //        button_close = new Button(context);
//        //        //按钮的大小
//        //        LayoutParams lp = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        //        //设置位置
//        ////        lp.addRule(RelativeLayout.ALIGN_PARENT_END);
//        //        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        //        button_close.setLayoutParams(lp);
//        //        button_close.getBackground().setAlpha(0);
//        //        button_close.setText("关闭");
//        //
//        //        //Button点击事件
//        //        button_close.setOnClickListener(new OnClickListener() {
//        //            @Override
//        //            public void onClick(View v) {
//        //
//        //                //
//        //                maiListener.onSkip();
//        //            }
//        //        });
//        //
//        //        this.addView(button_close); //把Button添加到MaiFullScreen中
//
//        init(context, maiID); //初始化MaiFullScreen（自己）
//    }
//
//    //初始化  先初始化maifullscreen  再初始化maiAdapter
//    private void init(Context context, String maiID) {
//        MaiManager.getInstance(context);
//        //MaiView 中的maiAdapter
//        maiAdapter = new MaiAdapter(context);
//        maiAdapter.SUCCESS_SHOW = 201;
//        maiAdapter.LANDINGPAGE = 202;
//        maiAdapter.USER_CLOSED = 203;
//        maiAdapter.SELF_CLOSED = 204;
//        maiAdapter.setAdType("fullScreen");
//        //得到 maiRequest
//        maiRequest = MaiManager.getRequest(new MaiRequest());
//        //给 MaiRequest 配置参数
////        maiRequest.m_adw = MaiManager.screenwidth;       //屏幕 宽度
////        maiRequest.m_adh = MaiManager.screenheight;      //屏幕 高度
////        maiRequest.m_adw = 640;       //屏幕 宽度
////        maiRequest.m_adh = 1136;      //屏幕 高度
//        maiRequest.m_stype = MaiStype.Fixed_advertising; //固定广告位
//        maiRequest.m_int = "0";       // R 是否全屏/互动展示广告(Stringerstitial ad) 0:否 1:是   // TODO: 16/8/18  标志是否全屏
//        maiRequest.l = maiID;
//        // 是否全屏/互动展示广告(Stringerstitial ad) 0:否 1:是   // TODO: 16/8/18  标志是否全屏
//
//        // 初始化maiAdapter
//        // 1.上下文对象  2.maiRequest  3.adapter所要依附的父布局
//        maiAdapter.init(context, maiRequest, image_RelativeLayout);
//        maiAdapter.setMaiCloseTypeListener(this);
//    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        image_RelativeLayout = null;
//    }
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
