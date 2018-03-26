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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.DataModify;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.datasite.function.ModifyViewFactory;
import cn.com.chinaccs.datasite.main.db.dao.DAOAssets;
import cn.com.chinaccs.datasite.main.db.model.AssetsModel;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.ui.MainApp;

public class AssetsModifyActivity extends BaseActivity {
	private Context context;
	private LinearLayout layout;
	private Button btnSub,btnSave;
	private List<EditText> listEt;
	private List<DataModify> listDm;
	private JSONArray array;
	private String id;
	private int datatype;
	public static final int REQUEST_CODE_ANGLE = 1;
	public static final int REQUEST_CODE_DOWNTILT = 2;
	private String name;
	private TextView tvTips;
	private GpsHandler gpsHandler;
	private List<Integer> listWState = new ArrayList<Integer>();
	private List<String> listWTitle = new ArrayList<String>();
	private int[] ps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_ds_assets_modify);
		initToolbar("现场数据采集");
		gpsHandler = ((MainApp) getApplication()).gpsHandler;
		Bundle be = getIntent().getExtras();
		String json = be.getString("array");
		name = be.getString("name");
		ps = be.getIntArray("ps");
		try {
			array = new JSONArray(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		id = be.getString("id");
		datatype = be.getInt("datatype");
		this.findViews();
		if (datatype == 2) {
			tvTips.setVisibility(View.VISIBLE);
		} else {
			tvTips.setVisibility(View.GONE);
		}
		this.buildForms();
		btnSub.setOnClickListener(subLr);
//		btnSave.setOnClickListener(subSa);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		listEt = null;
		array = null;
		listDm = null;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (gpsHandler != null)
			gpsHandler.unregisterGpsReceiver();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (gpsHandler != null)
			gpsHandler.registerGpsReceiver();
	}

	private OnClickListener subLr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog ad = App.alertDialog(context, "提示！", "确定提交数据？",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							saveDatas();
						}
					}, null);
			ad.show();
		}
	};
	//缓存按钮
	private OnClickListener subSa = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog ad = App.alertDialog(context, "提示！", "确定保存数据？",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							cashDatas();
						}
					}, null);
			ad.show();
		}
	};

	/**
	 *
	 */
	private void saveDatas() {
		ProgressDialog pd = App.progressDialog(context, getResources()
				.getString(R.string.common_response));
		pd.show();
		final JSONObject output = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			for (int i = 0; i < listEt.size(); i++) {
				EditText et = listEt.get(i);
				if (et == null) {
					Toast.makeText(context, "未知错误", Toast.LENGTH_LONG).show();
					if (pd != null && pd.isShowing()) {
						pd.dismiss();
					}
					return;
				}
				Log.d(App.LOG_TAG, "listWState: " + listWState.toString());
				Log.d(App.LOG_TAG, "listWTitle: " + listWTitle.toString());
				String etStr = et.getText().toString();
				Boolean sign = false; // 定义一个标记，用来标记EditText的值有没有存入array中
				for (int j = 0; j < listWState.size(); j++) {
					if (i == listWState.get(j)) {
						if (!etStr.equals("") && !etStr.equals(null)) {
							array.put(etStr);
							sign = true;
							break;
						} else {
							Toast.makeText(context, listWTitle.get(j) + "不能为空",
									Toast.LENGTH_LONG).show();
							if (pd != null && pd.isShowing()) {
								pd.dismiss();
							}
							return;
						}
					}
				}
				if (!sign) {
					array.put(etStr);
				}

			}
			SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
			output.put("userid",
					share.getString(AppCheckLogin.SHARE_USER_ID, ""));
			output.put("id", id);
			output.put("datatype", datatype);
			output.put("data", array);
			output.put("start", ps[0]);
			output.put("end", ps[1]);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final DsHandler hr = new DsHandler(context, pd);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				conn(hr, output.toString());
			}
		}).start();

	}


	private void cashDatas(){

		ProgressDialog pd = App.progressDialog(context, getResources()
				.getString(R.string.common_response));
		pd.show();
		final JSONObject output = new JSONObject();
		final JSONArray array = new JSONArray();
		try {
			for (int i = 0; i < listEt.size(); i++) {
				EditText et = listEt.get(i);
				if (et == null) {
					Toast.makeText(context, "未知错误", Toast.LENGTH_LONG).show();
					if (pd != null && pd.isShowing()) {
						pd.dismiss();
					}
					return;
				}
				Log.d(App.LOG_TAG, "listWState: " + listWState.toString());
				Log.d(App.LOG_TAG, "listWTitle: " + listWTitle.toString());
				String etStr = et.getText().toString();
				Boolean sign = false;  //定义一个标记，用来标记EditText的值有没有存入array中
				for (int j = 0; j < listWState.size(); j++) {
					if (i == listWState.get(j) ) {
						if (!etStr.equals("") && !etStr.equals(null)) {
							array.put(etStr);
							sign = true;
							break;
						}
//						else {
//							Toast.makeText(context, listWTitle.get(j) + "不能为空",
//									Toast.LENGTH_LONG).show();
//							if (pd != null && pd.isShowing()) {
//								pd.dismiss();
//							}
//							return;
//						}
					}
				}
				if (!sign) {
					array.put(etStr);
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final AssetsModel assetsInfo = new AssetsModel();
//		for(int i=0;i<array.length();i++){
//		{ 1, 36 }, { 37, 63 }, { 64, 102 }, { 103, 107 }, { 108, 115 },
//		{ 116, 130 }, { 131, 136 }, { 137, 151 }, { 152, 162 }, { 163, 174 },
//		{ 175, 185 }, { 186, 191 }, { 192, 196 }, { 197, 201 }, { 202, 206 },
//		{ 207, 209 }, { 210, 211 }, { 212, 215 }, { 216, 225 }, { 226, 240 }
		try {

			assetsInfo.setAssetsid(id);
			if(ps[0]==1&&ps[1]==36){
			assetsInfo.setZdWlzmc(array.getString(0));
			assetsInfo.setZdWlzbh(array.getString(1));
			assetsInfo.setZdGdzcbh(array.getString(2));
			assetsInfo.setZdSf(array.getString(3));
			assetsInfo.setZdDs(array.getString(4));
			assetsInfo.setZdQx(array.getString(5));
			assetsInfo.setZdXzj(array.getString(6));
			assetsInfo.setZdCj(array.getString(7));
			assetsInfo.setZdYxdz(array.getString(8));
			assetsInfo.setZdCjjd(array.getString(9));
			assetsInfo.setZdCjwd(array.getString(10));
			assetsInfo.setZdFgcj(array.getString(11));
			assetsInfo.setZdWhnd(array.getString(12));
			assetsInfo.setZdJzdj(array.getString(13));
			assetsInfo.setZdSblx(array.getString(14));
			assetsInfo.setZdZdlx(array.getString(15));
			assetsInfo.setZdZsbxh(array.getString(16));
			assetsInfo.setZdCbbusl(array.getString(17));
			assetsInfo.setZdLbbusl(array.getString(18));
			assetsInfo.setZdTxfwja(array.getString(19));
			assetsInfo.setZdTxfwjb(array.getString(20));
			assetsInfo.setZdTxfwjc(array.getString(21));
			assetsInfo.setZdTxlx(array.getString(22));
			assetsInfo.setZdJflx(array.getString(23));
			assetsInfo.setZdCdhtlx(array.getString(24));
			assetsInfo.setZdCqdw(array.getString(25));
			assetsInfo.setZdCqxz(array.getString(26));
			assetsInfo.setZdCdgx(array.getString(27));
			assetsInfo.setZdDxgx(array.getString(28));
			assetsInfo.setZdCdzlzj(array.getString(29));
			assetsInfo.setZdCdzlqx(array.getString(30));
			assetsInfo.setZdWylxr(array.getString(31));
			assetsInfo.setZdWylxdh(array.getString(32));
			assetsInfo.setZdJfjryq(array.getString(33));
			assetsInfo.setZdJfjrsj(array.getString(34));
			assetsInfo.setZdDzwhlc(array.getString(35));
			}
			else if(ps[0]==37&&ps[1]==63){
			assetsInfo.setJfJflx(array.getString(0));
			assetsInfo.setJfJfjg(array.getString(1));
			assetsInfo.setJfJfsl(array.getString(2));
			assetsInfo.setJfGdzcbh(array.getString(3));
			assetsInfo.setJfJs(array.getString(4));
			assetsInfo.setJfKj(array.getString(5));
			assetsInfo.setJfJfjgm(array.getString(6));
			assetsInfo.setJfJfmj(array.getString(7));
			assetsInfo.setJfJfms(array.getString(8));
			assetsInfo.setJfSzlc(array.getString(9));
			assetsInfo.setJfXyjzsl(array.getString(10));
			assetsInfo.setJfJfsysl(array.getString(11));
			assetsInfo.setJfRjglkw(array.getString(12));
			assetsInfo.setJfDxkws(array.getString(13));
			assetsInfo.setJfKxczks(array.getString(14));
			assetsInfo.setJfKxcsks(array.getString(15));
			assetsInfo.setJfMhqsl(array.getString(16));
			assetsInfo.setJfNfszfd(array.getString(17));
			assetsInfo.setJfJdbtlj(array.getString(18));
			assetsInfo.setJfJcsfcj(array.getString(19));
			assetsInfo.setJfYwps(array.getString(20));
			assetsInfo.setJfYwls(array.getString(21));
			assetsInfo.setJfDbxsfwh(array.getString(22));
			assetsInfo.setJfFlsfwh(array.getString(23));
			assetsInfo.setJfMssfwh(array.getString(24));
			assetsInfo.setJfDbds(array.getString(25));
			assetsInfo.setJfFlqrl(array.getString(26));
			}
			else if(ps[0]==64&&ps[1]==102){
			assetsInfo.setGtGdzcbh(array.getString(0));
			assetsInfo.setGtGtgd(array.getString(1));
			assetsInfo.setGtGttsgd(array.getString(2));
			assetsInfo.setGtGtlx(array.getString(3));
			assetsInfo.setGtZdptgd(array.getString(4));
			assetsInfo.setGtPtpjjj(array.getString(5));
			assetsInfo.setGtGtjc(array.getString(6));
			assetsInfo.setGtHbgd(array.getString(7));
			assetsInfo.setGtSfblzz(array.getString(8));
			assetsInfo.setGtGtcq(array.getString(9));
			assetsInfo.setGtGjdw(array.getString(10));
			assetsInfo.setGtGxdw(array.getString(11));
			assetsInfo.setGtGtgxfs(array.getString(12));
			assetsInfo.setGtCzd(array.getString(13));
			assetsInfo.setGtTgj(array.getString(14));
			assetsInfo.setGtTjls(array.getString(15));
			assetsInfo.setGtTtjd(array.getString(16));
			assetsInfo.setGtTj(array.getString(17));
			assetsInfo.setGtTpt(array.getString(18));
			assetsInfo.setGtPtjfz(array.getString(19));
			assetsInfo.setGtYwazkj(array.getString(20));
			assetsInfo.setGtSfwgfzc(array.getString(21));
			assetsInfo.setGtPtsl(array.getString(22));
			assetsInfo.setGtYckxkj(array.getString(23));
			assetsInfo.setGtYcptgd(array.getString(24));
			assetsInfo.setGtYcptlx(array.getString(25));
			assetsInfo.setGtBgsl(array.getString(26));
			assetsInfo.setGtYcyybgs(array.getString(27));
			assetsInfo.setGtYctxsl(array.getString(28));
			assetsInfo.setGtEckxkj(array.getString(29));
			assetsInfo.setGtEcptgd(array.getString(30));
			assetsInfo.setGtEcptlx(array.getString(31));
			assetsInfo.setGtEcbgsl(array.getString(32));
			assetsInfo.setGtEcyybgs(array.getString(33));
			assetsInfo.setGtEctxsl(array.getString(34));
			assetsInfo.setGtBgqk(array.getString(35));
			assetsInfo.setGtZybgs(array.getString(36));
			assetsInfo.setGtRybgs(array.getString(37));
			assetsInfo.setGtKxzbg(array.getString(38));
			assetsInfo.setWsdGdzcbh(array.getString(0));
			assetsInfo.setWsdYrfs(array.getString(1));
			assetsInfo.setWsdDygyfs(array.getString(2));
			assetsInfo.setWsdKgrl(array.getString(3));
			assetsInfo.setWsdGdfs(array.getString(4));
			}
			else if(ps[0]==108&&ps[1]==115){
			assetsInfo.setPdxSbsl(array.getString(0));
			assetsInfo.setPdxZrl(array.getString(1));
			assetsInfo.setPdxKxdks(array.getString(2));
			assetsInfo.setPdxGdzcbh(array.getString(3));
			assetsInfo.setPdxXh(array.getString(4));
			assetsInfo.setPdxSccs(array.getString(5));
			assetsInfo.setPdxScdks(array.getString(6));
			assetsInfo.setPdxSfyljk(array.getString(7));
			}
			else if(ps[0]==116&&ps[1]==130){
			assetsInfo.setKgdySbsl(array.getString(0));
			assetsInfo.setKgdyXh(array.getString(1));
			assetsInfo.setKgdySccs(array.getString(2));
			assetsInfo.setKgdyJkmkxh(array.getString(3));
			assetsInfo.setKgdyPtzlmks(array.getString(4));
			assetsInfo.setKgdyZlmkxh(array.getString(5));
			assetsInfo.setKgdyNfecxd(array.getString(6));
			assetsInfo.setKgdySycws(array.getString(7));
			assetsInfo.setKgdyGjdzt(array.getString(8));
			assetsInfo.setKgdyFlmkzs(array.getString(9));
			assetsInfo.setKgdyEdscdy(array.getString(10));
			assetsInfo.setKgdyJkxsp(array.getString(11));
			assetsInfo.setKgdyGdzcbh(array.getString(12));
			assetsInfo.setKgdyFcgzfh(array.getString(13));
			assetsInfo.setKgdyQysj(array.getString(14));
			}
			else if(ps[0]==131&&ps[1]==136){
			assetsInfo.setZlpXh(array.getString(0));
			assetsInfo.setZlpSccs(array.getString(1));
			assetsInfo.setZlpZrl(array.getString(2));
			assetsInfo.setZlpRssys(array.getString(3));
			assetsInfo.setZlpWsyrss(array.getString(4));
			assetsInfo.setZlpGdzcbh(array.getString(5));
			}
			else if(ps[0]==137&&ps[1]==151){
			assetsInfo.setXdcSblx(array.getString(0));
			assetsInfo.setXdcGdzcbh(array.getString(1));
			assetsInfo.setXdcXh(array.getString(2));
			assetsInfo.setXdcSccs(array.getString(3));
			assetsInfo.setXdcSbsl(array.getString(4));
			assetsInfo.setXdcDzedrl(array.getString(5));
			assetsInfo.setXdcDzdydj(array.getString(6));
			assetsInfo.setXdcDzxdcs(array.getString(7));
			assetsInfo.setXdcAzfs(array.getString(8));
			assetsInfo.setXdcWgywbx(array.getString(9));
			assetsInfo.setXdcSfly(array.getString(10));
			assetsInfo.setXdcLjtpywfs(array.getString(11));
			assetsInfo.setXdcZcbwsfbx(array.getString(12));
			assetsInfo.setXdcFdsj(array.getString(13));
			assetsInfo.setXdcKssysj(array.getString(14));
			}
			else if(ps[0]==152&&ps[1]==162){
			assetsInfo.setKtXh(array.getString(0));
			assetsInfo.setKtSccs(array.getString(1));
			assetsInfo.setKtSbsl(array.getString(2));
			assetsInfo.setKtZlxgsfwh(array.getString(3));
			assetsInfo.setKtSwjzsfbx(array.getString(4));
			assetsInfo.setKtPsgsfls(array.getString(5));
			assetsInfo.setKtSftdzq(array.getString(6));
			assetsInfo.setKtGdzcbh(array.getString(7));
			assetsInfo.setKtZll(array.getString(8));
			assetsInfo.setKtSredgl(array.getString(9));
			assetsInfo.setKtEddy(array.getString(10));
			}
			else if(ps[0]==163&&ps[1]==174){
			assetsInfo.setYjSblx(array.getString(0));
			assetsInfo.setYjXh(array.getString(1));
			assetsInfo.setYjSccs(array.getString(2));
			assetsInfo.setYjEdgl(array.getString(3));
			assetsInfo.setYjEdgzdy(array.getString(4));
			assetsInfo.setYjQdfs(array.getString(5));
			assetsInfo.setYjLqfs(array.getString(6));
			assetsInfo.setYjQddcxh(array.getString(7));
			assetsInfo.setYjQddczs(array.getString(8));
			assetsInfo.setYjQddcrl(array.getString(9));
			assetsInfo.setYjYxrj(array.getString(10));
			assetsInfo.setYjGdzcbh(array.getString(11));
			}
			else if(ps[0]==175&&ps[1]==185){
			assetsInfo.setByqXh(array.getString(0));
			assetsInfo.setByqSccs(array.getString(1));
			assetsInfo.setByqLx(array.getString(2));
			assetsInfo.setByqEdgl(array.getString(3));
			assetsInfo.setByqSreddy(array.getString(4));
			assetsInfo.setByqSceddy(array.getString(5));
			assetsInfo.setByqGdzcbh(array.getString(6));
			assetsInfo.setByqYqywsfzc(array.getString(7));
			assetsInfo.setByqGyxlfs(array.getString(8));
			assetsInfo.setByqSfyjg(array.getString(9));
			assetsInfo.setByqZlsfkq(array.getString(10));
			}
			else if(ps[0]==186&&ps[1]==191){
			assetsInfo.setQfSfyxt(array.getString(0));
			assetsInfo.setQfSccj(array.getString(1));
			assetsInfo.setQfGgxh(array.getString(2));
			assetsInfo.setQfGdzcbh(array.getString(3));
			assetsInfo.setQfSl(array.getString(4));
			assetsInfo.setQfSfzcgz(array.getString(5));
			}
			else if(ps[0]==192&&ps[1]==196){
			assetsInfo.setDhjkSfdh(array.getString(0));
			assetsInfo.setDhjkDhcj(array.getString(1));
			assetsInfo.setDhjkGgxh(array.getString(2));
			assetsInfo.setDhjkGdzcbh(array.getString(3));
			assetsInfo.setDhjkSfzc(array.getString(4));
			}
			else if(ps[0]==197&&ps[1]==201){
			assetsInfo.setAfxtSfy(array.getString(0));
			assetsInfo.setAfxtAfcj(array.getString(1));
			assetsInfo.setAfxtGgxh(array.getString(2));
			assetsInfo.setAfxtGdzcbh(array.getString(3));
			assetsInfo.setAfxtSfzc(array.getString(4));
			}
			else if(ps[0]==202&&ps[1]==206){
			assetsInfo.setFlxSccj(array.getString(0));
			assetsInfo.setFlxGgxh(array.getString(1));
			assetsInfo.setFlxLx(array.getString(2));
			assetsInfo.setFlxGdzcbh(array.getString(3));
			assetsInfo.setFlxSl(array.getString(4));
			}
			else if(ps[0]==207&&ps[1]==209){
			assetsInfo.setDbxGwdbxhh(array.getString(0));
			assetsInfo.setDbxGdzcbh(array.getString(1));
			assetsInfo.setDbxKkrl(array.getString(2));
			}
			else if(ps[0]==210&&ps[1]==211){
			assetsInfo.setJdpSnsl(array.getString(0));
			assetsInfo.setJdpSwsl(array.getString(1));
			}
			else if(ps[0]==212&&ps[1]==215){
			assetsInfo.setZhgCj(array.getString(0));
			assetsInfo.setZhgXh(array.getString(1));
			assetsInfo.setZhgGdzcbh(array.getString(2));
			assetsInfo.setZhgZhgsl(array.getString(3));
			}
			else if(ps[0]==216&&ps[1]==225){
			assetsInfo.setCssbSccj(array.getString(0));
			assetsInfo.setCssbGgxh(array.getString(1));
			assetsInfo.setCssbCsfs(array.getString(2));
			assetsInfo.setCssbSjcsdk(array.getString(3));
			assetsInfo.setCssbZcgs(array.getString(4));
			assetsInfo.setCssbSfjl(array.getString(5));
			assetsInfo.setCssbJklx(array.getString(6));
			assetsInfo.setCssbSfodf(array.getString(7));
			assetsInfo.setCssbSfefs(array.getString(8));
			assetsInfo.setCssbZwfs(array.getString(9));
			}
			else if(ps[0]==226&&ps[1]==240){
			assetsInfo.setQtGcjzsj(array.getString(0));
			assetsInfo.setQtBjbjlx(array.getString(1));
			assetsInfo.setQtBjbjjl(array.getString(2));
			assetsInfo.setQtBjxls(array.getString(3));
			assetsInfo.setQtBjxlzs(array.getString(4));
			assetsInfo.setQtBjxlx(array.getString(5));
			assetsInfo.setQtSfxj(array.getString(6));
			assetsInfo.setQtSblx1(array.getString(7));
			assetsInfo.setQtGdzcbh1(array.getString(8));
			assetsInfo.setQtGgxh1(array.getString(9));
			assetsInfo.setQtSccj1(array.getString(10));
			assetsInfo.setQtSblx2(array.getString(11));
			assetsInfo.setQtGdzcbh2(array.getString(12));
			assetsInfo.setQtGgxh2(array.getString(13));
			assetsInfo.setQtSccj2(array.getString(14));
			}
			else if(ps[0]==103&&ps[1]==107){
			assetsInfo.setWsdGdzcbh(array.getString(0));
			assetsInfo.setWsdYrfs(array.getString(1));
			assetsInfo.setWsdDygyfs(array.getString(2));
			assetsInfo.setWsdKgrl(array.getString(3));
			assetsInfo.setWsdGdfs(array.getString(4));
			}
			else if(ps[0]==108&&ps[1]==115){
			assetsInfo.setPdxSbsl(array.getString(0));
			assetsInfo.setPdxZrl(array.getString(1));
			assetsInfo.setPdxKxdks(array.getString(2));
			assetsInfo.setPdxGdzcbh(array.getString(3));
			assetsInfo.setPdxXh(array.getString(4));
			assetsInfo.setPdxSccs(array.getString(5));
			assetsInfo.setPdxScdks(array.getString(6));
			assetsInfo.setPdxSfyljk(array.getString(7));
			}
			else if(ps[0]==116&&ps[1]==130){
			assetsInfo.setKgdySbsl(array.getString(0));
			assetsInfo.setKgdyXh(array.getString(1));
			assetsInfo.setKgdySccs(array.getString(2));
			assetsInfo.setKgdyJkmkxh(array.getString(3));
			assetsInfo.setKgdyPtzlmks(array.getString(4));
			assetsInfo.setKgdyZlmkxh(array.getString(5));
			assetsInfo.setKgdyNfecxd(array.getString(6));
			assetsInfo.setKgdySycws(array.getString(7));
			assetsInfo.setKgdyGjdzt(array.getString(8));
			assetsInfo.setKgdyFlmkzs(array.getString(9));
			assetsInfo.setKgdyEdscdy(array.getString(10));
			assetsInfo.setKgdyJkxsp(array.getString(11));
			assetsInfo.setKgdyGdzcbh(array.getString(12));
			assetsInfo.setKgdyFcgzfh(array.getString(13));
			assetsInfo.setKgdyQysj(array.getString(14));
			}
			else if(ps[0]==131&&ps[1]==136){
			assetsInfo.setZlpXh(array.getString(0));
			assetsInfo.setZlpSccs(array.getString(1));
			assetsInfo.setZlpZrl(array.getString(2));
			assetsInfo.setZlpRssys(array.getString(3));
			assetsInfo.setZlpWsyrss(array.getString(4));
			assetsInfo.setZlpGdzcbh(array.getString(5));
			}
			else if(ps[0]==137&&ps[1]==151){
			assetsInfo.setXdcSblx(array.getString(0));
			assetsInfo.setXdcGdzcbh(array.getString(1));
			assetsInfo.setXdcXh(array.getString(2));
			assetsInfo.setXdcSccs(array.getString(3));
			assetsInfo.setXdcSbsl(array.getString(4));
			assetsInfo.setXdcDzedrl(array.getString(5));
			assetsInfo.setXdcDzdydj(array.getString(6));
			assetsInfo.setXdcDzxdcs(array.getString(7));
			assetsInfo.setXdcAzfs(array.getString(8));
			assetsInfo.setXdcWgywbx(array.getString(9));
			assetsInfo.setXdcSfly(array.getString(10));
			assetsInfo.setXdcLjtpywfs(array.getString(11));
			assetsInfo.setXdcZcbwsfbx(array.getString(12));
			assetsInfo.setXdcFdsj(array.getString(13));
			assetsInfo.setXdcKssysj(array.getString(14));
			}
			else if(ps[0]==152&&ps[1]==162){
			assetsInfo.setKtXh(array.getString(0));
			assetsInfo.setKtSccs(array.getString(1));
			assetsInfo.setKtSbsl(array.getString(2));
			assetsInfo.setKtZlxgsfwh(array.getString(3));
			assetsInfo.setKtSwjzsfbx(array.getString(4));
			assetsInfo.setKtPsgsfls(array.getString(5));
			assetsInfo.setKtSftdzq(array.getString(6));
			assetsInfo.setKtGdzcbh(array.getString(7));
			assetsInfo.setKtZll(array.getString(8));
			assetsInfo.setKtSredgl(array.getString(9));
			assetsInfo.setKtEddy(array.getString(10));
			}
			else if(ps[0]==163&&ps[1]==174){
			assetsInfo.setYjSblx(array.getString(0));
			assetsInfo.setYjXh(array.getString(1));
			assetsInfo.setYjSccs(array.getString(2));
			assetsInfo.setYjEdgl(array.getString(3));
			assetsInfo.setYjEdgzdy(array.getString(4));
			assetsInfo.setYjQdfs(array.getString(5));
			assetsInfo.setYjLqfs(array.getString(6));
			assetsInfo.setYjQddcxh(array.getString(7));
			assetsInfo.setYjQddczs(array.getString(8));
			assetsInfo.setYjQddcrl(array.getString(9));
			assetsInfo.setYjYxrj(array.getString(10));
			assetsInfo.setYjGdzcbh(array.getString(11));
			}
			else if(ps[0]==175&&ps[1]==185){
			assetsInfo.setByqXh(array.getString(0));
			assetsInfo.setByqSccs(array.getString(1));
			assetsInfo.setByqLx(array.getString(2));
			assetsInfo.setByqEdgl(array.getString(3));
			assetsInfo.setByqSreddy(array.getString(4));
			assetsInfo.setByqSceddy(array.getString(5));
			assetsInfo.setByqGdzcbh(array.getString(6));
			assetsInfo.setByqYqywsfzc(array.getString(7));
			assetsInfo.setByqGyxlfs(array.getString(8));
			assetsInfo.setByqSfyjg(array.getString(9));
			assetsInfo.setByqZlsfkq(array.getString(10));
			}
			else if(ps[0]==186&&ps[1]==191){
			assetsInfo.setQfSfyxt(array.getString(0));
			assetsInfo.setQfSccj(array.getString(1));
			assetsInfo.setQfGgxh(array.getString(2));
			assetsInfo.setQfGdzcbh(array.getString(3));
			assetsInfo.setQfSl(array.getString(4));
			assetsInfo.setQfSfzcgz(array.getString(5));
			}
			else if(ps[0]==192&&ps[1]==196){
			assetsInfo.setDhjkSfdh(array.getString(0));
			assetsInfo.setDhjkDhcj(array.getString(1));
			assetsInfo.setDhjkGgxh(array.getString(2));
			assetsInfo.setDhjkGdzcbh(array.getString(3));
			assetsInfo.setDhjkSfzc(array.getString(4));
			}
			else if(ps[0]==197&&ps[1]==201){
			assetsInfo.setAfxtSfy(array.getString(0));
			assetsInfo.setAfxtAfcj(array.getString(1));
			assetsInfo.setAfxtGgxh(array.getString(2));
			assetsInfo.setAfxtGdzcbh(array.getString(3));
			assetsInfo.setAfxtSfzc(array.getString(4));
			}
			else if(ps[0]==202&&ps[1]==206){
			assetsInfo.setFlxSccj(array.getString(0));
			assetsInfo.setFlxGgxh(array.getString(1));
			assetsInfo.setFlxLx(array.getString(2));
			assetsInfo.setFlxGdzcbh(array.getString(3));
			assetsInfo.setFlxSl(array.getString(4));
			}
			else if(ps[0]==207&&ps[1]==209){
			assetsInfo.setDbxGwdbxhh(array.getString(0));
			assetsInfo.setDbxGdzcbh(array.getString(1));
			assetsInfo.setDbxKkrl(array.getString(2));
			}
			else if(ps[0]==210&&ps[1]==211){
			assetsInfo.setJdpSnsl(array.getString(0));
			assetsInfo.setJdpSwsl(array.getString(1));
			}
			else if(ps[0]==212&&ps[1]==215){
			assetsInfo.setZhgCj(array.getString(0));
			assetsInfo.setZhgXh(array.getString(1));
			assetsInfo.setZhgGdzcbh(array.getString(2));
			assetsInfo.setZhgZhgsl(array.getString(3));
			}
			else if(ps[0]==216&&ps[1]==225){
			assetsInfo.setCssbSccj(array.getString(0));
			assetsInfo.setCssbGgxh(array.getString(1));
			assetsInfo.setCssbCsfs(array.getString(2));
			assetsInfo.setCssbSjcsdk(array.getString(3));
			assetsInfo.setCssbZcgs(array.getString(4));
			assetsInfo.setCssbSfjl(array.getString(5));
			assetsInfo.setCssbJklx(array.getString(6));
			assetsInfo.setCssbSfodf(array.getString(7));
			assetsInfo.setCssbSfefs(array.getString(8));
			assetsInfo.setCssbZwfs(array.getString(9));
			}
			else if(ps[0]==226&&ps[1]==240){
			assetsInfo.setQtGcjzsj(array.getString(0));
			assetsInfo.setQtBjbjlx(array.getString(1));
			assetsInfo.setQtBjbjjl(array.getString(2));
			assetsInfo.setQtBjxls(array.getString(3));
			assetsInfo.setQtBjxlzs(array.getString(4));
			assetsInfo.setQtBjxlx(array.getString(5));
			assetsInfo.setQtSfxj(array.getString(6));
			assetsInfo.setQtSblx1(array.getString(7));
			assetsInfo.setQtGdzcbh1(array.getString(8));
			assetsInfo.setQtGgxh1(array.getString(9));
			assetsInfo.setQtSccj1(array.getString(10));
			assetsInfo.setQtSblx2(array.getString(11));
			assetsInfo.setQtGdzcbh2(array.getString(12));
			assetsInfo.setQtGgxh2(array.getString(13));
			assetsInfo.setQtSccj2(array.getString(14));
			}



			final DsHandler hr = new DsHandler(context, pd);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if(DAOAssets.getInstance(AssetsModifyActivity.this).searchAssetsId(id) == null){
					   DAOAssets.getInstance(AssetsModifyActivity.this).add(assetsInfo);
					}else{
						AssetsModel assets = DAOAssets.getInstance(AssetsModifyActivity.this).searchAssetsId(id).get(0);
						try {
							if(ps[0]==1&&ps[1]==36){
								assetsInfo.setZdWlzmc(array.getString(0));
								assetsInfo.setZdWlzbh(array.getString(1));
								assetsInfo.setZdGdzcbh(array.getString(2));
								assetsInfo.setZdSf(array.getString(3));
								assetsInfo.setZdDs(array.getString(4));
								assetsInfo.setZdQx(array.getString(5));
								assetsInfo.setZdXzj(array.getString(6));
								assetsInfo.setZdCj(array.getString(7));
								assetsInfo.setZdYxdz(array.getString(8));
								assetsInfo.setZdCjjd(array.getString(9));
								assetsInfo.setZdCjwd(array.getString(10));
								assetsInfo.setZdFgcj(array.getString(11));
								assetsInfo.setZdWhnd(array.getString(12));
								assetsInfo.setZdJzdj(array.getString(13));
								assetsInfo.setZdSblx(array.getString(14));
								assetsInfo.setZdZdlx(array.getString(15));
								assetsInfo.setZdZsbxh(array.getString(16));
								assetsInfo.setZdCbbusl(array.getString(17));
								assetsInfo.setZdLbbusl(array.getString(18));
								assetsInfo.setZdTxfwja(array.getString(19));
								assetsInfo.setZdTxfwjb(array.getString(20));
								assetsInfo.setZdTxfwjc(array.getString(21));
								assetsInfo.setZdTxlx(array.getString(22));
								assetsInfo.setZdJflx(array.getString(23));
								assetsInfo.setZdCdhtlx(array.getString(24));
								assetsInfo.setZdCqdw(array.getString(25));
								assetsInfo.setZdCqxz(array.getString(26));
								assetsInfo.setZdCdgx(array.getString(27));
								assetsInfo.setZdDxgx(array.getString(28));
								assetsInfo.setZdCdzlzj(array.getString(29));
								assetsInfo.setZdCdzlqx(array.getString(30));
								assetsInfo.setZdWylxr(array.getString(31));
								assetsInfo.setZdWylxdh(array.getString(32));
								assetsInfo.setZdJfjryq(array.getString(33));
								assetsInfo.setZdJfjrsj(array.getString(34));
								assetsInfo.setZdDzwhlc(array.getString(35));

								assetsInfo.setJfJflx("");
								assetsInfo.setJfJfjg(array.getString(1));
								assetsInfo.setJfJfsl(array.getString(2));
								assetsInfo.setJfGdzcbh(array.getString(3));
								assetsInfo.setJfJs(array.getString(4));
								assetsInfo.setJfKj(array.getString(5));
								assetsInfo.setJfJfjgm(array.getString(6));
								assetsInfo.setJfJfmj(array.getString(7));
								assetsInfo.setJfJfms(array.getString(8));
								assetsInfo.setJfSzlc(array.getString(9));
								assetsInfo.setJfXyjzsl(array.getString(10));
								assetsInfo.setJfJfsysl(array.getString(11));
								assetsInfo.setJfRjglkw(array.getString(12));
								assetsInfo.setJfDxkws(array.getString(13));
								assetsInfo.setJfKxczks(array.getString(14));
								assetsInfo.setJfKxcsks(array.getString(15));
								assetsInfo.setJfMhqsl(array.getString(16));
								assetsInfo.setJfNfszfd(array.getString(17));
								assetsInfo.setJfJdbtlj(array.getString(18));
								assetsInfo.setJfJcsfcj(array.getString(19));
								assetsInfo.setJfYwps(array.getString(20));
								assetsInfo.setJfYwls(array.getString(21));
								assetsInfo.setJfDbxsfwh(array.getString(22));
								assetsInfo.setJfFlsfwh(array.getString(23));
								assetsInfo.setJfMssfwh(array.getString(24));
								assetsInfo.setJfDbds(array.getString(25));
								assetsInfo.setJfFlqrl(array.getString(26));
								}
								else if(ps[0]==37&&ps[1]==63){
								assetsInfo.setJfJflx(array.getString(0));
								assetsInfo.setJfJfjg(array.getString(1));
								assetsInfo.setJfJfsl(array.getString(2));
								assetsInfo.setJfGdzcbh(array.getString(3));
								assetsInfo.setJfJs(array.getString(4));
								assetsInfo.setJfKj(array.getString(5));
								assetsInfo.setJfJfjgm(array.getString(6));
								assetsInfo.setJfJfmj(array.getString(7));
								assetsInfo.setJfJfms(array.getString(8));
								assetsInfo.setJfSzlc(array.getString(9));
								assetsInfo.setJfXyjzsl(array.getString(10));
								assetsInfo.setJfJfsysl(array.getString(11));
								assetsInfo.setJfRjglkw(array.getString(12));
								assetsInfo.setJfDxkws(array.getString(13));
								assetsInfo.setJfKxczks(array.getString(14));
								assetsInfo.setJfKxcsks(array.getString(15));
								assetsInfo.setJfMhqsl(array.getString(16));
								assetsInfo.setJfNfszfd(array.getString(17));
								assetsInfo.setJfJdbtlj(array.getString(18));
								assetsInfo.setJfJcsfcj(array.getString(19));
								assetsInfo.setJfYwps(array.getString(20));
								assetsInfo.setJfYwls(array.getString(21));
								assetsInfo.setJfDbxsfwh(array.getString(22));
								assetsInfo.setJfFlsfwh(array.getString(23));
								assetsInfo.setJfMssfwh(array.getString(24));
								assetsInfo.setJfDbds(array.getString(25));
								assetsInfo.setJfFlqrl(array.getString(26));
								}
								else if(ps[0]==64&&ps[1]==102){
								assetsInfo.setGtGdzcbh(array.getString(0));
								assetsInfo.setGtGtgd(array.getString(1));
								assetsInfo.setGtGttsgd(array.getString(2));
								assetsInfo.setGtGtlx(array.getString(3));
								assetsInfo.setGtZdptgd(array.getString(4));
								assetsInfo.setGtPtpjjj(array.getString(5));
								assetsInfo.setGtGtjc(array.getString(6));
								assetsInfo.setGtHbgd(array.getString(7));
								assetsInfo.setGtSfblzz(array.getString(8));
								assetsInfo.setGtGtcq(array.getString(9));
								assetsInfo.setGtGjdw(array.getString(10));
								assetsInfo.setGtGxdw(array.getString(11));
								assetsInfo.setGtGtgxfs(array.getString(12));
								assetsInfo.setGtCzd(array.getString(13));
								assetsInfo.setGtTgj(array.getString(14));
								assetsInfo.setGtTjls(array.getString(15));
								assetsInfo.setGtTtjd(array.getString(16));
								assetsInfo.setGtTj(array.getString(17));
								assetsInfo.setGtTpt(array.getString(18));
								assetsInfo.setGtPtjfz(array.getString(19));
								assetsInfo.setGtYwazkj(array.getString(20));
								assetsInfo.setGtSfwgfzc(array.getString(21));
								assetsInfo.setGtPtsl(array.getString(22));
								assetsInfo.setGtYckxkj(array.getString(23));
								assetsInfo.setGtYcptgd(array.getString(24));
								assetsInfo.setGtYcptlx(array.getString(25));
								assetsInfo.setGtBgsl(array.getString(26));
								assetsInfo.setGtYcyybgs(array.getString(27));
								assetsInfo.setGtYctxsl(array.getString(28));
								assetsInfo.setGtEckxkj(array.getString(29));
								assetsInfo.setGtEcptgd(array.getString(30));
								assetsInfo.setGtEcptlx(array.getString(31));
								assetsInfo.setGtEcbgsl(array.getString(32));
								assetsInfo.setGtEcyybgs(array.getString(33));
								assetsInfo.setGtEctxsl(array.getString(34));
								assetsInfo.setGtBgqk(array.getString(35));
								assetsInfo.setGtZybgs(array.getString(36));
								assetsInfo.setGtRybgs(array.getString(37));
								assetsInfo.setGtKxzbg(array.getString(38));
								}
								else if(ps[0]==103&&ps[1]==107){
								assetsInfo.setWsdGdzcbh(array.getString(0));
								assetsInfo.setWsdYrfs(array.getString(1));
								assetsInfo.setWsdDygyfs(array.getString(2));
								assetsInfo.setWsdKgrl(array.getString(3));
								assetsInfo.setWsdGdfs(array.getString(4));
								}
								else if(ps[0]==108&&ps[1]==115){
								assetsInfo.setPdxSbsl(array.getString(0));
								assetsInfo.setPdxZrl(array.getString(1));
								assetsInfo.setPdxKxdks(array.getString(2));
								assetsInfo.setPdxGdzcbh(array.getString(3));
								assetsInfo.setPdxXh(array.getString(4));
								assetsInfo.setPdxSccs(array.getString(5));
								assetsInfo.setPdxScdks(array.getString(6));
								assetsInfo.setPdxSfyljk(array.getString(7));
								}
								else if(ps[0]==116&&ps[1]==130){
								assetsInfo.setKgdySbsl(array.getString(0));
								assetsInfo.setKgdyXh(array.getString(1));
								assetsInfo.setKgdySccs(array.getString(2));
								assetsInfo.setKgdyJkmkxh(array.getString(3));
								assetsInfo.setKgdyPtzlmks(array.getString(4));
								assetsInfo.setKgdyZlmkxh(array.getString(5));
								assetsInfo.setKgdyNfecxd(array.getString(6));
								assetsInfo.setKgdySycws(array.getString(7));
								assetsInfo.setKgdyGjdzt(array.getString(8));
								assetsInfo.setKgdyFlmkzs(array.getString(9));
								assetsInfo.setKgdyEdscdy(array.getString(10));
								assetsInfo.setKgdyJkxsp(array.getString(11));
								assetsInfo.setKgdyGdzcbh(array.getString(12));
								assetsInfo.setKgdyFcgzfh(array.getString(13));
								assetsInfo.setKgdyQysj(array.getString(14));
								}
								else if(ps[0]==131&&ps[1]==136){
								assetsInfo.setZlpXh(array.getString(0));
								assetsInfo.setZlpSccs(array.getString(1));
								assetsInfo.setZlpZrl(array.getString(2));
								assetsInfo.setZlpRssys(array.getString(3));
								assetsInfo.setZlpWsyrss(array.getString(4));
								assetsInfo.setZlpGdzcbh(array.getString(5));
								}
								else if(ps[0]==137&&ps[1]==151){
								assetsInfo.setXdcSblx(array.getString(0));
								assetsInfo.setXdcGdzcbh(array.getString(1));
								assetsInfo.setXdcXh(array.getString(2));
								assetsInfo.setXdcSccs(array.getString(3));
								assetsInfo.setXdcSbsl(array.getString(4));
								assetsInfo.setXdcDzedrl(array.getString(5));
								assetsInfo.setXdcDzdydj(array.getString(6));
								assetsInfo.setXdcDzxdcs(array.getString(7));
								assetsInfo.setXdcAzfs(array.getString(8));
								assetsInfo.setXdcWgywbx(array.getString(9));
								assetsInfo.setXdcSfly(array.getString(10));
								assetsInfo.setXdcLjtpywfs(array.getString(11));
								assetsInfo.setXdcZcbwsfbx(array.getString(12));
								assetsInfo.setXdcFdsj(array.getString(13));
								assetsInfo.setXdcKssysj(array.getString(14));
								}
								else if(ps[0]==152&&ps[1]==162){
								assetsInfo.setKtXh(array.getString(0));
								assetsInfo.setKtSccs(array.getString(1));
								assetsInfo.setKtSbsl(array.getString(2));
								assetsInfo.setKtZlxgsfwh(array.getString(3));
								assetsInfo.setKtSwjzsfbx(array.getString(4));
								assetsInfo.setKtPsgsfls(array.getString(5));
								assetsInfo.setKtSftdzq(array.getString(6));
								assetsInfo.setKtGdzcbh(array.getString(7));
								assetsInfo.setKtZll(array.getString(8));
								assetsInfo.setKtSredgl(array.getString(9));
								assetsInfo.setKtEddy(array.getString(10));
								}
								else if(ps[0]==163&&ps[1]==174){
								assetsInfo.setYjSblx(array.getString(0));
								assetsInfo.setYjXh(array.getString(1));
								assetsInfo.setYjSccs(array.getString(2));
								assetsInfo.setYjEdgl(array.getString(3));
								assetsInfo.setYjEdgzdy(array.getString(4));
								assetsInfo.setYjQdfs(array.getString(5));
								assetsInfo.setYjLqfs(array.getString(6));
								assetsInfo.setYjQddcxh(array.getString(7));
								assetsInfo.setYjQddczs(array.getString(8));
								assetsInfo.setYjQddcrl(array.getString(9));
								assetsInfo.setYjYxrj(array.getString(10));
								assetsInfo.setYjGdzcbh(array.getString(11));
								}
								else if(ps[0]==175&&ps[1]==185){
								assetsInfo.setByqXh(array.getString(0));
								assetsInfo.setByqSccs(array.getString(1));
								assetsInfo.setByqLx(array.getString(2));
								assetsInfo.setByqEdgl(array.getString(3));
								assetsInfo.setByqSreddy(array.getString(4));
								assetsInfo.setByqSceddy(array.getString(5));
								assetsInfo.setByqGdzcbh(array.getString(6));
								assetsInfo.setByqYqywsfzc(array.getString(7));
								assetsInfo.setByqGyxlfs(array.getString(8));
								assetsInfo.setByqSfyjg(array.getString(9));
								assetsInfo.setByqZlsfkq(array.getString(10));
								}
								else if(ps[0]==186&&ps[1]==191){
								assetsInfo.setQfSfyxt(array.getString(0));
								assetsInfo.setQfSccj(array.getString(1));
								assetsInfo.setQfGgxh(array.getString(2));
								assetsInfo.setQfGdzcbh(array.getString(3));
								assetsInfo.setQfSl(array.getString(4));
								assetsInfo.setQfSfzcgz(array.getString(5));
								}
								else if(ps[0]==192&&ps[1]==196){
								assetsInfo.setDhjkSfdh(array.getString(0));
								assetsInfo.setDhjkDhcj(array.getString(1));
								assetsInfo.setDhjkGgxh(array.getString(2));
								assetsInfo.setDhjkGdzcbh(array.getString(3));
								assetsInfo.setDhjkSfzc(array.getString(4));
								}
								else if(ps[0]==197&&ps[1]==201){
								assetsInfo.setAfxtSfy(array.getString(0));
								assetsInfo.setAfxtAfcj(array.getString(1));
								assetsInfo.setAfxtGgxh(array.getString(2));
								assetsInfo.setAfxtGdzcbh(array.getString(3));
								assetsInfo.setAfxtSfzc(array.getString(4));
								}
								else if(ps[0]==202&&ps[1]==206){
								assetsInfo.setFlxSccj(array.getString(0));
								assetsInfo.setFlxGgxh(array.getString(1));
								assetsInfo.setFlxLx(array.getString(2));
								assetsInfo.setFlxGdzcbh(array.getString(3));
								assetsInfo.setFlxSl(array.getString(4));
								}
								else if(ps[0]==207&&ps[1]==209){
								assetsInfo.setDbxGwdbxhh(array.getString(0));
								assetsInfo.setDbxGdzcbh(array.getString(1));
								assetsInfo.setDbxKkrl(array.getString(2));
								}
								else if(ps[0]==210&&ps[1]==211){
								assetsInfo.setJdpSnsl(array.getString(0));
								assetsInfo.setJdpSwsl(array.getString(1));
								}
								else if(ps[0]==212&&ps[1]==215){
								assetsInfo.setZhgCj(array.getString(0));
								assetsInfo.setZhgXh(array.getString(1));
								assetsInfo.setZhgGdzcbh(array.getString(2));
								assetsInfo.setZhgZhgsl(array.getString(3));
								}
								else if(ps[0]==216&&ps[1]==225){
								assetsInfo.setCssbSccj(array.getString(0));
								assetsInfo.setCssbGgxh(array.getString(1));
								assetsInfo.setCssbCsfs(array.getString(2));
								assetsInfo.setCssbSjcsdk(array.getString(3));
								assetsInfo.setCssbZcgs(array.getString(4));
								assetsInfo.setCssbSfjl(array.getString(5));
								assetsInfo.setCssbJklx(array.getString(6));
								assetsInfo.setCssbSfodf(array.getString(7));
								assetsInfo.setCssbSfefs(array.getString(8));
								assetsInfo.setCssbZwfs(array.getString(9));
								}
								else if(ps[0]==226&&ps[1]==240){
								assetsInfo.setQtGcjzsj(array.getString(0));
								assetsInfo.setQtBjbjlx(array.getString(1));
								assetsInfo.setQtBjbjjl(array.getString(2));
								assetsInfo.setQtBjxls(array.getString(3));
								assetsInfo.setQtBjxlzs(array.getString(4));
								assetsInfo.setQtBjxlx(array.getString(5));
								assetsInfo.setQtSfxj(array.getString(6));
								assetsInfo.setQtSblx1(array.getString(7));
								assetsInfo.setQtGdzcbh1(array.getString(8));
								assetsInfo.setQtGgxh1(array.getString(9));
								assetsInfo.setQtSccj1(array.getString(10));
								assetsInfo.setQtSblx2(array.getString(11));
								assetsInfo.setQtGdzcbh2(array.getString(12));
								assetsInfo.setQtGgxh2(array.getString(13));
								assetsInfo.setQtSccj2(array.getString(14));
								}
							DAOAssets.getInstance(AssetsModifyActivity.this).update(assets);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					Message msg = hr.obtainMessage(600);
					hr.sendMessage(msg);
				}
			}).start();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


//		}


	}

	private void buildForms() {
		listEt = new ArrayList<EditText>();
		listDm = new ArrayList<DataModify>();
		ModifyViewFactory factory = new ModifyViewFactory(context);
		try {
			if (array == null) {
				Toast.makeText(context,
						getResources().getString(R.string.common_error),
						Toast.LENGTH_LONG).show();
				btnSub.setEnabled(false);
				return;
			}
			int start = ps[0];
			int end = ps[1];
			ALog.d("array: " + array.toString());
			int wr = 0;
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
				int index = data.getInt(6);
				if (index >= start && index <= end) {
					String vtype = data.getString(1);
					String writeRule = data.getString(7);
					if (vtype.equals("3")) {
						DataModify dm = new DataModify();
						dm.setTitle(data.getString(0));
						dm.setType(data.getString(2));
						dm.setCustom(Boolean.valueOf(data.getString(3)));
						dm.setOptional(Boolean.valueOf(data.getString(4)));
						dm.setCheckbox(Boolean.valueOf(data.getString(5)));
						dm.setIndex(data.getInt(6));
						dm.setTips(data.getString(7));
						dm.setFieldName(data.getString(8));
						dm.setFieldBelong(data.getString(9));
						dm.setValue(data.getString(10));
						listDm.add(dm);
					}
					if (writeRule.contains("必填") || writeRule.contains("必选")) {
						listWState.add(wr);
						listWTitle.add(data.getString(0));
					}
					wr ++;
				}
			}
			for (int i = 0; i < listDm.size(); i++) {
				DataModify attr = listDm.get(i);
				String type = attr.getType();
				if (type.equals(DataModify.TYPE_NORMAL)) {
					if (attr.isOptional()) {
						if (attr.isCheckbox()) {
							factory.addCheckboxView(attr, layout, listEt);
						} else {
							factory.addRadioView(attr, layout, listEt);
						}
					} else {
						factory.addNormalView(attr, layout, listEt);
					}
				} else if (type.equals(DataModify.TYPE_LONGITUDE)) {
					if (listDm.size() > (i + 1)) {
						String v1 = listDm.get(i + 1).getValue();
						factory.addLocationView(gpsHandler.getLastLocation(),
								attr, layout, listEt, 1, v1, name);
					}
				} else if (type.equals(DataModify.TYPE_LATITUDE)) {
					if (0 <= (i - 1)) {
						String v1 = listDm.get(i - 1).getValue();
						factory.addLocationView(gpsHandler.getLastLocation(),
								attr, layout, listEt, 2, v1, name);
					}
				} else if (type.equals(DataModify.TYPE_AZIMUTH)) {
					factory.addAzimuthView(attr, layout, listEt, i);
				} else if (type.equals(DataModify.TYPE_DOWNTILT)) {
					factory.addDowntiltView(attr, layout, listEt, i);
				}
			}
			layout.invalidate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void conn(Handler hr, String output) {
		Message msg = null;
		StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
				.append("DSSaveAssets.do");
		String url = br.toString();
		AppHttpClient client = new AppHttpClient(context);
		try {
			output = URLEncoder.encode(output, App.ENCODE_UTF8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String res = client.getResultByPOST(url, output);
		Log.d(App.LOG_TAG, res);
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
		layout = (LinearLayout) findViewById(R.id.layout_dsc);
		btnSub = (Button) findViewById(R.id.btn_close);
//		btnSave = (Button) findViewById(R.id.btn_save);
		tvTips = (TextView) findViewById(R.id.tv_tips);
	}

	/**
	 * @author Fddi
	 *
	 */
	static class DsHandler extends Handler {
		private Context context;
		private ProgressDialog pd;

		public DsHandler(Context context, ProgressDialog pd) {
			this.context = context;
			this.pd = pd;
		}

		public DsHandler(OnClickListener onClickListener) {
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_ANGLE:
			if (data != null && resultCode == RESULT_OK) {
				Bundle be = data.getExtras();
				String angle = be.getString("angle");
				int index = be.getInt("index");
				listEt.get(index).setText(angle);
				Toast.makeText(context, "方位角测量值:" + angle, Toast.LENGTH_LONG)
						.show();
			}
			break;
		case REQUEST_CODE_DOWNTILT:
			if (data != null && resultCode == RESULT_OK) {
				Bundle be = data.getExtras();
				String angle = be.getString("angle");
				int index = be.getInt("index");
				listEt.get(index).setText(angle);
				Toast.makeText(context, "下倾角测量值:" + angle, Toast.LENGTH_LONG)
						.show();
			}
			break;
		default:
			break;
		}
	}

}
