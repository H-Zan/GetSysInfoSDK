package com.admai.sdk.mai;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.admai.sdk.str.MaiAd;
import com.admai.sdk.str.MaiMonitor;
import com.admai.sdk.str.MaiMonitorResponse;
import com.admai.sdk.str.MaiPath;
import com.admai.sdk.str.MaiRequest;
import com.admai.sdk.util.json.JsonFactory;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogUtil;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by macmi001 on 16/7/4.
 * 请求控制器
 */
public class MaiController {

    private static String TAG = MaiController.class.getSimpleName();
    ;
    private static final int EXPOSURE_MONITOR = 0; //?
//    private MaiAdapter maiAdapter;  //适配器
    private MaiRequest maiRequest;  //请求容器
    private MaiAd maiAd;            //广告响应容器 bean

    //自定义 的 handler  new 的时候把 自己 (MaiController) 放进弱引用
    MyHandle mHandler = new MyHandle(this);

    private Timer timer;
    private int timeindex;

    private String serviceUrl = MaiPath.MAI_PATH_REQUEST;
    private Context mContext;
    private long requestTime;
    private long showTime;
    private long clickTime;

    public void setContext(Context context) {
        mContext = context;
    }

    //自定义的 handler
    class MyHandle extends Handler {

        //弱引用
        private WeakReference<MaiController> maiControllerWeakReference;

        //new myHandle  的时候  把  MaiController 对象 放进 弱引用
        public MyHandle(MaiController _maiControllerWeakReference) {
            maiControllerWeakReference = new WeakReference<MaiController>(_maiControllerWeakReference);
        }

        //处理消息
        public void handleMessage(Message msg) {
            long startTime = System.currentTimeMillis();
            if (msg.what == EXPOSURE_MONITOR) {
                //                sendExposureMonitor(startTime,maiAdapter.SUCCESS_SHOW);
            }
        }

    }

    //  new  MaiController 的时候会把 maiAdapter   maiRequest 都传过来
    //  MaiRequest  广告投放请求容器 (设备信息)
    public MaiController(MaiRequest _maiRequest, MaiAdapter _maiAdapter) {
//        maiAdapter = _maiAdapter;
        maiRequest = _maiRequest;
    }

    //收到传入的 serviceUrl  并赋值  在请求数据的时候调用
    public void setServiceUrl(String _serviceUrl) {
        serviceUrl = _serviceUrl;
    }

