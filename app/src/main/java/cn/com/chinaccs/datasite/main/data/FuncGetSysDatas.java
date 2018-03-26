package cn.com.chinaccs.datasite.main.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.baidu.location.BDLocation;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.bean.Collector;
import cn.com.chinaccs.datasite.main.bean.InfoCell;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppNetWork;
import cn.com.chinaccs.datasite.main.common.WGSTOGCJ02;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.util.AppScaffolding;


public class FuncGetSysDatas {

    private static final String TAG = "FuncGetSysDatas";
    private Context context;
    // public List<Collector> list;
    private TelephonyManager tm;
    public JSONObject common;
    private ConnectivityManager cm;
    private NetworkInfo info;
    private WifiManager wm;
    private SharedPreferences preferences;
    private String userPhone;

    public FuncGetSysDatas(Context context) {
        this.context = context;
        this.tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        preferences = context.getSharedPreferences(CoConfig.SHARE_TAG, 0);
        userPhone = preferences.getString(AppCheckLogin.SHARE_USER_ID, "");
    }

    /**
     *
     * @return
     */
    public JSONObject getSysJSON() {
        String imei = tm.getDeviceId(); // 获取终端的唯一标识
        imei = imei == null ? "" : imei;

        String imsi = tm.getSubscriberId(); // 获取用户唯一标识
        imsi = imsi == null ? "" : imsi;

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

        String NetworkOperatorName = tm.getNetworkOperatorName(); // 获取移动网络运营商的名称
        NetworkOperatorName = NetworkOperatorName == null ? ""
                : NetworkOperatorName;

        String NETWORK_TYPE = AppNetWork.getConnectModel(context);
        String Gps_state = AppScaffolding.isGpsOpen(context) + "";
        Calendar cr = Calendar.getInstance(Locale.CHINA);
        String dateStr = CoConfig.getDateToStr(cr, 0);
        String fv = Build.VERSION.RELEASE;
        String model = Build.MODEL;
        String sv = Build.DISPLAY;
        common = new JSONObject();
        try {
            NetworkOperatorName = URLEncoder.encode(NetworkOperatorName,
                    CoConfig.ENCODE_UTF8);
            JSONObject sim = new JSONObject();
            sim.put("dateItem", dateStr);
            sim.put("IMEI", imei);
            sim.put("IMSI", imsi);
            sim.put("PHONE_TYPE", phoneType);
            sim.put("NetworkOperatorName", NetworkOperatorName);
            sim.put("NETWORK_TYPE", NETWORK_TYPE);
            sim.put("Gps_state", Gps_state);
            sim.put("firmware version", fv);
            sim.put("model", model);
            sim.put("sv", sv);
            sim.put("phone", userPhone);
            common.put("sim", sim);

            getCellInfo();
            getLocation();
            getConn();

            // json解析
            // JSONTokener jsonParser = new JSONTokener(sim.toString());
            // JSONObject user = (JSONObject) jsonParser.nextValue();
            // user.getJSONObject("comon");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return common;
    }

    /**
     * 通过调用百度定位接口，获取经纬度的值
     */
    public void getLocation() {
        JSONObject location = new JSONObject();
        double latitude = 0.0;
        double longitude = 0.0;
        // String addr = "";

        if (MainApp.geoBD != null
                && MainApp.geoBD.locClient != null
                && MainApp.geoBD.locClient.isStarted()) {
            // 百度接口定位
            MainApp.geoBD.locClient.requestLocation();
            BDLocation bloc = MainApp.geoBD.location;
            if (bloc != null) {
                WGSTOGCJ02 wg = new WGSTOGCJ02();
                Map<String, Double> wgsloc = wg.gcj2wgs(bloc.getLongitude(),
                        bloc.getLatitude());
                Map<String, Double> locs = new HashMap<String, Double>();
                locs.put("lng", wgsloc.get("lon"));
                locs.put("lat", wgsloc.get("lat"));

                latitude = wgsloc.get("lat");
                longitude = wgsloc.get("lon");
            }
        }

        try {
            location.put("latitude", latitude);
            location.put("longitude", longitude);
            common.put("location", location);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取网络链接状态
     */
    public void getConn() {
        JSONObject conn = new JSONObject();
        info = cm.getActiveNetworkInfo();
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
        WifiInfo wi = wm.getConnectionInfo();
        String Mac = wi.getMacAddress() == null ? "" : wi.getMacAddress();
        String ip = getLocalIpAddress();
        ip = ip == null ? "" : ip;
        try {
            conn.put("dataState", dataState);
            conn.put("Mac", Mac);
            conn.put("ip", ip);
            if (info != null && info.isAvailable()) {
                String ConncetType = AppNetWork.getConnectModel(context);
                conn.put("ConncetType", ConncetType);
            }
            common.put("conn", conn);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取本地IP地址
     * @return
     */
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

    /**
     * 获取基站信息
     */
    public void getCellInfo() {
        JSONObject cell = new JSONObject();
        int phoneType = tm.getPhoneType();
        int networkType = tm.getNetworkType();
        if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {

            CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
            if (location != null) {
                int sid = location.getSystemId();// 系统标识 mobileNetworkCode
                int bid = location.getBaseStationId();// 基站小区号 cellId
                int nid = location.getNetworkId();// 网络标识 locationAreaCode
                double Latitude = (double) location.getBaseStationLatitude() / 14400;
                double Longitude = (double) location.getBaseStationLongitude() / 14400;
                java.text.DecimalFormat df = new java.text.DecimalFormat(
                        "#0.0000");
                String lat = new String(df.format(Latitude));
                String lng = new String(df.format(Longitude));
                try {
                    cell.put("sid", sid);
                    // 如果使用LTE，那么获取LTE基站。
                    if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {
                        InfoCell infoCell = new InfoCell(context);
                        List<Collector> lists = new ArrayList<Collector>();
                        infoCell.getCellInfoNew(true, true, lists);
                        // 第二个为获取的LTE_CI值
                        Collector collector = lists.get(1);
                        if (!collector.getDataValue().equals("")) {
                            cell.put("ci", collector.getDataValue());
                        } else {
                            cell.put("ci", bid);
                        }

                    } else {
                        cell.put("ci", bid);
                    }
                    cell.put("nid", nid);
                    cell.put("lat", lat);
                    cell.put("lng", lng);
                    common.put("cell", cell);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
            if (location != null) {
                int cid = location.getCid();
                int lac = location.getLac();
                int psc = location.getPsc();
                try {
                    cell.put("cid", cid);
                    cell.put("lac", lac);
                    cell.put("psc", psc);
                    common.put("cell", cell);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
