package cn.com.chinaccs.datasite.main.ui.cmos;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;

public class ProductMainActivity extends BaseActivity {
	private Context context;
	// private TextView tvTitle;
	private Spinner spType;
	private Spinner spFt;
	private Spinner spModel;
	private Button btnQuery;
	private String eqType;
	private ProgressDialog pd;
	private List<String> listType;
	private List<String> listFts;
	private List<String> listmodels;
	private String userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = ProductMainActivity.this;
		setContentView(R.layout.activity_product_main);
		initToolbar("");
		this.findViews();
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		eqType = getIntent().getStringExtra("type");
		if (eqType.equals(DataSiteStart.TYPE_EQ_MAIN)) {
			initToolbar(getResources().getString(R.string.know_master));
			// tvTitle.setText(getResources().getString(R.string.know_master));
		} else {
			initToolbar(getResources().getString(R.string.know_assort));
		}
		this.getEqType();
		btnQuery.setOnClickListener(lr);
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

	private OnClickListener lr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_query:
				if ((listType != null && listType.size() > 0)
						&& (listFts != null && listFts.size() > 0)
						&& (listmodels != null && listmodels.size() > 0)) {
					getEqContent();
				} else {
					Toast.makeText(
							context,
							getResources().getString(R.string.common_vad_empty),
							Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};

	private void getEqContent() {
		final String type = listType.get(spType.getSelectedItemPosition());
		String factory = listFts.get(spFt.getSelectedItemPosition());
		try {
			factory = URLEncoder.encode(factory, App.ENCODE_UTF8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final String ft = factory;
		final String model = listmodels.get(spModel.getSelectedItemPosition());
		final String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
				+ listFts.get(spFt.getSelectedItemPosition()) + model);
		pd = App.progressDialog(context,
				context.getResources().getString(R.string.common_response));
		pd.show();
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("ProductContent.do?userid=").append(userid)
						.append("&factory=").append(ft).append("&model=")
						.append(model).append("&sign=").append(sign);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				final String conResult = conn.getConnectionResult();
				hr.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.cancel();
						pd = null;
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
									StringBuffer title = new StringBuffer(type);
									title.append(
											listFts.get(spFt
													.getSelectedItemPosition()))
											.append(model);
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
	}

	private void getEqType() {
		pd = App.progressDialog(context,
				context.getResources().getString(R.string.common_response));
		pd.show();
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
						+ eqType);
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("DataChildren.do?userid=").append(userid)
						.append("&tag=").append(eqType).append("&sign=")
						.append(sign);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				final String conResult = conn.getConnectionResult();
				hr.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.cancel();
						pd = null;
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
									buildEpTypeSp(resJson);
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
	}

	private void buildEpTypeSp(JSONObject resJson) throws JSONException {
		JSONArray array = resJson.getJSONArray("data");
		listType = new ArrayList<String>();
		final List<String> listVals = new ArrayList<String>();
		for (int n = 0; n < array.length(); n++) {
			JSONArray datas = array.getJSONArray(n);
			listType.add(datas.getString(1));
			listVals.add(datas.getString(0));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.item_spinner, listType);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spType.setAdapter(adapter);
		spType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> ad, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				getEqModel(listVals.get(index));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void getEqModel(final String eqVal) {
		pd = App.progressDialog(context,
				context.getResources().getString(R.string.common_response));
		pd.show();
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
						+ eqVal);
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("ProductModel.do?userid=").append(userid)
						.append("&eqtype=").append(eqVal).append("&sign=")
						.append(sign);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				final String conResult = conn.getConnectionResult();
				hr.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.cancel();
						pd = null;
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
									buildEpModel(resJson);
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
	}

	private void buildEpModel(JSONObject resJson) throws JSONException {
		JSONArray fts = resJson.getJSONArray("factory");
		listFts = new ArrayList<String>();
		for (int n = 0; n < fts.length(); n++) {
			String ft = fts.getString(n);
			listFts.add(ft);
		}
		final JSONArray models = resJson.getJSONArray("model");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.item_spinner, listFts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spFt.setAdapter(adapter);
		spFt.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> ad, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				try {
					listmodels = new ArrayList<String>();
					JSONArray array = models.getJSONArray(index);
					for (int n = 0; n < array.length(); n++) {
						String ft = array.getString(n);
						listmodels.add(ft);
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							context, R.layout.item_spinner, listmodels);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spModel.setAdapter(adapter);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void findViews() {
		spType = (Spinner) findViewById(R.id.sp_pd_type);
		spFt = (Spinner) findViewById(R.id.sp_pd_ft);
		spModel = (Spinner) findViewById(R.id.sp_pd_model);
		btnQuery = (Button) findViewById(R.id.btn_query);
		// tvTitle = (TextView) findViewById(R.id.txt_title);
	}
}
