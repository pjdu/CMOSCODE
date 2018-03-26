package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


/**
 * Created by AndyHua on 2015/10/21.
 */
public class FuncBSLocSearch {
    private Context context;
    private final String TAG=FuncBSLocSearch.class.getSimpleName();

    public FuncBSLocSearch(Context context) {
        this.context = context;
    }

    public void getData(final OnGetDataFinishedListener lr, final String bsName) {
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid + bsName);

                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                String bsname = bsName;
                try {
                    bsname = URLEncoder.encode(bsName, App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                url.append("BSLocSearch.do?userid=").append(userid)
                        .append("&bsname=").append(bsname)
                        .append("&sign=").append(sign);
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                Log.i(TAG, url.toString());
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
