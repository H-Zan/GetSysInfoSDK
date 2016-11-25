package com.admai.sdk.util;

import com.admai.sdk.mai.MaiReportListener;
import com.admai.sdk.mai.MaiReportLogsListener;
import com.admai.sdk.str.MaiMonitor;
import com.admai.sdk.util.json.JsonFactory;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ZAN on 16/8/31.
 */
public class HttpHelper {
    private static final String TAG = HttpHelper.class.getSimpleName();
    ;
    
    public HttpHelper() {
    }

    /**
     * 曝光有参的
     *
     * @param object
     * @param _url
     * @param maiMonitor
     * @param listener
     * @return
     */
    public static String postMonitor(Object object, String _url, MaiMonitor maiMonitor, MaiReportListener listener) {

//        String json = new Gson().toJson(maiMonitor);     //生成 请求的数据 json  要发送给服务器的



        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpConn = null;
        ByteArrayOutputStream baos = null;
        L.e(TAG, "postM: http ");
        int responseCode = 0;
        try {
            // TODO: 2016/11/20  gson-json
            String json = JsonFactory.toJson(maiMonitor);     //生成 请求的数据 json  要发送给服务器的
            L.e(TAG+"--postMonitor(有参的post)", json);

            URL urlObj = new URL(_url);
            httpConn = (HttpURLConnection) urlObj.openConnection();

            // 如果通过post方式给服务器传递数据，那么setDoOutput()必须设置为true。否则会异常。
            // 默认情况下setDoOutput()为false。
            // 其实也应该设置setDoInput()，但是因为setDoInput()默认为true。所以不一定要写。

            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(5 * 1000);
            // 设置请求方式。请求方式有两种：POST/GET。注意要全大写。
            // POST传递数据量大，数据更安全，地址栏中不会显示传输数据。
            // 而GET会将传输的数据暴露在地址栏中，传输的数据量大小有限制，相对POST不够安全。但是GET操作灵活简便。

            // 判断是否要往服务器传递参数。如果不传递参数，那么就没有必要使用输出流了。

            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("charset", "UTF-8");

            if (json != null) {
                L.e(TAG, "postM  http 2" + json);
                byte[] data = json.getBytes();
                bos = new BufferedOutputStream(httpConn.getOutputStream());
                L.e(TAG, "postM  http 3 " + bos.toString());
                bos.write(data);
                bos.flush();
            }
            // 判断访问网络的连接状态
            httpConn.getResponseCode();
            if (httpConn.getResponseCode() == 200) {
                if (bos != null) {
                    L.e(TAG, "postM  http 4 " + bos.toString());
                }
                bis = new BufferedInputStream(httpConn.getInputStream());

                // 将获取到的输入流转成字节数组
                baos = new ByteArrayOutputStream();

                int c = 0;
                byte[] buffer = new byte[8 * 1024];

                while ((c = bis.read(buffer)) != -1) {
                    baos.write(buffer, 0, c);
                    baos.flush();
                }


                String s = new String(baos.toByteArray());
                L.e(TAG, "postM  http 5 " + s);       // s 无数据
                listener.onSucceed(object, s + "成功", maiMonitor);
                return s;
            }

        } catch (ProtocolException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }

            listener.onResponseError(object, e.toString(), responseCode);
            return "false";
        } catch (NullPointerException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }

