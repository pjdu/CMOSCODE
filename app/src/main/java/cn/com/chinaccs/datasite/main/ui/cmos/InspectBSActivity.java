package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.datasite.function.ContentListAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.FuncServerInset;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetNearBS;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.MainApp;

/**
 * @author Fddi
 */
public class InspectBSActivity extends Activity {
    private Context context;
    private Button btnIBs;   //当前接入基站
    private Button btnCell;  //当前接入小区
    private EditText etName;   //查询输入框
    private Button btnQuery;   //查询按钮
    private TextView tvState;   //空白时显示的布局
    private ListView lvNBs;    //附近基站列表
    private String ci;      //基站小区号 cellId(估计是统一硬件id) 扇区，天线
    private String sid;    // 系统标识(估计是统一硬件id)
    private String iBsId;   //基站id(内部id)
    private String iBsName;  //基站名称
    private String iBsLongitude;  //基站经度
    private String iBsLatitude;//基站维度
    private String iCellId;    //基站小区号 cellId(内部id)
    private String iCellName;  //小区名称
    private String iCellLongitude; //小区经度
    private String iCellLatitude; //小区维度
    //附近基站列表的属性
    private String[] bsIds;//基站id
    private String[] bsNames;//基站名称
    private String[] bsBtsIds;  //基站收发台id
    private String[] bsBscs;    //基站控制台id
    private String bsBtsId;//基站收发台id
    private String bsBsc;//基站控制台id
    private List<Map<String, String>> locList;
    private String isSingleRru;   //当前接入小区是否是独立站点
    private String[] isSingles;
    private String isSingle;   //附近基站列表中的基站，是否是独立站点
    private boolean isFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;

        setContentView(R.layout.activity_inspect_bs);
        this.findViews();
        lvNBs.setFastScrollEnabled(true);
        ci = AppCDMABaseStation.getCi(context);
        sid = AppCDMABaseStation.getCDMASid(context);
        this.buildInsertBs();
        this.buildList();
        btnIBs.setOnClickListener(clr);
        btnCell.setOnClickListener(clr);
        btnQuery.setOnClickListener(clr);
        tvState.setOnClickListener(clr);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        ci = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private OnClickListener clr = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_insetbs:
                    if (iBsId == null) {
                        Toast.makeText(context, "获取接入基站失败！", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Bundle be = new Bundle();
                        be.putString("bsId", iBsId);
                        be.putString("bsName", iBsName);
                        be.putString("longitude", iBsLongitude);
                        be.putString("latitude", iBsLatitude);
                        be.putBoolean("isRRU", false);
                        Intent i = new Intent(context, InspectPlanActivity.class);
                        i.putExtras(be);
                        startActivity(i);
                    }
                    break;
                case R.id.btn_insetcell:
                    if (iCellId == null) {
                        Toast.makeText(context, "获取接入小区失败！", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        if (isSingleRru != null && isSingleRru.equals("0")) {
                            Toast.makeText(context, "该小区不是独立物理RRU站点，请选择基站巡检！", Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Bundle be = new Bundle();
                            be.putString("bsId", iBsId);
                            be.putString("bsName", iBsName);
                            be.putString("longitude", iCellLongitude);
                            be.putString("latitude", iCellLatitude);
                            be.putBoolean("isRRU", true);
                            be.putString("cellId", iCellId);
                            be.putString("cellName", iCellName);
                            Intent i = new Intent(context, InspectPlanActivity.class);
                            i.putExtras(be);
                            startActivity(i);
                        }
                    }
                    break;
                case R.id.btn_bs_query:
                    String bsname = etName.getText().toString();
                    bsname = bsname == null ? "" : bsname;
                    if (bsname.equals("")) {
                        Toast.makeText(context, "请输入名称！", Toast.LENGTH_LONG).show();
                    } else {
                        buildList();
                    }
                    break;
                case R.id.tv_state:
                    buildList();
                    break;
                default:
                    break;
            }
        }
    };

