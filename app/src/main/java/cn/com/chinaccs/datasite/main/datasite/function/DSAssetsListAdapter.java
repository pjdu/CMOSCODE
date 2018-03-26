package cn.com.chinaccs.datasite.main.datasite.function;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;

public class DSAssetsListAdapter extends BaseAdapter {
	private List<JSONArray> list;
	private Context context;

	public DSAssetsListAdapter(Context context, List<JSONArray> list) {
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater flater = LayoutInflater.from(context);
		convertView = flater.inflate(R.layout.item_list_assets, null);
		try {
			JSONArray data = list.get(position);
			String assetsName = data.getString(1);
			String assetsNum = data.getString(2);
			String assetsAddr = data.getString(3);
			String assetsArea = data.getString(4);
			String assetsIns = data.getString(5);
			int assetsPhotos = data.getInt(6);
			if(assetsIns.equals("已清查")){
				if(assetsPhotos==0)
					assetsIns = "照片未清查";
			}
			TextView txtName = (TextView) convertView
					.findViewById(R.id.res_ass_code);
			txtName.setText("站址名称：" + assetsName);
			TextView txtNum = (TextView) convertView
					.findViewById(R.id.res_name);
			txtNum.setText("站址编号：" + assetsNum);
			TextView txtAddr = (TextView) convertView
					.findViewById(R.id.res_addr);
			txtAddr.setText("详细地址：" + assetsAddr);
			TextView txtIns = (TextView) convertView
					.findViewById(R.id.txt_assets_ins);
			txtIns.setText(assetsIns);
			
			if(assetsIns.contains("未清查")) {
				txtIns.setTextColor(Color.RED);
			} 
			TextView txtArea = (TextView) convertView
					.findViewById(R.id.res_city);
			txtArea.setText(assetsArea);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

}
