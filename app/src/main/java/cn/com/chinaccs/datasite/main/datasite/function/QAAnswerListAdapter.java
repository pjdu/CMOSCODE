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

public class QAAnswerListAdapter extends BaseAdapter{
	
	private List<JSONArray> list;
	private Context context;
	
	public QAAnswerListAdapter(Context context, List<JSONArray> list) {
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
		convertView = flater.inflate(R.layout.item_question_anwser_list, null);
		try {
			JSONArray data = list.get(position);
			String answer = data.getString(1);
			String userName = data.getString(5);
			String time = data.getString(3);
			String status = data.getString(4);
			
			TextView answerTextView = (TextView)convertView.findViewById(R.id.question_answer);
			         answerTextView.setText(answer);
			TextView useridTextView = (TextView)convertView.findViewById(R.id.question_answer_userId);
			         useridTextView.setText(userName);
			TextView timeTextView = (TextView)convertView.findViewById(R.id.question_answer_createTime);
			         timeTextView.setText(time);
			View view = (View)convertView.findViewById(R.id.best_bad_other_img);
			TextView bestBadOther = (TextView)convertView.findViewById(R.id.best_bad_other_status);
			
			if ((Integer.parseInt(status) == 2)) {
			         view.setVisibility(View.VISIBLE);
			         view.setBackgroundResource(R.drawable.ic_question_best_answer);
			         bestBadOther.setVisibility(View.VISIBLE);
			         bestBadOther.setText("最佳答案");
			}
			else if ((Integer.parseInt(status) == 3)) {
			         view.setVisibility(View.VISIBLE);
			         view.setBackgroundResource(R.drawable.ic_question_bad_answer);
			         bestBadOther.setVisibility(View.VISIBLE);
			         bestBadOther.setText("最酱油答案");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return convertView;
	}

}
