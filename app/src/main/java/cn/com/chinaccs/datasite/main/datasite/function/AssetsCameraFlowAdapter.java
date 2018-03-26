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
import cn.com.chinaccs.datasite.main.db.dao.DAOAssetsPhoto;
import cn.com.chinaccs.datasite.main.db.model.AssetsPhotosModel;

public class AssetsCameraFlowAdapter extends BaseAdapter {
	private List<Map<String, String>> list;
	private LayoutInflater inflater;
	private Context context;
	private String dsId;
	
	public AssetsCameraFlowAdapter(Context context, List<Map<String, String>> list,String id) {
		this.context = context;
		this.list = list;
		this.dsId = id;
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
		TextView tvState = (TextView) convertView.findViewById(R.id.tv_item_camera_state);
		Map<String, String> data = list.get(position);
		String name = data.get("desc");
		String subType = data.get("subType");
		List<AssetsPhotosModel> list = DAOAssetsPhoto.getInstance(context).searchAssetsSubType(dsId, subType, false);
		tv.setText(name);
		String url = data.get("imgUrl");
		if (url == null || url.equals("")) {
			img.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_star_off32));
		} else {
			img.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_star_upload));
			tvState.setText("已有上传照片");
		}
		if(list!=null && list.size()>0){
			img.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_star32));
			tvState.setText("缓存未提交");
		}
		return convertView;
	}
}
