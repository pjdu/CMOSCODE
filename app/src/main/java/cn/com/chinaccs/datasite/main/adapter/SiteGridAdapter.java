package cn.com.chinaccs.datasite.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.bean.WebSite;


public class SiteGridAdapter extends BaseAdapter {
	private List<WebSite> listWs;
	Context context;
	private LayoutInflater flater;
	Integer[] icMarks = { R.drawable.ic_mark_fail,
			R.drawable.ic_mark_hyperslow, R.drawable.ic_mark_slow,
			R.drawable.ic_mark_fast, R.drawable.ic_mark_furious };

	public SiteGridAdapter(Context context, List<WebSite> listWs) {
		this.context = context;
		this.listWs = listWs;
		flater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listWs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listWs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return listWs.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null)
			convertView = flater.inflate(R.layout.item_grid_site, null);
		ImageView imgSite = (ImageView) convertView.findViewById(R.id.img_site);
		ImageView imgSt = (ImageView) convertView.findViewById(R.id.img_st);
		ImageView imgMark = (ImageView) convertView.findViewById(R.id.img_mark);
		ProgressBar pd = (ProgressBar) convertView.findViewById(R.id.pb_site);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tvSpeed = (TextView) convertView.findViewById(R.id.tv_speed);
		WebSite site = listWs.get(position);
		String state = site.getState();
		imgSite.setImageResource(site.getIconRes());
		tvTitle.setText(site.getName());
		if (site.isChecked()) {
			imgSt.setImageResource(R.drawable.ic_selected_green);
		} else {
			imgSt.setImageResource(R.drawable.ic_deselected);
		}
		if (state.equals(WebSite.STATE_WAIT)) {
			pd.setVisibility(View.INVISIBLE);
			imgMark.setVisibility(View.INVISIBLE);
			tvSpeed.setVisibility(View.INVISIBLE);
		} else if (state.equals(WebSite.STATE_TEST)) {
			pd.setVisibility(View.VISIBLE);
		} else if (state.equals(WebSite.STATE_FINISH)) {
			pd.setVisibility(View.INVISIBLE);
			imgMark.setVisibility(View.VISIBLE);
			tvSpeed.setVisibility(View.VISIBLE);
			imgMark.setImageResource(icMarks[site.getClassify()]);
			float speed = site.getSpeed();
			if (speed > 1024) {
				BigDecimal bdl = new BigDecimal((speed / 1024));
				speed = bdl.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				tvSpeed.setText(speed + "Mbps");
			} else {
				tvSpeed.setText(speed + "Kbps");
			}
		} else if (state.equals(WebSite.STATE_ERROR)) {
			pd.setVisibility(View.INVISIBLE);
			imgMark.setVisibility(View.VISIBLE);
			tvSpeed.setVisibility(View.INVISIBLE);
			imgMark.setImageResource(icMarks[0]);
		}
		return convertView;
	}

}
