package com.admai.sdk.mai;

/**
 * Created by ZAN on 16/9/6.
 */
public interface MaiCloseTypeListener {


    void closeBySelf();

    void closeByUser(int adType);

    void closeByClick();

    void closeByError();

}
