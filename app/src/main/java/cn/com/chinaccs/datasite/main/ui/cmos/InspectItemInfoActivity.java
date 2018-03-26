package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.DefaultAdapter;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * @author Fddi
 */
public class InspectItemInfoActivity extends BaseActivity {
    private Context context;
    private TextView tvBs;
    private ListView lvInspectItem;
    private String bsName;
    private DefaultAdapter ad;
    private Bundle be;
    private String tid;
    private List<String> list;
    private int pagestart = 0;
    private int total = 0;
    private boolean isRequestState = false;
    private String is_rru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_item_info);
        initToolbar("巡检项信息");
        this.context = this;
        this.findViews();
        be = getIntent().getExtras();
        bsName = be.getString("bsName");
        tid = be.getString("tid");
        is_rru = be.getString("is_rru");
        if (is_rru == null) {
            tvBs.setText("无法获取数据");
        } else {
            if (is_rru.equals("TRUE")) {
                tvBs.setText("RRU巡检站点：");
            } else {
                tvBs.setText("巡检基站：");
            }
        }
        if (bsName == null) {
            tvBs.append("");
        } else {
            tvBs.append(bsName);
        }
        list = new ArrayList<String>();
        this.buildList();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        list = null;
        ad = null;
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

    private void buildList() {
        final ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_request));
        pd.show();
        OnGetDataFinishedListener glr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                isRequestState = false;
                if (output.equals(AppHttpConnection.RESULT_FAIL)) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    if (ad == null) {
                        list.add(getResources().getString(
                                R.string.common_not_network));
                        ad = new DefaultAdapter(context, list);
                        lvInspectItem.setAdapter(ad);
                    }
                } else {
                    try {
                        pd.dismiss();
                        JSONObject json = new JSONObject(output);
                        String result = json.getString("result");
                        String msg = json.getString("msg");
                        if (result.equals("1")) {
                            pagestart += 10;
                            String totalStr = json.getString("total");
                            total = Integer.parseInt(totalStr);
                            JSONArray array = json.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONArray data = array.getJSONArray(i);
                                StringBuffer str = new StringBuffer();
                                str.append("【维护项目】").append(data.getString(0))
                                        .append("\n【维护子项】")
                                        .append(data.getString(1))
                                        .append("\n【巡检状态】")
                                        .append(data.getString(2))
                                        .append("\n【巡检结果】")
                                        .append(data.getString(3));
                                list.add(str.toString());
                                Log.d(App.LOG_TAG, str.toString());
                            }
                            if (ad == null) {
                                ad = new DefaultAdapter(context, list);
                                lvInspectItem.setAdapter(ad);
                                lvInspectItem.setOnScrollListener(slr);
                            } else {
                                ad.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG)
                                    .show();
                            if (ad == null) {
                                list.add(msg);
                                ad = new DefaultAdapter(context, list);
                                lvInspectItem.setAdapter(ad);
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        };
        getDatas(glr);
    }

    private void getDatas(final OnGetDataFinishedListener lr) {
//		SharedPreferences share = context
//				.getSharedPreferences(App.SHARE_TAG, 0);
//		final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + tid + pagestart);
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("InspectItemInfo.do?tid=").append(tid)
                        .append("&pagestart=").append(pagestart)
                        .append("&sign=").append(sign);
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                final String conResult = conn.getConnectionResult();
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
        }.start();
    }

    private OnScrollListener slr = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                if (total <= pagestart && pagestart != 0) {
                    // 起始页从零开始，累加10，当大于总数时说明已无数据
                    return;
                }
                if (isRequestState) {
                    // 避免重复请求，若正在请求数据时返回
                    return;
                }
                isRequestState = true;
                buildList();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub

        }
    };

    private void findViews() {
        tvBs = (TextView) findViewById(R.id.tv_inspect_bsname);
        lvInspectItem = (ListView) findViewById(R.id.lv_inspect_item);
    }

}
