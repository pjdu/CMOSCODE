/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 10:56:42.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetUserArea;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.adapters.AssetMobileCardAdapter;

public class MobileCardsActivity extends BaseActivity {
    public final static int REQUEST_ASSETS_CODE_SCANNING = 1;
    public final static int REQUEST_SERIAL_CODE_SCANNING = 2;

    private Context context;
    private Spinner spArea;
    private Spinner spIns;
    private Spinner spSearchType;
    private EditText etname;
    private String insInfo;
    private LinearLayout btnQuery;
    private ListView lvAssets;
    private ProgressBar pbAssets;
    private TextView txtState;
    private String areaName;
    private String[] areas;
    private List<JSONArray> listRes;
    private Integer pagestart = 0;
    private Integer total = 0;
    private Boolean isRequestState = false;
    private ProgressDialog proDialog;
    private AssetMobileCardAdapter assetsAdapter;
    private String[] searchType;
    private String[] insStrings;
    private TextView tvAsTotal;
    private String assetsNo = null;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_asset_mobile_cards);
        initToolbar("资产卡片");
        Bundle bundle = getIntent().getExtras();
        assetsNo = bundle.getString("assetsNo", "");
        areaName = bundle.getString("areaName", "");
        ALog.d(assetsNo);
        ALog.d(areaName);
        this.findViews();
        this.buildSp();
        btnQuery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pagestart = 0;
                total = 0;
                areaName = DataSiteStart.AREANAME[spArea
                        .getSelectedItemPosition()];
                if (!isRequestState) {
                    isRequestState = true;
                    buildList();
                }
            }
        });

        searchType = new String[]{
                getResources().getString(R.string.assets_card_num),
                getResources().getString(R.string.assets_card_name)};
        ArrayAdapter<String> searchAd = new ArrayAdapter<>(context,
                R.layout.item_spinner_s, searchType);
        searchAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchType.setPrompt("请选择查询方式");
        spSearchType.setAdapter(searchAd);
        spSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (((TextView) arg1).getText() == searchType[0]) {
                    etname.setHint(getResources().getString(
                            R.string.hint_card_num));
                } else {
                    etname.setHint(getResources().getString(
                            R.string.hint_card_name));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        insInfoSpSecond();
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        spArea = null;
        spIns = null;
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

    private AdapterView.OnItemClickListener lrList = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            // TODO Auto-generated method stub
            JSONArray array = listRes.get(index);
            ALog.json(array.toString());
            try {
                String id = array.getString(0);
                String assCode = array.getString(2);
                Bundle be = new Bundle();
                be.putString("id", id);
                be.putString("assCode", assCode);
                Intent intent = new Intent(context, MobileCardsConActivity_.class);
                intent.putExtras(be);
                startActivityForResult(intent, REQUEST_ASSETS_CODE_SCANNING);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private void buildList() {
        txtState.setVisibility(View.VISIBLE);
        txtState.setText(getResources().getString(R.string.common_request));
        listRes = null;
        listRes = new ArrayList<>();
        proDialog = null;
        proDialog = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        proDialog.show();
        final BsHandler hr = new BsHandler(this, false);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                conn(hr);
            }
        }).start();

    }

    private void buildSp() {
        if (areaName.equals("")) {
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
            };
            func.getData(lr);
        } else {
            areas = new String[]{areaName};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context, R.layout.item_spinner, areas);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spArea.setAdapter(adapter);
            spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View view,
                                           int index, long arg3) {
                    // TODO Auto-generated method stub
                    areaName = areas[index];
                    // List<String> list = townlist.get(index);
                    // getSpSecond(list);
                    // etname.setText(null);

                    pagestart = 0;
                    total = 0;
                    if (!isRequestState) {
                        isRequestState = true;
                        buildList();// 创建列表
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) { //
                    // TODO Auto-generated method stub

                }
            });
            spArea.setSelection(0, true);
            setListBottomEvent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_asset_new,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int i = item.getItemId();
        if (i == R.id.menu_asset_new){
			Intent intent = new Intent(AssetsCheckInfoActivity.this, AssetsContentNewActivity.class);
			intent.putExtra("type",0);
			startActivity(intent);
		}*/
        return true;
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
                List<String> arealist = new ArrayList<>();
                List<String> aclist = new ArrayList<>();
                final List<List<String>> townlist = new ArrayList<>();
                arealist.add(data.getString(1));
                aclist.add(data.getString(0));
                List<String> list = new ArrayList<>();
                for (int i = 1; i < datas.length(); i++) {
                    data = datas.getJSONArray(i);
                    if (id.equals(data.getString(2))) {
                        list.add(data.getString(1));
                    } else {
                        id = data.getString(0);
                        arealist.add(data.getString(1));
                        aclist.add(data.getString(0));
                        townlist.add(list);
                        list = new ArrayList<>();
                    }
                }
                townlist.add(list);
                areas = new String[arealist.size()];
                for (int i = 0; i < arealist.size(); i++) {
                    areas[i] = arealist.get(i);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        context, R.layout.item_spinner, areas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spArea.setAdapter(adapter);
                spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View view,
                                               int index, long arg3) {
                        // TODO Auto-generated method stub
                        areaName = areas[index];
                        // List<String> list = townlist.get(index);
                        // getSpSecond(list);
                        // etname.setText(null);

                        pagestart = 0;
                        total = 0;
                        if (!isRequestState) {
                            isRequestState = true;
                            buildList();// 创建列表
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) { //
                        // TODO Auto-generated method stub

                    }
                });
                spArea.setSelection(0, true);
                setListBottomEvent();
            }
        }
    }

    private void insInfoSpSecond() {
        insStrings = new String[]{getResources().getString(R.string.ins_all),
                getResources().getString(R.string.check_ins),
                getResources().getString(R.string.check_unins)};
        ArrayAdapter<String> searchAd = new ArrayAdapter<>(context,
                R.layout.item_spinner, insStrings);
        searchAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIns.setPrompt("盘点情况");
        spIns.setAdapter(searchAd);
        spIns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int index, long arg3) {
                // TODO Auto-generated method stub
                insInfo = insStrings[index];
                // etname.setText(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        spIns.setSelection(0, true);
        setListBottomEvent();
    }

    private void findViews() {
        spArea = (Spinner) findViewById(R.id.sp_assets_area);
        etname = (EditText) findViewById(R.id.edit_assets_name);
        etname.setText(assetsNo);
        btnQuery = (LinearLayout) findViewById(R.id.btn_assets_query);
        lvAssets = (ListView) findViewById(R.id.lv_assets);
        pbAssets = (ProgressBar) findViewById(R.id.pb_assets);
        txtState = (TextView) findViewById(R.id.txt_assets_state);
        spIns = (Spinner) findViewById(R.id.sp_assets_ins);
        spSearchType = (Spinner) findViewById(R.id.sp_assets_search_select);
        tvAsTotal = (TextView) findViewById(R.id.tv_as_total);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            this.setResult(RESULT_OK, data);
            this.finish();
        }
    }

    private void setListBottomEvent() {
        lvAssets.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    if (total <= pagestart && pagestart != 0) {
                        // 起始页从零开始，累加10，当大于总数时说明已无数据
                        return;
                    }
                    if (isRequestState) {
                        // 避免重复请求，若正在请求数据时返回
                        return;
                    }
                    isRequestState = true;
                    pbAssets.setVisibility(View.VISIBLE);
                    final Handler hr = new BsHandler(MobileCardsActivity.this,
                            true);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            conn(hr);
                        }
                    }).start();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void conn(Handler hr) {
        areaName = areas[spArea.getSelectedItemPosition()];
        if (areaName.equals("西双版纳")){
            areaName = "版纳";
        } else {
            areaName = areaName.substring(0, areaName.length() - 1);
        }
        ALog.d(areaName);
        String SearchTypeRe = searchType[spSearchType.getSelectedItemPosition()];
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        Message msg = null;
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        String name = etname.getText().toString();
        name = name == null ? "" : name;
        try {
            if (SearchTypeRe.equals(getResources().getString(
                    R.string.assets_card_name))) {
                String sType = "bs_name";
                String zd_wlzmc = etname.getText().toString();
                zd_wlzmc = zd_wlzmc == null ? "" : zd_wlzmc;
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
                        + areaName + insInfo + zd_wlzmc + sType + pagestart);

                String area = URLEncoder.encode(areaName, App.ENCODE_UTF8);
                String insInfos = URLEncoder.encode(insInfo, App.ENCODE_UTF8);
                zd_wlzmc = URLEncoder.encode(zd_wlzmc, App.ENCODE_UTF8);

                url.append("AssetsCardList.do?userid=").append(userId)
                        .append("&areaname=").append(area).append("&insInfo=")
                        .append(insInfos).append("&name=").append(zd_wlzmc)
                        .append("&searchType=").append(sType)
                        .append("&pagestart=").append(pagestart)
                        .append("&sign=").append(sign);

            } else {
                String sType = "bs_num";
                String zd_wlzbh = etname.getText().toString();
                zd_wlzbh = zd_wlzbh == null ? "" : zd_wlzbh;
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
                        + areaName + insInfo + zd_wlzbh + sType + pagestart);

                String area = URLEncoder.encode(areaName, App.ENCODE_UTF8);
                String insInfos = URLEncoder.encode(insInfo, App.ENCODE_UTF8);
                zd_wlzbh = URLEncoder.encode(zd_wlzbh, App.ENCODE_UTF8);

                url.append("AssetsCardList.do?userid=").append(userId)
                        .append("&areaname=").append(area).append("&insInfo=")
                        .append(insInfos).append("&name=").append(zd_wlzbh)
                        .append("&searchType=").append(sType)
                        .append("&pagestart=").append(pagestart)
                        .append("&sign=").append(sign);
            }
            AppHttpConnection conn = new AppHttpConnection(context,
                    url.toString());
            ALog.d(url.toString());
            String conResult = conn.getConnectionResult();
            ALog.json(conResult);
            if (conResult.equals("fail")) {
                msg = hr.obtainMessage(500);
                hr.sendMessage(msg);
                return;
            }
            JSONObject resJson = new JSONObject(conResult);
            String result = resJson.getString("result");
            if (!result.equals("1")) {
                msg = hr.obtainMessage(501, resJson.getString("msg"));
                hr.sendMessage(msg);
                return;
            }
            pagestart += 10;
            String totalStr = resJson.getString("total");
            total = Integer.parseInt(totalStr);
            JSONArray array = resJson.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONArray data = array.getJSONArray(i);
                listRes.add(data);
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

    static class BsHandler extends Handler {

        private MobileCardsActivity activity;
        private boolean isPageRequest;

        public BsHandler(Activity activity, boolean isPageRequest) {
            this.activity = (MobileCardsActivity) activity;
            this.isPageRequest = isPageRequest;
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 200:
                    if (!isPageRequest) {
                        activity.assetsAdapter = null;
                        activity.assetsAdapter = new AssetMobileCardAdapter(activity,
                                activity.listRes);
                        activity.lvAssets.setVisibility(View.VISIBLE);
                        activity.txtState.setVisibility(View.GONE);
                        activity.tvAsTotal.setText("共" + activity.total + "个资产卡片");
                        activity.lvAssets.setAdapter(activity.assetsAdapter);
                        activity.lvAssets.setOnItemClickListener(activity.lrList);
                    } else {
                        if (activity.assetsAdapter != null)
                            activity.assetsAdapter.notifyDataSetChanged();
                    }
                    break;
                case 500:
                    activity.txtState.setText("连接失败：请检查网络连接！");
                    Toast.makeText(activity.context, "连接失败：请检查网络连接！",
                            Toast.LENGTH_LONG).show();
                    break;
                case 501:
                    String info = (String) msg.obj;
                    activity.txtState.setText("提示：" + info);
                    Toast.makeText(activity.context, "提示：" + info,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    activity.txtState.setText("未知错误");
                    Toast.makeText(activity.context, "未知错误", Toast.LENGTH_LONG)
                            .show();
                    break;
            }
            if (activity.proDialog != null && activity.proDialog.isShowing()) {
                activity.proDialog.dismiss();
                activity.proDialog = null;
            }
            if (isPageRequest) {
                activity.pbAssets.setVisibility(View.GONE);
            }
            activity.isRequestState = false;
        }

    }
}
