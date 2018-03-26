package cn.com.chinaccs.datasite.main.ui.cmos;

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
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;

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
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetUserArea;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.ui.MainApp;

/**
 * 新增资产清查录入功能
 * Created by AndyHua on 2015/11/6.
 */
public class AssetsContentNewActivity extends BaseActivity implements View.OnClickListener {
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
    // 村、街道
    private EditText zdCjEt;
    private String zdCjStr;
    // 详细地址（位置名称、小区、门牌号）
    private EditText zdYxdzEt;
    private String zdYxdzStr;
    // 现场采集经度
    private EditText zdCjjdEt;
    private String zdCjjdStr;
    private Button zdCjjdBtn;
    // 现场采集纬度
    private EditText zdCjwdEt;
    private String zdCjwdStr;
    private Button zdCjwdBtn;

    //判断typr 0为新增资产清查
    private int type = 0;

    private final String TAG=AssetsContentNewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_assets_content_new);
        initToolbar(getResources().getString(R.string.assets_content_new));
        gpsHandler = ((MainApp) getApplication()).gpsHandler;
        this.findViews();
        zdDsBtn.setOnClickListener(this);
        zdQxBtn.setOnClickListener(this);
        zdCjjdBtn.setOnClickListener(this);
        zdCjwdBtn.setOnClickListener(this);
        btnSub.setOnClickListener(subLr);
        setLocation();
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
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        array = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (gpsHandler != null)
            gpsHandler.unregisterGpsReceiver();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (gpsHandler != null)
            gpsHandler.registerGpsReceiver();
    }

    private View.OnClickListener subLr = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            zdWlzmcStr = zdWlzmcEt.getText().toString().trim();
            if (zdWlzmcStr == null || zdWlzmcStr.equals("")) {
                Toast.makeText(context, "站点名称,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdWlzbhStr = zdWlzbhEt.getText().toString().trim();
            if (zdWlzbhStr == null || zdWlzbhStr.equals("")) {
                Toast.makeText(context, "站点编号,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdSfStr = zdSfEt.getText().toString().trim();
            if (zdSfStr == null || zdSfStr.equals("")) {
                Toast.makeText(context, "所在省（直辖市）,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdDsStr = zdDsEt.getText().toString().trim();
            if (zdDsStr == null || zdDsStr.equals("")) {
                Toast.makeText(context, "所在地市,现场核对、现场必选....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdQxStr = zdQxEt.getText().toString().trim();
            if (zdWlzbhStr == null || zdWlzbhStr.equals("")) {
                Toast.makeText(context, "所在区县,现场核对、现场必选....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdXzjStr = zdXzjEt.getText().toString().trim();
            if (zdXzjStr == null || zdXzjStr.equals("")) {
                Toast.makeText(context, "乡、镇、街道办事处,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdCjStr = zdCjEt.getText().toString().trim();
            if (zdCjStr == null || zdCjStr.equals("")) {
                Toast.makeText(context, "村、街道,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
                return;
            }
            zdYxdzStr = zdYxdzEt.getText().toString().trim();
            if (zdYxdzStr == null || zdYxdzStr.equals("")) {
                Toast.makeText(context, "详细地址（位置名称、小区、门牌号）,现场必填....", Toast.LENGTH_SHORT).show();
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
            AlertDialog ad = App.alertDialog(context, "提示！", "确定提交数据？",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            saveDatas();
                        }
                    }, null);
            ad.show();
        }
    };

    /**
     *
     */
    private void saveDatas() {
        ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();
        final JSONObject output = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            array.put(zdWlzmcStr);
            array.put(zdWlzbhStr);
            array.put(zdSfStr);
            array.put(zdDsStr);
            array.put(zdQxStr);
            array.put(zdXzjStr);
            array.put(zdCjStr);
            array.put(zdYxdzStr);
            array.put(zdCjjdStr);
            array.put(zdCjwdStr);
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
                conn(hr, output.toString());
            }
        }).start();

    }

    private void conn(Handler hr, String output) {
        Log.e("Asky", output);
        Message msg = null;
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("DSSaveNewAssets.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
        Log.i(TAG,output);
        Log.i(TAG,url);
        try {
            output = URLEncoder.encode(output, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String res = client.getResultByPOST(url, output);
        Log.d(App.LOG_TAG, res);
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
            id = tempData.getString(0);
            name = tempData.getString(1);
            Bundle be = new Bundle();
            be.putString("id", id);
            be.putString("name", name);
            Intent i = new Intent(context, AssetsContentActivity.class);
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
        zdWlzmcEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_wlzmc);
        zdWlzbhEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_wlzbh);
        zdSfEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_sf);
        zdDsEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_ds);
        zdDsBtn = (Button) findViewById(R.id.btn_item_zd_ds);
        zdQxEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_qx);
        zdQxBtn = (Button) findViewById(R.id.btn_item_zd_qx);
        zdXzjEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_xzj);
        zdCjEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_xzc);
        zdYxdzEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_yxdz);
        zdCjjdEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_cjjd);
        zdCjjdBtn = (Button) findViewById(R.id.btn_item_zd_cjjd);
        zdCjwdEt = (EditText) findViewById(R.id.et_item_assets_bs_zd_cjwd);
        zdCjwdBtn = (Button) findViewById(R.id.btn_item_zd_cjwd);
        btnSub = (Button) findViewById(R.id.btn_close);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_item_zd_ds:
                buildSp();
                break;
            case R.id.btn_item_zd_qx:
                break;
            case R.id.btn_item_zd_cjjd:
                setLocation();
                break;
            case R.id.btn_item_zd_cjwd:
                setLocation();
                break;
            default:
                break;
        }
    }

    private void setLocation() {
        Map<String, Double> locs = getLocs(gpsHandler.getLastLocation());
        if (locs != null) {
            double lng = locs.get("lng");
            double lat = locs.get("lat");
            zdCjjdEt.setText(lng + "");
            zdCjwdEt.setText(lat + "");
        } else {
            Toast.makeText(context, "获取定位信息失败！", Toast.LENGTH_LONG)
                    .show();
        }
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
}
