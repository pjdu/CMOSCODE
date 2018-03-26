package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import cn.com.chinaccs.datasite.main.R;

/**
 * 上次填写作业计划内容列表
 * <p>
 * Created by Asky on 15/10/13.
 */
public class InspectInfoViewFactory {

    private Context context;

    public InspectInfoViewFactory(Context context) {
        this.context = context;
    }

    public boolean addView(JSONArray array, LinearLayout layout) {
        boolean show = true;
        LayoutInflater lit = LayoutInflater.from(context);
        View view = lit.inflate(R.layout.item_list_plan_new_info, null);
        TextView tvEq = (TextView) view.findViewById(R.id.tv_eq);
        TextView tvSubeq = (TextView) view.findViewById(R.id.tv_subeq);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_itcontent);
        CheckBox cb0 = (CheckBox) view.findViewById(R.id.cb_cok_0);
        CheckBox cb1 = (CheckBox) view.findViewById(R.id.cb_cok_1);
        try {
            tvEq.setText(array.getString(1));
            tvSubeq.setText(array.getString(2));
            tvContent.setText(array.getString(3));
            String state = array.getString(8);
            if (state.equals("0")){
                cb0.setChecked(true);
            } else {
                cb1.setChecked(true);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        layout.addView(view);
        return show;
    }
}
