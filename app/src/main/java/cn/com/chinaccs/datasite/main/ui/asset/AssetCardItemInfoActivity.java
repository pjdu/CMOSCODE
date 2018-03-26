/*
 * Created by AndyHua on 2017/11/3.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-30 11:51:04.
 */

package cn.com.chinaccs.datasite.main.ui.asset;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetUserArea;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * 资源详细信息
 */
@EActivity(R.layout.activity_asset_card_con)
public class AssetCardItemInfoActivity extends BaseActivity {

    public final static int REQUEST_ASS_CODE_SCANNING = 1;
    public final static int REQUEST_RES_CODE_SCANNING = 2;
    public final static String REQUEST_CODE_SCANNING = "REQUEST_TYPE";

    private Context context;
    private String id;
    private String assCode;
    private String oldAssetCode;
    private ProgressDialog proDialog;
    private JSONObject jsonRes;

    @ViewById(R.id.et_res_code)
    EditText resCodeEt;
    @ViewById(R.id.tv_card_old_asset_code)
    TextView oldAssetCodeTv;
    @ViewById(R.id.tv_card_asset_code)
    TextView assetCodeTv;
    @ViewById(R.id.tv_card_comp_name)
    TextView compNameTv;
    @ViewById(R.id.tv_card_city_name)
    TextView cityNameTv;
    @ViewById(R.id.tv_card_county_name)
    TextView countyNameTv;
    @ViewById(R.id.tv_card_town_name)
    TextView townNameTv;
    @ViewById(R.id.tv_card_use_dep_name)
    TextView useDepNameTv;
    @ViewById(R.id.tv_card_asset_type15)
    TextView assetTypeTv;
    @ViewById(R.id.tv_card_asset_catlog)
    TextView assetCatlogTv;
    @ViewById(R.id.tv_card_asset_catlog_name)
    TextView assetCatlogNameTv;
    @ViewById(R.id.tv_card_asset_name)
    TextView assetNameTv;
    @ViewById(R.id.tv_card_model)
    TextView modelTv;
    @ViewById(R.id.tv_card_count_a)
    TextView countATv;
    @ViewById(R.id.tv_card_count_a_unit)
    TextView countAUnitTv;
    @ViewById(R.id.tv_card_vendor)
    TextView vendorTv;
    @ViewById(R.id.tv_card_address)
    TextView addressTv;
    @ViewById(R.id.tv_card_create_time)
    TextView createTimeTv;
    @ViewById(R.id.tv_card_update_time)
    TextView updateTimeTv;
    @ViewById(R.id.tv_card_status)
    TextView statusTv;
    @ViewById(R.id.tv_card_org_asset_code)
    TextView argAssetCodeTv;
    @ViewById(R.id.tv_card_parent_asset_code)
    TextView parentAssetCodeTv;
    @ViewById(R.id.tv_card_asset_manager)
    TextView assetManagerTv;
    @ViewById(R.id.tv_card_asset_keeper)
    TextView assetKeeperTv;
    @ViewById(R.id.tv_card_person_resp)
    TextView personRespTv;
    @ViewById(R.id.tv_card_client_asset)
    TextView clientAssetTv;
    @ViewById(R.id.tv_card_prj_name)
    TextView prjNameTv;
    @ViewById(R.id.tv_card_prj_no)
    TextView prjNoTv;
    @ViewById(R.id.tv_card_pro_center_code)
    TextView proCenterCodeTv;
    @ViewById(R.id.tv_card_pro_center_gp_code)
    TextView proCenterGpCodeTv;

