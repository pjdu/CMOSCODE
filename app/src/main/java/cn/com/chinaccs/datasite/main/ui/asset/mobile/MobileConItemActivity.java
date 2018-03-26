/*
 * Created by AndyHua on 2017/10/27.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-27 09:40:57.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.ui.adapters.AssetOpticalConItemAdapter;
import cn.com.chinaccs.datasite.main.zbar.ScannerActivity;

/**
 * 资源信息列表
 */
public class MobileConItemActivity extends BaseActivity {
    public final static int REQUEST_SERIAL_CODE_SCANNING = 2;
    public final static int REQUEST_SUBMIT_OK = 10;
    public final static String REQUEST_CODE_SCANNING = "REQUEST_TYPE";

    private Context context;
    private ListView lvAssets;
    private TextView txtState;
    private List<JSONArray> listRes;
    private ProgressDialog proDialog;
    private AssetOpticalConItemAdapter assetsAdapter;

    private Bundle be = null;
    private String type = null;
    private String id = null;

    private ProgressBar pbAssets;
    private Integer pagestart = 0;
    private Integer total = 0;
    private Boolean isRequestState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_mobile_bts);
        context = this;

        be = getIntent().getExtras();
        type = be.getString("title");
        id = be.getString("id");
        initToolbar(type);
        // initView
        this.findViews();
        if (!isRequestState) {
            isRequestState = true;
            buildList();// 创建列表
        }
        setListBottomEvent();
    }

    private void findViews() {
        lvAssets = (ListView) findViewById(R.id.lv_assets);
        txtState = (TextView) findViewById(R.id.txt_assets_state);
        pbAssets = (ProgressBar) findViewById(R.id.pb_assets);
    }

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

    private OnItemClickListener lrList = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            // TODO Auto-generated method stub
            JSONArray array = listRes.get(index);
            be.putString("json", array.toString());
            Intent i = new Intent(context, MobileConItemInfoActivity_.class);
            i.putExtras(be);
            startActivityForResult(i, REQUEST_SUBMIT_OK);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_card_qr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_card_qr) {
            Bundle be = new Bundle();
            be.putInt(REQUEST_CODE_SCANNING, REQUEST_SERIAL_CODE_SCANNING);
            Intent intent = new Intent(context, ScannerActivity.class);
            intent.putExtras(be);
            startActivityForResult(intent, ScannerActivity.REQUEST_SERIAL_CODE_SCANNING);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SERIAL_CODE_SCANNING:
                if (data != null && resultCode == RESULT_OK) {
                    final Bundle bundle = data.getExtras();
                    String serialId = bundle.getString("result");
                    ALog.i(serialId);
                    JSONArray array = gotoContent(serialId);
                    if (array != null) {
                        be.putString("json", array.toString());
                        Intent intent = new Intent(context, MobileConItemInfoActivity_.class);
                        intent.putExtras(be);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "无法查到该设备!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_SUBMIT_OK:
               /* pagestart = 0;
                total = 0;
                this.buildList();*/
            default:
                break;
        }
    }

    private JSONArray gotoContent(String serialId) {
        JSONArray result = null;
        for (int i = 0; i < listRes.size(); i++) {
            JSONArray array = listRes.get(i);
            String temp = "";
            try {
                temp = array.getString(37);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (temp.equals(serialId)) {
                result = array;
            }
        }
        return result;
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
                    final Handler hr = new BsHandler(MobileConItemActivity.this,
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
        Message msg = null;
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        try {
            String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + id + type + pagestart);
            String typeStr = URLEncoder.encode(type, App.ENCODE_UTF8);

            url.append("PsiteByTypeList.do?psiteno=").append(id)
                    .append("&type=").append(typeStr)
                    .append("&pagestart=").append(pagestart)
                    .append("&sign=").append(sign);
            AppHttpConnection conn = new AppHttpConnection(context,
                    url.toString());
            ALog.d(url.toString());
            String conResult = conn.getConnectionResult();
            if (conResult.equals("fail")) {
                msg = hr.obtainMessage(500);
                hr.sendMessage(msg);
                return;
            }
            conResult = AESCryto.decrypt(DataSiteStart.AES_KEY, conResult);
            conResult = URLDecoder.decode(conResult, App.ENCODE_UTF8);
            ALog.json(conResult);
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

        private MobileConItemActivity activity;
        private boolean isPageRequest;

        public BsHandler(Activity activity, boolean isPageRequest) {
            this.activity = (MobileConItemActivity) activity;
            this.isPageRequest = isPageRequest;
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 200:
                    if (!isPageRequest) {
                        activity.assetsAdapter = null;
                        activity.assetsAdapter = new AssetOpticalConItemAdapter(activity,
                                activity.listRes);
                        activity.lvAssets.setVisibility(View.VISIBLE);
                        activity.txtState.setVisibility(View.GONE);
                        activity.lvAssets.setAdapter(activity.assetsAdapter);
                        activity.lvAssets.setOnItemClickListener(activity.lrList);
                    } else {
                        if (activity.assetsAdapter != null)
                            activity.assetsAdapter.notifyDataSetChanged();
                    }
                    isPageRequest = true;
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
