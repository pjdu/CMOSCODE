package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.datasite.database.DBInspectHandler;
import cn.com.chinaccs.datasite.main.datasite.database.IDBHandler;

public class PlanListAdapter extends BaseAdapter {
	Context context;
	private JSONArray array;
	private LayoutInflater flater;
	private String bsId;
	private boolean isRRu;
	private String cellId;

	public PlanListAdapter(Context context, JSONArray array, String bsId,
			Boolean isRRu, String cellId) {
		this.context = context;
		this.array = array;
		flater = LayoutInflater.from(context);
		this.bsId = bsId;
		this.isRRu = isRRu;
		this.cellId = cellId;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.length();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		try {
			return array.get(arg0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		try {
			return array.getJSONArray(position).getLong(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null)
			convertView = flater.inflate(R.layout.item_list_plan, null);
		TextView tv = (TextView) convertView.findViewById(R.id.tv_item_plan);
		ImageView imgState = (ImageView) convertView
				.findViewById(R.id.img_plan_state);
		try {
			tv.setText(array.getJSONArray(position).getString(1));
			String planId = array.getJSONArray(position).getString(0);
			if (isPlaned(planId)) {
				imgState.setVisibility(View.VISIBLE);
			} else {
				imgState.setVisibility(View.INVISIBLE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

	private boolean isPlaned(String planId) {
		boolean isPlan = false;
		DBInspectHandler dbh = new DBInspectHandler(context,
				IDBHandler.MODE_READ_DATABASE);
		isPlan = dbh.dbCheckIsPlaned(isRRu, bsId, cellId, planId);
		dbh.closeDataBase();
		return isPlan;
	}
}
