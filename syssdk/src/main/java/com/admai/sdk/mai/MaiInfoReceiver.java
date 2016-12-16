package com.admai.sdk.mai;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.admai.sdk.type.MaiLType;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogSSUtil;
import com.admai.sdk.util.log.LogUtil;
import com.admai.sdk.util.persistance.SharePreferencePersistance;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;

import static com.admai.sdk.mai.MaiManager.getNetworkClass;

/**
 * 判断wifi是否链接,wifi链接且location!=null,就抓取,否则换为gps,gps等location改变时,记录位置发送???
 * 抓取地理位置:在应用开启期间只抓取一次: 有没有只调用一次的方法
 * gps-- location 有时会为null,还不知空旷地区是否第一次为null
 *         
 *         
 * 因为有时,或者第一次抓取的话,location有可能是null 这样position是没有数据的,这时不能发送
 * 但是在位置监听器里,只要调用,position就是有数据的,位置改变时会被多次调用 : (怎样让位置改变时只调用一次?)
 * 怎么等position有数据的时候再发送而且只发送一次?
 *
 * ---怎么判断连接了不同的 wifi ???
 * ---可以吧上次的ssid存到shared中,下次链接上之后判断一下,如果相同就不发送,如果不同再发送
 *
 * ===位置在打开期间只记录一次SharedPreference能否实现?        
 * ===若location!=null,且有position时,即刻发送.
 * ===position为0.00,0.00的情况下:利用SharedPreference在oncreate中初始化,在位置回调时:如果值为初始值,记录并发送位置,改为已经发送的值;否则,不做任何事.
 *
 * Created by Zan on 2016/11/22.
 */

