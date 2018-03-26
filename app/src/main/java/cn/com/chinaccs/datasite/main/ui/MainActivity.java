package cn.com.chinaccs.datasite.main.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.XFastFactory.XDbUtils.XDbUtils;
import com.XFastFactory.XTask.XSimpleTask;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.adapter.SiteGridAdapter;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.bean.Collector;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecordsInfo;
import cn.com.chinaccs.datasite.main.bean.InfoCell;
import cn.com.chinaccs.datasite.main.bean.InfoConnectivity;
import cn.com.chinaccs.datasite.main.bean.InfoSim;
import cn.com.chinaccs.datasite.main.bean.InfoSystem;
import cn.com.chinaccs.datasite.main.bean.InfoWifi;
import cn.com.chinaccs.datasite.main.bean.WebSite;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.common.AppNetWork;
import cn.com.chinaccs.datasite.main.connect.GetWebSite;
import cn.com.chinaccs.datasite.main.connect.OnGetDataFinishListener;
import cn.com.chinaccs.datasite.main.connect.PostBusiSiteData;
import cn.com.chinaccs.datasite.main.data.FuncGetSysDatas;
import cn.com.chinaccs.datasite.main.datasite.database.DBInspectHandler;
import cn.com.chinaccs.datasite.main.datasite.database.IDBHandler;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetRegionInfoItem;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetSignBsInfo;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.BaseStation;
import cn.com.chinaccs.datasite.main.db.model.RegionInfoItem;
import cn.com.chinaccs.datasite.main.ui.asset.AssetMainActivity;
import cn.com.chinaccs.datasite.main.ui.asset.mobile.MobileInfoActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.AssetsInfoActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.InspectHistoryActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.InspectSignActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.NewInspectBSActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.PhysicalSiteAlarmInfoActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.PhysicalSiteAlarmListActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.PhysicalSiteInfoActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.PhysicalSiteMapActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.SearchActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.SettingActivity;
import cn.com.chinaccs.datasite.main.ui.lte.LteMapMainActivity;
import cn.com.chinaccs.datasite.main.widget.BadgeView;
import cn.com.chinaccs.datasite.main.widget.PromptDailog;


