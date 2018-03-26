package cn.com.chinaccs.datasite.main.ui.cmos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetSignBsInfo;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.BaseStation;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.gps.GpsSatellite;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.widget.PromptDailog;


public class InspectSignActivity extends BaseActivity {
    private static final String TAG = App.LOG_TAG + "--" + InspectSignActivity.class.getSimpleName();
    public static final long TIME_SIGN_OUT_LOC = 3600000;
    public static final String SHARE_SIGN_LNG = "MAP_SHARE_SIGN_LNG";
    public static final String SHARE_SIGN_LAT = "MAP_SHARE_SIGN_LAT";
    public static final String SHARE_SIGN_DATE = "MAP_SHARE_SIGN_DATE";
    Context context;
    private GpsHandler gpsHandler;
    private ImageButton btnDel;
    private TextView tvState;
    private TextView tvLng;
    private TextView tvLat;
    private MapView mv;
    private Button btnSign;
    private BaiduMap mBaiduMap;
    public static BDGeoLocation geoGB;
    private Handler hr;
    private Location loc;
    private BDLocation location;

    private BDGeoLocation.BDLocationChangeListener listner;
    private ArrayList<BaseStation> bsList;
    private InfoWindow mInfoWindow;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_delete) {
            clearSign();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        gpsHandler = ((MainApp) getApplication()).gpsHandler;
        hr = new Handler();
        setContentView(R.layout.activity_inspect_sign);
        initToolbar("巡检签到");
        // 初始化地图位置
        geoGB = ((MainApp) getApplication()).geoBD;
        location = geoGB.location;
        if (geoGB != null && geoGB.locClient != null
                && geoGB.locClient.isStarted()) {
            geoGB.locClient.requestLocation();
        }
        this.findViews();
        this.initMapView();
        this.checkGPS();
    }

    private void checkGPS() {
        if (!App.isGpsOpen(context)) {
            PromptDailog prompt = new PromptDailog(context, null,
                    "GPS定位功能未开启，为了使用该功能，请打开手机GPS功能！", "设置",
                    new PromptDailog.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Intent i = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    });
            prompt.show();
        }
    }

    private OnClickListener lr = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_sign_now:
                    signNow();
                    break;

            }
        }
    };

    private void clearSign() {
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        share.edit().putFloat(SHARE_SIGN_LNG, 0).commit();
        share.edit().putFloat(SHARE_SIGN_LAT, 0).commit();
        share.edit().putLong(SHARE_SIGN_DATE, 0).commit();
        Toast.makeText(context, "签到记录已清除", Toast.LENGTH_LONG).show();
    }

    private void signNow() {
        if (loc != null) {
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            share.edit().putFloat(SHARE_SIGN_LNG, (float) loc.getLongitude())
                    .commit();
            share.edit().putFloat(SHARE_SIGN_LAT, (float) loc.getLatitude())
                    .commit();
            share.edit().putLong(SHARE_SIGN_DATE, new Date().getTime())
                    .commit();
            PromptDailog pdg = new PromptDailog(context, null,
                    "签到成功，已记录精确GPS位置信息", "退出",
                    new PromptDailog.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            //  this.dismiss();
                            // 签到点击返回后没反应修复 by wuhua
                            InspectSignActivity.this.finish();
                        }
                    });
            pdg.show();
            // getBSMapInfo();

        }
    }

    private void getBSMapInfo() {
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.i(App.LOG_TAG, output);
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray data = json.getJSONArray("data");
                        Log.e("bsstateInfo", "" + data.toString());
                        bsList = new ArrayList<BaseStation>();
                        BaseStation bs;
                        for (int i = 0; i < data.length(); i++) {
                            bs = new BaseStation(data.getJSONArray(i).getString(0),
                                    data.getJSONArray(i).getString(1),
                                    data.getJSONArray(i).getString(2),
                                    data.getJSONArray(i).getString(3),
                                    data.getJSONArray(i).getString(4),
                                    data.getJSONArray(i).getString(5),
                                    data.getJSONArray(i).getString(6),
                                    data.getJSONArray(i).getString(7));
                            bsList.add(bs);
                        }
                        showBSToMap();
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //是百度地图的坐标系
        FuncGetSignBsInfo func = new FuncGetSignBsInfo(context);
        //func.getData(lr, 2, String.valueOf(loc.getLongitude()), String.valueOf(loc.getLatitude()));
    }

    //在地图上显示基站信息
    private void showBSToMap() {
        for (BaseStation bs : bsList) {
            //获取来的数据是GPS原始数据
            LatLng sourceLatLng = new LatLng(Double.valueOf(bs.getBsLatitude()), Double.valueOf(bs.getBsLongitude()));
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(sourceLatLng);
            LatLng point = converter.convert();
            BitmapDescriptor bitmap = null;
            if (bs.getType().equals("critical")) {//紧急告警
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_major);
            } else if (bs.getType().equals("major")) {//严重告警
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_critical);
            } else if (bs.getType().equals("minor")) {//异常
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_minor);
            } else {//正常
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_nomall);
            }
            OverlayOptions option = new MarkerOptions()
                    .title(bs.getBsNames())
                    .position(point)
                    .icon(bitmap);
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            marker.setZIndex(Integer.valueOf(bs.getBsIds()));
            marker.setTitle("基站名称:" + bs.getBsNames() + "\n" + "基站信息:" + bs.getotherInfo());

        }
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                TextView button = new TextView(getApplicationContext());
                button.setClickable(true);
                button.setSingleLine(false);
                button.setHeight(400);
                button.setWidth(600);
                button.setBackgroundResource(R.color.white);
                final LatLng ll = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                p.y -= 47;//47
                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                for (BaseStation bs : bsList) {
//                    if (bs.getBsIds() == String.valueOf(marker.getZIndex())) {
                    button.setText(marker.getTitle());
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBaiduMap.hideInfoWindow();
                        }
                    });
