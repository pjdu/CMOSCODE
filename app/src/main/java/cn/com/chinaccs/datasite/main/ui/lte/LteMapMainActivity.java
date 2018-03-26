package cn.com.chinaccs.datasite.main.ui.lte;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.blankj.ALog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.functions.FuncGetLteSingalTestSiteList;
import cn.com.chinaccs.datasite.main.ui.utils.MapUtils;

/**
 * Created by Asky on 2016/3/31.
 */
public class LteMapMainActivity extends BaseActivity {

    private static final String TAG = App.LOG_TAG + "--" + LteMapMainActivity.class.getSimpleName();

    private Context context;
    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListener myLocationListener = new MyLocationListener();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    boolean isFirstLoc = true; // 是否首次定位
    private BDLocation mLocation;
    private ImageButton requestLocBtn;

    //private List<WeightedLatLng> signals;
    private List<LatLng> signals;

    private HeatMap heatMap;
    private boolean isDestroy;

    // 添加操作菜单
    private FloatingActionMenu mActionMenu;
    private FloatingActionButton mSignalBtn;
    private FloatingActionButton mBusiBtn;
    private FloatingActionButton mSpeedBtn;
    private FloatingActionButton mVideoBtn;

    private ProgressDialog progressDialog;
    // 实时场强
    private LinearLayout layoutSg;
    private Map<String, String> signalMap;
    private TelephonyManager tm;
    private PhoneStateListener cellListener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signal) {
            // TODO Auto-generated method stub
            super.onSignalStrengthsChanged(signal);
            Log.i(App.LOG_TAG, signal.toString());
            try {
                Method[] methods = android.telephony.SignalStrength.class
                        .getMethods();
                for (Method method : methods) {
                    if (method.getName().equals("getLteRsrp")) {
                        int sig = (Integer) method
                                .invoke(signal, new Object[]{});
                        signalMap.put("LTE_RSRP", Integer.toString(sig));
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
            if (tm != null && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                signalMap.put("CDMA_Signal",
                        Integer.toString(signal.getCdmaDbm()));
                signalMap.put("EVDO_Signal",
                        Integer.toString(signal.getEvdoDbm()));
            }
            signalChange();
        }

        @Override
        public void onCellLocationChanged(CellLocation location) {
            // TODO Auto-generated method stub
            super.onCellLocationChanged(location);
            Log.i(App.LOG_TAG, location.toString());
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            // TODO Auto-generated method stub
            super.onDataConnectionStateChanged(state);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lte_map_main);
        initToolbar("网络感知");
        context = LteMapMainActivity.this;
        // 初始化Phone状态监听事件
        signalMap = new HashMap<String, String>();
        tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(cellListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        this.initMapView();
        this.initView();
    }

    private void initMapView() {
        mMapView = (MapView) findViewById(R.id.mapView);
        //注册百度定位监听器
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(8.0f);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(msu);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        requestLocBtn = (ImageButton) findViewById(R.id.requestLocBtn);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        requestLocBtn.setImageResource(R.drawable.ic_icon_location);
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocBtn.setImageResource(R.drawable.ic_icon_follow);
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, null));
                        break;
                    case COMPASS:
                        requestLocBtn.setImageResource(R.drawable.ic_icon_location);
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, null));
                        break;
                    case FOLLOWING:
                        requestLocBtn.setImageResource(R.drawable.ic_icon_compass);
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, null));
                        break;
                    default:
                        break;
                }
            }
        };
        requestLocBtn.setOnClickListener(btnClickListener);

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void initView() {
        mActionMenu = (FloatingActionMenu) findViewById(R.id.action_menu);
        mActionMenu.setClosedOnTouchOutside(true);
        mVideoBtn = (FloatingActionButton) findViewById(R.id.video_action_button);
        mVideoBtn.setOnClickListener(clickListener);
        mBusiBtn = (FloatingActionButton) findViewById(R.id.busi_action_button);
        mBusiBtn.setOnClickListener(clickListener);
        mSpeedBtn = (FloatingActionButton) findViewById(R.id.speed_action_button);
        mSpeedBtn.setOnClickListener(clickListener);
        mSignalBtn = (FloatingActionButton) findViewById(R.id.signal_action_button);
        mSignalBtn.setOnClickListener(clickListener);
        layoutSg = (LinearLayout) findViewById(R.id.layout_signal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取当前州市的测试信息
     *
     * @param name
     * @param curLng
     */
    private void startGetSignTestList(String name, LatLng curLng) {
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.i(TAG,"热力地图"+output.toString());
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    return;
                }
                try {
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray data = json.getJSONArray("data");
                       // signals = new ArrayList<WeightedLatLng>();
                        signals=new ArrayList<LatLng>();
                        LatLng latLng;
                        for (int i = 0; i < data.length(); i++) {
                            String lon = data.getJSONArray(i).getString(0);
                            String lat = data.getJSONArray(i).getString(1);
                            String intensity = data.getJSONArray(i).getString(2);
                            // Logger.d(data.getJSONArray(i).toString());
                            if (!lon.equals("") && !lat.equals("")) {
                                latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                                latLng = MapUtils.gpsLnChangeToBdLn(latLng);
                               // signals.add(new WeightedLatLng(latLng, Double.valueOf(intensity)));
                                for (int i1=0;i1<intensity.length();i1++){
                                    signals.add(latLng);
                                }

                            }
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        addHeatMap();
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //是百度地图的坐标系
        FuncGetLteSingalTestSiteList func = new FuncGetLteSingalTestSiteList(context);
        func.getData(lr, 50, name, curLng);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        isDestroy = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ALog.d("super.handleMessage(msg);++++++++++++++++++");
            super.handleMessage(msg);
            ALog.d("super.handleMessage(msg);++++++++++++++++++!isDestroy" + !isDestroy);
            if (!isDestroy) {
                ALog.d("mBaiduMap.addHeatMap(heatMap)++++++++++++++++++");
                mBaiduMap.addHeatMap(heatMap);
            }
        }
    };

    /**
     * 添加热力地图
     */
    private void addHeatMap() {
        ALog.d("addHeatMap++++++++++++++++++");
        new Thread() {
            @Override
            public void run() {
                super.run();
                ALog.d("super.run()++++++++++++++++++signals.size()" + signals.size());
                if (signals != null && signals.size() > 0) {
                    ALog.d("new HeatMap.Builder().data(signals).build()++++++++++++++++++");
                   /* heatMap = new HeatMap.Builder()
                            .weightedData(signals).build();*/
                    heatMap=new HeatMap.Builder().data(signals).build();
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                if (progressDialog == null) {
                    progressDialog = CoConfig.progressDialog(context, "热力数据获取中....");
                    progressDialog.show();
                } else {
                    progressDialog.setMessage("热力数据获取中....");
                    progressDialog.show();
                }
                startGetSignTestList("昆明市", ll);
            }
            mLocation = location;
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.video_action_button:
                    intent = new Intent(LteMapMainActivity.this, VideoTestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.busi_action_button:
                    intent = new Intent(LteMapMainActivity.this, HttpTestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.speed_action_button:
                    intent = new Intent(LteMapMainActivity.this, SpeedActivity.class);
                    startActivity(intent);
                    break;
                case R.id.signal_action_button:
                    intent = new Intent(LteMapMainActivity.this, LteSignalMainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    /**
     * 信号实时场强变化后更新View
     */
    private void signalChange() {
        layoutSg.removeAllViews();
        layoutSg.invalidate();
        if (signalMap == null)
            return;
        if (tm != null && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            addSgView(getString(R.string.net_1x), signalMap.get("CDMA_Signal"));
            addSgView(getString(R.string.net_3G), signalMap.get("EVDO_Signal"));
        } else {
            // 移动、联通制式的手机不现实
            layoutSg.setVisibility(View.GONE);
        }
        if (signalMap.containsKey("LTE_RSRP")) {
            String value = signalMap.get("LTE_RSRP");
            if (Integer.parseInt(value) < 0) {
                addSgView(getString(R.string.net_4g), value);
            }
        }
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
}
