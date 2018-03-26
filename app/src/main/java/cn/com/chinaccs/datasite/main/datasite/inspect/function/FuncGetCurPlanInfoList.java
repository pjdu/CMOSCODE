package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


/**
 * 获取上次作业时填写的内容
 *
 * Created by Asky on 15/10/13.
 */
public class FuncGetCurPlanInfoList {
    private Context context;

    public FuncGetCurPlanInfoList(Context context) {
        this.context = context;
    }

    public void getData(final OnGetDataFinishedListener lr, final String planedId) {
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE+userid+planedId);
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("BSPlanItemList.do?userid=").append(userid)
                        .append("&planid=").append(planedId)
                        .append("&sign=").append(sign);
                Log.e("url", "url" + url.toString());
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
}
