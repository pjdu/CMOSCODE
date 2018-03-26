package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetHiddenPlanList;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.TroublePlanListAdapter;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.db.model.BaseStation;
import cn.com.chinaccs.datasite.main.db.model.CurrentCell;

/**
 * 隐患列表Activity
 */
public class TroubleInfoActivity extends BaseActivity {
	private Context context;
	private TextView tvBs;
	private ListView lvPlan;
	private Long planId;
	private Long troubleId;
	private JSONArray arrayData;
	private TroublePlanListAdapter ad;
	private BaseStation bs;
	private CurrentCell cell;
	private Boolean isRRu;
	private Bundle be;
	//图片地址
	String[] strArray = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.troubleinfo);
		initToolbar("隐患作业列表");
		this.context = this;
		this.findViews();
		be= getIntent().getExtras();
		isRRu=be.getBoolean("isRRU");

		boolean isRRu = be.getBoolean("isRRU");
		//根据是否是独立站点设置界面头及右侧图片
		if (isRRu) {
			cell = (CurrentCell) be.get("cell");
			tvBs.setText("当前RRU：");
			tvBs.append(cell.getiCellName());
			tvBs.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_rru_on), null);
		} else {
			bs = (BaseStation) be.get("bs");
			tvBs.setText("当前基站：");
			tvBs.append(bs.getBsNames());
			tvBs.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_rru_off), null);
		}
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
			if (ad != null) {
				planId = ad.getPlanId(position);
				troubleId = ad.getTroubleId(position);
				be.putString("bsName", bs.getBsNames());
				be.putLong("planId", planId);
				be.putLong("troubleId", troubleId);
				be.putBoolean("isHidden", true);
				Intent i = new Intent(context, TroubleContentActivity.class);
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
		FuncGetHiddenPlanList func = new FuncGetHiddenPlanList(context);
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
					String result = json.getString("result");
					String msg = json.getString("msg");
					if (result.equals("1")) {
						arrayData = json.getJSONArray("data");
						Log.e("arrayData", "arrayData"+arrayData.toString());
						String bsId =bs.getBsIds();
						String cellId = "";
						if (isRRu)
							cellId =cell.getiCellId();
						ad = new TroublePlanListAdapter(context, arrayData, bsId,
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
	}
}
