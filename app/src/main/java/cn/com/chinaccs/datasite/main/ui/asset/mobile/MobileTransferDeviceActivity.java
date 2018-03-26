/*
 * Created by AndyHua on 2017/10/20.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 09:31:24.
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
 * 传输设备信息
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
@EActivity(R.layout.activity_asset_mobile_transfer_device)
public class MobileTransferDeviceActivity extends BaseActivity {
    public final static int REQUEST_ASSETS_CODE_SCANNING = 1;
    public final static int REQUEST_SERIAL_CODE_SCANNING = 2;
    public final static String REQUEST_CODE_SCANNING = "REQUEST_TYPE";

    private Context context = null;
    private Bundle be = null;
    private JSONArray array;
    private String datas;
    private String id;
    private String title = "传输设备";

    // 卡片管理信息
    private String assetSnCode;         // 资产编号
    private String resCode = "";        // 资源编号
    private String resType = "传输";     // 资源类别
    private String resScanSn;           // 设备序列号扫描结果及电子序列号
    private String assScanCode;         // 资产标签扫描结果及资产卡片编号
    private String phone;               // 盘点用户手机号
    private String createTime = "";     // 生成时间
    private String updateTime = "";     // 修改时间
    private String psiteNo;             // 物理站址编号

    @ViewById(R.id.et_transfer_assets_no)
    EditText mTransferAssetsNo;
    private String transferAssetsNo;

    @ViewById(R.id.et_transfer_serial_id)
    EditText mTransferSerialId;
    private String transferSerialId;

    @ViewById(R.id.et_transfer_transfer_way)
    EditText mTransferTransferWay;

    @ViewById(R.id.et_transfer_device_type)
    EditText mTransferDeviceType;

    @ViewById(R.id.et_transfer_device_firm)
    EditText mTransferDeviceFirm;

    @ViewById(R.id.et_transfer_site_link)
    EditText mTransferSiteLink;

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
        transferAssetsNo = array.getString(59);
        mTransferAssetsNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTransferAssetsNo.clearFocus();
                    Bundle be = new Bundle();
                    be.putString("assetsNo", mTransferAssetsNo.getText().toString().trim());
                    Intent intent = new Intent(context, MobileCardsActivity.class);
                    intent.putExtras(be);
                    startActivityForResult(intent, REQUEST_ASSETS_CODE_SCANNING);
                }
            }
        });
        transferSerialId = array.getString(60);
        mTransferTransferWay.setText(array.getString(61));
        mTransferDeviceType.setText(array.getString(62));
        mTransferDeviceFirm.setText(array.getString(63));
        mTransferSiteLink.setText(array.getString(64));
    }

    @Click(R.id.btn_close)
    void closeBtnClick() {
        onBackPressed();
    }

    @Click(R.id.btn_submit)
    void submitBtnClick() {
        assScanCode = mTransferAssetsNo.getText().toString().trim();
        if (assScanCode.equals("")) {
            Toast.makeText(context, "请先扫资产卡片编号", Toast.LENGTH_SHORT).show();
            return;
        }
        resScanSn = mTransferSerialId.getText().toString().trim();
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
    void transferAssetsLinkBtnCLick() {
        Bundle be = new Bundle();
        be.putString("id", id);
        be.putString("type", "传输");
        be.putString("title", "接入传输设备资产卡片");
        Intent intent = new Intent(context, MobileLinkedCardsActivity.class);
        intent.putExtras(be);
        startActivity(intent);
    }

    @Click(R.id.btn_transfer_assets_no)
    void transferAssetsNoBtnClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_ASSETS_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_ASSETS_CODE_SCANNING);
    }

    @Click(R.id.btn_transfer_serial_id)
    void transferSerialIdBtnClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_SERIAL_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_SERIAL_CODE_SCANNING);
    }

    @Click(R.id.btn_transfer_transfer_way)
    void transferWayBtnClick() {
        final String[] items = new String[]{"IPRAN", "SDH", "PDH", "微波", "租用电路", "S200", "其他资产名称"};
        DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int index) {
                // TODO Auto-generated method stub
                mTransferTransferWay.setText(items[index]);
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(context)
                .setTitle("传输方式")
                .setSingleChoiceItems(items, -1, dlr)
                .show();
    }

    @Click(R.id.btn_transfer_site_link)
    void siteLinkBtnClick() {
        final String[] items = new String[]{"成环", "单链", "租用"};
        DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int index) {
                // TODO Auto-generated method stub
                mTransferSiteLink.setText(items[index]);
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(context)
                .setTitle("站点是否成环")
                .setSingleChoiceItems(items, -1, dlr)
                .show();
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
                            mTransferAssetsNo.setText(assScanCode);
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
                            mTransferSerialId.setText(resScanSn);
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
     *
     */
    private void saveData() {
        final JSONObject output = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            data.put(array.getString(58));
            if (!transferAssetsNo.equals("")) {
                transferAssetsNo += "," + mTransferAssetsNo.getText().toString().trim();
            } else {
                transferAssetsNo = mTransferAssetsNo.getText().toString().trim();
            }
            data.put(transferAssetsNo);
            if (!transferSerialId.equals("")) {
                transferSerialId += "," + mTransferSerialId.getText().toString().trim();
            } else {
                transferSerialId = mTransferSerialId.getText().toString().trim();
            }
            data.put(transferSerialId);
            data.put(mTransferTransferWay.getText().toString().trim());
            data.put(mTransferDeviceType.getText().toString().trim());
            data.put(mTransferDeviceFirm.getText().toString().trim());
            data.put(mTransferSiteLink.getText().toString().trim());
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            output.put("userid",
                    share.getString(AppCheckLogin.SHARE_USER_ID, ""));
            output.put("id", id);
            output.put("data", data);
            output.put("start", 58);
            output.put("end", 64);
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
