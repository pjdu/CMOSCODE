package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetCellOption;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.BaseStation;
import cn.com.chinaccs.datasite.main.db.model.CurrentCell;

/**
 * 非独立站点时，跳转到该页面
 */
public class InspectCellOptionActivity_newbs extends BaseActivity {
	private Context context;
	private Button btnNext;
	private ListView lvInsCell;
//	private String bsId;
//	private String bsName;
//	private String bsLongitude;
//	private String bsLatitude;
//	private String bsBtsId;
//	private String bsBsc;
	private BaseStation bs;
	
	private List<CurrentCell> cells;
	
	private String[] cellNames;
//	private String[] cellIds;
//	private String[] cellCis;
//	private String[] cellLongitudes;
//	private String[] cellLatitudes;
	
	private CurrentCell cell;
	
//	private String cellLongitude;
//	private String cellLatitude;
//	private String cellName;
//	private String cellId;
//	private String cellCi;
	
	private Boolean isRRU;
	private final String TAG=InspectCellOptionActivity_newbs.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_inspect_cell);
		initToolbar("是否RRU巡检");
		Bundle be = getIntent().getExtras();
		bs=(BaseStation) be.get("bs");
		isRRU = false;
		this.findViews();
		getCellData();
		btnNext.setOnClickListener(clr);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private OnClickListener clr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_next:
				Bundle be = new Bundle();
				be.putSerializable("bs", bs);
				be.putBoolean("isRRU", isRRU);
				if (isRRU == true) {
					be.putSerializable("cell", cell);
				} else {
					be.putString("longitude", bs.getBsLongitude());
					be.putString("latitude", bs.getBsLatitude());
				}
				Intent i = new Intent(context, NewInspectPlanActivity.class);
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
					Log.d(TAG,json.toString());
					String result = json.getString("result");
					// String msg = json.getString("msg");
					if (result.equals("1")) {
						JSONArray data = json.getJSONArray("data");
						cellNames = new String[data.length()];
						cellNames[0] = "非RRU巡检";
						CurrentCell c;
						for (int i = 0; i < data.length(); i++) {
							c=new CurrentCell();
							c.setiCellId(data.getJSONArray(i).getString(0));
							c.setiCellName(data.getJSONArray(i)
									.getString(1));
							c.setCellCi(data.getJSONArray(i).getString(2));
							c.setiCellLongitude(data.getJSONArray(i).getString(3));
							c.setiCellLatitude(data.getJSONArray(i).getString(4));
							cells.add(c);
						}
						
						for (int i = 0; i <cells.size(); i++) {
							cellNames[i+1]=cells.get(i).getiCellName();
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
					e.printStackTrace();
				}
			}
		};
		func.getData(glr, bs.getBsBtsId(), bs.getBsBsc());

		lvInsCell.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				if (arg2 == 0) {
					isRRU = false;
					cell=null;
				} else {
					isRRU = true;
					cell=cells.get(arg2-1);
				}
			}

		});
	}

	private void findViews() {
		btnNext = (Button) findViewById(R.id.btn_next);
		lvInsCell = (ListView) findViewById(R.id.lv_bsInspectCell);
	}
}
