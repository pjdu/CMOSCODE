package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.InspectContentInfoImageLoadTask;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetCurPlanInfoList;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.InspectInfoViewFactory;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * 上次作业时填写内容列表
 * Created by Asky on 15/10/10.
 */
public class InspectContentInfoActivity extends BaseActivity {
    private Context context;
    private TextView tvBs;   //显示在文件头的基站名称
    private LinearLayout layout;  //母布局，用于加入巡检内容
    private String planedId;
    private String bsname;
    private Bundle be;
    private ProgressDialog pd;
    private String userid;
    private JSONArray jsonRes;
    private int imageViewsId[] = {
            R.id.img_it_a1,
            R.id.img_it_a2
    };
    private int progressBarsId[] = {
            R.id.pb_state_0,
            R.id.pb_state_1,
            R.id.pb_state_2
    };
    private int tipStatesId[] = {
            R.id.tv_state_0,
            R.id.tv_state_1,
            R.id.tv_state_2
    };

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_plan_new_info);
        initToolbar("上次作业填写内容");
        this.context = this;
        this.findViews();
        be = getIntent().getExtras();
        planedId = be.getString("planedId");
        bsname = be.getString("bsName");
        tvBs.setText("当前基站：");
        tvBs.append(bsname);
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        //根据是否是独立站点设置界面头及右侧图片
        this.buildList();
        this.buildImageList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //获取作业列表
    private void buildList() {
        final ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_request));
        pd.show();
        FuncGetCurPlanInfoList func = new FuncGetCurPlanInfoList(context);
        OnGetDataFinishedListener glr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                pd.dismiss();
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i(App.LOG_TAG, output);
                try {
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        InspectInfoViewFactory factory = new InspectInfoViewFactory(
                                context);
                        JSONArray arrays = json.getJSONArray("data");
                        for (int i = 0; i < arrays.length(); i++) {
                            JSONArray array = arrays.getJSONArray(i);
                            factory.addView(array, layout);
                        }
                    } else {
                        //到这里非法访问
                        Log.e("msg", "msg" + msg);
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        func.getData(glr, planedId);
    }

    private void buildImageList() {
        pd = null;
        pd = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        pd.show();
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
                        + planedId);
                Log.d(App.LOG_TAG, "planedId------" + planedId);
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("BSPlanWorkPhoto.do?userid=").append(userid)
                        .append("&sign=").append(sign).append("&planid=").append(planedId);
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                final String conResult = conn.getConnectionResult();
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        if (conResult.equals(AppHttpConnection.RESULT_FAIL)) {
                            toast(getResources().getString(
                                    R.string.common_not_network));
                        } else {
                            Log.i(App.LOG_TAG, conResult);
                            try {
                                JSONObject json = new JSONObject(conResult);
                                if (("1").equals(json.getString("result"))) {
                                    jsonRes = json.getJSONArray("data");
                                    int lenght = 3;
                                    if (jsonRes.length() < 3) {
                                        lenght = jsonRes.length();
                                    }
                                    for (int i = 0; i < lenght; ++i) {
                                        JSONArray array = jsonRes.getJSONArray(i);
                                        showSingleImage(array.getString(1), i);
                                    }

                                } else {
                                    toast(json.getString("msg"));
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        }.start();
    }

    private void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    private void findViews() {
        tvBs = (TextView) findViewById(R.id.tv_bsname);
        layout = (LinearLayout) findViewById(R.id.layout_it);
    }


    //巡检已已上传照片
    private void showSingleImage(final String url, int index) {
        ImageView img = (ImageView) findViewById(imageViewsId[index]);
        ProgressBar pb = (ProgressBar) findViewById(progressBarsId[index]);
        TextView tv = (TextView) findViewById(tipStatesId[index]);

        pb.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);
        new InspectContentInfoImageLoadTask(img, pb, tv).execute(url);

        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDetailImage(url);
            }
        });
    }

    private void showDetailImage(String url) {
        LayoutInflater li = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = li.inflate(R.layout.pop_image, null);
        ImageView img = (ImageView) layout.findViewById(R.id.img_single);
        ProgressBar pb = (ProgressBar) layout.findViewById(R.id.pb_state);
        TextView tv = (TextView) layout.findViewById(R.id.tv_state);
        Button btnc = (Button) layout.findViewById(R.id.btn_close);
        final PopupWindow pop = new PopupWindow(layout,
                ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        pop.setBackgroundDrawable(context.getResources().getDrawable(
                R.drawable.draw_bg_opactiy));
        pop.setFocusable(true);
        pop.showAtLocation(((Activity) context).findViewById(R.id.main), Gravity.CENTER, 0, 0);
        pop.update();
        pb.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);
        new InspectContentInfoImageLoadTask(img, pb, tv).execute(url);
        btnc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (pop != null)
                    pop.dismiss();
            }
        });
    }
}
