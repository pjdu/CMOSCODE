package cn.com.chinaccs.datasite.main.ui.lte;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckVersion;

public class CheckActivity extends Activity {
	private Context context;
	private Myhandler hr;
	private AppCheckVersion appCv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 清除临时数据
		this.context = this;
		appCv = new AppCheckVersion();
		hr = new Myhandler(context, appCv);
		setContentView(R.layout.activity_welcome);


		this.versionCheck();
	}

	private void versionCheck() {
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
				JSONObject json = appCv.getAppVersionCheck(context,
						url.toString());
				Message m = hr.obtainMessage(100, json);
				hr.sendMessage(m);
			}
		}).start();
	}

	static class Myhandler extends Handler {
		private Context context;
		private AppCheckVersion appCv;

		public Myhandler(Context context, AppCheckVersion appCv) {
			this.context = context;
			this.appCv = appCv;
		}

		private void sleepTimes(int times) {
			//final Intent intent = new Intent(context, LoginActivity.class);
			Timer timer = new Timer(); // 建立一个timer，控制3秒后跳转activity；
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//context.startActivity(intent);
					((Activity) context).finish();
				}
			};
			timer.schedule(task, times);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 100:
				final JSONObject json = (JSONObject) msg.obj;
				try {
					if (json.getBoolean(App.JSON_MAP_RESULT)) {
						final String dUrl = json
								.getString(App.JSON_MAP_DOWNLOADURL);
						App.alertDialog(context, "有新版本",
								json.getString(App.JSON_MAP_MSG),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated
										// method stub
										appCv.downLoadApk(context, dUrl,
												DataSiteStart.APP_NAME);
										dialog.cancel();
									}
								}, new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										sleepTimes(100);
									}
								}).show();
					} else {
						Dialog alertDialog = new AlertDialog.Builder(context).
								setTitle("检查更新").setMessage("已是最新版本").setPositiveButton("确定",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sleepTimes(100);
							}
						}).create();
						alertDialog.show();


					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}

	}
}
