package com.admai.sdk.util.log;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.admai.sdk.mai.MaiManager;
import com.admai.sdk.mai.MaiPlatformController;
import com.admai.sdk.mai.MaiReportLogsListener;
import com.admai.sdk.str.MaiPath;
import com.admai.sdk.type.MaiLType;
import com.admai.sdk.util.HttpHelper;
import com.admai.sdk.util.MaiFileUtil;
import com.admai.sdk.util.MaiUtils;
import com.admai.sdk.util.json.JsonFactory;
import com.admai.sdk.util.pool.ATaskRunnable;
import com.admai.sdk.util.pool.ThreadPoolManager;

import java.io.File;

/**
 * 
 * Created by ZAN on 16/9/13.
 */
public class LogSSUtil {
	
	//日志只在wifi情况下发送?
	private static final String TAG = "lssut";
	
	private static String savePath;   //存储path
	
	private static String mFinalPath = "ImNotExist";  //最终path
	
	private static String mSSKey = "FinalFilePath";  //ss的key
	
	private static volatile LogSSUtil mLogSSUtil;
	
	private String adId;
	
	private String bc;
	
	private String androidId;
	
	private String aaId;
	
	private String phNum;
	
	private String imsi;
	
	private Context mContext;
	
	
	public static LogSSUtil getInstance() {
		if (mLogSSUtil == null) {
			
			synchronized (LogSSUtil.class) {
				if (mLogSSUtil == null) {
					
					mLogSSUtil = new LogSSUtil();
				}
			}
		}
		
		return mLogSSUtil;
	}
	
	public LogSSUtil() {
		
		mContext = MaiPlatformController.getInstance().getAppContext();
		
	}
	
	public void setAdId(String adId) {
		this.adId = adId;
	}
	
