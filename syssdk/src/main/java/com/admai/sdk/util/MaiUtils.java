package com.admai.sdk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by macmi001 on 16/7/7.
 * 工具类
 * 用来获取libs下assets的图片
 */
public class MaiUtils {
	private static final String TAG = MaiUtils.class.getSimpleName();
	
	public MaiUtils() {
	}
	
	public static Bitmap loadAssetsBitmap(Context context, String filePath) {
		InputStream is = null;
		Bitmap bitmap;
		try {
			is = context.getAssets().open(filePath);
		} catch (IOException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeStream(is, null, options);
		int width = options.outWidth;
		int height = options.outHeight;
		
		return bitmap;
	}
	
	public static boolean isTimeOut(long timeStamp, int days) {
		try {
			Date date = new Date(timeStamp);
			
			//            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			//            String ts = String.valueOf(timeStamp);
			//            Date time = formatter.parse(ts);
			
			
			Calendar cTime = Calendar.getInstance();
			cTime.setTime(date);
			cTime.add(Calendar.DATE, days);
			
			Date now = new Date();
			
			String str = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(cTime.getTime());
			L.e(TAG, "isTimeOut: str " + str);
			
			String str2 = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(now);
			L.e(TAG, "isTimeOut: now " + str2);
			return cTime.getTime().before(now);
			
		} catch (Exception e) {
			
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		}
		return true;
		
	}
	
	private static NetworkInfo getNetworkStatus(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo;

		        for (NetworkInfo info : connectivityManager.getAllNetworkInfo()) {
		            if (info.isConnected()) {
		                networkInfo = info;
		                break;
		            }
		        }
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			
//			Network[] networks = connectivityManager.getAllNetworks();
//
//			for (Network mNetwork : networks) {
//				networkInfo = connectivityManager.getNetworkInfo(mNetwork);
//				if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
//					return networkInfo;
//				}
//			}
			
		} else {
			
			if (connectivityManager != null) {
				//noinspection deprecation
				NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
				if (info != null) {
					for (NetworkInfo anInfo : info) {
						if (anInfo.isConnected()) {
							networkInfo = anInfo;
							return networkInfo;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public static boolean isWifi(Context context) {
		boolean isWifi = false;
		NetworkInfo info = getNetworkStatus(context);
		if (info != null) {
			isWifi = (info.getType() == ConnectivityManager.TYPE_WIFI);
		}
		return isWifi;
	}
	
	public static String getCurrentTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return dateFormat.format(new Date());
	}
	
	
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				                                                                 .getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	//    public static boolean isWiFiActive(Context inContext) {
	//        Context context = inContext.getApplicationContext();
	//        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	//        if (connectivity != null) {
	//            NetworkInfo[] info = connectivity.getAllNetworkInfo();
	//            if (info != null) {
	//                for (NetworkInfo anInfo : info) {
	//                    if (anInfo.getTypeName().equals("WIFI") && anInfo.isConnected()) {
	//                        L.e("MaiController===", "true");
	//                        return ping();
	//                    }
	//                }
	//            }
	//        }
	//        return false;
	//    }
	
	public static boolean ping() {
		
		String result = null;
		try {
			String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
			Process p = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 100 " + ip);// ping网址3次
			int status = p.waitFor();
			if (status == 0) {
				result = "success";
				return true;
			} else {
				result = "failed";
			}
		} catch (IOException e) {
			result = "IOException";
		} catch (InterruptedException e) {
			result = "InterruptedException";
		} finally {
			Log.d("----result---", "result = " + result);
		}
		return false;
	}
	
	public static boolean isSDCardMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	// 获取SDCard的可用空间大小
	public static long getSDCardAvailableSize() { // M
		long size = 0;
		if (isSDCardMounted()) {
			StatFs statFs = new StatFs(getSDCardBasePath());
			if (Build.VERSION.SDK_INT >= 18) {
				size = statFs.getAvailableBytes();
			} else {
				size = statFs.getAvailableBlocks() * statFs.getBlockSize();
			}
			return size / 1024 / 1024;
		} else {
			return 0;
		}
	}
	
	// 获取SDCard的根目录路径 
	public static String getSDCardBasePath() {
		if (isSDCardMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return null;
		}
	}
	
	// 保存bitmap图片到SDCard的私有Cache目录
	public static boolean saveBitmapToSDCard(Bitmap bitmap, String fileName, File file) throws Exception {
		if (isSDCardMounted()) {
			BufferedOutputStream bos = null;
			// 获取私有的Cache缓存目录
			bos = new BufferedOutputStream(new FileOutputStream(new File(file, fileName)));
			if (fileName != null && (fileName.contains(".png") || fileName.contains(".PNG"))) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			}
			bos.flush();
			bos.close();
			return true;
		} else {
			return false;
		}
		
	}
	
	public static int getBitmapSize(Bitmap bitmap) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {     //API 19
			return bitmap.getAllocationByteCount() / 1024;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
			return bitmap.getByteCount() / 1024 ;
		}
		return bitmap.getRowBytes() * bitmap.getHeight() / 1024 ;                //earlier version
	}
	
	public static Bitmap createThumbnail(byte[] data) throws Exception{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		options.inSampleSize = 2;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		return bm;
	}
	
	public static byte[] streamToByte(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c = 0;
		byte[] buffer = new byte[8 * 1024];
		
		while ((c = is.read(buffer)) != -1) {
			baos.write(buffer, 0, c);
			baos.flush();
		}
		baos.close();
		return baos.toByteArray();
	}
	
}
