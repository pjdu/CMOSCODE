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
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetFieldOptions;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.cmos.BoxCompassActivity;
import cn.com.chinaccs.datasite.main.zbar.ScannerActivity;

/**
 * 资产信息
 * 杆塔信息
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
@EActivity(R.layout.activity_asset_mobile_tower_content)
public class MobileTowerConActivity extends BaseActivity {
    public final static int REQUEST_ASSETS_CODE_SCANNING = 1;
    public final static int REQUEST_SERIAL_CODE_SCANNING = 2;
    public final static String REQUEST_CODE_SCANNING = "REQUEST_TYPE";

    public static final int REQUEST_CODE_ANGLE_A = 11;
    public static final int REQUEST_CODE_ANGLE_B = 12;
    public static final int REQUEST_CODE_ANGLE_C = 13;

    private Context context = null;
    private JSONArray array;
    private String datas;
    private String id;
    private String title = "杆塔信息";

    // 卡片管理信息
    private String assetSnCode;         // 资产编号
    private String resCode = "";        // 资源编号
    private String resType = "杆塔";     // 资源类别
    private String resScanSn;           // 设备序列号扫描结果及电子序列号
    private String assScanCode;         // 资产标签扫描结果及资产卡片编号
    private String phone;               // 盘点用户手机号
    private String createTime = "";     // 生成时间
    private String updateTime = "";     // 修改时间
    private String psiteNo;             // 物理站址编号


    @ViewById(R.id.et_tower_assets_no)
    EditText mTowerAssetsNo;
    private String towerAssetsNo;

    @ViewById(R.id.et_tower_serial_id)
    EditText mTowerSerialId;
    private String towerSerialId;

    @ViewById(R.id.et_tower_mian_level)
    EditText mTowerMainLevel;

    @ViewById(R.id.et_tower_share_sum)
    EditText mTowerShareSum;

    @ViewById(R.id.et_tower_buy_tower)
    EditText mTowerBuyTower;

    @ViewById(R.id.et_tower_height)
    EditText mTowerHeight;

    @ViewById(R.id.et_tower_aerial_type)
    EditText mTowerAerialType;

    @ViewById(R.id.et_tower_angle_a)
    EditText mTowerAngleA;

    @ViewById(R.id.et_tower_angle_b)
    EditText mTowerAngleB;

    @ViewById(R.id.et_tower_angle_c)
    EditText mTowerAngleC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        psiteNo = id;
        // 当前站址没有资源编号
        resCode = id;
        ALog.d(psiteNo);
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

        // mTowerAssetsNo.setText(array.getString(21));
        towerAssetsNo = array.getString(21);
        mTowerAssetsNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTowerAssetsNo.clearFocus();
                    Bundle be = new Bundle();
                    /*String assetsNo = mTowerAssetsNo.getText().toString().trim();
                    String[] assetsNos = assetsNo.split("-");
                    if (assetsNos.length >= 3) {
                        assetsNo = assetsNos[2];
                    }
                    ALog.d(assetsNo);*/
                    be.putString("assetsNo", mTowerAssetsNo.getText().toString().trim());
                    Intent intent = new Intent(context, MobileCardsActivity.class);
                    intent.putExtras(be);
                    startActivityForResult(intent, REQUEST_ASSETS_CODE_SCANNING);
                }
            }
        });
        // mTowerSerialId.setText(array.getString(21));
        towerSerialId = array.getString(22);
        mTowerMainLevel.setText(array.getString(23));
        mTowerShareSum.setText(array.getString(24));
        mTowerBuyTower.setText(array.getString(25));
        mTowerHeight.setText(array.getString(26));
        mTowerAerialType.setText(array.getString(27));
        mTowerAngleA.setText(array.getString(28));
        mTowerAngleB.setText(array.getString(29));
        mTowerAngleC.setText(array.getString(30));
    }

    @Click(R.id.btn_close)
    void closeBtnClick() {
        onBackPressed();
    }

    @Click(R.id.btn_submit)
    void submitBtnClick() {
        assScanCode = mTowerAssetsNo.getText().toString().trim();
        if (assScanCode.equals("")) {
            Toast.makeText(context, "请先扫资产卡片编号", Toast.LENGTH_SHORT).show();
            return;
        }
        resScanSn = mTowerSerialId.getText().toString().trim();
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
    void towerAssetsLinkBtnCLick() {
        Bundle be = new Bundle();
        be.putString("id", id);
        be.putString("type", "杆塔");
        be.putString("title", "杆塔资产卡片");
        Intent intent = new Intent(context, MobileLinkedCardsActivity.class);
        intent.putExtras(be);
        startActivity(intent);
    }

    @Click(R.id.btn_tower_assets_no)
    void towerAssetsNoBtnClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_ASSETS_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_ASSETS_CODE_SCANNING);
    }

    @Click(R.id.btn_tower_serial_id)
    void towerSerialIdBtnClick() {
        Bundle be = new Bundle();
        be.putInt(REQUEST_CODE_SCANNING, REQUEST_SERIAL_CODE_SCANNING);
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, ScannerActivity.REQUEST_SERIAL_CODE_SCANNING);
    }

    @Click(R.id.btn_tower_main_level)
    void mainLevelBtnClick() {
        final String[] items = new String[]{"高等级", "标准等级", "免责站址"};
        DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int index) {
                // TODO Auto-generated method stub
                mTowerMainLevel.setText(items[index]);
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(context)
                .setTitle("铁塔维护等级")
                .setSingleChoiceItems(items, -1, dlr)
                .show();
    }

    @Click(R.id.btn_tower_share_sum)
    void shareSumBtnClick() {
        final String[] items = new String[]{"1", "2", "3"};
        DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int index) {
                // TODO Auto-generated method stub
                mTowerShareSum.setText(items[index]);
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(context)
                .setTitle("铁塔共享家数")
                .setSingleChoiceItems(items, -1, dlr)
                .show();
    }

    @Click(R.id.btn_tower_buy_tower)
    void buyTowerBtnClick() {
        final String[] items = new String[]{"是", "否"};
        DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int index) {
                // TODO Auto-generated method stub
                mTowerBuyTower.setText(items[index]);
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(context)
                .setTitle("是否购买铁塔发电")
                .setSingleChoiceItems(items, -1, dlr)
                .show();
    }

    @Click(R.id.btn_tower_height)
    void towerHeightBtnClick() {
        final String[] items = new String[]{"45≤H≤50", "35≤H≤40", "35≤H<40", "30≤H<35", "H≤20", "-",
                "H<30", "25≤H<30", "40≤H<45", "20≤H<25", "H<20"};
        DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int index) {
                // TODO Auto-generated method stub
                mTowerHeight.setText(items[index]);
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(context)
                .setTitle("最高天线挂高(米)")
                .setSingleChoiceItems(items, -1, dlr)
                .show();
    }

    @Click(R.id.btn_tower_aerial_type)
    void aerialTypeBtnClick() {
        FuncGetFieldOptions fos = new FuncGetFieldOptions(context);
        OnGetDataFinishedListener oflr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    JSONArray array = new JSONArray(output);
                    final String[] items = new String[array
                            .length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray data = array.getJSONArray(i);
                        items[i] = data.getString(0);
                    }

                    DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            // TODO Auto-generated method stub
                            mTowerAerialType.setText(items[index]);
                            dialog.dismiss();
                        }
                    };
                    new AlertDialog.Builder(context)
                            .setTitle("天线类型")
                            .setSingleChoiceItems(items, -1, dlr)
                            .show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fos.getData(oflr, "ZD_TXLX", "cdma_assets");
    }

    @Click(R.id.btn_tower_angle_a)
    void AngleABtnClick() {
        Bundle be = new Bundle();
        Intent intent = new Intent(context, BoxCompassActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, REQUEST_CODE_ANGLE_A);
    }

    @Click(R.id.btn_tower_angle_b)
    void AngleBBtnClick() {
        Bundle be = new Bundle();
        Intent intent = new Intent(context, BoxCompassActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, REQUEST_CODE_ANGLE_B);
    }

    @Click(R.id.btn_tower_angle_c)
    void AngleCBtnClick() {
        Bundle be = new Bundle();
        Intent intent = new Intent(context, BoxCompassActivity.class);
        intent.putExtras(be);
        startActivityForResult(intent, REQUEST_CODE_ANGLE_C);
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
        ALog.d(requestCode);
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
                            mTowerAssetsNo.setText(assScanCode);
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
                            mTowerSerialId.setText(resScanSn);
                        }
                    });
                }
                break;
            case REQUEST_CODE_ANGLE_A:
                if (data != null && resultCode == RESULT_OK) {
                    Bundle be = data.getExtras();
                    String angle = be.getString("angle");
                    mTowerAngleA.setText(angle);
                    Toast.makeText(context, "方位角A测量值:" + angle, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CODE_ANGLE_B:
                if (data != null && resultCode == RESULT_OK) {
                    Bundle be = data.getExtras();
                    String angle = be.getString("angle");
                    mTowerAngleB.setText(angle);
                    Toast.makeText(context, "方位角B测量值:" + angle, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CODE_ANGLE_C:
                if (data != null && resultCode == RESULT_OK) {
                    Bundle be = data.getExtras();
                    String angle = be.getString("angle");
                    mTowerAngleC.setText(angle);
                    Toast.makeText(context, "方位角C测量值:" + angle, Toast.LENGTH_SHORT)
                            .show();
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
        JSONArray data = new JSONArray();
        try {
            data.put(assetSnCode);
            data.put(resCode);
            data.put(resType);
            data.put(resScanSn);
            data.put(assScanCode);
            data.put(phone);
            data.put(createTime);
            data.put(updateTime);
            data.put(psiteNo);
            output.put("data", data);
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
            data.put(array.getString(20));
            if (!towerAssetsNo.equals("")) {
                towerAssetsNo += "," + mTowerAssetsNo.getText().toString().trim();
            } else {
                towerAssetsNo = mTowerAssetsNo.getText().toString().trim();
            }
            data.put(towerAssetsNo);
            if (!towerSerialId.equals("")) {
                towerSerialId += "," + mTowerSerialId.getText().toString().trim();
            } else {
                towerSerialId = mTowerSerialId.getText().toString().trim();
            }
            data.put(towerSerialId);
            data.put(mTowerMainLevel.getText().toString().trim());
            data.put(mTowerShareSum.getText().toString().trim());
            data.put(mTowerBuyTower.getText().toString().trim());
            data.put(mTowerHeight.getText().toString().trim());
            data.put(mTowerAerialType.getText().toString().trim());
            data.put(mTowerAngleA.getText().toString().trim());
            data.put(mTowerAngleB.getText().toString().trim());
            data.put(mTowerAngleC.getText().toString().trim());
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            output.put("userid",
                    share.getString(AppCheckLogin.SHARE_USER_ID, ""));
            output.put("id", id);
            output.put("data", data);
            output.put("start", 20);
            output.put("end", 30);
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
