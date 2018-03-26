package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;

public class DSLTEListAdapter extends BaseAdapter {
	private List<JSONArray> list;
	private Context context;

	public DSLTEListAdapter(Context context, List<JSONArray> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater flater = LayoutInflater.from(context);
		convertView = flater.inflate(R.layout.item_list_dscommon, null);
		try {
			JSONArray data = list.get(position);
			String name = data.getString(1);
			String field1 = data.getString(2);
			String field2 = data.getString(3);
			TextView txtName = (TextView) convertView
					.findViewById(R.id.txt_item_name);
			txtName.setText(name);
			TextView txtField1 = (TextView) convertView
					.findViewById(R.id.txt_item_field1);
			txtField1.setText(field1);
			TextView txtField2 = (TextView) convertView
					.findViewById(R.id.txt_item_field2);
			txtField2.setText(field2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

}
