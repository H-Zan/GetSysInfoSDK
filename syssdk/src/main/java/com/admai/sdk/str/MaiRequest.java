package com.admai.sdk.str;


import com.admai.sdk.util.json.JsonParam;
import com.admai.sdk.view.NotProguard;

import java.util.ArrayList;

/**
 * Created by macmi001 on 16/7/4.
 * 广告投放请求容器
 */
public class MaiRequest {

    /**
     * ax:Client-to-Server           Y
     * bx:Server-to-Server
     */
    @JsonParam(key = "b")
    @NotProguard
    public String b = "ax";
    /**
     *  sdk=1                        Y
     *  wap=2
     */
    @JsonParam(key = "t")
    @NotProguard
    public int t = 1;
    /**
     * 广告位id                       Y
     */
    @JsonParam(key = "l")
    @NotProguard
//    public String l = "990100100";
    public  String l ;
    /**
     * 返回值类型                      Y
     * r=0 表示json
     */
    @JsonParam(key = "r")
    @NotProguard
    public int r = 0;
    /**
     * http协议版本  整数格式           Y
     */
    @JsonParam(key = "v")
    @NotProguard
    public int v = 1;


    /*
     * 设备类型                        Y
     *
     * 0:未知 2：pc 3：tv 4：phone 5：tablet
     * */
    @JsonParam(key = "bt")
    @NotProguard
    public int bt = 4;
    /*
    * 用户终端的操作系统类型              Y
    *  0:未知  1:Android 2:iOS 3:WP 4:Others  5:Windows PC  6:Linux PC  7:Mac OS不区分大小写
    * */
    @JsonParam(key = "m_os")
    @NotProguard
    public int m_os = 1;
    /*
     * 操作系统版本号                   Y
     * 无
     * */
    @JsonParam(key = "m_os_v")
    @NotProguard
    public String m_os_v;
    /*
     * OpenUDID
     * OpenUDID iOS<6(版本 6 以下的 操作系统需要提供)
     * */
    @JsonParam(key = "m0")
    @NotProguard
    public String m0;
    /*
     * Android ID                     Y
     * Android ID
     * */
    @JsonParam(key = "m1")
    @NotProguard
    public String m1;
    /*
     * IMEI(MD5)                      Y
     * IMEI 加密
     * */
    @JsonParam(key = "m2")
    @NotProguard
    public String m2;
    /*
     * IMEI                           Y
     * 非加密
     * */
    @JsonParam(key = "m3")
    @NotProguard
    public String m3;
    /*
     * MD5 加密的 Android ID           Y
     * */
    @JsonParam(key = "m4")
    @NotProguard
    public String m4;
    /*
     * iOS IDFA
     * iOS>=6
     * */
    @JsonParam(key = "m5")
    @NotProguard
    public String m5;
    /*
     * 保留分隔符”:”(保持大写)的 MAC 地址取 MD5 摘 要    Y
     * MAC
     * */
    @JsonParam(key = "m6")
    @NotProguard
    public String m6;
    /*
     * 去除分隔符”:”(保持大写)的 MAC 地址取 MD5 摘 要     Y
     * MAC
     * */
    @JsonParam(key = "m7")
    @NotProguard
    public String m7;
    /*
     * AAID(Advertising Id)                           Y
     * Advertising Id for Android
     * */
    @JsonParam(key = "m8")
    @NotProguard
    public String m8="None";
    /*
     * Windows Phone 用户终端的 DUID,md5 加密
     * DUID for WP
     * */
    @JsonParam(key = "m_duid")
    @NotProguard
    public String m_duid;
    /*
     * App Name                                         Y
     * */
    @JsonParam(key = "m_app")
    @NotProguard
    public String m_app;
    /*
     * APP 应用的包名称或 bundleID                         Y
     * */
    @JsonParam(key = "m_app_pn")
    @NotProguard
    public String m_app_pn;
    /*
     * iOS App iTunes ID
     * */
    @JsonParam(key = "m_itid")
    @NotProguard
    public String m_itid;
    /*
     * app 应用的分类编号                                            Y
     * */
    @JsonParam(key = "m_cat")
    @NotProguard
    public String m_cat="001";    //?????  1`
    /*
     * 应用商店的编号    R
     * 1:iOS AppStore 2:Google Play 3:91 Market
     * */
    @JsonParam(key = "m_mkt")
    @NotProguard
    public String m_mkt;      //??? 2
    /*
     * app 在上述应用商店内的编号  R
     * */
    @JsonParam(key = "m_mkt_app")
    @NotProguard
    public String m_mkt_app;    //4???
    /*
     * app 在上述应用商店内的分类编号
     * */
    @JsonParam(key = "m_mkt_cat")
    @NotProguard
    public String m_mkt_cat;    //5
    /*
     * app 在上述应用商店内的标签(英文或中文 UTF8-urlencode 编码) 多个标签使用半角逗号分隔  R
     * */
    @JsonParam(key = "m_mkt_tag")
    @NotProguard
    public String m_mkt_tag;   //6
    /*
     * 广告位的宽度,以像素为单位。(指密度无关像素, 即 DIP 或 CSS pixel)        Y
     * */
    @JsonParam(key = "m_adw")
    @NotProguard
    public int m_adw;
    /*
     * 广告位的高度,以像素为单位。( 指密度无关像素, 即 DIP 或 CSS pixel)       Y
     * */
    @JsonParam(key = "m_adh")
    @NotProguard
    public int m_adh;
    /*
     * 请求广告类型                                                       Y
     * 0: 固定广告位 1:Banner 2:插屏 3:开屏 4:信息流 5:H5
     *
     * 0: 横幅广告  1：插屏或全屏广告 2：开屏 3：原生广告 4：H5 5：video（嵌入式视频广告）
     *
     * */
    @JsonParam(key = "m_stype")
    @NotProguard
    public  int m_stype;

