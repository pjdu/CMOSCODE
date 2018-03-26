package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.blankj.ALog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * @author Fddi
 */
public class FuncGetUserArea {

    private Context context;

    public FuncGetUserArea(Context context) {
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
                url.append("UserAreaList.do?userid=").append(userid)
                        .append("&sign=").append(sign);
                ALog.d(url);
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                final String conResult = conn.getConnectionResult();
                ALog.json(conResult);
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

    public void getCountyData(final OnGetDataFinishedListener lr, final String city) {
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + city);
                String cityStr = city;
                try {
                    cityStr = URLEncoder.encode(cityStr, App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("AreaCountList.do?city=").append(cityStr)
                        .append("&sign=").append(sign);
                ALog.i(url);
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                final String conResult = conn.getConnectionResult();
                ALog.json(conResult);
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

    public void getAssCode(final OnGetDataFinishedListener lr, final String objId) {
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + objId);
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("AssetsResRel.do?objid=").append(objId)
                        .append("&sign=").append(sign);
                ALog.i(url);
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

    public void getResCode(final OnGetDataFinishedListener lr, final String resNo) {
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + resNo);
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("AssetsRelRes.do?asscode=").append(resNo)
                        .append("&sign=").append(sign);
                ALog.i(url);
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
