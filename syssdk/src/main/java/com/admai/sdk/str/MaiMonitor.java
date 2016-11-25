package com.admai.sdk.str;

import com.admai.sdk.util.json.JsonParam;
import com.admai.sdk.view.NotProguard;

/**
 * Created by macmi001 on 16/7/5.
 * 监控消息请求容器
 */
public class MaiMonitor {
    /*
     * 事件名称
     * */
    @JsonParam(key = "act")
    @NotProguard
    public int act;
    /*
   * 广告展示到互动事件产生的间隔时间,单位秒
   * */
    @JsonParam(key = "ts")
    @NotProguard
    public long ts;

    /*
     * 广告展示到互动事件产生的间隔时间,单位秒
     * */
    @JsonParam(key = "dts")
    @NotProguard
    public int dts;
    /*
     * 麦子互动平台在广告回应消息中返回的设备用户 唯一标识
     * */
    @JsonParam(key = "uuid")
    @NotProguard
    public String uuid;
    /*
     * 该次广告业务的业务 ID
     * */
    @JsonParam(key = "bc")
    @NotProguard
    public String bc;
    /*
     * 位置信息:经纬度
     * */
    @JsonParam(key = "loc")
    @NotProguard
    public String loc;

    @Override
    public String toString() {
        return act + "|" + ts  + "|" + dts + "|" + uuid + "|" + bc + "|" + loc ;
    }
}
