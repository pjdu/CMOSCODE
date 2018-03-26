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
 * @author Fddi
 */
public class FuncGetSignBsInfo {



    private Context context;

    public FuncGetSignBsInfo(Context context) {
        this.context = context;
    }

    /*	public void getData(final OnGetDataFinishedListener lr, final int range,
                final String longitude, final String latitude) {
            SharedPreferences share = context
                    .getSharedPreferences(App.SHARE_TAG, 0);
            final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
            final Handler hr = new Handler();
            new Thread() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
                            + range + longitude + latitude);

                    StringBuffer url = new StringBuffer(
                            DataSiteStart.HTTP_SERVER_URL);
                    url.append("BSStateNearList.do?userid=").append(userid)
                            .append("&range=").append(range)
                            .append("&longitude=").append(longitude)
                            .append("&latitude=").append(latitude)
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
        }*/


    /*
    @area 地市信息中文名称，根据spinner
    @county 区县信息中文名，根据spinner
    **/
    public void getData(final OnGetDataFinishedListener lr, final int range,
                        final String longitude, final String latitude,final String area,final String county) {

        SharedPreferences share = context
                .getSharedPreferences(App.SHARE_TAG, 0);
        Log.e(App.LOG_TAG,"请求的数据："+area+" "+county);
        final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String areaUTF = area;
                String countyUTF=county;
                try {
                     areaUTF = URLEncoder.encode(area, App.ENCODE_UTF8);
                     countyUTF =URLEncoder.encode(county,App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
                        + range + longitude + latitude);

                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL);
                url.append("BSStateAreaList.do?userid=").append(userid)
                        .append("&range=").append(range)
                        .append("&longitude=").append(longitude)
                        .append("&latitude=").append(latitude)
                        .append("&sign=").append(sign)
                        .append("&area=").append(areaUTF)
                        .append("&county=").append(countyUTF);
                AppHttpConnection conn = new AppHttpConnection(context,
                        url.toString());
                Log.d("MainActivity","请求基站信息的数据是："+area+""+county);
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