    private OnItemClickListener itemClr = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            // TODO Auto-generated method stub
            String bsId = bsIds[index];
            String bsName = bsNames[index];
            bsBtsId = bsBtsIds[index];
            bsBsc = bsBscs[index];
            isSingle = isSingles[index];
            Bundle be = new Bundle();
            be.putString("bsId", bsId);
            be.putString("bsName", bsName);
            be.putString("longitude", locList.get(index).get("longitude"));
            be.putString("latitude", locList.get(index).get("latitude"));
            be.putString("bsBtsId", bsBtsId);
            be.putString("bsBsc", bsBsc);
            Intent i = null;
            if (isSingle.equals("1")) {
                i = new Intent(context, InspectCellOptionActivity.class);
            } else {
                be.putBoolean("isRRU", false);
                i = new Intent(context, InspectPlanActivity.class);
            }
            i.putExtras(be);
            startActivity(i);
        }
    };

    private void buildList() {
        if (isFirstIn) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    delayGetList();
                }
            }, 1500);
            isFirstIn=false;
        } else {
            delayGetList();
        }
    }

    private void delayGetList() {
        tvState.setVisibility(View.VISIBLE);
        lvNBs.setVisibility(View.GONE);
        String bsname = etName.getText().toString();
        bsname = bsname == null ? "" : bsname;
        if (!bsname.equals("")) {// 名称搜索，则不做范围搜索
            getBSListData("", "", bsname);
        } else {
            if (MainApp.geoBD != null && MainApp.geoBD.locClient != null
                    && MainApp.geoBD.locClient.isStarted()) {
                MainApp.geoBD.locClient.requestLocation();
                BDLocation loc = MainApp.geoBD.location;
                if (loc != null) {
                    String longitude = String.valueOf(loc.getLongitude());
                    String latitude = String.valueOf(loc.getLatitude());
                    getBSListData(longitude, latitude, bsname);
                } else {
                    tvState.setText("定位失败，无法获取邻近基站,点击重试");
                }
            } else {
                tvState.setText("定位失败，无法获取邻近基站,点击重试");
            }
        }
    }

    private void getBSListData(String longitude, String latitude, String bsname) {
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.i(App.LOG_TAG, output);
                // TODO Auto-generated method stub
                if (output.equals("fail")) {
                    tvState.setText(getResources().getString(
                            R.string.common_not_network));
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
                        bsIds = new String[data.length()];
                        bsNames = new String[data.length()];
                        bsBtsIds = new String[data.length()];
                        bsBscs = new String[data.length()];
                        isSingles = new String[data.length()];
                        locList = new ArrayList<Map<String, String>>();
                        for (int i = 0; i < data.length(); i++) {
                            bsIds[i] = data.getJSONArray(i).getString(0);
                            bsNames[i] = data.getJSONArray(i).getString(1);
                            bsBtsIds[i] = data.getJSONArray(i).getString(4);
                            bsBscs[i] = data.getJSONArray(i).getString(5);
                            isSingles[i] = data.getJSONArray(i).getString(6);
                            Map<String, String> loc = new HashMap<String, String>();
                            loc.put("longitude", data.getJSONArray(i)
                                    .getString(2));
                            loc.put("latitude",
                                    data.getJSONArray(i).getString(3));
                            locList.add(loc);
                        }
                        ContentListAdapter adapter = new ContentListAdapter(
                                context, bsNames);
                        lvNBs.setAdapter(adapter);
                        lvNBs.setOnItemClickListener(itemClr);
                        tvState.setVisibility(View.GONE);
                        lvNBs.setVisibility(View.VISIBLE);
                    } else {
                        tvState.setText(msg);
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        FuncGetNearBS func = new FuncGetNearBS(context);
        Log.i(App.LOG_TAG, "longitude : " + longitude + "------" + "latitude : " + latitude + "------" + "bsname : " + bsname);
        func.getData(lr, 2, longitude, latitude, bsname);
    }

    private void buildInsertBs() {
        btnIBs.setText("暂无数据");
        btnCell.setText("暂无数据");
        final ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_request));
        pd.show();
        FuncServerInset func = new FuncServerInset(context);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.i(App.LOG_TAG, output);
                // TODO Auto-generated method stub
                pd.dismiss();
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
                        JSONArray abs = json.getJSONArray("bs");
                        iBsId = abs.getString(0);
                        iBsName = abs.getString(1);
                        iBsLongitude = abs.getString(4);
                        iBsLatitude = abs.getString(5);
                        btnIBs.setText(iBsName);
                        JSONArray cellOption = json.getJSONArray("cell");
                        bsBtsId = cellOption.getString(2);
                        bsBsc = cellOption.getString(3);
                        iCellId = cellOption.getString(0);
                        iCellName = cellOption.getString(1);
                        iCellLongitude = cellOption.getString(4);
                        iCellLatitude = cellOption.getString(5);
                        isSingleRru = cellOption.getString(6);
                        btnCell.setText(iCellName);
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        Log.i(App.LOG_TAG, "ci : " + ci + "----------------" + "sid : " + sid);
        func.insetQuery(lr, ci, sid);
    }

    private void findViews() {
        btnIBs = (Button) findViewById(R.id.btn_insetbs);
        btnCell = (Button) findViewById(R.id.btn_insetcell);
        etName = (EditText) findViewById(R.id.et_bs_name);
        btnQuery = (Button) findViewById(R.id.btn_bs_query);
        tvState = (TextView) findViewById(R.id.tv_state);
        lvNBs = (ListView) findViewById(R.id.lv_near_bs);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // MainApp.geoBD.locClient.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!MainApp.geoBD.locClient.isStarted()) {
            MainApp.geoBD.locClient.start();
        }
    }
}
