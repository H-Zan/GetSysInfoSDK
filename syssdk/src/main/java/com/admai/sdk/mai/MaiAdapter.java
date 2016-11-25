package com.admai.sdk.mai;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.admai.sdk.str.MaiAd;
import com.admai.sdk.str.MaiPath;
import com.admai.sdk.str.MaiRequest;
import com.admai.sdk.type.MaiLType;
import com.admai.sdk.type.MaiStype;
import com.admai.sdk.type.MaiType;
import com.admai.sdk.util.MaiUtils;
import com.admai.sdk.util.PermissionUtil;
import com.admai.sdk.util.log.L;
import com.admai.sdk.util.log.LogSSUtil;
import com.admai.sdk.util.log.LogUtil;
import com.admai.sdk.view.MaiInformationView;
import com.admai.sdk.view.widegt.MaiTimeButton;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by macmi001 on 16/7/4.
 * 广告适配器      
 */

public class MaiAdapter extends RelativeLayout implements View.OnClickListener {
	private static final int CLOSE_BY_USER = 21;
	private static final int CLOSE_BY_SELF = 22;
	private static final int CLOSE_BY_ERROR = 23;
	//	private static String TAG = MaiAdapter.class.getSimpleName();
	private static String TAG = "MaiAdapter";

	protected static final int SUCCESS_CREAT_IMAGE = 0;
	protected static final int SUCCESS_GET_IMAGE = 1;
	protected static final int SUCCESS_CLICK = 2;
	protected static final int SUCCESS_CLICK_html = 8;
	protected static final int SUCCESS_CLICK_MTR = 9;

	protected static final int SUCCESS_CREAT_INFO = 3;
	protected static final int SUCCESS_GET_INFOIMAGE = 4;

	protected static final int SUCCESS_CREAT_INFOVIEW = 5;
	protected static final int SUCCESS_CREATE_VIDEO = 6;
	protected static final int SUCCESS_GET_VIDEO = 7;


	protected static final int RECEIVE_REQUEST = 11;
	protected static final int RECEIVE_AD = 12;
	protected static final int FAILED_TO_RECEIVE_AD = 13;

	public int SUCCESS_SHOW;  // ?成功展示?  发送曝光的时候
	public int LANDINGPAGE;   // ?降落?  url成功被打开展示
	public int USER_CLOSED;   // ?降落?  url成功被打开展示
	public int SELF_CLOSED;   // ?降落?  url成功被打开展示

	private MaiListener maiListener;  //广告事件监听器

	private MaiAd maiAd;  //广告响应容器   bean  服务器返回的广告数据

	private MaiController maiController; //请求控制器

	private File cache;  //缓存
	private Context context;

	private MaiRequest maiRequest; //广告投放请求容器 请求广告时的参数
	private ImageView imageView;
	private WebView webView;

	private MaiInformationView relativeLayout;    //

	private long startTime;

	MyHandle mHandler = new MyHandle(this);
//	private MaiVideoView mMaiVideoView;

	private boolean isFullScreen = false;
	private int currentPosition;
	boolean successShow = false;
	private ProgressBar progressBar;
	private String adType = "noType";
	private Class<?> mTargetActivity;
	private Intent mIntent;

	private MaiCloseTypeListener mMaiCloseTypeListener;
	private String webviewContentWidth;
	private boolean isScale = false;


	public void setMaiCloseTypeListener(MaiCloseTypeListener maiCloseTypeListener) {
		mMaiCloseTypeListener = maiCloseTypeListener;
	}

	public void setAdType(String adType) {
		this.adType = adType;
	}

	public void setTargetActivity(Class<?> targetActivity) {
		if (targetActivity != null) {
			mTargetActivity = targetActivity;

		}
	}

	public void setIntent(Intent intent) {
		if (intent != null) {
			mIntent = intent;
		}
	}


	class MyHandle extends Handler {
		private WeakReference<MaiAdapter> maiAdapter;

		public MyHandle(MaiAdapter _maiAdapter) {
			maiAdapter = new WeakReference<MaiAdapter>(_maiAdapter);
		}


