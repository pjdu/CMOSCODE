package cn.com.chinaccs.datasite.main.ui.cmos;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.DSCdmaRealBSAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetBsTowns;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetUserArea;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * @author Fddi
 * 
 */
public class DSCdmaRealBSActivity extends Activity {
	private Context context;
	private Spinner spArea;
	private Spinner spCounty;
	private Spinner spTown;
	private EditText etBsName;
	private Button btnQuery;
	private ListView lvBs;
	private ProgressBar pbBs;
	private TextView txtState;
	private String areaName;
	private String countyName;
	private String townName;
	private String[] areas;
	private String[] countys;
	private String[] towns;
	private List<JSONArray> listRes;
	private Integer pagestart = 0;
	private Integer total = 0;
	private Boolean isRequestState = false;
	private ProgressDialog proDialog;
	private DSCdmaRealBSAdapter bsAdapter;
	private TextView tvBsTotal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_ds_cdma_real_basestation);
		this.findViews();
		this.buildSp();
		btnQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pagestart = 0;
				total = 0;
				if (!isRequestState) {
					isRequestState = true;
					buildList();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		spArea = null;
		spCounty = null;
		spTown = null;
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

	private OnItemClickListener lrList = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int index,
				long arg3) {
			// TODO Auto-generated method stub
			JSONArray array = listRes.get(index);
			try {
				String bsType = array.getString(6);
				if (bsType.equals("bs")) {
					String id = array.getString(0);
					String name = array.getString(1);
					String btsid = array.getString(2);
					String bsc = array.getString(5);
					Bundle be = new Bundle();
					be.putString("id", id);
					be.putString("name", name);
					be.putString("btsid", btsid);
					be.putString("bsc", bsc);
					Intent i = new Intent(context, DSCdmaBSContentActivity.class);
					i.putExtras(be);
					startActivity(i);
				} else if (bsType.equals("cell")){
					String id = array.getString(0);
					String name = array.getString(1);
					String ci = array.getString(5);
					Bundle be = new Bundle();
					be.putString("id", id);
					be.putString("name", name);
					be.putString("ci", ci);
					Intent i = new Intent(context, DSCdmaCellContentAcitivity.class);
					i.putExtras(be);
					startActivity(i);
				} 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private void buildList() {
		txtState.setVisibility(View.VISIBLE);
		txtState.setText(getResources().getString(R.string.common_request));
		listRes = null;
		listRes = new ArrayList<JSONArray>();
		proDialog = null;
		proDialog = App.progressDialog(context,
				getResources().getString(R.string.common_request));
		proDialog.show();
		final BsHandler hr = new BsHandler(this, false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				conn(hr);
			}
		}).start();
	}

	private void buildSp() {
		FuncGetUserArea func = new FuncGetUserArea(context);
		proDialog = null;
		proDialog = App.progressDialog(context,
				getResources().getString(R.string.common_request));
		proDialog.show();
		OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

			@Override
			public void onFinished(String output) {
				// TODO Auto-generated method stub
				proDialog.dismiss();
				try {
					getAdapterSp(output);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		};
		func.getData(lr);
	}

	private void getAdapterSp(String output) throws JSONException {
		if (output.equals(AppHttpConnection.RESULT_FAIL)) {
			Toast.makeText(context,
					getResources().getString(R.string.common_not_network),
					Toast.LENGTH_LONG).show();
		} else {
			JSONObject json = new JSONObject(output);
			String result = json.getString("result");
			if (!result.equals("1")) {
				Toast.makeText(context, json.getString("msg"),
						Toast.LENGTH_LONG).show();
			} else {
				JSONArray datas = json.getJSONArray("data");
				JSONArray data = datas.getJSONArray(0);
				String id = data.getString(0);
				List<String> arealist = new ArrayList<String>();
				List<String> aclist = new ArrayList<String>();
				final List<List<String>> countylist = new ArrayList<List<String>>();
				arealist.add(data.getString(1));
				aclist.add(data.getString(0));
				List<String> list = new ArrayList<String>();
				for (int i = 1; i < datas.length(); i++) {
					data = datas.getJSONArray(i);
					if (id.equals(data.getString(2))) {
						list.add(data.getString(1));
					} else {
						id = data.getString(0);
						arealist.add(data.getString(1));
						aclist.add(data.getString(0));
						countylist.add(list);
						list = new ArrayList<String>();
					}
				}
				countylist.add(list);
				areas = new String[arealist.size()];
				for (int i = 0; i < arealist.size(); i++) {
					areas[i] = arealist.get(i);
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, R.layout.item_spinner, areas);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spArea.setAdapter(adapter);
				spArea.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int index, long arg3) {
						// TODO Auto-generated method stub
						areaName = areas[index];
						List<String> list = countylist.get(index);
						getSpSecond(list);
						etBsName.setText(null);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) { //
						// TODO Auto-generated method stub

					}
				});
				spArea.setSelection(0, true);
			}
		}
	}

	private void getSpSecond(List<String> list) {
		countys = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			countys[i] = list.get(i);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.item_spinner, countys);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCounty.setAdapter(adapter);
		spCounty.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int index, long arg3) {
				// TODO Auto-generated method stub
				countyName = countys[index];
				
				getBsTownList(countyName);
//				pagestart = 0;
//				total = 0;
//				etBsName.setText(null);
//				if (!isRequestState) {
//					isRequestState = true;
//					buildList();// 创建列表
//				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { //
				// TODO Auto-generated method stub
			}
		});
		spCounty.setSelection(0, true);
