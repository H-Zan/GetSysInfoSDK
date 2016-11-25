package com.admai.sdk.mai;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

import com.admai.sdk.str.MaiMonitor;
import com.admai.sdk.str.MaiRequest;
import com.admai.sdk.str.MaiTrack;
import com.admai.sdk.type.MaiLType;
import com.admai.sdk.type.MaiStype;
import com.admai.sdk.type.MaiType;
import com.admai.sdk.util.MD5;
import com.admai.sdk.util.MaiUtils;
import com.admai.sdk.util.json.JsonFactory;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogSSUtil;
import com.admai.sdk.util.log.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by macmi001 on 16/7/4.
 * 网络请求
 */
public class MaiServer {
	private static String TAG = MaiServer.class.getSimpleName();
	;
	
	private static boolean Debug = false;
	private MaiRequest maiRequest;   // 广告投放请求容器    get请求需要
	private MaiMonitor maiMonitor;   // 监控消息请求容器    post请求需要
	private Context mContext;
	
	//构造方法0
	public MaiServer() {
	}
	
	//构造方法1
	public MaiServer(MaiRequest _maiRequest) {
		maiRequest = _maiRequest;
	}
	
	//构造方法2
	public MaiServer(MaiMonitor _maiMonitor) {
		maiMonitor = _maiMonitor;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	
	/**
	 * 发送 post请求
	 * post请求要在设置为POST后加这两句? 必须??
	 * conn.setDoInput(true);
	 * conn.setDoOutput(true);
	 */
	public String postMonitor(String _url) {
		
//		String json = new Gson().toJson(maiMonitor);     //生成 请求的数据 json  要发送给服务器的

		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		HttpURLConnection httpConn = null;
		
		L.e(TAG, "postM: ");
		
		try {
			// TODO: 2016/11/20  gson-json
			String json = JsonFactory.toJson(maiMonitor);     //生成 请求的数据 json  要发送给服务器的
			L.e(TAG, json);

			URL urlObj = new URL(_url);
			httpConn = (HttpURLConnection) urlObj.openConnection();
			
			// 如果通过post方式给服务器传递数据，那么setDoOutput()必须设置为true。否则会异常。
			// 默认情况下setDoOutput()为false。
			// 其实也应该设置setDoInput()，但是因为setDoInput()默认为true。所以不一定要写。
			
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setRequestMethod("POST");
			httpConn.setConnectTimeout(2 * 1000);
			// 设置请求方式。请求方式有两种：POST/GET。注意要全大写。
			// POST传递数据量大，数据更安全，地址栏中不会显示传输数据。
			// 而GET会将传输的数据暴露在地址栏中，传输的数据量大小有限制，相对POST不够安全。但是GET操作灵活简便。
			
			// 判断是否要往服务器传递参数。如果不传递参数，那么就没有必要使用输出流了。
			
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("charset", "UTF-8");
			
			if (json != null) {
				L.e(TAG, "postM 2", json);
				byte[] data = json.getBytes();
				bos = new BufferedOutputStream(httpConn.getOutputStream());
				L.e(TAG, "postM 3 ", bos.toString());
				bos.write(data);
				bos.flush();
			}
			// 判断访问网络的连接状态
			if (httpConn.getResponseCode() == 200) {
				L.e(TAG, "postM 4 ", bos.toString());
				bis = new BufferedInputStream(httpConn.getInputStream());
				
				// 将获取到的输入流转成字节数组
				String s = new String(streamToByte(bis));
				L.e(TAG, "postM 5 ", s);       // s 无数据
				
				return s;
			}
		} catch (ProtocolException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			L.e("jsonfactory faild");
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
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
		return null;
	}
	
	
	/**
	 * 作用：get 实现网络访问  是否访问成功
	 *
	 * @param _url
	 * 	：访问网络的url地址
	 * @return byte[]
	 */
	public boolean postMonitorGet(String _url) {
		
		L.e(TAG, "postMonitorGet1: ");
		
		HttpURLConnection httpConn = null;
		BufferedInputStream bis = null;
		try {
			URL urlObj = new URL(_url);
			httpConn = (HttpURLConnection) urlObj.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(2000);
			httpConn.connect();
			
			L.e(TAG, "postMonitorGet2: " + httpConn.getResponseCode());
			
			if (httpConn.getResponseCode() == 200) {
				bis = new BufferedInputStream(httpConn.getInputStream());
				String s = new String(streamToByte(bis));
				L.e(TAG, "postMonitorGet: " + s);
				
				return true;
			}
			
		} catch (Exception e) {
			
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
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
		return false;
	}
	
	
	//    /**
	//     * String.valueOf  将基本数据类型转化成 string
	//     * 发送 get请求   传入 网址
	//     */
	//    public String get(String _url) {
	//
	//        String json = null;
	//        try {
	//
	//            maiRequest.m_ts = System.currentTimeMillis() / 1000;
	//
	//            json = new Gson().toJson(maiRequest);     //生成 请求的数据 json  要发送给服务器的
	//
	//        } catch (Exception e) {
	//            e.printStackTrace();
	//        }
	//
	//        Log.d(TAG, json);
	//
	//        //        String param = getParamForJson(json);
	//
	//
	//        String request = null;
	//        URL url = null;
	//        HttpURLConnection conn = null;
	//        InputStream in = null;
	//        OutputStream out = null;
	//
	//        byte[] data = json.getBytes();      // 把json请求数据变为 byte[]
	//
	//
	//        //        byte[] data =param.getBytes();
	//
	//        try {
	//
	//            //            Content-Type: application/json;charset=UTF-8
	//
	//            url = new URL(_url);
	//
	//
	//            Log.d(TAG, url.toString());
	//
	//            conn = (HttpURLConnection) url.openConnection();
	//
	//
	//            //设置连接属性
	//            conn.setDoOutput(true);// 使用 URL 连接进行输出
	//            conn.setDoInput(true);// 使用 URL 连接进行输入
	//            conn.setUseCaches(false);// 忽略缓存
	//
	//
	//
	//            //            conn.setConnectTimeout(30000);//设置连接超时时长，单位毫秒
	//            conn.setConnectTimeout(2000);//设置连接超时时长，单位毫秒
	//
	//            conn.setRequestMethod("GET");//设置请求方式，POST or GET，注意：如果请求地址为一个servlet地址的话必须设置成POST方式
	//
	//
	//            //设置请求头
	//            conn.setRequestProperty("Content-Type", "application/json");
	//            conn.setRequestProperty("charset", "UTF-8");
	//
	//
	//            if (data != null) {
	//                Log.d(TAG, "" + 10);
	//
	//                out = conn.getOutputStream();    //这里超时
	//                Log.d(TAG, "" + 111111111);        //不打印
	//
	//                out.write(data);   //把请求数据byte[] write   这一步是相当于给地址加请求参数吗????
	//
	//            }
	//
	//            int code = conn.getResponseCode();
	//
	//            Log.d(TAG, "" + 12 + code);      //不打印的
	//
	//            if (code == 200) {
	//                Log.d(TAG, "" + 13);
	//                in = conn.getInputStream();// 可能造成阻塞
	//                long len = conn.getContentLength();
	//                byte[] bs = new byte[(int) len];//返回结果字节数组
	//                int all = 0;
	//                int dn = 0;
	//                while ((dn = in.read(bs, all, 1)) > 0) {
	//                    all += dn;
	//
	//                    if (all == len) {
	//                        request = new String(bs);   //返回的结果
	//                        break;
	//                    }
	//
	//                }
	//                Log.d(TAG, "" + 14);
	//
	//            }
	//        } catch (ProtocolException e) {
	//            e.printStackTrace();
	//        } catch (MalformedURLException e) {
	//            e.printStackTrace();
	//        } catch (IOException e) {
	//            e.printStackTrace();
	//        }
	//
	//
	//        L.e(TAG, "get: " + request);
	//
	//
	//        //        return request;       // 将返回结果返回
	//
	//
	//        String pathFull = "\"http://d.hiphotos.baidu.com/zhidao/pic/item/ca1349540923dd54e5c27f09d309b3de9c82481a.jpg\"";
	//        String pathBanner = "\"http://pic48.nipic.com/file/20140919/19404114_112216356000_2.jpg\"";
	//        String pathHtml = "\"https://www.baidu.com\"";
	//        String pathH52 = "\"http://g.eqxiu.com/s/O9aom2xC?eqrcode=1\"";
	//        String pathH5 = "\"http://abcccc8b.h5.baomitu.com/app/3C1fYR3z.html?iframe=1\"";
	//        String pathVideo = "\"http://vf2.mtime.cn/Video/2015/10/12/mp4/151012161154047911.mp4\"";
	//
	//        // 测试数据 选择类型
	//        String typeTset = "\"" + (maiRequest.m_stype == MaiStype.Video_advertising ? MaiType.V : maiRequest.m_stype == MaiStype.H5_advertising ? MaiType.X : maiRequest.m_stype == MaiStype.Information_advertising ? MaiType.F : MaiType.I) + "\"";
	//        String pathTest = (maiRequest.m_stype == MaiStype.Banner_advertising ? pathBanner : maiRequest.m_stype == MaiStype.Video_advertising ? pathVideo : maiRequest.m_stype == MaiStype.H5_advertising ? pathH5 : maiRequest.m_stype == MaiStype.Information_advertising ? MaiType.F : pathFull);
	//
	//
	//            String myJson = "{\n" +
	//                                "\"pid\": \"100\",\n" +
	//                                "\"uuid\": \"6fcbe31dfa3a41ed\",\n" +
	//                                "\"bc\": \"C2FEEEAC3CFCD411D158B05600600806D9B6\",\n" +
	//                                "\"etype\": \"B\",\n" +
	//                                "\"type\": " + typeTset + ",\n" +
	//                                "\"src\":" + pathTest + ",\n" +
	//                                "\"adw\":" + maiRequest.m_adw + ",\n" +
	//                                "\"adh\":" + maiRequest.m_adh + ",\n" +
	//                                "\"pm\": {\"7\":[\"http://g.cn.xchmai.com/x.gif?783&bc=90_14030_1355798855_1&freq=1&k=1003043&p=3xxL60&rt=2&o =\"], \"15\":[\"http://xxx.com/...\", \"http://yyy.com/...\"]},\n" +
	//                                "\"cm\": [\"http://www.test1.com\",\"http://www.test2.com\"],\n" +
	//                                "\"ldp\": \"https://www.baidu.com\",\n" +
	//                                "\"meta\": {\"check\":1,duration\":15} }";
	//        //        L.e(TAG, myJson );
	//
	//        Log.d(TAG, "" + 15);
	//
	//        return myJson;
	//    }
	
	
	public String get33(String _url) {
		maiRequest.m_ts = System.currentTimeMillis() / 1000;
		L.e("request", maiRequest.m_net);
		String pathFull = "\"http://d.hiphotos.baidu.com/zhidao/pic/item/ca1349540923dd54e5c27f09d309b3de9c82481a.jpg\"";
		String pathBanner = "\"http://pic48.nipic.com/file/20140919/19404114_112216356000_2.jpg\"";
		String pathHtml = "\"https://www.baidu.com\"";
		String pathH52 = "\"http://g.eqxiu.com/s/O9aom2xC?eqrcode=1\"";
		String pathH5 = "\"http://abcccc8b.h5.baomitu.com/app/3C1fYR3z.html?iframe=1\"";
		String pathVideo = "\"http://vf2.mtime.cn/Video/2015/10/12/mp4/151012161154047911.mp4\"";
		
		// 测试数据 选择类型
		String typeTset = "\"" + (maiRequest.m_stype == MaiStype.Video_advertising ? MaiType.V : maiRequest.m_stype == MaiStype.H5_advertising ? MaiType.H : maiRequest.m_stype == MaiStype.Information_advertising ? MaiType.F : MaiType.I) + "\"";
		String pathTest = maiRequest.m_stype == MaiStype.Banner_advertising ? pathBanner : (String) (maiRequest.m_stype == MaiStype.Video_advertising ? pathVideo : maiRequest.m_stype == MaiStype.H5_advertising ? pathH5 : maiRequest.m_stype == MaiStype.Information_advertising ? MaiType.F : pathFull);
		//		
		return "{\n" +
		       "\"pid\" : \"990100100\",\n" +
		       " \"uuid\" : \"hhhhhhhhhhhhhhhhhhhhhhhhhhhh\",\n" +
		       " \"bc\" : \"51A576B071374BA1A4998718C8F827D2\",\n" +
		       "  \"etype\" : \"1\",\n" +
		       " \"type\" : 1,\n" +
		       "  \"src\" : \"http://d.hiphotos.baidu.com/zhidao/pic/item/ca1349540923dd54e5c27f09d309b3de9c82481a.jpg\",\n" +
		       "  \"adw\" : 320,\n" +
		       " \"adh\" : 50,\n" +
		       "\"ldp\" : \"http://pic48.nipic.com/file/20140919/19404114_112216356000_2.jpg\",\n" +
		       " \"cm\" : [ ],\n" +
		       "\"pm\" : [ ],\n" +
		       "\"adct\" : 1\n" +
		       " }";
		
		//		return "{\n" +
		//		       "\"pid\" : \"990100100\",\n" +
		//		       " \"uuid\" : \"\",\n" +
		//		       " \"bc\" : \"51A576B071374BA1A4998718C8F827D2\",\n" +
		//		       "  \"etype\" : \"1\",\n" +
		//		       " \"type\" : 1,\n" +
		//		       "  \"src\" : \"http://d.hiphotos.baidu.com/zhidao/pic/item/ca1349540923dd54e5c27f09d309b3de9c82481a.jpg\",\n" +
		//		       "  \"adw\" : 320,\n" +
		//		       " \"adh\" : 50,\n" +
		//		       "\"ldp\" : \"http://pic48.nipic.com/file/20140919/19404114_112216356000_2.jpg\",\n" +
		//		       " \"cm\" : [ ],\n" +
		//		       "\"pm\" : [ ]\n" +
		//		       " }";
		//		return "{\n" +
		//		       "\"pid\" : \"990100100\",\n" +
		//		       " \"uuid\" : \"\",\n" +
		//		       " \"bc\" : \"51A576B071374BA1A4998718C8F827D2\",\n" +
		//		       "  \"etype\" : \"1\",\n" +
		//		       " \"type\" : 4,\n" +
		//		       "  \"src\" : \"<meta   http-equiv='Content-Type' content='text/html;charset=UTF-8'/><style type='text/css'>*{padding:0px;margin:0px;}a:link{text-decoration:none;}</style><a href=\\\"https://www.baidu.com\\\"><img width=\\\"640\\\" height=\\\"1136\\\" src=\\\"http://pic2.ooopic.com/10/58/92/15b1OOOPICb9.jpg\\\"></a><img src=\\\"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png\\\" width=\\\"1\\\" height=\\\"1\\\" style=\\\"display:none;\\\" /><img src=\\\"http://nbb-event-bj.bigtree.mobi:13898/imp?aid=51A576B071374BA1A4998718C8F827D2&amp;price=F2A68689841455E53B9C21B737D7EAD2&amp;lite=CAASBmlwd2w1MBoRZ2xpc3BhNjU4Nzc5NzU5LTEiCjQ4Mjg4NjczMTkyAEAGSAJQgYCA-AdYgKMF\\\" width=\\\"1\\\" height=\\\"1\\\" style=\\\"display:none;\\\" />\",\n" +
		//		       "  \"adw\" : 320,\n" +
		//		       " \"adh\" : 50,\n" +
		//		       "\"ldp\" : \"null\",\n" +
		//		       " \"cm\" : [ ],\n" +
		//		       "\"pm\" : [ ]\n" +
		//		       " }";
		////		
		//		return "{\n" +
		//		       "\"pid\" : \"990100100\",\n" +
		//		       " \"uuid\" : \"\",\n" +
		//		       " \"bc\" : \"51A576B071374BA1A4998718C8F827D2\",\n" +
		//		       "  \"etype\" : \"1\",\n" +
		//		       " \"type\" : 4,\n" +
		//		       "  \"src\" : \"<meta http-equiv='Content-Type' content='text/html;charset=UTF-8'/><style type='text/css'>*{padding:0px;margin:0px;}a:link{text-decoration:none;}</style><a href=${MONITOR_CLICK}\\\"${MONITOR_CLICK}http://c.bigtree.mobi/c?aid=69AB46314FF846069AC3D99C918911C2&amp;cidfa=&amp;osv=&amp;lite=CAESBmlwd2wzMBoTbW9idmlzdGEyMTEwNTg1NTAxOSILNTIxMTM5OTA5NDYyAEAGSAJQxPjb3ghYgKMF&amp;p=uiUbKGtsQKClbEIg4xLFga\\\"><img width=\\\"320px\\\" height=\\\"50px\\\" src=\\\"http://7xrrit.com2.z0.glb.qiniucdn.com/id1105855019_640x100.jpg\\\"></a><img src=\\\"http://c.bigtree.mobi/imp?aid=69AB46314FF846069AC3D99C918911C2&amp;ip=139.214.252.68&amp;ap=E2B6F8F1DF4F9DEECABBABD9206F1AC2&amp;af=admai&amp;skid=03431c28b8702bd529b2e00d3c9c1f5a&amp;tid=57fdce4f0000&amp;lite=CAESBmlwd2wzMBoTbW9idmlzdGEyMTEwNTg1NTAxOSILNTIxMTM5OTA5NDYyAEAGSAJQxPjb3ghYgKMF&amp;p=0qpgUhel6J8rQzGgE6gaNw\\\" width=\\\"1\\\" height=\\\"1\\\" style=\\\"display:none;\\\" /><img src=\\\"http://nbb-event-bj.bigtree.mobi:13898/imp?aid=69AB46314FF846069AC3D99C918911C2&amp;price=E2B6F8F1DF4F9DEECABBABD9206F1AC2&amp;lite=CAESBmlwd2wzMBoTbW9idmlzdGEyMTEwNTg1NTAxOSILNTIxMTM5OTA5NDYyAEAGSAJQxPjb3ghYgKMF\\\" width=\\\"1\\\" height=\\\"1\\\" style=\\\"display:none;\\\" />\",\n" +
		//		       "  \"adw\" : 320,\n" +
		//		       " \"adh\" : 50,\n" +
		//		       "\"ldp\" : \"null\",\n" +
		//		       " \"cm\" : [ ],\n" +
		//		       "\"pm\" : [ ]\n" +
		//		       " }";
		//		
		
		//		return "{\n" +
		//		       "    \"pid\": \"333399999\",\n" +
		//		       "    \"uuid\": \"861317037582406\",\n" +
		//		       "    \"bc\": \"EE5046F213AE4438B7C5C2871E0D7373\",\n" +
		//		       "    \"etype\": \"0\",\n" +
		//		       "    \"type\": \"4\",\n" +
		//		       "    \"src\": \"<iframe src=\\\"https://adp.xibao100.com/ads?actualSize=640x100&adz=333399999&cids=50006865+122996002+122952001&cs=2&devt=3&ext=&mids=0%3A861317037582406&referer=http%3A%2F%2Ftest-adx.admai.com%2Fmar&reqid=48F4E190726A42B9A36994BF2048D251&size=640x100&source=17&sp=AAAAAFf8oXcAAAAAAAAAAL7CmlgL9XYJInauVg%3D%3D&uid=961762&url=${MONITOR_CLICK}\\\" width=640 height=100 frameborder=0 marginheight=0 marginwidth=0 scrolling=no></iframe><img src=\\\"https://tracker.xibao100.com/impl?adz=333399999&af=0&cs=2&devt=3&proto=8dda4ab50b52fe924953cd802f33cdf5e2676ee05b7d8209d8c55877e82e2584&referer=http%3A%2F%2Ftest-adx.admai.com%2Fmar&s=17&ts=1476174199&url=https%3A%2F%2Fadscdn-ssl.xibao100.com%2F_.gif\\\" width=0 height=0  style=\\\"position:absolute;left:-10000px\\\" /> \",\n" +
		//		       "    \"adw\": 640,\n" +
		//		       "    \"adh\": 1136,\n" +
		//		       "    \"ldp\": \"null\",\n" +
		//		       "    \"cm\": [],\n" +
		//		       "    \"pm\": [],\n" +
		//		       "    \"ecode\": \"<script src=\\\"http://s11.cnzz.com/z_stat.php?id=1260488047&web_id=1260488047\\\" language=\\\"JavaScript\\\"></script>\"\n" +
		//		       "}";
		//		
		//		return "{\n" +
		//		       "    \"pid\": \"333399999\",\n" +
		//		       "    \"uuid\": \"861317037582406\",\n" +
		//		       "    \"bc\": \"EE5046F213AE4438B7C5C2871E0D7373\",\n" +
		//		       "    \"etype\": \"0\",\n" +
		//		       "    \"type\": \"4\",\n" +
		//		       "    \"src\": \"<meta http-equiv='Content-Type' content='text/html;charset=UTF-8'/><style type='text/css'>*{padding:0px;margin:0px;}a:link{text-decoration:none;}</style><a href=\\\"${MONITOR_CLICK}http%3A%2F%2Fc.bigtree.mobi%2Fc%3Faid%3DEE5046F213AE4438B7C5C2871E0D7373%26cidfa%3D%26osv%3D%26lite%3DCAASBmlwd2w1MBoPZ2xpc3BhNjU4Nzc5NzU5Igo0ODI4ODY3MzE5MgBABkgCUIGAgPgHWICjBQ%26p%3DMluiJOnW60DOdmR8ojQj2a\\\"><img width=\\\"320px\\\" height=\\\"50px\\\" src=\\\"http://7xrrit.com2.z0.glb.qiniucdn.com/id658779759_640x100.jpg\\\"></a><img src=\\\"http://c.bigtree.mobi/imp?aid=EE5046F213AE4438B7C5C2871E0D7373&amp;ip=127.0.0.1&amp;ap=F2A68689841455E53B9C21B737D7EAD2&amp;af=admai&amp;skid=f3e354546b1f81d369cb145fd7c01bb4&amp;tid=57fde6b30001&amp;lite=CAASBmlwd2w1MBoPZ2xpc3BhNjU4Nzc5NzU5Igo0ODI4ODY3MzE5MgBABkgCUIGAgPgHWICjBQ&amp;p=y2hh6.rMxESBQfeKHrdruL\\\" width=\\\"1\\\" height=\\\"1\\\" style=\\\"display:none;\\\" /><img src=\\\"http://nbb-event-bj.bigtree.mobi:13898/imp?aid=EE5046F213AE4438B7C5C2871E0D7373&amp;price=F2A68689841455E53B9C21B737D7EAD2&amp;lite=CAASBmlwd2w1MBoPZ2xpc3BhNjU4Nzc5NzU5Igo0ODI4ODY3MzE5MgBABkgCUIGAgPgHWICjBQ\\\" width=\\\"1\\\" height=\\\"1\\\" style=\\\"display:none;\\\" /> \",\n" +
		//		       "    \"adw\": 640,\n" +
		//		       "    \"adh\": 1136,\n" +
		//		       "    \"ldp\": \"null\",\n" +
		//		       "    \"cm\": [],\n" +
		//		       "    \"pm\": [],\n" +
		//		       "    \"ecode\": \"<script src=\\\"http://s11.cnzz.com/z_stat.php?id=1260488047&web_id=1260488047\\\" language=\\\"JavaScript\\\"></script>\"\n" +
		//		       "}";
	}
	
	/**
	 * post 请求 带请求参数
	 *
	 * @param _url
	 * @return
	 */
	public String get(String _url) {
		
		maiRequest.m_ts = System.currentTimeMillis();
		L.e(TAG, maiRequest.m_ts + "");
//		String json = new Gson().toJson(maiRequest);//生成 请求的数据 json  要发送给服务器的

		L.e("maiReuest", maiRequest.toString());

		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		HttpURLConnection httpConn = null;
		
		L.e(TAG, "doPostSubmit: ");
		int responsCode = -99999999;
		try {
			// TODO: 2016/11/20  gson-json
			String json = JsonFactory.toJson(maiRequest);//生成 请求的数据 json  要发送给服务器的

			L.e(TAG+"发送给服务器的json:\n", json);

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
				L.e(TAG, "doPostSubmit: 2" + json);
				byte[] data = json.getBytes();
				L.e(TAG, "doPostSubmit: 21" + json);
				
				OutputStream outputStream = httpConn.getOutputStream();
				L.e(TAG, "doPostSubmit: 22 outputStream" + outputStream.toString());
				
				bos = new BufferedOutputStream(outputStream);
				L.e(TAG, "doPostSubmit: 23" + json);
				
				bos.write(data);
				L.e(TAG, "doPostSubmit: 24" + json);
				
				bos.flush();
				L.e(TAG, "doPostSubmit:3 " + bos.toString() + "code" + httpConn.getResponseCode());
				L.e(TAG, "doPostSubmit:34 " + json);
				responsCode = httpConn.getResponseCode();
			}
			
			// 判断访问网络的连接状态
			int responseCode1 = httpConn.getResponseCode();
			if (responseCode1 == 200) {
				L.e(TAG, "doPostSubmit:4 ");
				bis = new BufferedInputStream(httpConn.getInputStream());
				// 将获取到的输入流转成字节数组
				byte[] data = streamToByte(bis);
				
				String s = null;
				if (data != null) {
					s = new String(data);
				} else {
					LogSSUtil.getInstance()
					         .saveLogs(MaiLType.REQUEST_F, TAG + " catch streamToByte error: ", 0, "none");
				}
				L.e(TAG, "doPostSubmit:5 " + s);
				
				return s;
			}
			
			
		} catch (Exception e) {
			LogUtil.E("AD Request", "Error !");
			LogSSUtil.getInstance()
			         .saveLogs(MaiLType.REQUEST_F, TAG + " get:catch error:__ " + e.toString(), responsCode, "none");
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
			
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
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
		
		if (responsCode == -99999999) {
			LogUtil.E("AD Request Error!", "Check Internet");
			LogSSUtil.getInstance()
			         .saveLogs(MaiLType.REQUEST_F, TAG + " Request : Time Out , No ResponsCode", responsCode, "none");
		} else {
			LogUtil.E("AD Request", "Error !");
			L.e(" AD Request Error ! ", "ResponsCode:" + responsCode);
			LogSSUtil.getInstance()
			         .saveLogs(MaiLType.REQUEST_F, TAG + " Request Error ", responsCode, "none");
		}
		
		return null;
		
		//		        return "{\n" +
		//		                   "\"pid\" : \"990100100\",\n" +
		//		                   " \"uuid\" : \"\",\n" +
		//		                   " \"bc\" : \"51A576B071374BA1A4998718C8F827D2\",\n" +
		//		                   "  \"etype\" : \"1\",\n" +
		//		                   " \"type\" : 4,\n" +
		//		                   "  \"src\" : \"<meta   http-equiv='Content-Type' content='text/html;charset=UTF-8'/><style type='text/css'>*{padding:0px;margin:0px;}a:link{text-decoration:none;}</style><a href=\\\"https://www.baidu.com\\\"><img width=\\\"640\\\" height=\\\"1136\\\" src=\\\"http://pic2.ooopic.com/10/58/92/15b1OOOPICb9.jpg\\\"></a><img src=\\\"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png\\\" width=\\\"1\\\" height=\\\"1\\\" style=\\\"display:none;\\\" /><img src=\\\"http://nbb-event-bj.bigtree.mobi:13898/imp?aid=51A576B071374BA1A4998718C8F827D2&amp;price=F2A68689841455E53B9C21B737D7EAD2&amp;lite=CAASBmlwd2w1MBoRZ2xpc3BhNjU4Nzc5NzU5LTEiCjQ4Mjg4NjczMTkyAEAGSAJQgYCA-AdYgKMF\\\" width=\\\"1\\\" height=\\\"1\\\" style=\\\"display:none;\\\" />\",\n" +
		//		                   "  \"adw\" : 320,\n" +
		//		                   " \"adh\" : 50,\n" +
		//		                   "\"ldp\" : \"null\",\n" +
		//		                   " \"cm\" : [ ],\n" +
		//		                   "\"pm\" : [ ]\n" +
		//		                   " }";
		
		//        return "{\n" +
		//               "\"pid\" : \"990100100\",\n" +
		//               " \"uuid\" : \"\",\n" +
		//               " \"bc\" : \"51A576B071374BA1A4998718C8F827D2\",\n" +
		//               "  \"etype\" : \"1\",\n" +
		//               " \"type\" : 1,\n" +
		//               "  \"src\" : \"http://d.hiphotos.baidu.com/zhidao/pic/item/ca1349540923dd54e5c27f09d309b3de9c82481a.jpg\",\n" +
		//               "  \"adw\" : 320,\n" +
		//               " \"adh\" : 50,\n" +
		//               "\"ldp\" : \"null\",\n" +
		//               " \"cm\" : [ ],\n" +
		//               "\"pm\" : [ ]\n" +
		//               " }";
		
		//		        return "{\n" +
		//		               "    \"pid\": \"333399999\",\n" +
		//		               "    \"uuid\": \"861317037582406\",\n" +
		//		               "    \"bc\": \"EE5046F213AE4438B7C5C2871E0D7373\",\n" +
		//		               "    \"etype\": \"0\",\n" +
		//		               "    \"type\": \"4\",\n" +
		//		               "    \"src\": \"<iframe src=\\\"https://adp.xibao100.com/ads?actualSize=640x100&adz=333399999&cids=50006865+122996002+122952001&cs=2&devt=3&ext=&mids=0%3A861317037582406&referer=http%3A%2F%2Ftest-adx.admai.com%2Fmar&reqid=48F4E190726A42B9A36994BF2048D251&size=640x100&source=17&sp=AAAAAFf8oXcAAAAAAAAAAL7CmlgL9XYJInauVg%3D%3D&uid=961762&url=${MONITOR_CLICK}\\\" width=640 height=100 frameborder=0 marginheight=0 marginwidth=0 scrolling=no></iframe><img src=\\\"https://tracker.xibao100.com/impl?adz=333399999&af=0&cs=2&devt=3&proto=8dda4ab50b52fe924953cd802f33cdf5e2676ee05b7d8209d8c55877e82e2584&referer=http%3A%2F%2Ftest-adx.admai.com%2Fmar&s=17&ts=1476174199&url=https%3A%2F%2Fadscdn-ssl.xibao100.com%2F_.gif\\\" width=0 height=0  style=\\\"position:absolute;left:-10000px\\\" /> \",\n" +
		//		               "    \"adw\": 640,\n" +
		//		               "    \"adh\": 1136,\n" +
		//		               "    \"ldp\": \"null\",\n" +
		//		               "    \"cm\": [],\n" +
		//		               "    \"pm\": [],\n" +
		//		               "    \"ecode\": \"<script src=\\\"http://s11.cnzz.com/z_stat.php?id=1260488047&web_id=1260488047\\\" language=\\\"JavaScript\\\"></script>\"\n" +
		//		               "}";
		
	}
	
	
	public static byte[] streamToByte(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c = 0;
		byte[] buffer = new byte[8 * 1024];
		try {
			while ((c = is.read(buffer)) != -1) {
				baos.write(buffer, 0, c);
				baos.flush();
			}
			return baos.toByteArray();
		} catch (IOException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
			
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				if (LogUtil.isShowError()) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	/*
	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
	 * 这里的path是图片的地址
	 */
	public static Uri getImageURI(String path, File cache) throws Exception {
		
		String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
		
		File file = new File(cache, name);
		// 如果图片存在本地缓存目录，则不去服务器下载
		if (file.exists()) {
			
			Uri uri = Uri.fromFile(file);
			return uri;//Uri.fromFile(path)这个方法能得到文件的URI
			
		} else {
			// 从网络上获取图片
			int responseCode;
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
				// 返回一个URI对象  用来判断是否有缓存的图片
				return Uri.fromFile(file);
			} else {
				LogSSUtil.getInstance()
				         .saveLogs(MaiLType.MTR_DOWN_F, " Download Img Error; ", responseCode, "none");
				return null;
			}
		}
	}/*
	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
	 * 这里的path是图片的地址
	 */
	
	public static Bitmap getImageBmp(Context context, String path, File cache, boolean isJustDownload, boolean hasPermission) throws Exception {
		
		String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
		if (hasPermission) {
			File file = new File(cache, name);
			// 如果图片存在本地缓存目录，则不去服务器下载
			if (file.exists()) {
				
				Uri uri = Uri.fromFile(file); //Uri.fromFile(path)这个方法能得到文件的URI
				
				return Media.getBitmap(context.getContentResolver(), uri);
				
			}else {
				// 从网络上获取图片
				return downloadBitmap(path, cache, isJustDownload);
			}
		}else{
			return downloadBitmap(path, cache, true);
		} 
	}
	
	
	public static Bitmap downloadBitmap(String path, File cache, boolean isJustDownload) throws Exception {
		
		String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
		//		File file = new File(cache, name);
		Bitmap bmp = null;
		// 从网络上获取图片
		int responseCode;
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			InputStream is = conn.getInputStream();
			//			bmp = BitmapFactory.decodeStream(is)
			byte[] data = MaiUtils.streamToByte(is);
			bmp = MaiUtils.createThumbnail(data);
			//			
			if (!isJustDownload) {
				if (bmp != null) {
					L.e("lssBitmapsize", MaiUtils.getBitmapSize(bmp) + "--size:" + MaiUtils.getSDCardAvailableSize());
					if (MaiUtils.getSDCardAvailableSize() >= 1) {
						//缓存
						MaiUtils.saveBitmapToSDCard(bmp, name, cache);
						
					} else {
						LogSSUtil.getInstance()
						         .saveLogs(MaiLType.MTR_DOWN_F, " SD Card No Space To Save Img ", responseCode, "none");
					}
				} else {
					LogSSUtil.getInstance().saveLogs(MaiLType.MTR_DOWN_F, " save Img Error; No Bitmap ", responseCode, "none");
				}
			}
			return bmp;
		} else {
			LogSSUtil.getInstance()
			         .saveLogs(MaiLType.MTR_DOWN_F, " Download Img Error; ", responseCode, "none");
			
		}
		return null;
	}
	
	
	public static Uri getImageURIifExists(String path, File cache) throws Exception {
		String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
		File file = new File(cache, name);
		// 如果图片存在本地缓存目录，则不去服务器下载
		if (file.exists()) {
			return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
		}
		return null;
	}
	
	
	public String postMonitor2() {   //扔要在runnable中运行
		return MaiReportManager.getInstance(mContext).monit(maiMonitor);
	}
	
	
	public boolean postMonitorGet2(String _url) {   //扔要在runnable中运行
		MaiTrack maiTrack = new MaiTrack();
		maiTrack.urlTrack = _url;
		maiTrack.tsTrack = System.currentTimeMillis();   //发送检测时间
		return MaiReportManager.getInstance(mContext).track(maiTrack);
	}
}
