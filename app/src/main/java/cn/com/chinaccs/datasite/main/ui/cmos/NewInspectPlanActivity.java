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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetCurPlanList;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.NewPlanListAdapter;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.BaseStation;
import cn.com.chinaccs.datasite.main.db.model.CurrentCell;

/**
 * @author Fddi
 * 选择巡检作业计划Activity
 */
public class NewInspectPlanActivity extends BaseActivity {
	private Context context;
	private TextView tvBs;
	private ListView lvPlan;
	private Button sendTrouble;
	private Long planId;
	private String planedId;
	private JSONArray arrayData;
	private NewPlanListAdapter ad;
	private BaseStation bs;
	private CurrentCell cell;
	private Boolean isRRu;
	private Bundle be;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspect_plan_new);
		initToolbar("选择巡检作业计划");
		this.context = this;
		this.findViews();
		be = getIntent().getExtras();
		isRRu=be.getBoolean("isRRU");

		final boolean isRRu = be.getBoolean("isRRU");
		//根据是否是独立站点设置界面头及右侧图片
		if (isRRu) {
			cell = (CurrentCell) be.get("cell");
			bs = (BaseStation) be.get("bs");
			Log.i(App.LOG_TAG, "isRRu");
			Log.i(App.LOG_TAG, bs.toString());
			tvBs.setText("当前RRU：");
			// tvBs.append(cell.getiCellName());
			tvBs.append(bs.getBsNames());
			tvBs.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_rru_on), null);
		} else {
			Log.i(App.LOG_TAG, "isNotRRu");
			bs = (BaseStation) be.get("bs");
			Log.i(App.LOG_TAG, bs.toString());
			tvBs.setText("当前基站：");
			tvBs.append(bs.getBsNames());
			tvBs.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_rru_off), null);
		}
		sendTrouble.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle be = new Bundle();
				be.putBoolean("isRRU", isRRu);
				be.putString("bsName",bs.getBsNames());
				be.putString("bsId",bs.getBsIds());
				Intent intent = new Intent(NewInspectPlanActivity.this, SecutityHiddenActivity.class);
				intent.putExtras(be);
				startActivity(intent);
			}
		});
		this.buildList();

	}

	@Override
	protected void onDestroy() {
		arrayData = null;
		ad = null;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ad != null) {
			ad.notifyDataSetChanged();
		}
	}

	private OnItemClickListener ilr = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
								long arg3) {
			Log.e("posion", position+"");
			if (ad != null) {
				if (ad.getState(position).equals("0")) {
					// Toast.makeText(context, "已巡检", Toast.LENGTH_SHORT).show();
					if (ad.getPlanId(position) != null){
						// 作业计划ID，非计划ID
						planedId = ad.getPlanId(position);
						be.putString("planedId", planedId);
						be.putString("bsName", bs.getBsNames());
						Intent i = new Intent(context, InspectContentInfoActivity.class);
						i.putExtras(be);
						startActivity(i);
					}
					return;
				}
				// add by wuhua for
				// 用物理基站进行巡检
				// 基站巡检进去的填1，新基站巡检进去的填2
				be.putString("inspectionType", "2");
				// ended by wuhua 20151104
				planId = ad.getItemId(position);
				be.putString("bsName", bs.getBsNames());
				be.putLong("planId", planId);
				be.putString("bsId", bs.getBsIds());
				be.putString("longitude", bs.getBsLongitude());
				be.putString("latitude", bs.getBsLatitude());
				be.putString("bsBtsId", bs.getBsBtsId());
				be.putString("bsBsc", bs.getBsBsc());
				if (isRRu){
					Log.i(App.LOG_TAG, "isRRu");
					Log.i(App.LOG_TAG, bs.toString());
					be.putString("cellId", cell.getiCellId());
					be.putString("cellName", cell.getiCellName());
				}
				Log.i(App.LOG_TAG, "isNotRRu");
				Log.i(App.LOG_TAG, bs.toString());
				Intent i = new Intent(context, InspectContentActivity.class);
				i.putExtras(be);
				startActivity(i);
			} else {
				Toast.makeText(context, "初始化列表出错！", Toast.LENGTH_LONG).show();
			}
		}
	};
	//获取作业列表
	private void buildList() {
		final ProgressDialog pd = App.progressDialog(context, getResources()
				.getString(R.string.common_request));
		pd.show();
		FuncGetCurPlanList func = new FuncGetCurPlanList(context);
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
				Log.i(App.LOG_TAG, output);
				try {
					JSONObject json = new JSONObject(output);
					String result = json.getString("result");
					String msg = json.getString("msg");
					if (result.equals("1")) {
						arrayData = json.getJSONArray("data");
						String bsId =bs.getBsIds();
						String cellId = "";
						if (isRRu==true&&!(cell==null)){
							cellId =cell.getiCellId();}
						ad = new NewPlanListAdapter(context, arrayData, bsId,
								isRRu, cellId);
						lvPlan.setAdapter(ad);
						lvPlan.setOnItemClickListener(ilr);
					} else {
						//到这里非法访问
						Log.e("msg", "msg"+msg);
						Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		func.getData(glr,bs.getBsIds(),isRRu);
	}

	private void findViews() {
		tvBs = (TextView) findViewById(R.id.tv_bsname);
		lvPlan = (ListView) findViewById(R.id.lv_plan);
		sendTrouble=(Button) findViewById(R.id.sendtroublebtn);
	}

}
