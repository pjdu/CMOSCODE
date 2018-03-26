package cn.com.chinaccs.datasite.main.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.bean.Collector;
import cn.com.chinaccs.datasite.main.util.AppScaffolding;

/**
 * 数据显示list适配器
 *
 * @author fddi
 */
public class WCAdapter extends BaseAdapter {
    private Context context;
    private List<Collector> list;

    public WCAdapter(Context context, List<Collector> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }

    /**
     * update data
     *
     * @param list
     */
    public void setList(List<Collector> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    /**
     * @param collector
     */
    public void addData(Collector collector) {
        if (this.list != null) {
            this.list.add(collector);
        }
        notifyDataSetChanged();
    }

    public void removeData(int location) {
        if (this.list != null) {
            this.list.remove(location);
        }
        notifyDataSetChanged();
    }

    /**
     * clear list
     */
    public void clearList() {
        if (this.list != null) {
            this.list.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater flater = LayoutInflater.from(context);
        convertView = flater.inflate(R.layout.item_list_info, null);
        Collector data = list.get(position);
        TextView txtName = (TextView) convertView
                .findViewById(R.id.tv_info_title);
        TextView txtValue = (TextView) convertView
                .findViewById(R.id.tv_info_value);
        Integer type = data.getDataType();
        String value = data.getDataValue() + data.getDataUnit();
        if (type != null
                && AppScaffolding.isBinaryInclude(type,
                Collector.DATA_TYPE_TITLE)) {
            txtName.setGravity(Gravity.CENTER);
            txtName.getPaint().setFakeBoldText(true);
            txtValue.setVisibility(View.GONE);
        }
        if (type != null
                && AppScaffolding.isBinaryInclude(type,
                Collector.DATA_TYPE_FULLLINE)) {
            txtName.setVisibility(View.GONE);
            txtValue.setGravity(Gravity.LEFT);
            if (!data.getDataName().equalsIgnoreCase("")) {
                value = "[" + data.getDataName() + "] " + value;
            }
        }
        if (type != null
                && AppScaffolding.isBinaryInclude(type,
                Collector.DATA_TYPE_LIST)) {
            txtValue.setGravity(Gravity.LEFT);
            List<Collector> list_c = data.getValueList();
            for (Collector c : list_c) {
                value += c.getDataName() + ":" + c.getDataValue()
                        + c.getDataUnit() + "\n";
            }
        }
        if (txtName.isEnabled())
            txtName.setText(data.getDataName());
        txtValue.setText(value);
        int id = data.getGroupId();
        convertView.setBackgroundResource(R.color.white);
        if (id % 2 == 0) {
            convertView.setBackgroundResource(R.color.blue3);
        }
        return convertView;
    }

}
