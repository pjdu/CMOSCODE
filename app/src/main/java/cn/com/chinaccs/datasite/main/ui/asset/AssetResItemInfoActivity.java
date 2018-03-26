/*
 * Created by AndyHua on 2017/11/20.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-30 11:51:04.
 */

package cn.com.chinaccs.datasite.main.ui.asset;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.blankj.ALog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetUserArea;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.gps.GpsSatellite;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.ui.asset.mobile.MobileCardsActivity;
import cn.com.chinaccs.datasite.main.zbar.ScannerActivity;

/**
 * 光网络资源详细信息
 */
@EActivity(R.layout.activity_asset_res_con)
public class AssetResItemInfoActivity extends BaseActivity {
    public final static int REQUEST_ASSETS_CODE_SCANNING = 1;
    public final static int REQUEST_SERIAL_CODE_SCANNING = 2;
    public final static String REQUEST_CODE_SCANNING = "REQUEST_TYPE";

    private Context context = null;
    private JSONArray array;
    private String datas;
    private String id;
    private String type;
    private String title = "";
    private String areaName;

    private GpsHandler gpsHandler;
    private Handler hr;
    private Location loc;
    public static BDGeoLocation geoGB;
    private BDLocation location;

    // 资产资源关联信息
    private String city;                // 地市行政区划编码
    private String county;              // 区县行政区划编码
    private String town;                // 乡镇行政区划编码
    private String objId;               // 实物ID
    private String resType;             // 资源规格
    private String assCode;             // 资产卡片编号
    private String oldAssetCode;        // 原资卡片编号
    private String prjNo;               // 项目编号
    private String resCode;             // 资源编码
    private String localResCode;        // 本地资源编码
    private String psiteNo;             // 物理站址ID
    private String isNew = "0";         // 是否新增资产
    private String serialId = "";       // 设备电子序列号
    private String resScanCode;         // 资源扫码结果
    private String assScanCode;         // 资产扫码结果
    private String isChecked = "0";     // 是否进行现场盘点
    private String phone;               // 盘点用户手机号
    private String createTime = "";     // 生成时间
    private String updateTime = "";     // 修改时间
    private String isDel = "0";         // 是否删除
    private String lon;                 // 经度
    private String lat;                 // 纬度
    private String address;             // 实物所在地点

    @ViewById(R.id.et_ass_code)
    EditText mAssCodeEt;

    @ViewById(R.id.et_obj_id)
    EditText mObjIdEt;

    @ViewById(R.id.et_res_code)
    EditText mResCodeEt;

    @ViewById(R.id.et_ass_catlog_name)
    EditText mAssCatlogNameEt;

    @ViewById(R.id.et_prj_name)
    EditText mPrjNameEt;

    @ViewById(R.id.et_res_name)
    EditText mResNameEt;

    @ViewById(R.id.et_model)
    EditText mModelEt;

    @ViewById(R.id.et_city_name)
    EditText mCityNameEt;

    @ViewById(R.id.et_county_name)
    EditText mCountyNameEt;

    @ViewById(R.id.et_address)
    EditText mAddressEt;

    @ViewById(R.id.et_insert_time)
    EditText mInsertTimeEt;

    @ViewById(R.id.et_type_name)
    EditText mTypeNameEt;

    @ViewById(R.id.et_site_name)
    EditText mSiteNameEt;

    @ViewById(R.id.btn_submit)
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        gpsHandler = ((MainApp) getApplication()).gpsHandler;
        hr = new Handler();

        // 初始化地图位置
        geoGB = ((MainApp) getApplication()).geoBD;
        location = geoGB.location;
        if (geoGB != null && geoGB.locClient != null
                && geoGB.locClient.isStarted()) {
            geoGB.locClient.requestLocation();
        }

        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        type = be.getString("type");
        datas = be.getString("json");
        if (null != datas) {
            try {
                array = new JSONArray(datas);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ALog.d(array);
        title = "设备信息";
    }

    @AfterViews
    public void init() {
        try {
            initViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initViews() throws JSONException {
        initToolbar(title);
        city = array.getString(21);
        county = array.getString(22);
        town = array.getString(23);
        objId = array.getString(0);
        mObjIdEt.setText(objId);
        resType = array.getString(25);
        assCode = array.getString(4);
        mAssCodeEt.setText(assCode);
        mAssCodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAssCodeEt.clearFocus();
                    Bundle extras = new Bundle();
                    extras.putString("assetsNo", mAssCodeEt.getText().toString().trim());
                    extras.putString("areaName", areaName);
                    Intent intent = new Intent(context, MobileCardsActivity.class);
                    intent.putExtras(extras);
                    startActivityForResult(intent, REQUEST_ASSETS_CODE_SCANNING);
                }
            }
        });
        prjNo = array.getString(5);
        // 资源编号
        resCode = array.getString(1);
        mResCodeEt.setText(resCode);
        resScanCode = resCode;
        localResCode = array.getString(17);
        // 物理站址编号(光网络都为空)
        psiteNo = array.getString(24);
        mAssCatlogNameEt.setText(array.getString(3));
        mPrjNameEt.setText(array.getString(6));
        mResNameEt.setText(array.getString(7));
        mModelEt.setText(array.getString(8));
        areaName = array.getString(9);
        mCityNameEt.setText(areaName);
        mCountyNameEt.setText(array.getString(10));
        address = array.getString(11);
        lon = array.getString(12);
        lat = array.getString(13);
        mAddressEt.setText(address);
        mInsertTimeEt.setText(array.getString(14));
        mTypeNameEt.setText(array.getString(25));
        mSiteNameEt.setText(array.getString(28));

