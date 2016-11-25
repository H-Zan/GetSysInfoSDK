package com.admai.sdk.util.log;

import com.admai.sdk.util.json.JsonParam;
import com.admai.sdk.view.NotProguard;

/**
 * Created by ZAN on 16/9/13.
 */
public class MaiLogs {
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

    */
    @JsonParam(key = "m_event_type")
    @NotProguard
    public int m_event_type;

    //事件产生时间   0000-00-00 00:00:00.000
    @JsonParam(key = "m_event_time")
    @NotProguard
    public String m_event_time;
    
    //广告位id
    @JsonParam(key = "m_ad_id")
    @NotProguard
    public String m_ad_id;

    //appName+packageName
    @JsonParam(key = "m_app_info")
    @NotProguard
    public String m_app_info;

    //sdk版本
    @JsonParam(key = "m_sdk_version")
    @NotProguard
    public String m_sdk_version;

    //广告类型 //只要广告位id便可
    //    protected int m_ad_type;

    //返回物料类型
    //    protected int m_mtr_type;

    //联网类型      //0:未知 1:Ethernet 2:wifi 3:蜂窝网络,2G 4:蜂窝网络,3G 5:蜂窝网络,4G
    @JsonParam(key = "m_net_type")
    @NotProguard
    public int m_net_type;

    //设备信息
    @JsonParam(key = "m_d_info")
    @NotProguard
    public String m_d_info;

    //错误信息
    @JsonParam(key = "m_error_info")
    @NotProguard
    public String m_error_info;

    //http响应吗
    @JsonParam(key = "m_http_code")
    @NotProguard
    public int m_http_code;

    //设备id
    @JsonParam(key = "m_d_id")
    @NotProguard
    public String m_d_id;

    //系统版本
    @JsonParam(key = "m_d_version")
    @NotProguard
    public String m_d_version;
    
    //被删除urls     7天    urls or 要发送的 string
    @JsonParam(key = "m_df_urls")
    @NotProguard
    public String m_df_urls;
    
    //广告业务的 业务ID
    @JsonParam(key = "m_bc")
    @NotProguard
    public String m_bc;
    
    //android ID
    @JsonParam(key = "m_android_id")
    @NotProguard
    public String m_android_id;
    
    //advertising ID
    @JsonParam(key = "m_aaid")
    @NotProguard
    public String m_aaid; 
    
    //phone num
    @JsonParam(key = "m_ph_num")
    @NotProguard
    public String m_ph_num;

    //sim卡imsi
    @JsonParam(key = "m_imsi")
    @NotProguard
    public String m_imsi;

    //sim卡imsi
    @JsonParam(key = "m_mac")
    @NotProguard
    public String m_mac;

    //sim卡imsi
    @JsonParam(key = "m_opr")
    @NotProguard
    public int m_opr;

    //sim卡imsi
    @JsonParam(key = "m_ip")
    @NotProguard
    public String m_ip;

    //sim卡imsi
    @JsonParam(key = "m_ua")
    @NotProguard
    public String m_ua;

    //sim卡imsi
    @JsonParam(key = "m_dvw")
    @NotProguard
    public String m_dvw;

    //sim卡imsi
    @JsonParam(key = "m_dvh")
    @NotProguard
    public String m_dvh;

    //sim卡imsi
    @JsonParam(key = "m_lan")
    @NotProguard
    public String m_lan;

    //sim卡imsi
    @JsonParam(key = "m_pos")
    @NotProguard
    public String m_pos;

    //sim卡imsi
    @JsonParam(key = "m_ssid")
    @NotProguard
    public String m_ssid;




    public MaiLogs() {
    }
}
