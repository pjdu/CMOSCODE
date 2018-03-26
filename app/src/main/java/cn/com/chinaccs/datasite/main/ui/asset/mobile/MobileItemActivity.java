/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 10:56:41.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.datasite.function.TableAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.TableAdapter.TableCell;
import cn.com.chinaccs.datasite.main.datasite.function.TableAdapter.TableRow;

/*
 * 三码融合
 * 资产信息条目项信息
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
public class MobileItemActivity extends BaseActivity {

    private Context context;
    private String id;
    private String title;
    private int[] ps;
    private String datas;
    private TextView tvTitle;
    private ListView lvList;
    private Button btnEdit;
    private Button btnClose;
    private JSONArray array;
    private String name;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_asset_mobile_item);
        Bundle be = getIntent().getExtras();
        id = be.getString("id");
        title = be.getString("title");
        initToolbar(title);
        ps = be.getIntArray("ps");
        name = be.getString("name");
        datas = be.getString("json");
        try {
            array = new JSONArray(datas);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.findViews();
        //tvTitle.setText(title);
        this.buildItem();
        btnEdit.setOnClickListener(ol);
        btnClose.setOnClickListener(ol);
    }

    private OnClickListener ol = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_as_edit:
                    updateData();
                    break;
                case R.id.btn_as_close:
                    ((Activity) context).finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void updateData() {
        Bundle be = new Bundle();
        be.putInt("datatype", DataSiteStart.TYPE_ASSETS);
        be.putString("id", id);
        be.putString("title", title);
        be.putString("name", name);
        be.putString("array", array.toString());
        be.putIntArray("ps", ps);
        Intent i = new Intent(context, MobileModifyActivity.class);
        i.putExtras(be);
        startActivityForResult(i, 0);
    }

    private void buildItem() {
        try {
            int start = ps[0];
            int end = ps[1];
            ArrayList<TableRow> table = new ArrayList<>();
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
            lvList.setAdapter(tableAdapter);
        } catch (NumberFormatException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            ((Activity) context).setResult(RESULT_OK);
        ((Activity) context).finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        lvList = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void findViews() {
        //tvTitle = (TextView) findViewById(R.id.tv_as_title);
        lvList = (ListView) findViewById(R.id.lv_item_list);
        btnEdit = (Button) findViewById(R.id.btn_as_edit);
        btnClose = (Button) findViewById(R.id.btn_as_close);

    }

}
