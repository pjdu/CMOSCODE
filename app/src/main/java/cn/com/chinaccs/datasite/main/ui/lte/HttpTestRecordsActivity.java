package cn.com.chinaccs.datasite.main.ui.lte;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.XFastFactory.XDbUtils.XDbUtils;
import com.XFastFactory.XTask.XSimpleTask;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.adapter.HttpTestRecordsAdapter;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecords;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecordsComparator;


/**
 * Created by andyhua on 15-6-14.
 */
public class HttpTestRecordsActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "HttpTestRecordsActivity";

    Context context;
    private ListView listView;
    private HttpTestRecordsAdapter adapter;
    private List<HttpTestRecords> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 显示标题
        /*getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.httptest_records));*/
        setContentView(R.layout.activity_httptest_records);
        initToolbar(getResources().getString(R.string.busi_result));
        this.findViews();
        this.context = getApplicationContext();
        adapter = new HttpTestRecordsAdapter(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        new XSimpleTask() {

            @Override
            public Object onTask(Object... objects) {
                list = getHttpTestRecords();
                return null;
            }

            @Override
            public void onComplete(Object o) {
                HttpTestRecordsComparator comparator = new HttpTestRecordsComparator();
                Collections.sort(list, comparator);
                adapter.setData(list);
            }

            @Override
            public void onUpdate(Object o) {

            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        listView = (ListView) findViewById(R.id.httptest_records_list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            HttpTestRecords item = (HttpTestRecords) adapter.getItem(position);
            Intent intent = new Intent(HttpTestRecordsActivity.this, HttpTestRecordsDetailActivity.class);
            intent.putExtra("data", item.testTime);
            startActivity(intent);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ArrayList<HttpTestRecords> getHttpTestRecords() {
        XDbUtils db = new XDbUtils(this.context);
        ArrayList<HttpTestRecords> infos = new ArrayList<HttpTestRecords>();
        try {
            db.createTable(HttpTestRecords.class);
            List<Object> objects = db.query(null, null, HttpTestRecords.class, null);
            for (int i = 0; i < objects.size(); ++i) {
                HttpTestRecords info = (HttpTestRecords) objects.get(i);
                infos.add(info);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }
}
