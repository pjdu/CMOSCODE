package cn.com.chinaccs.datasite.main.ui.lte;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.XFastFactory.XDbUtils.XDbUtils;
import com.XFastFactory.XTask.XSimpleTask;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.adapter.HttpTestRecordsInfoAdapter;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecordsInfo;

/**
 * Created by andyhua on 15-6-14.
 */
public class HttpTestRecordsDetailActivity extends BaseActivity {
    private static final String TAG = "HttpTestRecordsDetailActivity";

    Context context;
    private ListView listView;
    private HttpTestRecordsInfoAdapter adapter;
    private List<HttpTestRecordsInfo> list;
    private String testTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 显示标题
      /*  getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.httptest_records_detail));*/
        setContentView(R.layout.activity_httptest_records_detail);
        initToolbar(getResources().getString(R.string.site_test_records));
        this.findViews();
        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.containsKey("data")) {
            try {
                testTime = extra.getString("data");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        this.context = getApplicationContext();
        adapter = new HttpTestRecordsInfoAdapter(context);
        listView.setAdapter(adapter);
        new XSimpleTask() {

            @Override
            public Object onTask(Object... objects) {
                list = getInfoHttpTestRecords();
                return null;
            }

            @Override
            public void onComplete(Object o) {
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
        listView = (ListView) findViewById(R.id.httptest_records_info_list);
    }

    private ArrayList<HttpTestRecordsInfo> getInfoHttpTestRecords() {
        XDbUtils db = new XDbUtils(this.context);
        ArrayList<HttpTestRecordsInfo> infos = new ArrayList<HttpTestRecordsInfo>();
        try {
            db.createTable(HttpTestRecordsInfo.class);
            List<Object> objects = db.query(null, "testTime=" + testTime, HttpTestRecordsInfo.class, null);
            for (int i = 0; i < objects.size(); ++i) {
                HttpTestRecordsInfo info = (HttpTestRecordsInfo) objects.get(i);
                infos.add(info);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }
}

