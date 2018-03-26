/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 10:50:38.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
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
import java.net.URLEncoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.zbar.ScannerActivity;

/**
 * 资产信息
 * 空调电源设备信息-空调
 * <p>
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
@EActivity(R.layout.activity_asset_mobile_power_heater)
public class MobilePowerHeaterActivity extends BaseActivity {
    public final static int REQUEST_ASSETS_CODE_SCANNING = 1;
    public final static int REQUEST_SERIAL_CODE_SCANNING = 2;
    public final static String REQUEST_CODE_SCANNING = "REQUEST_TYPE";

    private Context context = null;
    private JSONArray array;
    private String datas;
    private String id;
    private String title = "空调";

    // 卡片管理信息
    private String assetSnCode;         // 资产编号
    private String resCode = "";        // 资源编号
    private String resType = "电源";     // 资源类别
    private String resScanSn;           // 设备序列号扫描结果及电子序列号
    private String assScanCode;         // 资产标签扫描结果及资产卡片编号
    private String phone;               // 盘点用户手机号
    private String createTime = "";     // 生成时间
    private String updateTime = "";     // 修改时间
    private String psiteNo;             // 物理站址编号

    @ViewById(R.id.et_power_assets_no)
    EditText mPowerAssetsNo;
    private String powerAssetsNo;

    @ViewById(R.id.et_power_serial_id)
    EditText mPowerSerialId;
    private String powerSerialId;

    @ViewById(R.id.et_power_device_type)
    EditText mPowerDeviceType;

    @ViewById(R.id.et_power_device_firm)
    EditText mPowerDeviceFirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        psiteNo = id;
        // 当前站址没有资源编号
        resCode = id;
        title = be.getString("title");
        datas = be.getString("json");
        if (null != datas) {
            try {
                array = new JSONArray(datas);
                array = array.getJSONArray(0);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ALog.d(array);
    }

    @AfterViews
    public void init() {
        try {
            initViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initViews() throws JSONException {
        initToolbar(title);
        mPowerDeviceType.setText(array.getString(46));
        mPowerDeviceFirm.setText(array.getString(47));
        powerAssetsNo = array.getString(49);
        mPowerAssetsNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPowerAssetsNo.clearFocus();
                    Bundle be = new Bundle();
                    be.putString("assetsNo", mPowerAssetsNo.getText().toString().trim());
                    Intent intent = new Intent(context, MobileCardsActivity.class);
                    intent.putExtras(be);
                    startActivityForResult(intent, REQUEST_ASSETS_CODE_SCANNING);
                }
            }
        });
        powerSerialId = array.getString(50);
    }

    @Click(R.id.btn_close)
    void closeBtnClick() {
        onBackPressed();
    }

    @Click(R.id.btn_submit)
    void submitBtnClick() {
        assScanCode = mPowerAssetsNo.getText().toString().trim();
        if (assScanCode.equals("")) {
            Toast.makeText(context, "请先扫资产卡片编号", Toast.LENGTH_SHORT).show();
            return;
        }
        resScanSn = mPowerSerialId.getText().toString().trim();
        if (resScanSn.equals("")) {
            Toast.makeText(context, "请先扫电子序列号", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO Auto-generated method stub
        AlertDialog ad = App.alertDialog(context, "提示！", "确定盘点资产？",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        submitQrCodeData();
                        saveData();
                    }
                }, null);
        ad.show();
    }

    @Click(R.id.btn_assets_link)
    void powerAssetsLinkBtnCLick() {
        Bundle be = new Bundle();
        be.putString("id", id);
        be.putString("type", "电源");
        be.putString("title", "空调电源设备资产卡片");
        Intent intent = new Intent(context, MobileLinkedCardsActivity.class);
        intent.putExtras(be);
        startActivity(intent);
    }

    @Click(R.id.btn_power_assets_no)
    void powerAssetsNoBtnClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_ASSETS_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_ASSETS_CODE_SCANNING);
    }

    @Click(R.id.btn_power_serial_id)
    void powerSerialIdBtnClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_SERIAL_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_SERIAL_CODE_SCANNING);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ASSETS_CODE_SCANNING:
                if (data != null && resultCode == RESULT_OK) {
                    final Bundle bundle = data.getExtras();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            assetSnCode = bundle.getString("ASSET_SN_CODE");
                            ALog.i(assetSnCode);
                            assScanCode = bundle.getString("ASS_SCAN_CODE");
                            ALog.i(assScanCode);
                            mPowerAssetsNo.setText(assScanCode);
                        }
                    });
                }
                break;
            case REQUEST_SERIAL_CODE_SCANNING:
                if (data != null && resultCode == RESULT_OK) {
                    final Bundle bundle = data.getExtras();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            resScanSn = bundle.getString("result");
                            ALog.i(resScanSn);
                            mPowerSerialId.setText(resScanSn);
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提交管理卡片信息
     */
    private void submitQrCodeData() {
        ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        phone = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final JSONObject output = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            array.put(assetSnCode);
            array.put(resCode);
            array.put(resType);
            array.put(resScanSn);
            array.put(assScanCode);
            array.put(phone);
            array.put(createTime);
            array.put(updateTime);
            array.put(psiteNo);
            output.put("data", array);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final DsHandler hr = new DsHandler(context, pd);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                asyncQrData(hr, output.toString());
            }
        }).start();

    }


    private void asyncQrData(Handler hr, String output) {
        ALog.json(output);
        Message msg = null;
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("DSSaveAssetsCard.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
        try {
            output = URLEncoder.encode(output, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String res = client.getResultByPOST(url, output);
        ALog.json(res);
        if (res.equals(AppHttpClient.RESULT_FAIL)) {
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
            return;
        }
        try {
            JSONObject resJson = new JSONObject(res);
            String result = resJson.getString("result");
            if (!result.equals("1")) {
                msg = hr.obtainMessage(501, resJson.getString("msg"));
                hr.sendMessage(msg);
                return;
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

    /**
     * @author AndyHua
     */
    static class DsHandler extends Handler {
        private Context context;
        private ProgressDialog pd;

        public DsHandler(Context context, ProgressDialog pd) {
            this.context = context;
            this.pd = pd;
        }

        public DsHandler(View.OnClickListener onClickListener) {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 200:
                    Toast.makeText(context, "资产盘点成功!", Toast.LENGTH_LONG).show();
                    ((Activity) context).finish();
                    break;
                case 500:
                    Toast.makeText(context, "连接失败：请检查网络连接!", Toast.LENGTH_LONG).show();
                    break;
                case 600:
                    Toast.makeText(context, "资产盘点成功!", Toast.LENGTH_LONG).show();
                    break;
                case 501:
                    String info = (String) msg.obj;
                    Toast.makeText(context, "提示：" + info, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(context, "未知错误", Toast.LENGTH_LONG).show();
                    break;
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    /**
     * 提交数据
     */
    private void saveData() {
        final JSONObject output = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            data.put(mPowerDeviceType.getText().toString().trim());
            data.put(mPowerDeviceFirm.getText().toString().trim());
            data.put(array.getString(48));
            if (!powerAssetsNo.equals("")) {
                powerAssetsNo += "," + mPowerAssetsNo.getText().toString().trim();
            } else {
                powerAssetsNo = mPowerAssetsNo.getText().toString().trim();
            }
            data.put(powerAssetsNo);
            if (!powerSerialId.equals("")) {
                powerSerialId += "," + mPowerSerialId.getText().toString().trim();
            } else {
                powerSerialId = mPowerSerialId.getText().toString().trim();
            }
            data.put(powerSerialId);
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            output.put("userid",
                    share.getString(AppCheckLogin.SHARE_USER_ID, ""));
            output.put("id", id);
            output.put("data", data);
            output.put("start", 46);
            output.put("end", 50);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                conn(output.toString());
            }
        }).start();

    }

    private void conn(String output) {
        ALog.json(output);
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("EditSaveAssets.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
        try {
            output = URLEncoder.encode(output, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String res = client.getResultByPOST(url, output);
        ALog.d(res);
        if (res.equals(AppHttpClient.RESULT_FAIL)) {
            return;
        }
        try {
            JSONObject resJson = new JSONObject(res);
            String result = resJson.getString("result");
            if (!result.equals("1")) {
                return;
            }
            setResult(RESULT_OK);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
