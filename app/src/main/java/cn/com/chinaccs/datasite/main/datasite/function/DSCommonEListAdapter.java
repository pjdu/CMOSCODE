package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import cn.com.chinaccs.datasite.main.R;

public class DSCommonEListAdapter extends BaseExpandableListAdapter {
	private String[] groups;
	private String[][] children;
	private LayoutInflater inflater;

	public DSCommonEListAdapter(Context context, String[] groups,
			String[][] children) {
		this.groups = groups;
		this.children = children;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return this.children[groupPosition][childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_elist_children, null);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.txt_item_children);
		textView.setText(String.valueOf(getChild(groupPosition, childPosition)));
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return this.children[groupPosition].length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groups[groupPosition];
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_elist_group, null);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.txt_item_group);
		textView.setText(String.valueOf(getGroup(groupPosition)));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
}
