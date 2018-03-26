package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.com.chinaccs.datasite.main.R;

public class ContentListAdapter extends BaseAdapter  
{  
    LayoutInflater inflater;  
    private String[] content;  
    private int count;  
  
    public ContentListAdapter(Context context, String[] content)  
    {  
        this.content = content;  
        this.count = content.length;  
        inflater = LayoutInflater.from(context);  
    }  
      
    @Override  
    public int getCount()  
    {  
        return count;  
    }  
  
    public void setCount(int count)  
    {  
        if (count > content.length)  
            this.count = content.length;  
        else if (count > 0)  
            this.count = count;  
    }  
  
    @Override  
    public Object getItem(int position)  
    {  
        return content[position];  
    }  
  
    @Override  
    public long getItemId(int arg0)  
    {  
        return 0;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent)  
    {   
        if (convertView == null)  
            convertView = inflater.inflate(R.layout.item_elist_children, null);
        TextView txtView = (TextView) convertView.findViewById(R.id.txt_item_children);
        txtView.setText(content[position]);  
        return convertView;  
    }  
} 
