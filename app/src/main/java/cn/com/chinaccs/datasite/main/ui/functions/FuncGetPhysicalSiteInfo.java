package cn.com.chinaccs.datasite.main.ui.functions;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * Created by Asky on 2016/6/13.
 */
public class FuncGetPhysicalSiteInfo {
    private static final String TAG = FuncGetPhysicalSiteInfo.class.getSimpleName();
    private Context context;

    public FuncGetPhysicalSiteInfo(Context context) {
        this.context = context;
    }

    public void getData(final OnGetDataFinishedListener lr, final String bsId) {
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + bsId);
                StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
                url.append("BSSiteInfo.do?bsid=").append(bsId)
                        .append("&sign=").append(sign);
                Log.i(TAG, url.toString());
                AppHttpConnection conn = new AppHttpConnection(context, url.toString());
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
