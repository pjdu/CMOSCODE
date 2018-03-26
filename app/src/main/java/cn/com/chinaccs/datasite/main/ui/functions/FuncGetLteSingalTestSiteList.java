package cn.com.chinaccs.datasite.main.ui.functions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.baidu.mapapi.model.LatLng;
import com.blankj.ALog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * Created by Asky on 2016/6/17.
 */
public class FuncGetLteSingalTestSiteList {
    private Context context;
    private final String TAG = FuncGetLteSingalTestSiteList.class.getSimpleName();

    public FuncGetLteSingalTestSiteList(Context context) {
        this.context = context;
    }

    public void getData(final OnGetDataFinishedListener lr, final int range,
                        final String area,
                        final LatLng currentLoc) {
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid + String.valueOf(currentLoc.longitude) + String.valueOf(currentLoc.latitude));

                StringBuffer url = new StringBuffer("http://222.221.16.153:8090/WDataSiteServertest/");
                String areaTemp = area;
                try {
                    if (!(areaTemp == null)) {
                        areaTemp = URLEncoder.encode(area, App.ENCODE_UTF8);
                    } else {
                        areaTemp = "";
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                url.append("SignTestList.do?userid=").append(userid)
                        .append("&longitude=").append(currentLoc.longitude)
                        .append("&latitude=").append(currentLoc.latitude)
                        .append("&sign=").append(sign)
                        .append("&area=").append(areaTemp);
                ALog.d(url.toString());
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                final String conResult = conn.getConnectionResult();
                ALog.d(conResult.toString());
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
