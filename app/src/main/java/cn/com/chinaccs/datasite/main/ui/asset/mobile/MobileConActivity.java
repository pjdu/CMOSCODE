/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 10:56:41.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.ui.adapters.AssetMobileConInfoAdapter;
import cn.com.chinaccs.datasite.main.db.dao.DAOAssets;
import cn.com.chinaccs.datasite.main.db.model.AssetsModel;
import cn.com.chinaccs.datasite.main.ui.cmos.AssetsModifyAllActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.AssetsPhotoActivity;

/*
 * 资产盘点
 * 资产信息
 */
public class MobileConActivity extends BaseActivity {

    private Context context;
    private String id;
    private String name;
    private TextView tvName;
    private TextView tvDate;
    private TextView tvUser;
    private ListView elv;
    private Button btnImg;
    private Button btnDatac;
    private ProgressDialog proDialog;
    private JSONObject jsonRes;
    //	private FuncDSCommon funcBs;
    private String lastUpdateTime;
    private TextView tvState;

    // List<AssetsModel> listModels=null;
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_asset_mobile_content);
        initToolbar("物理站址信息");
//		funcBs = new FuncDSCommon(context);
        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        name = be.getString("name");
        this.findView();
        tvName.setText(name);
        this.getData();
        btnImg.setOnClickListener(btnlr);
        btnDatac.setOnClickListener(btnlr);
        this.buildList();
        // new ReadCacheTask().execute(id);
    }

    protected void onDestroy() {
//		funcBs = null;
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onReusum() {
        super.onResume();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            this.getData();
    }

    private void findView() {
        tvName = (TextView) findViewById(R.id.txt_name_assets);
        tvDate = (TextView) findViewById(R.id.txt_date_assets);
        tvUser = (TextView) findViewById(R.id.txt_user_assets);
        elv = (ListView) findViewById(R.id.elv_assets);
        btnImg = (Button) findViewById(R.id.btn_img_assets);
        btnDatac = (Button) findViewById(R.id.btn_datac_assets);
        tvState = (TextView) findViewById(R.id.txt_state_assets);
    }

    private void buildList() {
        /*final String[] parent = {
                getResources().getString(R.string.assets_zd),
                getResources().getString(R.string.assets_jf),
                getResources().getString(R.string.assets_gt),
                getResources().getString(R.string.assets_wsd),
                getResources().getString(R.string.assets_pdx),
                getResources().getString(R.string.assets_kgdy),
                getResources().getString(R.string.assets_zlp),
                getResources().getString(R.string.assets_xdc),
                getResources().getString(R.string.assets_kt),
                getResources().getString(R.string.assets_yj),
                getResources().getString(R.string.assets_byq),
                getResources().getString(R.string.assets_qf),
                getResources().getString(R.string.assets_dhjk),
                getResources().getString(R.string.assets_afxt),
                getResources().getString(R.string.assets_flx),
                getResources().getString(R.string.assets_dbx),
                getResources().getString(R.string.assets_jdp),
                getResources().getString(R.string.assets_zhg),
                getResources().getString(R.string.assets_cssb),
                getResources().getString(R.string.assets_qtxx)};*/
        final String[] parent = {
                getResources().getString(R.string.assets_check_zd),
                getResources().getString(R.string.assets_check_gt),
                getResources().getString(R.string.assets_check_jf),
                //getResources().getString(R.string.assets_check_yd),
                getResources().getString(R.string.assets_check_kt),
                getResources().getString(R.string.assets_check_jr),
                getResources().getString(R.string.assets_check_zfz),
                getResources().getString(R.string.assets_check_bts),
                getResources().getString(R.string.assets_check_rru),
                getResources().getString(R.string.assets_check_ant)};
        final int[][] positons = {{1, 14}, {15, 23}, {24, 41}, {42, 55}, {56, 64}, {66, 67}, {66, 67}, {66, 67}, {66, 67}};
        AssetMobileConInfoAdapter ad = new AssetMobileConInfoAdapter(context, parent);
        elv.setAdapter(ad);

        //分类填写
        elv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub

                if (jsonRes == null) {
                    ALog.d("attributes geted failed!");
                    return;
                }
//                if (listModels == null) {
//                    Toast.makeText(AssetsContentActivity.this, "正在读取数据，请稍后重试...",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                AssetsModel assets = null;
                int[] ps = positons[position];


                String title = parent[position];
                try {
                    String res = jsonRes.getString("result");


                    if (!res.equals("1")) {
                        Toast.makeText(context, jsonRes.getString("msg"),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // assets = listModels.get(0);
                        if (DAOAssets.getInstance(MobileConActivity.this).searchAssetsId(id) != null) {
                            assets = DAOAssets.getInstance(MobileConActivity.this).searchAssetsId(id).get(0);
                        }
                        JSONArray array = jsonRes.getJSONArray("data");
                        final Bundle be = new Bundle();
                        be.putString("id", id);
                        be.putString("title", title);
                        be.putIntArray("ps", ps);
                        be.putString("name", name);

                        if (assets != null) {
                            ALog.d("----cashData----");
//							cashData(array);//返回缓存数据
                            be.putString("json", cashData(array, ps));

                            Intent i = new Intent(context, MobileItemActivity.class);
                            i.putExtras(be);
                            startActivityForResult(i, 0);
                        } else {
                            be.putString("json", array.toString());
                            /*if (position == 7) {
                                proDialog = null;
                                proDialog = App.progressDialog(context,
                                        getResources().getString(R.string.common_request));
                                proDialog.show();
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        asyncBtsData(be);
                                    }
                                }).start();
                            } else {*/
                            Intent intent = null;
                            switch (position) {
                                case 0:
                                    intent = new Intent(context, MobileBasicConActivity_.class);
                                    break;
                                case 1:
                                    intent = new Intent(context, MobileTowerConActivity_.class);
                                    break;
                                case 2:
                                    intent = new Intent(context, MobileEngineConActivity_.class);
                                    break;
                                case 3:
                                    intent = new Intent(context, MobilePowerActivity_.class);
                                    break;
                                case 4:
                                    intent = new Intent(context, MobileTransferActivity_.class);
                                    break;
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                    intent = new Intent(context, MobileConItemActivity.class);
                                    break;
                                   /* case 6:
                                        intent = new Intent(context, MobileBTSActivity.class);
                                        break;
                                    case 8:
                                        intent = new Intent(context, MobileANTActivity.class);
                                        break;*/
                                default:
                                    break;
                            }
                            if (intent != null) {
                                intent.putExtras(be);
                            }
                            startActivityForResult(intent, 0);
                            //  }
                        }

                    }
                } catch (JSONException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        });

    }

    //全部填写
    private void updateData() {
//        if (listModels == null) {
//            Toast.makeText(AssetsContentActivity.this, "正在读取数据，请稍后重试...", Toast.LENGTH_SHORT).show();
//            return;
//        }
        Bundle be = new Bundle();
        be.putInt("datatype", DataSiteStart.TYPE_ASSETS);
        be.putString("id", id);
        be.putString("name", name);
        AssetsModel assets = null;

        try {
            String res = jsonRes.getString("result");
            if (!res.equals("1")) {
                Toast.makeText(context, jsonRes.getString("msg"),
                        Toast.LENGTH_SHORT).show();
            } else {
                // assets = listModels.get(0);
                if (DAOAssets.getInstance(MobileConActivity.this).searchAssetsId(id) != null) {
                    assets = DAOAssets.getInstance(MobileConActivity.this).searchAssetsId(id).get(0);
                }
                JSONArray array = jsonRes.getJSONArray("data");
                if (assets != null) {
//					cashData(array,new int[]{1,240});//返回缓存数据
                    // be.putString("array", cashData(array, new int[]{1, 240}));

                    Intent i = new Intent(context, AssetsModifyAllActivity.class);
                    i.putExtras(be);
                    startActivityForResult(i, 0);
                } else {

                    be.putString("array", array.toString());

                    Intent i = new Intent(context, AssetsModifyAllActivity.class);
                    i.putExtras(be);
                    startActivityForResult(i, 0);
                }


            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private class ReadCacheTask extends AsyncTask<String, Void, List<AssetsModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<AssetsModel> doInBackground(String... params) {
            return DAOAssets.getInstance(MobileConActivity.this).searchAssetsId(params[0]);
        }

        @Override
        protected void onPostExecute(List<AssetsModel> assetsModels) {
            super.onPostExecute(assetsModels);
            if (assetsModels != null) {
                // listModels = assetsModels;
            }

        }
    }

    public String cashData(JSONArray array, int[] ps) {
        int[] num = ps;
        String cashassets = "[";
        try {

            AssetsModel assets = DAOAssets.getInstance(MobileConActivity.this).searchAssetsId(id).get(0);

            String[] cashdb = new String[240];

            cashdb[0] = (assets.getZdWlzmc());
            cashdb[1] = (assets.getZdWlzbh());
            cashdb[2] = (assets.getZdGdzcbh());
            cashdb[3] = (assets.getZdSf());
            cashdb[4] = (assets.getZdDs());
            cashdb[5] = (assets.getZdQx());
            cashdb[6] = (assets.getZdXzj());
            cashdb[7] = (assets.getZdCj());
            cashdb[8] = (assets.getZdYxdz());
            cashdb[9] = (assets.getZdCjjd());
            cashdb[10] = (assets.getZdCjwd());
            cashdb[11] = (assets.getZdFgcj());
            cashdb[12] = (assets.getZdWhnd());
            cashdb[13] = (assets.getZdJzdj());
            cashdb[14] = (assets.getZdSblx());
            cashdb[15] = (assets.getZdZdlx());
            cashdb[16] = (assets.getZdZsbxh());
            cashdb[17] = (assets.getZdCbbusl());
            cashdb[18] = (assets.getZdLbbusl());
            cashdb[19] = (assets.getZdTxfwja());
            cashdb[20] = (assets.getZdTxfwjb());
            cashdb[21] = (assets.getZdTxfwjc());
            cashdb[22] = (assets.getZdTxlx());
            cashdb[23] = (assets.getZdJflx());
            cashdb[24] = (assets.getZdCdhtlx());
            cashdb[25] = (assets.getZdCqdw());
            cashdb[26] = (assets.getZdCqxz());
            cashdb[27] = (assets.getZdCdgx());
            cashdb[28] = (assets.getZdDxgx());
            cashdb[29] = (assets.getZdCdzlzj());
            cashdb[30] = (assets.getZdCdzlqx());
            cashdb[31] = (assets.getZdWylxr());
            cashdb[32] = (assets.getZdWylxdh());
            cashdb[33] = (assets.getZdJfjryq());
            cashdb[34] = (assets.getZdJfjrsj());
            cashdb[35] = (assets.getZdDzwhlc());
            cashdb[36] = (assets.getJfJflx());
            cashdb[37] = (assets.getJfJfjg());
            cashdb[38] = (assets.getJfJfsl());
            cashdb[39] = (assets.getJfGdzcbh());
            cashdb[40] = (assets.getJfJs());
            cashdb[41] = (assets.getJfKj());
            cashdb[42] = (assets.getJfJfjgm());
            cashdb[43] = (assets.getJfJfmj());
            cashdb[44] = (assets.getJfJfms());
            cashdb[45] = (assets.getJfSzlc());
            cashdb[46] = (assets.getJfXyjzsl());
            cashdb[47] = (assets.getJfJfsysl());
            cashdb[48] = (assets.getJfRjglkw());
            cashdb[49] = (assets.getJfDxkws());
            cashdb[50] = (assets.getJfKxczks());
            cashdb[51] = (assets.getJfKxcsks());
            cashdb[52] = (assets.getJfMhqsl());
            cashdb[53] = (assets.getJfNfszfd());
            cashdb[54] = (assets.getJfJdbtlj());
            cashdb[55] = (assets.getJfJcsfcj());
            cashdb[56] = (assets.getJfYwps());
            cashdb[57] = (assets.getJfYwls());
            cashdb[58] = (assets.getJfDbxsfwh());
            cashdb[59] = (assets.getJfFlsfwh());
            cashdb[60] = (assets.getJfMssfwh());
            cashdb[61] = (assets.getJfDbds());
            cashdb[62] = (assets.getJfFlqrl());
            cashdb[63] = (assets.getGtGdzcbh());
            cashdb[64] = (assets.getGtGtgd());
            cashdb[65] = (assets.getGtGttsgd());
            cashdb[66] = (assets.getGtGtlx());
            cashdb[67] = (assets.getGtZdptgd());
            cashdb[68] = (assets.getGtPtpjjj());
            cashdb[69] = (assets.getGtGtjc());
            cashdb[70] = (assets.getGtHbgd());
            cashdb[71] = (assets.getGtSfblzz());
            cashdb[72] = (assets.getGtGtcq());
            cashdb[73] = (assets.getGtGjdw());
            cashdb[74] = (assets.getGtGxdw());
            cashdb[75] = (assets.getGtGtgxfs());
            cashdb[76] = (assets.getGtCzd());
            cashdb[77] = (assets.getGtTgj());
            cashdb[78] = (assets.getGtTjls());
            cashdb[79] = (assets.getGtTtjd());
            cashdb[80] = (assets.getGtTj());
            cashdb[81] = (assets.getGtTpt());
            cashdb[82] = (assets.getGtPtjfz());
            cashdb[83] = (assets.getGtYwazkj());
            cashdb[84] = (assets.getGtSfwgfzc());
            cashdb[85] = (assets.getGtPtsl());
            cashdb[86] = (assets.getGtYckxkj());
            cashdb[87] = (assets.getGtYcptgd());
            cashdb[88] = (assets.getGtYcptlx());
            cashdb[89] = (assets.getGtBgsl());
            cashdb[90] = (assets.getGtYcyybgs());
            cashdb[91] = (assets.getGtYctxsl());
            cashdb[92] = (assets.getGtEckxkj());
            cashdb[93] = (assets.getGtEcptgd());
            cashdb[94] = (assets.getGtEcptlx());
            cashdb[95] = (assets.getGtEcbgsl());
            cashdb[96] = (assets.getGtEcyybgs());
            cashdb[97] = (assets.getGtEctxsl());
            cashdb[98] = (assets.getGtBgqk());
            cashdb[99] = (assets.getGtZybgs());
            cashdb[100] = (assets.getGtRybgs());
            cashdb[101] = (assets.getGtKxzbg());
            cashdb[102] = (assets.getWsdGdzcbh());
            cashdb[103] = (assets.getWsdYrfs());
            cashdb[104] = (assets.getWsdDygyfs());
            cashdb[105] = (assets.getWsdKgrl());
            cashdb[106] = (assets.getWsdGdfs());
            cashdb[107] = (assets.getPdxSbsl());
            cashdb[108] = (assets.getPdxZrl());
            cashdb[109] = (assets.getPdxKxdks());
            cashdb[110] = (assets.getPdxGdzcbh());
            cashdb[111] = (assets.getPdxXh());
            cashdb[112] = (assets.getPdxSccs());
            cashdb[113] = (assets.getPdxScdks());
            cashdb[114] = (assets.getPdxSfyljk());
            cashdb[115] = (assets.getKgdySbsl());
            cashdb[116] = (assets.getKgdyXh());
            cashdb[117] = (assets.getKgdySccs());
            cashdb[118] = (assets.getKgdyJkmkxh());
            cashdb[119] = (assets.getKgdyPtzlmks());
            cashdb[120] = (assets.getKgdyZlmkxh());
            cashdb[121] = (assets.getKgdyNfecxd());
            cashdb[122] = (assets.getKgdySycws());
            cashdb[123] = (assets.getKgdyGjdzt());
            cashdb[124] = (assets.getKgdyFlmkzs());
            cashdb[125] = (assets.getKgdyEdscdy());
            cashdb[126] = (assets.getKgdyJkxsp());
            cashdb[127] = (assets.getKgdyGdzcbh());
            cashdb[128] = (assets.getKgdyFcgzfh());
            cashdb[129] = (assets.getKgdyQysj());
            cashdb[130] = (assets.getZlpXh());
            cashdb[131] = (assets.getZlpSccs());
            cashdb[132] = (assets.getZlpZrl());
            cashdb[133] = (assets.getZlpRssys());
            cashdb[134] = (assets.getZlpWsyrss());
            cashdb[135] = (assets.getZlpGdzcbh());
            cashdb[136] = (assets.getXdcSblx());
            cashdb[137] = (assets.getXdcGdzcbh());
            cashdb[138] = (assets.getXdcXh());
            cashdb[139] = (assets.getXdcSccs());
            cashdb[140] = (assets.getXdcSbsl());
            cashdb[141] = (assets.getXdcDzedrl());
            cashdb[142] = (assets.getXdcDzdydj());
            cashdb[143] = (assets.getXdcDzxdcs());
            cashdb[144] = (assets.getXdcAzfs());
            cashdb[145] = (assets.getXdcWgywbx());
            cashdb[146] = (assets.getXdcSfly());
            cashdb[147] = (assets.getXdcLjtpywfs());
            cashdb[148] = (assets.getXdcZcbwsfbx());
            cashdb[149] = (assets.getXdcFdsj());
            cashdb[150] = (assets.getXdcKssysj());
            cashdb[151] = (assets.getKtXh());
            cashdb[152] = (assets.getKtSccs());
            cashdb[153] = (assets.getKtSbsl());
            cashdb[154] = (assets.getKtZlxgsfwh());
            cashdb[155] = (assets.getKtSwjzsfbx());
            cashdb[156] = (assets.getKtPsgsfls());
            cashdb[157] = (assets.getKtSftdzq());
            cashdb[158] = (assets.getKtGdzcbh());
            cashdb[159] = (assets.getKtZll());
            cashdb[160] = (assets.getKtSredgl());
            cashdb[161] = (assets.getKtEddy());
            cashdb[162] = (assets.getYjSblx());
            cashdb[163] = (assets.getYjXh());
            cashdb[164] = (assets.getYjSccs());
            cashdb[165] = (assets.getYjEdgl());
            cashdb[166] = (assets.getYjEdgzdy());
            cashdb[167] = (assets.getYjQdfs());
            cashdb[168] = (assets.getYjLqfs());
            cashdb[169] = (assets.getYjQddcxh());
            cashdb[170] = (assets.getYjQddczs());
            cashdb[171] = (assets.getYjQddcrl());
            cashdb[172] = (assets.getYjYxrj());
            cashdb[173] = (assets.getYjGdzcbh());
            cashdb[174] = (assets.getByqXh());
            cashdb[175] = (assets.getByqSccs());
            cashdb[176] = (assets.getByqLx());
            cashdb[177] = (assets.getByqEdgl());
            cashdb[178] = (assets.getByqSreddy());
            cashdb[179] = (assets.getByqSceddy());
            cashdb[180] = (assets.getByqGdzcbh());
            cashdb[181] = (assets.getByqYqywsfzc());
            cashdb[182] = (assets.getByqGyxlfs());
            cashdb[183] = (assets.getByqSfyjg());
            cashdb[184] = (assets.getByqZlsfkq());
            cashdb[185] = (assets.getQfSfyxt());
            cashdb[186] = (assets.getQfSccj());
            cashdb[187] = (assets.getQfGgxh());
            cashdb[188] = (assets.getQfGdzcbh());
            cashdb[189] = (assets.getQfSl());
            cashdb[190] = (assets.getQfSfzcgz());
            cashdb[191] = (assets.getDhjkSfdh());
            cashdb[192] = (assets.getDhjkDhcj());
            cashdb[193] = (assets.getDhjkGgxh());
            cashdb[194] = (assets.getDhjkGdzcbh());
            cashdb[195] = (assets.getDhjkSfzc());
            cashdb[196] = (assets.getAfxtSfy());
            cashdb[197] = (assets.getAfxtAfcj());
            cashdb[198] = (assets.getAfxtGgxh());
            cashdb[199] = (assets.getAfxtGdzcbh());
            cashdb[200] = (assets.getAfxtSfzc());
            cashdb[201] = (assets.getFlxSccj());
            cashdb[202] = (assets.getFlxGgxh());
            cashdb[203] = (assets.getFlxLx());
            cashdb[204] = (assets.getFlxGdzcbh());
            cashdb[205] = (assets.getFlxSl());
            cashdb[206] = (assets.getDbxGwdbxhh());
            cashdb[207] = (assets.getDbxGdzcbh());
            cashdb[208] = (assets.getDbxKkrl());
            cashdb[209] = (assets.getJdpSnsl());
            cashdb[210] = (assets.getJdpSwsl());
            cashdb[211] = (assets.getZhgCj());
            cashdb[212] = (assets.getZhgXh());
            cashdb[213] = (assets.getZhgGdzcbh());
            cashdb[214] = (assets.getZhgZhgsl());
            cashdb[215] = (assets.getCssbSccj());
            cashdb[216] = (assets.getCssbGgxh());
            cashdb[217] = (assets.getCssbCsfs());
            cashdb[218] = (assets.getCssbSjcsdk());
            cashdb[219] = (assets.getCssbZcgs());
            cashdb[220] = (assets.getCssbSfjl());
            cashdb[221] = (assets.getCssbJklx());
            cashdb[222] = (assets.getCssbSfodf());
            cashdb[223] = (assets.getCssbSfefs());
            cashdb[224] = (assets.getCssbZwfs());
            cashdb[225] = (assets.getQtGcjzsj());
            cashdb[226] = (assets.getQtBjbjlx());
            cashdb[227] = (assets.getQtBjbjjl());
            cashdb[228] = (assets.getQtBjxls());
            cashdb[229] = (assets.getQtBjxlzs());
            cashdb[230] = (assets.getQtBjxlx());
            cashdb[231] = (assets.getQtSfxj());
            cashdb[232] = (assets.getQtSblx1());
            cashdb[233] = (assets.getQtGdzcbh1());
            cashdb[234] = (assets.getQtGgxh1());
            cashdb[235] = (assets.getQtSccj1());
            cashdb[236] = (assets.getQtSblx2());
            cashdb[237] = (assets.getQtGdzcbh2());
            cashdb[238] = (assets.getQtGgxh2());
            cashdb[239] = (assets.getQtSccj2());

            JSONArray cash = null;

            System.out.println("********1111****************" + (num[0] - 1) + "    " + (num[1] - 1));
            for (int i = num[0] - 1; i <= num[1] - 1; i++) {
                cash = (JSONArray) array.get(i);

                cashassets += "['" + cash.getString(0) + "',"
                        + "'" + cash.getString(1) + "',"
                        + "'" + cash.getString(2) + "',"
                        + "'" + cash.getString(3) + "',"
                        + "'" + cash.getString(4) + "',"
                        + "'" + cash.getString(5) + "',"
                        + "'" + cash.getString(6) + "',"
                        + "'" + cash.getString(7) + "',"
                        + "'" + cash.getString(8) + "',"
                        + "'" + cash.getString(9) + "',"
                        + "'" + cashdb[i] + "'],";
            }
            cashassets = cashassets.substring(0, cashassets.length() - 1);
            cashassets += "]";


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return cashassets;

    }

    private OnClickListener btnlr = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent i = null;
            switch (v.getId()) {
                case R.id.btn_img_assets:
                    i = new Intent(context, AssetsPhotoActivity.class);
                    i.putExtra("type", DataSiteStart.TYPE_ASSETS);
                    i.putExtra("id", id);
                    i.putExtra("name", name);
                    startActivity(i);
                    break;
                case R.id.btn_datac_assets:
                    //分类编辑
                /*i = new Intent(context, AssetsContentModifyActivity.class);
                i.putExtra("name", name);
				i.putExtra("id", id);
				startActivity(i);*/
                    if (lastUpdateTime != null && !lastUpdateTime.equals("")) {
                        Date now = new Date();
                        SimpleDateFormat format = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm", Locale.CHINA);
                        Date updateTime = null;
                        Date nowDate = null;
                        long diff;
                        long days = 0;
                        try {
                            String nowdate = format.format(now);
                            updateTime = format.parse(lastUpdateTime);
                            nowDate = format.parse(nowdate);
                            diff = nowDate.getTime() - updateTime.getTime();
                            days = diff / (1000 * 24 * 60 * 60);
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }

                        if (days <= 14 && days >= 0) {
                            new AlertDialog.Builder(context)
                                    .setTitle("提示！")
                                    .setMessage("最近两周内已有数据更新，是否继续数据更新？")
                                    .setPositiveButton("是",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // TODO Auto-generated method
                                                    // stub
                                                    updateData();
                                                }
                                            }).setNegativeButton("否", null).show();
                        } else {
                            updateData();
                        }
                        ALog.d("时间", updateTime + "--" + nowDate);
                        ALog.d("相差天数", String.valueOf(days));
                    } else {
                        updateData();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private void getData() {
        tvState.setVisibility(View.VISIBLE);
        tvState.setText(getResources().getString(R.string.common_request));
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
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        // String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        Message msg = null;
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + id);
        url.append("AssetsContentNew.do?id=").append(id).append("&sign=").append(sign);
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

        private MobileConActivity at;

        public BsHandler(Activity at) {
            this.at = (MobileConActivity) at;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    at.tvState.setVisibility(View.GONE);
                    /*try {
                        String subState = at.jsonRes.getString("user");
                        String date = "暂无人提交数据采集";
                        String user = "暂无";
                        String updateTime = null;
                        if (!subState.equals("") && !subState.equals(null)) {
                            date = at.jsonRes.getString("date");
                            user = at.jsonRes.getString("user");
                            updateTime = at.jsonRes.getString("date");
                        }

                        Log.d(App.LOG_TAG, subState);
                        at.tvDate.setText(date);
                        at.tvUser.setText(user);
                        at.lastUpdateTime = updateTime;
                    } catch (JSONException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                        Log.d(App.LOG_TAG, e.toString());
                    }*/
                    break;
                case 500:
                    at.tvState.setText("连接失败，请检查网络连接！");
                    Toast.makeText(at.context, "连接失败，请检查网络练级！", Toast.LENGTH_LONG)
                            .show();
                    at.btnDatac.setEnabled(false);
                    break;
                case 501:
                    String info = (String) msg.obj;
                    at.tvState.setText("提示：" + info);
                    Toast.makeText(at.context, "提示：" + info, Toast.LENGTH_LONG)
                            .show();
                    at.btnDatac.setEnabled(false);
                    break;
                default:
                    at.tvState.setText("未知错误！");
                    Toast.makeText(at.context, "未知错误！", Toast.LENGTH_LONG).show();
                    at.btnDatac.setEnabled(false);
                    break;
            }
            if (at.proDialog != null && at.proDialog.isShowing()) {
                at.proDialog.dismiss();
                at.proDialog = null;
            }
        }
    }

    private void asyncBtsData(Bundle bundle) {
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        try {
            String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + id);
            url.append("AssetsBtsList.do?PSITE_NO=").append(id)
                    .append("&sign=").append(sign);
            AppHttpConnection conn = new AppHttpConnection(context,
                    url.toString());
            ALog.d(url.toString());
            String conResult = conn.getConnectionResult();
            if (null != proDialog) {
                proDialog.dismiss();
            }
            ALog.d(conResult);
            if (conResult.equals("fail")) {
                ALog.d("BTS加载失败");
            }
            JSONObject resJson = new JSONObject(conResult);
            String result = resJson.getString("result");
            if (!result.equals("1")) {
                ALog.d("没有BTS数据");
                JSONArray array = new JSONArray();
                bundle.putString("bts", array.toString());
                Intent intent = new Intent(context, MobileRRUActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
                return;
            }
            JSONArray array = resJson.getJSONArray("data");
            bundle.putString("bts", array.toString());
            Intent intent = new Intent(context, MobileRRUActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            ALog.d("BTS加载失败");
        }
    }
}
