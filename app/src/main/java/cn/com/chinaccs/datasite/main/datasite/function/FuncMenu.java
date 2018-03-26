package cn.com.chinaccs.datasite.main.datasite.function;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckVersion;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.ui.cmos.AboutActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.AccountActivity;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.ui.cmos.SettingActivity;


public class FuncMenu {
	private Context context;
	private Button btnAbout;
	private Button btnAccount;
	private Button btnAppUpdate;
	private Button btnSetting;
	private PopupWindow pw;

	public FuncMenu(Context context) {
		this.context = context;
	}

	public void buildMenu(View v) {
		LayoutInflater li = (LayoutInflater) ((Activity) context)
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View menPop = li.inflate(R.layout.pop_menu_main, null);
		pw = new PopupWindow(menPop, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		pw.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.draw_bg_menu));
		pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		pw.showAsDropDown(v);
		findViews(menPop);
		OnClickListener lr = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.btn_menu_about:
					Intent ai = new Intent(context, AboutActivity.class);
					context.startActivity(ai);
					break;
				case R.id.btn_menu_safe:
					Intent si = new Intent(context, AccountActivity.class);
					context.startActivity(si);
					break;
				case R.id.btn_menu_update:
					getVersionCheck();
					break;
				case R.id.btn_menu_setting:
					Intent sei = new Intent(context, SettingActivity.class);
					context.startActivity(sei);
					break;
				default:
					break;
				}
				pw.dismiss();
			}
		};
		btnAbout.setOnClickListener(lr);
		btnAccount.setOnClickListener(lr);
		btnAppUpdate.setOnClickListener(lr);
		btnSetting.setOnClickListener(lr);
	}

	private void getVersionCheck() {
		final ProgressDialog pd = App.progressDialog(context, context
				.getResources().getString(R.string.common_request));
		pd.show();
		final AppCheckVersion appCv = new AppCheckVersion();
		final Handler hr = new Handler();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int v = App.getAppVersionCode(context);
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + v);
				final StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL)
						.append("VersionCheck.do").append("?version=")
						.append(v).append("&sign=").append(sign);
				final JSONObject json = appCv.getAppVersionCheck(context,
						url.toString());
				hr.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							if (json.getBoolean(App.JSON_MAP_RESULT)) {
								final String dUrl = json
										.getString(App.JSON_MAP_DOWNLOADURL);
								App.alertDialog(context, "有新版本",
										json.getString(App.JSON_MAP_MSG),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated
												// method stub
												appCv.downLoadApk(context,
														dUrl,
														DataSiteStart.APP_NAME);
												dialog.cancel();
											}
										}, null).show();
							} else {
								Toast.makeText(context,
										json.getString(App.JSON_MAP_MSG),
										Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pd.dismiss();
					}
				});
			}
		}).start();
	}

	private void findViews(View v) {
		btnAbout = (Button) v.findViewById(R.id.btn_menu_about);
		btnAccount = (Button) v.findViewById(R.id.btn_menu_safe);
		btnAppUpdate = (Button) v.findViewById(R.id.btn_menu_update);
		btnSetting = (Button) v.findViewById(R.id.btn_menu_setting);
	}
}
