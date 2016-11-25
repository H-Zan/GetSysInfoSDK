/*
 * Copyright (C) 2012-2013 AdCocoa All Rights Reserved (http://www.adcocoa.com)
 */

package com.admai.sdk.util.pool;

/**
 *
 *
 * 功能描述: 任务接口适配层
 *
 */
public abstract class ATaskRunnable implements ITaskRunnable {

    @Override
    public abstract Object onStart(Object... params);

    @Override
    public void onComplete(Object result, TaskThread taskThread) {

    }

    @Override
    public void onContinue(String taskId) {

    }

    @Override
    public void onPause(String taskId) {

    }

    @Override
    public void onStop(String taskId) {

    }

}
