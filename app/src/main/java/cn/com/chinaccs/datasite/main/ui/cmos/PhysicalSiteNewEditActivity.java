package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.XFastFactory.XDbUtils.XDbUtils;
import com.XFastFactory.XTask.XSimpleTask;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.common.WGSTOGCJ02;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetUserArea;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.RegionInfoItem;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.gps.GpsSatellite;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.ui.functions.FuncGetPlanSiteList;
import cn.com.chinaccs.datasite.main.ui.models.BaseSiteBean;

/**
 * 基站规划
 * Created by AndyHua on 2015/11/6.
 * 新接口无论新建站点和站址都只用一个接口DSSaveNewAssets.do
 * 当已有站址时，后台只获取建设类型、设备制式、RRU、BBU
 * 当新建站址时，所有数据后台都获取
 */
public class PhysicalSiteNewEditActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private GpsHandler gpsHandler;
    private Button btnSub;
    private JSONArray array;
    private String id;
    private String name;
    // 物理站址名称
    private EditText zdWlzmcEt;
    private String zdWlzmcStr;
    // 物理站址编号
    private EditText zdWlzbhEt;
    private String zdWlzbhStr;
    // 当前选中物理站址ID
    private String bsId;
    private String bsName;
    // 所在省（直辖市）
    private EditText zdSfEt;
    private String zdSfStr;
    // 地市
    private EditText zdDsEt;
    private String zdDsStr;
    private Button zdDsBtn;
    private ProgressDialog proDialog;
    // 区县
    private EditText zdQxEt;
    private String zdQxStr;
    private Button zdQxBtn;
    private String[] areas;
    // 乡、镇、街道办事处
    private EditText zdXzjEt;
    private String zdXzjStr;
    // 行政村
    private EditText zdXzcEt;
    private String zdXzcStr;
    // 自然村村
    private EditText zdZrcEt;
    private String zdZrcStr;
    // 详细地址（位置名称、小区、门牌号）
    private EditText zdXxdzEt;
    private String zdXxdzStr;
    // 现场采集经度
    private EditText zdCjjdEt;
    private String zdCjjdStr;
    private Button zdCjjdBtn;
    // 现场采集纬度
    private EditText zdCjwdEt;
    private String zdCjwdStr;
    private Button zdCjwdBtn;

    //覆盖区域类型spinner
    private Spinner spZdFgqylx;
    private EditText zdFgqylxEt;
    private String zdFgqylx = "1";

    // 站址来源
    private Spinner spzZdZzly;
    private EditText zdZzlyEt;
    private String zdZzly = "1";

    // 站址类型
    private Spinner spZdZzlx;
    private EditText zdZzlxEt;
    private String zdZzlx = "1";

    // 站址状态
    private Spinner spZdZzzt;
    private EditText zdZzztEt;
    private String zdZzzt = "1";

    //RRU数量
    private EditText zdRRUEt;
    private String zdRRUStr;

    //BBU数量
    private EditText zdBBUEt;
    private String zdBBUStr;

    //新增自动获取位置信息功能
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
    // 盲点/劣点所在镇(乡)/街道
    public String blindTown;
    private String[] blindTowns;
    private Spinner blindTownSp;
    public String blindTownID;
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

    private final String TAG = PhysicalSiteNewEditActivity.class.getSimpleName();

    //行政区域搜索
    //private DistrictSearch mDistrictSearch;
    //百度地图定位位置信息
    BDLocation location;
    private BDGeoLocation geoBD;
    private String baiduCity;
    private String baiduDistrict;
    private String baiduStreet;
    private String baiduLocation;
    private String baiduStreetNumber;
    //百度坐标
    private double longtitude;
    private double latitude;
    //转换后的gps
    private double longitudeGPS;
    private double latitudeGPS;

    //判断typr 1为新建站点规划
    private int type = 1;
    //建设类型
    private Spinner spBuildType;
    private String[] arrayBuildType;
    // 默认建设类型-预规划
    private String zdBuildType = "1";
    //设备制式
    private Spinner spSbzs;
    private String[] arraySbzs;
    // 默认设备制式-CDMA
    private String zdSbzs = "1";

    //物理站址编号50米内的自动获取
    private Button bsSiteIDbtn;
    private ArrayList<BaseSiteBean> payList;
    private double longitudeBaidu;
    private double latitudeBaidu;
    private LatLng latlng1;
    private ArrayList<BaseSiteBean> less50mList; //小于50米的基站

    // GPS坐标信息
    private Location loc;
    private Handler hr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_physical_site_new);
        initToolbar("站点规划");
        gpsHandler = ((MainApp) getApplication()).gpsHandler;
        hr = new Handler();
        this.findViews();
        //初始化行政区域列表
        queryRegionDataToInitSp();
        TWO_LEVEL_LIST = new ArrayList<>();
        THREE_LEVEL_LIST = new ArrayList<>();
        THREE_LEVEL_LIST_FILTER = new ArrayList<>();
        FOUR_LEVEL_LIST = new ArrayList<>();
        FOUR_LEVEL_LIST_FILTER = new ArrayList<>();

        //    zdDsBtn.setOnClickListener(this);
        zdQxBtn.setOnClickListener(this);
        zdCjjdBtn.setOnClickListener(this);
        zdCjwdBtn.setOnClickListener(this);
        btnSub.setOnClickListener(subLr);
        setLocation();
        Intent intent = getIntent();
        longitudeGPS = intent.getExtras().getDouble("longitudeGPS");
        latitudeGPS = intent.getExtras().getDouble("latitudeGPS");
        longitudeBaidu = intent.getExtras().getDouble("longitudeBaidu");
        latitudeBaidu = intent.getExtras().getDouble("latitudeBaidu");
        latlng1 = new LatLng(latitudeBaidu, longitudeBaidu);
        zdCjjdEt.setText(longitudeGPS + "");
        zdCjwdEt.setText(latitudeGPS + "");
        LatLng latLng = new LatLng(latitudeGPS, longitudeGPS);

        getPhysicalSiteData(2, latLng);

    }

    //计算距离
    private void countDistance() {

        if (payList != null) {
            for (BaseSiteBean bs : payList
                    ) {
                double latitudeBS = Double.valueOf(bs.getBsLatitude());
                double longitudeBS = Double.valueOf(bs.getBsLongitude());
                //转换GPS为百度坐标
                LatLng latlngBS = new LatLng(latitudeBS, longitudeBS);
                CoordinateConverter coordinateConverter = new CoordinateConverter();
                coordinateConverter.from(CoordinateConverter.CoordType.GPS);
                coordinateConverter.coord(latlngBS);
                LatLng latlng2 = coordinateConverter.convert();
                //测算距离
                double distance = getDistance(latlng1, latlng2);
                if (distance <= 200) {
                    less50mList.add(bs);
                    ALog.d(less50mList.size());
                }
            }
        }

    }

    //百度坐标计算距离
    private double getDistance(LatLng latlng1, LatLng latlng2) {
        return DistanceUtil.getDistance(latlng1, latlng2);
    }


    //获取已建的物理基站 range 距离（公里）
    private void getPhysicalSiteData(int range, LatLng latLng) {
        FuncGetPlanSiteList funcGetPlanSiteList = new FuncGetPlanSiteList(context);
        less50mList = new ArrayList<BaseSiteBean>();
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {
            @Override
            public void onFinished(String output) {
                Log.i(TAG, "OnGetDataFinishedListener" + output);
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    ALog.json(output);
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray data = json.getJSONArray("data");
                        payList = new ArrayList<BaseSiteBean>();
                        BaseSiteBean bs;
                        //BaseSiteBean bsTemp = new BaseSiteBean();
                        for (int i = 0; i < data.length(); i++) {
                            bs = new BaseSiteBean(data.getJSONArray(i).getString(0), data.getJSONArray(i).getString(1),
                                    data.getJSONArray(i).getString(2), data.getJSONArray(i).getString(3), data.getJSONArray(i).getString(4),
                                    data.getJSONArray(i).getString(5), data.getJSONArray(i).getString(6)
                            );
                            payList.add(bs);


                        }
                        countDistance();

                        Log.i(TAG, "paylist" + payList.toString());

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
        funcGetPlanSiteList.getLocalPhysicalData(lr, range, latLng);
    }


    private void buildSp() {
        FuncGetUserArea func = new FuncGetUserArea(context);
        proDialog = null;
        proDialog = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        proDialog.show();
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                proDialog.dismiss();
                try {
                    getAdapterSp(output);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            ;
        };
        func.getData(lr);
    }

    private void getAdapterSp(String output) throws JSONException {
        if (output.equals(AppHttpConnection.RESULT_FAIL)) {
            Toast.makeText(context,
                    getResources().getString(R.string.common_not_network),
                    Toast.LENGTH_LONG).show();
        } else {
            JSONObject json = new JSONObject(output);
            String result = json.getString("result");
            if (!result.equals("1")) {
                Toast.makeText(context, json.getString("msg"),
                        Toast.LENGTH_LONG).show();
            } else {
                JSONArray datas = json.getJSONArray("data");
                JSONArray data = datas.getJSONArray(0);
                String id = data.getString(0);
                List<String> arealist = new ArrayList<String>();
                List<String> aclist = new ArrayList<String>();
                final List<List<String>> townlist = new ArrayList<List<String>>();
                arealist.add(data.getString(1));
                aclist.add(data.getString(0));
                List<String> list = new ArrayList<String>();
                for (int i = 1; i < datas.length(); i++) {
                    data = datas.getJSONArray(i);
                    if (id.equals(data.getString(2))) {
                        list.add(data.getString(1));
                    } else {
                        id = data.getString(0);
                        arealist.add(data.getString(1));
                        aclist.add(data.getString(0));
                        townlist.add(list);
                        list = new ArrayList<String>();
                    }
                }
                townlist.add(list);
                areas = new String[arealist.size()];
                for (int i = 0; i < arealist.size(); i++) {
                    areas[i] = arealist.get(i);
                }
                final boolean[] listSel = new boolean[areas.length];
                for (int i = 0; i < areas.length; i++) {
                    listSel[i] = false;
                }
                DialogInterface.OnMultiChoiceClickListener mcclr = new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which, boolean isChecked) {
                        // TODO Auto-generated method stub
                        listSel[which] = isChecked;
                    }
                };
                DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int index) {
                        // TODO Auto-generated method stub
                        for (int i = 0; i < listSel.length; i++) {
                            if (listSel[i]) {
                                zdDsEt.setText(areas[i]);
                            }
                        }
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(context)
                        .setTitle("选择地市")
                        .setMultiChoiceItems(areas, listSel,
                                mcclr)
                        .setPositiveButton("确定", dlr).show();
            }
            Log.d(TAG, json.getString("msg"));
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        array = null;
        super.onDestroy();
        gpsHandler = null;
    }

    private View.OnClickListener subLr = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            zdWlzbhStr = zdWlzbhEt.getText().toString().trim();
            /*if (zdWlzbhStr == null || zdWlzbhStr.equals("")) {
                Toast.makeText(context, "站点编号,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            } else if (zdWlzbhStr.equals("200米内查无基站")) {
                zdWlzbhStr = "";
            }*/
            zdWlzmcStr = zdWlzmcEt.getText().toString().trim();
            if (zdWlzmcStr == null || zdWlzmcStr.equals("")) {
                Toast.makeText(context, "站点名称,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }

            zdSfStr = zdSfEt.getText().toString().trim();
            if (zdSfStr == null || zdSfStr.equals("")) {
                Toast.makeText(context, "所在省（直辖市）,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            //zdDsStr = zdDsEt.getText().toString().trim();
            // zdDsStr = blindAreaSp.getSelectedItem().toString().trim();
            if (blindAreaID == null) {
                Toast.makeText(context, "所在地市,现场核对、现场必选....", Toast.LENGTH_SHORT).show();
                return;
            }
            //zdQxStr = zdQxEt.getText().toString().trim();
            // zdQxStr = blindCountySp.getSelectedItem().toString().trim();
            if (blindCountyID == null) {
                Toast.makeText(context, "所在区县,现场核对、现场必选....", Toast.LENGTH_SHORT).show();
                return;
            }
            //zdXzjStr = zdXzjEt.getText().toString().trim();
            // zdXzjStr = blindTownSp.getSelectedItem().toString().trim();
            if (blindTownID == null) {
                Toast.makeText(context, "乡、镇、街道办事处,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdXzcStr = zdXzcEt.getText().toString().trim();
            if (zdXzcStr == null || zdXzcStr.equals("")) {
                Toast.makeText(context, "行政村,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdZrcStr = zdZrcEt.getText().toString().trim();
            if (zdZrcStr == null || zdZrcStr.equals("")) {
                Toast.makeText(context, "自然村,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdCjjdStr = zdCjjdEt.getText().toString().trim();
            if (zdCjjdStr == null || zdCjjdStr.equals("")) {
                Toast.makeText(context, "现场采集经度,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdCjwdStr = zdCjwdEt.getText().toString().trim();
            if (zdCjwdStr == null || zdCjwdStr.equals("")) {
                Toast.makeText(context, "现场采集纬度,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdXxdzStr = zdXxdzEt.getText().toString().trim();
            if (zdXxdzStr == null || zdXxdzStr.equals("")) {
                Toast.makeText(context, "详细地址（位置名称、小区、门牌号）,现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdRRUStr = zdRRUEt.getText().toString().trim();
            if (zdRRUStr == null || zdRRUStr.equals("")) {
                Toast.makeText(context, "RRU数量,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdBBUStr = zdBBUEt.getText().toString().trim();
            if (zdBBUStr == null || zdBBUStr.equals("")) {
                Toast.makeText(context, "BBU数量,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog ad = App.alertDialog(context, "提示！", "确定提交数据？",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if (bsId == null) {
                                saveNewPhysicalDatas();
                            } else {
/*                                ProgressDialog pd = App.progressDialog(context, getResources()
                                        .getString(R.string.common_response));
                                pd.show();
                                final DsHandler hr = new DsHandler(context, pd);
                                saveNewSiteDatas(bsId, bsName, hr);*/
                                saveNewPhysicalSite();
                            }
                        }
                    }, null);
            ad.show();
        }
    };

    /*新建站点*/
    private void saveNewPhysicalSite() {
        ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();
        final JSONObject output = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            array.put(zdWlzbhStr);
            array.put(zdWlzmcStr);
            array.put("530000");
            array.put(blindAreaID);
            array.put(blindCountyID);
            array.put(blindTownID);
            array.put(zdXzcStr);
            array.put(zdZrcStr);
            array.put(zdXxdzStr);
            array.put(zdCjjdStr);
            array.put(zdCjwdStr);
            array.put(zdFgqylx);
            array.put(zdZzly);
            array.put(zdZzlx);
            array.put(zdZzzt);
            array.put(zdBuildType);
            array.put(zdSbzs);
            array.put(zdRRUStr);
            array.put(zdBBUStr);
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            output.put("userid",
                    share.getString(AppCheckLogin.SHARE_USER_ID, ""));
            output.put("data", array);
            output.put("type", type);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final DsHandler hr = new DsHandler(context, pd);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                connNewPhysical(hr, output.toString());
            }
        }).start();

    }

    /**
     * 新建物理站址
     */
    private void saveNewPhysicalDatas() {
        ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();
        final JSONObject output = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            array.put(zdWlzbhStr);
            array.put(zdWlzmcStr);
            array.put("530000");
            array.put(blindAreaID);
            array.put(blindCountyID);
            array.put(blindTownID);
            array.put(zdXzcStr);
            array.put(zdZrcStr);
            array.put(zdXxdzStr);
            array.put(zdCjjdStr);
            array.put(zdCjwdStr);
            array.put(zdFgqylx);
            array.put(zdZzly);
            array.put(zdZzlx);
            array.put(zdZzzt);
            array.put(zdBuildType);
            array.put(zdSbzs);
            array.put(zdRRUStr);
            array.put(zdBBUStr);
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            output.put("userid",
                    share.getString(AppCheckLogin.SHARE_USER_ID, ""));
            output.put("data", array);
            output.put("type", type);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final DsHandler hr = new DsHandler(context, pd);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                connNewPhysical(hr, output.toString());
            }
        }).start();

    }


    private void connNewPhysical(Handler hr, String output) {
        ALog.json(output);
        Message msg = null;
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("DSSaveNewAssets.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
        ALog.json(output);
        Log.i(TAG, url);
        try {
            output = URLEncoder.encode(output, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String res = client.getResultByPOST(url, output);
        ALog.json(res);
        if (res.equals(AppHttpClient.RESULT_FAIL)) {
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
            return;
        }
        try {
            JSONObject resJson = new JSONObject(res);
            String result = resJson.getString("result");
            if (!result.equals("1")) {
                msg = hr.obtainMessage(501, resJson.getString("msg"));
                hr.sendMessage(msg);
                return;
            }
            /*JSONArray data = resJson.getJSONArray("data");
            JSONArray tempData = data.getJSONArray(0);
            String bsId = tempData.getString(0);
            String bsName = tempData.getString(1);
            Bundle be = new Bundle();
            be.putString("id", bsId);
            be.putString("name", bsName);
            Intent i = new Intent(context, AssetsContentActivity.class);
            i.putExtras(be);
            startActivity(i);*/

            // setNewPhysicalNumber(bsId);
            // saveNewSiteDatas(bsId, bsName, hr);
            msg = hr.obtainMessage(200);
            hr.sendMessage(msg);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
        }
    }

    /**
     * 新建物理站址
     */
    private void setNewPhysicalNumber(final String bsId) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + bsId);
                StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
                url.append("BSSiteSetNumber.do?bsid=").append(bsId)
                        .append("&sign=").append(sign);
                ALog.d(url.toString());
                AppHttpConnection conn = new AppHttpConnection(context, url.toString());
                final String conResult = conn.getConnectionResult();
                ALog.d(conResult);
            }
        }.start();
    }

    /**
     * 新建站点
     */
    private void setNewPhysicalResource(final String siteId) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + siteId);
                StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
                url.append("BSSiteSetResource.do?site_id=").append(siteId)
                        .append("&sign=").append(sign);
                ALog.d(url.toString());
                AppHttpConnection conn = new AppHttpConnection(context, url.toString());
                final String conResult = conn.getConnectionResult();
                ALog.d(conResult);
            }
        }.start();
    }

    /**
     * 新建物理站址
     */
    private void saveNewSiteDatas(final String bsId, final String bsName, final Handler hr) {
        /*ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();*/
        final JSONObject output = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            array.put(bsId);
            /*array.put(zdSfStr);
            array.put(zdDsStr);
            array.put(zdQxStr);
            array.put(zdXzjStr);
            array.put(zdXzcStr);
            array.put(zdXxdzStr);
            array.put(zdCjjdStr);
            array.put(zdCjwdStr);*/
            array.put(zdBuildType);
            array.put(zdSbzs);
            array.put(zdWlzmcStr);
            array.put(zdRRUStr);
            array.put(zdBBUStr);
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            output.put("userid",
                    share.getString(AppCheckLogin.SHARE_USER_ID, ""));
            output.put("data", array);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                connNewSite(hr, bsId, bsName, output.toString());
            }
        }).start();

    }

    private void connNewSite(Handler hr, String bsId, String bsName, String output) {
        ALog.d(output);
        Message msg = null;
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("DSSaveNewSite.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
        ALog.json(output);
        try {
            output = URLEncoder.encode(output, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String res = client.getResultByPOST(url, output);
        ALog.json(res);
        if (res.equals(AppHttpClient.RESULT_FAIL)) {
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
            return;
        }
        try {
            JSONObject resJson = new JSONObject(res);
            String result = resJson.getString("result");
            if (!result.equals("1")) {
                msg = hr.obtainMessage(501, resJson.getString("msg"));
                hr.sendMessage(msg);
                return;
            }
            JSONArray data = resJson.getJSONArray("data");
            JSONArray tempData = data.getJSONArray(0);
            String siteId = tempData.getString(0);
            setNewPhysicalResource(siteId);
            String siteName = tempData.getString(1);
            Bundle be = new Bundle();
            be.putString("id", bsId);
            be.putString("name", bsName);
            ALog.d(bsId + "-------------" + bsName);
            Intent i = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
            i.putExtras(be);
            startActivity(i);
            msg = hr.obtainMessage(200);
            hr.sendMessage(msg);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
        }
    }

    private void findViews() {
        bsSiteIDbtn = (Button) findViewById(R.id.btn_item_zd_wlzbh);
        bsSiteIDbtn.setOnClickListener(this);
        zdWlzmcEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_wlzmc);
        zdWlzbhEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_wlzbh);
        zdSfEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_sf);
        //zdDsEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_ds);
        //zdDsBtn = (Button) findViewById(R.id.btn_item_zd_ds);
        //zdQxEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_qx);
        zdQxBtn = (Button) findViewById(R.id.btn_item_zd_qx);
        // zdXzjEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_xzj);
        zdXzcEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_xzc);
        zdZrcEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_zrc);
        zdXxdzEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_yxdz);
        zdCjjdEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_cjjd);
        zdCjjdBtn = (Button) findViewById(R.id.btn_item_zd_cjjd);
        zdCjwdEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_cjwd);
        zdCjwdBtn = (Button) findViewById(R.id.btn_item_zd_cjwd);
        btnSub = (Button) findViewById(R.id.btn_close);
        //城市
        blindAreaSp = (Spinner) findViewById(R.id.sp_blindArea_assets);
        //区县
        blindCountySp = (Spinner) findViewById(R.id.sp_blindCounty_assets);
        //街道
        blindTownSp = (Spinner) findViewById(R.id.sp_blindTowns_assets);
        //覆盖场景
        //zdFgqylxEt= (EditText) findViewById(R.id.et_item_assets_bs_fgcj);
        //设备制式
        //zdSbzsEt= (EditText) findViewById(R.id.et_item_assets_bs_zd_sbzs);

        //RRU数量
        zdRRUEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_RRU);
        //BBU数量
        zdBBUEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_BBU);

        // 覆盖区域类型
        spZdFgqylx = (Spinner) findViewById(R.id.sp_item_assets_fgqylx);
        String[] arrayFgqylx = getResources().getStringArray(R.array.fgqylx);
        ArrayAdapter arrayFgqylxAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayFgqylx);
        arrayFgqylxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spZdFgqylx.setAdapter(arrayFgqylxAdapter);
        spZdFgqylx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdFgqylx = String.valueOf(position + 1);
                ALog.d("覆盖区域类型: " + zdFgqylx);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 站址来源
        spzZdZzly = (Spinner) findViewById(R.id.sp_item_assets_zzly);
        String[] arrayZzly = getResources().getStringArray(R.array.zzly);
        ArrayAdapter arrayZzlyAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayZzly);
        arrayZzlyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spzZdZzly.setAdapter(arrayZzlyAdapter);
        spzZdZzly.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdZzly = String.valueOf(position + 1);
                ALog.d("站址来源: " + zdZzly);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 站址类型
        spZdZzlx = (Spinner) findViewById(R.id.sp_item_assets_zzlx);
        String[] arrayZzlx = getResources().getStringArray(R.array.zzlx);
        ArrayAdapter arrayZzlxAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayZzlx);
        arrayZzlxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spZdZzlx.setAdapter(arrayZzlxAdapter);
        spZdZzlx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdZzlx = String.valueOf(position + 1);
                ALog.d("站址类型: " + zdZzlx);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 站址状态
        spZdZzzt = (Spinner) findViewById(R.id.sp_item_assets_zzzt);
        String[] arrayZzzt = getResources().getStringArray(R.array.zzzt);
        ArrayAdapter arrayZzztAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayZzzt);
        arrayZzztAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spZdZzzt.setAdapter(arrayZzztAdapter);
        spZdZzzt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdZzzt = String.valueOf(position + 1);
                ALog.d("站址状态: " + zdZzzt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSbzs = (Spinner) findViewById(R.id.sp_item_assets_bs_sbzs);
        arraySbzs = getResources().getStringArray(R.array.sbzs);
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.item_spinner, arraySbzs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSbzs.setAdapter(adapter);
        spSbzs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdSbzs = String.valueOf(position + 1);
                ALog.d("设备制式： " + zdSbzs);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spBuildType = (Spinner) findViewById(R.id.sp_item_assets_bs_build_type);
        arrayBuildType = getResources().getStringArray(R.array.build_type);
        ArrayAdapter buildTypeAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayBuildType);
        buildTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBuildType.setAdapter(buildTypeAdapter);
        spBuildType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdBuildType = String.valueOf(position + 1);
                ALog.d("建设类型：" + zdBuildType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.btn_item_zd_ds:
            //   buildSp();
            //  break;
            case R.id.btn_item_zd_wlzbh:
                if (less50mList.isEmpty()) {
                    Toast.makeText(context, "附近没有可利旧站址、站址编号将自动生成、请继续下一项....", Toast.LENGTH_SHORT).show();
                } else {
                    String[] array = new String[less50mList.size()];
                    for (int i = 0; i < array.length; i++) {
                        array[i] = less50mList.get(i).getBsNames();
                    }
                    showDialogBS(array, context, "请选择物理站址", zdWlzmcEt);
                }
            case R.id.btn_item_zd_qx:
                break;
            case R.id.btn_item_zd_cjjd:
                setLocation();
                zdCjjdEt.setText(longitudeGPS + "");
                zdCjwdEt.setText(latitudeGPS + "");

                break;
            case R.id.btn_item_zd_cjwd:
                setLocation();
                zdCjjdEt.setText(longitudeGPS + "");
                zdCjwdEt.setText(latitudeGPS + "");
                break;
            default:
                break;
        }
    }

    /*遍历小于50米内的基站*/
    private void showBSNumber(String name) {
        for (int i = 0; i < less50mList.size(); i++) {
            if (less50mList.get(i).getBsNames().equals(name)) {
                zdWlzbhEt.setText(less50mList.get(i).getBsNumber());
                bsId = less50mList.get(i).getBsIds();
                bsName = less50mList.get(i).getBsNames();
            }
        }
    }


    /*如果50米内有数据显示50米内的杆编号
*/
    private void showDialogBS(final String[] array, final Context context, String title, final EditText editText) {
        final Dialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editText.setText(array[which]);
                        showBSNumber(array[which]);


                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }).create();
        alertDialog.show();
    }

    private void setLocation() {
       /* Map<String, Double> locs = getLocs(gpsHandler.getLastLocation());
        if (locs != null) {
            double lng = locs.get("lng");
            double lat = locs.get("lat");
            zdCjjdEt.setText(lng + "");
            zdCjwdEt.setText(lat + "");
        } else {
            Toast.makeText(context, "获取定位信息失败！", Toast.LENGTH_LONG)
                    .show();
        }*/
        geoBD = ((MainApp) getApplication()).geoBD;
        location = geoBD.location;
        baiduCity = location.getCity();
        baiduDistrict = location.getDistrict();
        baiduStreet = location.getStreet();
        baiduStreetNumber = location.getStreetNumber();
        baiduLocation = location.getAddrStr();


        Log.i(TAG, "百度坐标系经度纬度:" + longtitude + "," + latitude + "转换后的坐标:" + longitudeGPS + "," + latitudeGPS);

    }

    /**
     * @author Fddi
     */
    static class DsHandler extends Handler {
        private Context context;
        private ProgressDialog pd;

        public DsHandler(Context context, ProgressDialog pd) {
            this.context = context;
            this.pd = pd;
        }

        public DsHandler(View.OnClickListener onClickListener) {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 200:
                    Toast.makeText(context, "数据提交成功 ！", Toast.LENGTH_LONG).show();
                    ((Activity) context).setResult(RESULT_OK);
                    ((Activity) context).finish();
                    break;
                case 500:
                    Toast.makeText(context, "连接失败：请检查网络连接！", Toast.LENGTH_LONG).show();
                    break;
                case 600:
                    Toast.makeText(context, "数据保存成功！", Toast.LENGTH_LONG).show();
                    break;
                case 501:
                    String info = (String) msg.obj;
                    Toast.makeText(context, "提示：" + info, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(context, "未知错误", Toast.LENGTH_LONG).show();
                    break;
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    private Map<String, Double> getLocs(Location loc) {
        if (loc != null) {// GPS定位
            long ot = new Date().getTime() - loc.getTime();
            if (ot <= GpsHandler.TIME_OUT_LOC) {
                Map<String, Double> locs = new HashMap<String, Double>();
                locs.put("lng", loc.getLongitude());
                locs.put("lat", loc.getLatitude());
                Log.d(App.LOG_TAG, "location by GPS --" + locs.get("lng") + "/"
                        + locs.get("lat"));
                return locs;
            }
        }
        if (MainApp.geoBD != null && MainApp.geoBD.locClient != null
                && MainApp.geoBD.locClient.isStarted()) {
            MainApp.geoBD.locClient.requestLocation();
            BDLocation bloc = MainApp.geoBD.location;
            if (bloc != null) {// 百度接口定位
                WGSTOGCJ02 wg = new WGSTOGCJ02();
                Map<String, Double> wgsloc = wg.gcj2wgs(bloc.getLongitude(),
                        bloc.getLatitude());
                Map<String, Double> locs = new HashMap<String, Double>();
                locs.put("lng", wgsloc.get("lon"));
                locs.put("lat", wgsloc.get("lat"));
                Log.d(App.LOG_TAG, "location by baidu --" + locs.get("lng")
                        + "/" + locs.get("lat"));
                return locs;
            }
        }
        return null;
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
                    } else if (item.ORG_LEVEL == 3 && item.ORG_NAME != "市辖区") {
                        THREE_LEVEL_LIST.add(item);
                    } else if (item.ORG_LEVEL == 4) {
                        FOUR_LEVEL_LIST.add(item);
                    }
                }
                getTheTwoRegionAdapterSp();
            }

            @Override
            public void onUpdate(Object o) {

            }
        }.execute();
    }

    //读取数据库
    private ArrayList<RegionInfoItem> getRegionInfoItems() {
        XDbUtils db = new XDbUtils(this.context);
        ArrayList<RegionInfoItem> infos = new ArrayList<RegionInfoItem>();
        try {
            db.createTable(RegionInfoItem.class);
            List<Object> objects = db.query(null, null, RegionInfoItem.class,  null);
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
                    blindTown = null;
                    blindTownID = null;
                    getTheThreeRegionAdapterCountySp();
                }
                if (blindAreaSp.getSelectedItem() != null) {
                    blindArea = blindAreaSp.getSelectedItem().toString();
                    //mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea));
                    Log.d(TAG, blindArea + ":" + "onItemselect");
                } else {
                    Toast.makeText(PhysicalSiteNewEditActivity.this, "找不到行政区域", Toast.LENGTH_SHORT).show();
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
                    //            mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea));
                }

            }
        });
        int index = 0;
