package cn.com.chinaccs.datasite.main.ui.functions;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.location.BDLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.bean.Collector;
import cn.com.chinaccs.datasite.main.bean.InfoCell;
import cn.com.chinaccs.datasite.main.bean.InfoConnectivity;
import cn.com.chinaccs.datasite.main.bean.InfoSim;
import cn.com.chinaccs.datasite.main.bean.InfoSystem;
import cn.com.chinaccs.datasite.main.bean.InfoWifi;
import cn.com.chinaccs.datasite.main.common.AppNetWork;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.ui.MainApp;

/**
 * Created by shaozhiqiang on 2016/8/5.
 * 网络测试的公共类
 */
public class NetworkTest {
    private BDGeoLocation geoGB;
    private BDLocation location;
    private Map<String, String> signalMap;
    private TelephonyManager tm;
    public List<Collector> listLTEMain;
    private Context context;
    private Integer level = 100;
    private InfoWifi wifi;
    private JSONObject LTE;
    private final String TAG = NetworkTest.class.getSimpleName();

    public JSONObject getLTE() {
        return LTE;
    }

    public void stopListener() {
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    public NetworkTest(Context context) {
        tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        this.context = context;
        // 获取地理位置信息
        geoGB = MainApp.geoBD;
        location = geoGB.location;
        if (geoGB != null && geoGB.locClient != null
                && geoGB.locClient.isStarted()) {
            geoGB.locClient.requestLocation();
        }
        if (listLTEMain == null) {
            listLTEMain = new ArrayList<Collector>();
        }
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        reCollectInfo();




    }
    /*获取手机的基本信息，os版本、内核版本等等*/
private void phoneState(){}

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signal) {
            // TODO Auto-generated method stub
            super.onSignalStrengthsChanged(signal);
//			Log.i(CoConfig.LOG_TAG, signal.toString());
            try {
                Method[] methods = android.telephony.SignalStrength.class
                        .getMethods();
                signalMap = new HashMap<String, String>();
                for (Method mthd : methods) {
                    if (mthd.getName().equals("getLteSignalStrength")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        if (sig != 0)
                            sig = -113 + 2 * sig;
                        signalMap.put("LTE_Signal", Integer.toString(sig));
                    } else if (mthd.getName().equals("getLteRsrp")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        signalMap.put("LTE_RSRP", Integer.toString(sig));
                    } else if (mthd.getName().equals("getLteRsrq")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        signalMap.put("LTE_RSRQ", Integer.toString(sig));
                        Log.i(TAG, "LTE_RSRQ setValue");
                    } else if (mthd.getName().equals("getLteRssnr")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        signalMap.put("LTE_SINR", (Integer.toString(sig / 10)));
                    } else if (mthd.getName().equals("getGsmSignalStrength")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        if (sig != 0)
                            sig = -113 + 2 * sig;
                        signalMap.put("Signal", Integer.toString(sig));
                    }
                }
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (tm != null
                    && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                Log.i(TAG, "signalMap setValue");
                signalMap.put("CDMA_Signal",
                        Integer.toString(signal.getCdmaDbm()));
                signalMap.put("CDMA_Ecio",
                        Integer.toString(signal.getCdmaEcio() / 10));
                signalMap.put("EVDO_Signal",
                        Integer.toString(signal.getEvdoDbm()));
                signalMap.put("EVDO_Ecio",
                        Integer.toString(signal.getEvdoEcio() / 10));
                signalMap
                        .put("EVDO_Snr", Integer.toString(signal.getEvdoSnr()));
                int level = (signal.getCdmaDbm() + 113) / 2;
                signalMap.put("Asu_level", Integer.toString(level));

            }

            //singalChange();
        }

        @Override
        public void onCellLocationChanged(CellLocation location) {
            // TODO Auto-generated method stub
            super.onCellLocationChanged(location);
           // singalChange();
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            // TODO Auto-generated method stub
            super.onDataConnectionStateChanged(state);
           // singalChange();
        }
    };