            listener.onResponseError(object, e.toString(), responseCode);
            return "false";

        } catch (IOException e) {

            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }

            listener.onNetError(e.toString(), responseCode);
            return "false";

        } catch (Exception e) {

            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onNetError(e.toString(), responseCode);
            return "false";

        } finally {  //关流的顺序 对否?
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (baos != null) {
                    baos.close();
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            } catch (IOException e) {
                if (LogUtil.isShowError()) {
                    e.printStackTrace();
                }
            }
        }
        Exception exception = new Exception("没try到走到最后了");
        listener.onNetError(exception.toString(), responseCode);
        return "false";
    }


    /**
     * 曝光无参的
     *
     * @param object
     * @param _url
     * @param listener
     * @return
     */
    public static boolean postMonitorGet(Object object, String _url, MaiReportListener listener) {

        L.e(TAG, "postMonitorGet http1: ");

        HttpURLConnection httpConn = null;
        BufferedInputStream bis = null;
        int responseCode = 0;
        try {
            URL urlObj = new URL(_url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(5000);
            httpConn.connect();

            L.e(TAG, "postMonitorGet http 2: " + httpConn.getResponseCode());
            responseCode = httpConn.getResponseCode();
            if (httpConn.getResponseCode() == 200) {

                listener.onSucceed(object, "success get", null);

                L.e(TAG, "postMonitorGet: http success");

                return true;
            }

        } catch (ProtocolException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onResponseError(object, e.toString(), responseCode);
            return false;
        } catch (NullPointerException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onResponseError(object, e.toString(), responseCode);
            return false;
        } catch (IOException e) {

            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onNetError(e.toString(), responseCode);
            return false;
        } catch (Exception e) {

            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onNetError(e.toString(), responseCode);
            return false;
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            } catch (IOException e) {
                if (LogUtil.isShowError()) {
                    e.printStackTrace();
                }
            }
        }

        Exception exception = new Exception("没try到走到最后了");
        listener.onNetError(exception.toString(), responseCode);
        return false;
    }


    public static String postLogs(Object object, String _url, String logStr, MaiReportLogsListener listener) {


        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpConn = null;
        ByteArrayOutputStream baos = null;
        L.e(TAG, "LogSSUtil postLogs: http " + logStr);
        //        listener.onSucceed(object, "log发送成功+"+logStr,"---");       测试==
        int responseCode = 0;
        try {
            URL urlObj = new URL(_url);
            httpConn = (HttpURLConnection) urlObj.openConnection();

            // 如果通过post方式给服务器传递数据，那么setDoOutput()必须设置为true。否则会异常。
            // 默认情况下setDoOutput()为false。
            // 其实也应该设置setDoInput()，但是因为setDoInput()默认为true。所以不一定要写。

            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(5 * 1000);
            // 设置请求方式。请求方式有两种：POST/GET。注意要全大写。
            // POST传递数据量大，数据更安全，地址栏中不会显示传输数据。
            // 而GET会将传输的数据暴露在地址栏中，传输的数据量大小有限制，相对POST不够安全。但是GET操作灵活简便。

            // 判断是否要往服务器传递参数。如果不传递参数，那么就没有必要使用输出流了。

            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("charset", "UTF-8");

            if (logStr != null) {
                L.e(TAG, "LogSSUtil postLogs  http 2" + logStr);
                byte[] data = logStr.getBytes();
                bos = new BufferedOutputStream(httpConn.getOutputStream());
                L.e(TAG, "LogSSUtil postLogs  http 3 " + bos.toString());
                bos.write(data);
                bos.flush();
            }
            // 判断访问网络的连接状态
            responseCode = httpConn.getResponseCode();
            L.e(TAG, "LogSSUtil postLogs responseCode " + responseCode);

            if (httpConn.getResponseCode() == 200) {
                if (bos != null) {
                    L.e(TAG, "LogSSUtil postLogs  http 4 " + bos.toString());
                }
                bis = new BufferedInputStream(httpConn.getInputStream());

                // 将获取到的输入流转成字节数组
                baos = new ByteArrayOutputStream();

                int c = 0;
                byte[] buffer = new byte[8 * 1024];

                while ((c = bis.read(buffer)) != -1) {
                    baos.write(buffer, 0, c);
                    baos.flush();
                }


                String s = new String(baos.toByteArray());
                L.e(TAG, "LogSSUtil postLogs  http 5 " + s);       // s 无数据
                listener.onSucceed(object, s + "成功", logStr);
                return s;
            }

        } catch (ProtocolException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onResponseError(logStr, e.toString(), responseCode);
            return "false";
        } catch (NullPointerException e) {
            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onResponseError(logStr, e.toString(), responseCode);
            return "false";

        } catch (IOException e) {

            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onNetError(logStr,e.toString(), responseCode);
            return "false";

        } catch (Exception e) {

            if (LogUtil.isShowError()) {
                e.printStackTrace();
            }
            listener.onNetError(logStr,e.toString(), responseCode);
            return "false";

        } finally {  //关流的顺序 对否?
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (baos != null) {
                    baos.close();
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            } catch (IOException e) {
                if (LogUtil.isShowError()) {
                    e.printStackTrace();
                }
            }
        }
        Exception exception = new Exception("false");
        listener.onNetError(logStr,exception.toString(), responseCode);
        return "false";
    }

}
