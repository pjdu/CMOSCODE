package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


/**
 * 获取隐患列表工具类
 */
public class FuncGetHiddenPlanList {

	private Context context;

	public FuncGetHiddenPlanList(Context context) {
		this.context = context;
	}
	/**
	 * 获取隐患列表
	 */
	public void getData(final OnGetDataFinishedListener lr, final String bsId, Boolean isRRu) {
		SharedPreferences share = context
				.getSharedPreferences(App.SHARE_TAG, 0);
		final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		final String type=isRRu?"1":"0";
		final Handler hr = new Handler();
		Log.e("sign", bsId+type);
		new Thread() {

			@Override
			public void run() {
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid+bsId+type);
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("BSPotentialWork.do?userid=").append(userid)
				.append("&bsid=").append(bsId)
				.append("&sign=").append(sign)
				.append("&type=").append(type);
				Log.e("url", "hhhhh"+url.toString());
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				final String conResult = conn.getConnectionResult();
				hr.post(new Runnable() {

					@Override
					public void run() {
						lr.onFinished(conResult);
					}
				});
			}
		}.start();
	}

}
