package cn.com.chinaccs.datasite.main.gps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;

import java.math.BigDecimal;
import java.util.List;

public class GpsHandler {
	public static final long TIME_INTERVAL_GPS = 30000;
    //2分钟内位置有效
	public static final long TIME_OUT_LOC = 120000;
	public static final String ACTION_GPS_RECEIVER = "android.action.gps.receiver";
	Context context;
	private Location location;
	private List<GpsSatellite> listSatellite;

	public GpsHandler(Context context) {
		this.context = context;
	}

	private BroadcastReceiver receiver;
  //程序启动时就调用，开启定位
	public void startGps(long times) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, GpsService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = System.currentTimeMillis() + 5000;// 延时10秒执行
		manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, times,
				pendingIntent);
		//之前没有调用这个方法，GpsHandler死活收不到location，巨坑
		registerGpsReceiver();
	}
;
	public void stopGps() {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, GpsService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.cancel(pendingIntent);
		location = null;
		listSatellite = null;
	}
//注册GPS变化监听广播
	public void registerGpsReceiver() {
		IntentFilter filter = new IntentFilter(ACTION_GPS_RECEIVER);
		receiver = new BroadcastReceiver() {

			@SuppressWarnings("unchecked")
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				location = (Location) intent
						.getParcelableExtra(GpsService.KEY_INTENT_PARCELABLE_LOC);
				listSatellite = (List<GpsSatellite>) intent
						.getSerializableExtra(GpsService.KEY_INTENT_SER_SATELLITE);
			}
		};
		context.registerReceiver(receiver, filter);
	}

	public void unregisterGpsReceiver() {
		if (receiver != null)
			context.unregisterReceiver(receiver);
	}

	public Location getLastLocation() {
		context.startService(new Intent(context, GpsService.class));
		if (location != null) {
			return location;
		}
		return null;
	}

	public List<GpsSatellite> getGpsSatellites() {
		if (listSatellite != null) {
			return listSatellite;
		}
		return null;
	}

	public interface OnLocationChanged {
		public void changed(Location loc, List<GpsSatellite> GpsSatellites);
	}

	// 地球半径
	public static double EARTH_RADIUS = 6378137.0;

	/**
	 * google 计算距离方法
	 * 
	 * @param lng_a
	 * @param lat_a
	 * @param lng_b
	 * @param lat_b
	 * @return
	 */
	public static double getShortDistance(double lng_a, double lat_a,
			double lng_b, double lat_b) {

		double radLat1 = (lat_a * Math.PI / 180.0);

		double radLat2 = (lat_b * Math.PI / 180.0);

		double a = radLat1 - radLat2;

		double b = (lng_a - lng_b) * Math.PI / 180.0;

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)

		+ Math.cos(radLat1) * Math.cos(radLat2)

		* Math.pow(Math.sin(b / 2), 2)));

		s = s * EARTH_RADIUS;

		s = new BigDecimal(s).setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		return s;

	}
}
