package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetCellOption;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * 非独立站点时，跳转到该页面
 */
public class InspectCellOptionActivity extends Activity {
	private Context context;
	private Button btnNext;
	private ListView lvInsCell;
	private String bsId;
	private String bsName;
	private String bsLongitude;
	private String bsLatitude;
	private String bsBtsId;
	private String bsBsc;
	private String[] cellNames;
	private String[] cellIds;
	private String[] cellCis;
	private String[] cellLongitudes;
	private String[] cellLatitudes;
	private String cellLongitude;
	private String cellLatitude;
	private String cellName;
	private String cellId;
	private String cellCi;
	private Boolean isRRU;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_inspect_cell);
		Bundle be = getIntent().getExtras();
		bsId = be.getString("bsId");
		bsName = be.getString("bsName");
		bsLongitude = be.getString("longitude");
		bsLatitude = be.getString("latitude");
		bsBtsId = be.getString("bsBtsId");
		bsBsc = be.getString("bsBsc");
		isRRU = false;
		this.findViews();
		getCellData();
		btnNext.setOnClickListener(clr);
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

	private OnClickListener clr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_next:
				Bundle be = new Bundle();
				be.putString("bsId", bsId);
				be.putString("bsName", bsName);
				be.putBoolean("isRRU", isRRU);
				if (isRRU == true) {
					be.putString("cellId", cellId);
					be.putString("cellName", cellName);
					be.putString("cellCi", cellCi);
					be.putString("longitude", cellLongitude);
					be.putString("latitude", cellLatitude);
				} else {
					be.putString("longitude", bsLongitude);
					be.putString("latitude", bsLatitude);
				}
				Intent i = new Intent(context, InspectPlanActivity.class);
				i.putExtras(be);
				startActivity(i);
				break;
			default:
				break;
			}
		}
	};

	private void getCellData() {
		final ProgressDialog pd = App.progressDialog(context, getResources()
				.getString(R.string.common_request));
		pd.show();
		FuncGetCellOption func = new FuncGetCellOption(context);
		OnGetDataFinishedListener glr = new OnGetDataFinishedListener() {

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
					// String msg = json.getString("msg");
					if (result.equals("1")) {
						JSONArray data = json.getJSONArray("data");
						cellIds = new String[data.length()];
						cellNames = new String[data.length() + 1];
						cellCis = new String[data.length()];
						cellNames[0] = "非RRU巡检";
						cellLongitudes = new String[data.length()];
						cellLatitudes = new String[data.length()];
						for (int i = 0; i < data.length(); i++) {
							cellIds[i] = data.getJSONArray(i).getString(0);
							cellNames[i + 1] = data.getJSONArray(i)
									.getString(1);
							cellCis[i] = data.getJSONArray(i).getString(2);
							cellLongitudes[i] = data.getJSONArray(i).getString(
									3);
							cellLatitudes[i] = data.getJSONArray(i)
									.getString(4);
						}
					} else {
						Toast.makeText(context, "此基站无RRU", Toast.LENGTH_LONG)
								.show();
						cellNames = new String[1];
						cellNames[0] = "非RRU巡检";
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							context, R.layout.item_list_single_choice,
							cellNames);
					lvInsCell.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					lvInsCell.setAdapter(adapter);
					lvInsCell.setItemChecked(0, true);
					lvInsCell.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		func.getData(glr, bsBtsId, bsBsc);

		lvInsCell.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					isRRU = false;
					cellId = null;
					cellName = null;
					cellCi = null;
					cellLongitude = null;
					cellLatitude = null;
				} else {
					isRRU = true;
					cellId = cellIds[arg2 - 1];
					cellName = cellNames[arg2];
					cellCi = cellCis[arg2 - 1];
					cellLongitude = cellLongitudes[arg2 - 1];
					cellLatitude = cellLatitudes[arg2 - 1];
				}
			}

		});
	}

	private void findViews() {
		btnNext = (Button) findViewById(R.id.btn_next);
		lvInsCell = (ListView) findViewById(R.id.lv_bsInspectCell);
	}
}
