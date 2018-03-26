package cn.com.chinaccs.datasite.main.common;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fddi
 * 
 */
public abstract class App {
	/**
	 * utf-8编码
	 */
	public static final String ENCODE_UTF8 = "UTF-8";
	/**
	 * 控制日志输出的TAG
	 */
	public static final String LOG_TAG = "chinaccs_log";
	/**
	 * share存储域的TAG
	 */
	public static final String SHARE_TAG = "chinaccs_share";

	/**
	 * 
	 */
	public static final String JSON_MAP_RESULT = "result";
	/**
	 * 
	 */
	public static final String JSON_MAP_MSG = "msg";
	/**
	 * 
	 */
	public static final String JSON_MAP_DOWNLOADURL = "downloadUrl";
	/**
	 * 
	 */
	public static final String JSON_MAP_USERNAME = "userName";
	/**
	 * 
	 */
	public static final String JSON_MAP_ORGCODE = "orgCode";
	/**
	 *
	 */
	public static final String JSON_MAP_USERTYPE = "userType";

	/**
	 * 清除换行和空格
	 * 
	 * @param str
	 * 
	 * @return
	 */
	public static String clearString(String str) {
		if (str == null) {
			return null;
		}
		String c_str = "";
		Pattern pattern = Pattern.compile("\\s+|\t|\r|\n");
		Matcher m = pattern.matcher(str);
		c_str = m.replaceAll(" ");
		return c_str;
	}

	/**
	 * 判断目标值的二进制值中，在判断值的二进制值为1的所有索引位置是否为1，是则返回true，否则返回false
	 * 
	 * @param value
	 *            目标值
	 * @param index
	 *            判断值
	 * @return true or false
	 */
	public static Boolean isBinaryInclude(int tValue, int iValue) {
		boolean isInclud = true;
		String t = Integer.toBinaryString(tValue);
		String i = Integer.toBinaryString(iValue);
		if (t.length() < i.length()) {
			isInclud = false;
			return isInclud;
		}
		int len = t.length() - i.length();
		for (int n = 0; n < i.length(); n++) {
			Character ichar = i.charAt(n);
			Character tchar = t.charAt(len + n);
			if (ichar.toString().equals("1")) {

				if (!tchar.toString().equals(ichar.toString())) {
					isInclud = false;
				}
			}
		}
		return isInclud;
	}

	/**
	 * MD5加密
	 * 
	 * @param keyStr
	 *            需要加密的字符串
	 * @return 返回MD5加密后的字符串
	 */
	public static String signMD5(String keyStr) {
		String sign = "";
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(keyStr.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		sign = md5StrBuff.toString();
		return sign;
	}

	/**
	 * 获取软件版本号code
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		int versionCode = 0;
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			versionCode = info.versionCode; // 版本名
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 获取软件版本名name
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			versionName = info.versionName; // 版本名
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 判断手机GPS是否开启
	 * 
	 * @return true--GPS开启 false--GPS关闭
	 */
	public static Boolean isGpsOpen(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 生成普通等待框
	 * 
	 * @param context
	 *            上下文关系
	 * @param msg
	 *            显示信息
	 * @return 返回一个等待框
	 */
	public static ProgressDialog progressDialog(Context context, String msg) {
		ProgressDialog loadDialog = new ProgressDialog(context);
		loadDialog.setTitle("");
		loadDialog.setMessage(msg);
		return loadDialog;
	}

	/**
	 * 生成普通对话框
	 * 
	 * @param context
	 *            上下文关系
	 * @param title
	 * @param msg
	 * @param ol_1
	 *            确定按钮事件
	 * @param ol_2
	 *            取消按钮事件
	 * @return
	 */
	public static AlertDialog alertDialog(Context context, String title,
										  String msg, OnClickListener ol_1, OnClickListener ol_2) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title).setMessage(msg);
		builder.setPositiveButton("确定", ol_1);
		builder.setNegativeButton("取消", ol_2);
		return builder.create();
	}

	/**
	 * @param context
	 * @param title
	 * @param msg
	 * @param ol_1
	 * @param btnName_1
	 * @param ol_2
	 * @param btnName_2
	 * @return
	 */
	public static AlertDialog alertDialog(Context context, String title,
										  String msg, OnClickListener ol_1, String btnName_1,
										  OnClickListener ol_2, String btnName_2) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title).setMessage(msg);
		builder.setPositiveButton(btnName_1, ol_1);
		builder.setNegativeButton(btnName_2, ol_2);
		return builder.create();
	}

	/**
	 * @param cr
	 * @param type
	 * @return
	 */
	public static String getDateToStr(Calendar cr, Integer type) {
		String date = "";
		SimpleDateFormat format;
		switch (type) {
		case 0:
			format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
			break;
		case 1:
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
			break;
		case 2:
			format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss", Locale.CHINA);
			break;
		default:
			format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
			break;
		}
		date = format.format(cr.getTime());
		return date;
	}

	/**
	 * 判断手机是否为CDMA制式
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPhoneTypeByCDMA(Context context) {
		boolean isCdma = false;
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int phoneType = tm.getPhoneType();
		if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
			isCdma = true;
		}
		return isCdma;
	}

	/**
	 * 获取imsi号
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		String imsi = null;
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		imsi = tm.getSubscriberId();
		return imsi;
	}
}
