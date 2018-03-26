package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
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
import org.json.JSONObject;


import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.ui.MainApp;

/**
 * @author Fddi
 */
public class InspectHistoryContentActivity extends BaseActivity {
    private Context context;
    private TextView tvName;
    private TextView tvPlan;
    private TextView tvStates;
    private TextView tvTime;
    private String tid;
    private Double longitude;
    private Double latitude;
    private Double bs_longitude;
    private Double bs_latitude;
    private MapView mv;
    private JSONArray data;
    private Handler hr;
    private ImageButton btnImg;
    private Button btnItem;
    private String bsName;
    private String is_rru;
    private ProgressDialog pd;
    private BDGeoLocation geoGB;
    private BaiduMap mBaiduMap;
    private BDGeoLocation.BDLocationChangeListener listner;
    private boolean isFirstIn=true;
    private Marker mMarkerXunaJianDian;
    private Marker mMarkerJieRuJiZhan;
    private InfoWindow mInfoWindow;
    private boolean isFristIn=true;
    private double mLongtitude;
    private double mLatitude;
    private final String TAG=InspectHistoryContentActivity.class.getSimpleName();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.menu_picture){
            Intent i = new Intent(context, InspectImgActivity.class);
            i.putExtra("type", 101);
            i.putExtra("id", tid);
            startActivity(i);

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        geoGB = MainApp.geoBD;
        setContentView(R.layout.activity_inspect_history);
        initToolbar("历史记录");
        Bundle be = getIntent().getExtras();
        tid = be.getString("tid");
        // Toast.makeText(context, "ID" + tid, Toast.LENGTH_LONG).show();
        findViews();
        hr = new Handler();
        buildMap();
        getDatas();
        //btnImg.setOnClickListener(btnLr);
        btnItem.setOnClickListener(btnLr);
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
        super.onResume();
        mv.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        geoGB.removeChangeListener(listner);
        geoGB.locClient.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启百度定位
        mBaiduMap.setMyLocationEnabled(true);
        //在这里开启GPS定位服务
        if (!geoGB.locClient.isStarted()) {
            geoGB.locClient.start();
        }
    }

    private void getDatas() {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    StringBuffer url = new StringBuffer(
                            DataSiteStart.HTTP_SERVER_URL);
                    String sign = App
                            .signMD5(DataSiteStart.HTTP_KEYSTORE + tid);
                    url.append("InspectWLHistoryContent.do?tid=").append(tid)
                            .append("&sign=").append(sign);
                    AppHttpConnection conn = new AppHttpConnection(context,
                            url.toString());
                    Log.d(App.LOG_TAG, url.toString());
                    String conResult = conn.getConnectionResult();
                    JSONObject resJson = new JSONObject(conResult);
                    Log.i(TAG,conResult);
                    String result = resJson.getString("result");
                    if (!result.equals("1")) {
                        Toast.makeText(context,"获取数据失败",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }
                    pd.dismiss();
                    JSONArray array = resJson.getJSONArray("data");
                    data = array.getJSONArray(0);
                    hr.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                bsName = data.getString(0);
                                is_rru = data.getString(8);
                                tvName.setText(data.getString(0));
                                tvPlan.setText(data.getString(1));
                                tvStates.setText(data.getString(2));
                                tvTime.setText(data.getString(3));

                                longitude = Double.parseDouble(data
                                        .getString(4));
                                latitude = Double.parseDouble(data.getString(5));
                                //这里的数据是从哪里提交的，google坐标是gcj
                                LatLng sourceLatLng1 = new LatLng(latitude,longitude);
                                CoordinateConverter converter  = new CoordinateConverter();
                                converter.from(CoordinateConverter.CoordType.COMMON);
                                converter.coord(sourceLatLng1);
                                LatLng point1 = converter.convert();

                                bs_longitude = Double.parseDouble(data
                                        .getString(6));
                                bs_latitude = Double.parseDouble(data
                                        .getString(7));
                                LatLng sourceLatLng2 = new LatLng(bs_latitude,bs_longitude);
                                CoordinateConverter converter2  = new CoordinateConverter();
                                converter2.from(CoordinateConverter.CoordType.GPS);
                                converter2.coord(sourceLatLng2);
                                LatLng point2 = converter2.convert();
                                addOverlays(point1, point2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void buildMap() {
        pd = null;
        pd = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        pd.show();

//        mv.setBuiltInZoomControls(true);
//        mv.setDrawOverlayWhenZooming(true);
//        MapController mc = mv.getController();
//        GeoPoint point = new GeoPoint((int) (25.05 * 1E6), (int) (102.72 * 1E6));
//        mc.setCenter(point);
//        mc.setZoom(15);
        // getCellOverlays();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap = mv.getMap();
        mBaiduMap.setMapStatus(msu);
        listner=new BDGeoLocation.BDLocationChangeListener() {
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
        

    }

    private void addOverlays(LatLng point1, LatLng point2) {
        if (mv == null) {
            return;
        }
        BitmapDescriptor bitmap1 = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_map_inspect);
        OverlayOptions option = new MarkerOptions()
                .position(point1)
                .title("巡检点")
                .icon(bitmap1);
        mMarkerXunaJianDian= (Marker) mBaiduMap.addOverlay(option);

        BitmapDescriptor bitmap2 = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_cell);
        OverlayOptions option2 = new MarkerOptions()
                .position(point2)
                .title(is_rru.equals("TRUE")?"RRU接入站点":"接入基站")
                .icon(bitmap2);
       mMarkerJieRuJiZhan= (Marker) mBaiduMap.addOverlay(option2);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point1);
        mBaiduMap.animateMapStatus(u);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                final LatLng ll = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                p.y -= 47;
                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                button.setText(marker.getTitle());
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                    }
                });
                mInfoWindow = new InfoWindow(button, llInfo,0);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    private void findViews() {
        mv = (MapView) findViewById(R.id.map_inspect);
        tvName = (TextView) findViewById(R.id.txt_bs_name);
        tvPlan = (TextView) findViewById(R.id.txt_plan);
        tvStates = (TextView) findViewById(R.id.txt_state);
        tvTime = (TextView) findViewById(R.id.txt_time);
        // tvState = (TextView) findViewById(R.id.txt_inspect_state);
        //btnImg = (ImageButton) findViewById(R.id.btn_inspect_img);
        btnItem = (Button) findViewById(R.id.btn_item_info);
        mBaiduMap = mv.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        listner = new BDGeoLocation.BDLocationChangeListener() {
            @Override
            public void setLocation(BDLocation bdlocation) {
                MyLocationData data = new MyLocationData.Builder()
                        .accuracy(bdlocation.getRadius())
                        .longitude(bdlocation.getLongitude())
                        .latitude(bdlocation.getLatitude()).build();
                mBaiduMap.setMyLocationData(data);

                if (isFirstIn) {
                    longitude = bdlocation.getLongitude();
                    latitude = bdlocation.getLatitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                    mBaiduMap.animateMapStatus(msu);
                    isFirstIn = false;
                }
            }
        };
    }

    private OnClickListener btnLr = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {

                case R.id.btn_item_info:
                    Intent it = new Intent(context, InspectItemInfoActivity.class);
                    it.putExtra("tid", tid);
                    it.putExtra("bsName", bsName);
                    it.putExtra("is_rru", is_rru);
                    startActivity(it);
                    break;
                default:
                    break;
            }
        }
    };

}
