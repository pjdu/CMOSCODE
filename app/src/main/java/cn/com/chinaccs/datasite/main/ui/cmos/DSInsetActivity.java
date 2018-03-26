package cn.com.chinaccs.datasite.main.ui.cmos;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.datasite.function.ContentListAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.FuncServerInset;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

public class DSInsetActivity extends Activity {
	Context context;
	private Button btnBs;
	private Button btnCell;
	private ListView lvWifi;
	private String ci;
	private String sid;
	private String bsId;
	private String bsName = "";
	private String btsid = "";
	private String bsc = "";
	private String cellId;
	private String cellName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ds_inset);
		this.context = this;
		this.findViews();
		ci = AppCDMABaseStation.getCi(context);
		sid = AppCDMABaseStation.getCDMASid(context);
		this.buildBs();
		btnBs.setOnClickListener(lr);
		btnCell.setOnClickListener(lr);
		this.buildWifi();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		bsId = null;
		cellId = null;
		btsid = null;
		bsc = null;
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
			case R.id.btn_insetbs:
				if (bsId != null) {
					Bundle be = new Bundle();
					be.putString("id", bsId);
					be.putString("name", bsName);
					be.putString("btsid", btsid);
					be.putString("bsc", bsc);
					Intent bsi = new Intent(context,
							DSCdmaBSContentActivity.class);
					bsi.putExtras(be);
					startActivity(bsi);
				} else {
					Toast.makeText(context, "暂无数据", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.btn_insetcell:
				if (cellId != null) {
					Bundle be = new Bundle();
					be.putString("id", cellId);
					be.putString("name", cellName);
					be.putString("ci", ci);
					Intent bsi = new Intent(context,
							DSCdmaCellContentAcitivity.class);
					bsi.putExtras(be);
					startActivity(bsi);
				} else {
					Toast.makeText(context, "暂无数据", Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
			}
		}
	};

	private void buildBs() {
		btnBs.setText("暂无数据");
		btnCell.setText("暂无数据");
		final ProgressDialog pd = App.progressDialog(context, getResources()
				.getString(R.string.common_request));
		pd.show();
		FuncServerInset func = new FuncServerInset(context);
		OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

			@Override
			public void onFinished(String output) {
				// TODO Auto-generated method stub
				pd.dismiss();
				if (output.equals("fail")) {
					Toast.makeText(
							context,
							getResources().getString(
									R.string.common_not_network),
							Toast.LENGTH_LONG).show();
					return;
				}
				try {
					JSONObject json = new JSONObject(output);
					String result = json.getString("result");
					String msg = json.getString("msg");
					if (result.equals("1")) {
						JSONArray abs = json.getJSONArray("bs");
						bsId = abs.getString(0);
						bsName = abs.getString(1);
						btsid = abs.getString(2);
						bsc = abs.getString(3);
						btnBs.setText(bsName);
						JSONArray acell = json.getJSONArray("cell");
						cellId = acell.getString(0);
						cellName = acell.getString(1);
						btnCell.setText(cellName);
					} else {
						Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		func.insetQuery(lr, ci, sid);
	}

	private void buildWifi() {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean isEnabled = wm.isWifiEnabled();
		if (!isEnabled) {
			Toast.makeText(context, "手机WIFI功能处于关闭状态！", Toast.LENGTH_LONG)
					.show();
			return;
		}
		final List<ScanResult> scanResults = wm.getScanResults();
		if (scanResults != null && scanResults.size() > 0) {
			int size = scanResults.size();
			String[] ssids = new String[size];
			for (int i = 0; i < size; i++) {
				ScanResult scan = scanResults.get(i);
				ssids[i] = scan.SSID;
			}
			ContentListAdapter adapter = new ContentListAdapter(context, ssids);
			lvWifi.setAdapter(adapter);
			lvWifi.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int index, long arg3) {
					// TODO Auto-generated method stub
					showWifiDialog(scanResults.get(index));
				}
			});
		}
	}

	private void showWifiDialog(ScanResult scan) {
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setNegativeButton("关闭", null).create();
		dialog.setTitle("wifi详细信息");
		StringBuffer msg = new StringBuffer("ssid：");
		msg.append(scan.SSID).append("\nmac地址：").append(scan.BSSID)
				.append("\n信号强度：").append(scan.level).append("dBm\n频率：")
				.append(scan.frequency).append("MHz\n加密方式：")
				.append(scan.capabilities);
		dialog.setMessage(msg.toString());
		dialog.show();
	}

	private void findViews() {
		btnBs = (Button) findViewById(R.id.btn_insetbs);
		btnCell = (Button) findViewById(R.id.btn_insetcell);
		lvWifi = (ListView) findViewById(R.id.list_inset_wifi);
	}
}
