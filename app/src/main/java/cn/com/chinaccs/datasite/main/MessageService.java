package cn.com.chinaccs.datasite.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.ui.MainActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.InspectHistoryActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.QAPersonalCenter;


public class MessageService extends Service {
	private final long MINS_GET = 60;
	private Context context;
	private SharedPreferences share;
	private WakeLock mWakeLock;
	private NotificationManager notifManager;
	private Notification notif;
	private Timer timer;
	private TimerTask task;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.context = this;
		this.acquireWakeLock();
		share = getSharedPreferences(App.SHARE_TAG, 0);
		notifManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		this.sendNotificationOnFinishedUpLoad("已开启消息提醒功能！", 2);
		getTimerCycle();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		notifManager.cancel(1);
		notifManager.cancel(2);
		notifManager.cancel(3);
		this.releaseWakeLock();
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		notifManager = null;
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	private void getTimerCycle() { // 定时获取消息
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				connServer();
			}
		};
		timer = new Timer(true);
		Long t = MINS_GET * (60 * 1000);
		timer.schedule(task, 60000, t);
	}

	private void connServer() {
		boolean ismIt = share.getBoolean(DataSiteStart.SHARE_IS_MSG_IT, false);
		boolean ismData = share.getBoolean(DataSiteStart.SHARE_IS_MSG, false);
		boolean ismQa = share.getBoolean(DataSiteStart.SHARE_IS_MSG, false);
		String userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid + ismIt
				+ ismData + ismQa + MINS_GET);
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		url.append("MessageHandler.do?userid=").append(userid).append("&isit=")
				.append(ismIt).append("&isdata=").append(ismData)
				.append("&isqa=").append(ismQa).append("&times=")
				.append(MINS_GET).append("&sign=").append(sign);
		Log.d(App.LOG_TAG, url.toString());
		AppHttpConnection conn = new AppHttpConnection(context, url.toString());
		final String conResult = conn.getConnectionResult();
		if (!conResult.equals(AppHttpConnection.RESULT_FAIL)) {
			try {
				JSONObject json = new JSONObject(conResult);
				if (json.has("itmsg")) {
					sendNotificationOnFinishedUpLoad(json.getString("itmsg"), 1);
				}
				if (json.has("datamsg")) {
					sendNotificationOnFinishedUpLoad(json.getString("datamsg"),
							2);
				}
				if (json.has("qamsg")) {
					sendNotificationOnFinishedUpLoad(json.getString("qamsg"), 3);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void sendNotificationOnFinishedUpLoad(String msg, int msgType) {// 通知栏消息
		notifManager.cancel(0);
		notif = null;
		notif = new Notification();
		/*notif.icon = R.drawable.ic_launcher;
		notif.defaults = Notification.DEFAULT_SOUND;
		notif.tickerText = msg;*/
		//API 11以上需要用Notification.Builer 对象
		Notification.Builder builder=new Notification.Builder(context).setTicker(msg).setSmallIcon(R.drawable.ic_launcher).setDefaults(Notification.DEFAULT_SOUND);
		// 点击该通知后要跳转的Activity
		//Intent ni=null;
		Intent ni =new Intent();
		ni.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//当实例在栈顶时无需创建新实例
		switch (msgType) {
		case 1:
			ni = new Intent(context, InspectHistoryActivity.class);
			break;
		case 2:
			ni = new Intent(context, MainActivity.class);
			break;
		case 3:
			ni = new Intent(context, QAPersonalCenter.class);
			break;
		default:
			break;
		}
		PendingIntent contentItent = PendingIntent.getActivity(context, 0, ni,
				0);
		//notif.setLatestEventInfo(context, notif.tickerText, "消息提示",
		//		contentItent); 因为aip提高被弃用
        //新增的notifycation设置，build要求minsdk 16
		notif = builder.setContentIntent(contentItent).setContentText("消息提示").setContentTitle(msg).build();
		notifManager.notify(msgType, notif);


	}

	private void acquireWakeLock() {
		if (null == mWakeLock) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "");
			if (null != mWakeLock) {
				mWakeLock.acquire();
				Log.d(App.LOG_TAG, "wake lock");
			}
		}
	}

	private void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
}
