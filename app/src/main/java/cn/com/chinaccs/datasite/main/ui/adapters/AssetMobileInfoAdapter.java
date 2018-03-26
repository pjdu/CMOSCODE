/*
 * Created by AndyHua on 2017/10/26.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-26 08:59:45.
 */

package cn.com.chinaccs.datasite.main.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;

/*
 * 资产盘点
 * 资产列表信息
 */
public class AssetMobileInfoAdapter extends BaseAdapter {
    private List<JSONArray> list;
    private Context context;

    public AssetMobileInfoAdapter(Context context, List<JSONArray> list) {
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

    @SuppressLint({"ResourceAsColor", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.item_list_asset_mobile, null);
        try {
            JSONArray data = list.get(position);
            String assetsName = data.getString(1);
            String assetsNum = data.getString(2);
            String assetsAddr = data.getString(3);
            String assetsArea = data.getString(4);
            TextView txtName = (TextView) convertView
                    .findViewById(R.id.res_ass_code);
            txtName.setText("站址名称:" + assetsName);
            TextView txtNum = (TextView) convertView
                    .findViewById(R.id.res_ass_name);
            txtNum.setText("站址编号:" + assetsNum);
            TextView txtAddr = (TextView) convertView
                    .findViewById(R.id.res_addr);
            txtAddr.setText("详细地址:" + assetsAddr);
            TextView txtIns = (TextView) convertView
                    .findViewById(R.id.txt_assets_ins);
            /*txtIns.setText("未盘点");
            txtIns.setTextColor(Color.RED);*/

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
