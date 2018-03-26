package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncBSLocSearch;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetSignBsInfo;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.BaseStation;
import cn.com.chinaccs.datasite.main.ui.MainApp;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;
import com.baidu.mapapi.utils.CoordinateConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Asky on 15/10/13.
 */
public class BSDataSiteInfoActivity extends Activity implements View.OnClickListener, OnGetGeoCoderResultListener {
    public static final long TIME_SIGN_OUT_LOC = 3600000;
    public static final String SHARE_SIGN_LNG = "MAP_SHARE_SIGN_LNG";
    public static final String SHARE_SIGN_LAT = "MAP_SHARE_SIGN_LAT";
    public static final String SHARE_SIGN_DATE = "MAP_SHARE_SIGN_DATE";
    private final int moveDist = 4;
    Context context;
    private TextView tvState;
    private TextView tvLng;
    private TextView tvLat;
    private MapView mv;
    private BaiduMap mBaiduMap;
    private double mLatitude;
    private double mLongtitude;
    public static BDGeoLocation geoGB;
    // 添加重置到当前位置按钮
    private ImageButton centerBtn;
    // 添加搜索基站地图
    private EditText bsNameEt;
    private Button bsSearchBtn;
    // 搜索模块
    private GeoCoder mSearch = null;
    private boolean datasiteSearch = false;
    /**
     * 当前的精度
     */
    private boolean isFristIn;

    private BDGeoLocation.BDLocationChangeListener listner;
    private ArrayList<BaseStation> bsList;
    private InfoWindow mInfoWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        isFristIn = true;
        setContentView(R.layout.activity_bs_datasite_info);
        this.findViews();
        //初始化位置为昆明市政府
        geoGB = ((MainApp) getApplication()).geoBD;
        if (!geoGB.locClient.isStarted()) {
            geoGB.locClient.start();
        }
        //注册百度定位监听器
        geoGB.addChangeListener(listner);

        // 初始化搜索模块、注册监听事件
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    private void getBSMapInfo(final LatLng latLng) {
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
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
                        showBSToMap(latLng);
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
       // func.getData(lr, moveDist, String.valueOf(latLng.longitude), String.valueOf(latLng.latitude));
    }

    //在地图上显示基站信息
    private void showBSToMap(LatLng latLng) {
        Log.i(App.LOG_TAG, "showBSToMap");
        // 用户退出时、地图被销毁
        if (mv == null) {
            return;
        }
        mBaiduMap.clear();
        if (latLng != null) {
            //获取来的数据是GPS原始数据
            CoordinateConverter converter1 = new CoordinateConverter();
            converter1.from(CoordinateConverter.CoordType.GPS);
            ////定义Maker坐标点并将原始坐标转为百度坐标
            // sourceLatLng待转换坐标
            converter1.coord(latLng);
            LatLng desLatLng1 = converter1.convert();
            if (datasiteSearch) {
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(desLatLng1));
                desLatLng1 = new LatLng(desLatLng1.latitude + 0.0008, desLatLng1.longitude + 0.00009);
            }
            Marker marker = (Marker) mBaiduMap.addOverlay(new MarkerOptions().position(desLatLng1).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location)));
            marker.setTitle("中心点标记");

        } else {
            if (datasiteSearch) {
                Toast.makeText(context, "未找到您查询的基站，请从新查询....", Toast.LENGTH_LONG).show();
            }
        }
        for (BaseStation bs : bsList) {
            // 基站经纬度传回来的存在""
            if (bs.getBsLatitude().equals("") || bs.getBsLongitude().equals("")) {
                return;
            }
            //获取来的数据是GPS原始数据
            LatLng sourceLatLng = new LatLng(Double.valueOf(bs.getBsLatitude()), Double.valueOf(bs.getBsLongitude()));
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            ////定义Maker坐标点并将原始坐标转为百度坐标
            // sourceLatLng待转换坐标
            converter.coord(sourceLatLng);
            LatLng desLatLng = converter.convert();
            BitmapDescriptor bitmap = null;
            if (bs.getType().equals("critical")) {//紧急告警
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_critical);
            } else if (bs.getType().equals("major")) {//严重告警
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_major);
            } else if (bs.getType().equals("minor")) {//异常
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_minor);
            } else {//正常
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_nomall);
            }
            OverlayOptions option = new MarkerOptions()
                    .title(bs.getBsNames())
                    .position(desLatLng)
                    .icon(bitmap);
            // 用户退出时、地图被销毁
            if (mv == null) {
                return;
            }
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            // markerList.add(marker);
            marker.setZIndex(Integer.valueOf(bs.getBsIds()));
            marker.setTitle("基站名称:" + bs.getBsNames() + "\n" + "基站信息:" + bs.getotherInfo());

        }
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                View view = LayoutInflater.from(context).inflate(R.layout.marker_title, null); //自定义气泡形状
                TextView title = (TextView) view.findViewById(R.id.marker_title);
                double latitude, longitude;
                latitude = marker.getPosition().latitude;
                longitude = marker.getPosition().longitude;
                final LatLng pt = new LatLng(latitude + 0.0004, longitude + 0.00005);

                for (BaseStation bs : bsList) {
//                    if (bs.getBsIds() == String.valueOf(marker.getZIndex())) {
                    title.setText(marker.getTitle());
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBaiduMap.hideInfoWindow();
                        }
                    });
