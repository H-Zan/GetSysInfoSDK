/*
 * Copyright (C) 2012-2013 AdCocoa All Rights Reserved (http://www.adcocoa.com)
 */

package com.admai.sdk.util.pool;

public interface IWaitingTaskListener {

    /**
     * 当自动启动了一个被挂起的任务时回调，注意这是线程执行的！
     */
    public void onWaitingTaskStartedByThreadPool(TaskThread thread);

}
