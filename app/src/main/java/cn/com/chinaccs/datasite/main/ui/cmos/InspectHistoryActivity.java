package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.DefaultAdapter;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetHistoryList;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetUserCount;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.widget.TabDrawTop;


public class InspectHistoryActivity extends BaseActivity {
	private Context context;
	private TabHost tabMain;
	private TextView tvUser;   //用户
	private TextView tvDateFrom;  //开始时间
	private TextView tvDateTo;  //截止时间
	private Button btnQuery;   //查询按钮
	private TextView tvC1;    //巡检统计的3个textview
	private TextView tvC2;
	private TextView tvC3;
	private LinearLayout layoutPlan;   //作业统计的布局
	private ListView lvHistory;   //历史记录的listview
	private SharedPreferences share;
	private DatePickerDialog dpdFrom;
	private DatePickerDialog dpdTo;
	private int pagestart = 0;  //单页数量
	private int total = 0;  //记录总数，从后台获取，用于判断是否继续加载数据
	private List<String> list,listId;
	private DefaultAdapter ad;
	private boolean isRequestState = false;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_inspect_percenter);
		initToolbar("巡检记录");
		this.findViews();
		this.initTabhost();
		this.initDatePick();
		share = getSharedPreferences(App.SHARE_TAG, 0);
		tvDateFrom.setOnClickListener(lr);
		tvDateTo.setOnClickListener(lr);
		btnQuery.setOnClickListener(lr);
		list = new ArrayList<String>();
		listId = new ArrayList<String>();
		getCount();
		getHistoryList();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		list = null;
		listId = null;
		ad = null;
		pd = null;
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

	private OnClickListener lr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tv_datefrom:
				if (dpdFrom != null & !dpdFrom.isShowing()) {
					dpdFrom.show();
				}
				break;
			case R.id.tv_dateto:
				if (dpdTo != null & !dpdTo.isShowing()) {
					dpdTo.show();
				}
				break;
			case R.id.btn_query:
				if (!isRequestState) {
					isRequestState = true;
					pagestart = 0;
					total = 0;
					list = new ArrayList<String>();
					listId = new ArrayList<String>();
					ad = null;
					getCount();
					getHistoryList();
				}
				break;
			default:
				break;
			}
		}
	};

	private void initTabhost() {
		View vc = (View) LayoutInflater.from(context).inflate(
				R.layout.item_tab_widget, null);
		TextView tv1 = (TextView) vc.findViewById(R.id.tab_label);
		tv1.setText("巡检统计");
		tv1.setTextColor(getResources().getColor(R.color.green_deep));

		View vp = (View) LayoutInflater.from(context).inflate(
				R.layout.item_tab_widget, null);
		TextView tv2 = (TextView) vp.findViewById(R.id.tab_label);
		tv2.setText("作业统计");

		View vh = (View) LayoutInflater.from(context).inflate(
				R.layout.item_tab_widget, null);
		TextView tv3 = (TextView) vh.findViewById(R.id.tab_label);
		tv3.setText("历史记录");

		tabMain.setup();
		tabMain.addTab(tabMain.newTabSpec("tab1").setIndicator(vc)
				.setContent(R.id.layout_times));
		tabMain.addTab(tabMain.newTabSpec("tab2").setIndicator(vp)
				.setContent(R.id.layout_plan));
		tabMain.addTab(tabMain.newTabSpec("tab3").setIndicator(vh)
				.setContent(R.id.lv_ithostory));
		tabMain.setCurrentTab(0);
		TabDrawTop tabDraw = new TabDrawTop(tabMain);
		tabDraw.getTabWidget();
		tabMain.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				TabWidget tabWidget = tabMain.getTabWidget();
				for (int i = 0; i < tabWidget.getChildCount(); i++) {
					final TextView tv = (TextView) tabWidget.getChildAt(i)
							.findViewById(R.id.tab_label);
					if (tabMain.getCurrentTab() == i) {
						tv.setTextColor(getResources().getColor(
								R.color.green_deep));
					} else {
						tv.setTextColor(getResources().getColor(R.color.black));
					}
				}
			}
		});
	}

	private void initDatePick() {
		OnDateSetListener dlr1 = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
								  int dayOfMonth) {
				// TODO Auto-generated method stub
				String strFormoth = Integer.toString(monthOfYear + 1);
				String strForDay = Integer.toString(dayOfMonth);
				if (monthOfYear < 9) {
					strFormoth = "0" + Integer.toString(monthOfYear + 1);
				}
				if (dayOfMonth < 10) {
					strForDay = "0" + Integer.toString(dayOfMonth);
				}
				String dateShow = Integer.toString(year) + "-" + strFormoth
						+ "-" + strForDay;
				tvDateFrom.setText(dateShow);
			}
		};
		OnDateSetListener dlr2 = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
								  int dayOfMonth) {
				// TODO Auto-generated method stub
				String strFormoth = Integer.toString(monthOfYear + 1);
				String strForDay = Integer.toString(dayOfMonth);
				if (monthOfYear < 9) {
					strFormoth = "0" + Integer.toString(monthOfYear + 1);
				}
				if (dayOfMonth < 10) {
					strForDay = "0" + Integer.toString(dayOfMonth);
				}
				String dateShow = Integer.toString(year) + "-" + strFormoth
						+ "-" + strForDay;
				tvDateTo.setText(dateShow);
			}
		};
		Calendar crTo = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		String str = sdf.format(crTo.getTime());
		tvDateTo.setText(str);
		Calendar crFrom = Calendar.getInstance();
		crFrom.set(Calendar.DAY_OF_MONTH, 1);
		str = sdf.format(crFrom.getTime());
		tvDateFrom.setText(str);
		dpdFrom = new DatePickerDialog(context, dlr1,
				crFrom.get(Calendar.YEAR), crFrom.get(Calendar.MONTH),
				crFrom.get(Calendar.DAY_OF_MONTH));
		dpdTo = new DatePickerDialog(context, dlr2, crTo.get(Calendar.YEAR),
				crTo.get(Calendar.MONTH), crTo.get(Calendar.DAY_OF_MONTH));
	}
	//加入巡检统计
	private void getCount() {
		pd = App.progressDialog(context,
				getResources().getString(R.string.common_request));
		pd.show();
		OnGetDataFinishedListener flr = new OnGetDataFinishedListener() {

			@Override
			public void onFinished(String output) {
				// TODO Auto-generated method stub
				isRequestState = false;
				int total = 0;
				int c1 = 0;
				int c2 = 0;
				int c3 = 0;
				if (output.equals(AppHttpConnection.RESULT_FAIL)) {
					Toast.makeText(
							context,
							getResources().getString(
									R.string.common_not_network),
							Toast.LENGTH_LONG).show();
				} else {
					try {
						JSONObject json = new JSONObject(output);
						String result = json.getString("result");
						String msg = json.getString("msg");
						if (result.equals("1")) {
							JSONArray array = json.getJSONArray("data");
							for (int i = 0; i < array.length(); i++) {
								JSONArray data = array.getJSONArray(i);
								switch (data.getInt(0)) {
								case -1:
									c3 = data.getInt(1);
									break;
								case 0:
									c1 = data.getInt(1);
									break;
								case 1:
									c2 = data.getInt(1);
									break;

								default:
									break;
								}
							}
						} else {
							Toast.makeText(context, msg, Toast.LENGTH_LONG)
									.show();
						}
						if (json.has("dataPlan")) {
							JSONArray arrayPlan = json.getJSONArray("dataPlan");
							layoutPlan.removeAllViews();
							for (int i = 0; i < arrayPlan.length(); i++) {
								JSONArray data = arrayPlan.getJSONArray(i);
								addPlanCount(data);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				tvC1.setText("尚未审核巡检记录数：" + c1);
				tvC2.setText("合格巡检记录数：" + c2);
				tvC3.setText("不合格巡检记录数：" + c3);
				total = c1 + c2 + c3;
				String userName = share.getString(
						AppCheckLogin.SHARE_USER_NAME, "");
				tvUser.setText(userName + "【巡检次数：" + total + "】");
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
			}
		};
		FuncGetUserCount func = new FuncGetUserCount(context);
		String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		func.getData(flr, userid, tvDateFrom.getText().toString(), tvDateTo
				.getText().toString());
	}
	//加入作业统计
	private void addPlanCount(JSONArray data) {
		View vp = LayoutInflater.from(context).inflate(R.layout.item_list_plan,
				null);
		TextView tv = (TextView) vp.findViewById(R.id.tv_item_plan);
		try {
			tv.setText(data.getString(0));
			tv.append("\n【巡检次数】：");
			tv.append(data.getString(1));
			layoutPlan.addView(vp);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	//加入历史记录
	private void getHistoryList() {
		OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

			@Override
			public void onFinished(String output) {
				isRequestState = false;
				if (output.equals(AppHttpConnection.RESULT_FAIL)) {
					Toast.makeText(
							context,
							getResources().getString(
									R.string.common_not_network),
							Toast.LENGTH_LONG).show();
					if (ad == null) {
						list.add(getResources().getString(
								R.string.common_not_network));
						ad = new DefaultAdapter(context, list);
						lvHistory.setAdapter(ad);
					}
				} else {
					try {
						JSONObject json = new JSONObject(output);
						String result = json.getString("result");
						String msg = json.getString("msg");
						if (result.equals("1")) {
							pagestart += 10;
							String totalStr = json.getString("total");
							total = Integer.parseInt(totalStr);
							JSONArray array = json.getJSONArray("data");
							for (int i = 0; i < array.length(); i++) {
								JSONArray data = array.getJSONArray(i);
								StringBuffer str = new StringBuffer(
										data.getString(0));
								str.append("\n巡检基站：【").append(data.getString(1))
										.append("】\n是否RRU巡检：【")
										.append(data.getString(5))
										.append("】\n任务计划：【")
										.append(data.getString(2))
										.append("】\n相差距离：【")
										.append(data.getString(7))
										.append("米】\n状态【")
										.append(data.getString(3)).append("】");
								list.add(str.toString());
								listId.add(data.getString(4));
								Log.d(App.LOG_TAG, str.toString());
							}
							if (ad == null) {
								ad = new DefaultAdapter(context, list);
								lvHistory.setAdapter(ad);
								lvHistory.setOnScrollListener(slr);
								lvHistory.setOnItemClickListener(hislr);
							} else {
								ad.notifyDataSetChanged();
							}
						} else {
							Toast.makeText(context, msg, Toast.LENGTH_LONG)
									.show();
							if (ad == null) {
								list.add(msg);
								ad = new DefaultAdapter(context, list);
								lvHistory.setAdapter(ad);
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		};
		FuncGetHistoryList func = new FuncGetHistoryList(context);
		String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		func.getData(lr, userid, tvDateFrom.getText().toString(), tvDateTo
				.getText().toString(), pagestart);
	}
	
	private OnItemClickListener hislr = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
								long arg3) {
			// TODO Auto-generated method stub
			String taskId = listId.get(position);
			Bundle be = new Bundle();
			be.putString("tid", taskId);
			Intent it = new Intent(context, InspectHistoryContentActivity.class);
			it.putExtras(be);
			startActivity(it);
			Toast.makeText(context, "ID" + taskId, Toast.LENGTH_LONG).show();
		}
	};

	private OnScrollListener slr = new OnScrollListener() {

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
				getHistoryList();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub

		}
	};

	private void findViews() {
		tabMain = (TabHost) findViewById(R.id.tab_main);
		tvUser = (TextView) findViewById(R.id.tv_user);
		tvDateFrom = (TextView) findViewById(R.id.tv_datefrom);
		tvDateTo = (TextView) findViewById(R.id.tv_dateto);
		tvC1 = (TextView) findViewById(R.id.tv_count1);
		tvC2 = (TextView) findViewById(R.id.tv_count2);
		tvC3 = (TextView) findViewById(R.id.tv_count3);
		btnQuery = (Button) findViewById(R.id.btn_query);
		layoutPlan = (LinearLayout) findViewById(R.id.layout_plan);
		lvHistory = (ListView) findViewById(R.id.lv_ithostory);
	}
}
