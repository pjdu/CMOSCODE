/*
 * Created by AndyHua on 2017/10/20.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 09:27:43.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import cn.com.chinaccs.datasite.main.ui.adapters.AssetMobileCardAdapter;

/**
 * 以关联融合资产卡片信息列表
 * <p>
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
public class MobileLinkedCardsActivity extends BaseActivity {

    private Context context;
    private ListView lvAssets;
    private ProgressBar pbAssets;
    private TextView txtState;
    private List<JSONArray> listRes;
    private ProgressDialog proDialog;
    private AssetMobileCardAdapter assetsAdapter;

    private String id;
    private String title;
    private String type;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_asset_mobile_linked_cards);
        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        type = be.getString("type");
        title = be.getString("title");
        initToolbar(title);
        this.findViews();
        this.getData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
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
            try {
                String id = array.getString(0);
                String name = array.getString(1);
                Bundle be = new Bundle();
                be.putString("id", id);
                be.putString("name", name);
                Intent intent = new Intent(context, MobileLinkedCardsConActivity_.class);
                intent.putExtras(be);
                startActivity(intent);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };


    private void findViews() {
        lvAssets = (ListView) findViewById(R.id.lv_assets);
        pbAssets = (ProgressBar) findViewById(R.id.pb_assets);
        txtState = (TextView) findViewById(R.id.txt_assets_state);
    }

    private void getData() {
        pbAssets.setVisibility(View.VISIBLE);
        listRes = new ArrayList<>();
        proDialog = null;
        proDialog = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        proDialog.show();
        final Handler hr = new BsHandler(MobileLinkedCardsActivity.this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                conn(hr);
            }
        }).start();
    }

    private void conn(Handler hr) {
        Message msg = null;
        ALog.d(type);
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        try {
            String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + id + type);
            String resType = URLEncoder.encode(type, App.ENCODE_UTF8);
            url.append("AssetsCardInfo.do?psite_no=").append(id)
                    .append("&res_type=").append(resType)
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

        private MobileLinkedCardsActivity activity;

        public BsHandler(Activity activity) {
            this.activity = (MobileLinkedCardsActivity) activity;
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 200:
                    activity.assetsAdapter = null;
                    activity.assetsAdapter = new AssetMobileCardAdapter(activity,
                            activity.listRes);
                    activity.lvAssets.setVisibility(View.VISIBLE);
                    activity.txtState.setVisibility(View.GONE);
                    activity.lvAssets.setAdapter(activity.assetsAdapter);
                    activity.lvAssets.setOnItemClickListener(activity.lrList);
                    if (activity.assetsAdapter != null)
                        activity.assetsAdapter.notifyDataSetChanged();
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
            activity.pbAssets.setVisibility(View.GONE);
        }

    }
}
