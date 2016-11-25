package com.admai.sdk.util.pool;

import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogUtil;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;

/**
 * 类名: ThreadPoolManager
 * 功能描述: 自定义线程池
 */
public class ThreadPoolManager {

    public static final int STATUS_WAITING = 0;

    public static final int STATUS_RUNNING = 1;

    public static final int STATUS_EXIT = 2;

    public static final int FLAG_COMPLETE = 10;

    public static final int FLAG_EXIT = 11;

    private final String THREAD_LOCK = "thread_lock";

    private static final String INSTANCE_LOCK = "instance_lock";

    private static ThreadPoolManager mThreadPoolManager;

    // 设定线程池的初始化线程数量，超过这个数量的线程，在执行任务完毕后会被关闭
    private int mMaxThreadCount = 3;

    // 空闲的持续时间，超过3秒处于空闲状态的线程，且超过最大线程数，就会被关闭
    private int mFreeDuration = 3000;

    private LinkedList<TaskThread> mThreadList;

    private int mLimit = 99999;

    private NotificationListener mNotificationListener;

    private LinkedList<WaitingTask> mWaitingTasks;

    private ThreadPoolManager() {
        mNotificationListener = new NotificationListener();
        mThreadList = new LinkedList<TaskThread>();
        mWaitingTasks = new LinkedList<WaitingTask>();
    }

    public static ThreadPoolManager getInstance() {
        synchronized (INSTANCE_LOCK) {
            if (mThreadPoolManager == null) {
                mThreadPoolManager = new ThreadPoolManager();
            }
            return mThreadPoolManager;
        }
    }

    /**
     * 函数名: initThreadPool<br>
     * 功能: 设定线程池的启动参数<br>
     *
     * @param maxThreadCount
     *     最大线程数
     * @param freeDuration
     *     允许的持续空闲时间，超过这个空闲时间且超过最大线程数的线程会被关闭<br>
     *     只能被初始化一次，如果要重新初始化线程池，要先执行closeThreadPool关闭线程池
     */
    public synchronized void initThreadPool(int maxThreadCount, int freeDuration) {
        initThreadPool(maxThreadCount, Integer.MAX_VALUE, freeDuration);
    }

    public int getThreadCount() {
        return mThreadList.size();
    }

    /**
     * 函数名: initThreadPool<br>
     * 功能: 设定线程池的启动参数<br>
     *
     * @param maxThreadCount
     *     初始化线程数
     * @param limit
     *     最大线程数
     * @param freeDuration
     *     允许的持续空闲时间，超过这个空闲时间且超过最大线程数的线程会被关闭<br>
     *     只能被初始化一次，如果要重新初始化线程池，要先执行closeThreadPool关闭线程池
     */
    public synchronized void initThreadPool(int maxThreadCount, int limit,
                                               int freeDuration) {
        // 如果未执行过初始化操作，则开始初始化线程
        if (mThreadList.size() == 0) {
            mLimit = limit;
            if (maxThreadCount > 0) {
                mMaxThreadCount = maxThreadCount;
            }
            if (mMaxThreadCount > mLimit) {
                mMaxThreadCount = mLimit;
            }
            if (freeDuration > 0) {
                mFreeDuration = freeDuration;
            }
            for (int i = 0; i < mMaxThreadCount; i++) {
                TaskThread taskThread = new TaskThread(mNotificationListener);
                mThreadList.add(taskThread);
                taskThread.start();
            }
        }
    }

    /**
     * 设置线程池线程上限，默认为99999，达到上限后任务将被挂起，直到有空闲线程后才重新分配任务
     */
    public void setLimit(int limit) {
        if (limit < 1) {
            limit = 99999;
        }
        mLimit = limit;
    }

    /**
     * 获取一个被挂起的任务
     */
    public WaitingTask getWaitingTask(String taskId) {
        if (taskId != null) {
            for (WaitingTask task : mWaitingTasks) {
                if (task.mTaskId.equals(taskId)) {
                    return task;
                }
            }
        }
        return null;
    }

    /**
     * 取消一个指定的被挂起任务
     */
    public void cancelWaitingTask(String taskId) {
        synchronized (THREAD_LOCK) {
            for (WaitingTask task : mWaitingTasks) {
                if (task.mTaskId.equals(taskId)) {
                    mWaitingTasks.remove(task);
                    break;
                }
            }
        }
    }

    /**
     * 取消所有被挂起的任务
     */
    public void cancelAllWaitingTask() {
        synchronized (THREAD_LOCK) {
            mWaitingTasks.clear();
        }
    }

    /**
     * 函数名: publishTask<br>
     * 功能: 发布一个任务<br>
     *
     * @param runnable
     *     任务接口
     * @param params
     *     任务参数
     * @return 任务被挂起时，return null
     * @throws Exception
     */
    public synchronized TaskThread publishTask(String taskId, ITaskRunnable runnable, Object... params) {
        return publishTask(taskId, runnable, null, params);
    }

    public synchronized TaskThread publishTask(String taskId, ITaskRunnable runnable, IWaitingTaskListener listener, Object... params) {
        if (runnable == null) {
            throw new IllegalArgumentException("任务不能为null");
        }

        // 提高效率
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
        }

