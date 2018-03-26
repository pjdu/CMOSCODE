/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 10:52:55.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

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
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.blankj.ALog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.common.WGSTOGCJ02;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetFieldOptions;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.ui.MainApp;

/**
 * 资产信息
 * 基本信息
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
@EActivity(R.layout.activity_asset_mobile_basic_content)
public class MobileBasicConActivity extends BaseActivity {
    public final static int REQUEST_CODE_SCANNING = 3;

    private Context context = null;
    private JSONArray array;
    private String datas;
    private String id;
    private String title = "基础信息";
    private GpsHandler gpsHandler;

    @ViewById(R.id.et_psite_name)
    EditText mPSiteNameEt;

    @ViewById(R.id.et_psite_no)
    EditText mPSiteNoEt;

    @ViewById(R.id.et_psite_assets_no)
    EditText mPSiteAssetsNoEt;

    @ViewById(R.id.et_psite_province)
    EditText mPSiteProvinceEt;

    @ViewById(R.id.et_psite_city)
    EditText mPSiteCityEt;

    @ViewById(R.id.et_psite_county)
    EditText mPSiteCountyEt;

    @ViewById(R.id.et_psite_town)
    EditText mPSiteTownEt;

    @ViewById(R.id.et_psite_a_village)
    EditText mPSiteAVillageEt;

    @ViewById(R.id.et_psite_village)
    EditText mPSiteVillageEt;

    @ViewById(R.id.et_psite_address)
    EditText mPSiteAddressEt;

    @ViewById(R.id.et_psite_lon)
    EditText mPSiteLonEt;

    @ViewById(R.id.et_psite_lat)
    EditText mPSiteLatEt;

    @ViewById(R.id.et_psite_cover_type)
    EditText mPSiteCoverTypeEt;

    @ViewById(R.id.et_psite_main_level)
    EditText mPSiteMainLevelEt;

    @ViewById(R.id.et_psite_site_level)
    EditText mPSiteSiteLevelEt;

    @ViewById(R.id.et_psite_share_type)
    EditText mPSiteShareTypeEt;

    @ViewById(R.id.et_psite_ownership_type)
    EditText mPSiteOwnershipTypeEt;

    @ViewById(R.id.et_psite_device_type)
    EditText mPSiteDeviceTypeEt;

    @ViewById(R.id.et_psite_site_type)
    EditText mPSiteSiteTypeEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        gpsHandler = ((MainApp) getApplication()).gpsHandler;

        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        title = be.getString("title");
        datas = be.getString("json");
        if (null != datas) {
            try {
                array = new JSONArray(datas);
                array = array.getJSONArray(0);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ALog.d(array);
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

        mPSiteNameEt.setText(array.getString(1));
        mPSiteNoEt.setText(array.getString(2));
        mPSiteAssetsNoEt.setText(array.getString(3));
        mPSiteProvinceEt.setText(array.getString(4));
        mPSiteCityEt.setText(array.getString(5));
        mPSiteCountyEt.setText(array.getString(6));
        mPSiteTownEt.setText(array.getString(7));
        mPSiteAVillageEt.setText(array.getString(8));
        mPSiteVillageEt.setText(array.getString(9));
        mPSiteAddressEt.setText(array.getString(10));
        mPSiteLonEt.setText(array.getString(11));
        mPSiteLatEt.setText(array.getString(12));
        mPSiteCoverTypeEt.setText(array.getString(13));
        mPSiteMainLevelEt.setText(array.getString(14));
        mPSiteSiteLevelEt.setText(array.getString(15));
        mPSiteShareTypeEt.setText(array.getString(16));
        mPSiteOwnershipTypeEt.setText(array.getString(17));
        mPSiteDeviceTypeEt.setText(array.getString(18));
        mPSiteSiteTypeEt.setText(array.getString(19));
    }

    @Click(R.id.btn_close)
    void closeBtnClick() {
        onBackPressed();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SCANNING:
                if (data != null && resultCode == RESULT_OK) {
                    final Bundle bundle = data.getExtras();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ALog.i(bundle.getString("result"));
                            String num = bundle.getString("result");
                            int index = bundle.getInt("index");
                           /* ((EditText) findViewById(R.id.et_item_modify1)).setText(num);*/
                            Toast.makeText(context, "序列号:" + num, Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    @UiThread
    void updateContent(Bundle bundle) {

    }

    @Click({R.id.btn_psite_lon, R.id.btn_psite_lat})
    void locBtnClick() {
        if (!App.isGpsOpen(context)) {
            Intent intent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }
        Location loc = gpsHandler.getLastLocation();
        Map<String, Double> locs = getLocs(loc);
        if (locs != null) {
            double lng = locs.get("lng");
            mPSiteLonEt.setText(String.valueOf(lng));
            double lat = locs.get("lat");
            mPSiteLatEt.setText(String.valueOf(lat));
            String text = "获取定位信息成功:"
                    + "\n[经度]" + lng
                    + "\n[纬度]" + lat;
            Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(context, "获取定位信息失败！", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private Map<String, Double> getLocs(Location loc) {
        if (loc != null) {// GPS定位
            long ot = new Date().getTime() - loc.getTime();
            if (ot <= GpsHandler.TIME_OUT_LOC) {
                Map<String, Double> locs = new HashMap<String, Double>();
                locs.put("lng", loc.getLongitude());
                locs.put("lat", loc.getLatitude());
                ALog.d("location by GPS --" + locs.get("lng") + "/"
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
                ALog.d("location by baidu --" + locs.get("lng")
                        + "/" + locs.get("lat"));
                return locs;
            }
        }
        return null;
    }

    @Click(R.id.btn_psite_cover_type)
    void coverTypeBtnClick() {
        FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
        OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = new JSONArray(output);
                    final String[] items = new String[array
                            .length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray data = array.getJSONArray(i);
                        items[i] = data.getString(0);
                    }

                    DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            // TODO Auto-generated method stub
                            mPSiteCoverTypeEt.setText(items[index]);
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(context)
                            .setTitle("覆盖场景")
                            .setSingleChoiceItems(items, -1, dlr)
                            .show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fos.getData(oflr, "ZD_FGCJ", "cdma_assets");
    }

    @Click(R.id.btn_psite_main_level)
    void mainLevelBtnClick() {
        FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
        OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = new JSONArray(output);
                    final String[] items = new String[array
                            .length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray data = array.getJSONArray(i);
                        items[i] = data.getString(0);
                    }

                    DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            // TODO Auto-generated method stub
                            mPSiteMainLevelEt.setText(items[index]);
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(context)
                            .setTitle("维护难度")
                            .setSingleChoiceItems(items, -1, dlr)
                            .show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fos.getData(oflr, "ZD_WHND", "cdma_assets");
    }

    @Click(R.id.btn_psite_site_level)
    void siteLevelBtnClick() {
        FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
        OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = new JSONArray(output);
                    final String[] items = new String[array
                            .length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray data = array.getJSONArray(i);
                        items[i] = data.getString(0);
                    }

                    DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            // TODO Auto-generated method stub
                            mPSiteSiteLevelEt.setText(items[index]);
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(context)
                            .setTitle("电信基站等级")
                            .setSingleChoiceItems(items, -1, dlr)
                            .show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fos.getData(oflr, "ZD_JZDJ", "cdma_assets");
    }

    @Click(R.id.btn_psite_share_type)
    void shareTypeBtnClick() {
        FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
        OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = new JSONArray(output);
                    final String[] items = new String[array
                            .length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray data = array.getJSONArray(i);
                        items[i] = data.getString(0);
                    }

                    DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            // TODO Auto-generated method stub
                            mPSiteShareTypeEt.setText(items[index]);
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(context)
                            .setTitle("共享类型")
                            .setSingleChoiceItems(items, -1, dlr)
                            .show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fos.getData(oflr, "ZD_CDGX", "cdma_assets");
    }

    @Click(R.id.btn_psite_ownership_type)
    void ownershipTypeBtnClick() {
        FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
        OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = new JSONArray(output);
                    final String[] items = new String[array
                            .length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray data = array.getJSONArray(i);
                        items[i] = data.getString(0);
                    }

                    DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            // TODO Auto-generated method stub
                            mPSiteOwnershipTypeEt.setText(items[index]);
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(context)
                            .setTitle("产权性质")
                            .setSingleChoiceItems(items, -1, dlr)
                            .show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fos.getData(oflr, "ZD_CQXZ", "cdma_assets");
    }

    @Click(R.id.btn_psite_device_type)
    void deviceTypeBtnClick() {
        FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
        OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = new JSONArray(output);
                    final String[] items = new String[array
                            .length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray data = array.getJSONArray(i);
                        items[i] = data.getString(0);
                    }

                    DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            // TODO Auto-generated method stub
                            mPSiteDeviceTypeEt.setText(items[index]);
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(context)
                            .setTitle("设备类型")
                            .setSingleChoiceItems(items, -1, dlr)
                            .show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fos.getData(oflr, "ZD_SBLX", "cdma_assets");
    }

    @Click(R.id.btn_psite_site_type)
    void siteTypeBtnClick() {
        FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
        OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = new JSONArray(output);
                    final String[] items = new String[array
                            .length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray data = array.getJSONArray(i);
                        items[i] = data.getString(0);
                    }

                    DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            // TODO Auto-generated method stub
                            mPSiteSiteTypeEt.setText(items[index]);
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(context)
                            .setTitle("站点类型")
                            .setSingleChoiceItems(items, -1, dlr)
                            .show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fos.getData(oflr, "ZD_ZDLX", "cdma_assets");
    }

    @Click(R.id.btn_submit)
    void submitBtnClick() {
        // TODO Auto-generated method stub
        AlertDialog ad = App.alertDialog(context, "提示！", "确定提交数据？",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        saveData();
                    }
                }, null);
        ad.show();
    }

    /**
     *
     */
    private void saveData() {
        ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();
        final JSONObject output = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            data.put(mPSiteNameEt.getText().toString().trim());
            data.put(mPSiteNoEt.getText().toString().trim());
            data.put(mPSiteAssetsNoEt.getText().toString().trim());
            data.put(mPSiteProvinceEt.getText().toString().trim());
            data.put(mPSiteCityEt.getText().toString().trim());
            data.put(mPSiteCountyEt.getText().toString().trim());
            data.put(mPSiteTownEt.getText().toString().trim());
            data.put(mPSiteAVillageEt.getText().toString().trim());
            data.put(mPSiteVillageEt.getText().toString().trim());
            data.put(mPSiteAddressEt.getText().toString().trim());
            data.put(mPSiteLonEt.getText().toString().trim());
            data.put(mPSiteLatEt.getText().toString().trim());
            data.put(mPSiteCoverTypeEt.getText().toString().trim());
            data.put(mPSiteMainLevelEt.getText().toString().trim());
            data.put(mPSiteSiteLevelEt.getText().toString().trim());
            data.put(mPSiteShareTypeEt.getText().toString().trim());
            data.put(mPSiteOwnershipTypeEt.getText().toString().trim());
            data.put(mPSiteDeviceTypeEt.getText().toString().trim());
            data.put(mPSiteSiteTypeEt.getText().toString().trim());
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            output.put("userid",
                    share.getString(AppCheckLogin.SHARE_USER_ID, ""));
            output.put("id", id);
            output.put("data", data);
            output.put("start", 1);
            output.put("end", 19);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final DsHandler hr = new DsHandler(context, pd);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                conn(hr, output.toString());
            }
        }).start();

    }

    private void conn(Handler hr, String output) {
        ALog.json(output);
        Message msg = null;
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("EditSaveAssets.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
        try {
            output = URLEncoder.encode(output, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String res = client.getResultByPOST(url, output);
        ALog.d(App.LOG_TAG, res);
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
}
