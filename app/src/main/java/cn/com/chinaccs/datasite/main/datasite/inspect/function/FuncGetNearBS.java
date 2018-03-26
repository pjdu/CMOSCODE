package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

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
public class FuncGetNearBS {

	private Context context;

	public FuncGetNearBS(Context context) {
		this.context = context;
	}

	public void getData(final OnGetDataFinishedListener lr, final int range,
			final String longitude, final String latitude, final String bsname) {
		SharedPreferences share = context
				.getSharedPreferences(App.SHARE_TAG, 0);
		final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
						+ range + longitude + latitude + bsname);
				String Edstr = bsname;
				try {
					Edstr = URLEncoder.encode(bsname, App.ENCODE_UTF8);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("BSNearList.do?userid=").append(userid)
						.append("&range=").append(range).append("&longitude=")
						.append(longitude).append("&latitude=")
						.append(latitude).append("&bsname=").append(Edstr)
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
	}
}
