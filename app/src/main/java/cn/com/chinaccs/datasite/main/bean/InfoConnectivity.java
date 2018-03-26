package cn.com.chinaccs.datasite.main.bean;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.chinaccs.datasite.main.common.AppNetWork;

/**
 * @author fddi
 * 
 */
public class InfoConnectivity {

	private ConnectivityManager cm;
	private TelephonyManager tm;
	private WifiManager wm;
	private NetworkInfo info;
	private Context context;

	public InfoConnectivity(Context context) {
		this.context=context;
		cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	public List<Collector> getConectiviyInfo(List<Collector> list) {
		info = cm.getActiveNetworkInfo();
		list.add(new Collector("Connectivity-Info", "", 5,
				Collector.DATA_TYPE_TITLE));
		getActiveNetworkInfo(list);
		return list;
	}

	public List<Collector> getActiveNetworkInfo(List<Collector> list) {
		int state = tm.getDataState();
		String dataState = "";
		switch (state) {
		case TelephonyManager.DATA_DISCONNECTED:// 网络断开
			dataState = "DATA_DISCONNECTED";
			break;
		case TelephonyManager.DATA_CONNECTING:// 网络正在连接
			dataState = "DATA_CONNECTING";
			break;
		case TelephonyManager.DATA_CONNECTED:// 网络连接上
			dataState = "DATA_CONNECTED";
			break;
		case TelephonyManager.DATA_SUSPENDED: // 连接挂起
			dataState = "DATA_SUSPENDED";
			break;
		}
		list.add(new Collector("DataState", dataState, 5,
				Collector.DATA_TYPE_UPLOAD));
		WifiInfo wi = wm.getConnectionInfo();
		list.add(new Collector("Mac", wi.getMacAddress() == null ? "" : wi
				.getMacAddress(), 5, Collector.DATA_TYPE_UPLOAD));
		String ip = getLocalIpAddress();
		list.add(new Collector("ip", ip == null ? "" : ip, 5,
				Collector.DATA_TYPE_UPLOAD));
		if (info != null && info.isAvailable()) {
			list.add(new Collector("ConncetType", AppNetWork
					.getConnectModel(context), 5, Collector.DATA_TYPE_UPLOAD));
			list.add(new Collector("SubTypeName", info.getSubtypeName(), 5,
					Collector.DATA_TYPE_UPLOAD));
			list.add(new Collector("SubType", Integer.toString(info
					.getSubtype()), 5));
			list.add(new Collector("Extra", info.getExtraInfo(), 5,
					Collector.DATA_TYPE_UPLOAD));
			list.add(new Collector("State", info.getState().toString(), 5));
			list.add(new Collector("DetailState", info.getDetailedState()
					.toString(), 5));
			list.add(new Collector("Reason", info.getReason(), 5));
		}
		return list;
	}

	public String getLocalIpAddress() {
		try {
			String ipv4 = null;
			ArrayList<NetworkInterface> mylist = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : mylist) {
				ArrayList<InetAddress> ialist = Collections.list(ni
						.getInetAddresses());
				for (InetAddress address : ialist) {
					if (!address.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ipv4 = address
									.getHostAddress())) {
						return ipv4;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
