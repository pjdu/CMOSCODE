package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.R;

public class CameraFlowAdapter extends BaseAdapter {
	private List<Map<String, String>> list;
	private LayoutInflater inflater;
	private Context context;

	public CameraFlowAdapter(Context context, List<Map<String, String>> list) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Map<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_grid_camera, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.tv_item_camera);
		ImageView img = (ImageView) convertView.findViewById(R.id.img_state);
		Map<String, String> data = list.get(position);
		String name = data.get("desc");
		tv.setText(name);
		String url = data.get("imgUrl");
		if (url == null || url.equals("")) {
			img.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_star_off32));
		} else {
			img.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_star32));
		}
		return convertView;
	}
}
