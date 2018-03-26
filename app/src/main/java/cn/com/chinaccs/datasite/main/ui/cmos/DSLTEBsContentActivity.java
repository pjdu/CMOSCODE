package cn.com.chinaccs.datasite.main.ui.cmos;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.DSCommonEListAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.FuncDSCommon;

/**
 * @author Fddi LTE某基站详细界面
 */
public class DSLTEBsContentActivity extends Activity {
	private Context context;
	private String id;
	private TextView tvName;
	private TextView tvDate;
	private TextView tvUser;
	private TextView tvDataState;
	private TextView tvState;
	private ExpandableListView elv;
	private Button btnImg;
	private Button btnDatac;
	private Button btnGetCell;
	private ProgressDialog proDialog;
	private JSONObject jsonRes;
	private FuncDSCommon funcBs;
	private String[] cellNames;
	private String[] cellIds;
	private String lastUpdateTime;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		funcBs = new FuncDSCommon(context);
		setContentView(R.layout.activity_ds_cdma_bs_content);
		Bundle be = getIntent().getExtras();
		id = be.getString("id");
		name = be.getString("name");
		this.findViews();
		tvName.setText(name);
		this.getData();
		btnImg.setOnClickListener(btnLr);
		btnDatac.setOnClickListener(btnLr);
		btnGetCell.setOnClickListener(btnLr);
		this.buildListGroup();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		funcBs = null;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK)
			this.getData();
	}

	private void buildListGroup() {
		String[] group = { getResources().getString(R.string.bs_item_bi),
				getResources().getString(R.string.bs_item_pt),
				getResources().getString(R.string.bs_item_other) };
		String[][] children = {
				{ getResources().getString(R.string.bs_item_biarea),
						getResources().getString(R.string.bs_item_bibelong) },
				{ getResources().getString(R.string.lte_bs_tower),
						getResources().getString(R.string.lte_bs_trans),
						getResources().getString(R.string.lte_cdma_share),
						getResources().getString(R.string.lte_power),
						getResources().getString(R.string.bs_item_ptkt),
						getResources().getString(R.string.bs_item_ptroom) },
				{ getResources().getString(R.string.bs_item_other) } };

		final int[][][] positons = { { { 1, 4 }, { 5, 26 } },
				{ { 27, 31 }, { 32, 35 }, { 36, 37 }, { 38, 57 }, { 58, 65 }, {75, 88} },
				{ { 66, 74 } } };

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
		be.putInt("datatype", DataSiteStart.TYPE_LTE_BASESTATION);
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

	private OnClickListener btnLr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = null;
			switch (v.getId()) {
			case R.id.btn_img:
				i = new Intent(context, DSCameraFlowActivity.class);
				i.putExtra("type", DataSiteStart.TYPE_LTE_BASESTATION);
				i.putExtra("id", id);
				startActivity(i);
				break;
			case R.id.btn_datac:
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
			case R.id.btn_get_cell:
				proDialog = null;
				proDialog = App.progressDialog(context,
						getResources().getString(R.string.common_request));
				proDialog.show();
				getCellData();
				break;
			default:
				break;
			}
		}
	};

	private void findViews() {
		tvName = (TextView) findViewById(R.id.txt_name);
		tvDate = (TextView) findViewById(R.id.txt_date);
		tvUser = (TextView) findViewById(R.id.txt_user);
		tvDataState = (TextView) findViewById(R.id.txt_datastate);
		tvState = (TextView) findViewById(R.id.txt_state);
		elv = (ExpandableListView) findViewById(R.id.elv_ds_cdma_bs);
		btnImg = (Button) findViewById(R.id.btn_img);
		btnDatac = (Button) findViewById(R.id.btn_datac);
		btnGetCell = (Button) findViewById(R.id.btn_get_cell);
	}

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

	private void getCellData() {
		final CellHandler cellHandler = new CellHandler(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				cellConn(cellHandler);
			}
		}).start();

	}

	private void conn(Handler hr) {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId + id
				+ DataSiteStart.TYPE_LTE_BASESTATION);
		url.append("DSContentCommon.do?userid=").append(userId).append("&id=")
				.append(id).append("&type=")
				.append(DataSiteStart.TYPE_LTE_BASESTATION).append("&sign=")
				.append(sign);
		AppHttpConnection conn = new AppHttpConnection(context, url.toString());
		Log.d(App.LOG_TAG, url.toString());
		String conResult = conn.getConnectionResult();
		if (conResult.equals("fail")) {
			msg = hr.obtainMessage(500);
			hr.sendMessage(msg);
			return;
		}
		try {
			// 传输解密
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

		private DSLTEBsContentActivity at;

		public BsHandler(Activity at) {
			this.at = (DSLTEBsContentActivity) at;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 200:
				at.tvState.setVisibility(View.GONE);
				try {
					String subState = at.jsonRes.getString("substate");
					String date = "暂无人提交采集数据";
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
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d(App.LOG_TAG, e.toString());
				}
				break;
			case 500:
				at.tvState.setText("连接失败：请检查网络连接！");
				Toast.makeText(at.context, "连接失败：请检查网络连接！", Toast.LENGTH_LONG)
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
				at.tvState.setText("未知错误");
				Toast.makeText(at.context, "未知错误", Toast.LENGTH_LONG).show();
				at.btnDatac.setEnabled(false);
				break;
			}
			if (at.proDialog != null && at.proDialog.isShowing()) {
				at.proDialog.dismiss();
				at.proDialog = null;
			}
		}
	}

	private void cellConn(Handler handler) {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId + name);
		Log.d(App.LOG_TAG, "userId:" + userId + ";name:" + name);
		try {
			
			
			String name1 = URLEncoder.encode(name, App.ENCODE_UTF8);
			
			url.append("DSLTEBSCELL.do?userid=").append(userId)
					.append("&bsName=").append(name1)
					.append("&sign=").append(sign);
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
			cellNames = new String[array.length()];
			cellIds = new String[array.length()];
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
				cellIds[i] = data.getString(0);
				cellNames[i] = data.getString(1);
			}
			if (cellIds != null && cellIds.length != 0 && cellNames != null
					&& cellNames.length != 0) {
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

	static class CellHandler extends Handler {
		private DSLTEBsContentActivity at;

		public CellHandler(Activity at) {
			this.at = (DSLTEBsContentActivity) at;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1000:
				AlertDialog dialog = new AlertDialog.Builder(at).setItems(
						at.cellNames, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String id = at.cellIds[which];
								String name = at.cellNames[which];
								Bundle be = new Bundle();
								be.putString("id", id);
								be.putString("name", name);
								Intent i = new Intent(at,
										DSLTECellContentActivity.class);
								i.putExtras(be);
								at.startActivity(i);
								dialog.dismiss();
							}
						}).create();
				dialog.setTitle("所属扇区");
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
				Toast.makeText(at.context, "提示：基站没有所属的扇区系统！", Toast.LENGTH_LONG)
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
