package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.util.Map;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.common.WGSTOGCJ02;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.ui.MainApp;


public class BoxMapActivity extends BaseActivity {
    private static final String TAG = App.LOG_TAG + "--" + BoxMapActivity.class.getSimpleName();
    private MapView mv;
    private TextView tvBs;
    private TextView textDv;;
    private Double cellLac;
    private Double cellLng;
    private Double locLac;
    private Double locLng;
    private Context context;
    BDLocation location;
    private BaiduMap mBaiduMap;
    private int mBaiduMapState = 0;
    public static BDGeoLocation geoGB;
    private BDGeoLocation.BDLocationChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initToolbar("地图定位");
        this.context = BoxMapActivity.this;
        geoGB = ((MainApp) getApplication()).geoBD;
        if (geoGB != null && geoGB.locClient != null
                && geoGB.locClient.isStarted()) {
            geoGB.locClient.requestLocation();
            location = geoGB.location;
        }
        this.initView();
        this.initMapView();
        this.buildMap();
    }

    private void initView() {
        tvBs = (TextView) findViewById(R.id.tv_bs);
        tvBs.setText("显示设备：接入基站");
        textDv = (TextView) findViewById(R.id.text_dv);
    }

    /**
     * 初始化地图定位、并定位到当前位置
     */
    private void initMapView() {
        mv = (MapView) findViewById(R.id.mapview_test);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap = mv.getMap();
        mBaiduMap.setMapStatus(msu);
        listener = new BDGeoLocation.BDLocationChangeListener() {
            @Override
            public void setLocation(BDLocation bdlocation) {
                Log.e(TAG, bdlocation.getLongitude() + "----" + bdlocation.getLatitude() + "----" + bdlocation.getAddrStr());
                // 定位信息更新同时更新地图位置
               updateMapsStatus(bdlocation);
            }
        };
        // 注册监听地图位置信息变化
        geoGB.addChangeListener(listener);
    }

    /**
     * 更新地图状态
     * @param location 当前百度地图属性
     */
    private void updateMapsStatus(BDLocation location){
        if (location == null){
            return;
        }
        Log.e(TAG, location.getLongitude() + "----" + location.getLatitude() + "----" + location.getAddrStr());
        MyLocationData data = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude()).build();
        mBaiduMap.setMyLocationData(data);

        locLng = location.getLongitude();
        locLac = location.getLatitude();
        LatLng latLng = new LatLng(locLac, locLng);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);

        // 地图更新完毕后执行获取距离和方位角
        getDesc();
    }

    /**
     * 地图模式切换
     */
    private OnClickListener modelListener = new OnClickListener() {
        String[] strs = {"地图模式", "卫星模式"};

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new AlertDialog.Builder(BoxMapActivity.this)
                    .setTitle("地图模式切换")
                    .setSingleChoiceItems(strs, mBaiduMapState,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    switch (which) {
                                        case 0:
                                            if (mBaiduMap != null)
                                                //设置卫星模式,就是实物地图
                                                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                                            mBaiduMapState = 0;
                                            dialog.dismiss();
                                            break;
                                        case 1:
                                            if (mBaiduMap != null)
                                                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                                            mBaiduMapState = 1;
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            }).show();
        }
    };

    private void buildMap() {

        addCellOverlays();

    }

    //添加覆盖物
    private void addCellOverlays() {
        AppCDMABaseStation.CDMABaseStation bs = AppCDMABaseStation.getInsertBaseStation(context);
        if (bs == null) {
            return;
        }
        cellLac = Double.valueOf(bs.getLatitude());
        cellLng = Double.valueOf(bs.getLongitude());
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

    //获取距离和方位角
    private void getDesc() {
        if (locLng != null && locLac != null && cellLng != null
                && cellLac != null) {
            WGSTOGCJ02 wgs = new WGSTOGCJ02();
            Map<String, Double> locs = wgs.gcj2wgs(locLng, locLac);
            double dv = BDGeoLocation.getShortDistance(locs.get("lon"),
                    locs.get("lat"), cellLng, cellLac);
            double ah = BDGeoLocation.getGpsAzimuth(locs.get("lon"),
                    locs.get("lat"), cellLng, cellLac);
            textDv.setText("基站距离：" + String.format("%.2f", dv) + "m 方位角："
                    + String.format("%.2f", ah) + "°");
            Log.e("BoxMapActivity", "is set text");
        }
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
        geoGB.removeChangeListener(listener);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_model) {
            String[] strs = {"地图模式", "卫星模式"};
            // TODO Auto-generated method stub
            new AlertDialog.Builder(BoxMapActivity.this)
                    .setTitle("地图模式切换")
                    .setSingleChoiceItems(strs, mBaiduMapState,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    switch (which) {
                                        case 0:
                                            if (mBaiduMap != null)
                                                //设置卫星模式,就是实物地图
                                                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                                            mBaiduMapState = 0;
                                            dialog.dismiss();
                                            break;
                                        case 1:
                                            if (mBaiduMap != null)
                                                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                                            mBaiduMapState = 1;
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            }).show();

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_model, menu);
        return true;
    }

