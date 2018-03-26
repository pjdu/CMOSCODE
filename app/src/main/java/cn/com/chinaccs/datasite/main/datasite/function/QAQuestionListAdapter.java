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

public class QAQuestionListAdapter extends BaseAdapter {
	
	private List<JSONArray> list;
	private Context context;
	
	public QAQuestionListAdapter(Context context, List<JSONArray> list) {
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
		convertView = flater.inflate(R.layout.item_question_list, null);
		try {
			JSONArray data = list.get(position);
			String name = data.getString(1); 
			String userName = data.getString(5);
			String time = data.getString(3);
			String count = data.getString(4);
			String statues = data.getString(6);
			
			TextView txtName = (TextView)convertView.findViewById(R.id.question_describ);
			         txtName.setText(name);
			TextView txtContent = (TextView)convertView.findViewById(R.id.question_time);
			         txtContent.setText(time);
			TextView txtUserId = (TextView)convertView.findViewById(R.id.user_name);
			         txtUserId.setText(userName);;
			TextView txtCount = (TextView)convertView.findViewById(R.id.anwser_count);
			         txtCount.setText("回答次数："+count);
			View statuesView = (View)convertView.findViewById(R.id.question_statues);
			if (statues.equals("3")) {
				statuesView.setBackgroundResource(R.drawable.quetion_ygb);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return convertView;
	}

}
