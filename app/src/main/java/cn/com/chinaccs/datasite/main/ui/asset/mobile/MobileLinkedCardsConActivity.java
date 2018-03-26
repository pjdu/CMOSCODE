/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 10:48:11.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.net.URLDecoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;

/**
 * 以关联融合资产卡片信息
 * <p>
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
@EActivity(R.layout.activity_asset_mobile_linked_cards_content)
public class MobileLinkedCardsConActivity extends BaseActivity {

    private Context context;
    private String id;
    private String name;
    private ProgressDialog proDialog;
    private JSONObject jsonRes;

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


    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        name = be.getString("name");
        this.getData();
    }

    @AfterViews
    public void init() {
        initToolbar(name);
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

        private MobileLinkedCardsConActivity at;

        public BsHandler(Activity at) {
            this.at = (MobileLinkedCardsConActivity) at;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    try {
                        JSONArray array = (JSONArray) at.jsonRes.get("data");
                        array = array.getJSONArray(0);
                        at.id = array.getString(0);
                        at.oldAssetCodeTv.setText(array.getString(1));
                        at.assetCodeTv.setText(array.getString(2));
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