public class MaiInfoReceiver extends BroadcastReceiver {
	private  Context mContext;
	private LocationManager mLocationManager;
	private String postion;
	private double latitude;
	private double longitude;
	private String provider;
	private String LOCATION_KEY = "HasLocation";
	private String wifissid;
	private String ipaddress;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		if (LocationManager.MODE_CHANGED_ACTION.equals(intent.getAction())) {
			L.e("GPS");

		}
		if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
			L.e("GPS-----");
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(gpsEnabled){
				L.e("gps-已打开");
				SharePreferencePersistance share = new SharePreferencePersistance();
				String string = share.getString(mContext, LOCATION_KEY, "None");
				//已经有过位置
				if (string.equals("HasLocation")) {
					//do nothing
					Toast.makeText(mContext,string+",doNothing", Toast.LENGTH_SHORT).show();
				}else {
					//没有获得过位置
					Toast.makeText(mContext, string + ",NoLocation", Toast.LENGTH_SHORT).show();
					//获取位置
					openAndGetLocation(context);
				}
//				MaiManager.getInstance(context).getSysInfo(context);
//				postion=MaiManager.getPostion();
		    L.e("gps-已打开",postion);
			}else {
				L.e("gps-已关闭",postion);
			}
		}
		//监听语言与地区改变
		if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
			Locale locale = context.getResources().getConfiguration().locale;
			String language = locale.getLanguage();  //
			String country = locale.getCountry();    //
			String displayCountry = locale.getDisplayCountry();
			String displayLanguage = locale.getDisplayLanguage();
			LogSSUtil.getInstance().saveLogs(MaiLType.SYS_INFO_LATER,"LAN:"+language+"-Country:"+country,0,"none");
			LogSSUtil.getInstance().sendLogs();
		}

		//监听网络连接 : 确保一次或者能够分辨wifi网络有改变
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			//测试
			int networktype = networktype(context);
			LogSSUtil.getInstance().saveLogs(MaiLType.SYS_INFO_LATER, "networktype:" + networktype + "-ipaddress:" + ipaddress + "-wifissid:" + wifissid, 0, "none");
			LogSSUtil.getInstance().sendLogs();
			L.e("CONNECTIVITY_ACTION", "networktype:" + networktype + "-ipaddress:" + ipaddress + "-wifissid:" + wifissid);
			//连接了之后开启服务? 或者直接不用服务
		}
		
		//证明:
		//1.没开启app的情况下,连接上wifi之后开启服务,打印log

		//2.地理位置的监听
	}
	

	//位置相关
	private void openAndGetLocation(Context context) {
	   L.e("location","openAndGetLocation");
		boolean providerEnabled = false;
		try {
			mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			providerEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!providerEnabled) {
				L.e("location","!providerEnabled");
				providerEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			}
			L.e("location","openAndGetLocation2");
		} catch (Exception e) {
			e.printStackTrace();
			L.e("Please", "Add ACCESS_FINE_LOCATION Or ACCESS_COARSE_LOCATION Permission");
		}
		if (providerEnabled) {
			L.e("location","providerEnabled");
			getLocation(context);
			L.e("location", "openAndGetLocation: " + "有权限");
			//            Toast.makeText(this, "定位模块正常", Toast.LENGTH_SHORT).show();
		} else {
			L.e("location","provider-NOT-Enabled");
			postion = latitude + "," + longitude;
		}
		//        Toast.makeText(this, "请开启定位权限", Toast.LENGTH_SHORT).show();
		//        // 跳转到GPS的设置页面
		//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		//        startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
	}

	private void getLocation(Context con) {
		L.e("location","getLocation");
		// android通过criteria选择合适的地理位置服务
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
		//        criteria.setAccuracy(Criteria. ACCURACY_COARSE);// 高精度
		criteria.setAltitudeRequired(false);// 设置不需要获取海拔方向数据
		criteria.setBearingRequired(false);// 设置不需要获取方位数据
		criteria.setCostAllowed(true);// 设置允许产生资费
		criteria.setPowerRequirement(Criteria.POWER_LOW);//  低功耗  wifi
//		criteria.setPowerRequirement(Criteria.POWER_HIGH);// 高功耗 gps
		
		provider = mLocationManager.getBestProvider(criteria, true);// 获取GPS信息
		L.e("location", "provider: location " + provider);
		Toast.makeText(con, provider+"1", Toast.LENGTH_SHORT).show();
		if (provider != null) {
			L.e("location", "provider != null");

			Location location = mLocationManager.getLastKnownLocation(provider);// 通过GPS获取位置
			if (location == null) {
				criteria.setPowerRequirement(Criteria.POWER_LOW);// 低功耗  wifi
				provider = mLocationManager.getBestProvider(criteria, true);// 获取GPS信息
				if (provider != null) {
					
					location = mLocationManager.getLastKnownLocation(provider);
					L.e("location", "location == null");
					L.e("location", "provider:" + provider);
					Toast.makeText(con, provider+"2", Toast.LENGTH_SHORT).show();
				}
			}
			if (provider != null) {
				if (location == null && provider.contains("network")) {  //没有wifi 情况下 变成gps
					criteria.setPowerRequirement(Criteria.POWER_HIGH);// 高功耗  gps
					provider = mLocationManager.getBestProvider(criteria, true);// 获取GPS信息
					if (provider != null) {
						location = mLocationManager.getLastKnownLocation(provider);
						L.e("location", "location == null");
						L.e("location", "provider:" + provider);
						Toast.makeText(con, provider + "3", Toast.LENGTH_SHORT).show();
					}
				}
			}
			updateUIToNewLocation(location);
			L.e("location", location);
			// 设置监听器，自动更新的最小时间为间隔N秒(这里的单位是微秒)或最小位移变化超过N米(这里的单位是米) 0.00001F
			mLocationManager.requestLocationUpdates(provider, 1000, 1, locationListener);
		} else {
			L.e("Please", "Add ACCESS_FINE_LOCATION Or ACCESS_COARSE_LOCATION Permission");
		}
	}


	private void updateUIToNewLocation(Location location) {
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			postion = latitude + "," + longitude;
			// Location类的方法：
			// getAccuracy():精度（ACCESS_FINE_LOCATION／ACCESS_COARSE_LOCATION）
			// getAltitude():海拨
			// getBearing():方位，行动方向
			// getLatitude():纬度
			// getLongitude():经度
			// getProvider():位置提供者（GPS／NETWORK）
			// getSpeed():速度
			// getTime():时刻
		} else {
			postion = +latitude + "," + longitude;
		}
	}
	
	// 定义对位置变化的监听函数
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			SharePreferencePersistance share = new SharePreferencePersistance();
			String string = share.getString(mContext, LOCATION_KEY, "None");
			if (string.equals("HasLocation")) {
				Toast.makeText(mContext,string+",doNothing,onLocationChanged", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, string + ",NoLocation,onLocationChanged", Toast.LENGTH_SHORT).show();
				if (location != null) {
					//得到位置
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					postion = latitude + "," + longitude;
					//发送日志
					LogSSUtil.getInstance().saveLogs(MaiLType.SYS_INFO_LATER,"postion:"+postion,0,"none");
					LogSSUtil.getInstance().sendLogs();
					
					Toast.makeText(mContext, postion, Toast.LENGTH_SHORT).show();
					L.e("location", "onLocationChanged: " + "纬度：" + location.getLatitude() + "\n经度" + location.getLongitude());
					//
					share.putString(mContext,LOCATION_KEY,"HasLocation");
				} else {
					postion = latitude + "," + longitude;
					L.e("location","!!!!NO");
				}
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

			L.e("location", "onStatusChanged: " + "privider:" + provider + "status:" + status + "extras:" + extras);
		}

		public void onProviderEnabled(String provider) {

			L.e("location", "onProviderEnabled: " + "privider:" + provider);

		}

		public void onProviderDisabled(String provider) {

			L.e("location", "onProviderDisabled: " + "privider:" + provider);

		}
	};
	
	
	
	
	//网络相关
	public int networktype(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		int networkType = 0;
		try {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mobNetInfoActivity = connectivityManager.getActiveNetworkInfo(); //没有网络的时候是null
			if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
				L.e("CONNECTIVITY_ACTION","无网络");
				networkType = 0;
			} else {   //有网络
				// NetworkInfo不为null开始判断是网络类型
				L.e("CONNECTIVITY_ACTION","有网络");
				int netType = mobNetInfoActivity.getType();
				if (netType == ConnectivityManager.TYPE_WIFI) {
					// wifi net处理
					networkType = 2;
					ipaddress = getwifiipaddress(wifi);
					L.e("CONNECTIVITY_ACTION","-ipaddress:"+ipaddress+"-wifissid:"+wifissid);
				} else if (netType == ConnectivityManager.TYPE_MOBILE) {
					networkType = getNetworkClass(tm.getNetworkType());
					ipaddress = getnetipaddress();
				}
				L.e("isAvailable",mobNetInfoActivity.isAvailable());
			}
			
		} catch (Exception e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
			return networkType = 0;
		}
		return networkType;
	}
	
	private String getwifiipaddress(WifiManager wifi) {
		//获取wifi服务
		//判断wifi是否开启
		if (wifi.isWifiEnabled()) {
			
			WifiInfo wifiInfo = wifi.getConnectionInfo();
			wifissid = wifiInfo.getSSID();
			//            wifissid = "aaaa";
			
			if (wifissid.indexOf("\"") == 0) {
				wifissid = wifissid.substring(1, wifissid.length());      //去掉第一个 "
			}
			if (wifissid.lastIndexOf("\"") == (wifissid.length() - 1)) {
				wifissid = wifissid.substring(0, wifissid.length() - 1);  //去掉最后一个 "
			}
			L.e("CONNECTIVITY_ACTION","-wifissid:"+wifissid);
			int ipAddress = wifiInfo.getIpAddress();
			return intToIp(ipAddress);
		}
		return null;
	}
	
	private String intToIp(int i) {
		
		return (i & 0xFF) + "." +
		       ((i >> 8) & 0xFF) + "." +
		       ((i >> 16) & 0xFF) + "." +
		       (i >> 24 & 0xFF);
	}
	
	private String getnetipaddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return "";
	}
	
	//service相关
	private boolean isServiceRunning(Context context, String serviceName) {
		if (!TextUtils.isEmpty(serviceName) && context != null) {
			ActivityManager activityManager
				= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			ArrayList<RunningServiceInfo> runningServiceInfoList
				= (ArrayList<RunningServiceInfo>) activityManager.getRunningServices(100);
			for (RunningServiceInfo runningServiceInfo : runningServiceInfoList) {
				if (serviceName.equals(runningServiceInfo.service.getClassName())) {
					return true;
				}
			}
		} else {
			return false;
		}
		return false;
	}
}
