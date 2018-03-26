package cn.com.chinaccs.datasite.main.ui.cmos;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.ProductOfferListAdapter;

/**
 * @author Fddi
 * 
 */
public class ProductOfferListActivity extends BaseActivity {
	private Context context;
	private ListView lvPM;
	private ProgressBar pbPM;
	private TextView txtState;
	private List<JSONArray> listRes;
	private Integer pagestart;
	private Integer total = 0;
	private Boolean isRequestState = false;
	private ProgressDialog proDialog;
	private ProductOfferListAdapter polAdapter;
	private String productId;
	private Button btnProposeOffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_product_offer_list);
		Bundle be = getIntent().getExtras();
		productId = be.getString("productId");

		initToolbar(getResources().getString(R.string.pd_offer));
		this.findViews();
		this.buildList();
		this.setListBottomEvent();
		btnProposeOffer.setOnClickListener(lr) ;
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK) {
			this.buildList();
		}
	}

	private OnClickListener lr = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle be = new Bundle();
			Long productID = Long.parseLong(productId);
			be.putLong("productId", productID);
			be.putInt("datatype", DataSiteStart.TYPE_PRODUCT_OFFER);
			Intent i = new Intent(context, ProductOfferActivity.class);
			i.putExtras(be);
			startActivityForResult(i, 0);
		}
		
	};

	private void buildList() {
		pagestart = 0;
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
		lvPM = (ListView) findViewById(R.id.lv_pd);
		pbPM = (ProgressBar) findViewById(R.id.pb_pd);
		txtState = (TextView) findViewById(R.id.txt_pd_state);
		btnProposeOffer = (Button) findViewById(R.id.btn_product_offer);
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
							ProductOfferListActivity.this, true);
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
		@SuppressWarnings("unused")
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);

		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + productId
				+ pagestart);
		try {
			productId = URLEncoder.encode(productId, App.ENCODE_UTF8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		url.append("ProductOffer.do?productId=").append(productId)
				.append("&pagestart=").append(pagestart).append("&sign=")
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

		private ProductOfferListActivity activity;
		private boolean isPageRequest;

		public PmHandler(Activity activity, boolean isPageRequest) {
			this.activity = (ProductOfferListActivity) activity;
			this.isPageRequest = isPageRequest;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 200:
				if (!isPageRequest) {
					activity.polAdapter = null;
					activity.polAdapter = new ProductOfferListAdapter(activity,
							activity.listRes);
					activity.lvPM.setVisibility(View.VISIBLE);
					activity.txtState.setVisibility(View.GONE);
					activity.lvPM.setAdapter(activity.polAdapter);
				} else {
					if (activity.polAdapter != null)
						activity.polAdapter.notifyDataSetChanged();
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
