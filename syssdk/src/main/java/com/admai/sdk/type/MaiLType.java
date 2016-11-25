package com.admai.sdk.type;

/**
 * Created by ZAN on 16/9/13.
 */
public class MaiLType {
    private static final String TAG = MaiLType.class.getSimpleName();;
    
    public MaiLType() {
    }

    /*
     事件类型
     0----crash
     1----请求失败
     2----请求成功物料下载失败
     3----物料下载成功展示失败
     4----监测失败url被删除
     5----log发送失败 
     6----app启动 
     7----监测发送失败
     8----For Mai Weather
     9----抓取设备信息

     7----Permission ： be refused（6.0 以上）
 */
    public static final int CRASH = 0;
    public static final int REQUEST_F = 1;
    public static final int MTR_DOWN_F = 2;
    public static final int MTR_SHOW_F = 3;
    public static final int TRACK_DELETE = 4;
    public static final int LOG_SEND_F = 5;
    public static final int APP_START = 6;
    public static final int TRACK_F = 7;
    public static final int MAI_WEATHER = 8;
    public static final int SYS_INFO = 9;
    public static final int GET_PERMIS_F = 7;
}
