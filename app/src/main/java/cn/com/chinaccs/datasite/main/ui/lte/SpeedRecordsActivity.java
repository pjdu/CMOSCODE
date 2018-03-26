package cn.com.chinaccs.datasite.main.ui.lte;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.XFastFactory.XDbUtils.XDbUtils;
import com.XFastFactory.XJson.XJsonUtils;
import com.XFastFactory.XTask.XSimpleTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.chinaccs.datasite.main.adapter.InfoRecordsAdapter;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.bean.InfoSpeedRecords;
import cn.com.chinaccs.datasite.main.bean.SpeedRecordsComparator;

import cn.com.chinaccs.datasite.main.R;



/**
 * Created by andyhua on 15-4-28.
 */
public class SpeedRecordsActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "SpeedTestRecords";

    Context context;
    private ListView listView;
    private InfoRecordsAdapter adapter;
    private List<InfoSpeedRecords> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 显示标题
        /*getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.speed_records));*/
        setContentView(R.layout.activity_speed_records);
        this.initToolbar(getResources().getString(R.string.speed_result));
        this.findViews();
        this.context = SpeedRecordsActivity.this;
        adapter = new InfoRecordsAdapter(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        new XSimpleTask(){

            @Override
            public Object onTask(Object... objects) {
                list = getInfoSpeedRecords();
                Log.d("SpeedRecordsActivity","list赋值");
                return null;
            }

            @Override
            public void onComplete(Object o) {
                SpeedRecordsComparator comparator = new SpeedRecordsComparator();
                Collections.sort(list, comparator);
                adapter.setData(list);
                Log.d("SpeedRecordsActivity","传入list数据");
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

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void findViews() {
       listView = (ListView) findViewById(R.id.speed_records_list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            InfoSpeedRecords item = (InfoSpeedRecords) adapter.getItem(position);
            Intent intent = new Intent(SpeedRecordsActivity.this, SpeedRecordsDetailActivity.class);
            intent.putExtra("data", XJsonUtils.toJson(item).toString());
            startActivity(intent);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ArrayList<InfoSpeedRecords> getInfoSpeedRecords() {
        XDbUtils db = new XDbUtils(this.context);
        ArrayList<InfoSpeedRecords> infos = new ArrayList<InfoSpeedRecords>();
        try {
            db.createTable(InfoSpeedRecords.class);
            List<Object> objects = db.query(null, null, InfoSpeedRecords.class, null);
            for (int i = 0; i < objects.size(); ++i) {
                InfoSpeedRecords info = (InfoSpeedRecords) objects.get(i);
                infos.add(info);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }
}
