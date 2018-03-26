package cn.com.chinaccs.datasite.main.bean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;

import java.util.List;

public class InfoCellKinds {
	private static final String TAG = InfoCellKinds.class.getSimpleName();
	Context context;

	public InfoCellKinds(Context context) {
		this.context = context;
	};

	@SuppressLint("NewApi")
	public List<Collector> getCDMACellInfo(CellInfoCdma cell,
										   List<Collector> list) {
		CellIdentityCdma cid = cell.getCellIdentity();
		CellSignalStrengthCdma css = cell.getCellSignalStrength();
		int sid = cid.getSystemId();// 系统标识 mobileNetworkCode
		int bid = cid.getBasestationId();// 基站小区号 cellId
		int nid = cid.getNetworkId();// 网络标识 locationAreaCode
		double Latitude = (double) cid.getLatitude() / 14400;
		double Longitude = (double) cid.getLongitude() / 14400;
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.0000");
		String lat = new String(df.format(Latitude));
		String lng = new String(df.format(Longitude));
		Collector data = new Collector("CDMA基站信息", "", 3,
				Collector.DATA_TYPE_TITLE);
		list.add(data);
		list.add(new Collector("CI", Integer.toString(bid), 3,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("sid", Integer.toString(sid), 3,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("nid", Integer.toString(nid), 3,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("Latitude", lat, 3, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("Longitude", lng, 3, Collector.DATA_TYPE_UPLOAD));
		/*Log.i(CoConfig.LOG_TAG, "-----" + Integer.toString(css.getCdmaDbm())+ "-----"
				+ Integer.toString(css.getCdmaEcio() / 10) + "--------"
				+ Integer.toString(css.getEvdoDbm()) + "---------"
				+Integer.toString(css.getEvdoEcio()) + "---------"
				+ Integer.toString(css.getEvdoSnr()));*/
		/*list.add(new Collector("CDMA_Signal", Integer.toString(css.getDbm()),
				3, Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));*/
		list.add(new Collector("CDMA_Signal", Integer.toString(css.getCdmaDbm()),
				3, Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));
		list.add(new Collector("CDMA_Ecio",
				Integer.toString(css.getCdmaEcio() / 10), 3,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("EVDO_Signal",
				Integer.toString(css.getEvdoDbm()), 3,
				Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));
		list.add(new Collector("EVDO_Ecio",
				Integer.toString(css.getEvdoEcio()), 3,
				Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DB));
		list.add(new Collector("EVDO_Snr", Integer.toString(css.getEvdoSnr()),
				3, Collector.DATA_TYPE_UPLOAD));

		list.add(new Collector("Asu_level",
				Integer.toString(css.getAsuLevel()), 3,
				Collector.DATA_TYPE_UPLOAD));
		return list;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public List<Collector> getGSMCellInfo(CellInfoGsm cell, List<Collector> list) {
		CellIdentityGsm cid = cell.getCellIdentity();
		CellSignalStrengthGsm css = cell.getCellSignalStrength();
		Collector data = new Collector("GSM基站信息", "", 8,
				Collector.DATA_TYPE_TITLE);
		list.add(data);
		list.add(new Collector("GSM_CI", Integer.toString(cid.getCid()), 8,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("GSM_Lac", Integer.toString(cid.getLac()), 8,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("GSM_Mcc", Integer.toString(cid.getMcc()), 8,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("GSM_Mnc", Integer.toString(cid.getMnc()), 8,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("GSM_Psc", Integer.toString(cid.getPsc()), 8,
				Collector.DATA_TYPE_UPLOAD));

		list.add(new Collector("GSM_Signal", Integer.toString(css.getDbm()), 8,
				Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));
		list.add(new Collector("GSM_Asu_level", Integer.toString(css
				.getAsuLevel()), 8, Collector.DATA_TYPE_UPLOAD));
		return list;
	}

	@SuppressLint("NewApi")
	public List<Collector> getWCDMACellInfo(CellInfoWcdma cell,
											List<Collector> list) {
		CellIdentityWcdma cid = cell.getCellIdentity();
		CellSignalStrengthWcdma css = cell.getCellSignalStrength();
		Collector data = new Collector("WCDMA基站信息", "", 8,
				Collector.DATA_TYPE_TITLE);
		list.add(data);
		list.add(new Collector("GSM_CI", Integer.toString(cid.getCid()), 8,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("GSM_Lac", Integer.toString(cid.getLac()), 8,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("GSM_Mcc", Integer.toString(cid.getMcc()), 8,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("GSM_Mnc", Integer.toString(cid.getMnc()), 8,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("GSM_Psc", Integer.toString(cid.getPsc()), 8,
				Collector.DATA_TYPE_UPLOAD));

		list.add(new Collector("GSM_Signal", Integer.toString(css.getDbm()), 8,
				Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));
		list.add(new Collector("GSM_Asu_level", Integer.toString(css
				.getAsuLevel()), 8, Collector.DATA_TYPE_UPLOAD));
		return list;
	}

	@SuppressLint("NewApi")
	public List<Collector> getLTECellInfo(CellInfoLte cell, List<Collector> list) {
		CellIdentityLte cid = cell.getCellIdentity();
		CellSignalStrengthLte css = cell.getCellSignalStrength();
		Collector data = new Collector("LTE基站信息", "", 4,
				Collector.DATA_TYPE_TITLE);
		list.add(data);
		list.add(new Collector("LTE_dBm", "", 4, Collector.DATA_TYPE_UPLOAD,
				Collector.UNIT_SIGNAL_DBM));
		// 由于获取的CI是微基站，而集团基站是宏基站，
		// 宏基站ID长20bit，可标识2^20=1048576≈1百万基站
		// 微站ID长28bit，可标识更多基站（2^28≈2.6亿个）
        int ci = cid.getCi();
        String cellId = Integer.toBinaryString(ci).substring(0, 20);
        ci = Integer.valueOf(cellId, 2);
        list.add(new Collector("LTE_CI", Integer.toString(ci), 4,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Mcc", Integer.toString(cid.getMcc()), 4,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Mnc", Integer.toString(cid.getMnc()), 4,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Pci", Integer.toString(cid.getPci()), 4,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Tac", Integer.toString(cid.getTac()), 4,
				Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Signal",
				Integer.toString(css.getDbm() > 0 ? 0 : css.getDbm()), 4,
				Collector.DATA_TYPE_UPLOAD, Collector.UNIT_SIGNAL_DBM));

//		list.add(new Collector("LTE_TimingAdvance", Integer.toString(css
//				.getTimingAdvance()), 4, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_Asulevel", Integer.toString(css
				.getAsuLevel()), 4, Collector.DATA_TYPE_UPLOAD));

		list.add(new Collector("LTE_RSRP", "", 4, Collector.DATA_TYPE_UPLOAD,
				Collector.UNIT_SIGNAL_DBM));
		list.add(new Collector("LTE_RSRQ", "", 4, Collector.DATA_TYPE_UPLOAD));
		list.add(new Collector("LTE_SINR", "", 4, Collector.DATA_TYPE_UPLOAD));

		return list;
	}
}
