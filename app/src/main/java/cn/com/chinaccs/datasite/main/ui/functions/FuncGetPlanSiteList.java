package cn.com.chinaccs.datasite.main.ui.functions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * Created by Asky on 2016/6/17.
 */
public class FuncGetPlanSiteList {
    private Context context;
    private final String TAG = FuncGetPlanSiteList.class.getSimpleName();

    public FuncGetPlanSiteList(Context context) {
        this.context = context;
    }

    public void getData(final OnGetDataFinishedListener lr, final String siteName, final LatLng currentLoc,
                        final String flag, final String blindArea, final String blindCounty) {
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid + String.valueOf(currentLoc.longitude) + String.valueOf(currentLoc.latitude));

                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                String bsName = siteName;
                try {
                    if (!(bsName == null)) {
                        bsName = URLEncoder.encode(siteName, App.ENCODE_UTF8);
                    } else {
                        bsName = "";
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                url.append("PlanSiteList.do?userid=").append(userid)
                        .append("&siteName=").append(bsName)
                        .append("&longitude=").append(currentLoc.longitude)
                        .append("&latitude=").append(currentLoc.latitude)
                        .append("&flag=").append(flag)
                        .append("&area=").append(blindArea == null ? "530100" : blindArea)
                        .append("&county=").append(blindCounty == null ? "" : blindCounty)
                        .append("&sign=").append(sign);
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                final String conResult = conn.getConnectionResult();
                ALog.i(url.toString());
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

    public void getLocalPhysicalData(final OnGetDataFinishedListener listener, final int range, final LatLng currentLoc) {
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid + range + String.valueOf(currentLoc.longitude) + String.valueOf(currentLoc.latitude));

                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);

                url.append("BSStateNearList.do?userid=").append(userid)
                        .append("&range=").append(range)
                        .append("&longitude=").append(currentLoc.longitude)
                        .append("&latitude=").append(currentLoc.latitude)
                        .append("&sign=").append(sign);
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                final String conResult = conn.getConnectionResult();
                Log.i(TAG, url.toString());
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        listener.onFinished(conResult);
                    }
                });
            }
        }.start();
    }

    public void upLoadType(final OnGetDataFinishedListener lr, final int type, final int ids) {
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                JSONObject output = new JSONObject();
                JSONArray array = new JSONArray();
                try {
                    output.put("userid", userid);
                    output.put("id", ids);
                    output.put("data", array);
                    output.put("state", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String output1 = output.toString();
                String output2 = null;
                try {
                    output2 = URLEncoder.encode(output1, App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                //String[] strings=new String[]{};
                url.append("DSSaveAssets.do");
                String br = url.toString();
                AppHttpClient client = new AppHttpClient(context);
                Log.i(TAG, br);
                Log.i(TAG, output2);
                Log.i(TAG, output1);
                final String res = client.getResultByPOST(br, output2);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(res);
                    }
                });
            }
        }.start();
    }

    public void getPaySiteInfo(final OnGetDataFinishedListener lr, final String id) {
        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + id);

                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("PaySiteInfo.do?id=").append(id)
                        .append("&sign=").append(sign);
                ALog.i(url.toString());
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

}
