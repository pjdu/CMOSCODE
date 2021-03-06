package cn.com.chinaccs.datasite.main.ui.cmos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.ui.functions.FuncGetPlanSiteList;
import cn.com.chinaccs.datasite.main.ui.models.BaseSiteBean;

/**
 * 入网申请功能
 * Created by Asky on 2016/3/31.
 */
public class PhysicalSiteApplyActivity extends BaseActivity {

    private static final String TAG = PhysicalSiteApplyActivity.class.getSimpleName();
    private Context context;
    private SearchView mSearchView;
    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListenner myLocationListenner = new MyLocationListenner();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private ArrayList<BaseSiteBean> bsList;
    private ArrayList<BaseSiteBean> bsListFail;
    private InfoWindow mInfoWindow;
    boolean isFirstLoc = true; // 是否首次定位
    private BDLocation mLocation;
    private ImageButton requestLocBtn;
    private LatLng mLatLng;

    // 需随时更新地图上新建基站位子
    private Marker newBaseMarker;
    private Circle newBaseCircle1;
    private Circle newBaseCircle2;

    //百度地图定位位置信息
    //BDLocation location;
    private BDGeoLocation geoGB;
    //百度坐标
    private double longtitude;
    private double latitude;
    //转换后的gps
    private double longitudeGPS;
    private double latitudeGPS;

    private LatLng sourceLatLng;
    private LatLng gpsLatLng;

    //点击mark后上传状态
    private View view;
    private Button btn_physicalCheck;

    //基站名称和ID用于查询详细信息
    private String bsName;
    private String bsID;

    // 列表方式查看站址信息
    private ImageButton switchViewBtn;

