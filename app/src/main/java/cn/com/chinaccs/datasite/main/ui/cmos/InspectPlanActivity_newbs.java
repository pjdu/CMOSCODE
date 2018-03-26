package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetPlanList;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.PlanListAdapter;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * @author Fddi
 * 选择巡检作业计划Activity
 */
public class InspectPlanActivity_newbs extends Activity {
	private Context context;
	private TextView tvBs;
	private ListView lvPlan;
	private Long planId;
	private JSONArray arrayData;
	private PlanListAdapter ad;
	private Bundle be;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspect_plan);
		this.context = this;
		this.findViews();
		be = getIntent().getExtras();
		this.buildList();
		boolean isRRu = be.getBoolean("isRRU");
		//根据是否是独立站点设置界面头及右侧图片
		if (isRRu) {
			String bsName = be.getString("cellName");
			tvBs.setText("当前RRU：");
			tvBs.append(bsName);
			tvBs.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_rru_on), null);
		} else {
			String rruName = be.getString("bsName");
			tvBs.setText("当前基站：");
			tvBs.append(rruName);
			tvBs.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_rru_off), null);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		arrayData = null;
		ad = null;
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
		if (ad != null) {
			ad.notifyDataSetChanged();
		}
	}

	private OnItemClickListener ilr = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
								long arg3) {
			// TODO Auto-generated method stub
			if (ad != null) {
				planId = ad.getItemId(position);
				be.putLong("planId", planId);
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
		FuncGetPlanList func = new FuncGetPlanList(context);
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
					String msg = json.getString("msg");
					if (result.equals("1")) {
						arrayData = json.getJSONArray("data");
						String bsId = be.getString("bsId");
						Boolean isRRu = be.getBoolean("isRRU");
						String cellId = "";
						if (isRRu)
							cellId = be.getString("cellId");
						ad = new PlanListAdapter(context, arrayData, bsId,
								isRRu, cellId);
						lvPlan.setAdapter(ad);
						lvPlan.setOnItemClickListener(ilr);
					} else {
						Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		func.getData(glr);
	}

	private void findViews() {
		tvBs = (TextView) findViewById(R.id.tv_bsname);
		lvPlan = (ListView) findViewById(R.id.lv_plan);
	}

}