public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        OnGetDistricSearchResultListener,
        BaiduMap.OnMarkerClickListener,
        BaiduMap.OnMapClickListener,
        BaiduMap.OnMapStatusChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    Context context;
    // 地图当前比例尺
    private int moveDist = 4;
    // 地图行政区划加载完成后的zoom
    private int mZoom = 15;
    // 判断是否在进行行政区划的检索、检索完成后做对应的操作
    private boolean isZoomed = false;
    // private FloatingActionMenu mActionMenu;
    // 首次加载地图进行操作
    private boolean isFirstIn = true;
    // user info
    private ImageView mLogoutIv;
    private TextView mUserNameTv;
    private TextView mAccountTv;

    //first httptest
    private Handler dialogHandler;
    private String addr;
    //网站测试
    private List<Collector> list;

    private JSONObject busiJsonDatas;
    private String address1;
    private String testTime;
    private ArrayList<WebSite> listSites;
    private SiteGridAdapter saMenuItems;
    private String testResult;
    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;

    /**
     * 　* 各级比例尺分母值数组
     * <p/>
     */
    //判断是否进行网站测试
    private boolean autoHttpTest;
    private static final int[] SCALES = {1, 1, 1, 1, 1, 1, 2, 5, 10, 20, 25, 50, 100, 200, 500, 1000, 2000};

    private MapView mv;
    private BaiduMap mBaiduMap;
    private double mLatitude;
    private double mLongtitude;
    BDLocation location;
    public static BDGeoLocation geoBD;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private ImageButton requestLocBtn;
    // 保存当前的Marker
    private ArrayList<Marker> currMarkers;

    private BDGeoLocation.BDLocationChangeListener listener;
    private ArrayList<BaseStation> bsList;
    private ArrayList<BaseStation> critacalList;
    private ArrayList<BaseStation> majorList;
    private ArrayList<BaseStation> minorList;
    private ArrayList<BaseStation> warningList;
    private ArrayList<BaseStation> normalList;

    private View mPopView;
    private TextView mPopInfoTv;
    private String currentID;

    // 盲点/劣点所在市/州
    private String blindArea;
    private String[] blindAreas;
    private Spinner blindAreaSp;
    // 盲点/劣点所在县或区
    public String blindCounty;
    private String[] blindCountys;
    private Spinner blindCountySp;
    // 盲点/劣点所在镇(乡)/街道
    public String blindTown;
    private String[] blindTowns;
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
    private String address = null;

    //行政区域搜索
    private DistrictSearch mDistrictSearch;
    //private final int color = 0xAA00FF00;
    private int sideColor = 0xAABFEFFF;
    private int backgroundColor = 0x57FFFFFF;

    // 加载行政区划
    private String regionVersion = null;
    private JSONArray datas;

    // 用户权限
    private String userType = null;

    // 基站信息列表
    // 点击查看对应的基站
    private ImageView critacalIv;
    private BadgeView critacalBadge;
    private ImageView majorIv;
    private BadgeView majorBadge;
    private ImageView minorIv;
    private BadgeView minorBadge;
    private ImageView warningLv;
    private BadgeView warningBadge;
    private ImageView normalIv;
    private BadgeView normalBadge;

    //基站点击状态
    private boolean critacalIvState;
    private boolean majorIvState;
    private boolean minorIvState;
    private boolean warningState;
    private boolean normalIvState;

    //判断网站测速
    private boolean speedTest = true;


    //网站测试结果数据传输
    private List<HttpTestRecordsInfo> upLoadList;
    //网站测试的基础的数据存在该list中
    private List<Collector> listLTEMain;
    //增加测试的项目
    private InfoWifi wifi;
    private Integer level = 100;
    private TelephonyManager tm;
    //将测试的基础信息存入jsonObject中
    private JSONObject LTE;
    //上传状态为强行上传
    private final int upLoadState = 0;
    //popupwindow的显示位置为toolbar的下面
    Toolbar toolbar;
    ImageView infomation;
    View view;
    //httptestTime限定接口只返回一次
    int httptestTime = 0;

    //显示操作提示
    private boolean isShowedOperatorTip = false;

    /**
     * 更新基站数据
     */
    private LatLng mTarget;
    private Handler hrBsInfo;
    private Runnable refreshBsInfo = new Runnable() {// 5秒刷新一次联网方式

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mTarget != null && blindArea != null) {
                if (blindCounty == null) {
                    blindCounty = "";
                } else {
                    if (blindCounty.equals("市辖区")) {
                        blindCounty = "";
                    }
                    if (blindCounty.equals("州辖区")) {
                        blindCounty = "";
                    }
                }
                Log.i(App.LOG_TAG, "------refreshBsInfo-------");
                getMapLoc(mTarget, blindArea, blindCounty);
            }
            hrBsInfo.postDelayed(refreshBsInfo, 300000);
        }
    };
    private boolean isStartedRefreshRun = false;
    // 初始化接入基站
    private boolean isBuildCellBs = false;

    //网站测试优化"ECIO","Snr","LteRsrq"
    private Map<String, String> signalMap;

    // 列表方式查看告警信息
    private ImageButton switchViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 获取地理位置信息
        geoBD = ((MainApp) this.getApplication()).geoBD;
        location = geoBD.location;
        if (geoBD != null && geoBD.locClient != null
                && geoBD.locClient.isStarted()) {
            geoBD.locClient.requestLocation();
        }
        sharedPreferences = getSharedPreferences(App.SHARE_TAG, 0);

        autoHttpTest = sharedPreferences.getBoolean(AppNetWork.SHARE_IF_AUTOTEST, true);
        userType = sharedPreferences.getString(AppCheckLogin.SHARE_USER_TYPE, "3");
        // 初始化基站列表
        warningList = new ArrayList<BaseStation>();
        normalList = new ArrayList<BaseStation>();
        critacalList = new ArrayList<BaseStation>();
        majorList = new ArrayList<BaseStation>();
        minorList = new ArrayList<BaseStation>();
        bsList = new ArrayList<BaseStation>();
        currMarkers = new ArrayList<>();
        TWO_LEVEL_LIST = new ArrayList<>();
        THREE_LEVEL_LIST = new ArrayList<>();
        THREE_LEVEL_LIST_FILTER = new ArrayList<>();
        FOUR_LEVEL_LIST = new ArrayList<>();
        FOUR_LEVEL_LIST_FILTER = new ArrayList<>();
        critacalIvState = false;
        majorIvState = false;
        minorIvState = false;
        normalIvState = false;
        warningState = false;
        //初始化网站测试的数据
        if (speedTest == true && autoHttpTest == true) {
            if (listLTEMain == null) {
                listLTEMain = new ArrayList<Collector>();
            }
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                    | PhoneStateListener.LISTEN_CELL_LOCATION
                    | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        }

        // 获取行政区划数据
        //初始化城市区域数据
        this.buildRegion();
        // 初始化用户设置
        initView();
        // 初始化地图
        initMapView();
        // 初始化接入基站
        this.buildCellBaseStation();
        //初始化区域搜索
        initDistrictSearch();
        /**
         * 更新基站数据
         */
        hrBsInfo = new Handler();
    }

    //检查并更新版本
    private void checkAndUpdate() {

    }

    private void initTeachPicture() {
        boolean autoTeach = sharedPreferences.getBoolean(AppNetWork.SHARE_IF_AUTOTEACH, true);
        if (autoTeach) {
            final ImageView imgInfo = (ImageView) this.findViewById(R.id.img_information);
            imgInfo.setVisibility(View.VISIBLE);
            imgInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgInfo.setVisibility(View.GONE);
                    sharedPreferences.edit().putBoolean(AppNetWork.SHARE_IF_AUTOTEACH, false).commit();
                    checkGPS();
                    deleteCache();
                }
            });
        } else {
            checkGPS();
            deleteCache();
        }
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
                    .center(point).stroke(new Stroke(2, Color.BLUE))
                    .radius(1002);
            mBaiduMap.addOverlay(ooCircle2);
        }
    }

    /**
     * 加载市区文件
     */
    private void getTheTwoRegionAdapterSp() {
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
                critacalIvState = false;
                majorIvState = false;
                minorIvState = false;
                warningState = false;
                normalIvState = false;
                // TODO Auto-generated method stub
                if (TWO_LEVEL_LIST.isEmpty()) {
                    Toast.makeText(context, "正在加载数据请稍后", Toast.LENGTH_SHORT);
                }
                TWO_LEVEL_ITEM = TWO_LEVEL_LIST.get(index);
                if (TWO_LEVEL_ITEM != null) {
                    blindArea = TWO_LEVEL_ITEM.ORG_NAME;
                    THREE_LEVEL_LIST_FILTER.clear();
                    THREE_LEVEL_ITEM = null;
                    blindCounty = null;
                    FOUR_LEVEL_LIST_FILTER.clear();
                    FOUR_LEVEL_ITEM = null;
                    blindTown = null;
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
                if (address.indexOf(blindAreas[i]) != -1) {
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
                critacalIvState = false;
                majorIvState = false;
                minorIvState = false;
                warningState = false;
                normalIvState = false;
                // TODO Auto-generated method stub
                if (THREE_LEVEL_LIST_FILTER.isEmpty()) {
                    Toast.makeText(context, "正在加载数据请稍后", Toast.LENGTH_SHORT);
                }
                THREE_LEVEL_ITEM = THREE_LEVEL_LIST_FILTER.get(index);
                if (THREE_LEVEL_ITEM != null) {
                    blindCounty = THREE_LEVEL_ITEM.ORG_NAME;
                    FOUR_LEVEL_LIST_FILTER.clear();
                    FOUR_LEVEL_ITEM = null;
                    blindTown = null;
                    //getTheFourRegionAdapterTownSp();
                }
                if (blindCountySp.getSelectedItem() != null) {
                    blindCounty = blindCountySp.getSelectedItem().toString();
                    Log.i(App.LOG_TAG, blindArea);
                    Log.i(App.LOG_TAG, blindCounty);
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
                    Toast.makeText(MainActivity.this, "找不到行政区域", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                if (THREE_LEVEL_LIST_FILTER.isEmpty()) {
                    Toast.makeText(context, "加载中....", Toast.LENGTH_SHORT);
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

    private void deleteRegionInfoItems() {
        XDbUtils db = new XDbUtils(this.context);
        try {
            db.deleteTable(RegionInfoItem.class);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //请求市区县
    private void buildRegion() {
        if (progressDialog == null) {
            progressDialog = CoConfig.progressDialog(context, "数据同步中....");
            progressDialog.show();
        } else {
            progressDialog.setMessage("数据同步中....");
            progressDialog.show();
        }
        FuncGetRegionInfoItem func = new FuncGetRegionInfoItem(context);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    getRegionINfo(output);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }
            }

            ;
        };
        func.getData(lr);
    }

    private void insertRegionInfoItems(RegionInfoItem item) {
        Log.i(App.LOG_TAG, item.toString());
        XDbUtils db = new XDbUtils(this.context);
        try {
            db.createTable(RegionInfoItem.class);
            db.insert(item);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getRegionINfo(String output) throws JSONException {
        Log.i(App.LOG_TAG, output);
        if (output.equals(AppHttpConnection.RESULT_FAIL)) {
            Toast.makeText(context,
                    getResources().getString(R.string.common_get_data_error),
                    Toast.LENGTH_LONG).show();
            // 初始化行政区划列表
            queryRegionDataToInitSp();
        } else {
            JSONObject json = new JSONObject(output);
            String result = json.getString("result");
            Log.i(App.LOG_TAG, "==============" + result);
            if (result.equals("-1")) {
                Toast.makeText(context, json.getString("msg"),
                        Toast.LENGTH_LONG).show();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            } else {
                regionVersion = sharedPreferences.getString("SHARE_REGION_VERSION", "0");
                if (Integer.valueOf(result) <= Integer.valueOf(regionVersion)) {
                    // 初始化行政区划列表
                    queryRegionDataToInitSp();
                    return;
                }
                sharedPreferences.edit().putString("SHARE_REGION_VERSION", result).commit();
                datas = json.getJSONArray("data");
                new XSimpleTask() {

                    @Override
                    public Object onTask(Object... objects) {
                        deleteRegionInfoItems();
                        for (int i = 0; i < datas.length(); ++i) {
                            JSONArray temp = null;
                            try {
                                temp = datas.getJSONArray(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RegionInfoItem item = null;
                            try {
                                item = new RegionInfoItem(temp.getLong(0), temp.getString(1), temp.getInt(2), temp.getInt(3), temp.getInt(4));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            insertRegionInfoItems(item);
                        }
                        return null;
                    }

                    @Override
                    public void onComplete(Object o) {
                        // 初始化行政区划列表
                        queryRegionDataToInitSp();
                        /*Intent toLike = new Intent(context, BSDataSiteInfoActivity.class);
                        startActivity(toLike);*/
                    }

                    @Override
                    public void onUpdate(Object o) {

                    }
                }.execute();
            }
        }
    }

    private void initMapView() {
        mv = (MapView) findViewById(R.id.mapView);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap = mv.getMap();
        mBaiduMap.setMapStatus(msu);
        listener = new BDGeoLocation.BDLocationChangeListener() {
            @Override
            public void setLocation(BDLocation bdlocation) {
                Log.e(TAG, bdlocation.getLongitude() + "----" + bdlocation.getLatitude() + "----" + bdlocation.getAddrStr());
                // 定位信息更新同时更新地图位置
                updateMapsStatus(bdlocation);
                location = bdlocation;
            }
        };
        // 注册监听地图位置信息变化
        geoBD.addChangeListener(listener);
        // 首次加载获取定位信息更新地图
        updateMapsStatus(location);
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapStatusChangeListener(this);
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
    }

    /**
     * @param mapStatus
     */
    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    /**
     * @param mapStatus
     */
    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    /**
     * @param mapStatus
     */
    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        Log.i(App.LOG_TAG, "onMapStatusChangeFinish--------------------------------------------");
        // 获取当前比例尺
        int index = (SCALES.length + 2) - (int) mapStatus.zoom;
        if (index < SCALES.length - 1 && index >= 0) {
            moveDist = SCALES[index];
        }
        Log.i(App.LOG_TAG, "-----------------------moveDist:" + moveDist + "--------------");
        // 获取当前zoom 作为地图缩放显示数据的基数
        Log.i(App.LOG_TAG, "-----------------------zoom: " + (int) mapStatus.zoom + "--------------");
        if (isZoomed) {
            mZoom = (int) mapStatus.zoom;
            isZoomed = false;
            // 在获取数据之前做相应的处理
            if (blindCounty != null && blindCounty.equals("市辖区")) {
                blindCounty = "";
            }
            if (blindCounty != null && blindCounty.equals("州辖区")) {
                blindCounty = "";
            }
            mTarget = mapStatus.target;
            // 地图会进行clear
            isBuildCellBs = false;
            getMapLoc(mapStatus.target, blindArea, blindCounty);
            if (!isStartedRefreshRun) {
                hrBsInfo.postDelayed(refreshBsInfo, 300000);
                isStartedRefreshRun = true;
            }
        }
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

        if (isFirstIn) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(msu);
            isFirstIn = false;
        }
        // 更新地址
        address = location.getAddrStr();
    }


    private void getBSMapInfo(final LatLng latLng, final String blindArea, final String blindCounty) {
        final OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
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
                        // Log.i(TAG, "data" + data.toString());
                        critacalList.clear();
                        majorList.clear();
                        minorList.clear();
                        warningList.clear();
                        normalList.clear();
                        bsList.clear();
                        BaseStation bs;
                        for (int i = 0; i < data.length(); i++) {
                            bs = new BaseStation(data.getJSONArray(i).getString(0),
                                    data.getJSONArray(i).getString(1),
                                    data.getJSONArray(i).getString(2),
                                    data.getJSONArray(i).getString(3),
                                    data.getJSONArray(i).getString(4),
                                    data.getJSONArray(i).getString(5),
                                    data.getJSONArray(i).getString(6),
                                    data.getJSONArray(i).getString(7),
                                    data.getJSONArray(i).getString(8)

                            );
                            if (bs.getType().equals("critical")) {
                                critacalList.add(bs);//严重警告
                            } else if (bs.getType().equals("major")) {
                                majorList.add(bs);//主要警告
                            } else if (bs.getType().equals("minor")) {
                                minorList.add(bs);//次要告警
                            } else if (bs.getType().equals("warning")) {
                                warningList.add(bs);//一般告警
                            } else {
                                normalList.add(bs);//正常
                            }
                            if (bs.getBsNames().equals("金平金水河老刘")) {
                                // Logger.json(data.getJSONArray(i).toString());
                            }
                           /* if (bs.getBsNetType().equals("3G") || bs.getBsNetType().equals("4G")) {
                                Logger.t(TAG).json(data.getJSONArray(i).toString());
                            }*/
                        }
                        critacalBadge.setText(critacalList.size() + "");
                        majorBadge.setText(majorList.size() + "");
                        minorBadge.setText(minorList.size() + "");
                        warningBadge.setText(warningList.size() + "");
                        normalBadge.setText(normalList.size() + "");
                        // 首先加载异常基站
                        bsList.addAll(critacalList);
                        bsList.addAll(majorList);
                        bsList.addAll(minorList);
                        bsList.addAll(warningList);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        // 接入基站
                        if (!isBuildCellBs) {
                            buildCellBaseStation();
                            isBuildCellBs = true;
                        }
                        // 同一站址有多个告警的情况，按最高级别来渲染
                        // 挑选出最高告警级别的基站
                        showBSToMap(true);
                        //每次登陆都进行网站测试

                        if (speedTest == true && autoHttpTest == true) {
                            startHttpTest();
                            speedTest = false;
                        }
                        //初始化教学图片
                        if (!isShowedOperatorTip) {
                            initTeachPicture();
                            isShowedOperatorTip = true;
                        }
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "critacalList" + critacalList.toString() + ":" + "majorList" + majorList.toString() + ":" + "minorList" + minorList.toString() + ":" + "normalList" + normalList.toString());
            }
        };
        FuncGetSignBsInfo func = new FuncGetSignBsInfo(context);
        func.getData(lr, moveDist, String.valueOf(latLng.longitude), String.valueOf(latLng.latitude), blindArea, blindCounty);
    }

    private void checkGPS() {
        if (!App.isGpsOpen(context)) {
            PromptDailog prompt = new PromptDailog(context, null,
                    "GPS定位功能未开启，为了使用本软件相关功能，请打开手机GPS功能！", "设置",
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

    private void deleteCache() {
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        String dateItem = share.getString(
                DataSiteStart.SHARE_DATEITEM_CLEAR_IT_CACHE, "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        if (!dateItem.equals(sdf.format(new Date()))) {
            DBInspectHandler dbh = new DBInspectHandler(context,
                    IDBHandler.MODE_WRITE_DATABASE);
            dbh.clearDayCache();
        }
    }

    //在地图上显示基站信息

    /**
     * @param neededFilter 是否需要过滤掉低级告警
     */
    private void showBSToMap(boolean neededFilter) {
        Log.i(App.LOG_TAG, "showBSToMap");
        // 用户退出时、地图被销毁
        if (mv == null) {
            return;
        }

        // 清楚基站信息
        for (Marker marker : currMarkers) {
            if (marker != null)
                marker.remove();
        }
        currMarkers.clear();
        // 添加基站信息
        // 当不需要删除前一个告警级别的基站时调至此处进行执行
        scene1:
        for (BaseStation bs : bsList) {
            // 基站经纬度传回来的存在""
            if (bs.getBsLatitude().equals("") || bs.getBsLongitude().equals("")) {
                continue;
            }
            // 同一站址有多个告警的情况，按最高级别来渲染
            // 挑选出最高告警级别的基站
            if (bs.getBsIds().equals("13151")) {
                ALog.d(bs.toString());
            }
            if (neededFilter) {
                for (Marker marker : currMarkers) {
                    if (marker == null) {
                        continue;
                    }
                    Bundle bundle = marker.getExtraInfo();
                    if (bundle == null) {
                        continue;
                    }
                    String bsId = bundle.getString("BsID");
                    String bsAlarmType = bundle.getString("BsAlarmType");
                    // Logger.d(bsId + "------------------" + bsAlarmType);
                    if (bsId.equals(bs.getBsIds())) {
                        if (isNeededRemovePreMarker(bsAlarmType, bs.getType())) {
                            ALog.d(bs.toString());
                            marker.remove();
                        } else {
                            // Logger.d(bs.toString());
                            continue scene1;
                        }
                    }
                }
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
                if (bs.getGeneration().equals("3G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_critical_3);
                } else if (bs.getGeneration().equals("4G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_critical_4);
                } else {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_critical);
                }
            } else if (bs.getType().equals("major")) {//严重告警
                if (bs.getGeneration().equals("3G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_major_3);
                } else if (bs.getGeneration().equals("4G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_major_4);
                } else {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_major);
                }

            } else if (bs.getType().equals("minor")) {//异常
                if (bs.getGeneration().equals("3G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_minor_3);
                } else if (bs.getGeneration().equals("4G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_minor_4);
                } else {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_minor);
                }
            } else if (bs.getType().equals("warning")) {//一般警告
                if (bs.getGeneration().equals("3G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_warning_3);
                } else if (bs.getGeneration().equals("4G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_warning_4);
                } else {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_warning);
                }
            } else {//正常
                ALog.d(bs.getGeneration());
                if (bs.getGeneration().equals("3G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_nomall_3);
                } else if (bs.getGeneration().equals("4G")) {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_nomall_4);
                } else {
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_nomall);
                }
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
            bundle.putString("BsType", bs.getGeneration());
            bundle.putString("BsAddress", bs.getBsBtsId());
            bundle.putString("BsAlarmType", bs.getType());
            option.extraInfo(bundle);
            // 用户退出时、地图被销毁
            if (mv == null) {
                return;
            }
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            currMarkers.add(marker);
            // markerList.add(marker);
            // used bundle replace it
            // marker.setZIndex(Integer.valueOf(bs.getBsIds()));
            // marker.setTitle("站址名称:" + bs.getBsName() + "\n" + "站址信息:" + bs.getBsType());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mBaiduMap.hideInfoWindow();
        // TODO Auto-generated method stub
        if (marker == null) {
            return false;
        }
        Bundle bundle = marker.getExtraInfo();
        mPopInfoTv.setText("站址名称:" + bundle.getString("BsName")
                + "\n" + "站址信息:" + bundle.getString("BsBsc")
                + "\n" + "告警信息:" + bundle.getString("BsAlarmInfo")
                + "\n" + "站址类型:" + bundle.getString("BsType")
                + "\n" + "地址:" + bundle.getString("BsAddress"));
        InfoWindow mInfoWindow = new InfoWindow(mPopView, marker.getPosition(), -47);
        mBaiduMap.showInfoWindow(mInfoWindow);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
        mBaiduMap.setMapStatus(update);
        currentID = bundle.getString("BsID");
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        // TODO
        return false;
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

    /**
     * show the site alarm info
     *
     * @param v
     */
    public void showAlarmInfoClick(View v) {
        mBaiduMap.hideInfoWindow();
        Intent toShowAlarmInfo = new Intent(this, PhysicalSiteAlarmInfoActivity.class);
        toShowAlarmInfo.putExtra("BsId", currentID);
        startActivity(toShowAlarmInfo);
    }
    //在地图上显示基站信息
/*    private void showBSToMap(LatLng latLng) {
        // 用户退出时、地图被销毁
        if (mv == null) {
            return;
        }
        mBaiduMap.clear();
        if (mActionMenu != null) {
            mActionMenu.close(true);
        }
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
            Marker marker = (Marker) mBaiduMap.addOverlay(new MarkerOptions().position(desLatLng1).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            marker.setTitle("中心点标记");

        } else {
            if (datasiteSearch) {
                Toast.makeText(context, "未找到您查询的基站，请从新查询....", Toast.LENGTH_LONG).show();
            }
        }

        int total = bsList.size();
        for (BaseMapBean bs : bsList) {
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

            switch (bs.getMainType()) {
                case 0://基站
                    if (bs.getBsState().equals("critical")) {//紧急告警
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.bsstate_critical);
                    } else if (bs.getBsState().equals("major")) {//严重告警
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.bsstate_major);
                    } else if (bs.getBsState().equals("minor")) {//异常
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.bsstate_minor);
                    } else {//正常
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.bsstate_nomall);
                    }
                    break;
                case 1://人
                    Random randomP = new Random(); // 定义随机类
                    int numP = randomP.nextInt(100);// 返回[0,10)集合中的整数，注意不包括10
                    if (numP % 4 == 0) {
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.little_boy_blue);
                    } else if (numP % 4 == 1) {
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.little_boy_red);
                    } else if (numP % 4 == 2) {
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.little_boy_yellow);
                    } else if (numP % 4 == 3) {
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.little_boy_yellow);
                    } else {
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.little_boy_blue);
                    }
                    break;
                case 2://车
                    Random randomC = new Random(); // 定义随机类
                    int numC = randomC.nextInt(10);// 返回[0,10)集合中的整数，注意不包括10
                    if (numC % 2 == 0) {
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.jeep_car);
                    } else {
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.lorry_car);
                    }
                    break;
                default:
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.bsstate_nomall);
                    break;
            }


            OverlayOptions option = new MarkerOptions()
                    .title(bs.getBsName())
                    .position(desLatLng)
                    .icon(bitmap);
            // 用户退出时、地图被销毁
            if (mv == null) {
                return;
            }
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            // markerList.add(marker);
            marker.setZIndex(Integer.valueOf(bs.getBsIds()));
            marker.setTitle("基站名称:" + bs.getBsName() + "\n" + "基站信息:" + bs.getOtherInfo());

        }
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (mActionMenu != null) {
                    mActionMenu.close(true);
                }
                View view = LayoutInflater.from(context).inflate(R.layout.marker_title, null); //自定义气泡形状
                final TextView mainActionView = (TextView) view.findViewById(R.id.marker_title);
                mainActionView.setText(marker.getTitle());
                double latitude, longitude;
                latitude = marker.getPosition().latitude;
                longitude = marker.getPosition().longitude;
                final LatLng pt = new LatLng(latitude + 0.0004, longitude + 0.00005);

                SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(MainActivity.this);
                ImageView rlIcon1 = new ImageView(context);
                ImageView rlIcon2 = new ImageView(context);
                ImageView rlIcon3 = new ImageView(context);

                rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat));
                rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera));
                rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video));

                mActionMenu = new FloatingActionMenu.Builder(MainActivity.this)
                        .setStartAngle(-45)
                        .setEndAngle(-135)
                        .setRadius(getResources().getDimensionPixelSize(R.dimen.radius_large))
                        .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                        .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                        .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                        // listen state changes of each menu
                        //  .setStateChangeListener(this)
                        .attachTo(mainActionView)
                        .build();

                *//*for (BaseMapBean bs : bsList) {
                    if (bs.getBsIds() == String.valueOf(marker.getZIndex())) {
                        title.setText(marker.getTitle());
                        title.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBaiduMap.hideInfoWindow();
                            }
                        });
                    }
                }*//*
               *//* mainActionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                    }
                });*//*
                mInfoWindow = new InfoWindow(view, pt, 0);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }*/

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
    private void getMapLoc(final LatLng sourceLatLng, final String blindArea, final String blindCounty) {
        Log.d(App.LOG_TAG, "getMapLoc");
        // Log.d(App.LOG_TAG, "longitude: " + sourceLatLng.longitude + "  latitude: " + sourceLatLng.latitude);
        if (sourceLatLng != null) {
            // Toast.makeText(this, "定位成功", Toast.LENGTH_SHORT).show();
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
            double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
            double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
            LatLng centerLatLng = new LatLng(latitude, longitude);
            getBSMapInfo(centerLatLng, blindArea, blindCounty);

        } else {
            Toast.makeText(this, "定位中", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        // 初始化权限
        Menu menu = navigationView.getMenu();
        if (userType.equals("1")) {
            menu.findItem(R.id.nav_NewInspectBS).setVisible(false);
            menu.findItem(R.id.nav_InspectHistory).setVisible(false);
            menu.findItem(R.id.nav_AssetsInfo).setVisible(false);
        }
        View headerLayout = navigationView.getHeaderView(0);
        mLogoutIv = (ImageView) headerLayout.findViewById(R.id.logoutIv);
        mLogoutIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        sharedPreferences.edit()
                                .putString(AppCheckLogin.SHARE_USER_NAME, "")
                                .commit();
                        sharedPreferences.edit()
                                .putString(AppCheckLogin.SHARE_ORG_CODE, "")
                                .commit();
                        sharedPreferences.edit().putString(AppCheckLogin.SHARE_USER_ID, "")
                                .commit();
                        sharedPreferences.edit()
                                .putString(AppCheckLogin.SHARE_USER_PWD, "")
                                .commit();
                        sharedPreferences.edit()
                                .putString(AppCheckLogin.SHARE_USER_TYPE, "")
                                .commit();
                        MainApp.RESTART_CODE = MainApp.CODE_RELOGIN;
                        final Intent intent = new Intent(context, WelcomeActivity.class);
                        context.startActivity(intent);
                        finish();
                    }
                };
                AlertDialog ad = App.alertDialog(context, "提示！", "注销当前帐号？",
                        dlr, null);
                ad.show();
            }
        });
        mUserNameTv = (TextView) headerLayout.findViewById(R.id.userNameTv);
        mAccountTv = (TextView) headerLayout.findViewById(R.id.accountTv);
        String userName = sharedPreferences.getString(AppCheckLogin.SHARE_USER_NAME, "");
        String userId = sharedPreferences.getString(AppCheckLogin.SHARE_USER_ID, "");
        mUserNameTv.setText("用户：" + userName);
        mAccountTv.setText("帐号：" + userId);

        critacalIv = (ImageView) findViewById(R.id.badge_bsstate_critical);
        findViewById(R.id.layout_bsstate_critical).setOnClickListener(this);
        critacalBadge = new BadgeView(this, critacalIv);
        critacalBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        critacalBadge.setBadgeBackgroundColor(Color.parseColor("#A4C639"));
        critacalBadge.setText("0");
        critacalBadge.show();

        majorIv = (ImageView) findViewById(R.id.badge_bsstate_major);
        findViewById(R.id.layout_bsstate_major).setOnClickListener(this);
        majorBadge = new BadgeView(this, majorIv);
        majorBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        majorBadge.setBadgeBackgroundColor(Color.parseColor("#A4C639"));
        majorBadge.setText("0");
        majorBadge.show();

        minorIv = (ImageView) findViewById(R.id.badge_bsstate_minor);
        findViewById(R.id.layout_bsstate_minor).setOnClickListener(this);
        minorBadge = new BadgeView(this, minorIv);
        minorBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        minorBadge.setBadgeBackgroundColor(Color.parseColor("#A4C639"));
        minorBadge.setText("0");
        minorBadge.show();

        warningLv = (ImageView) findViewById(R.id.badge_bsstate_warning);
        findViewById(R.id.layout_bsstate_warning).setOnClickListener(this);
        warningBadge = new BadgeView(this, warningLv);
        warningBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        warningBadge.setBadgeBackgroundColor(Color.parseColor("#A4C639"));
        warningBadge.setText("0");
        warningBadge.show();


        normalIv = (ImageView) findViewById(R.id.badge_bsstate_nomall);
        findViewById(R.id.layout_bsstate_nomall).setOnClickListener(this);
        normalBadge = new BadgeView(this, normalIv);
        normalBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        normalBadge.setBadgeBackgroundColor(Color.parseColor("#A4C639"));
        normalBadge.setText("0");
        normalBadge.show();

        blindAreaSp = (Spinner) findViewById(R.id.sp_blindArea);
        blindCountySp = (Spinner) findViewById(R.id.sp_blindCounty);

        // init marker click show view
        mPopView = LayoutInflater.from(context).inflate(R.layout.marker_title, null); //自定义气泡形状
        mPopInfoTv = (TextView) mPopView.findViewById(R.id.marker_title);

        // 切换至列表形式
        switchViewBtn = (ImageButton) findViewById(R.id.switchViewBtn);
        switchViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String area = blindArea;
                String county = blindCounty;
                double lat = 4.9E-324;
                double lon = 4.9E-324;
                if (mTarget == null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                } else {
                    lat = mTarget.latitude;
                    lon = mTarget.longitude;
                }
                if (area == null) {
                    area = "昆明市";
                }
                if (county == null) {
                    county = "";
                }
                Intent intent = new Intent(context, PhysicalSiteAlarmListActivity.class);
                intent.putExtra("longitude", lon);
                intent.putExtra("latitude", lat);
                intent.putExtra("area", area);
                intent.putExtra("county", county);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.searchMenu) {
            Intent toSearch = new Intent(this, SearchActivity.class);
            startActivity(toSearch);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_plan) {
            // 规划阶段
            Intent toPhysicalSiteMap = new Intent(this, PhysicalSiteMapActivity.class);
            toPhysicalSiteMap.putExtra("stage", 1);
            this.startActivity(toPhysicalSiteMap);
        } else if (id == R.id.nav_pay) {
            // 交付阶段
            Intent toPhysicalSiteMap = new Intent(this, PhysicalSiteMapActivity.class);
            toPhysicalSiteMap.putExtra("stage", 2);
            this.startActivity(toPhysicalSiteMap);
        } else if (id == R.id.nav_netIn) {
            // 入网阶段
            Intent toPhysicalSiteMap = new Intent(this, PhysicalSiteMapActivity.class);
            toPhysicalSiteMap.putExtra("stage", 3);
            this.startActivity(toPhysicalSiteMap);
        } else if (id == R.id.nav_maintenance) {
            // 交维阶段
            Intent toPhysicalSiteMap = new Intent(this, PhysicalSiteMapActivity.class);
            toPhysicalSiteMap.putExtra("stage", 4);
            this.startActivity(toPhysicalSiteMap);
        } else if (id == R.id.nav_task) {
            // 维护作业
            Intent toPhysicalSiteMap = new Intent(this, PhysicalSiteMapActivity.class);
            toPhysicalSiteMap.putExtra("stage", 5);
            this.startActivity(toPhysicalSiteMap);
        } else if (id == R.id.nav_repair) {
            // 整治网优
            Intent toPhysicalSiteMap = new Intent(this, PhysicalSiteMapActivity.class);
            toPhysicalSiteMap.putExtra("stage", 6);
            this.startActivity(toPhysicalSiteMap);
        } else if (id == R.id.nav_remove) {
            // 站址拆迁
            Intent toPhysicalSiteMap = new Intent(this, PhysicalSiteMapActivity.class);
            toPhysicalSiteMap.putExtra("stage", 7);
            this.startActivity(toPhysicalSiteMap);
        } else if (id == R.id.nav_AssetsInfo) {
            //资产清查
            Intent toAssets = new Intent(this, AssetsInfoActivity.class);
            this.startActivity(toAssets);
        } else if (id == R.id.nav_ThreeInfo) {
            //资产盘点
            Intent toThreeAssets = new Intent(this, AssetMainActivity.class);
            this.startActivity(toThreeAssets);
        } else if (id == R.id.nav_InspectSign) {
            //巡检签到
            Intent toInspect = new Intent(this, InspectSignActivity.class);
            this.startActivity(toInspect);
        } else if (id == R.id.nav_NewInspectBS) {
            //物理站址巡检
            Intent toNewInspect = new Intent(this, NewInspectBSActivity.class);
            this.startActivity(toNewInspect);
        } else if (id == R.id.nav_InspectHistory) {
            //巡检记录
            Intent toInspectHis = new Intent(this, InspectHistoryActivity.class);
            this.startActivity(toInspectHis);
        } else if (id == R.id.nav_toolbox) {
            //工具箱
            Intent toToolBox = new Intent(this, ToolboxActivity.class);
            this.startActivity(toToolBox);
        } else if (id == R.id.nav_knowledge) {
            //知识库
            Intent toKnowledge = new Intent(this, KnowledgeBaseActivity.class);
            startActivity(toKnowledge);
        } else if (id == R.id.nav_wlte) {
            //网络感知
            Intent toLte = new Intent(this, LteMapMainActivity.class);
            startActivity(toLte);
        } else if (id == R.id.nav_settings) {
            Intent toChangePW = new Intent(this, SettingActivity.class);
            startActivity(toChangePW);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
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
        geoBD.removeChangeListener(listener);
        geoBD.locClient.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启百度定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!geoBD.locClient.isStarted()) {
            geoBD.locClient.start();

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
            case R.id.layout_bsstate_critical:
                if (blindArea != null && blindCounty != null && critacalIvState == false) {
                    critacalIvState = true;
                    normalIvState = false;
                    minorIvState = false;
                    majorIvState = false;
                    warningState = false;
                    bsList.clear();
                    bsList.addAll(critacalList);
                    showBSToMap(false);
                }
                break;
            case R.id.layout_bsstate_major:
                if (blindArea != null && blindCounty != null && majorIvState == false) {
                    majorIvState = true;
                    normalIvState = false;
                    minorIvState = false;
                    critacalIvState = false;
                    warningState = false;
                    bsList.clear();
                    bsList.addAll(majorList);
                    showBSToMap(false);
                }
                break;
            case R.id.layout_bsstate_minor:
                if (blindArea != null && blindCounty != null && minorIvState == false) {
                    normalIvState = false;
                    minorIvState = true;
                    majorIvState = false;
                    critacalIvState = false;
                    warningState = false;
                    bsList.clear();
                    bsList.addAll(minorList);
                    showBSToMap(false);
                }
                break;

            case R.id.layout_bsstate_warning:
                if (blindArea != null && blindCounty != null && warningState == false) {
                    normalIvState = false;
                    minorIvState = false;
                    majorIvState = false;
                    critacalIvState = false;
                    warningState = true;
                    bsList.clear();
                    bsList.addAll(warningList);
                    showBSToMap(false);
                }
                break;

            case R.id.layout_bsstate_nomall:
                blindCounty = blindCountySp.getSelectedItem().toString();
                if (blindArea != null && blindCounty != null) {
                    if (normalIvState == false) {
                        if (blindCounty.equals("市辖区") || blindCounty.equals("州辖区")) {
                            Toast.makeText(context, "选择对应【区县】查看....", Toast.LENGTH_SHORT).show();

                        } else {
                            normalIvState = true;
                            minorIvState = false;
                            majorIvState = false;
                            critacalIvState = false;
                            warningState = false;
                            bsList.clear();
                            bsList.addAll(normalList);
                            showBSToMap(false);
                        }
                    }

                }
                break;
            default:
                break;
        }
    }

    public boolean isChinese(String chineseContent) {
        if (chineseContent == null) return false;
        String reg = "[\\u4e00-\\u9fa5]+";//表示+表示一个或多个中文
        return chineseContent.matches(reg);
    }

    // 回到定位点并获取数据
    private void backToCenter() {
        // datasiteSearch = false;
        // mBaiduMap.clear();
        if (geoBD.location != null) {
            LatLng latLng = new LatLng(geoBD.location.getLatitude(), geoBD.location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.setMapStatus(update);
            /*if (latLng != null) {
                getMapLoc(latLng);
            }*/
        }
    }

    // 退出时间
    private long currentBackPressedTime = 0;

    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 3000;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {// 截取返回按钮事件，弹出提示框
            if ((System.currentTimeMillis() - currentBackPressedTime) > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

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
                /*signalMap.put("CDMA_Signal",
                        Integer.toString(signal.getCdmaDbm()));*/
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
            //singalChange();
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            // TODO Auto-generated method stub
            super.onDataConnectionStateChanged(state);
            //singalChange();
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


    private void startHttpTest() {
        dialogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //buildResultTable();
                        break;
                    case 1: {
                        //moreInfoBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "网站测试完成！",
                                Toast.LENGTH_SHORT).show();
                        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
                        Log.d(TAG, busiJsonDatas.toString());
                        PostBusiSiteData post = new PostBusiSiteData(context);
                        //服务端返回的数据在这里回调
                        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

                            @Override
                            public void onFinished(String output) {
                                // TODO Auto-generated method stub
                                //只执行一次回调
                                if (httptestTime == 5) {
                                    JSONObject responseJson = null;
                                    String result = null;
                                    String msg = null;
                                    Log.d(TAG, "output:" + output);
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
                                        } else {
                                            Toast.makeText(context, "上传失败",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
/*                            if (output.equals(AppHttpClient.RESULT_FAIL)) {

                            } else {
                                Toast.makeText(context, "测试结果上传成功！下一步请进行视频测试！",
                                        Toast.LENGTH_LONG).show();
                            }*/
                                    httptestTime = 0;

                                } else {
                                    httptestTime++;
                                    Log.d(TAG, "httptestTime" + httptestTime);
                                }
                            }


                        };
                        post.postDataBaidu(lr, LTE, upLoadList, upLoadState);
                        post.postDataLeshiwang(lr, LTE, upLoadList, upLoadState);
                        post.postDataTaobao(lr, LTE, upLoadList, upLoadState);
                        post.postDataXinglang(lr, LTE, upLoadList, upLoadState);
                        post.postDataWangyi(lr, LTE, upLoadList, upLoadState);
                        post.postDataZhangshangyingyeting(lr, LTE, upLoadList, upLoadState);
                        Log.i(TAG, "postData执行");
                        Log.i(TAG, upLoadList.toString());

                    /*Intent intent = new Intent(HttpTestActivity.this, HttpTestRecordsDetailActivity.class);
                    intent.putExtra("data", testTime);
                    startActivity(intent);*/
                    }
                    break;
                    default:
                        break;
                }
            }
        };
        dialogHandler.post(new Runnable() {
            @Override
            public void run() {
                reCollectInfo();
                initCollectInfo();
                getSysInfo();
                buildList();
                startTest();
                Log.d(TAG, "startHttpTest执行完毕");
            }
        });


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
        if (geoBD.locClient != null && geoBD.locClient.isStarted()) {
            geoBD.locClient.requestLocation();
            BDLocation loc = geoBD.location;
            if (loc != null && loc.hasAddr()) {
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
        listLTEMain = sys.getSystemInfo(this, listLTEMain);
        listLTEMain.get(getPosition("Battery_free")).setDataValue(
                Integer.toString(level));
        //Sim卡和运营商信息
        InfoSim sim = new InfoSim(this);
        listLTEMain = sim.getSimInfo(listLTEMain);
        //基站信息
        InfoCell cell = new InfoCell(context);
        listLTEMain = cell.getCellInfo(listLTEMain);
        listLTEMain = cell.getInitLTECell(listLTEMain);

        //Connectivity-Info
        InfoConnectivity ct = new InfoConnectivity(context);
        listLTEMain = ct.getConectiviyInfo(listLTEMain);
        wifi = new InfoWifi(this);
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

    private void initCollectInfo() {
        if (list == null) {
            list = new ArrayList<Collector>();
        }
        list.clear();

        // add the location
        addr = "";
        double lat = 0;
        double lon = 0;
        //if (geoBD.locClient != null) {
        geoBD.locClient.requestLocation();
        // BDLocation loc = geoBD.location;
        if (location != null && location.hasAddr()) {
            addr = location.getAddrStr();
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.d(TAG, "获取到位置信息" + addr + "" + lat + "" + lon);
        }
        // }
        list.add(new Collector("坐标位置信息", "", 0, Collector.DATA_TYPE_TITLE));
        list.add(new Collector("Lat", lat + "", 0, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("Lon", lon + "", 0, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("Addr", addr, 0, Collector.DATA_TYPE_UPLOAD));
        InfoCell cell = new InfoCell(context);
        list = cell.getCellInfo(list);
        InfoConnectivity ct = new InfoConnectivity(context);
        list = ct.getConectiviyInfo(list);
        Log.d(TAG, "显示获取到的位置信息" + addr + "" + lat + "" + lon);
        Log.d(TAG, "initCollectInfo执行完毕");
    }

    private void getSysInfo() {
        busiJsonDatas = new JSONObject();
        FuncGetSysDatas fgsd = new FuncGetSysDatas(context);
        busiJsonDatas = fgsd.getSysJSON();
        address = addr;
        Log.d(TAG, "getSysInfo" + address);
        try {
            addr = URLEncoder.encode(addr, CoConfig.ENCODE_UTF8);
            busiJsonDatas.put("addr", addr);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (testTime == null) {
            Calendar cr = Calendar.getInstance(Locale.CHINA);
            testTime = CoConfig.getDateToStr(cr, 0);
        }
        try {
            testTime = busiJsonDatas.getJSONObject("sim").getString("dateItem");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getSysInfo执行完毕");
    }

    private void startTest() {
//		tvTestResult.setText("");
//        moreInfoBtn.setVisibility(View.GONE);
//        layout.removeAllViews();
        singalChange();
        if (!AppNetWork.isNetWork(context)) {
            Toast.makeText(context, getString(R.string.common_not_network),
                    Toast.LENGTH_LONG).show();
            return;
        }
        GetWebSite func = new GetWebSite(this.getApplicationContext());
        for (int i = 0; i < listSites.size(); i++) {
            listSites.get(i).setState(WebSite.STATE_WAIT);
        }

        OnGetDataFinishListener lr = new OnGetDataFinishListener() {

            public void onFinishJSON(JSONObject output) {
                // TODO Auto-generated method stub
                try {
                    busiJsonDatas.put("busiResult", output);
                    Log.d(CoConfig.LOG_TAG + "-网站测试数据-", output.toString());
                    //MainActivity.mainTestResult.put("busiResult", output);
                    dialogHandler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinishList(List list) {
                upLoadList = list;
            }

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                testResult = output;
                dialogHandler.sendEmptyMessage(0);
//				bsr = new BusiSitesResult(tvTestResult, testResult);
//				bsr.execute();

            }
        };
        func.getData(lr, listSites, saMenuItems, testTime, address);
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
                LTE.put("Ecio", listLTEMain.get(getPosition("EVDO_Ecio")).getDataValue());
                LTE.put("Snr", listLTEMain.get(getPosition("EVDO_Snr")).getDataValue());
                LTE.put("LteRsrq", listLTEMain.get(getPosition("LTE_RSRQ")).getDataValue());
                Log.d(TAG, "IMEI" + listLTEMain.get(getPosition("IMEI")).getDataValue());
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
                LTE.put("Ecio", listLTEMain.get(getPosition("EVDO_Ecio")).getDataValue());
                LTE.put("Snr", listLTEMain.get(getPosition("EVDO_Snr")).getDataValue());
                LTE.put("LteRsrq", listLTEMain.get(getPosition("LTE_RSRQ")).getDataValue());
            }
            Log.d(TAG, "lte" + LTE.toString());

            /*LTE.put("LTE_dBm", list.get(getPosition("LTE_dBm")).getDataValue() + "dBm");
            LTE.put("LTE_Signal", list.get(getPosition("LTE_Signal")).getDataValue() + "dBm");
            LTE.put("LTE_RSRQ", list.get(getPosition("LTE_RSRQ")).getDataValue());
            LTE.put("LTE_Mcc", list.get(getPosition("LTE_Mcc")).getDataValue());
            LTE.put("LTE_Mnc", list.get(getPosition("LTE_Mnc")).getDataValue());
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "startTest执行完毕");

    }

    private void buildList() {
        // TODO Auto-generated method stub
        listSites = new ArrayList<WebSite>();
        String[] sitenames = context.getResources().getStringArray(
                R.array.sitesBusiName);
        String[] urls = context.getResources().getStringArray(R.array.sitesBusiUrls);

        int[] imgs = {R.drawable.baidu, R.drawable.leshi, R.drawable.taobao,
                R.drawable.xinlang, R.drawable.wangyi_news, R.drawable.ctchandhold};
        for (int i = 0; i < sitenames.length; i++) {
            WebSite site = new WebSite();
            site.setName(sitenames[i]);
            site.setIconRes(imgs[i]);
            site.setUrl(urls[i]);
            site.setChecked(true);
            site.setClassify(0);
            site.setId(i);
            site.setState(WebSite.STATE_WAIT);
            listSites.add(site);
        }
        saMenuItems = new SiteGridAdapter(context, listSites);
        // 添加Item到网格中
        //gridSites.setAdapter(saMenuItems);
        // gridSites.setOnItemClickListener(gridClt);
        // showPopupWindow(saMenuItems);
        Log.d(TAG, "buildList执行完毕");
    }

    private void initDistrictSearch() {
        // searchInCity = (Button) this.findViewById(R.id.btn_searchInCity);
        mDistrictSearch = DistrictSearch.newInstance();
        mDistrictSearch.setOnDistrictSearchListener(this);


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
                Log.i(App.LOG_TAG, "polyLines is : " + polyline.toString());
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

    /**
     * 获取数据库行政区划数据初始化行政区划列表
     */
    private void queryRegionDataToInitSp() {
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
                getTheTwoRegionAdapterSp();
            }

            @Override
            public void onUpdate(Object o) {

            }
        }.execute();
    }

    //每次打开app都进网站测试，测试结果显示到Popupwindow上
    private void showPopupWindow(SiteGridAdapter adapter) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_httptest, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        //popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_white_disabled));
        Button firstHttpTest = (Button) contentView.findViewById(R.id.btn_firsthttptest);
        firstHttpTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                //初始化教学图片
                if (!isShowedOperatorTip) {
                    initTeachPicture();
                    isShowedOperatorTip = true;
                }
            }
        });
        GridView gridView = (GridView) contentView.findViewById(R.id.grid_firsthttptest_sites);
        gridView.setAdapter(adapter);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                //拦截后会同porgressdialog冲突，可以优化，但是要线程延长执行时间
            }
        });
        popupWindow.setFocusable(true);

        //popupWindow.showAsDropDown(toolbar, 0,0,Gravity.CENTER);
        popupWindow.showAtLocation(toolbar, Gravity.CENTER, 0, 0);
        //popupWindow.showAsDropDown(View);
    }

    /**
     * 判断两个基站的告警等级
     *
     * @param addedBs
     * @param currentBs
     * @return 是否要重新绘制基站
     */
    private boolean isNeededRemovePreMarker(String addedBs, String currentBs) {
        // Logger.d(addedBs + "------------------" + currentBs);
        String[] alarmTypes = {"warning", "minor", "major", "critical"};
        int addedIndex = find(alarmTypes, addedBs);
        int currentIndex = find(alarmTypes, currentBs);
        // ALog.d(addedIndex + "------------------" + currentIndex);
        return currentIndex > addedIndex;
    }

    public int find(String[] arr, String str) {
        boolean flag = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(str)) {
                flag = true;
                return i;
            }
        }
        if (flag == false) {
            return -1;
        }
        return -1;
    }
}