//    public class CellOverlay extends Overlay {
//        GeoPoint geoPoint = new GeoPoint((int) (25.05 * 1E6),
//                (int) (102.72 * 1E6));
//        ;
//
//        public CellOverlay(int lat, int lng) {
//            geoPoint = new GeoPoint(lat, lng);
//            geoPoint = CoordinateConvert.bundleDecode(CoordinateConvert
//                    .fromWgs84ToBaidu(geoPoint));
//        }
//
//        public CellOverlay(Double lat, Double lng) {
//            geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
//            geoPoint = CoordinateConvert.bundleDecode(CoordinateConvert
//                    .fromWgs84ToBaidu(geoPoint));
//        }
//
//        @Override
//        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    // 把经纬度变换到相对于MapView左上角的屏幕像素坐标
//            Point point = mBaiduMap.getProjection().toPixels(geoPoint, null);
//            Paint paint = new Paint();
//            paint.setColor(Color.BLACK); //
//            paint.setStyle(Style.FILL);
//            paint.setAntiAlias(true);
//            paint.setAlpha(30);
//            paint.setDither(true);
//            paint.setStyle(Paint.Style.FILL);// 设置实心
//            paint.setStrokeCap(Paint.Cap.ROUND);
//            paint.setStrokeJoin(Paint.Join.ROUND);
//            paint.setStrokeWidth(0);// 设置线宽为0 也可以设置为其他值
//
//            Paint paint2 = new Paint();
//            paint2.setColor(Color.GREEN); //
//            paint.setStyle(Style.FILL);
//            paint2.setAntiAlias(true);
//            paint2.setAlpha(100);
//            paint2.setDither(true);
//            paint2.setStyle(Paint.Style.STROKE);// 设置实心
//            paint2.setStrokeCap(Paint.Cap.ROUND);
//            paint2.setStrokeJoin(Paint.Join.ROUND);
//            paint2.setStrokeWidth(2);// 设置外层红线的宽度
////x/y坐标系和地球经纬度坐标系之间进行转换
//            Projection projection = mBaiduMap.getProjection();
//            projection.to
//
//            canvas.drawCircle(point.x, point.y,
//                    projection.metersToEquatorPixels(1000), paint);
//            canvas.drawCircle(point.x, point.y,
//                    projection.metersToEquatorPixels(1002), paint2);
//            Drawable d = getResources().getDrawable(R.drawable.ic_cell);
//            BitmapDrawable bd = (BitmapDrawable) d;
//            Bitmap m = bd.getBitmap();
//            canvas.drawBitmap(m, point.x - 24, point.y - 24, new Paint());
//        }
//    }

}
