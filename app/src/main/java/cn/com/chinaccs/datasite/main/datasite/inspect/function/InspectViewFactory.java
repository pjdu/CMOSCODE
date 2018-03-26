package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;

/**
 * @author Fddi
 * 
 */
public class InspectViewFactory {
	private Context context;

	public InspectViewFactory(Context context) {
		this.context = context;
	}

	public boolean addView(JSONArray array, LinearLayout layout,
			List<PlanItem> list) {
		boolean show = true;
		LayoutInflater lit = LayoutInflater.from(context);
		View view = lit.inflate(R.layout.item_form_it, null);
		TextView tvEq = (TextView) view.findViewById(R.id.tv_eq);
		TextView tvSubeq = (TextView) view.findViewById(R.id.tv_subeq);
		TextView tvContent = (TextView) view.findViewById(R.id.tv_itcontent);
		CheckBox cb = (CheckBox) view.findViewById(R.id.cb_cok);
		EditText et = (EditText) view.findViewById(R.id.et_it_content);
		TableRow trCb = (TableRow) view.findViewById(R.id.tr_form_check);
		TableRow trInput = (TableRow) view.findViewById(R.id.tr_form_input);
		try {
			final PlanItem pi = new PlanItem();
			pi.setPlanItemId(array.getString(0));
			tvEq.setText(array.getString(1));
			tvSubeq.setText(array.getString(2));
			tvContent.setText(array.getString(3));
			String formType = array.getString(6);
			if (formType.equals("1")) {
				trCb.setVisibility(View.VISIBLE);
				trInput.setVisibility(View.GONE);
				pi.setState("0");
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							pi.setState("1");
						} else {
							pi.setState("0");
						}
					}
				});
			} else if (formType.equals("2")) {
				trCb.setVisibility(View.GONE);
				trInput.setVisibility(View.VISIBLE);
				pi.setState("1");
				pi.setEt(et);
			}
			list.add(pi);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		layout.addView(view);
		return show;
	}
}
