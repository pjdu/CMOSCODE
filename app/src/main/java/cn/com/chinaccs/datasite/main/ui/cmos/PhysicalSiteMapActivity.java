package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.XFastFactory.XDbUtils.XDbUtils;
import com.XFastFactory.XTask.XSimpleTask;
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
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.RegionInfoItem;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.ui.functions.FuncGetPlanSiteList;
import cn.com.chinaccs.datasite.main.ui.models.BaseSiteBean;
import cn.com.chinaccs.datasite.main.ui.utils.MapUtils;

/**
 * 基站规划地图显示
 * Created by Asky on 2016/3/31.
 */
public class PhysicalSiteMapActivity extends BaseActivity implements
        View.OnClickListener,
        OnGetDistricSearchResultListener,
        BaiduMap.OnMarkerClickListener,
        BaiduMap.OnMapClickListener,
        BaiduMap.OnMapStatusChangeListener {


    private static final String TAG = PhysicalSiteMapActivity.class.getSimpleName();
    private Context context;
    private SearchView mSearchView;
    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListener mMyLocationListener = new MyLocationListener();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MyLocationConfiguration.LocationMode mCurrentMode;

    private InfoWindow mInfoWindow;
    boolean isFirstLoc = true; // 是否首次定位
    private BDLocation mLocation;
    private ImageButton requestLocBtn;
    private LatLng mLatLng;

    // 保存当前的Marker
    private ArrayList<Marker> currMarkers;

    // 需随时更新地图上新建基站位子
    private Marker newBaseMarker;
    private Circle newBaseCircle1;
    private Circle newBaseCircle2;

    //百度地图定位位置信息
    //BDLocation location;
    private BDGeoLocation geoBD;
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
    private Button physicalSiteEditBtn;

    //站点规划显示四种状态
    private ArrayList<BaseSiteBean> siteList;

    // 列表方式查看站址信息
    private ImageButton switchViewBtn;

    // 基站建设所处阶段
    private int stage = 0;

    // 地图当前比例尺
    private int moveDist = 4;
    // 地图行政区划加载完成后的zoom
    private int mZoom = 15;
    // 判断是否在进行行政区划的检索、检索完成后做对应的操作
    private boolean isZoomed = false;
    private static final int[] SCALES = {1, 1, 1, 1, 1, 1, 2, 5, 10, 20, 25, 50, 100, 200, 500, 1000, 2000};
    private LatLng mTarget;
    // 盲点/劣点所在市/州
    private String blindArea;
    private String[] blindAreas;
    private Spinner blindAreaSp;
    private String blindAreaID;
    // 盲点/劣点所在县或区
    public String blindCounty;
    private String[] blindCountys;
    private Spinner blindCountySp;
    public String blindCountyID;

    // private Spinner blindTownSp;
    private ArrayList<RegionInfoItem> REGION_LIST;

    // 第二节列表(市/州)
    private ArrayList<RegionInfoItem> TWO_LEVEL_LIST;
    // 第二节列表选中项(市/州)
    private RegionInfoItem TWO_LEVEL_ITEM = null;
    // 第三节列表(县/市)
    private ArrayList<RegionInfoItem> THREE_LEVEL_LIST;
    private ArrayList<RegionInfoItem> THREE_LEVEL_LIST_FILTER;
    // 第三节列表选中项(县/市)
    private RegionInfoItem THREE_LEVEL_ITEM = null;
    // 第四节列表(镇(乡)/街道)
    private ArrayList<RegionInfoItem> FOUR_LEVEL_LIST;
    private ArrayList<RegionInfoItem> FOUR_LEVEL_LIST_FILTER;
    // 第四节列表选中项(镇(乡)/街道)
    private RegionInfoItem FOUR_LEVEL_ITEM = null;

    //行政区域搜索
    private DistrictSearch mDistrictSearch;
    //private final int color = 0xAA00FF00;
    private int sideColor = 0xAABFEFFF;
    private int backgroundColor = 0x57FFFFFF;

    // 加载行政区划
    private String regionVersion = null;
    private JSONArray datas;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_site);
        stage = getIntent().getIntExtra("stage", 0);
        if (stage == 1) {
            initToolbar("规划阶段");
        } else if (stage == 2) {
            initToolbar("交付阶段");
        } else if (stage == 3) {
            initToolbar("入网阶段");
        } else if (stage == 4) {
            initToolbar("交维阶段");
        } else if (stage == 5) {
            initToolbar("维护阶段");
        } else if (stage == 6) {
            initToolbar("网优阶段");
        } else if (stage == 7) {
            initToolbar("拆迁阶段");
        }
        context = PhysicalSiteMapActivity.this;
        geoBD = ((MainApp) getApplication()).geoBD;
        mLocation = geoBD.location;
        if (mLocation != null) {
            longtitude = mLocation.getLongitude();
            latitude = mLocation.getLatitude();
        }

        // 获取行政区划数据
        TWO_LEVEL_LIST = new ArrayList<>();
        THREE_LEVEL_LIST = new ArrayList<>();
        THREE_LEVEL_LIST_FILTER = new ArrayList<>();
        FOUR_LEVEL_LIST = new ArrayList<>();
        FOUR_LEVEL_LIST_FILTER = new ArrayList<>();
        currMarkers = new ArrayList<>();
        // 初始化城市区域数据
        // 当前位置
        final String tempAddress = mLocation.getAddrStr();
        this.buildRegion(tempAddress);
        this.initViews();
        this.initMapView();
        this.buildCellBaseStation();
        // 初始化区域搜索
        initDistrictSearch();
        initPhycalSiteInfoWindow();
        sourceLatLng = new LatLng(latitude, longtitude);
        getMapLoc(sourceLatLng);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(sourceLatLng);
        mBaiduMap.setMapStatus(update);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /*getMapLoc(sourceLatLng);
        if (blindCounty.equals("市辖区") || blindCounty.equals("州辖区")){
            blindCountyID = "";
        }
        getBSPlanSiteList(gpsLatLng, "", blindAreaID, blindCountyID);*/
    }

    private void initPhycalSiteInfoWindow() {
        view = LayoutInflater.from(context).inflate(R.layout.physical_site_info_window, null); //自定义气泡形状
        physicalSiteEditBtn = (Button) view.findViewById(R.id.btn_physical_site_edit);
        if (stage == 1) {
            physicalSiteEditBtn.setText("关闭");
        } else if (stage == 2) {
            physicalSiteEditBtn.setText("交付");
        } else {
            physicalSiteEditBtn.setText("关闭");
        }
        /*else if (stage == 3){
            physicalSiteEditBtn.setText("入网");
        }else if (stage == 4){
            physicalSiteEditBtn.setText("交维");
        }else if (stage == 5){
            physicalSiteEditBtn.setText("维护");
        }else if (stage == 6){
            physicalSiteEditBtn.setText("网优");
        }else if (stage == 7){
            physicalSiteEditBtn.setText("拆迁");
        }*/

    }

    private void initViews() {
        blindAreaSp = (Spinner) findViewById(R.id.sp_blindArea);
        blindCountySp = (Spinner) findViewById(R.id.sp_blindCounty);
    }

    private void initDistrictSearch() {
        // searchInCity = (Button) this.findViewById(R.id.btn_searchInCity);
        mDistrictSearch = DistrictSearch.newInstance();
        mDistrictSearch.setOnDistrictSearchListener(this);

    }

    /**
     *
     */
    private void initMapView() {
        mMapView = (MapView) findViewById(R.id.mapView);
        //注册百度定位监听器
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(msu);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapStatusChangeListener(this);

        // 切换至列表形式
        switchViewBtn = (ImageButton) findViewById(R.id.switchViewBtn);
        switchViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 百度地图坐标
                if (mLocation == null) {
                    Toast.makeText(context, "正在获取位置信息，请稍后....", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, PhysicalSiteListActivity.class);
                //
                intent.putExtra("stage", stage);
                intent.putExtra("longitude", mLocation.getLongitude());
                intent.putExtra("latitude", mLocation.getLatitude());
                if (blindCounty.equals("市辖区") || blindCounty.equals("州辖区")){
                    blindCountyID = "";
                }
                intent.putExtra("blindArea", blindAreaID);
                intent.putExtra("blindCounty", blindCountyID);
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
        mLocClient.registerLocationListener(mMyLocationListener);
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
        getMenuInflater().inflate(R.menu.menu_site_map, menu);
        final MenuItem addItem = menu.findItem(R.id.menu_site_new);
        if (stage != 1) {
            addItem.setVisible(false);
        }
        final MenuItem item = menu.findItem(R.id.ab_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startBsQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(sourceLatLng);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
                return false;
            }
        });
        return true;
    }

    private void startBsQuery(String name) {
        if (name.equals("")) {
            return;
        }
        if (siteList == null) {
            return;
        }
        for (BaseSiteBean bean : siteList) {
            String title = bean.getBsNames();
            if (title.equals(name)) {
                String lat = bean.getBsLatitude();
                String lon = bean.getBsLongitude();
                if (lat.equals("") || lon.equals("")) {
                    continue;
                }
                LatLng location = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                location = MapUtils.gpsLnChangeToBdLn(location);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(location);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    /**
     * 本地查询基站
     *
     * @param name
     */
    private void localQueryByName(String name) {
        if (siteList == null || siteList.size() <= 0) {
            // getBSPlanSiteList(gpsLatLng, name);
        } else {
            // for (in)
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_site_new) {
            longtitude = mLocation.getLongitude();
            latitude = mLocation.getLatitude();
            getMapLoc(sourceLatLng);
            Intent intent = new Intent(PhysicalSiteMapActivity.this, PhysicalSiteNewEditActivity.class);
            intent.putExtra("longitudeGPS", longitudeGPS);
            intent.putExtra("latitudeGPS", latitudeGPS);
            intent.putExtra("longitudeBaidu", longtitude);
            intent.putExtra("latitudeBaidu", latitude);
            startActivity(intent);
        }
        return true;
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
        Log.d(TAG, "当前接入基站坐标纬度经度" + cellLac + ":" + cellLng);
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
            LatLng point = new LatLng(mLocation.getLongitude(), mLocation.getLatitude());
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

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        // 获取当前比例尺
        int index = (SCALES.length + 2) - (int) mapStatus.zoom;
        if (index < SCALES.length - 1 && index >= 0) {
            moveDist = SCALES[index];
        }
        ALog.i("-----------------------moveDist:" + moveDist + "--------------");
        // 获取当前zoom 作为地图缩放显示数据的基数
        ALog.i("-----------------------zoom: " + (int) mapStatus.zoom + "--------------");
        if (isZoomed) {
            mZoom = (int) mapStatus.zoom;
            isZoomed = false;
            // 在获取数据之前做相应的处理
            if (blindCounty.equals("市辖区") || blindCounty.equals("州辖区")) {
                blindCountyID = "";
            }
            mTarget = mapStatus.target;
            getMapLoc(sourceLatLng);
            getBSPlanSiteList(gpsLatLng, "", blindAreaID, blindCountyID);
        }
    }

    @Override
    public void onGetDistrictResult(DistrictResult districtResult) {
        if (districtResult == null) {
            Log.i(App.LOG_TAG, "districtResult == null");
            return;
        }
        if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<List<LatLng>> polyLines = districtResult.getPolylines();
            if (polyLines == null) {
                Log.i(App.LOG_TAG, "polyLines == null");
                Toast.makeText(context, "无法加载" + blindCounty + "行政区划信息", Toast.LENGTH_SHORT);
                return;
            }
            mBaiduMap.clear();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (List<LatLng> polyline : polyLines) {
                // Log.i(App.LOG_TAG, "polyLines is : " + polyline.toString());
                OverlayOptions ooPolyline11 = new PolylineOptions().width(18)
                        .points(polyline).dottedLine(true).color(sideColor);
                mBaiduMap.addOverlay(ooPolyline11);
                OverlayOptions ooPolygon = new PolygonOptions().points(polyline)
                        .stroke(new Stroke(10, 0xAA00FF88)).fillColor(backgroundColor);
                mBaiduMap.addOverlay(ooPolygon);
                for (LatLng latLng : polyline) {
                    builder.include(latLng);
                }
            }
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build()));
            // 此处调用了行政区划检索事件
            isZoomed = true;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
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
            sourceLatLng = ll;

            // 更新基站位置
            addNewCellBaseStation();
        }
    }

    // 查询站点规划的基站
    private void getBSPlanSiteList(final LatLng latLng, final String bsName, final String blindArea, final String blindCounty) {
        if (latLng == null) {
            Toast.makeText(context, "无法获取到经纬度,请稍后再试....", Toast.LENGTH_LONG).show();
            return;
        }
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.i(TAG, "getdata" + output);
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
                        siteList = new ArrayList<BaseSiteBean>();
                        BaseSiteBean bs;
                        for (int i = 0; i < data.length(); i++) {
                            bs = new BaseSiteBean(
                                    data.getJSONArray(i).getString(0),
                                    data.getJSONArray(i).getString(1),
                                    data.getJSONArray(i).getString(2),
                                    data.getJSONArray(i).getString(3),
                                    data.getJSONArray(i).getString(4),
                                    data.getJSONArray(i).getString(5),
                                    data.getJSONArray(i).getString(6),
                                    data.getJSONArray(i).getString(7)
                            );
                            siteList.add(bs);
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
        FuncGetPlanSiteList func = new FuncGetPlanSiteList(context);
        String tempBlindArea = blindAreaID;
        String tempBlindCounty = blindCountyID;
        if (blindCounty != null && blindCounty.equals("市辖区")){
            tempBlindCounty = "";
        }
        func.getData(lr, bsName, latLng, String.valueOf(stage), tempBlindArea, tempBlindCounty);
    }

    //在地图上显示基站信息
    private void showBSToMap() {
        // 用户退出时、地图被销毁
        if (mMapView == null) {
            return;
        }
        // 清楚基站信息
        for (Marker marker : currMarkers) {
            if (marker != null)
                marker.remove();
        }
        currMarkers.clear();

        if (siteList != null) {
            for (BaseSiteBean bs : siteList) {
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
                // mBaiduMap.setMapStatus(update);
                // Log.i(TAG, desLatLng.toString());
                BitmapDescriptor bitmap = null;

                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.bsstate_new_checking);
                OverlayOptions option = new MarkerOptions()
                        .title(bs.getBsNames())
                        .position(desLatLng)
                        .icon(bitmap);
                // 用户退出时、地图被销毁
                if (mMapView == null) {
                    return;
                }
                Marker marker = (Marker) mBaiduMap.addOverlay(option);
                Bundle bundle = new Bundle();
                bundle.putString("name", bs.getBsNames());
                bundle.putString("id", bs.getBsIds());
                bundle.putString("code", bs.getBsCode());
                bundle.putString("stage", bs.getBsType());
                marker.setExtraInfo(bundle);
                marker.setTitle("站址编号:" + bs.getBsCode() + "\n"
                        + "站址名称:" + bs.getBsNames() + "\n"
                        + "站址状态:" + getStateString(bs.getBsType()) + "\n"
                        + "站址地址:" + bs.getBsLocation());
                currMarkers.add(marker);
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
                physicalSiteEditBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                        Intent intent = null;
                        if (stage == 1) {
                            // intent = new Intent(context, PhysicalSiteNewEditActivity.class);
                        } else if (stage == 2) {
                            intent = new Intent(context, PhysicalSitePayEditActivity.class);
                        } /*else if (stage == 3) {
                            intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                        } else if (stage == 4) {
                            intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                        } else if (stage == 5) {
                            intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                        } else if (stage == 6) {
                            intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                        } else if (stage == 7) {
                            intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                        }*/
                        if (intent == null) {
                            return;
                        }
                        Bundle be = marker.getExtraInfo();
                        intent.putExtras(be);
                        startActivity(intent);
                    }
                });


                title.setText(marker.getTitle());
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                    }
                });

                mInfoWindow = new InfoWindow(view, pt, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    //转换百度地图坐标为GPS
    private void getMapLoc(final LatLng sourceLatLng) {
        Log.d(App.LOG_TAG, "getMapLoc");
        if (sourceLatLng != null) {
            Toast.makeText(this, "定位成功", Toast.LENGTH_SHORT).show();
            //将百度坐标转为GPS坐标
            //把百度坐标当成一个GPS坐标
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(sourceLatLng);
            //模拟纠偏得到d百度地图坐标
            LatLng desLatLng = converter.convert();
            longitudeGPS = 2 * sourceLatLng.longitude - desLatLng.longitude;
            latitudeGPS = 2 * sourceLatLng.latitude - desLatLng.latitude;
            gpsLatLng = new LatLng(latitudeGPS, longitudeGPS);

        } else {
            Toast.makeText(this, "定位中", Toast.LENGTH_SHORT).show();
        }
    }

    private String getStateString(String type) {
        String name = "规划阶段";
        if (type.equals("1")) {
            name = "规划阶段";
        } else if (type.equals("2")) {
            name = "交付阶段";
        } else if (type.equals("3")) {
            name = "入网阶段";
        } else if (type.equals("4")) {
            name = "交维阶段";
        } else if (type.equals("5")) {
            name = "维护阶段";
        } else if (type.equals("6")) {
            name = "网优阶段";
        } else if (type.equals("7")) {
            name = "拆迁阶段";
        }
        return name;
    }

    //请求市区县
    private void buildRegion(final String address) {
        if (progressDialog == null) {
            progressDialog = CoConfig.progressDialog(context, "数据同步中....");
            progressDialog.show();
        } else {
            progressDialog.setMessage("数据同步中....");
            progressDialog.show();
        }
        queryRegionDataToInitSp(address);
    }

    /**
     * 获取数据库行政区划数据初始化行政区划列表
     */
    private void queryRegionDataToInitSp(final String address) {
        new XSimpleTask() {

            @Override
            public Object onTask(Object... objects) {
                REGION_LIST = getRegionInfoItems();
                return null;
            }

            @Override
            public void onComplete(Object o) {
                for (int i = 0; i < REGION_LIST.size(); ++i) {
                    RegionInfoItem item = REGION_LIST.get(i);
                    if (item.ORG_LEVEL == 2) {
                        TWO_LEVEL_LIST.add(item);
                    } else if (item.ORG_LEVEL == 3) {
                        THREE_LEVEL_LIST.add(item);
                    } else if (item.ORG_LEVEL == 4) {
                        FOUR_LEVEL_LIST.add(item);
                    }
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                getTheTwoRegionAdapterSp(address);
            }

            @Override
            public void onUpdate(Object o) {

            }
        }.execute();
    }

    /**
     * 加载市区文件
     */
    private void getTheTwoRegionAdapterSp(final String address) {
        blindAreas = new String[TWO_LEVEL_LIST.size()];
        for (int i = 0; i < TWO_LEVEL_LIST.size(); ++i) {
            blindAreas[i] = TWO_LEVEL_LIST.get(i).ORG_NAME;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, R.layout.item_spinner, blindAreas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blindAreaSp.setAdapter(adapter);

        blindAreaSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int index, long arg3) {
                // TODO Auto-generated method stub
                if (TWO_LEVEL_LIST.isEmpty()) {
                    Toast.makeText(context, "正在加载数据请稍后", Toast.LENGTH_SHORT);
                }
                TWO_LEVEL_ITEM = TWO_LEVEL_LIST.get(index);
                if (TWO_LEVEL_ITEM != null) {
                    blindArea = TWO_LEVEL_ITEM.ORG_NAME;
                    blindAreaID = String.valueOf(TWO_LEVEL_ITEM.ORG_ID) + "00";
                    THREE_LEVEL_LIST_FILTER.clear();
                    THREE_LEVEL_ITEM = null;
                    blindCounty = null;
                    blindCountyID = null;
                    FOUR_LEVEL_LIST_FILTER.clear();
                    FOUR_LEVEL_ITEM = null;
                    getTheThreeRegionAdapterCountySp();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { //
                // TODO Auto-generated method stub
                if (TWO_LEVEL_LIST.isEmpty()) {
                    Toast.makeText(context, "正在加载数据请稍后", Toast.LENGTH_SHORT);
                }
                if (blindAreaSp.getSelectedItem() != null) {
                    blindArea = blindAreaSp.getSelectedItem().toString();
                    ALog.d(blindArea);
                    mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea).districtName(""));
                }

            }
        });
        int index = 2;
        if (address != null && !address.equals("")) {
            for (int i = 0; i < blindAreas.length; ++i) {
                if (address.contains(blindAreas[i])) {
                    index = i;
                }
            }
        }
        blindAreaSp.setSelection(index, true);
    }

    private void getTheThreeRegionAdapterCountySp() {
        // 为地州加上州辖区，for 查看整个地州基站数据
        if (TWO_LEVEL_ITEM.ORG_NAME.contains("州")) {
            THREE_LEVEL_LIST_FILTER.add(new RegionInfoItem(Long.valueOf(TWO_LEVEL_ITEM.ORG_ID + "00"), "州辖区", (int) TWO_LEVEL_ITEM.ORG_ID, 3, 0));
        }
        for (int i = 0; i < THREE_LEVEL_LIST.size(); ++i) {
            RegionInfoItem item = THREE_LEVEL_LIST.get(i);
            if (item.ORG_PARENT_ID == TWO_LEVEL_ITEM.ORG_ID) {
                THREE_LEVEL_LIST_FILTER.add(item);
            }
        }
        blindCountys = new String[THREE_LEVEL_LIST_FILTER.size()];
        for (int i = 0; i < THREE_LEVEL_LIST_FILTER.size(); ++i) {
            RegionInfoItem item = THREE_LEVEL_LIST_FILTER.get(i);
            blindCountys[i] = item.ORG_NAME;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.item_spinner, blindCountys);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blindCountySp.setAdapter(adapter);

        blindCountySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int index, long arg3) {
                // TODO Auto-generated method stub
                if (THREE_LEVEL_LIST_FILTER.isEmpty()) {
                    Toast.makeText(context, "正在加载数据请稍后", Toast.LENGTH_SHORT);
                }
                THREE_LEVEL_ITEM = THREE_LEVEL_LIST_FILTER.get(index);
                if (THREE_LEVEL_ITEM != null) {
                    blindCounty = THREE_LEVEL_ITEM.ORG_NAME;
                    blindCountyID = String.valueOf(THREE_LEVEL_ITEM.ORG_ID);
                    FOUR_LEVEL_LIST_FILTER.clear();
                    FOUR_LEVEL_ITEM = null;
                    //getTheFourRegionAdapterTownSp();
                }
                if (blindCountySp.getSelectedItem() != null) {
                    blindCounty = blindCountySp.getSelectedItem().toString();
                    Log.i(App.LOG_TAG, blindAreaID + ":" + blindArea);
                    Log.i(App.LOG_TAG, blindCountyID + ":" + blindCounty);
                    if (!blindCounty.equals("市辖区") && !blindCounty.equals("州辖区")) {
                        if (blindCounty.equals("石林县")) {
                            blindCounty = "石林彝族自治县";
                        } else if (blindCounty.equals("禄劝县")) {
                            blindCounty = "禄劝彝族苗族自治县";
                        } else if (blindCounty.equals("寻甸县")) {
                            blindCounty = "寻甸回族彝族自治县";
                        } else if (blindCounty.equals("峨山县")) {
                            blindCounty = "峨山彝族自治县";
                        } else if (blindCounty.equals("新平县")) {
                            blindCounty = "新平彝族傣族自治县";
                        } else if (blindCounty.equals("元江县")) {
                            blindCounty = "元江";
                        } else if (blindCounty.equals("宁蒗县")) {
                            blindCounty = "宁蒗彝族自治县";
                        } else if (blindCounty.equals("玉龙县")) {
                            blindCounty = "玉龙纳西族自治县";
                        } else if (blindCounty.equals("宁洱县")) {
                            blindCounty = "宁洱哈尼族彝族自治县";
                        } else if (blindCounty.equals("墨江县")) {
                            blindCounty = "墨江哈尼族自治县";
                        } else if (blindCounty.equals("景东县")) {
                            blindCounty = "景东彝族自治县";
                        } else if (blindCounty.equals("景谷县")) {
                            blindCounty = "景谷傣族彝族自治县";
                        } else if (blindCounty.equals("镇沅县")) {
                            blindCounty = "镇沅";
                        } else if (blindCounty.equals("江城县")) {
                            blindCounty = "江城哈尼族彝族自治县";
                        } else if (blindCounty.equals("孟连县")) {
                            blindCounty = "孟连";
                        } else if (blindCounty.equals("澜沧县")) {
                            blindCounty = "澜沧拉祜族自治县";
                        } else if (blindCounty.equals("西盟县")) {
                            blindCounty = "西盟佤族自治县";
                        } else if (blindCounty.equals("双江县")) {
                            blindCounty = "双江";
                        } else if (blindCounty.equals("耿马县")) {
                            blindCounty = "耿马傣族佤族自治县";
                        } else if (blindCounty.equals("沧源县")) {
                            blindCounty = "沧源佤族自治县";
                        } else if (blindCounty.equals("屏边县")) {
                            blindCounty = "屏边苗族自治县";
                        } else if (blindCounty.equals("金平县")) {
                            blindCounty = "金平苗族瑶族傣族自治县";
                        } else if (blindCounty.equals("河口县")) {
                            blindCounty = "河口瑶族自治县";
                        } else if (blindCounty.equals("漾濞县")) {
                            blindCounty = "漾濞彝族自治县";
                        } else if (blindCounty.equals("南涧县")) {
                            blindCounty = "南涧彝族自治县";
                        } else if (blindCounty.equals("巍山县")) {
                            blindCounty = "巍山彝族回族自治县";
                        } else if (blindCounty.equals("贡山县")) {
                            blindCounty = "贡山独龙族怒族自治县";
                        } else if (blindCounty.equals("兰坪县")) {
                            blindCounty = "兰坪白族普米族自治县";
                        } else if (blindCounty.equals("维西县")) {
                            blindCounty = "维西傈僳族自治县";
                        }
                        ALog.d(blindArea + "-" + blindCounty);
                        mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea).districtName(blindCounty));
                    } else {
                        if (blindArea.equals("红河州")) {
                            blindArea = "红河哈尼族彝族自治州";
                        } else if (blindArea.equals("文山州")) {
                            blindArea = "文山壮族苗族自治州";
                        } else if (blindArea.equals("大理州")) {
                            blindArea = "大理白族自治州";
                        } else if (blindArea.equals("德宏州")) {
                            blindArea = "德宏傣族景颇族自治州";
                        } else if (blindArea.equals("怒江州")) {
                            blindArea = "怒江傈僳族自治州";
                        } else if (blindArea.equals("迪庆州")) {
                            blindArea = "迪庆藏族自治州";
                        }
                        ALog.d(blindArea);
                        mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea).districtName(""));
                    }

                } else {
                    Toast.makeText(PhysicalSiteMapActivity.this, "找不到行政区域", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                if (THREE_LEVEL_LIST_FILTER.isEmpty()) {
                    Toast.makeText(context, "加载中....", Toast.LENGTH_SHORT).show();
                }
                blindCounty = blindCountySp.getSelectedItem().toString();
                mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea).districtName(blindCounty));
            }
        });
        int index = 0;
        blindCountySp.setSelection(index, true);

    }

    private ArrayList<RegionInfoItem> getRegionInfoItems() {
        XDbUtils db = new XDbUtils(this.context);
        ArrayList<RegionInfoItem> infos = new ArrayList<RegionInfoItem>();
        try {
            db.createTable(RegionInfoItem.class);
            List<Object> objects = db.query(null, null, RegionInfoItem.class, null);
            for (int i = 0; i < objects.size(); ++i) {
                RegionInfoItem info = (RegionInfoItem) objects.get(i);
                infos.add(info);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }
}
