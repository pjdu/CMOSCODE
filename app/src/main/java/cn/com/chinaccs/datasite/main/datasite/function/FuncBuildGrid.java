package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;

public class FuncBuildGrid {
	private Context context;
	private GridView gv;

	public FuncBuildGrid(Context context, GridView gv) {
		this.context = context;
		this.gv = gv;
	}

	public void buildGrid(int[] items, int[] imgs) {
		if (items == null || items.length <= 0) {
			Log.d(App.LOG_TAG, "grid内项目为零");
			return;
		}
		ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < items.length; i++) {
			map = new HashMap<String, Object>();
			map.put("ItemImage", imgs[i]);
			map.put("ItemText", context.getResources().getString(items[i]));
			meumList.add(map);
		}
		SimpleAdapter saMenuItem = new SimpleAdapter(context, meumList, // 数据源
				R.layout.item_grid, // xml实现
				new String[] { "ItemImage", "ItemText" }, // 对应map的Key
				new int[] { R.id.image_griditem, R.id.text_griditem }); // 对应R的Id

		// 添加Item到网格中
		gv.setAdapter(saMenuItem);
	}

	public void attachEvent(final Intent[] intents) {
		OnItemClickListener gridClt = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> ad, View v, int index,
					long arg3) {
				// TODO Auto-generated method stub
				// Bundle be = new Bundle();
				// be.putInt("itemId", index);
				// intents[index].putExtra("itemId", index);
				Log.e(App.LOG_TAG, index + "------------------");
				try {
					if (intents[index] != null) {
						Log.e(App.LOG_TAG, "------------------" + index);
						context.startActivity(intents[index]);
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.e(App.LOG_TAG, "------------------" + e.toString() + "-------------------");
					Toast.makeText(context, "抱歉，链接已失效！", Toast.LENGTH_LONG)
							.show();
				}
			}
		};
		gv.setOnItemClickListener(gridClt);
	}
}