	public void setBc(String bc) {
		this.bc = bc;
	}
	
	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}
	
	public void setAaId(String aaId) {
		this.aaId = aaId;
	}
	
	public void setPhNum(String phNum) {
		this.phNum = phNum;
	}
	
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	
	public void saveLogs(int type, String errorInfo, int httpCode, String dFUrls) {
		MaiLogs maiLogs = new MaiLogs();
		
		maiLogs.m_event_type = type;
		maiLogs.m_event_time = MaiUtils.getCurrentTime();
		maiLogs.m_ad_id = this.adId;
		maiLogs.m_app_info = MaiManager.getaplctInfo();
		maiLogs.m_sdk_version = MaiPath.MAI_VERSION;
		maiLogs.m_net_type = MaiManager.getNetworktype();
		maiLogs.m_d_info = "MANUFACTURER:" + Build.MANUFACTURER + "--MODEL:" + Build.MODEL;
		maiLogs.m_error_info = errorInfo;
		maiLogs.m_http_code = httpCode;
		maiLogs.m_df_urls = dFUrls;
		maiLogs.m_d_id = MaiManager.deviceid;
		maiLogs.m_d_version = MaiManager.osVersion;
		maiLogs.m_bc=this.bc;
		maiLogs.m_android_id=this.androidId;
		maiLogs.m_aaid=this.aaId;
		maiLogs.m_ph_num=this.phNum;
		maiLogs.m_imsi=this.imsi;
		
//		Gson gson = new Gson();
//		String maiLogStr = gson.toJson(maiLogs);
		// TODO: 2016/11/20  gson-json   catch 块里错误???怎么整
		String maiLogStr = null;
		try {
			maiLogStr = JsonFactory.toJson(maiLogs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		maiLogStr = maiLogStr.replace("\\n", "__");
		maiLogStr = maiLogStr.replace("\\t", "__");
		L.e(TAG, "maiLogStr:" + maiLogStr);
		
		saveLogToFile(maiLogStr);
	}

	public void saveLogs(MaiLogs maiLogs) {

		// TODO: 2016/11/20  gson-json   catch 块里错误???怎么整
		String maiLogStr = null;
		try {
			maiLogStr = JsonFactory.toJson(maiLogs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		maiLogStr = maiLogStr.replace("\\n", "__");
		maiLogStr = maiLogStr.replace("\\t", "__");
		maiLogStr = maiLogStr.replace("\\/", "/");
		L.e(TAG+"sendLogs-sysInfo", "maiLogs:" + maiLogStr);

		saveLogToFile(maiLogStr);
	}

	public void sendLogs(MaiLogs maiLogs) {

		// TODO: 2016/11/20  gson-json   catch 块里错误???怎么整
		String maiLogStr = null;
		try {
			maiLogStr = JsonFactory.toJson(maiLogs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		maiLogStr = maiLogStr.replace("\\n", "__");
		maiLogStr = maiLogStr.replace("\\t", "__");
		maiLogStr = maiLogStr.replace("\\/", "/");
		L.e(TAG+"sendLogs-sysInfo", "maiLogs:" + maiLogStr);

		sendUpLogs("["+maiLogStr+"]");
	}

	//存
	private synchronized String saveLogToFile(String maiLogStr) {
		
		try {
			
			mContext = MaiPlatformController.getInstance().getAppContext();
			if (mContext != null) {
				savePath = mContext.getCacheDir().getPath();
				if (TextUtils.isEmpty(savePath)) {
					
					savePath = mContext.getCacheDir().getPath();
				}
				
				///////////////////////测试使用//////////////////////////////////////////////////////////
				//                switch (2) {
				//                    case 0:
				//                        savePath = mContext.getCacheDir().getPath();
				//                        break;
				//                    case 1:
				//                        savePath = mContext.getExternalCacheDir().getPath();
				//                        break;
				//                    case 2:
				//                        savePath = Environment.getExternalStorageDirectory().getPath();
				//                }
				///////////////////////////////////////////////////////////////////////////////////////
				
				
				maiLogStr = maiLogStr + ",";
				
				
				// String  fileName = System.currentTimeMillis()+".MAI"+System.currentTimeMillis()+".MAIMAI";
				
				
				String fileName = MaiPath.MAI_LOGS_FILENAME + ".MAI" + MaiPath.MAI_LOGS_FILENAME_S + ".MAIMAI";
				
				mFinalPath = LogFileUtil.saveLogs(mContext, maiLogStr, savePath, fileName, true);
				
				
				L.e(TAG, "savePath" + savePath + "----finalPath" + mFinalPath + "---fileName" + fileName);
				
				if (mFinalPath != null) {
					L.e(TAG, "保存log成功");
					SharedPreferences sharedPreferences = mContext.getSharedPreferences(MaiPath.MAI_LOGS_FILENAME_SS, Context.MODE_PRIVATE);
					SharedPreferences.Editor edit = sharedPreferences.edit();
					edit.clear();
					edit.putString(mSSKey, mFinalPath);
					boolean isCommitSucc = edit.commit();
					if (!isCommitSucc) {
						L.e(TAG, " edit.commit() false");
					}
				} else {
					L.e(TAG, "mFinalPath==null 保存log失败");
				}
				
				return fileName;
			} else {
				L.e(TAG, "context==null 无法得到CacheDir");
			}
		} catch (Exception e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
	
	
	public void sendLogs() {
		// 判断文件是否存在 不存在return  存在 读取 加工(去掉最后一个, 添加[ ],变为jsonArray) 发送
		// TODO: 16/9/13   9.14要做
		
		String maiLogs = readFromFile();  //read
		if (maiLogs == null) {
			return;
		}
		L.e(TAG, maiLogs);
		sendUpLogs(maiLogs);      //send
	}
	
	
	//读取Logs
	private synchronized String readFromFile() {
		String finalPath = "None";
		
		if (!"ImNotExist".equals(mFinalPath)) {
			finalPath = mFinalPath;
			
			L.e(TAG + "!\"ImNotExist\".equals(mFinalPath):", finalPath);
			
		} else {
			
			if (null == mContext) {
				L.e(TAG, "mContext==null   不能读取sha");
				return null;
			}
			
			File file = new File("data/data/" + mContext.getPackageName() + "/shared_prefs/" + MaiPath.MAI_LOGS_FILENAME_SS + ".xml");
			if (!file.isFile()) {
				L.e(TAG, "ss.xml not exist");
				return null;
			}
			
			SharedPreferences sharedPreferences = mContext.getSharedPreferences(MaiPath.MAI_LOGS_FILENAME_SS, Context.MODE_PRIVATE);
			finalPath = sharedPreferences.getString(mSSKey, "None");
			
			if (finalPath.equals("None")) {
				L.e(TAG, "log文件不存在");
				return null;
			}
			
		}
		String maiLogs = null;
		if (MaiFileUtil.isExistFile(finalPath)) {
			maiLogs = MaiFileUtil.readTextFile(finalPath);
			L.e(TAG, finalPath + "isExistFile:" + maiLogs);
		}
		
		return maiLogs;
	}
	
	//上传 logs
	private synchronized void sendUpLogs(final String maiLogs) {
		
		ThreadPoolManager.getInstance().publishTask("sendLogs",
		                                            new ATaskRunnable() {
			                                            @Override
			                                            public Object onStart(Object... params) {
				                                            synchronized (maiLogs) {
					                                            if (maiLogs != null) {
						                                            // FIXME: 16/9/14 可以加判断日志时间
						                                            //                          if (!MaiUtils.isTimeOut("日志时间,不过也不用加", MESSAGE_OUT_DAYS)) {  //7天之内
						                                            L.e(TAG, "发送log:::"+MaiPath.MAI_PATH_POSTLOG);
						                                            HttpHelper.postLogs("--", MaiPath.MAI_PATH_POSTLOG, maiLogs, new OnLogListener());

						
						                                            //                            }
					                                            }
				                                            }
				                                            return null;
			                                            }
		                                            });
		
	}
	
	class OnLogListener implements MaiReportLogsListener<Void> {  // 失败的时候记录log 并不对文件做处理   //发送的时候传listener 进去
		
		
		@Override
		public void onSucceed(Object context, String info, Object attachment) {
			L.e(TAG, "onSucceed: " + "OnLogListener" + info);
			L.e(TAG+"sendLogs-sysInfo", "onSucceed:");
			
			SharedPreferences sharedPreferences = mContext.getSharedPreferences(MaiPath.MAI_LOGS_FILENAME_SS, Context.MODE_PRIVATE);
			mFinalPath = sharedPreferences.getString(mSSKey, "None");
			L.e(TAG, "mFinalPath" + mFinalPath);
			
			boolean b = MaiFileUtil.deleteFile(mFinalPath);//删除文件
			
			if (b) {
				mFinalPath = "ImNotExist";
				
				
				if (null == mContext) {
					L.e(TAG, "上传log成功,删除文件成功,但重置ss失败");
					boolean d = MaiFileUtil.deleteFile(mFinalPath);
					return;
				}
				SharedPreferences sharedPreferences2 = mContext.getSharedPreferences(MaiPath.MAI_LOGS_FILENAME_SS, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = sharedPreferences.edit();
				edit.putString(mSSKey, "None");
				boolean commit = edit.commit();
				if (!commit) {
					L.e(TAG, "上传log成功 重置ss失败");
				} else {
					L.e(TAG, "删除已发送log成功");
				}
			} else {
				boolean xx = false;
				int a = 0;
				while (!xx && a <= 10) {
					xx = MaiFileUtil.deleteFile(mFinalPath);//删除文件
					a++;
					L.e("a", a + "" + xx);
				}
			}
		}
		
		@Override
		public void onResponseError(String logs, String e, int errorCode) {
			L.e(TAG, "onResponseError: " + "OnMonitListener" + e);
			
			onError(logs,e, errorCode);
			
		}
		
		@Override
		public void onNetError(String logs,String exception, int errorCode) {
			L.e(TAG, "onNetError: " + "OnMonitListener" + exception);
			
			onError(logs,exception, errorCode);
			
		}
		//先发送,发送失败存起来
		private void onError(String logs,String exception, int errorCode) {  // TODO: 16/8/30  错误的时候不做处理并且 记录日志发送失败的log
//			L.e(TAG+"sendLogs-sysInfo", "onError\n"+logs);
//			if (logs.startsWith("[")) {
//				logs = logs.replace("[", "");
//
//			}
//			if (logs.endsWith("]")) {
//				logs = logs.replace("]","");
//			}
//			saveLogToFile(logs);
			L.e(TAG+"sendLogs-sysInfo", "onError\n"+logs);
			saveLogs(MaiLType.LOG_SEND_F, "logs send error" + exception, errorCode, "none");
		}
		
	}
	
	
}
