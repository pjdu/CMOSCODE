package cn.com.chinaccs.datasite.main.bean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 基站相关信息
 * 
 * @author fddi
 * 
 */
public class InfoCell {
	private static final String TAG = "InfoCell";

	private TelephonyManager tm;
	private Context context;

	public InfoCell(Context context) {
		this.context = context;
		this.tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}

	public List<Collector> getCellInfo(List<Collector> list) {
		int phoneType = tm.getPhoneType();
		boolean getCDMA = false;
		boolean getGSM = false;
		if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
			CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
			if (location != null) {
				getCDMA = true;
				int sid = location.getSystemId();// 系统标识 mobileNetworkCode
				int bid = location.getBaseStationId();// 基站小区号 cellId
				int nid = location.getNetworkId();// 网络标识 locationAreaCode
				double Latitude = (double) location.getBaseStationLatitude() / 14400;
				double Longitude = (double) location.getBaseStationLongitude() / 14400;
				java.text.DecimalFormat df = new java.text.DecimalFormat(
						"#0.0000");
				String lat = new String(df.format(Latitude));
				String lng = new String(df.format(Longitude));
				Collector data = new Collector("CDMA基站信息", "", 3,
						Collector.DATA_TYPE_TITLE);
				list.add(data);
				list.add(new Collector("CI", bid + "", 3,
						Collector.DATA_TYPE_UPLOAD));
				list.add(new Collector("sid", sid + "", 3,
						Collector.DATA_TYPE_UPLOAD));
				list.add(new Collector("nid", nid + "", 3,
						Collector.DATA_TYPE_UPLOAD));
				/*list.add(new Collector("CDMA_Signal", "", 3,
						Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));*/
				list.add(new Collector("CDMA_Signal", "", 3,
						Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));
				list.add(new Collector("CDMA_Ecio", "", 3,
						Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DB));

				list.add(new Collector("EVDO_Signal", "", 3,
						Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));
				list.add(new Collector("EVDO_Ecio", "", 3,
						Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DB));
				list.add(new Collector("EVDO_Snr", "", 3,
						Collector.DATA_TYPE_UPLOAD));
				list.add(new Collector("Latitude", lat, 3,
						Collector.DATA_TYPE_UPLOAD));
				list.add(new Collector("Longitude", lng, 3,
						Collector.DATA_TYPE_UPLOAD));

				list.add(new Collector("Asu_level", "", 3,
						Collector.DATA_TYPE_UPLOAD));
			}
		} else {
			GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
			if (location != null) {
				getGSM = true;
				int cid = location.getCid();
				int lac = location.getLac();
				int psc = location.getPsc();
				Collector data = new Collector("GSM基站信息", "", 8,
						Collector.DATA_TYPE_TITLE);
				list.add(data);
				list.add(new Collector("GSM_CI", cid + "", 8,
						Collector.DATA_TYPE_UPLOAD));
				list.add(new Collector("GSM_Lac", lac + "", 8,
						Collector.DATA_TYPE_UPLOAD));
				list.add(new Collector("GSM_Mcc", "", 8,
						Collector.DATA_TYPE_UPLOAD));
				list.add(new Collector("GSM_Mnc", "", 8,
						Collector.DATA_TYPE_UPLOAD));
				list.add(new Collector("GSM_Psc", psc + "", 8,
						Collector.DATA_TYPE_UPLOAD));

				list.add(new Collector("GSM_Signal", "", 8,
						Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));
				list.add(new Collector("GSM_Asu_level", "", 8,
						Collector.DATA_TYPE_UPLOAD));

				list.add(new Collector("相邻基站信息", "", 9,
						Collector.DATA_TYPE_TITLE));
				list = this.getNeighboringCell(list);
			}
		}
		if (VERSION.SDK_INT >= 17) {
//			Log.i(CoConfig.LOG_TAG, "-------------------VERSION.SDK_INT >= 17");
			list = getCellInfoNew(getCDMA, getGSM, list);
		} else {
			getInitLTECell(list);
		}
		return list;
	}

	@SuppressLint("NewApi")
	public List<Collector> getCellInfoNew(boolean getCDMA, boolean getGSM,
										  List<Collector> list) {
		List<CellInfo> cis = tm.getAllCellInfo();
		if (cis == null || cis.size() <= 0) {
			getInitLTECell(list);
			return list;
		}
		InfoCellKinds kinds = new InfoCellKinds(context);
		boolean isGet = false;
		for (CellInfo ci : cis) {
			if (ci instanceof CellInfoCdma) {
				// 获取cdma基站信息
				if (!isGet && !getCDMA) {
//					Log.i(CoConfig.LOG_TAG, "获取cdma基站信息-------------------");
					kinds.getCDMACellInfo((CellInfoCdma) ci, list);
					isGet = true;
				}
			} else if (ci instanceof CellInfoGsm) {
				// 获取gsm基站信息
				if (!isGet && !getGSM) {
//					Log.i(CoConfig.LOG_TAG, "获取gsm基站信息-------------------");
					kinds.getGSMCellInfo((CellInfoGsm) ci, list);
					isGet = true;
				}
			} else if (VERSION.SDK_INT >= 18 && ci instanceof CellInfoWcdma) {
				// 获取wcdma基站信息
				if (!isGet && !getGSM) {
//					Log.i(CoConfig.LOG_TAG, "获取wcdma基站信息-------------------");
					kinds.getWCDMACellInfo((CellInfoWcdma) ci, list);
					isGet = true;
				}
			} else if (ci instanceof CellInfoLte) {
				// 获取lte基站信息
				if (!isGet){
//					Log.i(CoConfig.LOG_TAG, "获取lte基站信息-------------------");
					kinds.getLTECellInfo((CellInfoLte) ci, list);
					isGet = true;
				}
			}
//			if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
//				// 获取lte基站信息
//				kinds.getLTECellInfo((CellInfoLte) ci, list);
//			}
		}
//		Log.d(TAG, "基站信息：" + list.toString());
		return list;
	}

	/**
	 * gsm取得临近基站的一种方法
	 * 
	 * @param list
	 * @return
	 */
	public List<Collector> getNeighboringCell(List<Collector> list) {
		List<NeighboringCellInfo> infos = tm.getNeighboringCellInfo();
		if (infos == null || infos.size() == 0) {
			list.add(new Collector("", "无法获取相邻基站信息", 9,
					Collector.DATA_TYPE_FULLLINE));
		}
		for (NeighboringCellInfo info : infos) {// 获取相邻基站信息
			List<Collector> list_c = new ArrayList<Collector>();
			list_c.add(new Collector("cid", info.getCid() + "", 9));
			list_c.add(new Collector("lac", info.getLac() + "", 9));
			list_c.add(new Collector("psc", info.getPsc() + "", 9));
			int signal = -113 + 2 * info.getRssi();
			list_c.add(new Collector("rssi", Integer.toString(signal), 9,
					Collector.UNIT_SIGNAL_DBM));
			list.add(new Collector("", list_c, 9, Collector.DATA_TYPE_LIST
					| Collector.DATA_TYPE_FULLLINE | Collector.DATA_TYPE_NBCELL));
		}
		return list;
	}

	/**
	 * 初始化LTE
	 * 
	 * @param list
	 * @return
	 */
	public List<Collector> getInitLTECell(List<Collector> list) {
		Collector data = new Collector("LTE基站信息", "", 4,
				Collector.DATA_TYPE_TITLE);
		list.add(data);
		list.add(new Collector("LTE_dBm", "", 4, Collector.DATA_TYPE_UPLOAD,
				Collector.UNIT_SIGNAL_DBM));
		list.add(new Collector("LTE_CI", "", 4, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Mcc", "", 4, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Mnc", "", 4, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Pci", "", 4, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Tac", "", 4, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Signal", "", 4, Collector.DATA_TYPE_UPLOAD,
				Collector.UNIT_SIGNAL_DBM));

//		list.add(new Collector("LTE_TimingAdvance", "", 4,
//				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Asulevel", "", 4,
				Collector.DATA_TYPE_UPLOAD));

		list.add(new Collector("LTE_RSRP", "", 4, Collector.DATA_TYPE_UPLOAD,
				Collector.UNIT_SIGNAL_DBM));
		list.add(new Collector("LTE_RSRQ", "", 4, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_SINR", "", 4, Collector.DATA_TYPE_UPLOAD));
		return list;
	}
}
