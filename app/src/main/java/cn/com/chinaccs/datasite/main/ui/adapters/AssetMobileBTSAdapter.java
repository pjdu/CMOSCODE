/*
 * Created by AndyHua on 2017/10/26.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-26 11:02:24.
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
 * 资产列表信息
 */
public class AssetMobileBTSAdapter extends BaseAdapter {
    private List<JSONArray> list;
    private Context context;

    public AssetMobileBTSAdapter(Context context, List<JSONArray> list) {
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
        convertView = inflater.inflate(R.layout.item_list_asset_mobile_bts, null);
        try {
            JSONArray data = list.get(position);
            String btsName = data.getString(11);
            String assetsNo = data.getString(32);
            String serialId = data.getString(37);
            String assetsAddr = data.getString(3);
            String city = data.getString(12);
            TextView txtName = (TextView) convertView
                    .findViewById(R.id.res_ass_code);
            txtName.setText("基站名称:" + btsName);
            TextView txtNum = (TextView) convertView
                    .findViewById(R.id.res_ass_name);
            txtNum.setText("资产卡片编号:" + assetsNo);
            TextView txtSerialId = (TextView) convertView
                    .findViewById(R.id.res_code);
            txtSerialId.setText("电子序列号:" + serialId);
            TextView txtAddr = (TextView) convertView
                    .findViewById(R.id.res_addr);
            txtAddr.setText("详细地址:" + assetsAddr);
            TextView txtIns = (TextView) convertView
                    .findViewById(R.id.txt_assets_ins);
            if (assetsNo.equals("") || serialId.equals("")) {
                txtIns.setText("待盘点");
                txtIns.setTextColor(Color.parseColor("#C03033"));
            } else {
                txtIns.setText("已盘点");
                txtIns.setTextColor(Color.parseColor("#79A96F"));
            }
            TextView txtArea = (TextView) convertView
                    .findViewById(R.id.res_city);
            txtArea.setText(city);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }

}