    /**
     * m_type 请求物料类型, 可多选    R
     *
     * 1：图片 2：flash 4：html 5：视频物料
     *
     */
    @JsonParam(key = "m_type")
    @NotProguard
    public ArrayList<Integer> m_type;    //也有 
    
	/**
     * 支持的点击行为
     * 
     * 1:打开网页 2:应用下载
     */
    @JsonParam(key = "m_adct")
    @NotProguard
    public ArrayList<Integer> m_adct;    
    
    /*
     * 是否全屏/互动展示广告(Stringerstitial ad)      R
     * 0:否 1:是
     * */
    @JsonParam(key = "m_int")
    @NotProguard
    public String m_int;
    /*
     * 网络运营商
     * 0:未知 1:联通 2:移动 3:电信 4:增值运营商
     * */
    @JsonParam(key = "m_opr")
    @NotProguard
    public int m_opr;
    /*
     * 联网类型
     * 0:未知 1:Ethernet 2:wifi 3:蜂窝网络,2G 4:蜂窝网络,3G 5:蜂窝网络,4G
     * */
    @JsonParam(key = "m_net")
    @NotProguard
    public int m_net;
    /*
     * IP
     * */
    @JsonParam(key = "m_ip")
    @NotProguard
    public String m_ip;
    /*
     * User-Agent(字符串,需要 escape 转义)
     * */
    @JsonParam(key = "m_ua")
    @NotProguard
    public String m_ua;
    /*
     * 发送请求时的本地 UNIX 时间戳(秒数, 10 进制)                             Y
     * SDK 需要
     * */
    @JsonParam(key = "m_ts")
    @NotProguard
    public long m_ts;
    /*
     * 设备屏幕的宽度,以像素为单位。(指密度无关像素,即 DIP 或 CSS pixel)
     * */
    @JsonParam(key = "m_dvw")
    @NotProguard
    public int m_dvw;
    /*
     * 设备屏幕的宽度,以像素为单位。(指密度无关像素,即 DIP 或 CSS pixel)
     * */
    @JsonParam(key = "m_dvh")
    @NotProguard
    public int m_dvh;
    /*
     * 设备生产商(字符串格式)
     * */
    @JsonParam(key = "m_mfr")
    @NotProguard
    public String m_mfr;
    /*
     * 设备型号
     * */
    @JsonParam(key = "m_mdl")
    @NotProguard
    public String m_mdl;
    /*
     * 投放 sdk 版本
     * */
    @JsonParam(key = "m_sdk")
    @NotProguard
    public String m_sdk = MaiPath.MAI_VERSION;
    /*
     * 目前使用的语言和国家
     * */
    @JsonParam(key = "m_lan")
    @NotProguard
    public String m_lan;
    /*
     * iOS 设备是否越狱
     * 0:否/未知(默认) 1:开启
     * */
    @JsonParam(key = "m_brk")
    @NotProguard
    public String m_brk;
    /*
     * 地理位置(经度, 维度, 精确度为单位)
     * 标准地理坐标
     * */
    @JsonParam(key = "m_pos")
    @NotProguard
    public String m_pos;   //无
    /*
     * Wifi SSID
     * 支持中文
     * */
    @JsonParam(key = "m_ssid")
    @NotProguard
    public String m_ssid;

    public MaiRequest() {

    }


    public String getAdId() {
        return l;
    }

    public String getMsdk() {
        return m_sdk;
    }
}
