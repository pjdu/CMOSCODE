/*
 * LisView控件的数据原型，模拟table显示数据；
 * */

package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.chinaccs.datasite.main.R;

public class TableAdapter extends BaseAdapter {
	private Context context;
	private List<TableRow> table;

	public TableAdapter(Context context, List<TableRow> table) {
		this.context = context;
		this.table = table;
	}

	@Override
	public int getCount() {
		return table.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public TableRow getItem(int position) {
		return table.get(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TableRow tableRow = table.get(position);
		return new TableRowView(this.context, tableRow);
	}

	/**
	 * TableRowView 实现表格行的样式
	 * 
	 * 
	 */
	class TableRowView extends LinearLayout {
		public TableRowView(final Context context, TableRow tableRow) {
			super(context);
			this.setOrientation(LinearLayout.HORIZONTAL);
			for (int i = 0; i < tableRow.getSize(); i++) {// 逐个格单元添加到行
				TableCell tableCell = tableRow.getCellValue(i);
				LayoutParams layoutParams = new LayoutParams(
						tableCell.width, tableCell.height);// 按照格单元指定的大小设置空间
				TextView textCell = new TextView(context);
				textCell.setSingleLine(false);
				textCell.setPadding(3, 8, 3, 8);
				textCell.setTextSize(15);
				textCell.setGravity(Gravity.CENTER);
				if (tableCell.type == TableCell.TITLE) {
					layoutParams.setMargins(0, 1, 1, 2);// 预留空隙制造边框
					textCell.setTextColor(Color.WHITE);
					textCell.setBackgroundResource(R.drawable.bg_header);
				} else {
					layoutParams.setMargins(0, 0, 1, 2);// 预留空隙制造边框
					if (tableCell.isIfNol()) {
						textCell.setBackgroundResource(R.color.white);
					} else {
						textCell.setBackgroundResource(R.color.red_warn);
					}
					textCell.setTextColor(Color.BLACK);

				}
				textCell.setText(String.valueOf(tableCell.value));
				addView(textCell, layoutParams);
			}
			this.setBackgroundResource(R.color.green_deep);// 背景蓝色，利用空隙来实现边框
		}
	}

	/**
	 * TableRow 实现表格的行
	 * 
	 * 
	 */
	static public class TableRow {
		private TableCell[] cell;

		public TableRow(TableCell[] cell) {
			this.cell = cell;
		}

		public int getSize() {
			return cell.length;
		}

		public TableCell getCellValue(int index) {
			if (index >= cell.length)
				return null;
			return cell[index];
		}
	}

	/**
	 * TableCell 实现表格的格单元
	 * 
	 * 
	 */
	static public class TableCell {
		static public final int STRING = 0;
		static public final int TITLE = 1;
		public Object value;
		public int width;
		public int height;
		private int type;
		private boolean ifNol = true;

		public TableCell(Object value, int width, int height, int type) {
			this.value = value;
			this.width = width;
			this.height = height;
			this.type = type;
		}

		public void setIfNol(boolean ifNol) {
			this.ifNol = ifNol;
		}

		public boolean isIfNol() {
			return ifNol;
		}
	}
}