    @ViewById(R.id.btn_submit)
    Button submitBtn;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        Bundle be = getIntent().getExtras();
        id = be.getString("result");
        ALog.d(id);
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @AfterViews
    public void init() {
        initToolbar("资产卡片信息");
        submitBtn.setVisibility(View.GONE);

        resCodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    resCodeEt.clearFocus();
                    String resCode = resCodeEt.getText().toString().trim();
                    if (resCode.equals("")) {
                        return;
                    }
                    getResItemInfo(resCode);
                }
            }
        });
        // 初始化资产卡片信息
        this.getData();
        // 初始化资源关联资源信息
        getResCode();
    }

    private void getResCode() {
        FuncGetUserArea func = new FuncGetUserArea(context);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    initResCode(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
        String assCode = id;
        String[] assCodes = id.split("-");
        if (assCodes.length == 3) {
            assCode = assCodes[2];
        }
        // assCode = "000002101707";
        func.getResCode(lr, assCode);
    }

    private void initResCode(String output) throws Exception {
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
                resCodeEt.setText(temp.getString(9));
            }
        }
    }

    private void getResItemInfo(final String resCode) {
        proDialog = null;
        proDialog = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        proDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                resConn(resCode);
            }
        }).start();
    }

    private void resConn(String resCode) {
        StringBuilder url = new StringBuilder(DataSiteStart.HTTP_SERVER_URL);
        String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + resCode);
        try {
            resCode = URLEncoder.encode(resCode, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url.append("AssetsResInfo.do?rescode=").append(resCode).append("&sign=").append(sign);
        AppHttpConnection conn = new AppHttpConnection(context, url.toString());
        ALog.d(url.toString());
        String conResult = conn.getConnectionResult();
        if (proDialog != null && proDialog.isShowing()) {
            proDialog.dismiss();
        }
        if (conResult.equals("fail")) {
            return;
        }
        try {
            conResult = AESCryto.decrypt(DataSiteStart.AES_KEY, conResult);
            conResult = URLDecoder.decode(conResult, App.ENCODE_UTF8);
            JSONObject resJson = new JSONObject(conResult);
            ALog.d(resJson.toString());
            String result = resJson.getString("result");
            final String msg = resJson.getString("msg");
            if (!result.equals("1")) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
            JSONArray array = resJson.getJSONArray("data");
            Bundle bundle = new Bundle();
            bundle.putString("json", array.get(0).toString());
            Intent intent = new Intent(context, AssetResQrConActivity_.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_res_code)
    void btsAssetsNoClick() {
        Intent intent = new Intent(context, AssetResScannerActivity.class);
        Bundle extras = new Bundle();
        extras.putString("ASS_SCAN_CODE", id);
        extras.putString("ASSET_SN_CODE", assCode);
        extras.putString("OLD_ASSET_CODE", oldAssetCode);
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String resCode = data.getExtras().getString("RES_SCAN_CODE");
            resCodeEt.setText(resCode);
        }
    }

    @Click(R.id.btn_close)
    void closeBtnClick() {
        onBackPressed();
    }

    private void getData() {
        jsonRes = null;
        proDialog = null;
        proDialog = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        proDialog.show();
        final AssetCardItemInfoActivity.BsHandler hr = new AssetCardItemInfoActivity.BsHandler(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                conn(hr);
            }
        }).start();
    }

    private void conn(Handler hr) {
        Message msg;
        StringBuilder url = new StringBuilder(DataSiteStart.HTTP_SERVER_URL);
        String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + id);
        url.append("AssetsCardContent.do?SN_CODE=").append(id).append("&sign=").append(sign);
        AppHttpConnection conn = new AppHttpConnection(context, url.toString());
        ALog.d(url.toString());
        String conResult = conn.getConnectionResult();
        if (conResult.equals("fail")) {
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
            return;
        }
        try {
            conResult = AESCryto.decrypt(DataSiteStart.AES_KEY, conResult);
            conResult = URLDecoder.decode(conResult, App.ENCODE_UTF8);
            JSONObject resJson = new JSONObject(conResult);
            String result = resJson.getString("result");
            if (!result.equals("1")) {
                msg = hr.obtainMessage(501, resJson.getString("msg"));
                hr.sendMessage(msg);
                return;
            }
            jsonRes = resJson;
            ALog.d(jsonRes.toString());
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

        private AssetCardItemInfoActivity at;

        public BsHandler(Activity at) {
            this.at = (AssetCardItemInfoActivity) at;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    try {
                        JSONArray array = (JSONArray) at.jsonRes.get("data");
                        array = array.getJSONArray(0);
                        at.id = array.getString(0);
                        at.oldAssetCode = array.getString(1);
                        at.oldAssetCodeTv.setText(at.oldAssetCode);
                        at.assCode = array.getString(2);
                        at.assetCodeTv.setText(at.assCode);
                        at.compNameTv.setText(array.getString(4));
                        at.cityNameTv.setText(array.getString(6));
                        at.countyNameTv.setText(array.getString(8));
                        at.townNameTv.setText(array.getString(10));
                        at.useDepNameTv.setText(array.getString(11));
                        at.assetTypeTv.setText(array.getString(12));
                        at.assetCatlogTv.setText(array.getString(13));
                        at.assetCatlogNameTv.setText(array.getString(14));
                        at.assetNameTv.setText(array.getString(15));
                        at.modelTv.setText(array.getString(16));
                        at.countATv.setText(array.getString(17));
                        at.countAUnitTv.setText(array.getString(18));
                        at.vendorTv.setText(array.getString(19));
                        at.addressTv.setText(array.getString(20));
                        at.createTimeTv.setText(array.getString(22));
                        at.updateTimeTv.setText(array.getString(23));
                        at.statusTv.setText(array.getString(24));
                        at.assetManagerTv.setText(array.getString(27));
                        at.assetKeeperTv.setText(array.getString(28));
                        at.personRespTv.setText(array.getString(29));
                        at.clientAssetTv.setText(array.getString(30));
                        at.prjNameTv.setText(array.getString(31));
                        at.prjNoTv.setText(array.getString(21));
                        at.proCenterCodeTv.setText(array.getString(32));
                        at.proCenterGpCodeTv.setText(array.getString(33));
                    } catch (JSONException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    break;
                case 500:
                    Toast.makeText(at.context, "连接失败，请检查网络练级！", Toast.LENGTH_LONG)
                            .show();
                    break;
                case 501:
                    String info = (String) msg.obj;
                    Toast.makeText(at.context, "提示：" + info, Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    Toast.makeText(at.context, "未知错误！", Toast.LENGTH_LONG).show();
                    break;
            }
            if (at.proDialog != null && at.proDialog.isShowing()) {
                at.proDialog.dismiss();
                at.proDialog = null;
            }
        }
    }
}
