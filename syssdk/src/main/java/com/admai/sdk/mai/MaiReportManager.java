package com.admai.sdk.mai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.admai.sdk.str.MaiMonitor;
import com.admai.sdk.str.MaiPath;
import com.admai.sdk.str.MaiTrack;
import com.admai.sdk.type.MaiLType;
import com.admai.sdk.util.HttpHelper;
import com.admai.sdk.util.MaiUtils;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogSSUtil;
import com.admai.sdk.util.log.LogUtil;
import com.admai.sdk.util.persistance.SharePreferencePersistance;
import com.admai.sdk.util.pool.ATaskRunnable;
import com.admai.sdk.util.pool.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZAN on 16/8/31.
 */

public class MaiReportManager {
    private static final String TAG = MaiReportManager.class.getSimpleName();;
    private NetworkStateReceiver mNetworkStateReceiver;

    private static volatile MaiReportManager mMaiReportManager;
    private final SharePreferencePersistance mPersistance;
    private List<MaiMonitor> mHangupMonitors;
    private List<MaiTrack> mHangupTracks;
    private static int MESSAGE_OUT_DAYS = 7;

    private Object[] mHangupParams;

    private Context mContext;

    private String monitorFileName = MaiPath.MAI_MONITORS_FILENAME;
    private String trackFileName = MaiPath.MAI_TRACKS_FILENAME;


    public static MaiReportManager getInstance(Context context) {
        if (mMaiReportManager == null) {

            synchronized (MaiReportManager.class) {
                if (mMaiReportManager == null) {

                    mMaiReportManager = new MaiReportManager(context);
                }
            }
        }

        return mMaiReportManager;
    }

    public MaiReportManager(Context context) {
        mHangupMonitors = new ArrayList<>();
        mHangupTracks = new ArrayList<>();
        mPersistance = new SharePreferencePersistance();
        mContext = context;
    }


    //取消注册  接收器
    public synchronized void unregisterNetworkStateReceiver() {
        try {
            if (mNetworkStateReceiver != null) {
                mContext.unregisterReceiver(mNetworkStateReceiver);
                mNetworkStateReceiver = null;
                L.e(TAG, "unregisterNetworkStateReceiver: 1 ");
            }
            L.e(TAG, "unregisterNetworkStateReceiver: 2 ");

        } catch (Exception e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }            L.e(TAG, "unregisterNetworkStateReceiver: e " + e.toString());

        }
    }

    //注册
    protected synchronized void registerNetworkStateReceiver() {
        if (mNetworkStateReceiver == null) {
            mNetworkStateReceiver = new NetworkStateReceiver();
            L.e(TAG, "registerNetworkStateReceiver: 1 ");
        }
        try {
            IntentFilter filter = new IntentFilter();
                        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

            mContext.registerReceiver(mNetworkStateReceiver, filter);  //注册
            L.e(TAG, "registerNetworkStateReceiver: 2 ");

        } catch (Exception e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }            L.e(TAG, "registerNetworkStateReceiver: e ");

        }

    }

    private class NetworkStateReceiver extends BroadcastReceiver {   ////  网络联网的接收器

        @Override
        public void onReceive(Context context, Intent intent) {

                        Bundle bundle = intent.getExtras();
                        if (bundle != null) {
                            NetworkInfo networkInfo = (NetworkInfo) bundle.get("networkInfo");
                            if (networkInfo != null) {
                                if (networkInfo.isConnected() && mHangupParams != null) {  //联网   mHangupParams!=null 代表有过失败   可是只有neterror的时候才 改变param?为什么
//                                if (networkInfo.isConnected() ) {  //联网   mHangupParams!=null 代表有过失败   可是只有neterror的时候才 改变param?为什么

                                    doWhenNetResume(mHangupParams);              //联网的时候发送
                                    mHangupParams = null;                        //代表失败的发送完毕
                                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                                        L.e(TAG, "onReceive: "+"wifi状态");
                                    }

                                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                                        L.e(TAG, "onReceive: "+"移动网状态");
                                    }
                                }
                            }
                        }



