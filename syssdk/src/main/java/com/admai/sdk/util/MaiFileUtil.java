package com.admai.sdk.util;

import android.content.Context;
import android.text.TextUtils;

import com.admai.sdk.util.log.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ZAN on 16/9/13.
 */
public class MaiFileUtil {
	private static final String TAG = MaiFileUtil.class.getSimpleName();
	;
	
	public MaiFileUtil() {
	}
	
	
	public static String save(Context context, String saveMsg, String savePath, String fileName, boolean var4) throws Exception {
		MessageDigest messageDigest = null;
		
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		}
		
		FileOutputStream fos = null;
		File file = null;
		
		try {
			byte[] saveMsgBytes = saveMsg.getBytes();
			if (messageDigest != null) {
				messageDigest.update(saveMsgBytes);
			}
			
			if (TextUtils.isEmpty(savePath)) {   //确保savePath存在
				savePath = getSavePath(context, savePath);
			}
			
			file = new File(savePath, fileName);
			fos = new FileOutputStream(file, var4);
			fos.write(saveMsgBytes, 0, saveMsgBytes.length);
			fos.flush();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					if (LogUtil.isShowError()) {
						e.printStackTrace();
					}
				}
				
				fos = null;
			}
			
		}
		
		String path = null;
		if (null != file) {
			path = file.getPath();
		}
		
		return path;
	}
	
	public static String getSavePath(Context context, String savePath) {
		try {
			if (TextUtils.isEmpty(savePath)) {
				savePath = context.getCacheDir().getPath() + File.separator + "maiAds";
			}
			
			File file = new File(savePath);
			file.mkdirs();
			return savePath;
		} catch (Exception e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
	public static String readTextFile(String filePath) {
		BufferedReader bReader = null;
		StringBuilder sb = null;
		try {
			bReader = new BufferedReader(new FileReader(filePath));
			String line = "";
			sb = new StringBuilder("[");
			while ((line = bReader.readLine()) != null) {
				sb.append(line);
			}
			String s = sb.toString();
			String substring = s.substring(0, s.lastIndexOf(","));
			s = substring + "]";
			return s;
		} catch (Exception e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (bReader != null) {
					bReader.close();
				}
				sb = null;
			} catch (IOException e) {
				if (LogUtil.isShowError()) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static byte[] readBinaryFile(String filePath) {
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		
		try {
			fis = new FileInputStream(filePath);
			baos = new ByteArrayOutputStream();
			int c = 0;
			byte[] buffer = new byte[1024 * 8];
			while ((c = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, c);
				baos.flush();
			}
			return baos.toByteArray();
		} catch (Exception e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				if (LogUtil.isShowError()) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static boolean writeTextFile(String content, String fileName) {
		PrintWriter pWriter = null;
		try {
			pWriter = new PrintWriter(new FileWriter(fileName, true), true);
			pWriter.write(content);
			return true;
		} catch (IOException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} finally {
			if (pWriter != null) {
				pWriter.close();
			}
		}
		return false;
	}
	
	public static boolean writeTextFile2(String content, String fileName) {
		BufferedWriter bWriter = null;
		try {
			bWriter = new BufferedWriter(new FileWriter(fileName, true));
			bWriter.write(content);
			bWriter.flush();
			return true;
		} catch (IOException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (bWriter != null) {
					bWriter.close();
				}
			} catch (IOException e) {
				if (LogUtil.isShowError()) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public static boolean writeBinaryFile(byte[] data, String filePath) {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filePath));
			bos.write(data, 0, data.length);
			bos.flush();
			return true;
		} catch (IOException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				if (LogUtil.isShowError()) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		return file.delete();
	}
	
	public static boolean isExistFile(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}
	
	public static String getFileExtension(String filePath) {
		return filePath.substring(filePath.lastIndexOf("."), filePath.length());
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
		return baos.toByteArray();
	}
	
	public static String streamToString(InputStream is, String charsetName) {
		BufferedInputStream bis = new BufferedInputStream(is);
		StringBuilder sb = new StringBuilder();
		int c = 0;
		byte[] buffer = new byte[8 * 1024];
		try {
			while ((c = bis.read(buffer)) != -1) {
				sb.append(new String(buffer, charsetName));
			}
			return sb.toString();
		} catch (IOException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				if (LogUtil.isShowError()) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static InputStream stringToInputStream(String str) {
		// InputStream in_nocode = new ByteArrayInputStream(str.getBytes());
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			if (LogUtil.isShowError()) {
				e.printStackTrace();
			}
		}
		return is;
	}
	
}

