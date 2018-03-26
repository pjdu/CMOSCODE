package cn.com.chinaccs.datasite.main.ui.cmos;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.adapters.PhysicalSiteAdapter;
import cn.com.chinaccs.datasite.main.ui.functions.FuncGetPhysicalSiteAlarmInfo;
import cn.com.chinaccs.datasite.main.ui.models.PhysicalSiteBean;
import cn.com.chinaccs.datasite.main.widget.LoadToast;

/**
 * Created by asky on 16-6-13.
 */
public class PhysicalSiteAlarmInfoActivity extends BaseActivity {

    private Context mContext;
    private String bsId;
    private JSONArray datas;

    private RecyclerView mRecyclerView;
    private PhysicalSiteAdapter mAdapter;
    private List<PhysicalSiteBean> mList;

    private final String text = "加载中....";
    private LoadToast mLoadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_site_alarm);

        this.mContext = this;

        initToolbar("告警详情");
        Bundle bundle = getIntent().getExtras();
        bsId = getIntent().getExtras().getString("BsId");
        this.findViews();
        mLoadToast = new LoadToast(this).setProgressColor(Color.BLUE).setBackgroundColor(Color.WHITE).setText(text).setTranslationY(360);
        mList = new ArrayList<>();
        mAdapter = new PhysicalSiteAdapter(mContext, mList);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        this.buildHistoryData();
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.physical_site_list);
    }

    private void buildHistoryData() {
        mLoadToast.show();
        FuncGetPhysicalSiteAlarmInfo func = new FuncGetPhysicalSiteAlarmInfo(mContext);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                try {
                    initFourGFindNoOrBadHistoryItems(output);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        func.getData(lr, bsId);
    }

    private void initFourGFindNoOrBadHistoryItems(String output) throws JSONException {
        Log.i(CoConfig.LOG_TAG, output);
        if (output.equals(AppHttpConnection.RESULT_FAIL)) {
            Toast.makeText(mContext,
                    getResources().getString(R.string.common_not_network),
                    Toast.LENGTH_LONG).show();
            mLoadToast.error();
        } else {
            ALog.json(output);
            JSONObject json = new JSONObject(output);
            String result = json.getString("result");
            mList.clear();
            if (result.equals("-1")) {
                Toast.makeText(mContext, json.getString("msg"),
                        Toast.LENGTH_LONG).show();
                mLoadToast.error();
            } else {
                datas = json.getJSONArray("data");
                for (int i = 0; i < datas.length(); ++i) {
                    JSONArray temp = null;
                    try {
                        temp = datas.getJSONArray(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PhysicalSiteBean item = null;
                    try {
                        String alarmType = temp.getString(1);
                        String alarmTypeString;
                        if (alarmType.equals("critical")) {
                            alarmTypeString = "严重警告";
                        } else if (alarmType.equals("major")) {
                            alarmTypeString = "主要警告";
                        } else if (alarmType.equals("minor")) {
                            alarmTypeString = "次要告警";
                        } else if (alarmType.equals("warning")) {
                            alarmTypeString = "一般告警";
                        } else {
                            alarmTypeString = "无告警";
                        }
                        String note = "告警等级:" + alarmTypeString + "\n告警内容:" + temp.getString(2) + "\n基站类型:" + temp.getString(4);
                        item = new PhysicalSiteBean(temp.getString(0), note, temp.getString(3), R.drawable.ic_event_white_24dp, PhysicalSiteBean.getBgColor(mContext, alarmType));
                        mList.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mLoadToast.success();
            }
            mAdapter.setData(mList);
        }
    }
}