        // 查看信息隐藏确认盘点按钮
        submitBtn.setVisibility(View.GONE);
        // 是否关联卡片信息到本地库
        if (assCode.equals("")) {
            getAssCode();
        }
    }

    private void getAssCode() {
        FuncGetUserArea func = new FuncGetUserArea(context);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    initAssCode(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
        func.getAssCode(lr, objId);
    }

    private void initAssCode(String output) throws Exception {
        if (output.equals(AppHttpConnection.RESULT_FAIL)) {
            ALog.i(getResources().getString(R.string.common_not_network));
        } else {
            String conResult = AESCryto.decrypt(DataSiteStart.AES_KEY, output);
            conResult = URLDecoder.decode(conResult, App.ENCODE_UTF8);
            ALog.json(conResult);
            JSONObject json = new JSONObject(conResult);
            String result = json.getString("result");
            if (!result.equals("1")) {
                ALog.i(json.getString("msg"));
            } else {
                JSONArray datas = json.getJSONArray("data");
                JSONArray temp = datas.getJSONArray(0);
                mAssCodeEt.setText(temp.getString(6));
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!geoGB.locClient.isStarted()) {
            geoGB.locClient.start();
        }
        //在这里开启GPS定位服务
        gpsHandler.startGps(GpsHandler.TIME_INTERVAL_GPS);
        hr.postDelayed(locChange, 1000);
    }

    @Override
    protected void onPause() {
        if (gpsHandler != null)
            gpsHandler.unregisterGpsReceiver();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gpsHandler != null)
            gpsHandler.registerGpsReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpsHandler = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gpsHandler != null) {
            gpsHandler.stopGps();
        }
    }

    @Click(R.id.btn_close)
    void closeBtnClick() {
        onBackPressed();
    }

    @Click(R.id.btn_submit)
    void submitBtnClick() {
        assScanCode = mAssCodeEt.getText().toString().trim();
        if (assScanCode.equals("")) {
            Toast.makeText(context, "请先扫资产卡片编号", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO Auto-generated method stub
        AlertDialog ad = App.alertDialog(context, "提示！", "确定盘点资产？",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        submitQrCodeData();
                        // saveData();
                    }
                }, null);
        ad.show();
    }

    @Click(R.id.btn_ass_code)
    void btsAssetsNoClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_ASSETS_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_ASSETS_CODE_SCANNING);
    }

    @Click(R.id.btn_bts_serial_id)
    void btsSerialIdClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_SERIAL_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_SERIAL_CODE_SCANNING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ASSETS_CODE_SCANNING:
                if (data != null && resultCode == RESULT_OK) {
                    final Bundle bundle = data.getExtras();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            assScanCode = bundle.getString("ASS_SCAN_CODE");
                            ALog.i(assScanCode);
                            assCode = bundle.getString("ASSET_SN_CODE");
                            ALog.i(assCode);
                            mAssCodeEt.setText(assCode);
                            oldAssetCode = bundle.getString("OLD_ASSET_CODE");
                            ALog.i(oldAssetCode);
                            // 修改信息显示确认盘点按钮
                            submitBtn.setVisibility(View.VISIBLE);
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提交管理卡片信息
     */
    private void submitQrCodeData() {
        ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        phone = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        if (null != location) {
            address = location.getAddrStr();
        }
        final JSONObject output = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            array.put(city);
            array.put(county);
            array.put(town);
            array.put(objId);
            array.put(resType);
            array.put(assCode);
            array.put(oldAssetCode);
            array.put(prjNo);
            array.put(resCode);
            array.put(localResCode);
            array.put(psiteNo);
            array.put(isNew);
            array.put(serialId);
            array.put(resScanCode);
            array.put(assScanCode);
            array.put(isChecked);
            array.put(phone);
            array.put(createTime);
            array.put(updateTime);
            array.put(isDel);
            array.put(lon);
            array.put(lat);
            array.put(address);
            output.put("data", array);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final DsHandler hr = new DsHandler(context, pd);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                asyncQrData(hr, output.toString());
            }
        }).start();

    }


    private void asyncQrData(Handler hr, String output) {
        ALog.json(output);
        Message msg = null;
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("DSSaveAssResRel.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
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
            msg = hr.obtainMessage(200);
            hr.sendMessage(msg);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
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
                getMapLoc();
            }
            hr.postDelayed(locChange, 1000);
        }
    };

    private void getMapLoc() {
        if (loc != null) {
            long ts = (new Date()).getTime() - loc.getTime();
            if (ts > GpsHandler.TIME_OUT_LOC) {
                Toast.makeText(context, "GPS定位中", Toast.LENGTH_SHORT).show();
            } else {
                ALog.i(loc.toString());
                lon = String.valueOf(loc.getLongitude());
                lat = String.valueOf(loc.getLatitude());
            }
        }
    }

    /**
     * @author AndyHua
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
                    Toast.makeText(context, "资产盘点成功!", Toast.LENGTH_LONG).show();
                    ((Activity) context).finish();
                    break;
                case 500:
                    Toast.makeText(context, "连接失败：请检查网络连接!", Toast.LENGTH_LONG).show();
                    break;
                case 600:
                    Toast.makeText(context, "资产盘点成功!", Toast.LENGTH_LONG).show();
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
}
