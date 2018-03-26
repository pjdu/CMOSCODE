/*
 * Created by AndyHua on 2017/10/26.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-26 14:52:29.
 */

package cn.com.chinaccs.datasite.main.ui.adapters;

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

/*
 * 资产盘点
 * 局站分类列表信息
 */
public class AssetOpticalConItemAdapter extends BaseAdapter {
    private List<JSONArray> list;
    private Context context;

    public AssetOpticalConItemAdapter(Context context, List<JSONArray> list) {
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
        convertView = inflater.inflate(R.layout.item_list_asset_optical_info, null);
        try {
            JSONArray data = list.get(position);
            // ALog.json(data.toString());
            String assCode = data.getString(4);
            TextView txtAss = (TextView) convertView
                    .findViewById(R.id.res_ass_code);
            txtAss.setText("资产卡片编号:" + assCode);
            TextView siteName = (TextView) convertView
                    .findViewById(R.id.site_name);
            siteName.setText("局站名称:" + data.getString(28));
            TextView resName = (TextView) convertView
                    .findViewById(R.id.res_ass_name);
            resName.setText("资源名称:" + data.getString(7));
            TextView txtResCode = (TextView) convertView
                    .findViewById(R.id.res_code);
            txtResCode.setText("资源编码:" + data.getString(1));
            TextView resAddr = (TextView) convertView
                    .findViewById(R.id.res_addr);
            resAddr.setText("安装地点:" + data.getString(11));
            TextView txtIns = (TextView) convertView
                    .findViewById(R.id.txt_assets_ins);
            if (!assCode.equals("")) {
                txtIns.setText("已盘点");
                txtIns.setTextColor(Color.parseColor("#2ED573"));
            } else {
                txtIns.setText("未盘点");
                txtIns.setTextColor(Color.parseColor("#F45C50"));
            }
            TextView txtArea = (TextView) convertView
                    .findViewById(R.id.res_city);
            txtArea.setText(data.getString(9));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }

}