    public void singalChange() {
        if (signalMap == null)
            return;
        if (listLTEMain == null)
            return;
        if (tm != null && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
//			Log.i(CoConfig.LOG_TAG, "-------------------singalChange");
            /*list.get(getPosition("CDMA_Signal")).setDataValue(
                    signalMap.get("CDMA_Signal"));*/
            listLTEMain.get(getPosition("CDMA_Signal")).setDataValue(
                    signalMap.get("CDMA_Signal"));
            listLTEMain.get(getPosition("CDMA_Ecio")).setDataValue(
                    signalMap.get("CDMA_Ecio"));
            listLTEMain.get(getPosition("EVDO_Signal")).setDataValue(
                    signalMap.get("EVDO_Signal"));
            listLTEMain.get(getPosition("EVDO_Ecio")).setDataValue(
                    signalMap.get("EVDO_Ecio"));
            listLTEMain.get(getPosition("EVDO_Snr")).setDataValue(
                    signalMap.get("EVDO_Snr"));
            listLTEMain.get(getPosition("Asu_level")).setDataValue(signalMap.get("Asu_level"));
            Log.i(TAG, "EVDO_Ecio has value");

        } else {
            listLTEMain.get(getPosition("GSM_Signal")).setDataValue(
                    signalMap.get("GSM_Signal"));
            String value = listLTEMain.get(getPosition("LTE_RSRP")).getDataValue();

        }
        if (signalMap.containsKey("LTE_Signal")
                && signalMap.containsKey("LTE_RSRP")
                && signalMap.containsKey("LTE_RSRQ")
                && signalMap.containsKey("LTE_SINR")
                && signalMap.containsKey("LTE_SINR")) {
            listLTEMain.get(getPosition("LTE_dBm")).setDataValue(
                    signalMap.get("CDMA_Signal"));
            listLTEMain.get(getPosition("LTE_Signal")).setDataValue(
                    signalMap.get("LTE_Signal"));
            listLTEMain.get(getPosition("LTE_RSRP")).setDataValue(
                    signalMap.get("LTE_RSRP"));
            listLTEMain.get(getPosition("LTE_RSRQ")).setDataValue(
                    signalMap.get("LTE_RSRQ"));
            listLTEMain.get(getPosition("LTE_SINR")).setDataValue(
                    signalMap.get("LTE_SINR"));
            Log.i(TAG, "LTE_RSRQ has value");

        }
        Log.i(TAG, "singalChange");

    }

    //遍历集合
    private int getPosition(String name) {
        int position = 0;
        for (int i = 0; i < listLTEMain.size(); i++) {
            if (name.equalsIgnoreCase(listLTEMain.get(i).getDataName())) {
                position = i;
                break;
            }
        }
        return position;
    }

    //获取网站测试的基本信息
    private void reCollectInfo() {
        singalChange();
        //判断用户是否打开GPS，若果打开则source为gps
        String source = null;
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps == true) {
            source = "GPS";
        } else {
            source = "LBS";
        }
        //获取用户的接入点名称
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String apn = networkInfo.getExtraInfo();
//		Log.i(CoConfig.LOG_TAG, "reCollectInfo---------------------");
        LTE = new JSONObject();

        listLTEMain.clear();

        // add the location
        //BDGeoLocation location = new BDGeoLocation(context);
        String addr = "";
        double lat = 0;
        double lon = 0;
        String province = null;
        String city = null;
        if (geoGB.locClient != null) {
            geoGB.locClient.requestLocation();
            BDLocation loc = geoGB.location;
            if (loc != null && loc.hasAddr()) {
                Log.i("Post", loc.toString());
                province = loc.getProvince();
                city = loc.getCity();
                addr = loc.getAddrStr();
                lat = loc.getLatitude();
                lon = loc.getLongitude();
            }
        }
        //坐标信息
        listLTEMain.add(new Collector("坐标位置信息", "", 0, Collector.DATA_TYPE_TITLE));
        listLTEMain.add(new Collector("Lat", lat + "", 0, Collector.DATA_TYPE_UPLOAD));
        listLTEMain.add(new Collector("Lon", lon + "", 0, Collector.DATA_TYPE_UPLOAD));
        listLTEMain.add(new Collector("Addr", addr, 0, Collector.DATA_TYPE_UPLOAD));

        //系统信息
        InfoSystem sys = new InfoSystem();
        listLTEMain = sys.getSystemInfo(context, listLTEMain);
        listLTEMain.get(getPosition("Battery_free")).setDataValue(
                Integer.toString(level));

        //Sim卡和运营商信息
        InfoSim sim = new InfoSim(context);
        listLTEMain = sim.getSimInfo(listLTEMain);
        //基站信息
        InfoCell cell = new InfoCell(context);
        listLTEMain = cell.getCellInfo(listLTEMain);
        listLTEMain = cell.getInitLTECell(listLTEMain);

        //Connectivity-Info
        InfoConnectivity ct = new InfoConnectivity(context);
        listLTEMain = ct.getConectiviyInfo(listLTEMain);
        wifi = new InfoWifi(context);
        wifi.getWifiInfo(listLTEMain);
        String netType = AppNetWork.getConnectModel(context);
        if (netType.equals(AppNetWork.MODEL_LTE)) {
            netType = AppNetWork.MODEL_LTE;
        } else if (netType.equals(AppNetWork.MODEL_WIFI)) {
            netType = AppNetWork.MODEL_WIFI;
        } else {
            netType = "EVDO";
        }
        /*String netType;
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            netType = "CDMA";
        } else {
            netType = "GSM";
        }*/

