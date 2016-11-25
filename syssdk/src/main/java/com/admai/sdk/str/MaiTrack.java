package com.admai.sdk.str;

import com.admai.sdk.util.json.JsonParam;
import com.admai.sdk.view.NotProguard;

/**
 * Created by ZAN on 16/8/31.
 */
public class MaiTrack {
    @JsonParam(key = "tsTrack")
    @NotProguard
    public long tsTrack;
    @JsonParam(key = "urlTrack")
    @NotProguard
    public String urlTrack;
    
    @Override
    public String toString() {
        return tsTrack + "|" + urlTrack;
    }
}
