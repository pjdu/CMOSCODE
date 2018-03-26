package cn.com.chinaccs.datasite.main.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;

import com.blankj.ALog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.com.chinaccs.datasite.main.common.App;

/**
 * GPS定位服务
 *
 * @author Fddi
 */
public class GpsService extends Service {
    Context context;
    private LocationManager lm;
    private Location location;
    private List<GpsSatellite> listSatellite;
    public static final String KEY_INTENT_PARCELABLE_LOC = "parcelable location";
    public static final String KEY_INTENT_SER_SATELLITE = "parcelable satellite";

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

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listSatellite = new ArrayList<GpsSatellite>();
        if (lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            String bestProvider = lm.getBestProvider(getCriteria(), true);
            location = lm.getLastKnownLocation(bestProvider);
            // 监听状态
            lm.addGpsStatusListener(stlr);
            // 绑定监听，有4个参数
            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
            // 参数2，位置信息更新周期，单位毫秒
            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
            // 参数4，监听
            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
            // 1秒更新一次，或最小位移变化超过1米更新一次；
            // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(1000);然后执行handler.sendMessage(),更新位置
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5,
                    loclr);
            ALog.d(App.LOG_TAG, "GPS SERVICE CREATE");
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        lm = null;
        listSatellite = null;
        location = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        // ALog.d("GpsService", "onStartCommand");
        if (lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            String bestProvider = lm.getBestProvider(getCriteria(), true);
            location = lm.getLastKnownLocation(bestProvider);
            //服务开启时会回调这里，但是可能现在定时不准了
            Intent i = new Intent(GpsHandler.ACTION_GPS_RECEIVER);
            Bundle be = new Bundle();
            be.putParcelable(KEY_INTENT_PARCELABLE_LOC, location);
            be.putSerializable(KEY_INTENT_SER_SATELLITE,
                    (Serializable) listSatellite);
            i.putExtras(be);
            sendBroadcast(i);
        }
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    private LocationListener loclr = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    ALog.d(App.LOG_TAG, "GPS AVAILABLE");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    ALog.d(App.LOG_TAG, "GPS OUT OF SERVICE");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    ALog.d(App.LOG_TAG, "GPS UNAVAILABLE");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            // GPS开启时触发
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            // GPS禁用时触发
        }

        @Override
        public void onLocationChanged(Location loc) {
            // TODO Auto-generated method stub
            // 位置信息变化时触发,这里9-20以前获取到了位置并没有通知其他组件位置变化
            location = loc;
//			String bestProvider = lm.getBestProvider(getCriteria(), true);
//			Location mlocation = lm.getLastKnownLocation(bestProvider);
//
//			if (mlocation != null) {
//				Toast.makeText(getApplicationContext(), "loc is not null and outside", Toast.LENGTH_LONG).show();
//				Log.e("GPsService", "loc is not null thank youyou");
//				Log.e("GPsService", ""+mlocation.getLatitude());
//				Log.e("GPsService", ""+mlocation.getLongitude());
//			} else {
//				Toast.makeText(getApplicationContext(),"nullnullnullnullnullnull",Toast.LENGTH_LONG).show();
//				Log.e("GPsService","nullnullnullnullnull");
//			}
        }
    };

    private GpsStatus.Listener stlr = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    ALog.d(App.LOG_TAG, "GPS FIRST FIX");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    // ALog.d(App.LOG_TAG, "GPS SATELLITE STATUS CHANGE");
                    // 获取当前状态
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<android.location.GpsSatellite> iters = gpsStatus
                            .getSatellites().iterator();
                    int count = 0;
                    listSatellite = new ArrayList<GpsSatellite>();
                    while (iters.hasNext() && count <= maxSatellites) {
                        android.location.GpsSatellite star = iters.next();
                        GpsSatellite gss = new GpsSatellite();
                        gss.setAzimuth(star.getAzimuth());
                        gss.setElevation(star.getElevation());
                        gss.setPrn(star.getPrn());
                        gss.setSnr(star.getSnr());
                        gss.setAlmanac(star.hasAlmanac());
                        gss.setEphemeris(star.hasEphemeris());
                        gss.setUse(star.usedInFix());
                        listSatellite.add(gss);
                    }
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    ALog.d(App.LOG_TAG, "GPS STARTED");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    ALog.d(App.LOG_TAG, "GPS STOPED");
                    break;
            }
        }

        ;
    };

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

}
