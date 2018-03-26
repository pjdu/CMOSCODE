/*
 * Created by AndyHua on 2017/10/26.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-26 11:20:08.
 */

package cn.com.chinaccs.datasite.main.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;

public class AssetOpticalConInfoAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private List<JSONArray> list;
    private int count;

    public AssetOpticalConInfoAdapter(Context context, List<JSONArray> list) {
        this.list = list;
        this.count = list.size();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (count > list.size())
            this.count = list.size();
        else if (count > 0)
            this.count = count;
    }

    @Override
    public JSONArray getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_list_asset_optical_con, null);
        TextView nameView = (TextView) convertView.findViewById(R.id.txt_item_name);
        TextView numView = (TextView) convertView.findViewById(R.id.txt_item_num);
        JSONArray temp = list.get(position);
        try {
            nameView.setText(temp.getString(0));
            numView.setText(temp.getString(1));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
} 
