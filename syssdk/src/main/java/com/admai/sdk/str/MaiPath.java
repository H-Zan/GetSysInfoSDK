package com.admai.sdk.str;

import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogUtil;

/**
 * Created by ZAN on 16/8/29.
 */
public class MaiPath {


    public static String BASIC_PATH = "";
    public static final String BASIC_PATH_TEST1 = "http://test-adx.admai.com/";
//    public static final String BASIC_PATH_TEST2 = "http://192.168.80.121:9008/";
    public static final String BASIC_PATH_TEST2 = "http://192.168.80.122:9008/";

    public static final String BASIC_PATH_FORMAL = "http://m.x.xchmai.com/";
//    public static final String DEFAULT_IMG_PATH = "http://cdnq.duitang.com/uploads/item/201407/27/20140727123401_xGsWB.jpeg";
    public static final String DEFAULT_IMG_PATH = "http://img5q.duitang.com/uploads/item/201504/25/20150425H0257_mBGnS.jpeg";

    public static String MAI_PATH_REQUEST = "mar";
    public static String MAI_PATH_MONITOR = "me";
    public static String MAI_PATH_POSTLOG = "http://m.x.xchmai.com/ml";

//    public static final String MAI_PATH_REQUEST = "http://192.168.80.121:9008/mar";
//    public static final String MAI_PATH_MONITOR = "http://192.168.80.121:9008/me";
//    public static final String MAI_PATH_POSTLOG = "http://192.168.80.121:9008/ml";


    public static final String MAI_MONITORS_FILENAME = "Mai_Monitors";

    public static final String MAI_TRACKS_FILENAME = "Mai_Tracks";

    public static final String MAI_LOGS_FILENAME = "HGJNKJDHKDHKDHKDJJ";
    public static final String MAI_LOGS_FILENAME_S ="05468946232156468";
    public static final String MAI_LOGS_FILENAME_SS ="maiFileNameL";    //ss .xml

    public static final String MAI_VERSION = "1.0.1";

    public static void isTest(boolean isTest){
        if (isTest) {
            BASIC_PATH = BASIC_PATH_TEST1;
            LogUtil.D("Mode","Test");
        }else{
            BASIC_PATH = BASIC_PATH_FORMAL;
            LogUtil.D("Mode","Formal");
        }
        MAI_PATH_REQUEST = BASIC_PATH + "mar";
        MAI_PATH_MONITOR = BASIC_PATH + "me";
        MAI_PATH_POSTLOG = BASIC_PATH + "ml";
        L.e("path",MAI_PATH_REQUEST+"--"+MAI_PATH_MONITOR+"--"+MAI_PATH_POSTLOG);
    }
    public static void setTestMode(boolean isTest,int testMode){

        if (isTest && testMode == 1) {
            BASIC_PATH = BASIC_PATH_TEST1;
            LogUtil.D("Mode","Test-1-test");
        }else if (isTest && testMode == 2){
            BASIC_PATH = BASIC_PATH_TEST2;
            LogUtil.D("Mode","Test-2-ip");
        }else if (!isTest){
            BASIC_PATH = BASIC_PATH_FORMAL;
            LogUtil.D("Mode","Formal");
        }
        MAI_PATH_REQUEST = BASIC_PATH + "mar";
        MAI_PATH_MONITOR = BASIC_PATH + "me";
        MAI_PATH_POSTLOG = BASIC_PATH + "ml";
        L.e("path::::\n",MAI_PATH_REQUEST+"\n"+MAI_PATH_MONITOR+"\n"+MAI_PATH_POSTLOG);
    }


}