		public void handleMessage(Message msg) {

			/////// 把listeners回调到主线程
			if (msg.what == RECEIVE_REQUEST) {

				//                maiListener.onReceiveRequest(new Gson().toJson(maiRequest));  // 已发送广告投放请求

			}

			if (msg.what == RECEIVE_AD) {
				//                    maiListener.onReceiveAd(new Gson().toJson(maiAd));   // 已接收广告投放响应    接口回调第二步 接收数据
				maiListener.onReceiveAd("Recieve AD Success!");   // 已接收广告投放响应    接口回调第二步 接收数据
			}

			if (msg.what == FAILED_TO_RECEIVE_AD) {

				String error = (String) msg.obj;
				maiListener.onFailedToReceiveAd(error);   //失败的回调
				if (mMaiCloseTypeListener != null) {
					mMaiCloseTypeListener.closeByError();
				}
			}

			if (msg.what == CLOSE_BY_USER) {
				int adType = (int) msg.obj;
				startTime = System.currentTimeMillis(); //开始的时间  是系统的当前时间
				if (mMaiCloseTypeListener != null) {
					mMaiCloseTypeListener.closeByUser(adType);
				}
				if (maiController != null) {
					maiController.sendExposureMonitor(startTime,USER_CLOSED);
					L.e("adapter : USER_CLOSED : ",USER_CLOSED);
				}

				L.e(TAG, "close by user");
			}

			if (msg.what == CLOSE_BY_SELF) {
				if (mMaiCloseTypeListener != null) {
					mMaiCloseTypeListener.closeBySelf();
				}
			}


			////////////////////////////////////////////

			if (msg.what == SUCCESS_CREAT_INFO) {  //成功创建富媒体view
//				MaiAdapter.this.addView(relativeLayout);
			}


			if (msg.what == SUCCESS_CREAT_IMAGE) {  //成功创建imageview
				LogUtil.D("Create ImgView", "Success !");
				MaiAdapter.this.addView(imageView);
			}

			//================= 成功得到image ==========================================
			//成功得到image
			if (msg.what == SUCCESS_GET_IMAGE) {
				//				Uri uri = (Uri) msg.obj;
				Bitmap bmp = (Bitmap) msg.obj;
				L.e("admai_log", bmp);
				if (imageView != null && bmp != null) {
					//					Bitmap bmp = null;
					try {
						L.e(TAG, "SUCCESS_GET_IMAGE: " + bmp);
						//						bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

						// bmp = Bitmap.createScaledBitmap(bmp, 720, 130, true); // TODO: 16/8/19 重新设置bitmap大小

					} catch (Exception e) {
						LogSSUtil.getInstance()
						         .saveLogs(MaiLType.MTR_SHOW_F, e.toString(), 0, "none");
						if (LogUtil.isShowError()) {
							e.printStackTrace();
						}
					}
					// LayoutParams layoutParams = new LayoutParams(maiRequest.m_adw,maiRequest.m_adh);

					/**
					 *服务器返回的 adw
					 */
					//                    int adw = maiAd.adw;   // adw = -1: match parent   banner
					//                    int adh = maiAd.adh;   //130                       banner


					int adw = maiRequest.m_adw;   // adw = -1: match parent   banner
					int adh = maiRequest.m_adh;

					//					LayoutParams layoutParams = new LayoutParams(adw, adh);
					LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					//                    LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.addRule(CENTER_IN_PARENT);//中
					imageView.setLayoutParams(layoutParams);

					MaiAdapter.this.setLayoutParams(layoutParams);
					//                    imageView.setImageURI(uri);
					imageView.setImageBitmap(bmp);
					// TODO: 16/8/19    给MaiAdapter设置了颜色测试
					imageView.setBackgroundColor(Color.RED);
					MaiAdapter.this.setBackgroundColor(Color.YELLOW);
					LogUtil.D("Show Pic", "Success !");

					createCloseBtn();
					createAdmai();

					startTime = System.currentTimeMillis(); //开始的时间  是系统的当前时间
					//                    maiController.sendMonitor(startTime,"1");

					//                    maiController.sendMonitoropm(startTime, SUCCESS_SHOW);    // TODO: 16/8/29  注释的曝光

					maiController.sendExposureMonitor(startTime, SUCCESS_SHOW);

					if (maiAd.ldp != null) {
						if (imageView != null) {
							//给imageView设置监听
							imageView.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									Message msg = new Message();
									//                        msg.what = SUCCESS_CLICK;  //成功点击
									msg.what = SUCCESS_CLICK_MTR;  //成功点击
									msg.obj = maiAd.ldp;
									mHandler.sendMessage(msg);
									if (null != maiTimeButton) {
										maiTimeButton.destroy();
										MaiAdapter.this.removeView(maiTimeButton);
									}
									if (null != button_close) {
										MaiAdapter.this.removeView(button_close);
									}
									if (null != imageButton_close) {
										MaiAdapter.this.removeView(imageButton_close);
									}
									removeAdmai();
								}
							});
						}
					}

				} else {
					LogSSUtil.getInstance()
					         .saveLogs(MaiLType.MTR_SHOW_F, "can't get image uri, can't read SD card, or no bitmap", 0, "none");
				}
			}

			//================= 成功点击图片 landing page ===================================
			//成功点击图片 landing page
			if (msg.what == SUCCESS_CLICK) {

				// TODO: 16/8/18   L
				L.e(TAG, imageView.getWidth() + "--img--" + imageView.getHeight() + "--adapter---" + MaiAdapter.this
					                                                                                     .getWidth() + "----" + MaiAdapter.this
						                                                                                                            .getHeight());

				maiController.sendMonitor(startTime, LANDINGPAGE); // maiController 中的 点击监测 和 第三方点击监测   // TODO: 16/8/29  曝光 点击

				if (maiRequest.m_stype == MaiStype.Opening_advertising) {
					startActivity();
				}

				LogUtil.D("Pic", " Clicked");
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(maiAd.ldp);
				intent.setData(content_url);
				context.startActivity(intent);

			}

			//            if (msg.what == SUCCESS_CLICK_html) {
			if (msg.what == SUCCESS_CLICK_MTR) {
				LogUtil.D("Html", "Clicked");
				if (maiListener != null) {
					maiListener.onClick();
				}
				if (mMaiCloseTypeListener != null) {
					mMaiCloseTypeListener.closeByClick();  //Y
				}
				startTime = System.currentTimeMillis();
				maiController.sendMonitor(startTime, LANDINGPAGE); // maiController 中的 点击监测 和 第三方点击监测   // TODO: 16/8/29  曝光 点击
				if (maiRequest.m_stype == MaiStype.Opening_advertising) {
					startActivity();
				}

				L.e(TAG, "click img: ");

				String url = (String) msg.obj;
				//                Intent intent = new Intent();
				//                intent.setAction(Intent.ACTION_VIEW);
				//                intent.setData(Uri.parse(url));
				//               context.startActivity(intent);
//				WebViewUtils.openPageInSDK(context, url, "");
				String adct = maiAd.adct;
				L.e("adapter--adct",adct);
			}


			//====================== html物料 =====================================
			//h5
			if (msg.what == SUCCESS_CREAT_INFOVIEW) {
				LogUtil.D("Get Html Data", "Success !");
				if (webView == null) {
					webView = new WebView(context);
					if (Build.VERSION.SDK_INT >= 17) {
						webView.setId(View.generateViewId());
					} else {
						webView.setId(generateViewId());
					}
				}
				//                progressBar = new ProgressBar(context);
				L.e(TAG, "==h5==1");
				String maiHtml = maiAd.src.replace("\\\"", "\"");

				if (maiAd.src.contains("${MONITOR_CLICK}")) {
					maiHtml = maiAd.src.replace("${MONITOR_CLICK}", "");
				}

				LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					layoutParams = new LayoutParams(maiRequest.m_adw, maiRequest.m_adh);   //api 14 这个。。。
				}

				webView.setLayoutParams(layoutParams);
				int adw = maiRequest.m_adw;   // adw = -1: match parent   banner
				int adh = maiRequest.m_adh;

				LayoutParams layoutParamsA = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				//                    LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				//                    LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				layoutParamsA.addRule(CENTER_IN_PARENT);
				MaiAdapter.this.setLayoutParams(layoutParamsA);
				// WebSettings 抽象类
				WebSettings webSettings = webView.getSettings();

				webSettings.setJavaScriptEnabled(true);  //启用Js支持
				//
				//                webSettings.setSupportZoom(true);
				//
				//				webSettings.setUseWideViewPort(true);//

				webSettings.setLoadWithOverviewMode(true);


				//js可以弹出窗体 对话框
				webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

				if (isScale) {
					setMediaScale();
					webView.setInitialScale(scale);
				} else {
					maiHtml = getHtmlData(maiHtml);
					//                  maiHtml = HtmlProcessor.processRawHtml(maiHtml,maiRequest.m_adh);
				}

				L.e("maihtml", maiHtml);
				webView.setBackgroundColor(Color.TRANSPARENT);
				//              webView.setBackgroundColor(Color.BLUE);
				webView.setScrollContainer(false);
				webView.setVerticalScrollBarEnabled(false);
				webView.setHorizontalScrollBarEnabled(false);
				webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
				webView.setFocusableInTouchMode(false);
				//                webView.setWebChromeClient(new WebChromeClient(){
				//                @Override
				//                public void onProgressChanged(WebView view, int newProgress) {
				//
				//                    if (newProgress == 100) {
				//                        progressBar.setVisibility(View.INVISIBLE);
				//                        //界面展示完成时回调   发送监测
				//                        maiController.sendExposureMonitor(startTime, SUCCESS_SHOW);
				//                        //                          maiController.sendMonitoropm(startTime, SUCCESS_SHOW);  // 发送曝光 opm
				//                        successShow=true;
				//                        L.e(TAG, "SUCCESS_SHOW: "+SUCCESS_SHOW );
				//
				//                    } else {
				//                        if (View.INVISIBLE == progressBar.getVisibility()) {
				//                            progressBar.setVisibility(View.VISIBLE);
				//                        }
				//                        progressBar.setProgress(newProgress);
				//                    }
				//                    super.onProgressChanged(view, newProgress);
				//                }
				//
				//            });
				webView.setWebViewClient(new WebViewClient() {

					@Override
					public void onPageStarted(WebView view, String url, Bitmap favicon) {
						super.onPageStarted(view, url, favicon);
						L.e(TAG, "onPageStarted");
					}

					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						//                        view.loadUrl(url);
						//如果返回值 是 true  使用自身的浏览器内核   如果使用false  启用其他的浏览器打开数据
						//                        return super.shouldOverrideUrlLoading(view, url);
						L.e(TAG, "handler");
						L.e(TAG, "success click html");
						if (null != maiTimeButton) {
							maiTimeButton.destroy();
							MaiAdapter.this.removeView(maiTimeButton);
						}
						if (null != imageButton_close) {
							MaiAdapter.this.removeView(imageButton_close);
						}

						if (maiRequest.m_stype != MaiStype.Banner_advertising) {
							if (null != button_close) {
								MaiAdapter.this.removeView(button_close);
							}
							removeAdmai();
							ViewGroup vp = (ViewGroup) view.getParent();
							if (vp != null) {
								vp.removeView(view);
							}
							view.destroy();
							MaiAdapter.this.setVisibility(GONE);

						}
						Message msg = Message.obtain();
						if (msg == null) {
							msg = new Message();
						}
						//                        msg.what = SUCCESS_CLICK_html;  //成功点击
						msg.what = SUCCESS_CLICK_MTR;  //成功点击
						msg.obj = url;
						mHandler.sendMessage(msg);
						return true;
					}
					//

					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						L.e(TAG, "==onPageFinished==");
						createCloseBtn();
						createAdmai();
						//                        界面展示完成时回调   发送监测
						startTime = System.currentTimeMillis();
						maiController.sendExposureMonitor(startTime, SUCCESS_SHOW);
						successShow = true;
						//                      maiController.sendMonitoropm(startTime, SUCCESS_SHOW);  // 发送曝光 opm        // TODO: 16/8/29 注释的曝光

						L.e(TAG, "SUCCESS_SHOW: " + SUCCESS_SHOW);
						LogUtil.D("Html Data", "Success Show !");

					}
				});


				//                // TODO: 16/8/23   h5 自动播放
				//                webView.setWebViewClient(new WebViewClient() {
				//                    /**
				//                     * 当前网页的链接仍在webView中跳转
				//                     */
				//
				////                    @Override
				////                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
				////                        view.loadUrl(url);
				////                        //如果返回值 是 true  使用自身的浏览器内核   如果使用false  启用其他的浏览器打开数据
				////                        return true;
				////                    }
				//
				//                    /**
				//                     * 页面载入完成回调
				//                     */
				//                    @Override
				//                    public void onPageFinished(WebView view, String url) {
				//                        super.onPageFinished(view, url);
				//
				//                        L.e(TAG, "==onPageFinished==");
				//
				//
				//                        //可能视频
				//                        //                        view.loadUrl("javascript:(function() { " +
				//                        //                                         "var videos = document.getElementsByTagName('video');" +
				//                        //                                         " for(var i=0;i<videos.length;i++){videos[i].play();}})()");
				//
				//                        //让H5页面自动播放音乐   必须在此回调中使用
				////                        view.loadUrl("javascript:(function() { " +
				////                                         "var videos = document.getElementsByTagName('audio');" +
				////                                         " for(var i=0;i<videos.length;i++){videos[i].play();}})()");
				//
				//
				//                        L.e(TAG, "==h5==4");
				//
				//                        //界面展示完成时回调   发送监测
				//                        maiController.sendExposureMonitor(startTime, SUCCESS_SHOW);
				//                        successShow=true;
				//                        //                maiController.sendMonitoropm(startTime, SUCCESS_SHOW);  // 发送曝光 opm        // TODO: 16/8/29  注释的曝光
				//
				//                        L.e(TAG, "SUCCESS_SHOW: "+SUCCESS_SHOW );
				//                    }
				//
				//                });


				MaiAdapter.this.addView(webView);       //忘记添加  不行


				L.e(TAG, "handleMessage: " + maiHtml);
				//                                webView.loadUrl(maiAd.src);

				webView.loadDataWithBaseURL(null, maiHtml, "text/html", "utf-8", null);
				//                                webView.loadUrl("https://www.baidu.com");

				//                webView.loadUrl("https://www.baidu.com");
				//                webView.loadUrl("http://abcccc8b.h5.baomitu.com/app/3C1fYR3z.html?iframe=1");
				//                webView.loadUrl("http://g.eqxiu.com/s/O9aom2xC?eqrcode=1");

				//                createCloseBtn();


				//				MaiAdapter.this.setFocusable(true);
				//				MaiAdapter.this.setFocusableInTouchMode(false);
				//				MaiAdapter.this.setOnClickListener(new OnClickListener() {
				//					@Override
				//					public void onClick(View view) {
				//						L.e(TAG,"MaiAdapter click");
				//					}
				//				});
				//				L.e(TAG, "==h5==2" + maiAd.src);

			}


			//===================== 视频 ======================================
			// 视频
