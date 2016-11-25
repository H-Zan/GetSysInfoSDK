package com.admai.sdk.str;

import com.admai.sdk.util.json.JsonParam;
import com.admai.sdk.view.NotProguard;

/**
 * Created by macmi001 on 16/7/5.
 * 监控消息应答容器
 */
public class MaiMonitorResponse {
    /*
     * 监控请求是否成功
     * */
    @JsonParam(key = "status")
    @NotProguard
    public String status;
    /*
     * 错误信息代码
     * */
    @JsonParam(key = "errcode")
    @NotProguard
    public int errcode;
}
