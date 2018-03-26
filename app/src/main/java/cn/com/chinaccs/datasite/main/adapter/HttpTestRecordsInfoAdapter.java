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
import cn.com.chinaccs.datasite.main.bean.HttpTestRecordsInfo;


/**
 * Created by andyhua on 15-6-14.
 */
public class HttpTestRecordsInfoAdapter extends BaseAdapter {
    private static final String TAG = "HttpTestRecordsInfoAdapter";
    private Context context;
    private List<HttpTestRecordsInfo> items;
    private LayoutInflater inflater;

    public HttpTestRecordsInfoAdapter(Context context) {
        this.items = new ArrayList<HttpTestRecordsInfo>();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<HttpTestRecordsInfo> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addData(HttpTestRecordsInfo item) {
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
            convertView = inflater.inflate(R.layout.item_httptest_records_info, null);
            holder.http_effective_url = (TextView) convertView.findViewById(R.id.test_host);
            holder.testTime = (TextView) convertView.findViewById(R.id.test_time_text);
            holder.testAddr = (TextView) convertView.findViewById(R.id.test_address_text);
            holder.http_response_code = (TextView) convertView.findViewById(R.id.status_code_text);
            holder.http_namelookup_time = (TextView) convertView.findViewById(R.id.analysis_time_text);
            holder.http_connect_time = (TextView) convertView.findViewById(R.id.connection_time_text);
            holder.http_pretransfer_time = (TextView) convertView.findViewById(R.id.request_time_text);
            holder.http_starttransfer_time = (TextView) convertView.findViewById(R.id.response_time_text);
            holder.http_first_delay = (TextView) convertView.findViewById(R.id.first_package_time_text);
            holder.http_total_time = (TextView) convertView.findViewById(R.id.finish_time_text);
            holder.siteType = (TextView) convertView.findViewById(R.id.sense_level_text);
            holder.http_primary_ip = (TextView) convertView.findViewById(R.id.server_ip_text);
            holder.http_local_ip = (TextView) convertView.findViewById(R.id.local_ip_text);
            holder.http_size_download = (TextView) convertView.findViewById(R.id.download_file_size);
            holder.http_speed_download = (TextView) convertView.findViewById(R.id.download_speed_text);
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

        holder.http_effective_url.setText(items.get(position).http_effective_url);
        holder.testTime.setText(testTime);
        holder.testAddr.setText(items.get(position).testAddr);
        holder.http_response_code.setText(items.get(position).http_response_code);
        holder.http_namelookup_time.setText(items.get(position).http_namelookup_time);
        holder.http_connect_time.setText(items.get(position).http_connect_time);
        holder.http_pretransfer_time.setText(items.get(position).http_pretransfer_time);
        holder.http_starttransfer_time.setText(items.get(position).http_starttransfer_time);
        holder.http_first_delay.setText(items.get(position).http_first_delay);
        holder.http_total_time.setText(items.get(position).http_total_time);
        holder.siteType.setText(items.get(position).siteType);
        holder.http_primary_ip.setText(items.get(position).http_primary_ip);
        holder.http_local_ip.setText(items.get(position).http_local_ip);
        holder.http_size_download.setText(items.get(position).http_size_download);
        holder.http_speed_download.setText(items.get(position).http_speed_download);
        return convertView;
    }

    private static class ViewHolder {
        // 测试时间
        public TextView testTime;
        // 测试地址
        public TextView testAddr;

        public TextView http_effective_url;
        public TextView http_response_code;
        public TextView http_namelookup_time;
        public TextView http_connect_time;
        public TextView http_pretransfer_time;
        public TextView http_starttransfer_time;
        public TextView http_first_delay;
        public TextView http_total_time;
        public TextView siteType;
        public TextView http_primary_ip;
        public TextView http_local_ip;
        public TextView http_size_download;
        public TextView http_speed_download;
    }
}