//			if (msg.what == SUCCESS_CREATE_VIDEO)
//
//			{  //视频
//
//
//				int adw = maiAd.adw;   // adw = -1: match parent   banner
//				int adh = maiAd.adh;   //130                       banner
//
//				mMaiVideoView = new MaiVideoView(context, adw, adh); //设置宽高
//
//				//                mMaiVideoView.setSize(adw,adh);
//				//                    LayoutParams layoutParams = new LayoutParams(adw, adh);
//				//                    mMaiVideoView.setLayoutParams(layoutParams);
//
//
//				Button button = new Button(context);
//				button.setText("点击开始");
//				LayoutParams startP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//				startP.addRule(RelativeLayout.CENTER_IN_PARENT);
//				button.setLayoutParams(startP);
//				button.getBackground().setAlpha(0);
//				button.setTextColor(Color.WHITE);
//
//				Button buttonFullScreen = new Button(context);
//				buttonFullScreen.setText("全屏");
//				LayoutParams fullP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//				fullP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//				fullP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//				buttonFullScreen.setLayoutParams(fullP);
//				buttonFullScreen.getBackground().setAlpha(0);
//				buttonFullScreen.setTextColor(Color.WHITE);
//
//				//                @IdRes
//				//                int buttonId = 11111;
//				//                @IdRes
//				//                int fullButtonId = 11112;
//
//				//                button.setId(11111);
//				//                buttonFullScreen.setId(11112);
//
//				if (Build.VERSION.SDK_INT >= 17) {
//					button.setId(View.generateViewId());
//					buttonFullScreen.setId(View.generateViewId());
//				} else {
//					button.setId(generateViewId());
//					buttonFullScreen.setId(generateViewId());
//				}
//
//				MaiAdapter.this.addView(mMaiVideoView);
//				MaiAdapter.this.addView(button);
//				MaiAdapter.this.addView(buttonFullScreen);
//				if (maiAd.src != null) {
//					Uri uri = Uri.parse(maiAd.src.trim());
//					mMaiVideoView.setVideoURI(uri);
//				}
//				button.setOnClickListener(MaiAdapter.this);
//				buttonFullScreen.setOnClickListener(MaiAdapter.this);
//				//                    mMaiVideoView.start();
//
//				if (mMaiVideoView.isPlaying()) {
//					startTime = System.currentTimeMillis(); //开始的时间  是系统的当前时间
//					// maiController.sendMonitoropm(startTime, SUCCESS_SHOW);         // TODO: 16/8/29  注释的曝光
//				}
//
//
//			}
//
		}

	}


	private Button button_close;
	private ImageButton imageButton_close;
	private int stayTime = 5;
	private boolean isCloseByself = true;
	private boolean isShowBtn = false; //for banner  default No!
	private MaiTimeButton maiTimeButton;

	public void setStayTime(int stayTime) {
		if ((stayTime > 0 && stayTime <= 20) || stayTime == -1) {
			this.stayTime = stayTime;

		}

		L.e(TAG, "setCloseMode: " + stayTime);
	}

	public void setCloseByself(boolean closeByself) {
		isCloseByself = closeByself;

	}

	public void isShowCloseBtn(boolean isShowBtn) { //for banner
		this.isShowBtn = isShowBtn;

	}

	private void createCloseBtn() {

		if (maiRequest.m_stype == MaiStype.Banner_advertising && isShowBtn) {

			L.e(TAG, "Banner_advertising: ");

			if (null == button_close) {

				button_close = new Button(context);
				MaiAdapter.this.addView(button_close);
			}

			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			button_close.setLayoutParams(lp);
			//            imageButton_close.setImageBitmap(MaiUtils.loadAssetsBitmap(context, "com.adchina.mraid.assets/mraid_close.png"));
			button_close.setText("关闭");
			button_close.setPadding(2, 2, 5, 2);
			button_close.setTextColor(Color.BLACK);
			button_close.getBackground().setAlpha(0);
			button_close.setBackgroundColor(Color.TRANSPARENT);
			button_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//                    maiListener.onClose();
					//                    mMaiCloseTypeListener.closeByUser();
					Message msg = Message.obtain();
					if (msg == null) {
						msg = new Message();
					}
					msg.what = CLOSE_BY_USER;  //banner
					msg.obj = 111;
					mHandler.sendMessage(msg);
				}
			});


		}

		if ("insert".equals(adType)) {
			L.e(TAG, "Insert_advertising: ");
			// TODO: 16/8/18   插屏状态下的按钮
			if (stayTime == -1) {
				if (null == imageButton_close) {
					if (context != null) {
						imageButton_close = new ImageButton(context);
						MaiAdapter.this.addView(imageButton_close);
					}
				}

				LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				imageButton_close.setLayoutParams(lp);

				Bitmap bitmap = MaiUtils.loadAssetsBitmap(context, "com.admai.web.assets/m_close.png");

				int width = getDensityDpiBtnWidth(20);
				int height = width;
				if (bitmap != null) {
					Bitmap resizedBitmap = getResizeBtnBitmap(bitmap, width, height);
					imageButton_close.setImageBitmap(resizedBitmap);
				}

				//                imageButton_close.setImageBitmap(MaiUtils.loadAssetsBitmap(context, "com.admai.assets/Left.png"));
				imageButton_close.getBackground().setAlpha(0);
				imageButton_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//                        maiListener.onClose();
						//                        mMaiCloseTypeListener.closeByUser();
						Message msg = Message.obtain();
						if (msg == null) {
							msg = new Message();
						}
						msg.what = CLOSE_BY_USER; //insert
						msg.obj = 222;
						mHandler.sendMessage(msg);
					}
				});
			}

			if (stayTime > 0) {

				LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				if (maiTimeButton == null) {
					maiTimeButton = new MaiTimeButton(context, stayTime, mMaiCloseTypeListener, MaiStype.Insert_advertising);
					MaiAdapter.this.addView(maiTimeButton, lp);
				}
				maiTimeButton.start(isCloseByself, null, null);

				maiTimeButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//                        maiListener.onClose();
						//                        mMaiCloseTypeListener.closeByUser();
						Message msg = Message.obtain();
						if (msg == null) {
							msg = new Message();
						}
						msg.what = CLOSE_BY_USER;   //insert
						msg.obj = 222;
						mHandler.sendMessage(msg);
					}
				});


			}

		}

		if ("fullScreen".equals(adType)) {
			L.e(TAG, "fullScreen: ");
			// TODO: 16/8/18   fullScreen状态下的按钮
			//新建一个关闭的button
			//            stayTime == -1   //不计时 且自动关闭true or false 时都不生效
			//            stayTime ==  0  闪跳

			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			if (maiTimeButton == null) {
				maiTimeButton = new MaiTimeButton(context, stayTime, mMaiCloseTypeListener, MaiStype.Fixed_advertising);
				MaiAdapter.this.addView(maiTimeButton, lp);

			}
			maiTimeButton.start(isCloseByself, null, null);

			maiTimeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//                       maiListener.onClose();
					//                    mMaiCloseTypeListener.closeByUser();
					Message msg = Message.obtain();
					if (msg == null) {
						msg = new Message();
					}
					msg.what = CLOSE_BY_USER;  //fullscreen
					msg.obj = 333;
					mHandler.sendMessage(msg);
				}
			});
		}


		if (maiRequest.m_stype == MaiStype.Opening_advertising) {   //会倒计时的  标记 stayTime
			L.e(TAG, "Opening_advertising: ");
			// TODO: 16/8/18   开屏状态下的按钮
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			if (maiTimeButton == null) {
				maiTimeButton = new MaiTimeButton(context, stayTime, mMaiCloseTypeListener, MaiStype.Opening_advertising);
				MaiAdapter.this.addView(maiTimeButton, lp);

			}
			maiTimeButton.start(isCloseByself, mTargetActivity, mIntent);

			maiTimeButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//                    maiListener.onClose();

					Message msg = Message.obtain();
					if (msg == null) {
						msg = new Message();
					}
					msg.what = CLOSE_BY_USER;  // splash
					msg.obj = 444;
					mHandler.sendMessage(msg);
					L.e(TAG, " msg.what = CLOSE_BY_USER;");

					if (context instanceof Activity) {
						L.e(TAG, "onClick: 点击" + mIntent + mTargetActivity);
						if (mIntent == null && mTargetActivity != null) {
							Intent maiIntent = new Intent(context, mTargetActivity);
							context.startActivity(maiIntent);
							((Activity) context).finish();
						}
						if (mIntent != null && mTargetActivity == null) {
							context.startActivity(mIntent);
							((Activity) context).finish();
						}

					} else {
						L.e(TAG, "context 不属于 activity ");
					}
				}
			});


		}
	}

	private int getDensityDpiBtnWidth(int w) {

		return w * getDensityDpi() / 160;
	}

	private int getDensityDpi() {

		DisplayMetrics displayMetrics = null;
		if (displayMetrics == null) {
			displayMetrics = new DisplayMetrics();
		}
		if (context instanceof Activity) {
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		} else {
			((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
			                                                                  .getMetrics(displayMetrics);
		}
		return displayMetrics.densityDpi;
	}

	private Bitmap getResizeBtnBitmap(Bitmap bitmap, float width, float height) {

		float scaleWidth = width / bitmap.getWidth();
		float scaleHeight = height / bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmapResize = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
		return bitmapResize;
	}

	private boolean isCreateAdmai = true;//default true
	private ImageView mImageViewLeft;
	private ImageView mImageViewRight;

	public void isShowAdMai(boolean isShowAdMai) {
		isCreateAdmai = isShowAdMai;
	}


	/**
	 * generateViewId
	 */
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	public static int generateViewId() {
		for (; ; ) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF) {
				newValue = 1; // Roll over to 1, not 0.
			}
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	private void createAdmai() {

		if (isCreateAdmai) {
			if (null == mImageViewLeft) {

				mImageViewLeft = new ImageView(context);
				MaiAdapter.this.addView(mImageViewLeft);
			}
			if (null == mImageViewRight) {
				mImageViewRight = new ImageView(context);
				MaiAdapter.this.addView(mImageViewRight);
			}
			LayoutParams lpLeft = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LayoutParams lpRight = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			//           LayoutParams lpLeft = (LayoutParams) webView.getLayoutParams();
			//            lpLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			//            lpLeft.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			if (webView != null) {
				lpLeft.addRule(RelativeLayout.ALIGN_LEFT, webView.getId());
				lpLeft.addRule(RelativeLayout.ALIGN_BOTTOM, webView.getId());

				lpRight.addRule(RelativeLayout.ALIGN_RIGHT, webView.getId());
				lpRight.addRule(RelativeLayout.ALIGN_BOTTOM, webView.getId());
			}

			if (imageView != null) {
				lpLeft.addRule(RelativeLayout.ALIGN_LEFT, imageView.getId());
				lpLeft.addRule(RelativeLayout.ALIGN_BOTTOM, imageView.getId());

				lpRight.addRule(RelativeLayout.ALIGN_RIGHT, imageView.getId());
				lpRight.addRule(RelativeLayout.ALIGN_BOTTOM, imageView.getId());
				L.e("imageView.getid", imageView.getId());
			}


			mImageViewLeft.setLayoutParams(lpLeft);
			//            lpRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			//            lpRight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

			mImageViewRight.setLayoutParams(lpRight);

			Bitmap bitmapLeft = MaiUtils.loadAssetsBitmap(context, "com.admai.assets/admai_ad_left.png");
			int width = getDensityDpiBtnWidth(22);
			int height = 22;
			if (bitmapLeft != null) {
				height = width * bitmapLeft.getHeight() / bitmapLeft.getWidth();
				Bitmap resizedBitmapLeft = getResizeBtnBitmap(bitmapLeft, width, height);
				mImageViewLeft.setImageBitmap(resizedBitmapLeft);
			}
			Bitmap bitmapRight = MaiUtils.loadAssetsBitmap(context, "com.admai.assets/admai_logo_right.png"); //等高
			if (bitmapRight != null) {
				Bitmap resizedBitmapRight = getResizeBtnBitmap(bitmapRight, height, height);
				mImageViewRight.setImageBitmap(resizedBitmapRight);
			}
			//                imageButton_close.setImageBitmap(MaiUtils.loadAssetsBitmap(context, "com.admai.assets/Left.png"));
			//           mImageViewLeft.getBackground().setAlpha(0);
		}
	}

	private void removeAdmai() {
		if (mImageViewLeft != null) {
			MaiAdapter.this.removeView(mImageViewLeft);
		}
		if (mImageViewRight != null) {
			MaiAdapter.this.removeView(mImageViewRight);
		}

	}

	//构造方法 为了 new  传入 context
	public MaiAdapter(Context _context) {
		super(_context);
		context = _context;
	}


	/**
	 * adapter 初始化
	 *
	 * @param context
	 * 	上下文
	 * @param _maiRequest
	 * 	MaiRequest  maiRequest = MaiManager.getRequest(new MaiRequest());
	 * @param viewGroup
	 * 	adapter所要依附的父布局
	 */
	public void init(Context context, MaiRequest _maiRequest, ViewGroup viewGroup) {
		L.e(TAG, "adapter init");
		//大小
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); // TODO: 16/8/18  warp_content
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		//设置大小
		this.setLayoutParams(layoutParams);

		//设置自己子布局的位置 为居中
		this.setGravity(Gravity.CENTER);
		// 此 maiRequest 被赋值 传入的 _maiRequest
		maiRequest = _maiRequest;

		maiController = new MaiController(maiRequest, this);
		maiController.setContext(context);
		// viewGroup 是传入的ViewGroup 也就是MaiAdapter的父布局
		viewGroup.addView(this);

	}


	//设置服务url
	public void setServiceUrl(String _serviceUrl) {
		if (maiController != null) {
			maiController.setServiceUrl(_serviceUrl);
		}
	}

	//拿到maiListener对象
	public void setListener(MaiListener _maiListener) {
		maiListener = _maiListener;
	}

	//开始
	public void start() {

		L.e(TAG, "adapter start");
		this.setVisibility(View.VISIBLE);   //设置显示
		if (maiController != null) {
			maiController.start();         //  maiController(请求控制器) start()
		}
		if (maiListener != null) {

			L.e(TAG, Thread.currentThread().getName());
			// maiListener : 广告事件监听器

			Message msg = Message.obtain();
			if (msg == null) {
				msg = new Message();
			}
			msg.what = RECEIVE_REQUEST;
			mHandler.sendMessage(msg);
		}
	}


	//重新显示
	public void resume() {
//
//		if (currentPosition != 0) {
//			if (null != mMaiVideoView) {
//				mMaiVideoView.seekTo(currentPosition);
//				mMaiVideoView.pause();
//			}
//
//		}

	}


	//暂停
	public void pause() {
		L.e(TAG, "adapter pause");
		if (maiController != null) {
			maiController.visibilePause();
		}
//		if (mMaiVideoView != null) {
//			currentPosition = mMaiVideoView.getCurrentPosition();
//		}

	}

	private void startActivity() {
		if (context instanceof Activity) {
			if (mIntent == null && mTargetActivity != null) {
				Intent maiIntent = new Intent(context, mTargetActivity);
				context.startActivity(maiIntent);
				((Activity) context).finish();
			}
			if (mIntent != null && mTargetActivity == null) {

				context.startActivity(mIntent);
				((Activity) context).finish();
			}
		} else {
			L.e(TAG, "context 不属于 activity ");
			LogUtil.E("MaiSplash ", "context not instance of activity , Please check the place where init MaiSplash !");
		}
	}

	//停止
	public void disappear() {
		L.e(TAG, "adapter disappear");

		this.setVisibility(View.GONE);  //消失
		if (maiTimeButton != null) {
			maiTimeButton.destroy();
			maiTimeButton = null;
		}

		if (maiController != null) {
			maiController.stop();
		}
	}

	public void stop() {
		L.e(TAG, "adapter stop");

		if (maiTimeButton != null) {
			maiTimeButton.stop();
			maiTimeButton = null;
		}

		if (maiController != null) {
			maiController.stop();
		}
	}

	//停止
	public void stopSplash() {

		L.e(TAG, "stopSplash");
		if (maiTimeButton != null) {
			maiTimeButton.stop();
			maiTimeButton = null;
		}

		if (maiController != null) {
			maiController.stop();
		}
	}


	public void destroy() {

		if (maiTimeButton != null) {
			maiTimeButton.destroy();
			maiTimeButton = null;
		}
		if (context != null) {
			context = null;
		}
		if (maiController != null) {
			maiController.destroy();
		}
		this.removeAllViews();
	}

	public void setAd(MaiAd _maiAd) {  // 收到 广告响应容器 bean 服务器传回的 数据 bean
		maiAd = _maiAd;

		if (maiListener != null) {

			Message msg = Message.obtain();
			if (msg == null) {
				msg = new Message();
			}
			msg.what = RECEIVE_AD;  //
			mHandler.sendMessage(msg);
			//          maiListener.onReceiveAd(new Gson().toJson(maiAd));
		}
	}


	//通过maiAd.type 判断是属于何种投放类型 进行合理投放
	public void reflash() {

		L.e(TAG, "reflash: " + maiAd.type + maiAd.src);
		L.e(TAG, maiAd.toString());

		//]]]]]]]]]]]]]]]]]]]]]]]]]]] 表示图片物料 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]

		// MaiType.I:表示图片物料

		if (maiAd.type.equals(MaiType.I)) {
			LogUtil.D("Get Img Data", "Success !");
			if (imageView == null) {
				imageView = new ImageView(context);
				if (Build.VERSION.SDK_INT >= 17) {
					imageView.setId(View.generateViewId());
				} else {
					imageView.setId(generateViewId());
				}
				LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);    //ViewGroup.LayoutParams.WRAP_CONTENT
				imageView.setLayoutParams(layoutParams);
				imageView.setAdjustViewBounds(true);//调整ImageView的界限来保持图像纵横比,宽高一定值一变化值    // TODO: 16/8/18
				//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				//				  imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);


				Message msg = new Message();
				msg.what = SUCCESS_CREAT_IMAGE;  //成功创建imageView
				mHandler.sendMessage(msg);
			}
			asyncloadImage(maiAd.src);
		}

		//]]]]]]]]]]]]]]]]]]]]]]]]]]]] flash 物料 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]

		// TODO: 16/8/24    flash 物料   MaiInformationView
		// MaiType.F:表示 Flash 物料   MaiInformationView(context);

		if (maiAd.type.equals(MaiType.F)) {
			if (relativeLayout == null) {
//				relativeLayout = new MaiInformationView(context);
//				relativeLayout.setAd(maiAd);

				Message msg = new Message();
				msg.what = SUCCESS_CREAT_INFO;
				mHandler.sendMessage(msg);
			}
			//            asyncloadinfoImage(maiAd.);
		}

		//]]]]]]]]]]]]]]]]]]]]]]]]]]] 视频物料 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]

		//MaiType.V:表示视频物料

