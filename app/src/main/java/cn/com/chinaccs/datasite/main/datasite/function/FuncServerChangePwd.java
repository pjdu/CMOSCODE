package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


/**
 * @author Fddi
 * 
 */
public class FuncServerChangePwd {

	private Context context;

	public FuncServerChangePwd(Context context) {
		this.context = context;
	}

	public void changePwd(final OnGetDataFinishedListener lr, final String pwd,
			final String newPwd) {
		SharedPreferences share = context
				.getSharedPreferences(App.SHARE_TAG, 0);
		final String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
						+ pwd + newPwd);
				String clearUserId = "";
				String clearPwd = "";
				String clearNewPwd = "";
				try {
					clearUserId = AESCryto.encrypt(DataSiteStart.AES_KEY,
							userId);
					clearPwd = AESCryto.encrypt(DataSiteStart.AES_KEY, pwd);
					clearNewPwd = AESCryto.encrypt(DataSiteStart.AES_KEY,
							newPwd);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("PasswordModify.do?userid=").append(clearUserId)
						.append("&pwd=").append(clearPwd).append("&newpwd=")
						.append(clearNewPwd).append("&sign=").append(sign);
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
