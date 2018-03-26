package cn.com.chinaccs.datasite.main.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.util.List;

public abstract class AppNetWork {
	public static final String SHARE_IF_AUTOTEACH="MAP_SHARE_IF_AUTOTEACH";
	public static final String SHARE_IF_AUTOTEST="MAP_SHARE_IF_AUTOTEST";
	public static final String SHARE_IF_AUTO4G = "MAP_SHARE_IF_AUTO4G";
	public static final String SHARE_IF_AUTOWIFI = "MAP_SHARE_IF_AUTOWIFI";
	public static final String WIFI_COMPLETED = "COMPLETED";
	public static final String MODEL_WIFI = "wifi";
	public static final String MODEL_3G = "mobile";
	public static final String MODEL_DISCONNECT = "disconnect";
	public static final String MODEL_LTE = "LTE";
	public static final String MODEL_1x = "1x";

	/**
	 * 判断手机是否联网
	 * 
	 * @return true--联网 false--无网络
	 */
	public static Boolean isNetWork(Context context) {
		boolean isActive = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			return isActive;
		}
		isActive = info.isConnectedOrConnecting();
		return isActive;
	}

	/**
	 * 当前手机联网模式
	 * 
	 * @param context
	 * @return
	 */
	public static String getConnectModel(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo infoCm = cm.getActiveNetworkInfo();
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo infoWm = wm.getConnectionInfo();
		if (infoCm == null || infoWm == null)
			return MODEL_DISCONNECT;
		if (!infoCm.isAvailable())
			return MODEL_DISCONNECT;
		SupplicantState state = infoWm.getSupplicantState();
		String bssid = infoWm.getBSSID() == null ? "" : infoWm.getBSSID();
		if (wm.isWifiEnabled() && !bssid.equals("")
				&& state.equals(SupplicantState.COMPLETED)) {
			return MODEL_WIFI;// 已连接wifi
		}
		int mt = infoCm.getSubtype();
		if (mt == 0) {
			return MODEL_DISCONNECT;
		} else if (mt == TelephonyManager.NETWORK_TYPE_1xRTT
				|| mt == TelephonyManager.NETWORK_TYPE_CDMA
				|| mt == TelephonyManager.NETWORK_TYPE_IDEN) {
			return MODEL_1x;
		} else if (mt == TelephonyManager.NETWORK_TYPE_EVDO_0
				|| mt == TelephonyManager.NETWORK_TYPE_EVDO_A
				|| mt == TelephonyManager.NETWORK_TYPE_EVDO_B
				|| mt == TelephonyManager.NETWORK_TYPE_EHRPD
				|| mt == TelephonyManager.NETWORK_TYPE_HSPAP) {
			return MODEL_3G;
		} else if (mt == TelephonyManager.NETWORK_TYPE_LTE) {
			return MODEL_LTE;
		} else {
			String name = infoCm.getSubtypeName();
			return name;
		}
	}

	/**
	 * 是否打开3G网络
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean is3GOpen(Context context) {
		boolean open = false;
		Object[] arg = null;
		try {
			open = invokeMethod(context, "getMobileDataEnabled", arg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return open;
	}

	/**
	 * 打开wifi
	 * 
	 * @return
	 */
	public static void openWifi(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean isEnabled = wm.isWifiEnabled();
		if (!isEnabled) {
			wm.setWifiEnabled(true);
		}
	}

	/**
	 * 判断wifi是否已连接
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isWifiConnected(Context context) {
		boolean isActive = false;
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean isEnabled = wm.isWifiEnabled();
		if (isEnabled) {
			WifiInfo info = wm.getConnectionInfo();
			if (info.getSupplicantState() == SupplicantState.COMPLETED) {
				return true;
			}
		}
		return isActive;
	}

	/**
	 * 断开当前wifi连接
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean disconnectWifi(Context context) {
		boolean isDisconnect = false;
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			WifiInfo info = wm.getConnectionInfo();
			if (info != null && info.getSSID() != null) {
				int nid = info.getNetworkId();
				isDisconnect = wm.disableNetwork(nid);
				isDisconnect = wm.disconnect();
			}
		}
		return isDisconnect;
	}

	/**
	 * 是否有可用的wifi连接
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isActivedWifi(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!wm.isWifiEnabled()) {
			wm.setWifiEnabled(true);
		}
		if (wm.isWifiEnabled()) {
			List<ScanResult> sl = wm.getScanResults();
			if (sl != null && sl.size() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 开启系统设置里的移动网络
	 */
	public static void setMobileNetChange(Context context, Boolean state) {
		try {
			invokeBooleanArgMethod(context, "setMobileDataEnabled", state);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static boolean invokeMethod(Context context, String methodName,
									   Object[] arg) throws Exception {

		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Class ownerClass = mConnectivityManager.getClass();

		Class[] argsClass = null;
		if (arg != null) {
			argsClass = new Class[1];
			argsClass[0] = arg.getClass();
		}
		@SuppressWarnings("unchecked")
		Method method = ownerClass.getMethod(methodName, argsClass);

		Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

		return isOpen;
	}

	@SuppressWarnings("rawtypes")
	public static Object invokeBooleanArgMethod(Context context,
												String methodName, boolean value) throws Exception {

		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Class ownerClass = mConnectivityManager.getClass();

		Class[] argsClass = new Class[1];
		argsClass[0] = boolean.class;

		@SuppressWarnings("unchecked")
		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(mConnectivityManager, value);
	}
}