//            String action = intent.getAction();
//            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//
//                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
//                if (netInfo != null && netInfo.isAvailable()) {
//                    L.e(TAG, "onReceive: " + "网络连接");
//
//                    /////////////网络连接
//                    String name = netInfo.getTypeName();
//
//
//                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                        /////WiFi网络
//                        L.e(TAG, "onReceive: " + "WiFi网络");
//
//                    } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
//                        /////有线网络
//                        L.e(TAG, "onReceive: " + "有线网络");
//
//                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//                        /////////3g网络
//                        L.e(TAG, "onReceive: " + "3g网络");
//
//                    }
//
//                    doWhenNetResume(mHangupParams);              //联网的时候发送
//                    mHangupParams = null;
//
//
//                } else {
//                    ////////网络断开
//                    L.e(TAG, "onReceive: " + "网络断开");
//
//                }
//
//            }
        }
    }

    protected void doWhenNetResume(Object... params) {     //联网的时候发送
//        resumeMonitors();
//        resumeTracks();
        resumeMonitorsRun();
        resumeTracksRun();


    }

    private synchronized void resumeMonitors() {

                    synchronized (mHangupMonitors) {
                        List<MaiMonitor> sendMaiMonitors = new ArrayList<>(mHangupMonitors);

                        mHangupMonitors.clear();
                        saveHangupMonitors();    //存空的  为了清空 shared

                        for (int i = 0; i < sendMaiMonitors.size(); i++) {
                            if (!MaiUtils.isTimeOut(mHangupMonitors.get(i).ts, MESSAGE_OUT_DAYS)) {
                                monit(sendMaiMonitors.get(i));  //失败的时候有 回调  会hung up  但万一我突然 退出应用没法送完 sendMaiMonitors 中的数据不都没了?
                                L.e(TAG, "mHangupMonitors: " + mHangupMonitors.size() + mHangupMonitors.toString()); //看每次循环monitor是否变少
                            } else{
                                LogSSUtil.getInstance().saveLogs(MaiLType.TRACK_DELETE," out 7 days ",0,mHangupMonitors.get(i).toString());
                            }
                        }

                        //                        //放在for 后面  都发送完才clear  成功的时候和失败都去除monitor
                        //                        mHangupMonitors.clear();
                        //                        saveHangupMonitors();

                        //                        //2 不管>0<0都save一下??
                        //                        if (mHangupMonitors.size()>0) {
                        //                            saveHangupMonitors();
                        //                        }


                    }

    }
    private synchronized void resumeMonitorsRun() {

        ThreadPoolManager.getInstance().publishTask("resumeMonitors",
            new ATaskRunnable() {
                @Override
                public Object onStart(Object... params) {
                    synchronized (mHangupMonitors) {
                        List<MaiMonitor> sendMaiMonitors = new ArrayList<>(mHangupMonitors);

                        mHangupMonitors.clear();
                        saveHangupMonitors();    //存空的  为了清空 shared

                        for (int i = 0; i < sendMaiMonitors.size(); i++) {
                            if (!MaiUtils.isTimeOut(mHangupMonitors.get(i).ts, MESSAGE_OUT_DAYS)) {
                                monit(sendMaiMonitors.get(i));  //失败的时候有 回调  会hung up  但万一我突然 退出应用没法送完 sendMaiMonitors 中的数据不都没了?
                                L.e(TAG, "mHangupMonitors: " + mHangupMonitors.size() + mHangupMonitors.toString()); //看每次循环monitor是否变少
                            }
                        }

                        //                        //放在for 后面  都发送完才clear  成功的时候和失败都去除monitor
                        //                        mHangupMonitors.clear();
                        //                        saveHangupMonitors();

                        //                        //2 不管>0<0都save一下??
                        //                        if (mHangupMonitors.size()>0) {
                        //                            saveHangupMonitors();
                        //                        }


                    }
                    return null;
                }
            });
    }
    private synchronized void saveHangupMonitors() {
        mPersistance.save(mContext, monitorFileName, mHangupMonitors);
    }

    /**
     * 发送monitor
     * 是在runnable中   //  在有monitor的地方 再次封装 才会有  monitor 传入  在需要发送的地方再发送啊....
     *
     * @param
     */
    public String monit(MaiMonitor monitor) { //    monitor 中有发送检测的 param
        //在runnable中么?  不在的话要在runnable中
        String s = HttpHelper.postMonitor("--", MaiPath.MAI_PATH_MONITOR, monitor, new OnMonitListener(monitor));//post 就是 带餐请求monitor和 用listener 回调minotor请求状态
        return s;  //s为上传的 string
    }


    class OnMonitListener implements MaiReportListener<Void> {  //失败的时候hunguptrack

        MaiMonitor monitor;

        public OnMonitListener(MaiMonitor monitor) {
            this.monitor = monitor;
        }


        @Override
        public void onSucceed(Object context, String info, Object attachment) {
            L.e(TAG, "onSucceed: " + "OnMonitListener" + info);

            mHangupMonitors.remove(monitor);    //去除成功的monitor


        }

        @Override
        public void onResponseError(Object context, String e, int errorCode) {
            L.e(TAG, "onResponseError: " + "OnMonitListener" + e);

            onError();

        }

        @Override
        public void onNetError(String exception,int errorCode) {
            L.e(TAG, "onNetError: " + "OnMonitListener" + exception.toString());

            onError();
            netOffLine(mContext);   //联网失败
        }

        private void onError() {  // 错误的时候挂起
            hangupMonitor(monitor);
            mHangupMonitors.remove(monitor);  //去除失败的monitor
            LogSSUtil.getInstance().saveLogs(MaiLType.TRACK_F, "Track Failed " + monitor.toString() , 0, "none");
        }
        //   mHangupMonitors 中去除成功的monitor 去除失败的montor 还剩 没成功也没失败的monitor
    }

    protected void netOffLine(Object... params) {
        L.e(TAG, "netOffLine");
        mHangupParams = params;
    }

    private void hangupMonitor(MaiMonitor monitor) {
        synchronized (mHangupTracks) {

            // contains是循环遍历，通过equals方式来判断两个对象是否一致
            if (!mHangupMonitors.contains(monitor)) {
                mHangupMonitors.add(monitor);
            }
            saveHangupMonitors();
        }
    }


    private synchronized void resumeTracks() {

        synchronized (mHangupTracks) {

            List<MaiTrack> trackList = new ArrayList<>(mHangupTracks);   //把  mHangupTracks 加入trackList
            L.e(TAG, "resumeTracks: "+ trackList.size());
            mHangupTracks.clear();        //清空
            saveHangupTracks();           //保存 空

            if (trackList.size() > 0) {     //若有track
                for (int i = 0; i < trackList.size(); i++) {   //循环track
                    if (!MaiUtils.isTimeOut(trackList.get(i).tsTrack, MESSAGE_OUT_DAYS)) {  //7天之内
                        track(trackList.get(i));         //track get url 无参
                        L.e(TAG, "resumeTracks: 7天之内");
                    }else{
                        LogSSUtil.getInstance().saveLogs(MaiLType.TRACK_DELETE," out 7 days ",0,trackList.get(i).urlTrack);
                    }
                }
            }
        }
    }


 private synchronized void resumeTracksRun() {

     ThreadPoolManager.getInstance().publishTask("resumeMonitors",
         new ATaskRunnable() {
             @Override
             public Object onStart(Object... params) {
        synchronized (mHangupTracks) {

            List<MaiTrack> trackList = new ArrayList<>(mHangupTracks);   //把  mHangupTracks 加入trackList
            L.e(TAG, "resumeTracks: "+ trackList.size());
            mHangupTracks.clear();        //清空
            saveHangupTracks();           //保存 空

            if (trackList.size() > 0) {     //若有track
                for (int i = 0; i < trackList.size(); i++) {   //循环track
                    if (!MaiUtils.isTimeOut(trackList.get(i).tsTrack, MESSAGE_OUT_DAYS)) {  //7天之内
                        track(trackList.get(i));         //track get url 无参
                        L.e(TAG, "resumeTracks: 7天之内");
                    }else{
                        LogSSUtil.getInstance().saveLogs(MaiLType.TRACK_DELETE," out 7 days ",0,trackList.get(i).urlTrack);
                    }
                }
            }
        }
                 return null;
             }
         });
    }


    public boolean track(MaiTrack track) { //    track 中有发送检测时间和url
        L.e(TAG, "track: tracktracktracktracktracktrack" );
        boolean isSuccess = HttpHelper.postMonitorGet("--", track.urlTrack, new OnTrackListener(track));

        return isSuccess;
    }


    class OnTrackListener implements MaiReportListener<Void> {  //失败的时候hunguptrack

        MaiTrack track;

        public OnTrackListener(MaiTrack track) {
            this.track = track;
        }

        @Override
        public void onSucceed(Object context, String info, Object attachment) {
            L.e(TAG, "onSucceed: " + "OnTrackListener" + info);
        }

        @Override
        public void onResponseError(Object context, String e, int errorCode) {
            L.e(TAG, "onResponseError: " + "OnTrackListener" + e);
            onError();
        }

        @Override
        public void onNetError(String exception,int errorCode) {
            L.e(TAG, "onNetError: " + "OnTrackListener" + exception.toString());
            onError();
            netOffLine(mContext);

        }


        private void onError() {  //  错误的时候挂起 track
            L.e(TAG, "onError: " + "OnTrackListener");
            hangupTrack(track);
            LogSSUtil.getInstance().saveLogs(MaiLType.TRACK_F, "Track Failed " + track.toString() , 0, "none");
    
        }

    }

    private void hangupTrack(MaiTrack track) {
        synchronized (mHangupTracks) {

            // contains是循环遍历，通过equals方式来判断两个对象是否一致
            if (!mHangupTracks.contains(track)) {
                mHangupTracks.add(track);
                L.e(TAG, "hangupTrack: " + track.urlTrack);

            }
            saveHangupTracks();
        }
    }

    private synchronized void saveHangupTracks() {

        mPersistance.save(mContext, trackFileName, mHangupTracks);
        L.e(TAG, "saveHangupTracks: " + trackFileName + mHangupTracks.toString());

    }


    //从文件中查询的 发送
    synchronized void restoreTracks() {
        mHangupTracks = mPersistance.restore(mContext, trackFileName, MaiTrack.class);
        L.e(TAG, "restoreTracks: "+mHangupTracks.size());
        if (mHangupTracks.size() > 0) {      //shared中有track时,,,
//            resumeTracks();
            resumeTracksRun();
        }                                  //准备开始track
    }

    synchronized void restoreMonitors() {      //从shareed文件中获取 mHangupMonitors mHangupMonitors中有的话 发送
        mHangupMonitors = mPersistance.restore(mContext, monitorFileName, MaiMonitor.class);
        if (mHangupTracks.size() > 0) {
//            resumeMonitors();
            resumeMonitorsRun();
        }
    }
}