//                    }
                }
                mInfoWindow = new InfoWindow(view, pt, 0);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    /**
     * 百度坐标和GPS坐标转换在很近的距离时偏差非常接近。
     * 假设你有百度坐标：x1=116.397428，y1=39.90923
     * 把这个坐标当成GPS坐标，通过接口获得他的百度坐标：x2=116.41004950566，y2=39.916979519873
     * 通过计算就可以得到GPS的坐标：
     * x = 2*x1-x2，y = 2*y1-y2
     * x=116.38480649434001
     * y=39.901480480127
     *
     * @param sourceLatLng
     */
    private void getMapLoc(LatLng sourceLatLng) {
        Log.d(App.LOG_TAG, "getMapLoc");
        // Log.d(App.LOG_TAG, "longitude: " + sourceLatLng.longitude + "  latitude: " + sourceLatLng.latitude);
        if (sourceLatLng != null) {
            tvState.setText("定位成功");
            tvLng.setText(String.valueOf(sourceLatLng.longitude));
            tvLat.setText(String.valueOf(sourceLatLng.latitude));
            //将百度坐标转为GPS坐标
            //把百度坐标当成一个GPS坐标
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(sourceLatLng);
            //模拟纠偏得到d百度地图坐标
            LatLng desLatLng = converter.convert();
            // Log.d(App.LOG_TAG, "longitude: " + desLatLng.longitude + "  latitude: " + desLatLng.latitude);
            double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
            double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
            LatLng centerLatLng = new LatLng(latitude, longitude);
            // Log.d(App.LOG_TAG, "longitude: " + centerLatLng.longitude + "  latitude: " + centerLatLng.latitude);
            //构建Marker图标
            //  BitmapDescriptor bitmap = BitmapDescriptorFactory
            //         .fromResource(R.drawable.ic_map_inspect);
            //构建MarkerOption，用于在地图上添加Marker
            // OverlayOptions option = new MarkerOptions()
            //      .position(sourceLatLng)
            //       .icon(bitmap);
            //在地图上添加Marker，并显示
            // mBaiduMap.addOverlay(option);
            getBSMapInfo(centerLatLng);

        } else {
            tvState.setText("定位中");
        }
    }

    private void findViews() {
        tvState = (TextView) findViewById(R.id.tv_loc_state);
        mv = (MapView) findViewById(R.id.mv_sgin);
        tvLng = (TextView) findViewById(R.id.tv_lng);
        tvLat = (TextView) findViewById(R.id.tv_lat);
        centerBtn = (ImageButton) findViewById(R.id.btn_mapCenter);
        centerBtn.setOnClickListener(this);
        bsNameEt = (EditText) findViewById(R.id.et_loc_name);
        bsSearchBtn = (Button) findViewById(R.id.btn_datasite_query);
        bsSearchBtn.setOnClickListener(this);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap = mv.getMap();
        mBaiduMap.setMapStatus(msu);
        listner = new BDGeoLocation.BDLocationChangeListener() {
            @Override
            public void setLocation(BDLocation bdlocation) {
                Log.e("InspectiSianActivity", "is on back");
                MyLocationData data = new MyLocationData.Builder()
                        .accuracy(bdlocation.getRadius())
                        .longitude(bdlocation.getLongitude())
                        .latitude(bdlocation.getLatitude()).build();
                mBaiduMap.setMyLocationData(data);

                if (isFristIn) {
                    mLongtitude = bdlocation.getLongitude();
                    mLatitude = bdlocation.getLatitude();
                    LatLng latLng = new LatLng(mLatitude, mLongtitude);
                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                    mBaiduMap.animateMapStatus(msu);
                    isFristIn = false;
                }
            }
        };
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng latlng = mBaiduMap.getMapStatus().target;
                if (latlng != null) {
                    getMapLoc(latlng);
                } else {
                    tvState.setText("定位中");
                }
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            LatLng startLng, finishLng;

            /**
             * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
             * @param mapStatus 地图状态改变开始时的地图状态
             */
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                startLng = mapStatus.target;
            }

            /**
             * 地图状态变化中
             * @param mapStatus 当前地图状态
             */
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            /**
             * 地图状态改变结束
             * @param mapStatus 地图状态改变结束后的地图状态
             */
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (datasiteSearch) {
                    return;
                }
                LatLng latlng = mapStatus.target;
                // 滑动搜索
                finishLng = mapStatus.target;
                if (startLng.latitude != finishLng.latitude
                        || startLng.longitude != finishLng.longitude) {
                    Projection ject = mBaiduMap.getProjection();
                    Point startPoint = ject.toScreenLocation(startLng);
                    Point finishPoint = ject.toScreenLocation(finishLng);
                    double x = Math.abs(finishPoint.x - startPoint.x);
                    double y = Math.abs(finishPoint.y - startPoint.y);
                    if (x > moveDist || y > moveDist) {
                        //在这处理滑动
                        if (latlng != null) {
                            getMapLoc(latlng);
                        } else {
                            tvState.setText("定位中");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e(App.LOG_TAG, "ondestroy");
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启百度定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!geoGB.locClient.isStarted()) {
            geoGB.locClient.start();
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mapCenter:
                backToCenter();
                break;
            case R.id.btn_datasite_query:
                if (bsNameEt.getText().toString().equals("")) {
                    Toast.makeText(context, "请输入你要查看地域名称或者基站名称....", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(context)
                        .setTitle("请选择")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setSingleChoiceItems(new String[]{"选择地域名称", "选择基站名称"}, 0, chooseListener)
                        .setPositiveButton("确定", chooseListener)
                        .show();
                break;
        }
    }

    private DialogInterface.OnClickListener chooseListener = new DialogInterface.OnClickListener() {
        private int index = 0;

        public void onClick(DialogInterface dialog, int which) {
            Log.d(App.LOG_TAG, "which" + which);
            if (which >= 0) {
                index = which;
            } else {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    if (index == 0) {
                        datasiteSearch = false;
                        startMapLocation();
                    } else if (index == 1) {
                        datasiteSearch = true;
                        startDataSiteLocation();
                    }
                }
            }
        }
    };

    // 回到定位点并获取数据
    private void backToCenter() {
        datasiteSearch = false;
        mBaiduMap.clear();
        if (geoGB.location != null) {
            LatLng latLng = new LatLng(geoGB.location.getLatitude(), geoGB.location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.setMapStatus(update);
            if (latLng != null) {
                getMapLoc(latLng);
            }
        }
    }

    // 地域定位
    private void startMapLocation() {
        // Geo搜索
        if (bsNameEt != null) {
            mSearch.geocode(new GeoCodeOption().city("").address(bsNameEt.getText().toString()));
        }
    }

    // 基站定位
    private void startDataSiteLocation() {
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Log.i(App.LOG_TAG, output);
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray data = json.getJSONArray("data");
                        Log.e("bsstateInfo", "" + data.toString());
                        bsList = new ArrayList<BaseStation>();
                        BaseStation bs;
                        BaseStation bsTemp = new BaseStation();
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
                            if (i == data.length() / 2) {
                                Log.i(App.LOG_TAG, "I : " + i + "------data.length : " + data.length());
                                bsTemp = bs;
                            }
                        }
                        // 基站经纬度传回来的存在""
                        if (bsTemp.getBsLatitude().equals("") || bsTemp.getBsLongitude().equals("")) {
                            showBSToMap(null);
                            return;
                        }
                        //获取来的数据是GPS原始数据
                        LatLng sourceLatLng = new LatLng(Double.valueOf(bsTemp.getBsLatitude()), Double.valueOf(bsTemp.getBsLongitude()));
                        showBSToMap(sourceLatLng);
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //是百度地图的坐标系
        FuncBSLocSearch func = new FuncBSLocSearch(context);
        func.getData(lr, bsNameEt.getText().toString().trim());
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(context, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        mBaiduMap.clear();
        LatLng latLng = geoCodeResult.getLocation();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(update);
        if (latLng != null) {
            getMapLoc(latLng);
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(context, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        // mBaiduMap.clear();
        LatLng latLng = reverseGeoCodeResult.getLocation();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(update);
//        if (latLng != null) {
//            getMapLoc(latLng);
//        }
    }
}
