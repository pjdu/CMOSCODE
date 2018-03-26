package cn.com.chinaccs.datasite.main.ui.cmos;

import java.io.UnsupportedEncodingException;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.ProductModelIndexAdapter;

/**
 * @author Fddi
 * 
 */
public class ProductModelIndexActivity extends BaseActivity {
	private Context context;
	private EditText etProductModule;
	private Button btnQuery;
	private ListView lvPM;
	private ProgressBar pbPM;
	private TextView txtState;
	private SharedPreferences share;
	private List<JSONArray> listRes;
	private Integer pagestart = 0;
	private Integer total = 0;
	private Boolean isRequestState = false;
	private ProgressDialog proDialog;
	private ProductModelIndexAdapter pmAdapter;
	private String userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		share = getSharedPreferences(App.SHARE_TAG, 0);
		userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		setContentView(R.layout.activity_product_model_index);
		initToolbar("设备型号索引");
		this.findViews();
		this.buildList();
		this.setListBottomEvent();
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
		public void onItemClick(AdapterView<?> arg0, View view, final int index,
				long arg3) {
			// TODO Auto-generated method stub
			final JSONArray array = listRes.get(index);
			try {
				//final String product_type = array.getString(1);
				String manufacturer1 = array.getString(2);
				try {
					manufacturer1 = URLEncoder.encode(manufacturer1, App.ENCODE_UTF8);
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				final String manufacturer = manufacturer1; 
				final String product_model = array.getString(3);
				final String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
						+ array.getString(2) + product_model);
				proDialog = App.progressDialog(context,
						context.getResources().getString(R.string.common_response));
				proDialog.show();
				final Handler hr = new Handler();
				new Thread() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						StringBuffer url = new StringBuffer(
								DataSiteStart.HTTP_SERVER_URL);
						url.append("ProductContent.do?userid=").append(userid)
								.append("&factory=").append(manufacturer).append("&model=")
								.append(product_model).append("&sign=").append(sign);
						AppHttpConnection conn = new AppHttpConnection(context,
								url.toString());
						final String conResult = conn.getConnectionResult();
						hr.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								proDialog.cancel();
								proDialog = null;
								if (conResult.equals(AppHttpConnection.RESULT_FAIL)) {
									Toast.makeText(
											context,
											getResources().getString(
													R.string.common_not_network),
											Toast.LENGTH_LONG).show();
								} else {
									try {
										JSONObject resJson = new JSONObject(conResult);
										String result = resJson.getString("result");
										if (!result.equals("1")) {
											Toast.makeText(context,
													resJson.getString("msg"),
													Toast.LENGTH_LONG).show();
										} else {
											StringBuffer title = new StringBuffer(array.getString(1));
											title.append(array.getString(2)).append(product_model);
											JSONArray array = resJson.getJSONArray(
													"data").getJSONArray(0);
											Bundle be = new Bundle();
											be.putString("title", title.toString());
											be.putString("array", array.toString());
											Intent i = new Intent(context,
													ProductContentActivity.class);
											i.putExtras(be);
											startActivity(i);
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						});
					}
				}.start();
				
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
		final PmHandler hr = new PmHandler(this, false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				conn(hr);
			}
		}).start();
	}

	private void findViews() {
		etProductModule = (EditText) findViewById(R.id.edit_product_model);
		btnQuery = (Button) findViewById(R.id.btn_pm_query);
		lvPM = (ListView) findViewById(R.id.lv_pm);
		pbPM = (ProgressBar) findViewById(R.id.pb_pm);
		txtState = (TextView) findViewById(R.id.txt_pm_state);
	}

	private void setListBottomEvent() {
		lvPM.setOnScrollListener(new OnScrollListener() {

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
					pbPM.setVisibility(View.VISIBLE);
					final Handler hr = new PmHandler(
							ProductModelIndexActivity.this, true);
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
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String productModule = etProductModule.getText().toString();
		productModule = productModule == null ? "" : productModule;
		
		
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
				+ productModule + pagestart);
		try {
			productModule = URLEncoder.encode(productModule, App.ENCODE_UTF8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		url.append("ProductModelIndex.do?userid=").append(userId)
				.append("&productModule=").append(productModule).append("&pagestart=")
				.append(pagestart).append("&sign=").append(sign);
		AppHttpConnection conn = new AppHttpConnection(context, url.toString());
		Log.d(App.LOG_TAG, url.toString());
		String conResult = conn.getConnectionResult();
		if (conResult.equals("fail")) {
			msg = hr.obtainMessage(500);
			hr.sendMessage(msg);
			return;
		}
		try {
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
			msg = hr.obtainMessage(200);
			hr.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = hr.obtainMessage(500);
			hr.sendMessage(msg);
		}
	}

	static class PmHandler extends Handler {

		private ProductModelIndexActivity activity;
		private boolean isPageRequest;

		public PmHandler(Activity activity, boolean isPageRequest) {
			this.activity = (ProductModelIndexActivity) activity;
			this.isPageRequest = isPageRequest;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 200:
				if (!isPageRequest) {
					activity.pmAdapter = null;
					activity.pmAdapter = new ProductModelIndexAdapter(activity,
							activity.listRes);
					activity.lvPM.setVisibility(View.VISIBLE);
					activity.txtState.setVisibility(View.GONE);
					activity.lvPM.setAdapter(activity.pmAdapter);
					activity.lvPM.setOnItemClickListener(activity.lrList);
				} else {
					if (activity.pmAdapter != null)
						activity.pmAdapter.notifyDataSetChanged();
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
				activity.pbPM.setVisibility(View.GONE);
			}
			activity.isRequestState = false;
		}

	}
}
