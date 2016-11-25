package com.admai.sdk.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.admai.sdk.type.MaiLType;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogSSUtil;
import com.admai.sdk.util.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZAN on 16/9/6.
 */
public class MaiCrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = MaiCrashHandler.class.getSimpleName();
    ;
    public boolean isInitHere = false;
    public boolean isInit = false;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //
    private UncaughtExceptionHandler mHandlerForLast;
    private static UncaughtExceptionHandler mHandlerForSystem;
    //CrashHandler实例
    private static volatile MaiCrashHandler sMaiCrashHandler;
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    
    private MaiCrashHandler() {
        
    }


    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static MaiCrashHandler getInstance() {
        if (sMaiCrashHandler == null) {

            synchronized (MaiCrashHandler.class) {
                if (sMaiCrashHandler == null) {

                    sMaiCrashHandler = new MaiCrashHandler();
                }
            }
        }

        return sMaiCrashHandler;
    }

    /**
     * 初始化
     */
    public void init() {

        UncaughtExceptionHandler handlerForChange;  //内部

        if ((handlerForChange = Thread.getDefaultUncaughtExceptionHandler()) != null) {
            String thisUeName = this.getClass().getName();     //此ueName
            String var3 = handlerForChange.getClass().getName();            //deFault Ue Name(上次)
            if (!thisUeName.equals(var3)) { //不相等的时候 说明 是第一次 设置
                if ("com.android.internal.os.RuntimeInit$UncaughtHandler".equals(handlerForChange.getClass().getName())) { //说明 default 是系统的  app开发者没有设置

                    L.e(TAG, "backup system java handler: %s", new Object[]{handlerForChange.toString()});

                    mHandlerForSystem = handlerForChange;    //yong f 接收 defaut   system

                    this.mHandlerForLast = handlerForChange;   //e 也接收 default

                } else {          //如果 不是系统的
                    L.e(TAG, "backup java handler: %s", new Object[]{handlerForChange.toString()});
                    this.mHandlerForLast = handlerForChange;  //用 e 接收 default
                }

                this.forSure(handlerForChange); //用e接收 handlerForChange
                Thread.setDefaultUncaughtExceptionHandler(this); //把自己设为default

                this.isInit = true;
                this.isInitHere = true;
                L.e(TAG, "registered java monitor: %s", new Object[]{this.toString()});
            }

        }


    }


    private synchronized void forSure(UncaughtExceptionHandler handler) {
        this.mHandlerForLast = handler;
    }


    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    //    @Override
    //    public final void uncaughtException(Thread thread, Throwable ex) {
    //        if (!handleException(ex) && mDefaultHandler != null) {
    //            //如果没有处理则让系统默认的异常处理器来处理
    //            mDefaultHandler.uncaughtException(thread, ex);
    //        } else {
    //            //            try {
    //            //                Thread.sleep(3000);
    //            //            } catch (InterruptedException e) {
    //            //                Log.e(TAG, "error : ", e);
    //            //            }
    //            Log.e(TAG, ex.toString());
    //
    //            mDefaultHandler.uncaughtException(thread, ex);
    //
    //            //退出程序
    //            android.os.Process.killProcess(android.os.Process.myPid());
    //            System.exit(1);
    //        }
    //    }
    @Override
    public final void uncaughtException(Thread var1, Throwable var2) {
        this.handleException(var1, var2, true);
    }

    public final void handleException(Thread thread, Throwable ex, boolean var3) {
        if (var3) { //true
            L.e("Java Crash Happen cause by %s(%d)", new Object[]{thread.getName(), Long.valueOf(thread.getId())});
            //            if (c()) {
            //                L.e("this class has handled this exception", new Object[0]);
            //                if (mHandlerForSystem != null) { //f:system
            //                    L.e("call system handler", new Object[0]);
            //                    mHandlerForSystem.uncaughtException(thread, ex);
            //                } else {
            //                    L.e("current process die", new Object[0]);
            //                    Process.killProcess(Process.myPid());
            //                    System.exit(1);
            //                }
            //            }
        } else {
            L.e("Java Catch Happen", new Object[0]);
        }

        try {
            if (!this.isInitHere) {//g false   didint init   return
                L.e("Java crash handler is disable. Just return.", new Object[0]);
                return;
            }


            if (!getEXString(ex).equals("fail")) {
                //save
                LogSSUtil.getInstance().saveLogs(MaiLType.CRASH, getEXString(ex), 0, "none");
                return;
            }

            L.e("pkg crash datas fail!", new Object[0]);
        } catch (Throwable e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }

            return;
        } finally {
            if (!var3) {
                L.e("not to shut down return");
                return;
            }
            //分开  因为default Ue 只有一个
            if (this.mHandlerForLast != null && isLastHandler(this.mHandlerForLast)) {    // 此 ue !=null  this.e捕捉  e: 上一个 handler
                L.e("sys default last handle start!");
                this.mHandlerForLast.uncaughtException(thread, ex);
                L.e("sys default last handle end!");
            } else if (mHandlerForSystem != null) {           // f!=null f 捕捉     f=system的ue
                L.e("system handle start!");
                mHandlerForSystem.uncaughtException(thread, ex);
                L.e("system handle end!");
            } else {                       //都没有的时候 关闭程序
                L.e("crashreport last handle start!");
                L.e("current process die");
                Process.killProcess(Process.myPid());
                System.exit(1);
                L.e("crashreport last handle end!");
            }

        }

    }

    private static boolean isLastHandler(UncaughtExceptionHandler handler) {
        if (handler == null) {
            return true;
        } else {
            String handlerName = handler.getClass().getName();
            String mStr = "uncaughtException";
            StackTraceElement[] stes;  //当前线程的 stackTrace中的信息
            int len = (stes = Thread.currentThread().getStackTrace()).length;

            for (int i = 0; i < len; ++i) {
                StackTraceElement ste;
                String clsName = (ste = stes[i]).getClassName();
                String mName = ste.getMethodName();
                if (handlerName.equals(clsName) && mStr.equals(mName)) {        //方法名或者className与handler相等的时候flase
                    return false;        //flase go down  >>> system handler or more down
                }
            }

            return true;          // true 的时候才会确定 last handler
        }
    }


    private String getEXString(Throwable ex) {    //ex cause
        if (ex == null) {
            return "fail";
        } else {
            try {
                StringWriter stringWriter = new StringWriter();
                ex.printStackTrace(new PrintWriter(stringWriter));
                return stringWriter.getBuffer().toString();
            } catch (Throwable e) {
                if (LogUtil.isShowError()) {
                    e.printStackTrace();
                }

                return "fail";
            }
        }
    }

    //    //在log里 b：isDebug
    //    private static boolean isD=false;
    //    public static boolean a(Throwable var0) {
    //        Throwable var1 = var0;
    //        byte var2 = 2;
    //        if (!isD) {
    //            return false;
    //        } else {
    //            String var3 = a.a(var1);
    //            return a(var2, var3, new Object[0]);
    //        }
    //    }


    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //收集设备参数信息
        collectDeviceInfo(mContext);


        //保存日志文件
        saveCatchInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCatchInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {

            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = "/mnt/sdcard/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                //发送
                sendCrashLog2PM(path + fileName);
                fos.close();
            }

            return fileName;

        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    /**
     * 将捕获的导致崩溃的错误信息发送
     * <p/>
     * 目前只将log日志保存在sdcard 和输出到LogCat中，并未发送给后台。
     */
    private void sendCrashLog2PM(String fileName) {
        if (!new File(fileName).exists()) {
            Toast.makeText(mContext, "日志文件不存在！", Toast.LENGTH_SHORT).show();
            return;
        }
        FileInputStream fis = null;
        BufferedReader reader = null;
        String s = null;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (true) {
                s = reader.readLine();
                if (s == null)
                    break;
                //由于目前尚未确定以何种方式发送，所以先打出log日志。
                Log.i("maiErrorInfo", s.toString());
            }
        } catch (FileNotFoundException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
        } finally {   // 关闭流
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                if (LogUtil.isShowError()) {
                    e.printStackTrace();
                }
            }
        }
    }
}