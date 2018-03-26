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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.DSCommonEListAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.FuncDSCommon;

public class DSCdmaCellContentAcitivity extends Activity {

	private Context context;
	private String id;
	private String name;
	private String ci;
	private TextView tvName;
	private TextView tvDate;
	private TextView tvUser;
	private TextView tvDataState;
	private TextView tvState;
	private ExpandableListView elv;
	private Button btnImg;
	private Button btnDatac;
	private Button btnGetIndoor;
	private Button btnGetRepeater;
	private ProgressDialog proDialog;
	private JSONObject jsonRes;
	private FuncDSCommon funcBs;
	private String[] indoorNames;
	private String[] indoorIds;
	private String[] repeaterNames;
	private String[] repeaterIds;
	private String lastUpdateTime;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_ds_cdma_cell_content);
		funcBs = new FuncDSCommon(context);
		Bundle be = getIntent().getExtras();
		id = be.getString("id");
		name = be.getString("name");
		ci = be.getString("ci");
		this.findView();
		tvName.setText(name);
		this.getData();
		btnImg.setOnClickListener(btnlr);
		btnDatac.setOnClickListener(btnlr);
		btnGetIndoor.setOnClickListener(btnlr);
		btnGetRepeater.setOnClickListener(btnlr);
		this.buildList();

	}

	protected void onDestroy() {
		funcBs = null;
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
		tvName = (TextView) findViewById(R.id.txt_name_cell);
		tvDate = (TextView) findViewById(R.id.txt_date_cell);
		tvUser = (TextView) findViewById(R.id.txt_user_cell);
		tvDataState = (TextView) findViewById(R.id.txt_datastate_cell);
		tvState = (TextView) findViewById(R.id.txt_state_cell);
		elv = (ExpandableListView) findViewById(R.id.elv_ds_cdma_cell);
		btnImg = (Button) findViewById(R.id.btn_img_cell);
		btnDatac = (Button) findViewById(R.id.btn_datac_cell);
		btnGetIndoor = (Button) findViewById(R.id.btn_get_indor);
		btnGetRepeater = (Button) findViewById(R.id.btn_get_repeater);
	}

	private void buildList() {
		String[] group = { getResources().getString(R.string.cell_item_bi),
				getResources().getString(R.string.cell_item_pt),
				getResources().getString(R.string.cell_item_other) };
		String[][] children = {
				{ getResources().getString(R.string.cell_item_biarea),
						getResources().getString(R.string.cell_item_bibelong),
						getResources().getString(R.string.cell_item_biconf) },
				{
						getResources().getString(R.string.cell_item_pttx),
						getResources().getString(R.string.cell_item_ptzgj),
						getResources().getString(R.string.cell_item_pttransfer),
						getResources().getString(R.string.cell_item_ptele),
						getResources().getString(R.string.cell_item_ptkt),
						getResources().getString(R.string.cell_item_ptroom) },
				{ getResources().getString(R.string.cell_item_bianjie),
						getResources().getString(R.string.cell_item_other) } };
		final int[][][] positons = {
				{ { 1, 4 }, { 5, 16 }, { 17, 35 } },
				{ { 38, 53 }, { 64, 67 }, { 68, 76 }, { 77, 94 }, { 95, 98 },
						{ 99, 113 } }, { { 36, 37 }, { 54, 63 } } };

		DSCommonEListAdapter ad = new DSCommonEListAdapter(context, group,
				children);
		elv.setAdapter(ad);

		elv.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView lv, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				if (jsonRes == null) {
					Log.d(App.LOG_TAG, "attributes geted failed!");
					return false;
				}
				int[] ps = positons[groupPosition][childPosition];

				try {
					String res = jsonRes.getString("result");
					if (!res.equals("1")) {
						Toast.makeText(context, jsonRes.getString("msg"),
								Toast.LENGTH_SHORT).show();
					} else {
						funcBs.showItems(ps, jsonRes);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		});

	}

	private void updateData() {
		Bundle be = new Bundle();
		be.putInt("datatype", DataSiteStart.TYPE_CELL);
		be.putString("id", id);
		be.putString("name", name);
		try {
			String res = jsonRes.getString("result");
			if (!res.equals("1")) {
				Toast.makeText(context, jsonRes.getString("msg"),
						Toast.LENGTH_SHORT).show();
			} else {
				JSONArray array = jsonRes.getJSONArray("data");
				be.putString("array", array.toString());
//				Intent i = new Intent(context, DSCommonModifyActivity.class);
//				i.putExtras(be);
//				startActivityForResult(i, 0);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private OnClickListener btnlr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = null;
			switch (v.getId()) {
			case R.id.btn_img_cell:
				i = new Intent(context, DSCameraFlowActivity.class);
				i.putExtra("type", DataSiteStart.TYPE_CELL);
				i.putExtra("id", id);
				startActivity(i);
				break;
			case R.id.btn_datac_cell:
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
					Log.d("时间", updateTime + "--" + nowDate);
					Log.d("相差天数", String.valueOf(days));
				} else {
					updateData();
				}

				break;
			case R.id.btn_get_indor:
				proDialog = null;
				proDialog =App.progressDialog(context,
						getResources().getString(R.string.common_request));
				proDialog.show();
				getIndoorData();
				break;
			case R.id.btn_get_repeater:
				proDialog = null;
				proDialog = App.progressDialog(context,
						getResources().getString(R.string.common_request));
				proDialog.show();
				getRepeaterData();
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

	private void getIndoorData() {
		final IndoorHandler indoorHandler = new IndoorHandler(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				indoorConn(indoorHandler);
			}
		}).start();
	}

	private void getRepeaterData() {
		final RepeaterHandler repeaterHandler = new RepeaterHandler(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				repeaterConn(repeaterHandler);
			}
		}).start();
	}

	private void conn(Handler hr) {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId + id
				+ DataSiteStart.TYPE_CELL);
		url.append("DSContentCommon.do?userid=").append(userId).append("&id=")
				.append(id).append("&type=").append(DataSiteStart.TYPE_CELL)
				.append("&sign=").append(sign);
		AppHttpConnection conn = new AppHttpConnection(context, url.toString());
		Log.d(App.LOG_TAG, url.toString());
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

		private DSCdmaCellContentAcitivity at;

		public BsHandler(Activity at) {
			this.at = (DSCdmaCellContentAcitivity) at;
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				at.tvState.setVisibility(View.GONE);
				try {
					String subState = at.jsonRes.getString("substate");
					String date = "暂无人提交数据采集";
					String user = "暂无";
					String state = "暂无";
					String updateTime = null;
					if (subState.equals("1")) {
						date = at.jsonRes.getString("date");
						user = at.jsonRes.getString("user");
						state = at.jsonRes.getString("state");
						updateTime = at.jsonRes.getString("date");
					}
					Log.d(App.LOG_TAG, subState);
					at.tvDate.setText(date);
					at.tvUser.setText(user);
					at.tvDataState.setText(state);
					at.lastUpdateTime = updateTime;
				} catch (JSONException e) {
					// TODO: handle exception
					e.printStackTrace();
					Log.d(App.LOG_TAG, e.toString());
				}
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

	private void indoorConn(Handler handler) {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId + ci);
		try {
			String ci1 = URLEncoder.encode(ci, App.ENCODE_UTF8);
			url.append("DSCDMABSINDOOR.do?userid=").append(userId)
					.append("&ci=").append(ci1).append("&sign=").append(sign);
			AppHttpConnection conn = new AppHttpConnection(context,
					url.toString());
			Log.d(App.LOG_TAG, url.toString());
			String conResult = conn.getConnectionResult();
			JSONObject resJson = new JSONObject(conResult);
			if (conResult.equals("fail")) {
				msg = handler.obtainMessage(1001);
				handler.sendMessage(msg);
				return;
			}
			String result = resJson.getString("result");
			if (!result.equals("1")) {
				msg = handler.obtainMessage(1002, resJson.getString("msg"));
				handler.sendMessage(msg);
				return;
			}
			JSONArray array = resJson.getJSONArray("data");
			indoorNames = new String[array.length()];
			indoorIds = new String[array.length()];
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
				indoorIds[i] = data.getString(0);
				indoorNames[i] = data.getString(1);
			}
			if (indoorIds != null && indoorIds.length != 0
					&& indoorNames != null && indoorNames.length != 0) {
				msg = handler.obtainMessage(1000);
				handler.sendMessage(msg);
				return;
			} else {
				msg = handler.obtainMessage(1003);
				handler.sendMessage(msg);
				return;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = handler.obtainMessage(1001);
			handler.sendMessage(msg);
		}

	}

	static class IndoorHandler extends Handler {
		private DSCdmaCellContentAcitivity at;

		public IndoorHandler(Activity at) {
			this.at = (DSCdmaCellContentAcitivity) at;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1000:
				AlertDialog dialog = new AlertDialog.Builder(at).setItems(
						at.indoorNames, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String id = at.indoorIds[which];
								String name = at.indoorNames[which];
								Bundle be = new Bundle();
								be.putString("id", id);
								be.putString("name", name);
								Intent i = new Intent(at,
										DSCdmaIndoorContentActivity.class);
								i.putExtras(be);
								at.startActivity(i);
								dialog.dismiss();
							}
						}).create();
				dialog.setTitle("所属室分");
				dialog.show();
				break;
			case 1001:
				Toast.makeText(at.context, "连接失败：请检查网络连接！", Toast.LENGTH_LONG)
						.show();
				break;
			case 1002:
				String info = (String) msg.obj;
				Toast.makeText(at.context, "提示：" + info, Toast.LENGTH_LONG)
						.show();
				break;
			case 1003:
				Toast.makeText(at.context, "提示：扇区没有所属的室分系统！", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				Toast.makeText(at.context, "未知错误", Toast.LENGTH_LONG).show();
				break;
			}
			if (at.proDialog != null && at.proDialog.isShowing()) {
				at.proDialog.dismiss();
				at.proDialog = null;
			}
		}
	}

	private void repeaterConn(Handler repeaterhandler) {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId + ci);
		try {
			String ci1 = URLEncoder.encode(ci, App.ENCODE_UTF8);
			url.append("DSCDMABSREPEATER.do?userid=").append(userId)
					.append("&ci=").append(ci1).append("&sign=").append(sign);
			AppHttpConnection conn = new AppHttpConnection(context,
					url.toString());
			Log.d(App.LOG_TAG, url.toString());
			String conResult = conn.getConnectionResult();
			JSONObject resJson = new JSONObject(conResult);
			if (conResult.equals("fail")) {
				msg = repeaterhandler.obtainMessage(1001);
				repeaterhandler.sendMessage(msg);
				return;
			}
			String result = resJson.getString("result");
			if (!result.equals("1")) {
				msg = repeaterhandler.obtainMessage(1002,
						resJson.getString("msg"));
				repeaterhandler.sendMessage(msg);
				return;
			}
			JSONArray array = resJson.getJSONArray("data");
			repeaterNames = new String[array.length()];
			repeaterIds = new String[array.length()];
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
				repeaterIds[i] = data.getString(0);
				repeaterNames[i] = data.getString(1);
			}
			if (repeaterIds != null && repeaterIds.length != 0
					&& repeaterNames != null && repeaterNames.length != 0) {
				msg = repeaterhandler.obtainMessage(1000);
				repeaterhandler.sendMessage(msg);
				return;
			} else {
				msg = repeaterhandler.obtainMessage(1003);
				repeaterhandler.sendMessage(msg);
				return;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = repeaterhandler.obtainMessage(1001);
			repeaterhandler.sendMessage(msg);
		}

	}

	static class RepeaterHandler extends Handler {
		private DSCdmaCellContentAcitivity at;

		public RepeaterHandler(Activity at) {
			this.at = (DSCdmaCellContentAcitivity) at;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1000:
				AlertDialog dialog = new AlertDialog.Builder(at).setItems(
						at.repeaterNames,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String id = at.repeaterIds[which];
								String name = at.repeaterNames[which];
								Bundle be = new Bundle();
								be.putString("id", id);
								be.putString("name", name);
								Intent i = new Intent(at,
										DSCdmaREContentActivity.class);
								i.putExtras(be);
								at.startActivity(i);
								dialog.dismiss();
							}
						}).create();
				dialog.setTitle("所属直放");
				dialog.show();
				break;
			case 1001:
				Toast.makeText(at.context, "连接失败：请检查网络连接！", Toast.LENGTH_LONG)
						.show();
				break;
			case 1002:
				String info = (String) msg.obj;
				Toast.makeText(at.context, "提示：" + info, Toast.LENGTH_LONG)
						.show();
				break;
			case 1003:
				Toast.makeText(at.context, "提示：扇区没有所属的直放站！", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				Toast.makeText(at.context, "未知错误", Toast.LENGTH_LONG).show();
				break;
			}
			if (at.proDialog != null && at.proDialog.isShowing()) {
				at.proDialog.dismiss();
				at.proDialog = null;
			}
		}
	}

}