/*        if (address != null && !address.equals("")) {
            for (int i = 0; i < blindAreas.length; ++i) {
                if (address.indexOf(blindAreas[i]) != -1) {
                    index = i;
                }
            }
        }*/
        for (int i = 0; i < blindAreas.length; i++) {
            if (blindAreas[i].equals(baiduCity)) {
                index = i;
            }
        }
        blindAreaSp.setSelection(index, true);


    }

    private void getTheThreeRegionAdapterCountySp() {
        for (int i = 0; i < THREE_LEVEL_LIST.size(); ++i) {
            RegionInfoItem item = THREE_LEVEL_LIST.get(i);
            if (item.ORG_PARENT_ID == TWO_LEVEL_ITEM.ORG_ID) {
                THREE_LEVEL_LIST_FILTER.add(item);
            }
        }
        // 把市辖区放到最后
        RegionInfoItem temp = null;
        for (int i = 0; i < THREE_LEVEL_LIST_FILTER.size(); ++i) {
            if (THREE_LEVEL_LIST_FILTER.get(i).ORG_NAME == "市辖区") {
                temp = THREE_LEVEL_LIST_FILTER.remove(i);
            }
        }
        if (temp != null) {
            THREE_LEVEL_LIST_FILTER.add(temp);
        }
        // end
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
                    blindTown = null;
                    blindTownID = null;
                    getTheFourRegionAdapterTownSp();
                }
                if (blindCountySp.getSelectedItem() != null) {
                    blindCounty = blindCountySp.getSelectedItem().toString();

                    if (!blindCounty.equals("市辖区")) {
                        //                       mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea).districtName(blindArea + blindCounty));
                    } else {
//                        mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea));
                    }
                    Log.d(TAG, blindCounty + ":" + "onItemselect");

                } else {
                    Toast.makeText(PhysicalSiteNewEditActivity.this, "找不到行政区域", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                if (THREE_LEVEL_LIST_FILTER.isEmpty()) {
                    Toast.makeText(context, "加载中....", Toast.LENGTH_SHORT);
                }
                blindCounty = blindCountySp.getSelectedItem().toString();
                //             mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea).districtName(blindCounty));
                Log.d(TAG, blindCounty + ":" + "onNothingItemSelect");

            }
        });
        int index = 0;
        for (int i = 0; i < blindCountys.length; i++) {
            if (blindCountys[i].equals(baiduDistrict)) {
                index = i;
            }
        }
        blindCountySp.setSelection(index, true);

    }

    private void getTheFourRegionAdapterTownSp() {
        for (int i = 0; i < FOUR_LEVEL_LIST.size(); ++i) {
            RegionInfoItem item = FOUR_LEVEL_LIST.get(i);
            if (item.ORG_PARENT_ID == THREE_LEVEL_ITEM.ORG_ID) {
                FOUR_LEVEL_LIST_FILTER.add(item);
            }
        }
        // 把市辖区放到最后
        // RegionInfoItem temp = null;
/*        for (int i = 0; i < FOUR_LEVEL_LIST_FILTER.size(); ++i) {
            if (FOUR_LEVEL_LIST_FILTER.get(i).ORG_NAME == "市辖区") {
                temp = FOUR_LEVEL_LIST_FILTER.remove(i);
            }
        }*/
/*        if (temp != null) {
            FOUR_LEVEL_LIST_FILTER.add(temp);
        }*/
        // end
        blindTowns = new String[FOUR_LEVEL_LIST_FILTER.size()];
        for (int i = 0; i < FOUR_LEVEL_LIST_FILTER.size(); ++i) {
            RegionInfoItem item = FOUR_LEVEL_LIST_FILTER.get(i);
            blindTowns[i] = item.ORG_NAME;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.item_spinner, blindTowns);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blindTownSp.setAdapter(adapter);

        blindTownSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int index, long arg3) {
                // TODO Auto-generated method stub
                if (FOUR_LEVEL_LIST_FILTER.isEmpty()) {
                    Toast.makeText(context, "正在加载数据请稍后", Toast.LENGTH_SHORT);
                }
                FOUR_LEVEL_ITEM = FOUR_LEVEL_LIST_FILTER.get(index);
                if (FOUR_LEVEL_ITEM != null){
                    blindTown = FOUR_LEVEL_ITEM.ORG_NAME;
                    blindTownID = String.valueOf(FOUR_LEVEL_ITEM.ORG_ID);
                }
                // blindTown = null;
                // getTheFourRegionAdapterTownSp();

                if (blindTownSp.getSelectedItem() != null) {
                    blindTown = blindTownSp.getSelectedItem().toString();
                    Log.i(TAG, blindArea);
                    Log.i(TAG, blindCounty);
                    Log.i(TAG, blindTown);
                    //if (!blindCounty.equals("市辖区")) {
                    //                       mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea).districtName(blindArea + blindCounty));
                    //} else {
//                        mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea));
                    // }
                    Log.d(TAG, blindTown + ":" + "onItemselect");

                } else {
                    Toast.makeText(PhysicalSiteNewEditActivity.this, "找不到乡镇街道", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                if (FOUR_LEVEL_LIST_FILTER.isEmpty()) {
                    Toast.makeText(context, "加载中....", Toast.LENGTH_SHORT);
                }
                blindTown = blindTownSp.getSelectedItem().toString();
                //             mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(blindArea).districtName(blindCounty));
                Log.d(TAG, blindTown + ":" + "onNothingItemselect");

            }
        });
        int index = 0;
        blindTownSp.setSelection(index, true);
        // zdXzcEt.setText(baiduStreet + baiduStreetNumber);
        zdXxdzEt.setText(baiduLocation);

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
            latitudeGPS = 2 * sourceLatLng.longitude - desLatLng.longitude;
            longitudeGPS = 2 * sourceLatLng.latitude - desLatLng.latitude;
            //LatLng centerLatLng = new LatLng(latitude, longitude);
            //getBSMapInfo(centerLatLng, blindArea, blindCounty);
            //gpsLatLng = new LatLng(latitudeGPS, longitudeGPS);

        } else {
            Toast.makeText(this, "定位中", Toast.LENGTH_SHORT).show();
        }
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
                getGPSLoc();
            }
            hr.postDelayed(locChange, 1000);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        //在这里开启GPS定位服务
        gpsHandler.startGps(GpsHandler.TIME_INTERVAL_GPS);
        hr.postDelayed(locChange, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gpsHandler != null) {
            gpsHandler.stopGps();
        }
    }

    private void getGPSLoc() {
        if (loc != null) {
            long ts = (new Date()).getTime() - loc.getTime();
            if (ts > GpsHandler.TIME_OUT_LOC) {
                Toast.makeText(context, "GPS定位已过期，正在重新定位，请稍后..", Toast.LENGTH_SHORT).show();
            } else {
                zdCjjdEt.setText(String.valueOf(loc.getLongitude()));
                zdCjwdEt.setText(String.valueOf(loc.getLatitude()));
            }
        }
    }
}
