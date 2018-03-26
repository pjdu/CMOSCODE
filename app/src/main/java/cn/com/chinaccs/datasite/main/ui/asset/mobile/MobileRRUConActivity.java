/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 10:51:47.
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
import android.widget.Button;
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
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.zbar.ScannerActivity;

@EActivity(R.layout.activity_asset_mobile_rru_content)
public class MobileRRUConActivity extends BaseActivity {
    public final static int REQUEST_ASSETS_CODE_SCANNING = 1;
    public final static int REQUEST_SERIAL_CODE_SCANNING = 2;
    public final static String REQUEST_CODE_SCANNING = "REQUEST_TYPE";

    private Context context = null;
    private JSONArray array;
    private String datas;
    private String id;
    private String title = "新建RRU设备信息";
    private GpsHandler gpsHandler;

    // 卡片管理信息
    private String assetSnCode;         // 资产编号
    private String resCode = "";        // 资源编号
    private String resType = "RRU";     // 资源类别
    private String resScanSn;           // 设备序列号扫描结果及电子序列号
    private String assScanCode;         // 资产标签扫描结果及资产卡片编号
    private String phone;               // 盘点用户手机号
    private String createTime = "";     // 生成时间
    private String updateTime = "";     // 修改时间
    private String psiteNo;             // 物理站址编号

    @ViewById(R.id.et_rru_rru_name)
    EditText mRruNameEt;

    @ViewById(R.id.et_rru_assets_no)
    EditText mAssetsNoEt;
    @ViewById(R.id.btn_rru_assets_no)
    Button mAssetsNoBtn;

    @ViewById(R.id.et_rru_serial_id)
    EditText mSerialIdEt;
    @ViewById(R.id.btn_rru_serial_id)
    Button mSerialIdBtn;


    @ViewById(R.id.et_rru_rru_address)
    EditText mRruAddressEt;

    @ViewById(R.id.et_rru_lon)
    EditText mLonEt;
    @ViewById(R.id.btn_rru_lon)
    Button mLonBtn;

    @ViewById(R.id.et_rru_lat)
    EditText mLatEt;
    @ViewById(R.id.btn_rru_lat)
    Button mLatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        gpsHandler = ((MainApp) getApplication()).gpsHandler;

        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        datas = be.getString("json");
        if (null != datas) {
            try {
                array = new JSONArray(datas);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ALog.d(array);
        title = "RRU信息";
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
        // 物理站址编号
        psiteNo = id;
        // 资源编号
        resCode = array.getString(2);

        mRruNameEt.setText(array.getString(25));
        mAssetsNoEt.setText(array.getString(27));
        mAssetsNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAssetsNoEt.clearFocus();
                    Bundle be = new Bundle();
                    be.putString("assetsNo", mAssetsNoEt.getText().toString().trim());
                    Intent intent = new Intent(context, MobileCardsActivity.class);
                    intent.putExtras(be);
                    startActivityForResult(intent, REQUEST_ASSETS_CODE_SCANNING);
                }
            }
        });
        mSerialIdEt.setText(array.getString(32));
        mRruAddressEt.setText(array.getString(15));
        mLonEt.setText(array.getString(13));
        mLatEt.setText(array.getString(14));
    }

    @Override
    protected void onPause() {
        if (gpsHandler != null)
            gpsHandler.unregisterGpsReceiver();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gpsHandler != null)
            gpsHandler.registerGpsReceiver();
    }

    @Click(R.id.btn_close)
    void closeBtnClick() {
        onBackPressed();
    }

    @Click(R.id.btn_submit)
    void submitBtnClick() {
        assScanCode = mAssetsNoEt.getText().toString().trim();
        if (assScanCode.equals("")) {
            Toast.makeText(context, "请先扫资产卡片编号", Toast.LENGTH_SHORT).show();
            return;
        }
        resScanSn = mSerialIdEt.getText().toString().trim();
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
    void rruAssetsLinkBtnCLick() {
        Bundle be = new Bundle();
        be.putString("id", id);
        be.putString("type", "RRU");
        be.putString("title", "RRU资产卡片");
        Intent intent = new Intent(context, MobileLinkedCardsActivity.class);
        intent.putExtras(be);
        startActivity(intent);
    }

    @Click(R.id.btn_rru_assets_no)
    void rruAssetsNoClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_ASSETS_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_ASSETS_CODE_SCANNING);
    }

    @Click(R.id.btn_rru_serial_id)
    void rruSerialIdClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_SERIAL_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_SERIAL_CODE_SCANNING);
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
                            mAssetsNoEt.setText(assScanCode);
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
                            String serialId = mSerialIdEt.getText().toString().trim();
                            if (serialId.equals("")) {
                                mSerialIdEt.setText(resScanSn);
                            } else {
                                if (!serialId.equals(resScanSn)) {
                                    AlertDialog ad = App.alertDialog(context, "提示！", "是否修改电子序列号？",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    mSerialIdEt.setText(resScanSn);
                                                }
                                            }, null);
                                    ad.show();
                                }
                            }
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
            /*for (int i = 1; i < array.length(); i++) {
                if (i != 17) {
                    data.put(array.get(i));
                }
            }
            data.put(25, assScanCode);
            data.put(30, resScanSn);*/
            data.put(assScanCode);
            data.put(resScanSn);
            output.put("id", array.getString(0));
            output.put("data", data);
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
                .append("EditSaveRru.do");
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
