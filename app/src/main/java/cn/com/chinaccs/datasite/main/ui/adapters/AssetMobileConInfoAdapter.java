/*
 * Created by AndyHua on 2017/10/26.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-26 10:20:03.
 */

package cn.com.chinaccs.datasite.main.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.com.chinaccs.datasite.main.R;

public class AssetMobileConInfoAdapter extends BaseAdapter
{
    LayoutInflater inflater;
    private String[] content;
    private int count;

    public AssetMobileConInfoAdapter(Context context, String[] content)
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
            convertView = inflater.inflate(R.layout.item_list_asset_mobile_con, null);
        TextView txtView = (TextView) convertView.findViewById(R.id.txt_item_children);
        txtView.setText(content[position]);  
        return convertView;  
    }  
} 
