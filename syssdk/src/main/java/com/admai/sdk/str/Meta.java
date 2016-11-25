package com.admai.sdk.str;

import com.admai.sdk.util.json.JsonParam;
import com.admai.sdk.view.NotProguard;

/**
 * Created by macmi001 on 16/7/6.
 * 广告时长
 */
public class Meta {
    @JsonParam(key = "check")
    @NotProguard
    public int check;
    @JsonParam(key = "duration")
    @NotProguard
    public int duration;

    @Override
    public String toString() {
        return check + " - " + duration;
    }
}