//                    }
                }
                mInfoWindow = new InfoWindow(button, llInfo, 0);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    private void getMapLoc() {
        if (loc != null) {
            long ts = (new Date()).getTime() - loc.getTime();
            if (ts > GpsHandler.TIME_OUT_LOC) {
                tvState.setText("GPS定位中");
                btnSign.setText("GPS定位已过期，正在重新定位，请稍后..");
                btnSign.setEnabled(false);
            } else {
                tvState.setText("GPS定位成功");
                btnSign.setText("签到");
                btnSign.setEnabled(true);

                tvLng.setText(String.valueOf(loc.getLongitude()));
                tvLat.setText(String.valueOf(loc.getLatitude()));
                ////定义Maker坐标点并将原始坐标转为百度坐标
                LatLng sourceLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
// sourceLatLng待转换坐标
                converter.coord(sourceLatLng);
                LatLng desLatLng = converter.convert();
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_map_inspect);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(desLatLng)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);
            }
        } else {

            btnSign.setEnabled(false);
        }
    }

    private void findViews() {
        //btnDel = (ImageButton) findViewById(R.id.btn_delete);
        //btnDel.setOnClickListener(lr);
        tvState = (TextView) findViewById(R.id.tv_loc_state);
        btnSign = (Button) findViewById(R.id.btn_sign_now);
        btnSign.setOnClickListener(lr);
        tvLng = (TextView) findViewById(R.id.tv_lng);
        tvLat = (TextView) findViewById(R.id.tv_lat);
    }

    /**
     * 初始化地图
     */
    private void initMapView(){
        mv = (MapView) findViewById(R.id.mv_sgin);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap = mv.getMap();
        mBaiduMap.setMapStatus(msu);
        listner = new BDGeoLocation.BDLocationChangeListener() {
            @Override
            public void setLocation(BDLocation bdlocation) {
                Log.e(TAG, bdlocation.getLongitude() + "----" + bdlocation.getLatitude() + "----" + bdlocation.getAddrStr());
                // 定位信息更新同时更新地图位置
                updateMapsStatus(bdlocation);
            }
        };
        // 注册监听地图位置信息变化
        geoGB.addChangeListener(listner);
        // 首次加载获取定位信息更新地图
        updateMapsStatus(location);
    }
    /**
     * 更新地图状态
     *
     * @param location 当前百度地图属性
     */
    private void updateMapsStatus(BDLocation location) {
        if (location == null) {
            return;
        }
        MyLocationData data = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude()).build();
        mBaiduMap.setMyLocationData(data);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);

        addCellOverlays(latLng);
    }

    //添加中心点
    private void addCellOverlays(LatLng center) {
        if (center == null) {
            return;
        }
        if (mBaiduMap != null) {
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_location);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions optionpic = new MarkerOptions()
                    .position(center)
                    .icon(bitmap);

            //在地图上添加Marker，并显示  Stroke是边框
            mBaiduMap.addOverlay(optionpic);

            /*OverlayOptions ooCircle1 = new CircleOptions().fillColor(0x000000FF)
                    .center(center).stroke(new Stroke(1, Color.BLUE))

                    .radius(100);

            mBaiduMap.addOverlay(ooCircle1);

            OverlayOptions ooCircle2 = new CircleOptions().fillColor(0x000000FF)
                    .center(center).stroke(new Stroke(2, Color.BLUE))
                    .radius(102);
            mBaiduMap.addOverlay(ooCircle2);*/
        }

    }
    /**
     *
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        gpsHandler = null;
        mv.onDestroy();
        mv = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mv.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        geoGB.removeChangeListener(listner);
        // geoBD.locClient.stop();
        if (gpsHandler != null) {
            gpsHandler.stopGps();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启百度定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!geoGB.locClient.isStarted()) {
            geoGB.locClient.start();
        }
        //在这里开启GPS定位服务
        gpsHandler.startGps(GpsHandler.TIME_INTERVAL_GPS);
        hr.postDelayed(locChange, 1000);

    }

    private Runnable locChange = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (gpsHandler == null)
                return;
            loc = gpsHandler.getLastLocation();
            List<GpsSatellite> gss = gpsHandler.getGpsSatellites();
            if (loc != null && gss != null) {
                getMapLoc();
            } else {
                tvState.setText("GPS定位中");
                btnSign.setText("GPS未成功定位，签到功能暂不可用，请稍后..");
            }
            hr.postDelayed(locChange, 1000);
        }
    };
}
