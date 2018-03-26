package cn.com.chinaccs.datasite.main.ui.lte;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;

import com.baidu.location.BDLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.adapter.WCAdapter;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.bean.Collector;
import cn.com.chinaccs.datasite.main.bean.InfoCell;
import cn.com.chinaccs.datasite.main.bean.InfoConnectivity;
import cn.com.chinaccs.datasite.main.bean.InfoSim;
import cn.com.chinaccs.datasite.main.bean.InfoSystem;
import cn.com.chinaccs.datasite.main.bean.InfoWifi;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.ui.MainApp;


/**
 * 网络信令
 *
 * @author fddi
 */
public class SignalInfoActivity extends BaseActivity {
    Context context;
    private ListView lvInfo;
    private WCAdapter adapter;
    private List<Collector> list;
    private Map<String, String> signalMap;
    private Handler hr;
    private static final String TAG="SignalInfoActivity";
    private BDGeoLocation geoGB;

    private Runnable rb = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.i(CoConfig.LOG_TAG, "Method Call : run()");
            reCollectInfo(false);
            singalChange();
            hr.postDelayed(rb, 30000);
        }
    };
    private TelephonyManager tm;
    private PhoneStateListener celllistener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signal) {
            // TODO Auto-generated method stub
            super.onSignalStrengthsChanged(signal);
            try {
                Method[] methods = android.telephony.SignalStrength.class
                        .getMethods();
                signalMap = new HashMap<String, String>();
                for (Method mthd : methods) {
                    if (mthd.getName().equals("getLteSignalStrength")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
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
                    } else if (mthd.getName().equals("getLteRssnr")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        signalMap.put("LTE_SINR", Integer.toString(sig / 10));
                    } else if (mthd.getName().equals("getGsmSignalStrength")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
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
				/*signalMap.put("CDMA_Signal",
						Integer.toString(signal.getCdmaDbm()));*/
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
            singalChange();
        }

        @Override
        public void onCellLocationChanged(CellLocation location) {
            // TODO Auto-generated method stub
            super.onCellLocationChanged(location);
            Log.i(CoConfig.LOG_TAG, "Method Call : onCellLocationChanged()");
            reCollectInfo(false);
            singalChange();
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            // TODO Auto-generated method stub
            super.onDataConnectionStateChanged(state);
            Log.i(CoConfig.LOG_TAG, "Method Call : onDataConnectionStateChanged()");
            reCollectInfo(false);
            singalChange();
        }
    };
    private InfoWifi wifi;
    private Integer level = 100;
    private IntentFilter filter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equalsIgnoreCase(
                    Intent.ACTION_BATTERY_CHANGED)) {
                level = intent.getIntExtra("level", 0);
                if (list != null) {
                    list.get(getPosition("Battery_free")).setDataValue(
                            Integer.toString(level));
                    adapter.setList(list);
                }
            }
            if (intent.getAction().equalsIgnoreCase(
                    WifiManager.NETWORK_STATE_CHANGED_ACTION)
                    || intent.getAction().equalsIgnoreCase(
                    WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                Log.i(CoConfig.LOG_TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION \n" +
                        "WifiManager.WIFI_STATE_CHANGED_ACTION");
                reCollectInfo(false);
                if (wifi != null) {
                    list = wifi.changeWifi(list);
                    adapter.setList(list);
                }
                singalChange();
                adapter.setList(list);
            }
            if (intent.getAction().equalsIgnoreCase(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.i(CoConfig.LOG_TAG, "WifiManager.SCAN_RESULTS_AVAILABLE_ACTION");
                if (wifi != null) {
                    list = wifi.changeWifi(list);
                    adapter.setList(list);
                }
            }
            if (intent.getAction().equalsIgnoreCase(
                    WifiManager.RSSI_CHANGED_ACTION)) {
                if (wifi != null) {
                    list = wifi.signalChange(list);
                    adapter.setList(list);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        geoGB = MainApp.geoBD;
        hr = new Handler();
        tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(celllistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        list = new ArrayList<Collector>();
        filter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);
        setContentView(R.layout.activity_signal_info);
        findViews();
        buildListView();
        // 显示标题
        initToolbar(getString(R.string.sg_detail));
        hr.postDelayed(rb, 30000);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        list = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        hr.removeCallbacks(rb);
        tm.listen(celllistener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        hr.postDelayed(rb, 5000);
        tm.listen(celllistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        registerReceiver(receiver, filter);
    }

    private void buildListView() {
        reCollectInfo(true);
        adapter = new WCAdapter(context, list);
        lvInfo.setAdapter(adapter);
    }
/*初始化详细信息列表的数据*/
    private void reCollectInfo(boolean isChanged) {
        Log.i(CoConfig.LOG_TAG, "Call method reCollectInfo");
        if (list == null) {
            list = new ArrayList<Collector>();
        } else {
            if (isChanged==true) {
                // add the location
                BDGeoLocation location = new BDGeoLocation(context);
                String addr = "";
                double lat = 0;
                double lon = 0;
                if (geoGB.locClient != null ) {
                    geoGB.locClient.requestLocation();
                    BDLocation loc = geoGB.location;
                    Log.d(TAG,"获取loc实例");
                    if (loc != null && loc.hasAddr()) {
                        addr = loc.getAddrStr();
                        lat = loc.getLatitude();
                        lon = loc.getLongitude();
                        Log.d(TAG,"loc实例不为空"+addr+lat+lon);
                    }
                }
                //坐标信息
                list.add(new Collector("坐标位置信息", "", 0, Collector.DATA_TYPE_TITLE));
                list.add(new Collector("Lat", lat + "", 0, Collector.DATA_TYPE_UPLOAD));
                list.add(new Collector("Lon", lon + "", 0, Collector.DATA_TYPE_UPLOAD));
                list.add(new Collector("Addr", addr, 0, Collector.DATA_TYPE_UPLOAD));
                Log.d(TAG,"坐标位置信息"+addr+lon+lat);
                //系统信息
                InfoSystem sys = new InfoSystem();
                list = sys.getSystemInfo(this, list);
                list.get(getPosition("Battery_free")).setDataValue(
                        Integer.toString(level));
                //Sim卡和运营商信息
                InfoSim sim = new InfoSim(this);
                list = sim.getSimInfo(list);
                //基站信息
                InfoCell cell = new InfoCell(this);
                list = cell.getCellInfo(list);
                adapter = new WCAdapter(this, list);
                //Connectivity-Info
                InfoConnectivity ct = new InfoConnectivity(this);
                list = ct.getConectiviyInfo(list);
                wifi = new InfoWifi(this);
                wifi.getWifiInfo(list);
                if (adapter != null) {
                    adapter.setList(list);
                    Log.d(TAG,"设置系统信息");
                }
                Log.d(TAG,"执行信息获取");
            }
        }
    }

    private void singalChange() {
        if (signalMap == null)
            return;
        if (list == null)
            return;
        if (tm != null && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            list.get(getPosition("CDMA_Signal")).setDataValue(
                    signalMap.get("CDMA_Signal"));
            list.get(getPosition("CDMA_Ecio")).setDataValue(
                    signalMap.get("CDMA_Ecio"));
            list.get(getPosition("EVDO_Signal")).setDataValue(
                    signalMap.get("EVDO_Signal"));
            list.get(getPosition("EVDO_Ecio")).setDataValue(
                    signalMap.get("EVDO_Ecio"));
            list.get(getPosition("EVDO_Snr")).setDataValue(
                    signalMap.get("EVDO_Snr"));
            list.get(getPosition("Asu_level")).setDataValue(signalMap.get("Asu_level"));
        } else {
            list.get(getPosition("Signal")).setDataValue(
                    signalMap.get("Signal"));
        }
        if (signalMap.containsKey("LTE_Signal")
                && signalMap.containsKey("LTE_RSRP")
                && signalMap.containsKey("LTE_RSRQ")
                && signalMap.containsKey("LTE_SINR")) {
            list.get(getPosition("LTE_Signal")).setDataValue(
                    signalMap.get("LTE_Signal"));
            list.get(getPosition("LTE_RSRP")).setDataValue(
                    signalMap.get("LTE_RSRP"));
            list.get(getPosition("LTE_RSRQ")).setDataValue(
                    signalMap.get("LTE_RSRQ"));
            list.get(getPosition("LTE_SINR")).setDataValue(
                    signalMap.get("LTE_SINR"));
        }
        if (adapter != null)
            adapter.setList(list);
    }

    private int getPosition(String name) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (name.equalsIgnoreCase(list.get(i).getDataName())) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void findViews() {
        lvInfo = (ListView) findViewById(R.id.lv_info);
    }

}
