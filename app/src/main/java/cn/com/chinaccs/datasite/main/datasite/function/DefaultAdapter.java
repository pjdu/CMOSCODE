package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;

public class DefaultAdapter extends BaseAdapter {
	private List<String> list;
	private LayoutInflater flater;

	public DefaultAdapter(Context context, List<String> list) {
		this.list = list;
		flater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
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
		if (convertView == null)
			convertView = flater.inflate(R.layout.item_list_default, null);
		TextView tv = (TextView) convertView.findViewById(R.id.tv_item);
		tv.setText(list.get(position));
		return convertView;
	}

}
