/*
 * Created by AndyHua on 2017/12/4.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-12-04 17:22:26.
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;

@EFragment
public class AssetResInfoFragment extends Fragment {

    @ViewById(R.id.et_obj_id)
    TextView mObjIdEt;

    @ViewById(R.id.et_res_code)
    TextView mResCodeEt;

    @ViewById(R.id.et_old_ass)
    TextView mOldAssEt;

    @ViewById(R.id.et_ass_catlog_name)
    TextView mAssCatlogNameEt;

    @ViewById(R.id.et_prj_name)
    TextView mPrjNameEt;

    @ViewById(R.id.et_res_name)
    TextView mResNameEt;

    @ViewById(R.id.et_model)
    TextView mModelEt;

    @ViewById(R.id.et_city_name)
    TextView mCityNameEt;

    @ViewById(R.id.et_county_name)
    TextView mCountyNameEt;

    @ViewById(R.id.et_address)
    TextView mAddressEt;

    @ViewById(R.id.et_insert_time)
    TextView mInsertTimeEt;

    @ViewById(R.id.et_type_name)
    TextView mTypeNameEt;

    @ViewById(R.id.et_site_name)
    TextView mSiteNameEt;

    private View mViewContent; // 缓存视图内容
    private String resCode;

    private Context context;
    private ProgressDialog proDialog;
    private JSONObject jsonRes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getArguments();
        if (null != extras) {
            resCode = extras.getString("RES_CODE");
        }

        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.fragment_asset_res_info, container, false);
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
                            showResInfo(array);
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

    private void showResInfo(JSONArray array) throws JSONException {
        mObjIdEt.setText(array.getString(0));
        // 资源编号
        mResCodeEt.setText(array.getString(1));
        // 资产编号
        mOldAssEt.setText(array.getString(4));
        // 物理站址编号(光网络都为空)
        mAssCatlogNameEt.setText(array.getString(3));
        mPrjNameEt.setText(array.getString(6));
        mResNameEt.setText(array.getString(7));
        mModelEt.setText(array.getString(8));
        mCityNameEt.setText(array.getString(9));
        mCountyNameEt.setText(array.getString(10));
        mAddressEt.setText(array.getString(11));
        mInsertTimeEt.setText(array.getString(14));
        mTypeNameEt.setText(array.getString(25));
        mSiteNameEt.setText(array.getString(28));
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
                        showResInfo(array);
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
