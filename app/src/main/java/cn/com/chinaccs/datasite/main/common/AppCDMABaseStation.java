package cn.com.chinaccs.datasite.main.common;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;

/**
 * @author Fddi
 * 
 */
public abstract class AppCDMABaseStation {

	/**
	 * 获取基站
	 * 
	 * @return
	 */
	public static CDMABaseStation getInsertBaseStation(Context context) {
		CDMABaseStation bs = null;
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int phoneType = tm.getPhoneType();
		if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
			CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
			if (location != null) {
				int sid = location.getSystemId();// 系统标识 mobileNetworkCode
				int ci = location.getBaseStationId();// 基站小区号 cellId
				int nid = location.getNetworkId();// 网络标识 locationAreaCode
				double Latitude = (double) location.getBaseStationLatitude() / 14400;
				double Longitude = (double) location.getBaseStationLongitude() / 14400;
				java.text.DecimalFormat df = new java.text.DecimalFormat(
						"#0.0000");
				String lat = new String(df.format(Latitude));
				String lng = new String(df.format(Longitude));
				bs = new CDMABaseStation();
				bs.setCi(String.valueOf(ci));
				bs.setSid(String.valueOf(sid));
				bs.setNid(String.valueOf(nid));
				bs.setLongitude(lng);
				bs.setLatitude(lat);
			}
		}
		return bs;
	}

	/**
	 * 获取基站ci
	 * 
	 * @param context
	 * @return
	 */
	public static String getCi(Context context) {
		String ci = null;
		CDMABaseStation bs = getInsertBaseStation(context);
		if (bs != null)
			ci = bs.getCi();
		return ci;
	}

	/**
	 * 获取基站sid
	 * 
	 * @param context
	 * @return
	 */
	public static String getCDMASid(Context context) {
		String sid = null;
		CDMABaseStation bs = getInsertBaseStation(context);
		if (bs != null)
			sid = bs.getSid();
		return sid;
	}

	/**
	 * 获取基站nid
	 * 
	 * @param context
	 * @return
	 */
	public static String getCDMANid(Context context) {
		String nid = null;
		CDMABaseStation bs = getInsertBaseStation(context);
		if (bs != null)
			nid = bs.getNid();
		return nid;
	}

	public static class CDMABaseStation {
		private String ci;
		private String sid;
		private String nid;
		private String longitude;
		private String latitude;

		public CDMABaseStation() {

		}

		public String getCi() {
			return ci;
		}

		public void setCi(String ci) {
			this.ci = ci;
		}

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getNid() {
			return nid;
		}

		public void setNid(String nid) {
			this.nid = nid;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
	}
}
