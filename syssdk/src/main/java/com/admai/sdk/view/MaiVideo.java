package com.admai.sdk.view;

/**
 * Created by ZAN on 16/8/25.
 */

public class MaiVideo {
//    private static final String TAG = MaiVideo.class.getSimpleName();
//    private Context mContext;
//    private MaiVideoView mMaiVideoView;
//
//
//
//    public MaiVideo(Context context,boolean isFullScreen) {
//        super(context);
//
//        initView();
//        init(context,isFullScreen);
//    }
//
//
//
//    private void initView() {
//        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        setLayoutParams(p);
//    }
//
//    /**
//     * 开始播放 501
//     * 点击到LandingPage 502
//     * 播放暂停 503
//     * 播放结束 504
//     * 用户关闭 505
//     * @param context
//     */
//    private void init(Context context,boolean isFullScreen) {
//
//        Log.d(TAG, "init");
//        maiAdapter = new MaiAdapter(context);
//        maiAdapter.SUCCESS_SHOW = 501;
//        maiAdapter.LANDINGPAGE = 502;
//
//        maiAdapter.fullScreen(isFullScreen);
//
//        maiRequest = MaiManager.getRequest(new MaiRequest());
//
//        maiRequest.m_stype = MaiStype.Video_advertising;
//        maiRequest.m_int = "0";
//        maiAdapter.init(context, maiRequest, this);
//
//    }
//
//    @NotProguard
//    public void setADSize(int width, int height) {
//
//        // 判断 类型
//        maiRequest.m_adw = width >= 0 ? width : width == -1 ? MaiManager.screenwidth : width == -2 ? -2 : 0;
//        maiRequest.m_adh = height >= 0 ? height : height == -1 ? MaiManager.screenheight : height == -2 ? -2 : 0;
//
//    }
//
//
//    @Override
//    public void stop() {
//        super.stop();
//    }
//
//   public void  setFullScreen(boolean isFullScreen){
//       if (maiAdapter != null) {
//
//           maiAdapter.fullScreen(isFullScreen);
//       }
//   }
}