//		if (maiAd.type.equals(MaiType.V)) {
//
//			// TODO: 16/8/16 视频物料
//			if (mMaiVideoView == null) {
//
//				Message msg = new Message();
//				msg.what = SUCCESS_CREATE_VIDEO;  //成功创建videoview
//				mHandler.sendMessage(msg);
//			}
//
//		}

		//]]]]]]]]]]]]]]]]]]]]]]]]]]] 富媒体物料 ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]

		//MaiType.X:表示 Flash 物料/富媒体物料 (多点击地址 Flash 物料是已嵌入 跳转地址和 Click 监测代码的物料)

		//html 物料
		if (maiAd.type.equals(MaiType.H)) {

			Message msg = new Message();
			msg.what = SUCCESS_CREAT_INFOVIEW;
			mHandler.sendMessage(msg);

		}

		//]]]]]]]]]]]]]]]]]]]]]]]]]]] landing page ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]

		// TODO: 16/8/18   landing page
		//从adapter中的点击事件  只要点击adapter 就会landing page
		// maiAd.ldp : 落地页 URL 地址

		//		if (maiAd.ldp != null) {
		//			if (imageView != null) {
		//				//给imageView设置监听
		//				imageView.setOnClickListener(new OnClickListener() {
		//					public void onClick(View v) {
		//						Message msg = new Message();
		//						//                        msg.what = SUCCESS_CLICK;  //成功点击
		//						msg.what = SUCCESS_CLICK_MTR;  //成功点击
		//						msg.obj = maiAd.ldp;
		//						mHandler.sendMessage(msg);
		//						if (null != maiTimeButton) {
		//							maiTimeButton.destroy();
		//							MaiAdapter.this.removeView(maiTimeButton);
		//						}
		//						if (null != button_close) {
		//							MaiAdapter.this.removeView(button_close);
		//						}
		//						if (null != imageButton_close) {
		//							MaiAdapter.this.removeView(imageButton_close);
		//						}
		//						removeAdmai();
		//					}
		//				});
		//			}
		//		}
	}


	//失败的时候
	public void failed(String error) {
		//开屏请求失败的时候跳转
		if (maiRequest.m_stype == MaiStype.Opening_advertising) {

			if (context instanceof Activity) {
				if (mIntent == null && mTargetActivity != null) {
					Intent maiIntent = new Intent(context, mTargetActivity);
					context.startActivity(maiIntent);
					((Activity) context).finish();
					LogUtil.E("failed", "so startActivity");

				}
				if (mIntent != null && mTargetActivity == null) {

					context.startActivity(mIntent);
					((Activity) context).finish();
					LogUtil.E("failed", "so startActivity");

				}
				MaiReportManager.getInstance(context).unregisterNetworkStateReceiver();
			} else {
				L.e(TAG, "context 不属于 activity ");
				LogUtil.E("Splash request error ! also context not instance of Activity !", "Please Restart and Check context and Error Info");
			}
		}

		if (maiListener != null) {
			Message msg = Message.obtain();
			if (msg == null) {
				msg = new Message();
			}
			msg.what = FAILED_TO_RECEIVE_AD;
			msg.obj = error;
			mHandler.sendMessage(msg);
		}
	}


	/**
	 * 加载图片方法
	 * 采用普通方式异步的加载图片
	 */
	private void asyncloadImage(final String path) {

		L.e("lss", MaiUtils.getSDCardAvailableSize() + "");
		if (PermissionUtil.hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			//创建缓存目录，系统一运行就得创建缓存目录的，
			if (cache == null) {
				cache = new File(Environment.getExternalStorageDirectory(), "com.admai.cache");
				if (!cache.exists()) {
					boolean mkdirs = cache.mkdirs();
					if (!mkdirs) { //不能创建缓存目录 ： 只下载 不保存
						LogSSUtil.getInstance()
						         .saveLogs(MaiLType.MTR_DOWN_F, "mkdirs failed cant save img", 0, "none"); //
						//						MaiAdapter.this.failed("mkdirs failed");  //zhan
						downloadPic(path, true, true);
						return;
					}
				}
			}
			//这个URI 是图片下载到本地后 的缓存目录中的 URI
			try {
				Uri uri = MaiServer.getImageURIifExists(path, cache);
				if (uri != null) {
					L.e("lssuri", uri);
					L.e("lssuri", "has permission");
					try {
						Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
						if (bitmap == null) {
							downloadPic(path, true, true);
							return;
						}
						Message msg = new Message();
						msg.what = SUCCESS_GET_IMAGE;
						msg.obj = bitmap;
						mHandler.sendMessage(msg);
					} catch (Exception e) {
						LogSSUtil.getInstance()
						         .saveLogs(MaiLType.MTR_SHOW_F, "getImageBitmap Error__" + e.toString(), 0, "none");
						if (LogUtil.isShowError()) {
							e.printStackTrace();
						}
					}

					return;  //有了就return

				}
			} catch (Exception e) {
				LogSSUtil.getInstance()
				         .saveLogs(MaiLType.MTR_DOWN_F, "getImageURI  Error" + e.toString(), 0, "none");
				if (LogUtil.isShowError()) {
					e.printStackTrace();
				}
			}

			// uri == null时 说明还没有下载  so
			// 子线程，开启子线程去下载或者去缓存目录找图片，并且返回图片在缓存目录的地址
			downloadPic(path, false, true);

		} else {
			L.e("lssPermission", "no permission");
			LogSSUtil.getInstance()
			         .saveLogs(MaiLType.MTR_DOWN_F, "No Read or Write Permission, cant read pic from phone, must use Internet and show", 0, "none");
			downloadPic(path, true, false);
		}
	}

	private void downloadPic(final String path, final boolean isJustDownload, final boolean hasPermission) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					//这个 URI 是图片下载到本地后的 缓存目录中的URI
					//						Uri uri = MaiServer.getImageURI(path, cache);//code?
					Bitmap bitmap = null;
					if (path == null || path.equals("null")) {
						LogSSUtil.getInstance()
						         .saveLogs(MaiLType.MTR_DOWN_F, "src is null , show another img", 0, "none");
						bitmap = MaiServer.getImageBmp(context, MaiPath.DEFAULT_IMG_PATH, cache, isJustDownload, hasPermission); //防止图片出不来

					} else {
						bitmap = MaiServer.getImageBmp(context, path, cache, isJustDownload, hasPermission);
						if (bitmap == null) {
							bitmap = MaiServer.getImageBmp(context, MaiPath.DEFAULT_IMG_PATH, cache, isJustDownload, hasPermission); //防止图片出不来
						}
					}

					Message msg = new Message();
					msg.what = SUCCESS_GET_IMAGE;
					msg.obj = bitmap;
					L.e(TAG, "downloadpic:" + bitmap);
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					//
					LogUtil.saveLog(context, "getImageURI Error", 0);
					LogSSUtil.getInstance()
					         .saveLogs(MaiLType.MTR_SHOW_F, "downloadPic Error__" + e.toString(), 0, "none");
					if (LogUtil.isShowError()) {
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(runnable).start();
	}


	//html
	private int mScaleMode = 0;
	private int layoutWidth;
	private int layoutHeight;
	private boolean isUserWh = false; //screen 540-960
	private int aWidth = 600;
	private int aHeight = 50;
	private int scale = 100;

	//法1
	private void setMediaScale() {
		//        if (mAdMedia == null || mAdMedia.width == null || mAdMedia.height == null ){
		//            LogUtil.LOG_E(TAG, "mAdMedia: " + mAdMedia + "width: "+ (mAdMedia != null ? mAdMedia.width : null) + "height: "+ (mAdMedia != null ? mAdMedia.height : null));
		//            scale = 1;
		//            return;
		//        }

		//获取server返回的物料尺寸
		int mediaWidth = maiAd.adw;
		int mediaHeight = maiAd.adh;

		//获取屏幕宽高
		int screenW, screenH;
		screenW = MaiManager.screenwidth;
		screenH = MaiManager.screenheight;
		L.e(TAG, "setMediaScale: W-- " + screenW + "--H--" + screenH);
		//
		int parentWidth = this.getLayoutParams().width;
		int parentHeight = this.getLayoutParams().height;
		L.e(TAG, "setMediaScale: P -- W-- " + parentWidth + "--H--" + parentHeight);

		if (parentWidth > 0) {
			layoutWidth = parentWidth < screenW ? parentWidth : screenW;
		}
		if (parentHeight > 0) {
			layoutHeight = parentHeight < screenH ? parentHeight : screenH;
		}
		//用户宽高自定义

		if (!isUserWh) {   // 不是用户自定义 ， 屏幕宽度 和 比例高度
			layoutWidth = screenW;
			layoutHeight = mediaHeight * screenW / mediaWidth;
		} else {
			//            layoutWidth = aWidth<screenW?aWidth:screenW;  //屏幕宽度 和 自定义宽度取小的，否则宽度超出屏幕 出现横向滚轮
			layoutWidth = aWidth;
			layoutHeight = aHeight;
		}

		L.e(TAG, "setMediaScale: -- Layout -- W-- " + layoutWidth + "--H--" + layoutHeight);

		scale = getScale(mediaWidth, mediaHeight, layoutWidth, layoutHeight);


		L.e(TAG, "mediaWidth:" + mediaWidth +
		         "mediaHeight:" + mediaHeight +
		         "layoutWidth:" + layoutWidth +
		         "layoutHeight:" + layoutHeight +
		         "scale:" + scale + isUserWh + mScaleMode);
	}

	/**
	 * 获取缩放
	 *
	 * @param mediaWidth
	 * 	素材宽
	 * @param mediaHeight
	 * 	素材高
	 * @param layoutWidth
	 * 	布局宽
	 * @param layoutHeight
	 * 	布局高
	 * @return 缩放尺寸
	 */
	protected int getScale(int mediaWidth, int mediaHeight, int layoutWidth, int layoutHeight) {
		int scale;
		double d = 0;
		double wd = (double) mediaWidth / (double) layoutWidth;     // 素材宽度/布局宽度
		double hd = (double) mediaHeight / (double) layoutHeight;    // 素材高度/布局高度
		if (mScaleMode == 0) {
			if (wd < hd) {
				d = (double) layoutWidth / (double) mediaWidth;
			} else {
				d = (double) layoutHeight / (double) mediaHeight;
			}
		} else if (mScaleMode == 1) {
			if (wd > hd) {
				d = (double) layoutWidth / (double) mediaWidth;
			} else {
				d = (double) layoutHeight / (double) mediaHeight;
			}
		}
		String result = String.format("%.8f", d);
		scale = (int) (Double.valueOf(result) * 100) + 1;
		return scale;
	}

	//法2 加头
	private String getHtmlData(String bodyHTML) {


		int h = maiRequest.m_adh;
		if (maiRequest.m_stype != MaiStype.Banner_advertising) {
			h = px2dip(h);
		}
		if (maiRequest.m_stype == MaiStype.Insert_advertising) {
			if (h > maiRequest.m_dvh) {  //插屏高度若超过屏幕会设为屏幕的4/5
				h = maiRequest.m_dvh / 5 * 4;
			}
		}

		//		String head = "<head>" +
		//		              "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
		//		              "<style>img{max-width: 100%; width: 100%; height:" + h + ";}</style>" +
		//		              "</head>";
		String head = "<head>" +
		              "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
		              "<style>img{max-width: 100%; width: 100%; height:100%" + ";}</style>" +
		              "</head>";
		String htmlData = "<html>" + head + "<body>" + bodyHTML + "</body></html>";

		L.e("getHtmlData", htmlData);
		L.e("maiRequest.m_adh", maiRequest.m_adh);
		L.e("maiRequest.m_adhMaiManager.outMetrics.density", MaiManager.outMetrics.density);
		L.e("maiRequest.m_adhgetDensityDpi()", getDensityDpi());
		L.e("px2dip(maiRequest.m_adh)", px2dip(maiRequest.m_adh));
		return htmlData;
	}

	/**
	 * <p>
	 * densityDpi/160 = density
	 *
	 * @param pixels
	 * @return
	 */
	private int px2dip(int pixels) {  // px-->dip
		L.e("outMetrics:", maiRequest.m_adh + "-----" + MaiManager.outMetrics + "==" + MaiAdManager.getInstance());
		return pixels * DisplayMetrics.DENSITY_DEFAULT / getDensityDpi();
	}

	//fixme///////////////////////////////////////////////////////
	// TODO: 16/8/23   return webview
	public WebView getWebView() {
		if (webView == null) {
			webView = new WebView(context);
		}
		return webView;
	}


	//
	@Override
	public void onClick(View v) {
//
//		switch (v.getId()) {
//
//			case 11111:
//
//				Button button = (Button) v;
//				if (mMaiVideoView != null) {
//					if (mMaiVideoView.isPlaying()) {
//						mMaiVideoView.pause();
//						//                button.setVisibility(VISIBLE);
//						button.setText("按下播放");
//
//					} else {
//						mMaiVideoView.start();
//						mMaiVideoView.fullScreen(isFullScreen);
//						startFullScreen(isFullScreen);
//						//                button.setVisibility(INVISIBLE);
//						button.setText("按下暂停");
//					}
//				}
//
//				break;
//
//			case 11112:
//				currentPosition = mMaiVideoView.getCurrentPosition();
//				mMaiVideoView.pause();
//				startFullScreen(true);
//
//				break;
//
//			default:
//
//				break;
//		}

	}


	public void fullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;

	}

//	public void startFullScreen(boolean isFullScreen) {
//
//		if (isFullScreen) {
//			Intent intent = new Intent(context, FullScreenVideo.class);
//			int currentPosition = mMaiVideoView.getCurrentPosition();
//			intent.putExtra("currentPosition", currentPosition);
//			intent.putExtra("url", maiAd.src);
//			context.startActivity(intent);
//
//		}
//	}


	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}


}
