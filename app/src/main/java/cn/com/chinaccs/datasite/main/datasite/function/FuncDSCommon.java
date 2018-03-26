package cn.com.chinaccs.datasite.main.datasite.function;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.chinaccs.datasite.main.datasite.function.TableAdapter.TableCell;
import cn.com.chinaccs.datasite.main.datasite.function.TableAdapter.TableRow;
import cn.com.chinaccs.datasite.main.R;

/**
 * @author Fddi
 * 
 */
public class FuncDSCommon {
	private Context context;

	public FuncDSCommon(Context context) {
		this.context = context;
	}

	public void showItems(int[] ps, JSONObject json) {
		try {
			int start = ps[0];
			int end = ps[1];
			JSONArray array = json.getJSONArray("data");
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layoutSub = li.inflate(R.layout.pop_ds_table, null);
			final PopupWindow pop = new PopupWindow(layoutSub,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			pop.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.draw_bg_opactiy));
			pop.setFocusable(true);
			pop.showAtLocation(((Activity) context).findViewById(R.id.main),
					Gravity.CENTER, 0, 0);
			ListView lvTable = (ListView) layoutSub
					.findViewById(R.id.lv_pop_submit);
			ArrayList<TableRow> table = new ArrayList<TableRow>();
			int width = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getWidth() / 2;
			TableCell[] titles = new TableCell[2];
			titles[0] = new TableCell("名称", width, LayoutParams.FILL_PARENT,
					TableCell.TITLE);
			titles[1] = new TableCell("值", width, LayoutParams.FILL_PARENT,
					TableCell.TITLE);
			table.add(new TableRow(titles));
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
				int index = data.getInt(6);
				if (index >= start && index <= end) {
					String t = data.getString(0);
					String v = data.getString(10);
					TableCell[] cells = new TableCell[2];
					cells[0] = new TableCell(t, width,
							LayoutParams.FILL_PARENT, TableCell.STRING);
					cells[1] = new TableCell(v, width,
							LayoutParams.FILL_PARENT, TableCell.STRING);
					cells[0].setIfNol(true);
					cells[1].setIfNol(true);
					table.add(new TableRow(cells));
				}
			}
			TableAdapter tableAdapter = new TableAdapter(context, table);
			lvTable.setAdapter(tableAdapter);
			Button btnTable = (Button) layoutSub
					.findViewById(R.id.btn_pop_table);
			btnTable.setText(context.getResources().getString(
					R.string.common_close));
			btnTable.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pop.dismiss();
				}
			});
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