        // 分配任务给空闲线程
        TaskThread taskThread = getFreeTaskThread();
        if (taskThread == null) {// 没找到空闲线程按常理应该要去新建一个线程
            // 判断线程数量是否超限，没超限则新建线程，并分配任务
            if (mThreadList.size() < mLimit) {
                taskThread = new TaskThread(mNotificationListener);
                mThreadList.add(taskThread);
                if (taskId == null) {
                    taskId = String.valueOf(taskThread.hashCode());
                }
                taskThread.setName(taskId);
                taskThread.setTask(runnable, params);
                taskThread.start();
            } else {// 如果超限，则挂起任务
                WaitingTask waitingTask = new WaitingTask();
                if (taskId == null) {
                    taskId = String.valueOf(waitingTask.hashCode());
                }
                waitingTask.mTaskId = taskId;
                waitingTask.mTaskRunnable = runnable;
                waitingTask.mTaskParams = params;
                waitingTask.mWaitingTaskListener = listener;
                // 注意：这里锁掉会导致主线程卡死
                mWaitingTasks.add(waitingTask);
            }
        } else {// 有空闲线程则直接提交任务
            if (taskId == null) {
                taskId = String.valueOf(taskThread.hashCode());
            }
            taskThread.setName(taskId);
            taskThread.setTask(runnable, params);
        }
        return taskThread;
    }

    /**
     * 函数名: closeThreadPool<br>
     * 功能: 关闭线程池<br>
     */
    public void closeThreadPool() {
        synchronized (THREAD_LOCK) {
            for (int i = mThreadList.size() - 1; i >= 0; i--) {
                mThreadList.get(i).stopThread();
            }
            mWaitingTasks.clear();
        }
    }

    /**
     * 函数名: stopTasks<br>
     * 功能: 批量停止任务<br>
     *
     * @param threads
     *     任务线程数组
     */
    public void stopTasks(TaskThread... threads) {
        for (TaskThread taskThread : threads) {
            taskThread.stopTask();
        }
    }

    private TaskThread getFreeTaskThread() {
        try {
            for (int i = mThreadList.size() - 1; i >= 0; i--) {
                TaskThread taskThread = mThreadList.get(i);
                if (taskThread.getTaskRunnable() == null && taskThread.getStatus() == STATUS_WAITING) {
                    return taskThread;
                }
            }
        } catch (ConcurrentModificationException e) {
        }
        return null;
    }

    // 检查空闲线程
    private void checkFreeThread(TaskThread thread, int flag) {
        synchronized (THREAD_LOCK) {
            if (flag == FLAG_EXIT) {
                mThreadList.remove(thread);
            }

            // 检查是否有暂挂任务，且设置了自动启动被挂起的任务
            if (mWaitingTasks.size() > 0) {
                WaitingTask task = mWaitingTasks.remove(0);
                thread.setName(task.mTaskId);
                thread.setTask(task.mTaskRunnable, task.mTaskParams);
                // 通知到监听器
                if (task.mWaitingTaskListener != null) {
                    task.mWaitingTaskListener.onWaitingTaskStartedByThreadPool(thread);
                }

            } else {
                // 标记任务结束，线程转入可重用状态
                thread.setName("free");
                thread.clearParams();
                thread.setTask(null, new Object[]{});
            }

            // 仅当当前线程数量超出预设的最大值时，才触发该检查
            if (mThreadList.size() > mMaxThreadCount) {
                for (int i = mThreadList.size() - 1; i > 0; i--) {
                    TaskThread taskThread = mThreadList.get(i);
                    // 如果是空闲线程，且超过空闲时间，则关闭
                    if (taskThread.getStatus() == STATUS_WAITING
                            && isOverFreeDuration(taskThread)) {
                        taskThread.stopThread();
                        try {
                            mThreadList.remove(taskThread);
                        } catch (ConcurrentModificationException e) {
                            L.e(this.getClass().getName(), e.getMessage()
                                                               + "");
                        }
                        // System.out.println("线程被移除，当前线程池大小：" +
                        // mThreadList.size());
                    }

                    // 如果关闭到剩余最大上限数，就退出检测
                    if (mThreadList.size() < mMaxThreadCount) {
                        break;
                    }
                }
            }
        }
    }

    // 当前线程的空闲时间是否超过了预设值
    private boolean isOverFreeDuration(TaskThread taskThread) {
        if (System.currentTimeMillis() - taskThread.getLastWorkTime() > mFreeDuration) {
            return true;
        }
        return false;
    }

    public class WaitingTask {
        public ITaskRunnable mTaskRunnable;
        public Object[] mTaskParams;
        public String mTaskId;
        public IWaitingTaskListener mWaitingTaskListener;
    }

    private class NotificationListener implements INotificationListener {

        @Override
        public void onFinishTask(TaskThread thread, int flag) {
            // 当任务线程执行任务完毕后，回调该方法通知线程池管理器
            checkFreeThread(thread, flag);
        }
    }
}
