package com.admai.sdk.mai;

/**
 * Created by Zan on 2016/11/20.
 */

public interface MaiReportLogsListener<T> {

	/**
	 * 函数名: onSucceed<br>
	 * 功能: 服务器返回OK，并且根据泛型自动 组装成相应的entity；<br>
	 *
	 */
	public void onSucceed(Object context, String info, Object attachment);


	/**
	 * 函数名: onResponseError
	 * 功能: 服务器解析包成功，但是返回错误信息;ResultCode 为错误信息枚举类<br>
	 *
	 */
	public void onResponseError(String logs, String e, int errorCode) ;

	/**
	 * 函数名: onNetError
	 * 功能: 服务器连接异常的一系列错误；具体如下
	 *
	 */
	public void onNetError(String logs,String exception,int errorCode) ;

}
