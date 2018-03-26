package cn.com.chinaccs.datasite.main.datasite.function;

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
 * 
 */
public class FuncGetBsTowns {

	private Context context;

	public FuncGetBsTowns(Context context) {
		this.context = context;
	}

	public void getData(final OnGetDataFinishedListener lr, final String county) {
		SharedPreferences share = context
				.getSharedPreferences(App.SHARE_TAG, 0);

		final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
							+ county); 
					String countyName = URLEncoder.encode(county, App.ENCODE_UTF8);
					StringBuffer url = new StringBuffer(
							DataSiteStart.HTTP_SERVER_URL);
					url.append("GetBsTownList.do?userid=").append(userid)
							.append("&countyName=").append(countyName)
							.append("&sign=").append(sign);
					Log.d(App.LOG_TAG, "------url--------" + url);
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
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
}