    /**
     * 曝光监测
     * <p/>
     * 自己的曝光监测 pm     和带参
     *
     * @param act
     */
    public void sendExposureMonitor(final long startTime, final int act) {
        showTime=System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            public void run() {
                //                List<String> urls = maiAd.pm.get(String.valueOf(timeindex));
                L.e(TAG, "sendExposureMonitor: maiAd.pm" + maiAd.pm.size() + maiAd.pm.toString());
                MaiMonitor maiMonitor = MaiManager.getMonitor(new MaiMonitor());
                maiMonitor.act = act;
                maiMonitor.bc = maiAd.bc;
                maiMonitor.ts = startTime;
                maiMonitor.dts = (int) ((startTime-requestTime) / 1000);
                maiMonitor.uuid = maiAd.uuid;
                MaiServer maiServer = new MaiServer(maiMonitor);
                maiServer.setContext(mContext);

                if (maiAd.pm != null && maiAd.pm.size() > 0) {
                    for (String url : maiAd.pm) {
                        //                            boolean isSuccess = maiServer.postMonitorGet(url);//get
                        boolean isSuccess = maiServer.postMonitorGet2(url);//get

                        L.e(TAG, "postMonitorGet:  for 计数 " + isSuccess);

                        if (!isSuccess) {
                            LogUtil.E("Show Monitor","Error !" );
                            L.e(TAG, "notSuccess" + url);
                        } else {
                            LogUtil.D("Show Monitor", "Success !");
                            L.e(TAG, "Success" + url);
                        }
                    }

                }else{
                    LogUtil.D("No Show Monitor To","Send");
                    L.e(TAG, "No pm to send");
                }
                //                    String mrJson = maiServer.postMonitor(MaiPath.MAI_PATH_MONITOR);   // post  失败的时候返回值里会有 失败的响应吗  记录失败的错误日志
                String mrJson = maiServer.postMonitor2();   // post  失败的时候返回值里会有 失败的响应吗  记录失败的错误日志
                L.e(TAG, "sendExposureMonitor:mrJson" + mrJson);   //mrJson 空
                try {
                    if (mrJson != null) {
//                        MaiMonitorResponse maiMonitorResponse = new Gson().fromJson(mrJson, MaiMonitorResponse.class);   //maiMonitorResponse 空

                        // TODO: 2016/11/20  gson-json    无用啊
//                        MaiMonitorResponse maiMonitorResponse = JsonFactory.fromJson(mrJson, MaiMonitorResponse.class);   //maiMonitorResponse 空
//                        L.e(TAG, "status = " + maiMonitorResponse.status);
//                        L.e(TAG, "errcode = " + maiMonitorResponse.errcode);
                    }
                } catch (Exception e) {
                    //                        e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * 点击监测    和第三方点击监测   和带参
     *
     * @param startTime
     * @param act
     */
    public void sendMonitor(final long startTime, final int act) {

        Runnable runnable = new Runnable() {
            public void run() {

                MaiMonitor maiMonitor = MaiManager.getMonitor(new MaiMonitor());
                maiMonitor.act = act;
                maiMonitor.bc = maiAd.bc;
                maiMonitor.ts = startTime;
                maiMonitor.dts = (int) ((startTime-showTime) / 1000);
                maiMonitor.uuid = maiAd.uuid;
                MaiServer maiServer = new MaiServer(maiMonitor);
                maiServer.setContext(mContext);
                L.e(TAG, "sendMonitor: maiAd.cm" + maiAd.cm.toString());

                //自己的
                if (maiAd.cm != null && maiAd.cm.size() > 0) {

                    for (String url : maiAd.cm) {
                        //                        maiServer.postMonitorGet(url);
                        boolean isSuccess = maiServer.postMonitorGet2(url);//get
                        if (isSuccess) {
                            LogUtil.D("Click Monitor", "Success !");
                        } else {
                            LogUtil.E("Click Monitor", "Error !");
                        }
                    }
                }else{
                    LogUtil.D("No Click Monitor To","Send");
                    L.e(TAG, "No cm to send");
                }

//                //第三方
//                if (maiAd.ocm != null && maiAd.ocm.size() > 0) {
//
//                    for (String url : maiAd.ocm) {
//                        //                        maiServer.postMonitorGet(url);
//                        maiServer.postMonitorGet2(url);
//                    }
//                }

                //自己的post
                //                String mrJson = maiServer.postMonitor(MaiPath.MAI_PATH_MONITOR);
                String mrJson = maiServer.postMonitor2();
                try {
//                    MaiMonitorResponse maiMonitorResponse = new Gson().fromJson(mrJson, MaiMonitorResponse.class);
                    // TODO: 2016/11/20  gson-json
                    MaiMonitorResponse maiMonitorResponse = JsonFactory.fromJson(mrJson, MaiMonitorResponse.class);
                    L.e(TAG, "status = " + maiMonitorResponse.status);
                    L.e(TAG, "errcode = " + maiMonitorResponse.errcode);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();

    }

    /**
     * 广告投放请求
//     */
//    public void sendRequest() {
//        requestTime=System.currentTimeMillis();
//        //        if (MaiUtils.isNetworkConnected(mContext)) {//// TODO: 2016/9/24   改为联网否 不一定是0    万一一开始没联网，，后来联网 监听不到的。。。改一下
//        //        if (MaiUtils.ping()) {//// TODO: 2016/9/24   改为联网否 不一定是0    万一一开始没联网，，后来联网 监听不到的。。。改一下
//        L.e(TAG, "sendRequest");
//        Runnable runnable = new Runnable() {
//            public void run() {
//                // MaiServer 网络请求 get  post  getImageURI  getImageURIifExists
//                MaiServer maiServer = new MaiServer(maiRequest);
//                maiServer.setContext(mContext);
//                String adJson = null;
//
//                adJson = maiServer.get(serviceUrl);
////                adJson = maiServer.get33(serviceUrl);
//
//                L.e(TAG + "5555", adJson);
//                //                String adJson = maiServer.get(serviceUrl).trim();  //get请求返回的数据json    url 是上get请求返回的数据json面setServiceUrl(url) 从外部传入的
//
//                if (null != adJson) { // 请求成功
//                    adJson = adJson.trim();
//                    LogUtil.D("AD Request", "Success !");
//                    try {
////                        maiAd = new Gson().fromJson(adJson, MaiAd.class);  // 解析服务器传回来的数据 变成 bean
//
//                        // TODO: 2016/11/20  gson-json  要不要catch里savelog?
//                        maiAd = JsonFactory.fromJson(adJson, MaiAd.class);  // 解析服务器传回来的数据 变成 bean
//
//                        LogSSUtil.getInstance().setBc(maiAd.bc);
//                        maiAdapter.setAd(maiAd);     // 把 广告响应容器 bean 传入  maiAdapter     maiAd:服务器返回的json bean
//                        maiAdapter.reflash();        // reflash() 中是 根据 服务器返回的 type 选择物料投放标准
//                    } catch (Exception e) {
//                        if (e == null) {
//                            e = new Exception(" catch Exception , but e == null ");
//                        }
//                        L.e(TAG, "maiAdapter.failed(\"ad error\");");
//                        L.e(TAG, "e.toString()" + e.toString());
//                        //失败的时候
//                        LogUtil.E("AD Show Error ! ", e.toString());
//                        if (maiAdapter != null) {
//                            maiAdapter.failed(" AD Show Error ! ");
//                        }
//
//                        if (LogUtil.isShowError()) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                } else {
//                    //请求失败的时候
//                    L.e(TAG, " AD Request Error ! " + " Request == null ");
//                    LogUtil.E("AD Request Error !", "Request == null");
//                    maiAdapter.failed("AD Request Failed !  Nothing Return ！");
//
//                }
//            }
//        };
//
//        new Thread(runnable).start();
//        //        } else {
//        //            L.e("no internet!", "failed");
//        //            LogUtil.E("Request AD Eror!", "No Internet!");
//        //            LogSSUtil.getInstance().saveLogs(MaiLType.REQUEST_F, "No Internet", 0, "none");
//        //            maiAdapter.failed("ad error ; " + " no internet");
//        //        }
//    }

    //开始
    public void start() {
        if (timer != null)
            timer.cancel(); //退出计时器

//        sendRequest();  // 广告投放请求

        L.e(TAG, "maiController start: ");
    }

    //停止
    public void stop() {
        if (timer != null)
            timer.cancel(); //退出计时器
    }

    public void destroy() {
//        maiAdapter = null;
        maiRequest = null;
        maiAd = null;
        mContext = null;
    }


    // ? 暂停显示 ??
    public void visibilePause() {


    }

    /**
     * 定时任务
     */
    class MyTimerTask extends TimerTask {  // TimerTask 抽象类
        @Override
        public void run() {
            Message message = new Message();
            message.what = EXPOSURE_MONITOR; // 曝光监测   XPOSURE_MONITOR
            mHandler.sendMessage(message);
        }
    }
}
