/*
 * Created by AndyHua on 2017/12/4.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-12-04 17:22:47.
 */

package cn.com.chinaccs.datasite.main.ui.asset;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;

@EFragment
public class AssetAssInfoFragment extends Fragment {

    @ViewById(R.id.tv_sn_code)
    TextView snCodeTv;
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

    private View mViewContent; // 缓存视图内容
    private String assNo;

    private Context context;
    private ProgressDialog proDialog;
    private JSONObject jsonRes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getArguments();
        if (null != extras) {
            assNo = extras.getString("ASS_NO");
        }

        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.fragment_asset_ass_info, container, false);
        }

        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }

        return mViewContent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 显示信息
        if (null == jsonRes) {
            this.getData();
        } else {
            try {
                JSONArray data = (JSONArray) jsonRes.get("data");
                final JSONArray array = data.getJSONArray(0);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showAssInfo(array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getData() {
        jsonRes = null;
        proDialog = null;
        proDialog = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        proDialog.show();
        final BsHandler hr = new BsHandler(context);
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
        String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + assNo);
        url.append("AssetsCardContent.do?SN_CODE=").append(assNo).append("&sign=").append(sign);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewContent = null;
    }


    private void showAssInfo(JSONArray array) throws JSONException {
        snCodeTv.setText(array.getString(0));
        oldAssetCodeTv.setText(array.getString(1));
        assetCodeTv.setText(array.getString(2));
        compNameTv.setText(array.getString(4));
        cityNameTv.setText(array.getString(6));
        countyNameTv.setText(array.getString(8));
        townNameTv.setText(array.getString(10));
        useDepNameTv.setText(array.getString(11));
        assetTypeTv.setText(array.getString(12));
        assetCatlogTv.setText(array.getString(13));
        assetCatlogNameTv.setText(array.getString(14));
        assetNameTv.setText(array.getString(15));
        modelTv.setText(array.getString(16));
        countATv.setText(array.getString(17));
        countAUnitTv.setText(array.getString(18));
        vendorTv.setText(array.getString(19));
        addressTv.setText(array.getString(20));
        createTimeTv.setText(array.getString(22));
        updateTimeTv.setText(array.getString(23));
        statusTv.setText(array.getString(24));
        assetManagerTv.setText(array.getString(27));
        assetKeeperTv.setText(array.getString(28));
        personRespTv.setText(array.getString(29));
        clientAssetTv.setText(array.getString(30));
        prjNameTv.setText(array.getString(31));
        prjNoTv.setText(array.getString(21));
        proCenterCodeTv.setText(array.getString(32));
        proCenterGpCodeTv.setText(array.getString(33));
    }

    private class BsHandler extends Handler {

        private Context context;

        public BsHandler(Context context) {
            this.context = context;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    try {
                        JSONArray array = (JSONArray) jsonRes.get("data");
                        array = array.getJSONArray(0);
                        showAssInfo(array);
                    } catch (JSONException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    break;
                case 500:
                    Toast.makeText(context, "连接失败，请检查网络练级！", Toast.LENGTH_LONG)
                            .show();
                    break;
                case 501:
                    String info = (String) msg.obj;
                    Toast.makeText(context, "提示：" + info, Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    Toast.makeText(context, "未知错误！", Toast.LENGTH_LONG).show();
                    break;
            }
            if (proDialog != null && proDialog.isShowing()) {
                proDialog.dismiss();
                proDialog = null;
            }
        }
    }
}
