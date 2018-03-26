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
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
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
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncBSLocSearch;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.BaseStation;

/**
 * Created by Asky on 2016/3/31.
 */
public class SearchActivity extends BaseActivity {

    private static final String TAG = App.LOG_TAG + "--" + SearchActivity.class.getSimpleName();

    private Context context;
    private SearchView mSearchView;
    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListenner myLocationListenner = new MyLocationListenner();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    boolean isFirstLoc = true; // 是否首次定位
    private BDLocation mLocation;
    private ImageButton requestLocBtn;

    private ArrayList<BaseStation> bsList;
    private InfoWindow mInfoWindow;

    //点击图标变为显示告警信息
    private View mPopView;
    private TextView mPopInfoTv;
    private String currentID;
    private String BsType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initToolbar("搜索");
        context = SearchActivity.this;
        this.initMapView();
        this.buildCellBaseStation();
        mPopView = LayoutInflater.from(context).inflate(R.layout.marker_title, null); //自定义气泡形状
        mPopInfoTv = (TextView) mPopView.findViewById(R.id.marker_title);
    }

    private void initMapView() {
        mMapView = (MapView) findViewById(R.id.mapView);
        //注册百度定位监听器
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
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
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startDataSiteLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // 基站定位
    private void startDataSiteLocation(String name) {
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
                    Log.i(TAG, output);
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
                                Log.i(TAG, "I : " + i + "------data.length : " + data.length());
                                bsTemp = bs;
                            }
                        }
                        // 基站经纬度传回来的存在""
                        if (bsTemp.getBsLatitude().equals("") || bsTemp.getBsLongitude().equals("")) {
                            for (BaseStation sourceBs : bsList) {
                                if (!sourceBs.getBsLatitude().equals("") || !sourceBs.getBsLongitude().equals("")) {
                                    bsTemp = sourceBs;
                                    break;
                                }
                            }
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
        func.getData(lr, name);
    }

    //在地图上显示基站信息
    private void showBSToMap(LatLng latLng) {
        Log.i(TAG, "showBSToMap");
        // 用户退出时、地图被销毁
        if (mMapView == null) {
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
            Marker marker = (Marker) mBaiduMap.addOverlay(new MarkerOptions().position(desLatLng1).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location)));
            marker.setTitle("中心点标记");
        }
        for (BaseStation bs : bsList) {
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
            if (bs.getType().equals("0")){
                BsType="已验收交付";
            }else {
                BsType="未验收交付";
            }
            MarkerOptions option = new MarkerOptions()
                    .title(bs.getBsNames())
                    .position(desLatLng)
                    .icon(bitmap);
            Bundle bundle = new Bundle();
            bundle.putString("BsID", bs.getBsIds());
            bundle.putString("BsName", bs.getBsNames());
            bundle.putString("BsBsc", bs.getBsBsc());
            bundle.putString("BsAlarmInfo", bs.getotherInfo());
            bundle.putString("BsType", BsType);
            option.extraInfo(bundle);

            // 用户退出时、地图被销毁
            if (mMapView == null) {
                return;
            }
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            // markerList.add(marker);
           /* marker.setZIndex(Integer.valueOf(bs.getBsIds()));
            marker.setTitle("基站名称:" + bs.getBsNames() +  "\n" + "基站类型:" + bs.getBsBsc()+"\n" + "基站信息:" + bs.getotherInfo());*/

        }
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
/*                View view = LayoutInflater.from(context).inflate(R.layout.physical_site_info_window, null); //自定义气泡形状
                TextView title = (TextView) view.findViewById(R.id.marker_title);
                Button btn_physical_new_check_apply_pay= (Button) view.findViewById(R.id.btn_physical_new_check_apply_pay);
                double latitude, longitude;
                latitude = marker.getPosition().latitude;
                longitude = marker.getPosition().longitude;
                final LatLng pt = new LatLng(latitude + 0.0004, longitude + 0.00005);

                for (BaseStation bs : bsList) {
//                    if (bs.getBsIds() == String.valueOf(marker.getZIndex())) {
                    title.setText(marker.getTitle());
                    btn_physical_new_check_apply_pay.setText("关闭");
                    btn_physical_new_check_apply_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBaiduMap.hideInfoWindow();
                        }
                    });
//                    }
                }
                mInfoWindow = new InfoWindow(view, pt, 0);
                mBaiduMap.showInfoWindow(mInfoWindow);*/
                // init marker click show view

                mBaiduMap.hideInfoWindow();
                // TODO Auto-generated method stub
                if (marker == null) {
                    return false;
                }
                Bundle bundle = marker.getExtraInfo();
                mPopInfoTv.setText("站址名称:" + bundle.getString("BsName")
                        + "\n" + "站址信息:" + bundle.get("BsBsc")
                        + "\n" + "告警信息:" + bundle.get("BsAlarmInfo")
                        + "\n" + "基站类型:" + bundle.get("BsType"));
                InfoWindow mInfoWindow = new InfoWindow(mPopView, marker.getPosition(), -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
                mBaiduMap.setMapStatus(update);
                currentID = bundle.getString("BsID");
                return true;
            }
        });
    }
    /**
     * show the site info by physical
     *
     * @param v
     */
    public void showSiteInfoClick(View v) {
        mBaiduMap.hideInfoWindow();
        Intent toShowSiteInfo = new Intent(this, PhysicalSiteInfoActivity.class);
        toShowSiteInfo.putExtra("BsId", currentID);
        startActivity(toShowSiteInfo);
    }

    public void showAlarmInfoClick(View v) {
        mBaiduMap.hideInfoWindow();
        Intent toShowAlarmInfo = new Intent(this, PhysicalSiteAlarmInfoActivity.class);
        toShowAlarmInfo.putExtra("BsId", currentID);
        startActivity(toShowAlarmInfo);
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
     *
     */
    private void buildCellBaseStation(){
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
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            mLocation = location;
        }
    }
}
