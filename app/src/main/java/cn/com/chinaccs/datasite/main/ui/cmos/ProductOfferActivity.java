package cn.com.chinaccs.datasite.main.ui.cmos;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;

public class ProductOfferActivity extends BaseActivity {
	private Context context;
	private Button btnSub;
	private EditText etOffer;
	private Long productId;
	private int datatype;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_product_offer);
		Bundle be = getIntent().getExtras();
		productId = be.getLong("productId");
		datatype = be.getInt("datatype");
		this.findViews();
		initToolbar(getResources().getString(R.string.pd_offer));
		btnSub.setOnClickListener(subLr);
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

	private OnClickListener subLr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String content = etOffer.getText().toString().trim();
			if (content != null && content.length() > 0) {
				AlertDialog ad = App.alertDialog(context, "提示！", "确定提交数据？",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								// TODO Auto-generated method stub
								saveDatas();
							}
						}, null);
				ad.show();
			} else{
				Toast.makeText(context, "评论不能为空！", Toast.LENGTH_LONG).show();
			}
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
			array.put(etOffer.getText());
			SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
			output.put("userid",
					share.getString(AppCheckLogin.SHARE_USER_ID, ""));
			output.put("productId", productId);
			output.put("datatype", datatype);
			output.put("data", array);
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
		etOffer = (EditText) findViewById(R.id.txt_pd_offer);
		btnSub = (Button) findViewById(R.id.btn_submit);
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
				((Activity) context).setResult(RESULT_OK);
				Toast.makeText(context, "数据提交成功 ！", Toast.LENGTH_LONG).show();
				((Activity) context).finish();
				break;
			case 500:
				Toast.makeText(context, "连接失败：请检查网络连接！", Toast.LENGTH_LONG)
						.show();
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

}
