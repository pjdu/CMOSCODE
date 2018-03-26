package cn.com.chinaccs.datasite.main.ui.lte;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.adapter.WCAdapter;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
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
 * Created by Asky on 2016/4/1.
 */
public class LteSignalMainActivity extends BaseActivity {

    Context context;
    private View v;
    private LinearLayout layoutSg;
    private ListView lvInfo;
    private Button btnInfo;
    private WCAdapter adapter;
    private List<Collector> listLTEMain;
    private Map<String, String> signalMap;
    private Handler hr;
    private final String TAG = LteSignalMainActivity.class.getSimpleName();
    //在此页面获取到，接口对应的共有数据
    public static JSONObject LTE = new JSONObject();

    private BDGeoLocation geoGB;
    //增加测试的项目
    private InfoWifi wifi;
    private Integer level = 100;

    private Runnable rb =  new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (v != null) {
                reCollectInfo();
                singalChange();
                hr.postDelayed(rb, 30000);
            }
        }
    };
    private TelephonyManager tm;
    private PhoneStateListener celllistener = new PhoneStateListener() {

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
            reCollectInfo();
            singalChange();
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            // TODO Auto-generated method stub
            super.onDataConnectionStateChanged(state);
            reCollectInfo();
            singalChange();
        }
    };
    private View.OnClickListener lr = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_info_detail:
                    startActivity(new Intent(context, SignalInfoActivity.class));
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);

        context = getApplicationContext();
        hr = new Handler();
        tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(celllistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        listLTEMain = new ArrayList<Collector>();

        geoGB = MainApp.geoBD;
        findViews();
        buildListView();

        initToolbar("网络信令");
        // createFloatActionBar(this);

        btnInfo.setOnClickListener(lr);
        hr.postDelayed(rb, 30000);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        listLTEMain = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        hr.removeCallbacks(rb);
        tm.listen(celllistener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (v != null) {
            hr.postDelayed(rb, 5000);
            tm.listen(celllistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                    | PhoneStateListener.LISTEN_CELL_LOCATION
                    | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        }
    }

    private void buildListView() {
        reCollectInfo();
        adapter = new WCAdapter(context, listLTEMain);
        lvInfo.setAdapter(adapter);
    }

    private void reCollectInfo() {
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
        if (listLTEMain == null) {
            listLTEMain = new ArrayList<Collector>();
        }
        listLTEMain.clear();

        // add the location
        BDGeoLocation location = new BDGeoLocation(context);
        String addr = "";
        double lat = 0;
        double lon = 0;
        String province = null;
        String city = null;
        if (geoGB.locClient != null ) {
            geoGB.locClient.requestLocation();
            BDLocation loc = geoGB.location;
            province = loc.getProvince();
            city = loc.getCity();
            Log.d(TAG, "city province" + province + city);
            if (loc != null && loc.hasAddr()) {
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
        listLTEMain = sys.getSystemInfo(this, listLTEMain);
        listLTEMain.get(getPosition("Battery_free")).setDataValue(
                Integer.toString(level));
        //Sim卡和运营商信息
        InfoSim sim = new InfoSim(this);
        listLTEMain = sim.getSimInfo(listLTEMain);
        //基站信息
        InfoCell cell = new InfoCell(context);
        listLTEMain = cell.getCellInfo(listLTEMain);
        //Connectivity-Info
        InfoConnectivity ct = new InfoConnectivity(context);
        listLTEMain = ct.getConectiviyInfo(listLTEMain);
        wifi = new InfoWifi(this);
        wifi.getWifiInfo(listLTEMain);

/*        String netType;
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA){
            netType="CDMA";
        }else {
            netType="GSM";
        }*/
        String netType = AppNetWork.getConnectModel(context);
        if (netType.equals(AppNetWork.MODEL_LTE)) {
            netType = AppNetWork.MODEL_LTE;
        } else if (netType.equals(AppNetWork.MODEL_WIFI)) {
            netType = AppNetWork.MODEL_WIFI;
        } else {
            netType = "EVDO";
        }


        try {
            if (listLTEMain.get(getPosition("IMEI")).getDataValue().isEmpty()) {
                LTE.put("IMSI", "");
                LTE.put("MEID", "");
                LTE.put("PhoneType", listLTEMain.get(getPosition("model")).getDataValue());
                LTE.put("OSVersion", "");
                LTE.put("BaseBand", listLTEMain.get(getPosition("system version")).getDataValue());
                LTE.put("Kernel", "");
                LTE.put("InnerVersion", "");
                LTE.put("RamUsage", listLTEMain.get(getPosition("RAM")).getDataValue());
                LTE.put("CpuUsage", "");
                LTE.put("Longitude", listLTEMain.get(getPosition("Lon")).getDataValue());
                LTE.put("Latitude", listLTEMain.get(getPosition("Lat")).getDataValue());
                LTE.put("LocationDesc", listLTEMain.get(getPosition("Addr")).getDataValue());
                LTE.put("Province", province);
                LTE.put("City", city);
                LTE.put("Source", source);
                LTE.put("NetType", netType);
                LTE.put("APN", apn);
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
                LTE.put("Ecio", listLTEMain.get(getPosition("EVDO_Ecio")).getDataValue());
                LTE.put("Snr", listLTEMain.get(getPosition("EVDO_Snr")).getDataValue());
                LTE.put("LteRsrq", listLTEMain.get(getPosition("LTE_RSRQ")).getDataValue());
                Log.d(TAG, "IMEI" + listLTEMain.get(getPosition("IMEI")).getDataValue());
            } else {

                LTE.put("IMSI", listLTEMain.get(getPosition("IMSI")).getDataValue());
                LTE.put("MEID", listLTEMain.get(getPosition("IMEI")).getDataValue());
                LTE.put("PhoneType", listLTEMain.get(getPosition("model")).getDataValue());
                LTE.put("OSVersion", "");
                LTE.put("BaseBand", listLTEMain.get(getPosition("system version")).getDataValue());
                LTE.put("Kernel", "");
                LTE.put("InnerVersion", "");
                LTE.put("RamUsage", listLTEMain.get(getPosition("RAM")).getDataValue());
                LTE.put("CpuUsage", "");
                LTE.put("Longitude", listLTEMain.get(getPosition("Lon")).getDataValue());
                LTE.put("Latitude", listLTEMain.get(getPosition("Lat")).getDataValue());
                LTE.put("LocationDesc", listLTEMain.get(getPosition("Addr")).getDataValue());
                LTE.put("Province", province);
                LTE.put("City", city);
                LTE.put("Source", source);
                LTE.put("NetType", netType);
                LTE.put("APN", apn);
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
                LTE.put("Ecio", listLTEMain.get(getPosition("EVDO_Ecio")).getDataValue());
                LTE.put("Snr", listLTEMain.get(getPosition("EVDO_Snr")).getDataValue());
                LTE.put("LteRsrq", listLTEMain.get(getPosition("LTE_RSRQ")).getDataValue());
            }
            Log.d(TAG, LTE.toString());

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

    public void singalChange() {
        layoutSg.removeAllViews();
        layoutSg.invalidate();
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


            addSgView(getString(R.string.net_1x), signalMap.get("CDMA_Signal"));
            addSgView(getString(R.string.net_3G), signalMap.get("EVDO_Signal"));
        } else {
            listLTEMain.get(getPosition("GSM_Signal")).setDataValue(
                    signalMap.get("GSM_Signal"));
            String value = listLTEMain.get(getPosition("LTE_RSRP")).getDataValue();
            if (isNum(value)) {
                if (Integer.parseInt(value) < 0) {
                    addSgView(getString(R.string.net_4g),
                            listLTEMain.get(getPosition("LTE_RSRP")).getDataValue());
                }
            }

            //addSgView(getString(R.string.net_gsm), signalMap.get("GSM_Signal"));
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
            String value = listLTEMain.get(getPosition("LTE_RSRP")).getDataValue();
            if (Integer.parseInt(value) < 0) {
                addSgView(getString(R.string.net_4g),
                        listLTEMain.get(getPosition("LTE_RSRP")).getDataValue());
            }
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void addSgView(String type, String value) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_signal,
                null);
        TextView tv = (TextView) view.findViewById(R.id.tv_signal_cell);
        int divs[] = context.getResources().getIntArray(R.array.signal_div);
        int v = Integer.parseInt(value);
        if (v >= 0) {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.drawable.signal_6), null, null, null);
            value = "0";
        } else if (v < divs[0]) {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.drawable.signal_1), null, null, null);
            tv.setTextColor(getResources().getColor(R.color.red));
        } else if (v < divs[1]) {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.drawable.signal_2), null, null, null);
            tv.setTextColor(getResources().getColor(R.color.red));
        } else if (v < divs[2]) {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.drawable.signal_3), null, null, null);
            tv.setTextColor(getResources().getColor(R.color.yellow));
        } else if (v < divs[3]) {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.drawable.signal_4), null, null, null);
        } else if (v < divs[4]) {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.drawable.signal_5), null, null, null);
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                    .getDrawable(R.drawable.signal_6), null, null, null);
        }
        tv.setText(value + "dBm\n(" + type + ")");
        layoutSg.addView(view);
    }

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

    private void findViews() {
        layoutSg = (LinearLayout) findViewById(R.id.layout_signal);
        lvInfo = (ListView) findViewById(R.id.lv_info);
        btnInfo = (Button) findViewById(R.id.btn_info_detail);
    }

  /*  private void createFloatActionBar(Activity context) {
        // Set up the white button on the lower right corner
        // more or less with default parameter
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);
        final ImageView fabIconNew = new ImageView(context);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(context)
                .setBackgroundDrawable(R.drawable.button_action_blue_selector)
                .setContentView(fabIconNew)
                .build();
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(context);
        rLSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);
        rLSubBuilder.setLayoutParams(blueContentParams);
        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        //blueParams.setLayoutDirection(LayoutDirection.RTL);

        rLSubBuilder.setLayoutParams(blueParams);

        ImageView fileTest = new ImageView(this);
        ImageView webTest = new ImageView(this);
        ImageView videoTest = new ImageView(this);

        fileTest.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_speed_selected));
        webTest.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_busi_selected));
        videoTest.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video));


        SubActionButton fileSub = rLSubBuilder.setContentView(fileTest, blueContentParams).build();
        SubActionButton webSub = rLSubBuilder.setContentView(webTest, blueContentParams).build();
        SubActionButton videoSub = rLSubBuilder.setContentView(videoTest, blueContentParams).build();
        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(context)
                .addSubActionView(fileSub, fileSub.getLayoutParams().width, fileSub.getLayoutParams().height)
                .addSubActionView(webSub, webSub.getLayoutParams().width, webSub.getLayoutParams().height)
                .addSubActionView(videoSub, videoSub.getLayoutParams().width, webSub.getLayoutParams().height)
                .attachTo(rightLowerButton)
                .build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        fileSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LteSignalMainActivity.this, SpeedActivity.class);
                startActivity(intent);

            }
        });

        webSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LteSignalMainActivity.this, HttpTestActivity.class);
                startActivity(intent);

            }
        });

        videoSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LteSignalMainActivity.this, VideoTestActivity.class);
                startActivity(intent);

            }
        });
    }*/

    public static boolean isNum(String str) {

        try {
            new BigDecimal(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
