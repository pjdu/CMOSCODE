package cn.com.chinaccs.datasite.main.ui.asset.function;

import android.content.Context;
import android.content.SharedPreferences;

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

/**
 * @author Fddi
 */
public class FuncGetCardLoc {

    private Context context;

    public FuncGetCardLoc(Context context) {
        this.context = context;
    }

    public void getCardLoc(final String assNo, final double lon, final double lat) {
        SharedPreferences share = context.getSharedPreferences(App.SHARE_TAG, 0);
        String name = share.getString(AppCheckLogin.SHARE_USER_NAME, "");
        final JSONObject output = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            data.put(assNo);
            data.put(lon + "");
            data.put(lat + "");
            data.put(name);
            output.put("data", data);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                conn(output.toString());
            }
        }).start();
    }

    private void conn(String output) {
        ALog.json(output);
        StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                .append("EditScanOrder.do");
        String url = br.toString();
        AppHttpClient client = new AppHttpClient(context);
        try {
            output = URLEncoder.encode(output, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String res = client.getResultByPOST(url, output);
        ALog.d(res);
    }
}
