package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


/**
 * 新基站巡检任务
 * 
 */
public class FuncGetPlanList {

	private Context context;

	public FuncGetPlanList(Context context) {
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
				url.append("PlanList.do?userid=").append(userid)
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
