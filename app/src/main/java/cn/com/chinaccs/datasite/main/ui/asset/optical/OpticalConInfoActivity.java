/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-23 09:57:47.
 */

package cn.com.chinaccs.datasite.main.ui.asset.optical;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.ui.adapters.AssetOpticalConInfoAdapter;

/*
 * 资产盘点
 * 光网络类设备信息列表
 */
public class OpticalConInfoActivity extends BaseActivity {

    private Context context;
    private String id;
    private String name;
    private TextView tvName;
    private ListView assetList;
    private AssetOpticalConInfoAdapter assetAdapter;
    private ProgressDialog proDialog;
    private List<JSONArray> listRes;
    private TextView tvState;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_asset_mobile_content);
        initToolbar("设备信息");
        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        name = be.getString("name");
        this.findView();
        tvName.setText(name);
        this.getData();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            this.getData();
    }

    private void findView() {
        tvName = (TextView) findViewById(R.id.txt_name_assets);
        assetList = (ListView) findViewById(R.id.elv_assets);
        tvState = (TextView) findViewById(R.id.txt_state_assets);
    }

    private OnItemClickListener assetIcl = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            // TODO Auto-generated method stub
            JSONArray array = listRes.get(index);
            try {
                String type = array.getString(0);
                Bundle be = new Bundle();
                be.putString("id", id);
                be.putString("type", type);
                Intent intent = new Intent(context, OpticalConItemActivity.class);
                intent.putExtras(be);
                startActivityForResult(intent, 0);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private void getData() {
        tvState.setVisibility(View.VISIBLE);
        tvState.setText(getResources().getString(R.string.common_request));
        listRes = null;
        listRes = new ArrayList<>();
        proDialog = null;
        proDialog = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        proDialog.show();
        final BsHandler hr = new BsHandler(this);
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
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + id);
        url.append("AssetsStationTypeList.do?siteid=").append(id).append("&sign=").append(sign);
        AppHttpConnection conn = new AppHttpConnection(context, url.toString());
        ALog.d(url.toString());
        String conResult = conn.getConnectionResult();
        ALog.json(conResult);
        if (conResult.equals("fail")) {
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
            return;
        }
        try {
           /* conResult = AESCryto.decrypt(DataSiteStart.AES_KEY, conResult);
            conResult = URLDecoder.decode(conResult, App.ENCODE_UTF8);*/
            JSONObject resJson = new JSONObject(conResult);
            String result = resJson.getString("result");
            if (!result.equals("1")) {
                msg = hr.obtainMessage(501, resJson.getString("msg"));
                hr.sendMessage(msg);
                return;
            }
            listRes = new ArrayList<>();
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

        private OpticalConInfoActivity activity;

        public BsHandler(Activity activity) {
            this.activity = (OpticalConInfoActivity) activity;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    activity.tvState.setVisibility(View.GONE);
                    activity.assetAdapter = null;
                    activity.assetAdapter = new AssetOpticalConInfoAdapter(activity,
                            activity.listRes);
                    activity.assetList.setAdapter(activity.assetAdapter);
                    activity.assetList.setOnItemClickListener(activity.assetIcl);
                    break;
                case 500:
                    activity.tvState.setText("连接失败，请检查网络连接！");
                    Toast.makeText(activity.context, "连接失败，请检查网络练级！", Toast.LENGTH_LONG)
                            .show();
                    break;
                case 501:
                    String info = (String) msg.obj;
                    activity.tvState.setText("提示：" + info);
                    Toast.makeText(activity.context, "提示：" + info, Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    activity.tvState.setText("未知错误！");
                    Toast.makeText(activity.context, "未知错误！", Toast.LENGTH_LONG).show();
                    break;
            }
            if (activity.proDialog != null && activity.proDialog.isShowing()) {
                activity.proDialog.dismiss();
                activity.proDialog = null;
            }
        }
    }
}
