package cn.com.chinaccs.datasite.main.ui.cmos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.adapters.PhysicalSiteListAdapter;
import cn.com.chinaccs.datasite.main.ui.functions.FuncGetPlanSiteList;
import cn.com.chinaccs.datasite.main.ui.models.PhysicalSiteListBean;
import cn.com.chinaccs.datasite.main.ui.utils.MapUtils;
import cn.com.chinaccs.datasite.main.widget.LoadToast;

/**
 * 建设功能个流程列表显示
 * Created by Asky on 2016/3/31.
 */
public class PhysicalSiteListActivity extends BaseActivity {


    private static final String TAG = PhysicalSiteListActivity.class.getSimpleName();
    private Context context;

    private RecyclerView mRecyclerView;
    private PhysicalSiteListAdapter mAdapter;
    private List<PhysicalSiteListBean> mList;

    private final String text = "加载中....";
    private LoadToast mLoadToast;
    // 当前坐标信息
    private double mLongitude;
    private double mLatitude;
    private LatLng mCurrentLoc;
    // 基站建设所处阶段
    private int stage = 0;
    // 当前地区
    private String blindArea;
    private String blindCounty;

    // 告警查询
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_site_list);
        context = PhysicalSiteListActivity.this;
        // 前环节
        stage = getIntent().getIntExtra("stage", 0);
        if (stage == 1) {
            initToolbar("规划阶段");
        } else if (stage == 2) {
            initToolbar("交付阶段");
        } else if (stage == 3) {
            initToolbar("入网阶段");
        } else if (stage == 4) {
            initToolbar("交维阶段");
        } else if (stage == 5) {
            initToolbar("维护阶段");
        } else if (stage == 6) {
            initToolbar("网优阶段");
        } else if (stage == 7) {
            initToolbar("拆迁阶段");
        }
        // 获取当前百度地图位置
        mLongitude = getIntent().getDoubleExtra("longitude", 0);
        mLatitude = getIntent().getDoubleExtra("latitude", 0);
        if (mLongitude != 0 || mLatitude != 0) {
            mCurrentLoc = new LatLng(mLongitude, mLatitude);
        }
        blindArea = getIntent().getStringExtra("blindArea");
        blindCounty = getIntent().getStringExtra("blindCounty");

        this.findViews();
        mLoadToast = new LoadToast(this).setProgressColor(Color.BLUE).setBackgroundColor(Color.WHITE).setText(text).setTranslationY(360);
        mList = new ArrayList<>();
        mAdapter = new PhysicalSiteListAdapter(context, mList);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        getPhysicalSiteNewList(MapUtils.bdLnChangeToGpsLn(mCurrentLoc), "");
        mAdapter.setOnItemClickListener(new PhysicalSiteListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, PhysicalSiteListBean data) {
                Intent intent = null;
                if (stage == 1) {
                    // intent = new Intent(context, PhysicalSiteNewEditActivity.class);
                } else if (stage == 2) {
                    intent = new Intent(context, PhysicalSitePayEditActivity.class);
                } /*else if (stage == 3) {
                    intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                    return;
                } else if (stage == 4) {
                    intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                    return;
                } else if (stage == 5) {
                    intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                    return;
                } else if (stage == 6) {
                    intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                    return;
                } else if (stage == 7) {
                    intent = new Intent(context, PhysicalSiteChekApplyPayContentActivity.class);
                    return;
                }*/
                if (intent == null) {
                    return;
                }
                Bundle be = new Bundle();
                be.putString("name", data.getTitle());
                be.putString("id", data.getBsId());
                be.putString("code", data.getBsCode());
                be.putString("stage", data.getBsType());
                intent.putExtras(be);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.physical_asset_new_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.ab_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startBsQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    if (mList != null) {
                        mAdapter.setData(mList);
                    }
                }
                return false;
            }
        });
        return true;
    }

    private void startBsQuery(String name) {
        if (name.equals("")) {
            return;
        }
        if (mList == null) {
            return;
        }
        List<PhysicalSiteListBean> queryReults = new ArrayList<>();
        for (PhysicalSiteListBean bean : mList) {
            String title = bean.getTitle();
            if (title.contains(name)) {
                queryReults.add(bean);
            }
        }
        mAdapter.setData(queryReults);
    }

    // 查询站点规划的基站
    private void getPhysicalSiteNewList(final LatLng latLng, final String name) {
        if (latLng == null) {
            Toast.makeText(context, "无法获取到经纬度,请稍后再试....", Toast.LENGTH_LONG).show();
            return;
        }
        mLoadToast.show();
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.i(TAG, "getdata" + output);
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    ALog.json(output);
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray data = json.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONArray temp = null;
                            try {
                                temp = data.getJSONArray(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String time = temp.getString(8);
                            if (time.equals("")) {
                                time = "未知时间";
                            }
                            PhysicalSiteListBean item = null;
                            try {
                                String bsState = temp.getString(5);
                                String bsStateString = getStateString(bsState);
                                String note = "经度:" + temp.getString(2) + "\n纬度:" + temp.getString(3)
                                        + "\n地址:" + temp.getString(4) + "\n状态:" + bsStateString;
                                item = new PhysicalSiteListBean(temp.getString(0), temp.getString(1), temp.getString(6), temp.getString(5),
                                        note, time, R.drawable.ic_event_white_24dp, PhysicalSiteListBean.getBgColor(context, bsState));
                                mList.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mLoadToast.success();
                        }

                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        mLoadToast.error();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.setData(mList);
            }
        };
        FuncGetPlanSiteList func = new FuncGetPlanSiteList(context);
        func.getData(lr, name, latLng, String.valueOf(stage), blindArea, blindCounty);
    }

    private String getStateString(String type) {
        String name = "规划阶段";
        if (type.equals("1")) {
            name = "规划阶段";
        } else if (type.equals("2")) {
            name = "交付阶段";
        } else if (type.equals("3")) {
            name = "入网阶段";
        } else if (type.equals("4")) {
            name = "交维阶段";
        } else if (type.equals("5")) {
            name = "维护阶段";
        } else if (type.equals("6")) {
            name = "网优阶段";
        } else if (type.equals("7")) {
            name = "拆迁阶段";
        }
        return name;
    }
}
