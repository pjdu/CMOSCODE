package cn.com.chinaccs.datasite.main.datasite.function;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.blankj.ALog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.R;

/**
 * @author Fddi
 * 
 */
public class FuncGetFieldOptions {

	private Context context;

	public FuncGetFieldOptions(Context context) {
		this.context = context;
	}

	public void getData(final OnGetDataFinishedListener lr, final String fn,
			final String fb) {
		final ProgressDialog pd = App.progressDialog(context, context
				.getResources().getString(R.string.common_request));
		pd.show();
		SharedPreferences share = context
				.getSharedPreferences(App.SHARE_TAG, 0);
		final String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
						+ fn + fb);
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("FieldOptionsList.do?userid=").append(userid)
						.append("&fieldname=").append(fn)
						.append("&fieldbelong=").append(fb).append("&sign=")
						.append(sign);
				ALog.d(url);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				final String conResult = conn.getConnectionResult();
				ALog.d(conResult);
				hr.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.dismiss();
						if (conResult.equals(AppHttpConnection.RESULT_FAIL)) {
							Toast.makeText(
									context,
									context.getResources().getString(
											R.string.common_not_network),
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								JSONObject json = new JSONObject(conResult);
								String result = json.getString("result");
								if (result.equals("1")) {
									JSONArray array = json.getJSONArray("data");
									lr.onFinished(array.toString());
								} else {
									Toast.makeText(context,
											json.getString("msg"),
											Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				});
			}
		}.start();
	}
}
