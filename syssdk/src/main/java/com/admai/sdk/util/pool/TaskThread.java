/*
 * Copyright (C) 2012-2013 AdCocoa All Rights Reserved (http://www.adcocoa.com)
 */

package com.admai.sdk.util.pool;

import com.admai.sdk.util.log.LogUtil;

/**
 * 功能描述: 任务线程，配合线程池使用
 */
public class TaskThread extends Thread {

    private ITaskRunnable mTaskRunnable;

    private long mLastWorkTime;

    private Object[] mParams;

    private int mStatus;

    private Object mTag;

    private INotificationListener mNotificationListener;

    protected TaskThread(INotificationListener listener) {
        mNotificationListener = listener;
    }

    @Override
    public void run() {
        while (mStatus != ThreadPoolManager.STATUS_EXIT) {
            // 如果没有被分配任务，则工作线程进入休眠
            if (mTaskRunnable == null) {
                synchronized (this) {
                    try {
                        mStatus = ThreadPoolManager.STATUS_WAITING;
                        wait();
                    } catch (InterruptedException e) {
                        if (LogUtil.isShowError()) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Object object = null;
                try {
                    object = mTaskRunnable.onStart(mParams);
                } catch (Exception e) {
                    if (LogUtil.isShowError()) {
                        e.printStackTrace();
                    }
                } finally {
                    mTaskRunnable.onComplete(object, this);
                    // 记录最后一次被分配工作的时间，用于计算空闲时间
                    mLastWorkTime = System.currentTimeMillis();
                    // 注意这里不能清空任务，不然可能被线程池误判为已结束任务
                    // mTaskRunnable = null;
                    // mParams = null;

                    // 通知管理器
                    mNotificationListener.onFinishTask(TaskThread.this, ThreadPoolManager.FLAG_COMPLETE);
                }

            }
        }

        mNotificationListener.onFinishTask(this, ThreadPoolManager.FLAG_EXIT);
    }

    /**
     * 暂停任务
     */
    public void pauseTask() {
        if (mTaskRunnable != null) {
            mTaskRunnable.onPause(getName());
        }
    }

    /**
     * 继续任务
     */
    public void continueTask() {
        if (mTaskRunnable != null) {
            mTaskRunnable.onContinue(getName());
        }
    }

    /**
     * 退出任务
     */
    public void stopTask() {
        if (mTaskRunnable != null) {
            mTaskRunnable.onStop(getName());
        }
    }

    /**
     * 退出任务线程
     */
    public void stopThread() {
        mStatus = ThreadPoolManager.STATUS_EXIT;
        synchronized (this) {
            notify();
        }
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    protected int getStatus() {
        return mStatus;
    }

    protected void setStatus(int status) {
        mStatus = status;
    }

    protected long getLastWorkTime() {
        return mLastWorkTime;
    }

    public ITaskRunnable getTaskRunnable() {
        return mTaskRunnable;
    }

    protected void clearParams() {
        mParams = null;
    }

    protected void setTask(ITaskRunnable taskRunnable, Object... params) {
        mTaskRunnable = taskRunnable;
        mParams = params;
        if (taskRunnable != null) {
            mStatus = ThreadPoolManager.STATUS_RUNNING;
            // 唤醒线程开始执行任务
            synchronized (this) {
                // System.out.println("唤醒" + this.hashCode());
                notify();
            }
        }
    }
}