//		setListBottomEvent();// 绑定list下拉加载事件
	}
	
	private void getBsTownList(String county) {
		FuncGetBsTowns func = new FuncGetBsTowns(context);
		proDialog = null;
		proDialog = App.progressDialog(context,
				getResources().getString(R.string.common_request));
		proDialog.show();
		OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

			@Override
			public void onFinished(String output) {
				// TODO Auto-generated method stub
				proDialog.dismiss();
				try {					
					if (output.equals(AppHttpConnection.RESULT_FAIL)) {
						Toast.makeText(context,
								getResources().getString(R.string.common_not_network),
								Toast.LENGTH_LONG).show();
					} else {
						JSONObject json = new JSONObject(output);
						String result = json.getString("result");
						if (!result.equals("1")) {
							Toast.makeText(context, json.getString("msg"),
									Toast.LENGTH_LONG).show();
						} else {
							JSONArray datas = json.getJSONArray("data");
							List<String> townList = new ArrayList<String>();
							JSONArray data = null;
							for (int i = 0; i < datas.length(); i++) {
								data = datas.getJSONArray(i);
								townList.add(data.getString(0));
							}
							getSpTownSecond(townList);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		};
		func.getData(lr, county);
	}
	
	private void getSpTownSecond(List<String> list) {
		towns = new String[list.size()];
		towns[0] = "全部";
		for (int i = 1; i < list.size(); i++) {
			towns[i] = list.get(i);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.item_spinner, towns);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTown.setAdapter(adapter);
		spTown.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int index, long arg3) {
				// TODO Auto-generated method stub
				townName = towns[index];
				pagestart = 0;
				total = 0;
				etBsName.setText(null);
				if (!isRequestState) {
					isRequestState = true;
					buildList();// 创建列表
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { //
				// TODO Auto-generated method stub
			}
		});
		spTown.setSelection(0, true);
		setListBottomEvent();// 绑定list下拉加载事件
	}

	private void findViews() {
		spArea = (Spinner) findViewById(R.id.sp_bs_area);
		spCounty = (Spinner) findViewById(R.id.sp_bs_county);
		spTown = (Spinner) findViewById(R.id.sp_bs_town);
		etBsName = (EditText) findViewById(R.id.edit_bs_name);
		btnQuery = (Button) findViewById(R.id.btn_bs_query);
		lvBs = (ListView) findViewById(R.id.lv_bs);
		pbBs = (ProgressBar) findViewById(R.id.pb_bs);
		txtState = (TextView) findViewById(R.id.txt_bs_state);
		tvBsTotal = (TextView) findViewById(R.id.tv_bs_total);
	}

	private void setListBottomEvent() {
		lvBs.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
					if (total <= pagestart && pagestart != 0) {
						// 起始页从零开始，累加10，当大于总数时说明已无数据
						return;
					}
					if (isRequestState) {
						// 避免重复请求，若正在请求数据时返回
						return;
					}
					isRequestState = true;
					pbBs.setVisibility(View.VISIBLE);
					final Handler hr = new BsHandler(
							DSCdmaRealBSActivity.this, true);
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							conn(hr);
						}
					}).start();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void conn(Handler hr) {
		areaName = areas[spArea.getSelectedItemPosition()];
		countyName = countys[spCounty.getSelectedItemPosition()];
		townName = towns[spTown.getSelectedItemPosition()];
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String name = etBsName.getText().toString();
		name = name == null ? "" : name;
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
				+ areaName + countyName + townName + name + pagestart);
		try {
			name = URLEncoder.encode(name, App.ENCODE_UTF8);
			String area = URLEncoder.encode(areaName, App.ENCODE_UTF8);
			String county = URLEncoder.encode(countyName, App.ENCODE_UTF8);
			String town = URLEncoder.encode(townName, App.ENCODE_UTF8);
			url.append("DSCDMARealBS.do?userid=").append(userId)
					.append("&areaname=").append(area).append("&countyname=")
					.append(county).append("&townName=").append(town)
					.append("&name=").append(name)
					.append("&pagestart=").append(pagestart).append("&sign=")
					.append(sign);
			AppHttpConnection conn = new AppHttpConnection(context,
					url.toString());
			Log.d(App.LOG_TAG, url.toString());
			String conResult = conn.getConnectionResult();
			if (conResult.equals("fail")) {
				msg = hr.obtainMessage(500);
				hr.sendMessage(msg);
				return;
			}
			JSONObject resJson = new JSONObject(conResult);
			String result = resJson.getString("result");
			if (!result.equals("1")) {
				msg = hr.obtainMessage(501, resJson.getString("msg"));
				hr.sendMessage(msg);
				return;
			}
			pagestart += 10;
			String totalStr = resJson.getString("total");
			total = Integer.parseInt(totalStr);
			JSONArray array = resJson.getJSONArray("data");
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
				listRes.add(data);
			}
			String testString = array.toJSONObject(array).toString();
			Log.d("test", testString);
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

		private DSCdmaRealBSActivity activity;
		private boolean isPageRequest;

		public BsHandler(Activity activity, boolean isPageRequest) {
			this.activity = (DSCdmaRealBSActivity) activity;
			this.isPageRequest = isPageRequest;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 200:
				if (!isPageRequest) {
					activity.bsAdapter = null;
					activity.bsAdapter = new DSCdmaRealBSAdapter(activity,
							activity.listRes);
					activity.lvBs.setVisibility(View.VISIBLE);
					activity.txtState.setVisibility(View.GONE);
					activity.tvBsTotal.setText("共" + activity.total + "个实际物理站点");
					activity.lvBs.setAdapter(activity.bsAdapter);
					activity.lvBs.setOnItemClickListener(activity.lrList);
				} else {
					if (activity.bsAdapter != null)
						activity.bsAdapter.notifyDataSetChanged();
				}
				break;
			case 500:
				activity.txtState.setText("连接失败：请检查网络连接！");
				Toast.makeText(activity.context, "连接失败：请检查网络连接！",
						Toast.LENGTH_LONG).show();
				break;
			case 501:
				String info = (String) msg.obj;
				activity.txtState.setText("提示：" + info);
				Toast.makeText(activity.context, "提示：" + info,
						Toast.LENGTH_LONG).show();
				break;
			default:
				activity.txtState.setText("未知错误");
				Toast.makeText(activity.context, "未知错误", Toast.LENGTH_LONG)
						.show();
				break;
			}
			if (activity.proDialog != null && activity.proDialog.isShowing()) {
				activity.proDialog.dismiss();
				activity.proDialog = null;
			}
			if (isPageRequest) {
				activity.pbBs.setVisibility(View.GONE);
			}
			activity.isRequestState = false;
		}

	}
}
