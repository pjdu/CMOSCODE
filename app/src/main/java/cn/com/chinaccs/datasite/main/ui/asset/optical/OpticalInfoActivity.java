/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-23 09:49:44.
 */

package cn.com.chinaccs.datasite.main.ui.asset.optical;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
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
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.ui.adapters.AssetOpticalInfoAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetUserArea;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/*
 * 资产盘点
 * 光网络类局站信息查询列表
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
public class OpticalInfoActivity extends BaseActivity {

    private Context context;
    private Spinner spCity;
    private Spinner spSearchType;
    private Spinner spCounty;
    private EditText etname;
    private String countyName;
    private LinearLayout btnQuery;
    private ListView lvAssets;
    private ProgressBar pbAssets;
    private TextView txtState;
    private String cityName;
    // private String countyName;
    private String[] citys;
    // private String[] countys;
    private List<JSONArray> listRes;
    private Integer pagestart = 0;
    private Integer total = 0;
    private Boolean isRequestState = false;
    private ProgressDialog proDialog;
    private AssetOpticalInfoAdapter assetsAdapter;
    private String[] searchType;
    private String[] countyStrings;
    private TextView tvAsTotal;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_asset_mobile);
        initToolbar("局站查询");
        this.findViews();
        this.buildCitySp();
        btnQuery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pagestart = 0;
                total = 0;
                cityName = DataSiteStart.AREANAME[spCity
                        .getSelectedItemPosition()];
                if (!isRequestState) {
                    isRequestState = true;
                    buildList();
                }
            }
        });
        searchType = new String[]{
                getResources().getString(R.string.asset_optical_name)};
        ArrayAdapter<String> searchAd = new ArrayAdapter<String>(context,
                R.layout.item_spinner_s, searchType);
        searchAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchType.setPrompt("局站名称");
        spSearchType.setAdapter(searchAd);
        spSearchType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (((TextView) arg1).getText() == searchType[0]) {
                    etname.setHint(getResources().getString(
                            R.string.asset_optical_hint_name));
                } else {
                    etname.setHint(getResources().getString(
                            R.string.asset_optical_hint_name));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        spCity = null;
        spCounty = null;
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

    private OnItemClickListener lrList = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            // TODO Auto-generated method stub
            JSONArray array = listRes.get(index);
            try {
                String id = array.getString(0);
                String name = array.getString(1);
                Bundle be = new Bundle();
                be.putString("id", id);
                be.putString("name", name);
                Intent i = new Intent(context, OpticalConInfoActivity.class);
                i.putExtras(be);
                startActivity(i);
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
        listRes = new ArrayList<JSONArray>();
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

    private void buildCitySp() {
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
                    initCitySpAdapter(output);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            ;
        };
        func.getData(lr);
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

    private void initCitySpAdapter(String output) throws JSONException {
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
                // changed for out of index
                // String id = data.getString(0);
                String id = data.getString(0).length() >= 4 ? data.getString(0).substring(0, 4) : data.getString(0);
                ALog.d(id);
                List<String> arealist = new ArrayList<String>();
                List<String> aclist = new ArrayList<String>();
                final List<List<String>> townlist = new ArrayList<List<String>>();
                arealist.add(data.getString(1));
                aclist.add(data.getString(0));
                List<String> list = new ArrayList<String>();
                for (int i = 1; i < datas.length(); i++) {
                    data = datas.getJSONArray(i);
                    // changed for out of index
                    // if (id.equals(data.getString(2))) {
                    String temp = data.getString(2).length() >= 4 ? data.getString(2).substring(0, 4) : data.getString(2);
                    if (id.equals(temp)) {
                        list.add(data.getString(1));
                    } else {
                        // changed for out of index
                        // id = data.getString(0);
                        id = data.getString(0).length() >= 4 ? data.getString(0).substring(0, 4) : data.getString(0);
                        // ALog.d(id);
                        arealist.add(data.getString(1));
                        aclist.add(data.getString(0));
                        townlist.add(list);
                        list = new ArrayList<>();
                    }
                }
                townlist.add(list);
                citys = new String[arealist.size()];
                for (int i = 0; i < arealist.size(); i++) {
                    citys[i] = arealist.get(i);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        context, R.layout.item_spinner, citys);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCity.setAdapter(adapter);
                spCity.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View view,
                                               int index, long arg3) {
                        // TODO Auto-generated method stub
                        cityName = citys[index];
                        // List<String> list = townlist.get(index);
                        // getSpSecond(list);
                        /*etname.setText(null);

                        pagestart = 0;
                        total = 0;
                        if (!isRequestState) {
                            isRequestState = true;
                            buildList();// 创建列表
                        }*/
                        // init county
                        buildCounty(cityName);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) { //
                        // TODO Auto-generated method stub

                    }
                });
                spCity.setSelection(0, true);
                setListBottomEvent();
            }
        }
    }

    private void buildCounty(String city) {
        if (city.equals("西双版纳州")) {
            city = "版纳";
        } else {
            city = city.substring(0, city.length() - 1);
        }
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
                    initCountySpAdapter(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ;
        };
        func.getCountyData(lr, city);
    }

    private void initCountySpAdapter(String output) throws JSONException {
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
                countyStrings = new String[datas.length() + 1];
                countyStrings[0] = "全部";
                for (int i = 0; i < datas.length(); i++) {
                    JSONArray temp = datas.getJSONArray(i);
                    countyStrings[i + 1] = temp.getString(1);
                }
                ArrayAdapter<String> searchAd = new ArrayAdapter<>(context,
                        R.layout.item_spinner, countyStrings);
                searchAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCounty.setPrompt("全部");
                spCounty.setAdapter(searchAd);
                spCounty.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View view,
                                               int index, long arg3) {
                        // TODO Auto-generated method stub
                        countyName = countyStrings[index];
                        etname.setText(null);

                        etname.setText(null);

                        pagestart = 0;
                        total = 0;
                        if (!isRequestState) {
                            isRequestState = true;
                            buildList();// 创建列表
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
                spCounty.setSelection(0, true);
                setListBottomEvent();
            }
        }
    }

    private void findViews() {
        spCity = (Spinner) findViewById(R.id.sp_assets_area);
        etname = (EditText) findViewById(R.id.edit_assets_name);
        btnQuery = (LinearLayout) findViewById(R.id.btn_assets_query);
        lvAssets = (ListView) findViewById(R.id.lv_assets);
        pbAssets = (ProgressBar) findViewById(R.id.pb_assets);
        txtState = (TextView) findViewById(R.id.txt_assets_state);
        spCounty = (Spinner) findViewById(R.id.sp_assets_ins);
        spSearchType = (Spinner) findViewById(R.id.sp_assets_search_select);
        tvAsTotal = (TextView) findViewById(R.id.tv_as_total);
    }

    private void setListBottomEvent() {
        lvAssets.setOnScrollListener(new OnScrollListener() {

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
                    final Handler hr = new BsHandler(OpticalInfoActivity.this,
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
        cityName = citys[spCity.getSelectedItemPosition()];
        if (cityName.equals("西双版纳州")) {
            cityName = "版纳";
        } else {
            cityName = cityName.substring(0, cityName.length() - 1);
        }
        Message msg = null;
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        try {
            String name = etname.getText().toString();
            name = name == null ? "" : name;
            String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + cityName + countyName + name + pagestart);
            String cityNameStr = URLEncoder.encode(cityName, App.ENCODE_UTF8);
            String countyNameStr = URLEncoder.encode(countyName, App.ENCODE_UTF8);
            name = URLEncoder.encode(name, App.ENCODE_UTF8);

            url.append("AssetsStationList.do?cityname=").append(cityNameStr)
                    .append("&countyname=").append(countyNameStr)
                    .append("&sitename=").append(name)
                    .append("&pagestart=").append(pagestart)
                    .append("&sign=").append(sign);
            AppHttpConnection conn = new AppHttpConnection(context,
                    url.toString());
            ALog.d(url.toString());
            String conResult = conn.getConnectionResult();
            ALog.d(conResult);
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

        private OpticalInfoActivity activity;
        private boolean isPageRequest;

        public BsHandler(Activity activity, boolean isPageRequest) {
            this.activity = (OpticalInfoActivity) activity;
            this.isPageRequest = isPageRequest;
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 200:
                    if (!isPageRequest) {
                        activity.assetsAdapter = null;
                        activity.assetsAdapter = new AssetOpticalInfoAdapter(activity,
                                activity.listRes);
                        activity.lvAssets.setVisibility(View.VISIBLE);
                        activity.txtState.setVisibility(View.GONE);
                        activity.tvAsTotal.setText("共" + activity.total + "个局站");
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
