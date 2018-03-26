package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;

public class NewBSListAdapter extends BaseAdapter  
{  
	LayoutInflater inflater;  
	private List<String> content;  

	public NewBSListAdapter(Context context, List<String> content)  
	{  
		this.content = removeDuplicate(content);  
		inflater = LayoutInflater.from(context);
	}  

	@Override  
	public int getCount()  
	{  
		return content.size();
	}  

	public void setContent(List<String> content){
		this.content = removeDuplicate(content);
		notifyDataSetChanged();
	}
//	public void setCount(int count)
//	{
//		if (count > content.size())
//			this.count = content.size();
//		else if (count > 0)
//			this.count = count;
//	}

	@Override  
	public Object getItem(int position)  
	{  
		return content.get(position);  
	}  

	@Override  
	public long getItemId(int arg0)  
	{  
		return 0;  
	}  

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("content", ""+content.size());

		if (convertView == null)
			convertView = inflater.inflate(R.layout.item_elist_children, null);
		TextView txtView = (TextView) convertView.findViewById(R.id.txt_item_children);
		txtView.setText(content.get(position));
		return convertView;
	}  
	//删除重复项
	public List<String>  removeDuplicate(List list)   { 
		for  ( int  i  =   0 ; i  <  list.size()  -   1 ; i ++ )   { 
			for  ( int  j  =  list.size()  -   1 ; j  >  i; j -- )   { 
				if  (list.get(j).equals(list.get(i)))   { 
					list.remove(j); 
				} 
			} 
		} 
		return list;
	}
} 
