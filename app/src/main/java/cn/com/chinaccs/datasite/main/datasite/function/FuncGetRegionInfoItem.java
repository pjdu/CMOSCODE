package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.os.Handler;

import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


/**
 * Created by Asky on 2015/12/28.
 */
public class FuncGetRegionInfoItem {
    private Context context;

    public FuncGetRegionInfoItem(Context context) {
        this.context = context;
    }

    public void getData(final OnGetDataFinishedListener lr) {
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringBuffer url = new StringBuffer("http://222.221.16.153:8080/WCollectorServer/");
                url.append("RegionInfo.do");
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
