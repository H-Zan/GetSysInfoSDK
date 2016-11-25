package com.admai.sdk.mai;

import com.admai.sdk.view.NotProguard;

/**
 * 接口回调第一步  设置接口
 *
 * Created by macmi001 on 16/7/6.
 * 广告事件监听器
 */
@NotProguard
public interface MaiListener {
//    /**
//     * 已发送广告投放请求感觉   这个不必要给开发者调用
//     *
//     * @param info
//     */
//
//    @NotProguard
//    public void onReceiveRequest(String info);

    /**
     * 已接收广告投放响应
     *
     * @param info
     */

    @NotProguard
    public void onReceiveAd(String info);

    /**
     * 接收广告投放响应失败
     *
     * @param error
     */

    @NotProguard
    public void onFailedToReceiveAd(String error);

    /**
     * 广告  自动关闭
     */

//    @NotProguard
//    public void onSkip();

    /**
     * 广告   被关闭
     */

    @NotProguard
    public void onClose();

    /**
     * 点击
     */
    @NotProguard
    public void onClick();




}