    //查询后跳转到最后一个基站
    private final int QUERY = 1;
    private final int SHOW = 0;
    private ArrayList<BaseSiteBean> allList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_site);
        initToolbar("入网申请");
        context = PhysicalSiteApplyActivity.this;
        this.initMapView();
        this.buildCellBaseStation();
        geoGB = ((MainApp) getApplication()).geoBD;
        mLocation = geoGB.location;
        longtitude = mLocation.getLongitude();
        latitude = mLocation.getLatitude();
        initPhycal_new_check_apply_pay();
        sourceLatLng = new LatLng(latitude, longtitude);
        getMapLoc(sourceLatLng);
        getBSPlanSiteList(gpsLatLng, "", SHOW);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(sourceLatLng);
        mBaiduMap.setMapStatus(update);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMapLoc(sourceLatLng);
        getBSPlanSiteList(gpsLatLng, "", SHOW);
    }

    //转换百度地图坐标
    private void getMapLoc(final LatLng sourceLatLng) {
        Log.d(App.LOG_TAG, "getMapLoc");
        // Log.d(App.LOG_TAG, "longitude: " + sourceLatLng.longitude + "  latitude: " + sourceLatLng.latitude);
        if (sourceLatLng != null) {
            Toast.makeText(this, "定位成功", Toast.LENGTH_SHORT).show();
            // tvLng.setText(String.valueOf(sourceLatLng.longitude));
            // tvLat.setText(String.valueOf(sourceLatLng.latitude));
            //将百度坐标转为GPS坐标
            //把百度坐标当成一个GPS坐标
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(sourceLatLng);
            //模拟纠偏得到d百度地图坐标
            LatLng desLatLng = converter.convert();
            // Log.d(App.LOG_TAG, "longitude: " + desLatLng.longitude + "  latitude: " + desLatLng.latitude);
            longitudeGPS = 2 * sourceLatLng.longitude - desLatLng.longitude;
            latitudeGPS = 2 * sourceLatLng.latitude - desLatLng.latitude;
            //LatLng centerLatLng = new LatLng(latitude, longitude);
            //getBSMapInfo(centerLatLng, blindArea, blindCounty);
            gpsLatLng = new LatLng(latitudeGPS, longitudeGPS);

        } else {
            Toast.makeText(this, "定位中", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPhycal_new_check_apply_pay() {
        view = LayoutInflater.from(context).inflate(R.layout.physical_site_info_window, null); //自定义气泡形状
        btn_physicalCheck = (Button) view.findViewById(R.id.btn_physical_site_edit);
        btn_physicalCheck.setText("入网申请");
    }

    private void initMapView() {
        mMapView = (MapView) findViewById(R.id.mapView);
        //注册百度定位监听器
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(msu);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 切换至列表形式
        switchViewBtn = (ImageButton) findViewById(R.id.switchViewBtn);
        switchViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 百度地图坐标
                if (mLocation == null) {
                    Toast.makeText(context, "正在获取位子信息，请稍后....", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, PhysicalSiteListActivity.class);
                // 3 表示当前为入网申请
                intent.putExtra("newState", "3");
                intent.putExtra("longitude", mLocation.getLongitude());
                intent.putExtra("latitude", mLocation.getLatitude());
                startActivity(intent);
            }
        });

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
        mLocClient.registerLocationListener(myLocationListenner);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.ab_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getBSPlanSiteList(mLatLng, query, QUERY);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    getBSPlanSiteList(mLatLng,"",SHOW);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    /**
     * 当前接入基站
     */
    private void buildCellBaseStation() {
        AppCDMABaseStation.CDMABaseStation bs = AppCDMABaseStation.getInsertBaseStation(context);
        if (bs == null) {
            return;
        }
        Double cellLac = Double.valueOf(bs.getLatitude());
        Double cellLng = Double.valueOf(bs.getLongitude());

        if (cellLac == null || cellLng == null) {
            return;
        }
        if (mBaiduMap != null) {
            LatLng point = new LatLng(cellLac, cellLng);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_cell);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions optionpic = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);

            //在地图上添加Marker，并显示  Stroke是边框
            mBaiduMap.addOverlay(optionpic);

            OverlayOptions ooCircle1 = new CircleOptions().fillColor(0x000000FF)
                    .center(point).stroke(new Stroke(1, Color.BLACK))

                    .radius(1000);

            mBaiduMap.addOverlay(ooCircle1);

            OverlayOptions ooCircle2 = new CircleOptions().fillColor(0x000000FF)
                    .center(point).stroke(new Stroke(2, R.color.colorPrimary))
                    .radius(1002);
            mBaiduMap.addOverlay(ooCircle2);

        }
    }

    /**
     *
     */
    private void addNewCellBaseStation() {
        if (mBaiduMap != null && mLocation != null) {
            if (newBaseMarker != null) {
                newBaseMarker.remove();
            }
            if (newBaseCircle1 != null) {
                newBaseCircle1.remove();
            }
            if (newBaseCircle2 != null) {
                newBaseCircle2.remove();
            }
            LatLng point = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_assets_new);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions optionpic = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);

            //在地图上添加Marker，并显示  Stroke是边框
            newBaseMarker = (Marker) mBaiduMap.addOverlay(optionpic);

            OverlayOptions ooCircle1 = new CircleOptions().fillColor(0x000000FF)
                    .center(point).stroke(new Stroke(1, Color.BLACK))

                    .radius(1000);

            newBaseCircle1 = (Circle) mBaiduMap.addOverlay(ooCircle1);

            OverlayOptions ooCircle2 = new CircleOptions().fillColor(0x000000FF)
                    .center(point).stroke(new Stroke(2, R.color.colorPrimary))
                    .radius(1002);
            newBaseCircle2 = (Circle) mBaiduMap.addOverlay(ooCircle2);

        }

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

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
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                // getBSPlanSiteList(ll, "");
            }
            mLocation = location;
            mLatLng = ll;
            // 更新基站位置
            // addNewCellBaseStation();
        }
    }

    // 查询站点规划的基站
    private void getBSPlanSiteList(final LatLng latLng, final String name, final int type) {
        if (latLng == null) {
            Toast.makeText(context, "无法获取到经纬度,请稍后再试....", Toast.LENGTH_LONG).show();
            return;
        }
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
                    Log.d(TAG, output);
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray data = json.getJSONArray("data");
                        Log.i(TAG, "data" + data.toString());
                        bsList = new ArrayList<BaseSiteBean>();
                        bsListFail = new ArrayList<BaseSiteBean>();
                        allList = new ArrayList<BaseSiteBean>();
                        BaseSiteBean bs;
                        BaseSiteBean bsTemp = new BaseSiteBean();
                        for (int i = 0; i < data.length(); i++) {
                            bs = new BaseSiteBean(data.getJSONArray(i).getString(0), data.getJSONArray(i).getString(1),
                                    data.getJSONArray(i).getString(2), data.getJSONArray(i).getString(3), data.getJSONArray(i).getString(4),
                                    data.getJSONArray(i).getString(5)
                            );
                            allList.add(bs);
                            if (bs.getBsType().equals("3")) {
                                bsList.add(bs);
                            } else if (bs.getBsType().equals("4")) {
                                bsListFail.add(bs);
                            }

                        }
                        if (type == SHOW) {
                            showBSToMap(SHOW);
                        } else {
                            showBSToMap(QUERY);
                        }
                        Log.i(TAG, bsList.toString());
                        Log.i(TAG, "getBSPlanSiteList");
                        //获取来的数据是GPS原始数据
                        // LatLng sourceLatLng = new LatLng(Double.valueOf(bsTemp.getBsLatitude()), Double.valueOf(bsTemp.getBsLongitude()));
                        //showBSToMap(sourceLatLng);

                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        FuncGetPlanSiteList func = new FuncGetPlanSiteList(context);
        //func.getData(lr, name, MapUtils.changeBLocToGPSLoc(latLng));
        // func.getData(lr, name, latLng);
    }

    private void upLoad(int type, int ids) {
        OnGetDataFinishedListener onGetDataFinishedListener = new OnGetDataFinishedListener() {
            @Override
            public void onFinished(String output) {
                JSONObject responseJson = null;
                String result = null;
                String msg = null;
                Log.i(TAG, "output:" + output);
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    try {
                        responseJson = new JSONObject(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        result = responseJson.getString("result");
                        msg = responseJson.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (result.equals("1")) {
                        Toast.makeText(context, msg,
                                Toast.LENGTH_SHORT).show();
                        getMapLoc(sourceLatLng);
                        getBSPlanSiteList(gpsLatLng, "", SHOW);
                    } else {
                        Toast.makeText(context, "验收失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

        };
        FuncGetPlanSiteList funcGetPlanSiteList = new FuncGetPlanSiteList(context);
        funcGetPlanSiteList.upLoadType(onGetDataFinishedListener, type, ids);
    }

    //在地图上显示基站信息
    private void showBSToMap(int type) {
        Log.i(TAG, "showBSToMap");
        // 用户退出时、地图被销毁
        if (mMapView == null) {
            return;
        }
        mBaiduMap.clear();
        //if (latLng != null) {
        //获取来的数据是GPS原始数据
                /*CoordinateConverter converter1 = new CoordinateConverter();
                converter1.from(CoordinateConverter.CoordType.GPS);
                ////定义Maker坐标点并将原始坐标转为百度坐标
                // sourceLatLng待转换坐标
                converter1.coord(latLng);
                LatLng desLatLng1 = converter1.convert();
                Log.i(TAG, desLatLng1.toString());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(desLatLng1);
                mBaiduMap.setMapStatus(update);*/
        /// }
        for (BaseSiteBean bs : bsList) {
            // 基站经纬度传回来的存在""
            if (bs.getBsLatitude().equals("") || bs.getBsLongitude().equals("")) {
                continue;
            }
            //获取来的数据是GPS原始数据
            LatLng sourceLatLng = new LatLng(Double.valueOf(bs.getBsLatitude()), Double.valueOf(bs.getBsLongitude()));
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            ////定义Maker坐标点并将原始坐标转为百度坐标
            // sourceLatLng待转换坐标
            converter.coord(sourceLatLng);
            LatLng desLatLng = converter.convert();
            // MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(desLatLng);
            //mBaiduMap.setMapStatus(update);
            Log.i(TAG, desLatLng.toString());
            BitmapDescriptor bitmap = null;

            bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.bsstate_new_applying);
            OverlayOptions option = new MarkerOptions()
                    .title(bs.getBsNames())
                    .position(desLatLng)
                    .icon(bitmap);
            // 用户退出时、地图被销毁
            if (mMapView == null) {
                return;
            }
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            //markerList.add(marker);
            bsName = bs.getBsNames();
            bsID = bs.getBsIds();
            Bundle bundle = new Bundle();
            bundle.putString("name", bsName);
            bundle.putString("id", bsID);
            marker.setExtraInfo(bundle);
            marker.setZIndex(Integer.valueOf(bs.getBsIds()));
            marker.setTitle("物理站址名称:" + bs.getBsNames() + "\n" + "物理站址状态:" + "已站址验收待入网申请");

        }
        if (!bsListFail.isEmpty()) {
            for (BaseSiteBean bs : bsList) {
                // 基站经纬度传回来的存在""
                if (bs.getBsLatitude().equals("") || bs.getBsLongitude().equals("")) {
                    continue;
                }
                //获取来的数据是GPS原始数据
                LatLng sourceLatLng = new LatLng(Double.valueOf(bs.getBsLatitude()), Double.valueOf(bs.getBsLongitude()));
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                ////定义Maker坐标点并将原始坐标转为百度坐标
                // sourceLatLng待转换坐标
                converter.coord(sourceLatLng);
                LatLng desLatLng = converter.convert();
                // MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(desLatLng);
                //mBaiduMap.setMapStatus(update);
                Log.i(TAG, desLatLng.toString());
                BitmapDescriptor bitmap = null;

                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_new_applying);
                OverlayOptions option = new MarkerOptions()
                        .title(bs.getBsNames())
                        .position(desLatLng)
                        .icon(bitmap);
                // 用户退出时、地图被销毁
                if (mMapView == null) {
                    return;
                }
                Marker marker = (Marker) mBaiduMap.addOverlay(option);
                //markerList.add(marker);
                bsName = bs.getBsNames();
                bsID = bs.getBsIds();
                Bundle bundle = new Bundle();
                bundle.putString("name", bsName);
                bundle.putString("id", bsID);
                marker.setExtraInfo(bundle);
                marker.setZIndex(Integer.valueOf(bs.getBsIds()));
                marker.setTitle("物理站址名称:" + bs.getBsNames() + "\n" + "物理站址状态:" + "入网申请失败");

            }
        }
        if (type == QUERY) {
            if (allList != null) {
                LatLng sourceLatLng = new LatLng(Double.valueOf(allList.get(allList.size()-1).getBsLatitude()),
                        Double.valueOf(allList.get(allList.size()-1).getBsLongitude()));
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                ////定义Maker坐标点并将原始坐标转为百度坐标
                // sourceLatLng待转换坐标
                converter.coord(sourceLatLng);
                LatLng desLatLng = converter.convert();
                MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newLatLng(desLatLng);
                mBaiduMap.setMapStatus(mapStatusUpdate);
            }
        }
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                TextView title = (TextView) view.findViewById(R.id.marker_title);
                double latitude, longitude;
                latitude = marker.getPosition().latitude;
                longitude = marker.getPosition().longitude;
                final LatLng pt = new LatLng(latitude, longitude);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(pt);
                mBaiduMap.setMapStatus(update);
                btn_physicalCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*int ids = marker.getZIndex();
                        int type = 3;
                        upLoad(type, ids);*/
                        Intent intent = new Intent(PhysicalSiteApplyActivity.this, PhysicalSiteChekApplyPayContentActivity.class);
                        Bundle be = marker.getExtraInfo();
                        int type = 3;
                        intent.putExtras(be);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    }
                });

                for (BaseSiteBean bs : bsList) {
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

                mInfoWindow = new InfoWindow(view, pt, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }
}
