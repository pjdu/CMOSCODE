package cn.com.chinaccs.datasite.main.bean;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.List;

import cn.com.chinaccs.datasite.main.common.AppNetWork;

/**
 * SIM卡和运营商信息
 * 
 * @author fddi
 * 
 */
public class InfoSim {
	private Context context;
	private TelephonyManager tm;
	public static final String[] NETWORK_CDMA = { "CDMA", "EVDO_0", "EVDO_A",
			"EVDO_B" };

	public InfoSim(Context context) {
		this.context = context;
		this.tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}

	public List<Collector> getSimInfo(List<Collector> list) {
		Collector data = new Collector("SIM卡和运营商信息", "", 2,
				Collector.DATA_TYPE_TITLE);
		list.add(data);
		String imei = tm.getDeviceId(); // 获取终端的唯一标识
		imei = imei == null ? "" : imei;
		list.add(new Collector("IMEI", imei, 2, Collector.DATA_TYPE_UPLOAD));

		String imsi = tm.getSubscriberId(); // 获取用户唯一标识
		imsi = imsi == null ? "" : imsi;
		list.add(new Collector("IMSI", imsi, 2, Collector.DATA_TYPE_UPLOAD));

		/*
		 * String pNum = tm.getLine1Number(); // 获取手机号 pNum = pNum == null ? ""
		 * : pNum; list.add(new Collector("PhoneNumber", pNum, 2));
		 */

		String phoneType = ""; // 获取手机制式

		switch (tm.getPhoneType()) {
		case TelephonyManager.PHONE_TYPE_GSM:
			phoneType = "GSM";
			break;
		case TelephonyManager.PHONE_TYPE_CDMA:
			phoneType = "CDMA";
			break;
		default:
			phoneType = "";
			break;
		}
		list.add(new Collector("PHONE_TYPE", phoneType, 2,
				Collector.DATA_TYPE_UPLOAD));

		list.add(new Collector("isNetworkRoaming", Boolean.toString(tm
				.isNetworkRoaming()), 2));// 是否漫游

		String voiceMail = tm.getVoiceMailAlphaTag() + tm.getVoiceMailNumber(); // 获取语音信箱信息
		voiceMail = voiceMail == null ? "" : voiceMail;
		voiceMail = voiceMail == "null" ? "" : voiceMail;
		list.add(new Collector("voiceMail", voiceMail, 2));

		String countryIso = tm.getNetworkCountryIso(); // 获取ISO标准的国家码
		countryIso = countryIso == null ? "" : countryIso;
		list.add(new Collector("countryIso", countryIso, 2));

		String NetworkOperator = tm.getNetworkOperator(); // 获取MCC+MNC代码
		NetworkOperator = NetworkOperator == null ? "" : NetworkOperator;
		list.add(new Collector("NetworkOperator", NetworkOperator, 2));

		String NetworkOperatorName = tm.getNetworkOperatorName(); // 获取移动网络运营商的名称
		NetworkOperatorName = NetworkOperatorName == null ? ""
				: NetworkOperatorName;
		list.add(new Collector("NetworkOperatorName", NetworkOperatorName, 2,
				Collector.DATA_TYPE_UPLOAD));

		list.add(new Collector("NETWORK_TYPE", AppNetWork
				.getConnectModel(context), 2, Collector.DATA_TYPE_UPLOAD));
		return list;
	}
}
