package cn.com.chinaccs.datasite.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecords;


/**
 * Created by andyhua on 15-6-11.
 */
public class HttpTestRecordsAdapter extends BaseAdapter {
    private static final String TAG = "InfoRecordsAdapter";
    private Context context;
    private List<HttpTestRecords> items;
    private LayoutInflater inflater;

    public HttpTestRecordsAdapter(Context context) {
        this.items = new ArrayList<HttpTestRecords>();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<HttpTestRecords> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addData(HttpTestRecords item) {
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
            convertView = inflater.inflate(R.layout.item_httptest_records, null);
            holder.fpTime = (TextView) convertView.findViewById(R.id.time_text_fp);
            holder.spTime = (TextView) convertView.findViewById(R.id.time_text_sp);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.avgFirDelay = (TextView) convertView.findViewById(R.id.avg_first_delay);
            holder.avgFullDelay = (TextView) convertView.findViewById(R.id.avg_full_time);
            holder.avgSpeed = (TextView) convertView.findViewById(R.id.avg_speed);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            date = sdf.parse(items.get(position).testTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String testTime = sdf.format(date.getTime());
        String[] times = testTime.split(" ");
        holder.fpTime.setText(times[0]);
        holder.spTime.setText(times[1]);
        holder.address.setText(items.get(position).testAddr);
        holder.avgFirDelay.setText(items.get(position).avgFirDefer);
        holder.avgFullDelay.setText(items.get(position).avgFuDefer);
        holder.avgSpeed.setText(items.get(position).avgSpeed);
        return convertView;
    }

    private static class ViewHolder {
        public TextView fpTime;
        public TextView spTime;
        public TextView address;
        public TextView avgFirDelay;
        public TextView avgFullDelay;
        public TextView avgSpeed;
    }
}
