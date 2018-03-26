package cn.com.chinaccs.datasite.main.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.chinaccs.datasite.main.R;

public class AppCheckLogin {
	/**
	 * 是否自动登录tag
	 */
	public static final String SHARE_IF_AUTOLOGIN = "MAP_SHARE_IF_AUTOLOGIN";
	/**
	 * 用户IDtag
	 */
	public static final String SHARE_USER_ID = "MAP_SHARE_USER_ID";
	/**
	 * 用户密码tag
	 */
	public static final String SHARE_USER_PWD = "MAP_SHARE_USER_PWD";

	/**
	 * 用户名tag
	 */
	public static final String SHARE_USER_NAME = "MAP_SHARE_USER_NAME";

	/**
	 * 机构tag
	 */
	public static final String SHARE_ORG_CODE = "MAP_SHARE_ORG_CODE";

	/**
	 * 用户类型tag
	 */
	public static final String SHARE_USER_TYPE = "MAP_SHARE_USER_TYPE";

	public void autoLogin(final Context context, final String url,
						  final Intent resultIntent) {
		final ProgressDialog pd = App.progressDialog(context, context
				.getResources().getString(R.string.common_response));
		pd.show();
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final JSONObject json = loginCheck(context, url);
				hr.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.cancel();
						boolean isPass;
						try {
							isPass = json.getBoolean(App.JSON_MAP_RESULT);
							String info = json.getString(App.JSON_MAP_MSG);
							if (isPass) {
								String name = json
										.getString(App.JSON_MAP_USERNAME);
								Toast.makeText(context, "登录成功，欢迎您：" + name,
										Toast.LENGTH_LONG).show();
								context.startActivity(resultIntent);
								((Activity) context).finish();
							} else {
								Toast.makeText(context, info, Toast.LENGTH_LONG)
										.show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

		}.start();
	}

	public JSONObject loginCheck(Context context, String url) {
		JSONObject json = new JSONObject();
		AppHttpConnection con = new AppHttpConnection(context, url);
		String res = con.getConnectionResult();
		try {
			if (res == "fail") {
				json.put(App.JSON_MAP_RESULT, false);
				json.put(App.JSON_MAP_MSG,
						context.getString(R.string.common_not_network));
			} else {
				JSONObject jsonRes;

				jsonRes = new JSONObject(res);
				Log.i("loginCheck", res);
				String resCode = jsonRes.getString("result");
				if (resCode.equals("1")) {
					json.put(App.JSON_MAP_RESULT, true);
					json.put(App.JSON_MAP_MSG, jsonRes.getString("msg"));
					json.put(App.JSON_MAP_USERNAME,
							jsonRes.getString("userName"));
					json.put(App.JSON_MAP_ORGCODE, jsonRes.getString("orgCode"));
					json.put(App.JSON_MAP_USERTYPE, jsonRes.getString("userType"));
				} else {
					json.put(App.JSON_MAP_RESULT, false);
					json.put(App.JSON_MAP_MSG, jsonRes.getString("msg"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
}
