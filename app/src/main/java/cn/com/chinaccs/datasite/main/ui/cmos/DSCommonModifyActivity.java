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
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.datasite.function.ModifyViewFactory;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.ui.MainApp;


public class DSCommonModifyActivity extends Activity {
	private Context context;
	private LinearLayout layout;
	private Button btnSub;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_ds_common_modify);
		gpsHandler = ((MainApp) getApplication()).gpsHandler;
		Bundle be = getIntent().getExtras();
		String json = be.getString("array");
		name = be.getString("name");
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
					return;
				}
				array.put(et.getText());
			}
			SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
			output.put("userid",
					share.getString(AppCheckLogin.SHARE_USER_ID, ""));
			output.put("id", id);
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
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
				String vtype = data.getString(1);
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
