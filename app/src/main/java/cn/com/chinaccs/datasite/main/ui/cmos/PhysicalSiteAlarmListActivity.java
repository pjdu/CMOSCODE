package cn.com.chinaccs.datasite.main.ui.cmos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetSignBsInfo;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.adapters.PhysicalSiteAdapter;
import cn.com.chinaccs.datasite.main.ui.models.PhysicalSiteBean;
import cn.com.chinaccs.datasite.main.widget.LoadToast;

/**
 * 建设功能个流程列表显示
 * Created by Asky on 2016/3/31.
 */
public class PhysicalSiteAlarmListActivity extends BaseActivity {


    private static final String TAG = PhysicalSiteAlarmListActivity.class.getSimpleName();
    private Context context;
    private JSONArray datas;

    private RecyclerView mRecyclerView;
    private PhysicalSiteAdapter mAdapter;
    private List<PhysicalSiteBean> mList;

    private final String text = "加载中....";
    private LoadToast mLoadToast;
    // 当前坐标信息
    private double mLongitude;
    private double mLatitude;
    private LatLng mCurrentLoc;
    // 当前区域
    private String mArea;
    private String mCounty;
    private int moveDist = 4;

    // 告警查询
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_site_list);
        context = PhysicalSiteAlarmListActivity.this;
        initToolbar("告警信息列表");
        // 前环节
        // 获取当前百度地图位置
        mLongitude = getIntent().getDoubleExtra("longitude", 0);
        mLatitude = getIntent().getDoubleExtra("latitude", 0);
        mArea = getIntent().getStringExtra("area");
        mCounty = getIntent().getStringExtra("county");
        if (mLongitude != 0 || mLatitude != 0) {
            mCurrentLoc = new LatLng(mLongitude, mLatitude);
        }
        this.findViews();
        mLoadToast = new LoadToast(this).setProgressColor(Color.BLUE).setBackgroundColor(Color.WHITE).setText(text).setTranslationY(360);
        mList = new ArrayList<>();
        mAdapter = new PhysicalSiteAdapter(context, mList);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        getMapLoc(mCurrentLoc, mArea, mCounty);
        mAdapter.setOnItemClickListener(new PhysicalSiteAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, PhysicalSiteBean data) {
                Intent toShowAlarmInfo = new Intent(PhysicalSiteAlarmListActivity.this, PhysicalSiteAlarmInfoActivity.class);
                toShowAlarmInfo.putExtra("BsId", data.getBsId());
                startActivity(toShowAlarmInfo);
            }
        });
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.physical_asset_new_list);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startBsQuery(String name) {
        if (name.equals("")){
            return;
        }
        if (mList == null){
            return;
        }
        List<PhysicalSiteBean> queryReults = new ArrayList<>();
        for (PhysicalSiteBean bean : mList) {
            String title = bean.getTitle();
            if (title.contains(name)){
                queryReults.add(bean);
            }
        }
        mAdapter.setData(queryReults);
    }

    /**
     * 百度坐标和GPS坐标转换在很近的距离时偏差非常接近。
     * 假设你有百度坐标：x1=116.397428，y1=39.90923
     * 把这个坐标当成GPS坐标，通过接口获得他的百度坐标：x2=116.41004950566，y2=39.916979519873
     * 通过计算就可以得到GPS的坐标：
     * x = 2*x1-x2，y = 2*y1-y2
     * x=116.38480649434001
     * y=39.901480480127
     *
     * @param sourceLatLng
     */
    private void getMapLoc(final LatLng sourceLatLng, final String blindArea, final String blindCounty) {
        if (sourceLatLng != null) {
            //将百度坐标转为GPS坐标
            //把百度坐标当成一个GPS坐标
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(sourceLatLng);
            //模拟纠偏得到d百度地图坐标
            LatLng desLatLng = converter.convert();
            // Log.d(App.LOG_TAG, "longitude: " + desLatLng.longitude + "  latitude: " + desLatLng.latitude);
            double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
            double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
            LatLng centerLatLng = new LatLng(latitude, longitude);
            getBSMapInfo(centerLatLng, blindArea, blindCounty);

        }
    }

    private void getBSMapInfo(final LatLng latLng, final String blindArea, final String blindCounty) {
        mLoadToast.show();
        final OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                if (output.equals("fail")) {
                    mLoadToast.error();
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    try {
                        JSONObject json = new JSONObject(output);
                        String result = json.getString("result");
                        mList.clear();
                        if (result.equals("-1")) {
                            Toast.makeText(context, json.getString("msg"),
                                    Toast.LENGTH_LONG).show();
                            mLoadToast.error();
                        } else {
                            datas = json.getJSONArray("data");
                            scene1:
                            for (int i = 0; i < datas.length(); ++i) {
                                JSONArray temp = null;
                                try {
                                    temp = datas.getJSONArray(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String bsId = temp.getString(0);
                                String bsAlarmType = temp.getString(6);
                                for (PhysicalSiteBean bean : mList) {
                                    if (bsId.equals(bean.getBsId())) {
                                        if (isNeededRemovePreMarker(bean.getBsAlarmType(), bsAlarmType)) {
                                            // Logger.d(bean.toString());
                                            mList.remove(bean);
                                            break;
                                        } else {
                                            // Logger.d(bs.toString());
                                            continue scene1;
                                        }
                                    }
                                }
                                PhysicalSiteBean item = null;
                                try {
                                    String note;
                                    if (bsAlarmType.equals("critical")) {
                                        ALog.json(temp.toString());
                                        note = "地址:" + temp.getString(4) + "\n站址类型:" + temp.getString(5) + "\n告警等级:" + "严重警告" + "\n告警内容:" + temp.getString(7) + "\n基站类型:" + temp.getString(8);
                                        item = new PhysicalSiteBean(bsId, bsAlarmType, temp.getString(1), note, temp.getString(9), R.drawable.ic_event_white_24dp, PhysicalSiteBean.getBgColor(context, bsAlarmType));
                                        mList.add(item);
                                    } else if (bsAlarmType.equals("major")) {
                                        note = "地址:" + temp.getString(4) + "\n站址类型:" + temp.getString(5) + "\n告警等级:" + "主要警告" + "\n告警内容:" + temp.getString(7) + "\n基站类型:" + temp.getString(8);
                                        item = new PhysicalSiteBean(bsId, bsAlarmType, temp.getString(1), note, temp.getString(9), R.drawable.ic_event_white_24dp, PhysicalSiteBean.getBgColor(context, bsAlarmType));
                                        mList.add(item);
                                    } else if (bsAlarmType.equals("minor")) {
                                        note = "地址:" + temp.getString(4) + "\n站址类型:" + temp.getString(5) + "\n告警等级:" + "次要告警" + "\n告警内容:" + temp.getString(7) + "\n基站类型:" + temp.getString(8);
                                        item = new PhysicalSiteBean(bsId, bsAlarmType, temp.getString(1), note, temp.getString(9), R.drawable.ic_event_white_24dp, PhysicalSiteBean.getBgColor(context, bsAlarmType));
                                        mList.add(item);
                                    } else if (bsAlarmType.equals("warning")) {
                                        note = "地址:" + temp.getString(4) + "\n站址类型:" + temp.getString(5) + "\n告警等级:" + "一般告警" + "\n告警内容:" + temp.getString(7) + "\n基站类型:" + temp.getString(8);
                                        item = new PhysicalSiteBean(bsId, bsAlarmType, temp.getString(1), note, temp.getString(9), R.drawable.ic_event_white_24dp, PhysicalSiteBean.getBgColor(context, bsAlarmType));
                                        mList.add(item);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mLoadToast.success();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.setData(mList);
            }
        };
        FuncGetSignBsInfo func = new FuncGetSignBsInfo(context);
        func.getData(lr, moveDist, String.valueOf(latLng.longitude), String.valueOf(latLng.latitude), blindArea, blindCounty);
    }

    /**
     * 判断两个基站的告警等级
     *
     * @param addedBs
     * @param currentBs
     * @return 是否要重新绘制基站
     */
    private boolean isNeededRemovePreMarker(String addedBs, String currentBs) {
        String[] alarmTypes = {"warning", "minor", "major", "critical"};
        int addedIndex = find(alarmTypes, addedBs);
        int currentIndex = find(alarmTypes, currentBs);
        // Logger.d(addedIndex + "------------------" + currentIndex);
        return currentIndex > addedIndex;
    }

    public int find(String[] arr, String str) {
        boolean flag = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(str)) {
                flag = true;
                return i;
            }
        }
        if (flag == false) {
            return -1;
        }
        return -1;
    }
}
