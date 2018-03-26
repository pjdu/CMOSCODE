package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.blankj.ALog;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


/**
 * Created by AndyHua on 15/8/16.
 *
 * 获取巡检任务列表
 */
public class FuncGetBSInsPlanList {
    private Context context;

    public FuncGetBSInsPlanList(Context context) {
        this.context = context;
    }

    public void getData(final OnGetDataFinishedListener lr) {
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid);
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("BSInsPlanList.do?userid=").append(userid)
                        .append("&sign=").append(sign);
                ALog.i(url.toString());
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                final String conResult = conn.getConnectionResult();
                ALog.i(conResult);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
        }.start();
        /* SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + "13368711787");
                Log.i(App.LOG_TAG, "sign : " + sign);
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("BSInsPlanList.do?userid=").append("13368711787")
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
        }.start();*/
    }
}
