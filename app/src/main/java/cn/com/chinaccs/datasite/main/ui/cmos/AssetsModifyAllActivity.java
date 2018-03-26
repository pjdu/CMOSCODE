package cn.com.chinaccs.datasite.main.ui.cmos;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import cn.com.chinaccs.datasite.main.DataModify;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.db.dao.DAOAssets;
import cn.com.chinaccs.datasite.main.db.model.AssetsModel;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.datasite.function.ModifyViewFactory;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.ui.MainApp;

public class AssetsModifyAllActivity extends BaseActivity {
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
//		ps = be.getIntArray("ps");
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
		btnSave.setOnClickListener(subSa);
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
/*			output.put("start", ps[0]);
			output.put("end", ps[1]);*/
			//提交时检查更新缓存信息
			saveTocache(array);
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
	private void saveTocache(JSONArray array){
		//如果本地没有缓存数据，则不做处理，否则更新本地缓存
		if(DAOAssets.getInstance(AssetsModifyAllActivity.this).searchAssetsId(id) != null){
			AssetsModel assetsInfo = DAOAssets.getInstance(AssetsModifyAllActivity.this).searchAssetsId(id).get(0);
			try {
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
		        assetsInfo.setJfJflx(array.getString(36));
		        assetsInfo.setJfJfjg(array.getString(37));
		        assetsInfo.setJfJfsl(array.getString(38));
		        assetsInfo.setJfGdzcbh(array.getString(39));
		        assetsInfo.setJfJs(array.getString(40));
		        assetsInfo.setJfKj(array.getString(41));
		        assetsInfo.setJfJfjgm(array.getString(42));
		        assetsInfo.setJfJfmj(array.getString(43));
		        assetsInfo.setJfJfms(array.getString(44));
		        assetsInfo.setJfSzlc(array.getString(45));
		        assetsInfo.setJfXyjzsl(array.getString(46));
		        assetsInfo.setJfJfsysl(array.getString(47));
		        assetsInfo.setJfRjglkw(array.getString(48));
		        assetsInfo.setJfDxkws(array.getString(49));
		        assetsInfo.setJfKxczks(array.getString(50));
		        assetsInfo.setJfKxcsks(array.getString(51));
		        assetsInfo.setJfMhqsl(array.getString(52));
		        assetsInfo.setJfNfszfd(array.getString(53));
		        assetsInfo.setJfJdbtlj(array.getString(54));
		        assetsInfo.setJfJcsfcj(array.getString(55));
		        assetsInfo.setJfYwps(array.getString(56));
		        assetsInfo.setJfYwls(array.getString(57));
		        assetsInfo.setJfDbxsfwh(array.getString(58));
		        assetsInfo.setJfFlsfwh(array.getString(59));
		        assetsInfo.setJfMssfwh(array.getString(60));
		        assetsInfo.setJfDbds(array.getString(61));
		        assetsInfo.setJfFlqrl(array.getString(62));
		        assetsInfo.setGtGdzcbh(array.getString(63));
		        assetsInfo.setGtGtgd(array.getString(64));
		        assetsInfo.setGtGttsgd(array.getString(65));
		        assetsInfo.setGtGtlx(array.getString(66));
		        assetsInfo.setGtZdptgd(array.getString(67));
		        assetsInfo.setGtPtpjjj(array.getString(68));
		        assetsInfo.setGtGtjc(array.getString(69));
		        assetsInfo.setGtHbgd(array.getString(70));
		        assetsInfo.setGtSfblzz(array.getString(71));
		        assetsInfo.setGtGtcq(array.getString(72));
		        assetsInfo.setGtGjdw(array.getString(73));
		        assetsInfo.setGtGxdw(array.getString(74));
		        assetsInfo.setGtGtgxfs(array.getString(75));
		        assetsInfo.setGtCzd(array.getString(76));
		        assetsInfo.setGtTgj(array.getString(77));
		        assetsInfo.setGtTjls(array.getString(78));
		        assetsInfo.setGtTtjd(array.getString(79));
		        assetsInfo.setGtTj(array.getString(80));
		        assetsInfo.setGtTpt(array.getString(81));
		        assetsInfo.setGtPtjfz(array.getString(82));
		        assetsInfo.setGtYwazkj(array.getString(83));
		        assetsInfo.setGtSfwgfzc(array.getString(84));
		        assetsInfo.setGtPtsl(array.getString(85));
		        assetsInfo.setGtYckxkj(array.getString(86));
		        assetsInfo.setGtYcptgd(array.getString(87));
		        assetsInfo.setGtYcptlx(array.getString(88));
		        assetsInfo.setGtBgsl(array.getString(89));
		        assetsInfo.setGtYcyybgs(array.getString(90));
		        assetsInfo.setGtYctxsl(array.getString(91));
		        assetsInfo.setGtEckxkj(array.getString(92));
		        assetsInfo.setGtEcptgd(array.getString(93));
		        assetsInfo.setGtEcptlx(array.getString(94));
		        assetsInfo.setGtEcbgsl(array.getString(95));
		        assetsInfo.setGtEcyybgs(array.getString(96));
		        assetsInfo.setGtEctxsl(array.getString(97));
		        assetsInfo.setGtBgqk(array.getString(98));
		        assetsInfo.setGtZybgs(array.getString(99));
		        assetsInfo.setGtRybgs(array.getString(100));
		        assetsInfo.setGtKxzbg(array.getString(101));
		        assetsInfo.setWsdGdzcbh(array.getString(102));
		        assetsInfo.setWsdYrfs(array.getString(103));
		        assetsInfo.setWsdDygyfs(array.getString(104));
		        assetsInfo.setWsdKgrl(array.getString(105));
		        assetsInfo.setWsdGdfs(array.getString(106));
		        assetsInfo.setPdxSbsl(array.getString(107));
		        assetsInfo.setPdxZrl(array.getString(108));
		        assetsInfo.setPdxKxdks(array.getString(109));
		        assetsInfo.setPdxGdzcbh(array.getString(110));
		        assetsInfo.setPdxXh(array.getString(111));
		        assetsInfo.setPdxSccs(array.getString(112));
		        assetsInfo.setPdxScdks(array.getString(113));
		        assetsInfo.setPdxSfyljk(array.getString(114));
		        assetsInfo.setKgdySbsl(array.getString(115));
		        assetsInfo.setKgdyXh(array.getString(116));
		        assetsInfo.setKgdySccs(array.getString(117));
		        assetsInfo.setKgdyJkmkxh(array.getString(118));
		        assetsInfo.setKgdyPtzlmks(array.getString(119));
		        assetsInfo.setKgdyZlmkxh(array.getString(120));
		        assetsInfo.setKgdyNfecxd(array.getString(121));
		        assetsInfo.setKgdySycws(array.getString(122));
		        assetsInfo.setKgdyGjdzt(array.getString(123));
		        assetsInfo.setKgdyFlmkzs(array.getString(124));
		        assetsInfo.setKgdyEdscdy(array.getString(125));
		        assetsInfo.setKgdyJkxsp(array.getString(126));
		        assetsInfo.setKgdyGdzcbh(array.getString(127));
		        assetsInfo.setKgdyFcgzfh(array.getString(128));
		        assetsInfo.setKgdyQysj(array.getString(129));
		        assetsInfo.setZlpXh(array.getString(130));
		        assetsInfo.setZlpSccs(array.getString(131));
		        assetsInfo.setZlpZrl(array.getString(132));
		        assetsInfo.setZlpRssys(array.getString(133));
		        assetsInfo.setZlpWsyrss(array.getString(134));
		        assetsInfo.setZlpGdzcbh(array.getString(135));
		        assetsInfo.setXdcSblx(array.getString(136));
		        assetsInfo.setXdcGdzcbh(array.getString(137));
		        assetsInfo.setXdcXh(array.getString(138));
		        assetsInfo.setXdcSccs(array.getString(139));
		        assetsInfo.setXdcSbsl(array.getString(140));
		        assetsInfo.setXdcDzedrl(array.getString(141));
		        assetsInfo.setXdcDzdydj(array.getString(142));
		        assetsInfo.setXdcDzxdcs(array.getString(143));
		        assetsInfo.setXdcAzfs(array.getString(144));
		        assetsInfo.setXdcWgywbx(array.getString(145));
		        assetsInfo.setXdcSfly(array.getString(146));
		        assetsInfo.setXdcLjtpywfs(array.getString(147));
		        assetsInfo.setXdcZcbwsfbx(array.getString(148));
		        assetsInfo.setXdcFdsj(array.getString(149));
		        assetsInfo.setXdcKssysj(array.getString(150));
		        assetsInfo.setKtXh(array.getString(151));
		        assetsInfo.setKtSccs(array.getString(152));
		        assetsInfo.setKtSbsl(array.getString(153));
		        assetsInfo.setKtZlxgsfwh(array.getString(154));
		        assetsInfo.setKtSwjzsfbx(array.getString(155));
		        assetsInfo.setKtPsgsfls(array.getString(156));
		        assetsInfo.setKtSftdzq(array.getString(157));
		        assetsInfo.setKtGdzcbh(array.getString(158));
		        assetsInfo.setKtZll(array.getString(159));
		        assetsInfo.setKtSredgl(array.getString(160));
		        assetsInfo.setKtEddy(array.getString(161));
		        assetsInfo.setYjSblx(array.getString(162));
		        assetsInfo.setYjXh(array.getString(163));
		        assetsInfo.setYjSccs(array.getString(164));
		        assetsInfo.setYjEdgl(array.getString(165));
		        assetsInfo.setYjEdgzdy(array.getString(166));
		        assetsInfo.setYjQdfs(array.getString(167));
		        assetsInfo.setYjLqfs(array.getString(168));
		        assetsInfo.setYjQddcxh(array.getString(169));
		        assetsInfo.setYjQddczs(array.getString(170));
		        assetsInfo.setYjQddcrl(array.getString(171));
		        assetsInfo.setYjYxrj(array.getString(172));
		        assetsInfo.setYjGdzcbh(array.getString(173));
		        assetsInfo.setByqXh(array.getString(174));
		        assetsInfo.setByqSccs(array.getString(175));
		        assetsInfo.setByqLx(array.getString(176));
		        assetsInfo.setByqEdgl(array.getString(177));
		        assetsInfo.setByqSreddy(array.getString(178));
		        assetsInfo.setByqSceddy(array.getString(179));
		        assetsInfo.setByqGdzcbh(array.getString(180));
		        assetsInfo.setByqYqywsfzc(array.getString(181));
		        assetsInfo.setByqGyxlfs(array.getString(182));
		        assetsInfo.setByqSfyjg(array.getString(183));
		        assetsInfo.setByqZlsfkq(array.getString(184));
		        assetsInfo.setQfSfyxt(array.getString(185));
		        assetsInfo.setQfSccj(array.getString(186));
		        assetsInfo.setQfGgxh(array.getString(187));
		        assetsInfo.setQfGdzcbh(array.getString(188));
		        assetsInfo.setQfSl(array.getString(189));
		        assetsInfo.setQfSfzcgz(array.getString(190));
		        assetsInfo.setDhjkSfdh(array.getString(191));
		        assetsInfo.setDhjkDhcj(array.getString(192));
		        assetsInfo.setDhjkGgxh(array.getString(193));
		        assetsInfo.setDhjkGdzcbh(array.getString(194));
		        assetsInfo.setDhjkSfzc(array.getString(195));
		        assetsInfo.setAfxtSfy(array.getString(196));
		        assetsInfo.setAfxtAfcj(array.getString(197));
		        assetsInfo.setAfxtGgxh(array.getString(198));
		        assetsInfo.setAfxtGdzcbh(array.getString(199));
		        assetsInfo.setAfxtSfzc(array.getString(200));
		        assetsInfo.setFlxSccj(array.getString(201));
		        assetsInfo.setFlxGgxh(array.getString(202));
		        assetsInfo.setFlxLx(array.getString(203));
		        assetsInfo.setFlxGdzcbh(array.getString(204));
		        assetsInfo.setFlxSl(array.getString(205));
		        assetsInfo.setDbxGwdbxhh(array.getString(206));
		        assetsInfo.setDbxGdzcbh(array.getString(207));
		        assetsInfo.setDbxKkrl(array.getString(208));
		        assetsInfo.setJdpSnsl(array.getString(209));
		        assetsInfo.setJdpSwsl(array.getString(210));
		        assetsInfo.setZhgCj(array.getString(211));
		        assetsInfo.setZhgXh(array.getString(212));
		        assetsInfo.setZhgGdzcbh(array.getString(213));
		        assetsInfo.setZhgZhgsl(array.getString(214));
		        assetsInfo.setCssbSccj(array.getString(215));
		        assetsInfo.setCssbGgxh(array.getString(216));
		        assetsInfo.setCssbCsfs(array.getString(217));
		        assetsInfo.setCssbSjcsdk(array.getString(218));
		        assetsInfo.setCssbZcgs(array.getString(219));
		        assetsInfo.setCssbSfjl(array.getString(220));
		        assetsInfo.setCssbJklx(array.getString(221));
		        assetsInfo.setCssbSfodf(array.getString(222));
		        assetsInfo.setCssbSfefs(array.getString(223));
		        assetsInfo.setCssbZwfs(array.getString(224));
		        assetsInfo.setQtGcjzsj(array.getString(225));
		        assetsInfo.setQtBjbjlx(array.getString(226));
		        assetsInfo.setQtBjbjjl(array.getString(227));
		        assetsInfo.setQtBjxls(array.getString(228));
		        assetsInfo.setQtBjxlzs(array.getString(229));
		        assetsInfo.setQtBjxlx(array.getString(230));
		        assetsInfo.setQtSfxj(array.getString(231));
		        assetsInfo.setQtSblx1(array.getString(232));
		        assetsInfo.setQtGdzcbh1(array.getString(233));
		        assetsInfo.setQtGgxh1(array.getString(234));
		        assetsInfo.setQtSccj1(array.getString(235));
		        assetsInfo.setQtSblx2(array.getString(236));
		        assetsInfo.setQtGdzcbh2(array.getString(237));
		        assetsInfo.setQtGgxh2(array.getString(238));
		        assetsInfo.setQtSccj2(array.getString(239));

				DAOAssets.getInstance(AssetsModifyAllActivity.this).update(assetsInfo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	        assetsInfo.setJfJflx(array.getString(36));
	        assetsInfo.setJfJfjg(array.getString(37));
	        assetsInfo.setJfJfsl(array.getString(38));
	        assetsInfo.setJfGdzcbh(array.getString(39));
	        assetsInfo.setJfJs(array.getString(40));
	        assetsInfo.setJfKj(array.getString(41));
	        assetsInfo.setJfJfjgm(array.getString(42));
	        assetsInfo.setJfJfmj(array.getString(43));
	        assetsInfo.setJfJfms(array.getString(44));
	        assetsInfo.setJfSzlc(array.getString(45));
	        assetsInfo.setJfXyjzsl(array.getString(46));
	        assetsInfo.setJfJfsysl(array.getString(47));
	        assetsInfo.setJfRjglkw(array.getString(48));
	        assetsInfo.setJfDxkws(array.getString(49));
	        assetsInfo.setJfKxczks(array.getString(50));
	        assetsInfo.setJfKxcsks(array.getString(51));
	        assetsInfo.setJfMhqsl(array.getString(52));
	        assetsInfo.setJfNfszfd(array.getString(53));
	        assetsInfo.setJfJdbtlj(array.getString(54));
	        assetsInfo.setJfJcsfcj(array.getString(55));
	        assetsInfo.setJfYwps(array.getString(56));
	        assetsInfo.setJfYwls(array.getString(57));
	        assetsInfo.setJfDbxsfwh(array.getString(58));
	        assetsInfo.setJfFlsfwh(array.getString(59));
	        assetsInfo.setJfMssfwh(array.getString(60));
	        assetsInfo.setJfDbds(array.getString(61));
	        assetsInfo.setJfFlqrl(array.getString(62));
	        assetsInfo.setGtGdzcbh(array.getString(63));
	        assetsInfo.setGtGtgd(array.getString(64));
	        assetsInfo.setGtGttsgd(array.getString(65));
	        assetsInfo.setGtGtlx(array.getString(66));
	        assetsInfo.setGtZdptgd(array.getString(67));
	        assetsInfo.setGtPtpjjj(array.getString(68));
	        assetsInfo.setGtGtjc(array.getString(69));
	        assetsInfo.setGtHbgd(array.getString(70));
	        assetsInfo.setGtSfblzz(array.getString(71));
	        assetsInfo.setGtGtcq(array.getString(72));
	        assetsInfo.setGtGjdw(array.getString(73));
	        assetsInfo.setGtGxdw(array.getString(74));
	        assetsInfo.setGtGtgxfs(array.getString(75));
	        assetsInfo.setGtCzd(array.getString(76));
	        assetsInfo.setGtTgj(array.getString(77));
	        assetsInfo.setGtTjls(array.getString(78));
	        assetsInfo.setGtTtjd(array.getString(79));
	        assetsInfo.setGtTj(array.getString(80));
	        assetsInfo.setGtTpt(array.getString(81));
	        assetsInfo.setGtPtjfz(array.getString(82));
	        assetsInfo.setGtYwazkj(array.getString(83));
	        assetsInfo.setGtSfwgfzc(array.getString(84));
	        assetsInfo.setGtPtsl(array.getString(85));
	        assetsInfo.setGtYckxkj(array.getString(86));
	        assetsInfo.setGtYcptgd(array.getString(87));
	        assetsInfo.setGtYcptlx(array.getString(88));
	        assetsInfo.setGtBgsl(array.getString(89));
	        assetsInfo.setGtYcyybgs(array.getString(90));
	        assetsInfo.setGtYctxsl(array.getString(91));
	        assetsInfo.setGtEckxkj(array.getString(92));
	        assetsInfo.setGtEcptgd(array.getString(93));
	        assetsInfo.setGtEcptlx(array.getString(94));
	        assetsInfo.setGtEcbgsl(array.getString(95));
	        assetsInfo.setGtEcyybgs(array.getString(96));
	        assetsInfo.setGtEctxsl(array.getString(97));
	        assetsInfo.setGtBgqk(array.getString(98));
	        assetsInfo.setGtZybgs(array.getString(99));
	        assetsInfo.setGtRybgs(array.getString(100));
	        assetsInfo.setGtKxzbg(array.getString(101));
	        assetsInfo.setWsdGdzcbh(array.getString(102));
	        assetsInfo.setWsdYrfs(array.getString(103));
	        assetsInfo.setWsdDygyfs(array.getString(104));
	        assetsInfo.setWsdKgrl(array.getString(105));
	        assetsInfo.setWsdGdfs(array.getString(106));
	        assetsInfo.setPdxSbsl(array.getString(107));
	        assetsInfo.setPdxZrl(array.getString(108));
	        assetsInfo.setPdxKxdks(array.getString(109));
	        assetsInfo.setPdxGdzcbh(array.getString(110));
	        assetsInfo.setPdxXh(array.getString(111));
	        assetsInfo.setPdxSccs(array.getString(112));
	        assetsInfo.setPdxScdks(array.getString(113));
	        assetsInfo.setPdxSfyljk(array.getString(114));
	        assetsInfo.setKgdySbsl(array.getString(115));
	        assetsInfo.setKgdyXh(array.getString(116));
	        assetsInfo.setKgdySccs(array.getString(117));
	        assetsInfo.setKgdyJkmkxh(array.getString(118));
	        assetsInfo.setKgdyPtzlmks(array.getString(119));
	        assetsInfo.setKgdyZlmkxh(array.getString(120));
	        assetsInfo.setKgdyNfecxd(array.getString(121));
	        assetsInfo.setKgdySycws(array.getString(122));
	        assetsInfo.setKgdyGjdzt(array.getString(123));
	        assetsInfo.setKgdyFlmkzs(array.getString(124));
	        assetsInfo.setKgdyEdscdy(array.getString(125));
	        assetsInfo.setKgdyJkxsp(array.getString(126));
	        assetsInfo.setKgdyGdzcbh(array.getString(127));
	        assetsInfo.setKgdyFcgzfh(array.getString(128));
	        assetsInfo.setKgdyQysj(array.getString(129));
	        assetsInfo.setZlpXh(array.getString(130));
	        assetsInfo.setZlpSccs(array.getString(131));
	        assetsInfo.setZlpZrl(array.getString(132));
	        assetsInfo.setZlpRssys(array.getString(133));
	        assetsInfo.setZlpWsyrss(array.getString(134));
	        assetsInfo.setZlpGdzcbh(array.getString(135));
	        assetsInfo.setXdcSblx(array.getString(136));
	        assetsInfo.setXdcGdzcbh(array.getString(137));
	        assetsInfo.setXdcXh(array.getString(138));
	        assetsInfo.setXdcSccs(array.getString(139));
	        assetsInfo.setXdcSbsl(array.getString(140));
	        assetsInfo.setXdcDzedrl(array.getString(141));
	        assetsInfo.setXdcDzdydj(array.getString(142));
	        assetsInfo.setXdcDzxdcs(array.getString(143));
	        assetsInfo.setXdcAzfs(array.getString(144));
	        assetsInfo.setXdcWgywbx(array.getString(145));
	        assetsInfo.setXdcSfly(array.getString(146));
	        assetsInfo.setXdcLjtpywfs(array.getString(147));
	        assetsInfo.setXdcZcbwsfbx(array.getString(148));
	        assetsInfo.setXdcFdsj(array.getString(149));
	        assetsInfo.setXdcKssysj(array.getString(150));
	        assetsInfo.setKtXh(array.getString(151));
	        assetsInfo.setKtSccs(array.getString(152));
	        assetsInfo.setKtSbsl(array.getString(153));
	        assetsInfo.setKtZlxgsfwh(array.getString(154));
	        assetsInfo.setKtSwjzsfbx(array.getString(155));
	        assetsInfo.setKtPsgsfls(array.getString(156));
	        assetsInfo.setKtSftdzq(array.getString(157));
	        assetsInfo.setKtGdzcbh(array.getString(158));
	        assetsInfo.setKtZll(array.getString(159));
	        assetsInfo.setKtSredgl(array.getString(160));
	        assetsInfo.setKtEddy(array.getString(161));
	        assetsInfo.setYjSblx(array.getString(162));
	        assetsInfo.setYjXh(array.getString(163));
	        assetsInfo.setYjSccs(array.getString(164));
	        assetsInfo.setYjEdgl(array.getString(165));
	        assetsInfo.setYjEdgzdy(array.getString(166));
	        assetsInfo.setYjQdfs(array.getString(167));
	        assetsInfo.setYjLqfs(array.getString(168));
	        assetsInfo.setYjQddcxh(array.getString(169));
	        assetsInfo.setYjQddczs(array.getString(170));
	        assetsInfo.setYjQddcrl(array.getString(171));
	        assetsInfo.setYjYxrj(array.getString(172));
	        assetsInfo.setYjGdzcbh(array.getString(173));
	        assetsInfo.setByqXh(array.getString(174));
	        assetsInfo.setByqSccs(array.getString(175));
	        assetsInfo.setByqLx(array.getString(176));
	        assetsInfo.setByqEdgl(array.getString(177));
	        assetsInfo.setByqSreddy(array.getString(178));
	        assetsInfo.setByqSceddy(array.getString(179));
	        assetsInfo.setByqGdzcbh(array.getString(180));
	        assetsInfo.setByqYqywsfzc(array.getString(181));
	        assetsInfo.setByqGyxlfs(array.getString(182));
	        assetsInfo.setByqSfyjg(array.getString(183));
	        assetsInfo.setByqZlsfkq(array.getString(184));
	        assetsInfo.setQfSfyxt(array.getString(185));
	        assetsInfo.setQfSccj(array.getString(186));
	        assetsInfo.setQfGgxh(array.getString(187));
	        assetsInfo.setQfGdzcbh(array.getString(188));
	        assetsInfo.setQfSl(array.getString(189));
	        assetsInfo.setQfSfzcgz(array.getString(190));
	        assetsInfo.setDhjkSfdh(array.getString(191));
	        assetsInfo.setDhjkDhcj(array.getString(192));
	        assetsInfo.setDhjkGgxh(array.getString(193));
	        assetsInfo.setDhjkGdzcbh(array.getString(194));
	        assetsInfo.setDhjkSfzc(array.getString(195));
	        assetsInfo.setAfxtSfy(array.getString(196));
	        assetsInfo.setAfxtAfcj(array.getString(197));
	        assetsInfo.setAfxtGgxh(array.getString(198));
	        assetsInfo.setAfxtGdzcbh(array.getString(199));
	        assetsInfo.setAfxtSfzc(array.getString(200));
	        assetsInfo.setFlxSccj(array.getString(201));
	        assetsInfo.setFlxGgxh(array.getString(202));
	        assetsInfo.setFlxLx(array.getString(203));
	        assetsInfo.setFlxGdzcbh(array.getString(204));
	        assetsInfo.setFlxSl(array.getString(205));
	        assetsInfo.setDbxGwdbxhh(array.getString(206));
	        assetsInfo.setDbxGdzcbh(array.getString(207));
	        assetsInfo.setDbxKkrl(array.getString(208));
	        assetsInfo.setJdpSnsl(array.getString(209));
	        assetsInfo.setJdpSwsl(array.getString(210));
	        assetsInfo.setZhgCj(array.getString(211));
	        assetsInfo.setZhgXh(array.getString(212));
	        assetsInfo.setZhgGdzcbh(array.getString(213));
	        assetsInfo.setZhgZhgsl(array.getString(214));
	        assetsInfo.setCssbSccj(array.getString(215));
	        assetsInfo.setCssbGgxh(array.getString(216));
	        assetsInfo.setCssbCsfs(array.getString(217));
	        assetsInfo.setCssbSjcsdk(array.getString(218));
	        assetsInfo.setCssbZcgs(array.getString(219));
	        assetsInfo.setCssbSfjl(array.getString(220));
	        assetsInfo.setCssbJklx(array.getString(221));
	        assetsInfo.setCssbSfodf(array.getString(222));
	        assetsInfo.setCssbSfefs(array.getString(223));
	        assetsInfo.setCssbZwfs(array.getString(224));
	        assetsInfo.setQtGcjzsj(array.getString(225));
	        assetsInfo.setQtBjbjlx(array.getString(226));
	        assetsInfo.setQtBjbjjl(array.getString(227));
	        assetsInfo.setQtBjxls(array.getString(228));
	        assetsInfo.setQtBjxlzs(array.getString(229));
	        assetsInfo.setQtBjxlx(array.getString(230));
	        assetsInfo.setQtSfxj(array.getString(231));
	        assetsInfo.setQtSblx1(array.getString(232));
	        assetsInfo.setQtGdzcbh1(array.getString(233));
	        assetsInfo.setQtGgxh1(array.getString(234));
	        assetsInfo.setQtSccj1(array.getString(235));
	        assetsInfo.setQtSblx2(array.getString(236));
	        assetsInfo.setQtGdzcbh2(array.getString(237));
	        assetsInfo.setQtGgxh2(array.getString(238));
	        assetsInfo.setQtSccj2(array.getString(239));



			final DsHandler hr = new DsHandler(context, pd);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (DAOAssets.getInstance(AssetsModifyAllActivity.this)
							.searchAssetsId(id) == null) {
						DAOAssets.getInstance(AssetsModifyAllActivity.this)
								.add(assetsInfo);
					} else {
						AssetsModel assets = DAOAssets
								.getInstance(AssetsModifyAllActivity.this)
								.searchAssetsId(id).get(0);
						assetsInfo.setId(assets.getId());
						DAOAssets.getInstance(AssetsModifyAllActivity.this)
								.update(assetsInfo);

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
			Log.d(App.LOG_TAG, "array: " + array.toString());
			int wr = 0;
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
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
				wr++;

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
				.append("DSSaveCommon.do");
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
		btnSave = (Button) findViewById(R.id.btn_save);
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
