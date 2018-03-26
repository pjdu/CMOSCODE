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

/**
 * Created by AndyHua on 15/8/16.
 */
public class NewPlanListAdapter extends BaseAdapter {
    Context context;
    private JSONArray array;
    private LayoutInflater flater;
    private String bsId;
    private boolean isRRu;
    private String cellId;

    public NewPlanListAdapter(Context context, JSONArray array, String bsId,
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
	public String getState(int position) {
		// TODO Auto-generated method stub
		try {
			return array.getJSONArray(position).getString(2);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "1";
		}
	}

    /**
     *
     * @param position
     * @return
     */
    public String getPlanId(int position){
        try{
            return array.getJSONArray(position).getString(4);
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null)
            convertView = flater.inflate(R.layout.item_list_plan_new, null);
        TextView tv = (TextView) convertView.findViewById(R.id.tv_item_plan);
        ImageView imgState = (ImageView) convertView
                .findViewById(R.id.img_plan_state);
        TextView planTime = (TextView) convertView.findViewById(R.id.planTime);
        try {
            tv.setText(array.getJSONArray(position).getString(1));
            planTime.setText("上次巡检时间:" + array.getJSONArray(position).getString(3));
            String state=getState(position);
            if (state.equals("0")) {
				imgState.setVisibility(View.VISIBLE);
			} else {
                planTime.setVisibility(View.GONE);
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