        try {
            LTE.put("APN", apn);
            LTE.put("Province", province);
            LTE.put("City", city);
            LTE.put("Source", source);
            LTE.put("NetType", netType);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public void setLTE() {
        try {
            if (listLTEMain.get(getPosition("IMEI")).getDataValue().isEmpty()) {
                LTE.put("IMSI", "");
                LTE.put("MEID", "");
                LTE.put("PhoneType", listLTEMain.get(getPosition("model")).getDataValue());
                LTE.put("OSVersion", listLTEMain.get(getPosition("firmware version")).getDataValue());
                LTE.put("BaseBand", listLTEMain.get(getPosition("system version")).getDataValue());
                LTE.put("Kernel", listLTEMain.get(getPosition("KernelVersion(liunx)")).getDataValue());
                LTE.put("InnerVersion", listLTEMain.get(getPosition("system version")).getDataValue());
                LTE.put("RamUsage", listLTEMain.get(getPosition("RAM")).getDataValue());
                LTE.put("CpuUsage",listLTEMain.get(getPosition("CPU")).getDataValue());
                LTE.put("Longitude", listLTEMain.get(getPosition("Lon")).getDataValue());
                LTE.put("Latitude", listLTEMain.get(getPosition("Lat")).getDataValue());
                LTE.put("LocationDesc", listLTEMain.get(getPosition("Addr")).getDataValue());
                LTE.put("CdmaSid", listLTEMain.get(getPosition("sid")).getDataValue());
                LTE.put("CdmaNid", listLTEMain.get(getPosition("nid")).getDataValue());
                LTE.put("CdmaBsid", listLTEMain.get(getPosition("CI")).getDataValue());
                LTE.put("CdmadBm", "");
                LTE.put("LteCi", listLTEMain.get(getPosition("LTE_CI")).getDataValue());
                LTE.put("LtePci", listLTEMain.get(getPosition("LTE_Pci")).getDataValue());
                LTE.put("LteTac", listLTEMain.get(getPosition("LTE_Tac")).getDataValue());
                LTE.put("LteRsrp", listLTEMain.get(getPosition("LTE_RSRP")).getDataValue() + "dBm");
                LTE.put("LteSinr", listLTEMain.get(getPosition("LTE_SINR")).getDataValue());
                LTE.put("InnerIP", listLTEMain.get(getPosition("ip")).getDataValue());
                LTE.put("OuterIP", "");
                LTE.put("Ecio", signalMap.get("EVDO_Ecio"));
                LTE.put("Snr", signalMap.get("EVDO_Snr"));
                LTE.put("LteRsrq", signalMap.get("LTE_RSRQ"));

            } else {

                LTE.put("IMSI", listLTEMain.get(getPosition("IMSI")).getDataValue());
                LTE.put("MEID", listLTEMain.get(getPosition("IMEI")).getDataValue());
                LTE.put("PhoneType", listLTEMain.get(getPosition("model")).getDataValue());
                LTE.put("OSVersion", listLTEMain.get(getPosition("firmware version")).getDataValue());
                LTE.put("BaseBand", listLTEMain.get(getPosition("system version")).getDataValue());
                LTE.put("Kernel", listLTEMain.get(getPosition("KernelVersion(liunx)")).getDataValue());
                LTE.put("InnerVersion", listLTEMain.get(getPosition("system version")).getDataValue());
                LTE.put("RamUsage", listLTEMain.get(getPosition("RAM")).getDataValue());
                LTE.put("CpuUsage", listLTEMain.get(getPosition("CPU")).getDataValue());
                LTE.put("Longitude", listLTEMain.get(getPosition("Lon")).getDataValue());
                LTE.put("Latitude", listLTEMain.get(getPosition("Lat")).getDataValue());
                LTE.put("LocationDesc", listLTEMain.get(getPosition("Addr")).getDataValue());
                LTE.put("CdmaSid", listLTEMain.get(getPosition("sid")).getDataValue());
                LTE.put("CdmaNid", listLTEMain.get(getPosition("nid")).getDataValue());
                LTE.put("CdmaBsid", listLTEMain.get(getPosition("CI")).getDataValue());
                LTE.put("CdmadBm", "");
                LTE.put("LteCi", listLTEMain.get(getPosition("LTE_CI")).getDataValue());
                LTE.put("LtePci", listLTEMain.get(getPosition("LTE_Pci")).getDataValue());
                LTE.put("LteTac", listLTEMain.get(getPosition("LTE_Tac")).getDataValue());
                LTE.put("LteRsrp", listLTEMain.get(getPosition("LTE_RSRP")).getDataValue() + "dBm");
                LTE.put("LteSinr", listLTEMain.get(getPosition("LTE_SINR")).getDataValue());
                LTE.put("InnerIP", listLTEMain.get(getPosition("ip")).getDataValue());
                LTE.put("OuterIP", "");
                LTE.put("Ecio", signalMap.get("EVDO_Ecio"));
                LTE.put("Snr", signalMap.get("EVDO_Snr"));
                LTE.put("LteRsrq",  signalMap.get("LTE_RSRQ"));
                Log.i("network", "LTE" + LTE.toString());
            }


            /*LTE.put("LTE_dBm", list.get(getPosition("LTE_dBm")).getDataValue() + "dBm");
            LTE.put("LTE_Signal", list.get(getPosition("LTE_Signal")).getDataValue() + "dBm");
            LTE.put("LTE_RSRQ", list.get(getPosition("LTE_RSRQ")).getDataValue());
            LTE.put("LTE_Mcc", list.get(getPosition("LTE_Mcc")).getDataValue());
            LTE.put("LTE_Mnc", list.get(getPosition("LTE_Mnc")).getDataValue());
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
