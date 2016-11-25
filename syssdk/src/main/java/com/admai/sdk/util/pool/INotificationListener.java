/*
 * Copyright (C) 2012-2013 AdCocoa All Rights Reserved (http://www.adcocoa.com)
 */

package com.admai.sdk.util.pool;

/**
 *
 * 功能描述: 回调接口，用于任务线程通知主线程任务执行完毕
 *
 */
public interface INotificationListener {

    public void onFinishTask(TaskThread thread, int flag);

}
