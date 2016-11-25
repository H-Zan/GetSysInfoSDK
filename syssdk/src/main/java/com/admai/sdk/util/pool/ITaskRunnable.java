/*
 * Copyright (C) 2012-2013 AdCocoa All Rights Reserved (http://www.adcocoa.com)
 */

package com.admai.sdk.util.pool;

/**
 *
 * 功能描述: 任务接口类，推荐使用抽象适配层即可
 *
 *
 */
public interface ITaskRunnable {

    public Object onStart(Object... params);

    public void onStop(String taskId);

    public void onPause(String taskId);

    public void onContinue(String taskId);

    public void onComplete(Object result, TaskThread taskThread);
}
