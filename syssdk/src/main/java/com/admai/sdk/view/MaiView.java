package com.admai.sdk.view;

/**
 * Created by macmi001 on 16/7/8.
 */
public class MaiView {
//	public static final String TAG = MaiView.class.getSimpleName();
//	//maiAdapter在 MaiFullScreen 中被new
//	public MaiAdapter maiAdapter;
//	//maiRequest在 MaiFullScreen 中被get
//	// maiRequest = MaiManager.getRequest(new MaiRequest());
//	public MaiRequest maiRequest;
//
//	public MaiListener maiListener;
//
//	public MaiView(Context context) {
//		super(context);
//	}
//
//	public MaiView(Context context, String maiID) {
//		super(context);
//		LogSSUtil.getInstance().setAdId(maiID);
//	}
//
//	/**
//	 * 设置请求物料类型
//	 *
//	 * @param hasImage
//	 * @param hasFlash
//	 * @param hasHtml
//	 * @param hasVideo
//	 */
//	@NotProguard
//	public void setType(boolean hasImage, boolean hasFlash, boolean hasHtml, boolean hasVideo) {
//		ArrayList<Integer> type = new ArrayList<>();
//		if (hasImage) {
//			type.add(1);
//		}
//		if (hasFlash) {
//			type.add(2);
//		}
//		if (hasHtml) {
//			type.add(4);
//		}
//		if (hasVideo) {
//			type.add(5);
//		}
//
//		if (!hasImage && !hasFlash && !hasHtml && !hasVideo) {
//			type = null;
//		}
//
//		maiRequest.m_type = type;
//	}
//
//	@NotProguard
//	public void isShowAdMai(boolean isShowAdMai) {
//		if (maiAdapter != null) {
//			maiAdapter.isShowAdMai(isShowAdMai);
//		}
//	}
//
//	public void setServiceUrl(String _serviceUrl) {
//		if (maiAdapter != null) {
//			maiAdapter.setServiceUrl(_serviceUrl);
//		}
//	}
//
//
//	@NotProguard
//	public void setListener(MaiListener _maiListener) {
//		maiListener = _maiListener;
//		if (maiAdapter != null) {
//			maiAdapter.setListener(_maiListener);
//			maiAdapter.setMaiCloseTypeListener(this);
//		}
//		if (this instanceof MaiInsert || this instanceof MaiSplash || this instanceof MaiFullScreen || this instanceof MaiBanner) {
//			LogUtil.D(this.getClass().getSimpleName(), "setMaiListener Success");
//		}
//	}
//
//	@NotProguard
//	public void start() {
//		//        this.setVisibility(View.VISIBLE);
//
//		if (this instanceof MaiInsert || this instanceof MaiSplash || this instanceof MaiFullScreen || this instanceof MaiBanner) {
//			String viewName = this.getClass().getSimpleName();
//			ViewGroup vp = (ViewGroup) this.getParent();
//			if (null != vp) {
//				L.e(TAG, "start: " + vp);
//				L.e(TAG, vp.getVisibility());
//
//				if (vp.getVisibility() == GONE) {
//					vp.setVisibility(VISIBLE);
//				}
//				if (vp.getBackground() != null) {
//					vp.getBackground().setAlpha(0);
//				}
//				if (maiAdapter != null) {
//					maiAdapter.start();
//				}
//				LogUtil.D(viewName, "Start");
//			} else {
//				LogUtil.D(viewName, "Can't Start ，Please give " + viewName + " A Parent ");
//				L.e(TAG, "no_parent");
//			}
//		}
//
//
//		L.e(TAG, "maiView.start()");
//	}
//
//	@NotProguard
//	public void stop() {
//		//        this.setVisibility(View.GONE);
//		if (maiAdapter != null) {
//			maiAdapter.stop();
//		}
//		if (this instanceof MaiInsert || this instanceof MaiFullScreen || this instanceof MaiBanner || this instanceof MaiSplash) {
//
//			String viewName = this.getClass().getSimpleName();
//			LogUtil.D(viewName, "Stop");
//		}
//	}
//
//	public boolean disMissBy(int byWho) {
//		if (this instanceof MaiInsert || this instanceof MaiFullScreen || this instanceof MaiBanner || this instanceof MaiSplash) {
//
//			ViewGroup vp = (ViewGroup) this.getParent();
//			String viewName = this.getClass().getSimpleName();
//			if (null != vp) {
//				vp.removeView(this);
//				if (vp.getVisibility() == VISIBLE) {
//					vp.setVisibility(GONE);
//				}
//				LogUtil.D(viewName, "DisMiss");
//				if (maiAdapter != null) {
//					maiAdapter.disappear();
//				}
//
//				if (maiListener != null) {
//
//					switch (byWho) {
//						case 0: //error
//
//							break;
//
//						case 1: //self
//
//
//
//							break;
//						case 2: //user
//
//							if (maiListener != null) {
//								maiListener.onClose();
//							}
//
//							break;
//
//						default:
//
//							break;
//					}
//				}
//				return true;
//			} else {
//				LogUtil.D(viewName, "Can't DisMiss ，" + viewName + "'s Parent == null");
//				return false;
//			}
//		}
//		return false;
//	}
//	public boolean disMissWithTrack(int byWho) {
//		if (this instanceof MaiInsert || this instanceof MaiFullScreen || this instanceof MaiBanner || this instanceof MaiSplash) {
//
//			ViewGroup vp = (ViewGroup) this.getParent();
//			String viewName = this.getClass().getSimpleName();
//			if (null != vp) {
//				vp.removeView(this);
//				if (vp.getVisibility() == VISIBLE) {
//					vp.setVisibility(GONE);
//				}
//				LogUtil.D(viewName, "DisMiss");
//				if (maiAdapter != null) {
//					maiAdapter.disappear();
//				}
//
//				if (maiListener != null) {
//
//					switch (byWho) {
//						case 0: //error
//
//							break;
//
//						case 1: //self
//
//
//
//							break;
//						case 2: //user
//
//							if (maiListener != null) {
//								maiListener.onClose();
//							}
//
//							break;
//
//						default:
//
//							break;
//					}
//				}
//				return true;
//			} else {
//				LogUtil.D(viewName, "Can't DisMiss ，" + viewName + "'s Parent == null");
//				return false;
//			}
//		}
//		return false;
//	}
//
//	@NotProguard
//	public boolean disMiss() {
//		if (this instanceof MaiInsert || this instanceof MaiFullScreen || this instanceof MaiBanner || this instanceof MaiSplash) {
//
//			ViewGroup vp = (ViewGroup) this.getParent();
//			String viewName = this.getClass().getSimpleName();
//			if (null != vp) {
//				vp.removeView(this);
//				if (vp.getVisibility() == VISIBLE) {
//					vp.setVisibility(GONE);
//				}
//				LogUtil.D(viewName, "DisMiss");
//				if (maiAdapter != null) {
//					maiAdapter.disappear();
//				}
//
//				if (maiListener != null) {
//					maiListener.onClose();
//
//				}
//				return true;
//			} else {
//				LogUtil.D(viewName, "Can't DisMiss ，" + viewName + "'s Parent == null");
//				return false;
//			}
//		}
//		//        if (this instanceof MaiSplash) {
//		//            if (maiListener != null) {
//		//                maiListener.onClose();
//		//            }
//		//        }
//
//		return false;
//	}
//
//	@NotProguard
//	public void onDestroy() {
//
//		maiRequest = null;
//		maiListener = null;
//		if (maiAdapter != null) {
//			maiAdapter.destroy();
//		}
//
//		if (this instanceof MaiInsert || this instanceof MaiSplash || this instanceof MaiFullScreen || this instanceof MaiBanner) {
//			String viewName = this.getClass().getSimpleName();
//			this.removeAllViews();
//			LogUtil.D(viewName, "Destroy");
//		}
//	}
//
//
//	@NotProguard
//	public void onPause() {
//
//		if (this instanceof MaiInsert || this instanceof MaiSplash || this instanceof MaiFullScreen || this instanceof MaiBanner) {
//			String viewName = this.getClass().getSimpleName();
//			LogUtil.D(viewName, "Pause");
//		}
//		if (maiAdapter != null) {
//			maiAdapter.pause();
//		}
//
//	}
//
//	@NotProguard
//	public void resume() {
//
//		if (this instanceof MaiInsert || this instanceof MaiSplash || this instanceof MaiFullScreen || this instanceof MaiBanner) {
//			String viewName = this.getClass().getSimpleName();
//			LogUtil.D(viewName, "Resume");
//		}
//		if (maiAdapter != null) {
//			maiAdapter.resume();
//		}
//	}
//
//	@Override
//	public void closeBySelf() {// also main Thread
//		this.disMissBy(1);
//		if (maiListener != null) {
//			//            maiListener.onSkip();
//		}
//	}
//
//	@Override
//	public void closeByUser(int adType) { //111 222 333 444
//		this.disMissBy(2);
//		//        if (maiListener != null) {
//		//            maiListener.onClose();
//		//        }
//	}
//
//	@Override
//	public void closeByClick() {
//		this.disMissBy(3);
//	}
//
//	@Override
//	public void closeByError() {
//		this.disMissBy(0);
//	}
	
}
