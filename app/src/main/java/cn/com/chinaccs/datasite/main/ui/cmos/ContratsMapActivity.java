package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;


/**
 * 巡检作业内容的地图查询
 */
public class ContratsMapActivity extends BaseActivity {
    Context context;
    private TextView tvBs;
    private TextView textDv;
    private ImageButton btnModel;
    private MapView mv;
    private BaiduMap mBaiduMap;
    private int mBaiduMapState = 0;
   private double dv;
    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_map);
        initToolbar("地图定位");
        this.context = this;
        initView();
        Bundle be = getIntent().getExtras();
        String bsLng = be.getString("bsLng") == null ? "" : be
                .getString("bsLng");
        String bsLat = be.getString("bsLat") == null ? "" : be
                .getString("bsLat");
        String ctLng = be.getString("ctLng") == null ? "" : be
                .getString("ctLng");
        String ctLat = be.getString("ctLat") == null ? "" : be
                .getString("ctLat");
        String bsName = be.getString("bsName");
        tvBs.setText("显示设备：");
        tvBs.append(bsName);
        if (!bsLng.equals("") && !bsLat.equals("") && !ctLng.equals("")
                && !ctLat.equals("")) {
            addOverLays(Double.valueOf(bsLng), Double.valueOf(bsLat),
                    Double.valueOf(ctLng), Double.valueOf(ctLat));

        }
    }

    private void initView() {
        mv = (MapView) findViewById(R.id.mapview_test);
        tvBs = (TextView) findViewById(R.id.tv_bs);
        textDv = (TextView) findViewById(R.id.text_dv);
        //btnModel = (ImageButton) findViewById(R.id.btn_mapModel);
        //btnModel.setOnClickListener(lr);
        mBaiduMap = mv.getMap();
    }

    private void addOverLays(double bslng, double bslat, double ctlng,
                             double ctlat) {
        Log.e("COntrats", "bslng=" + bslng + "bslat=" + bslat + "ctlng=" + ctlng + "ctlat=" + ctlat);
        LatLng sourceLatLng1 = new LatLng(ctlat, ctlng);
        LatLng sourceLatLng2 = new LatLng(bslat, bslng);
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
// sourceLatLng待转换坐标
        converter.coord(sourceLatLng1);
        LatLng point1 = converter.convert();

        CoordinateConverter converter2 = new CoordinateConverter();
        converter2.from(CoordinateConverter.CoordType.GPS);
// sourceLatLng待转换坐标
        converter2.coord(sourceLatLng2);
        LatLng point2 = converter2.convert();
        Log.e("point1", "" + point1.latitude + point1.longitude);
        Log.e("point2", "" + point2.latitude + point2.longitude);
        BitmapDescriptor bitmap1 = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_map_inspect);
        OverlayOptions option1 = new MarkerOptions()
                .position(point1)
                .icon(bitmap1);
        mBaiduMap.addOverlay(option1);

        BitmapDescriptor bitmap2 = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_cell);
        OverlayOptions option2 = new MarkerOptions()
                .position(point2)
                .icon(bitmap2);
        mBaiduMap.addOverlay(option2);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(point2);
        mBaiduMap.animateMapStatus(msu);
        //计算距离
        dv = BDGeoLocation.getShortDistance(point1.longitude,
                point1.latitude, point2.longitude,
                point2.latitude);
        textDv.setText("距离：" + String.format("%.2f", dv) + "m");
    }

/*    private OnClickListener lr = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_mapModel:
                    toggleMapModel();
                    break;
            }
        }
    };*/

    private void toggleMapModel() {
        String[] strs = {"地图模式", "卫星模式"};
        new AlertDialog.Builder(ContratsMapActivity.this)
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
}
