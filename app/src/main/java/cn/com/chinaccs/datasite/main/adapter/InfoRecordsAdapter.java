package cn.com.chinaccs.datasite.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.bean.InfoSpeedRecords;


/**
 * Created by andyhua on 15-4-28.
 */
public class InfoRecordsAdapter extends BaseAdapter {
    private static final String TAG = "InfoRecordsAdapter";
    private Context context;
    private List<InfoSpeedRecords> items;
    private LayoutInflater inflater;

    public InfoRecordsAdapter(Context context) {
        this.items = new ArrayList<InfoSpeedRecords>();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<InfoSpeedRecords> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addData(InfoSpeedRecords item) {
        this.items.add(item);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_speed_records, null);
            holder.netType = (TextView) convertView.findViewById(R.id.conn_type);
            holder.fpTime = (TextView) convertView.findViewById(R.id.time_text_fp);
            holder.spTime = (TextView) convertView.findViewById(R.id.time_text_sp);
            holder.downSpeed = (TextView) convertView.findViewById(R.id.down_text);
            holder.upSpeed = (TextView) convertView.findViewById(R.id.up_text);
            holder.pingDelay = (TextView) convertView.findViewById(R.id.ping_delay);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.netType.setText(items.get(position).networkType);
        String[] times = items.get(position).testTime.split(" ");
        holder.fpTime.setText(times[0]);
        holder.spTime.setText(times[1]);
        holder.downSpeed.setText(items.get(position).speedDown);
        holder.upSpeed.setText(items.get(position).speedUp);
        holder.pingDelay.setText(items.get(position).pingDelay);
        return convertView;
    }

    private static class ViewHolder{
        public TextView netType;
        public TextView fpTime;
        public TextView spTime;
        public TextView downSpeed;
        public TextView upSpeed;
        public TextView pingDelay;
    }
}
