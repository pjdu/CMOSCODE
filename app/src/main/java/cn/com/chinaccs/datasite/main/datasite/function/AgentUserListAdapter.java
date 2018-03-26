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

public class AgentUserListAdapter extends BaseAdapter {
	private List<JSONArray> list;
	private Context context;

	public AgentUserListAdapter(Context context, List<JSONArray> list) {
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
			String sex = data.getString(5);
			String field1 = data.getString(2);
			String field2 = data.getString(4);
			String cer = "";	//资格证书
			String phoneNum = "";
			TextView txtName = (TextView) convertView
					.findViewById(R.id.txt_item_name);
			txtName.setText("姓名：" + name + ",  性别：" + sex);
			TextView txtField1 = (TextView) convertView
					.findViewById(R.id.txt_item_field1);
			txtField1.setText("代维公司：" + field1);
			TextView txtField2 = (TextView) convertView
					.findViewById(R.id.txt_item_field2);
			txtField2.setText("区域：" + field2);
			
			for (int i = 7; i <= 15; i++) {
				if (!data.getString(i).equals(null) && !data.getString(i).equals("")) {
					cer += data.getString(i) + ", ";
				}
			}
			if(cer.trim().equals(null) || cer.trim().equals("")) {
				cer = "无";
			}
			TextView txtCer = (TextView) convertView
					.findViewById(R.id.txt_item_cer);
			txtCer.setText("资格证书：" + cer );
			
			TextView txtPhoneNum = (TextView) convertView
					.findViewById(R.id.txt_item_num);
			for (int i = 16; i <= 19; i ++) {
				if (!data.getString(i).equals(null) && !data.getString(i).equals("")) {
					phoneNum += data.getString(i) + ", ";
				}
			}
			if (phoneNum.trim().equals("") ) {
				txtPhoneNum.setVisibility(View.GONE);
			} else {
				txtPhoneNum.setVisibility(View.VISIBLE);
				txtPhoneNum.setText("联系电话：" + phoneNum);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

}
