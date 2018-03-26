package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.blankj.ALog;

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
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.functions.FuncGetPlanSiteList;

/**
 * 交付阶段
 * Created by Asky on 2016/12/23.
 */

public class PhysicalSitePayEditActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private Button btnSub;
    private Button btnPhoto;
    private JSONArray array;
    private String id;
    private String name;
    private String physicalCode;
    // 交付日期
    private Button payDateBtn;
    private EditText payDateEt;
    private String payDateStr;
    // 新建站址电信站址编码
    private EditText siteCodeEt;
    private String siteCodeStr;
    // 新建站址铁塔公司物理站址编码
    private EditText towerSiteCodeEt;
    private String towerSiteCodeStr;

    //是否验收
    private Spinner spIsOrNotCheck;
    private String zdIsOrNotCheck = "0";

    // 机房产权
    private Spinner spRoomPro;
    private String zdRoomPro = "1";

    // 机房共享信息
    private EditText roomShareInfoEt;
    private String roomShareInfoStr;

    // 塔桅产权
    private Spinner spTowerPolePro;
    private String zdTowerPolePro = "1";

    // 塔桅类型
    private Spinner spTowerPoleType;
    private String zdTowerPoleType = "1";

    // 塔桅共享信息
    private Spinner spTowerPoleShareInfo;
    private String zdTowerPoleShareInfo = "1";

    // 供电信息
    private EditText supplyPowerInfoEt;
    private String supplyPowerInfoStr;

    // 干接点/动环信息
    private Spinner spDryContactInfo;
    private String zdDryContactInfo = "1";

    // 转供电合同(协议)号
    private EditText supplyPowerContractEt;
    private String supplyPowerContractStr;

    // 土地及房屋租赁协议号
    private EditText landProtocolEt;
    private String landProtocolStr;

    // 租赁合同甲方主体名称
    private EditText tenancyContractEt;
    private String tenancyContractStr;


    private final String TAG = PhysicalSiteNewEditActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_physical_site_pay_edit);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        name = bundle.getString("name");
        physicalCode = bundle.getString("code");
        initToolbar(name);
        this.findViews();
        getPaySiteInfo(id);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        array = null;
        super.onDestroy();
    }

    /*新建站点*/
    private void submitPhysicalSitePayData() {
        ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();
        final JSONObject output = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            array.put(towerSiteCodeStr);
            array.put(zdIsOrNotCheck);
            array.put(zdRoomPro);
            array.put(roomShareInfoStr);
            array.put(zdTowerPolePro);
            array.put(zdTowerPoleType);
            array.put(zdTowerPoleShareInfo);
            array.put(supplyPowerInfoStr);
            array.put(zdDryContactInfo);
            array.put(supplyPowerContractStr);
            array.put(landProtocolStr);
            array.put(tenancyContractStr);
            SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
            output.put("userid",
                    share.getString(AppCheckLogin.SHARE_USER_ID, ""));
            output.put("id", id);
            output.put("data", array);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final PhysicalSiteNewEditActivity.DsHandler hr = new PhysicalSiteNewEditActivity.DsHandler(context, pd);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                connNewPhysical(hr, output.toString());
            }
        }).start();

    }

    // 查询交付站址信息
    private void getPaySiteInfo(final String id) {
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {

                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");

                    if (result.equals("1")) {
                        JSONArray data = json.getJSONArray("data");
                        if (data.length() >= 1){
                            JSONArray temp = null;
                            try {
                                temp = data.getJSONArray(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            towerSiteCodeEt.setText(temp.getString(2));
                            if (!temp.getString(3).equals("")){
                                spIsOrNotCheck.setSelection(Integer.valueOf(temp.getString(3)) - 1, true);
                            }
                            if (!temp.getString(4).equals("")){
                                spRoomPro.setSelection(Integer.valueOf(temp.getString(4)) - 1, true);
                            }
                            roomShareInfoEt.setText(temp.getString(5));
                            if (!temp.getString(6).equals("")){
                                spTowerPolePro.setSelection(Integer.valueOf(temp.getString(6)) - 1, true);
                            }
                            if (!temp.getString(7).equals("")){
                                spTowerPoleType.setSelection(Integer.valueOf(temp.getString(7)) - 1, true);
                            }
                            if (!temp.getString(8).equals("")){
                                spTowerPoleShareInfo.setSelection(Integer.valueOf(temp.getString(8)) - 1, true);
                            }
                            supplyPowerInfoEt.setText(temp.getString(9));
                            if (!temp.getString(10).equals("")){
                                spDryContactInfo.setSelection(Integer.valueOf(temp.getString(10)) - 1, true);
                            }
                            supplyPowerContractEt.setText(temp.getString(11));
                            landProtocolEt.setText(temp.getString(12));
                            tenancyContractEt.setText(temp.getString(13));
                        }
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        FuncGetPlanSiteList func = new FuncGetPlanSiteList(context);
        func.getPaySiteInfo(lr, id);
    }

    private void connNewPhysical(Handler hr, String output) {
        ALog.json(output);
        Message msg = null;
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("DSSavePsite.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
        ALog.json(output);
        Log.i(TAG, url);
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

    private void findViews() {
        btnSub = (Button) findViewById(R.id.btn_submit);
        btnSub.setOnClickListener(this);
        btnPhoto = (Button) findViewById(R.id.btn_img_assets);
        btnPhoto.setOnClickListener(this);
        // 交付日期
        payDateBtn = (Button) findViewById(R.id.btn_item_date);
        payDateEt = (EditText) findViewById(R.id.et_item_date);

        // 新建站址电信站址编码
        siteCodeEt = (EditText) findViewById(R.id.et_item_physical_site_code);
        siteCodeEt.setText(physicalCode);

        // 新建站址铁塔公司物理站址编码
        towerSiteCodeEt = (EditText) findViewById(R.id.et_item_tower_physical_site_code);

        //是否验收
        spIsOrNotCheck = (Spinner) findViewById(R.id.sp_is_or_not_check);
        String[] arrayIsOrNotCheck = getResources().getStringArray(R.array.sfys);
        ArrayAdapter arrayIsOrNotCheckAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayIsOrNotCheck);
        arrayIsOrNotCheckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIsOrNotCheck.setAdapter(arrayIsOrNotCheckAdapter);
        spIsOrNotCheck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdIsOrNotCheck = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spIsOrNotCheck.setSelection(1, true);

        // 机房产权
        spRoomPro = (Spinner) findViewById(R.id.sp_room_property);
        final String[] arrayRoomPro = getResources().getStringArray(R.array.jfcq);
        ArrayAdapter arrayRoomProAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayRoomPro);
        arrayRoomProAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomPro.setAdapter(arrayRoomProAdapter);
        spRoomPro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == arrayRoomPro.length) {
                    zdRoomPro = "99";
                } else {
                    zdRoomPro = String.valueOf(position + 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 机房共享信息
        roomShareInfoEt = (EditText) findViewById(R.id.et_item_room_share_info);

        // 塔桅产权
        spTowerPolePro = (Spinner) findViewById(R.id.sp_tower_pole_property);
        final String[] arrayTowerPolePro = getResources().getStringArray(R.array.twcq);
        ArrayAdapter arrayTowerPoleProAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayTowerPolePro);
        arrayTowerPoleProAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTowerPolePro.setAdapter(arrayTowerPoleProAdapter);
        spTowerPolePro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdTowerPolePro = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 塔桅类型
        spTowerPoleType = (Spinner) findViewById(R.id.sp_tower_pole_type);
        final String[] arrayTowerPoleType = getResources().getStringArray(R.array.twcq);
        ArrayAdapter arrayTowerPoleTypeAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayTowerPoleType);
        arrayTowerPoleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTowerPoleType.setAdapter(arrayTowerPoleTypeAdapter);
        spTowerPoleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdTowerPoleType = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 塔桅共享信息
        spTowerPoleShareInfo = (Spinner) findViewById(R.id.sp_tower_pole_share_info);
        final String[] arrayTowerPoleShareInfo = getResources().getStringArray(R.array.twgxxx);
        ArrayAdapter arrayTowerPoleShareInfoAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayTowerPoleShareInfo);
        arrayTowerPoleShareInfoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTowerPoleShareInfo.setAdapter(arrayTowerPoleShareInfoAdapter);
        spTowerPoleShareInfo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdTowerPoleShareInfo = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 供电信息
        supplyPowerInfoEt = (EditText) findViewById(R.id.et_item_supply_power_info);

        // 干接点/动环信息
        spDryContactInfo = (Spinner) findViewById(R.id.sp_dry_contact_info);
        final String[] arrayDryContactInfo = getResources().getStringArray(R.array.gjdordhxx);
        ArrayAdapter arrayDryContactInfoAdapter = new ArrayAdapter(context, R.layout.item_spinner, arrayDryContactInfo);
        arrayDryContactInfoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDryContactInfo.setAdapter(arrayDryContactInfoAdapter);
        spDryContactInfo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zdDryContactInfo = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 转供电合同(协议)号
        supplyPowerContractEt = (EditText) findViewById(R.id.et_item_supply_power_contract);

        // 土地及房屋租赁协议号
        landProtocolEt = (EditText) findViewById(R.id.et_item_land_protocol);

        // 租赁合同甲方主体名称
        tenancyContractEt = (EditText) findViewById(R.id.et_item_tenancy_contract);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_img_assets:
                Intent i = new Intent(context, AssetsPhotoActivity.class);
                i.putExtra("type", DataSiteStart.TYPE_ASSETS);
                i.putExtra("id", id);
                i.putExtra("name", name);
                startActivity(i);
                break;
            case R.id.btn_submit:
                checkSubmitDatas();
                break;
            default:
                break;
        }
    }

    private void checkSubmitDatas() {
        towerSiteCodeStr = towerSiteCodeEt.getText().toString().trim();
        if (towerSiteCodeStr.equals("")) {
            Toast.makeText(context, "铁塔编号,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
            return;
        }
        roomShareInfoStr = roomShareInfoEt.getText().toString().trim();
        if (roomShareInfoStr.equals("")) {
            Toast.makeText(context, "机房共享信息,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
            return;
        }
        supplyPowerInfoStr = supplyPowerInfoEt.getText().toString().trim();
        if (supplyPowerInfoStr.equals("")) {
            Toast.makeText(context, "供电信息,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
            return;
        }
        supplyPowerContractStr = supplyPowerContractEt.getText().toString().trim();
        if (supplyPowerContractStr.equals("")) {
            Toast.makeText(context, "供电信息,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
            return;
        }
        landProtocolStr = landProtocolEt.getText().toString().trim();
        if (landProtocolStr.equals("")) {
            Toast.makeText(context, "供电信息,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
            return;
        }
        tenancyContractStr = tenancyContractEt.getText().toString().trim();
        if (tenancyContractStr.equals("")) {
            Toast.makeText(context, "供电信息,现场核对、现场必填....", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog ad = App.alertDialog(context, "提示！", "确定提交数据？",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        submitPhysicalSitePayData();
                    }
                }, null);
        ad.show();
    }

    /**
     * @author Fddi
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
                    Toast.makeText(context, "数据提交成功 ！", Toast.LENGTH_LONG).show();
                    ((Activity) context).setResult(RESULT_OK);
                    ((Activity) context).finish();
                    break;
                case 500:
                    Toast.makeText(context, "连接失败：请检查网络连接！", Toast.LENGTH_LONG).show();
                    break;
                case 600:
                    Toast.makeText(context, "数据保存成功！", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
