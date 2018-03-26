package cn.com.chinaccs.datasite.main.util;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

/**
 * @author fddi
 * 
 */
public abstract class AppScaffolding {

	/**
	 * utf-8编码
	 */
	public static final String ENCODE_UTF8 = "UTF-8";
	/**
	 * 控制日志输出的TAG
	 */
	public static final String LOG_TAG = "AppScaffolding---Log";
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
	 * 判断目标值的二进制值中，在判断值的二进制值为1的所有索引位置是否为1，是则返回true，否则返回false
	 * 
	 * @param tValue
	 *            目标值
	 * @param iValue
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
	 * 判断服务是否启动
	 * 
	 * @param context
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30); // 先获得运行中sevice的列表
		for (int i = 0; i < serviceList.size(); i++) {
			Log.d(LOG_TAG, serviceList.get(i).service.getClassName());
			if (className.equals(serviceList.get(i).service.getClassName())) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
