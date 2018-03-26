/*
package cn.com.chinaccs.datasite.main.util;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;

public class BDGeoLocation {

    public LocationClient locClient;
    public BDLocationListener locListnner;
    public BDLocation location;
    */
/**
     * 国测局经纬度坐标系
     *//*

    public static final String COORTYPE_GCJ = "gcj02";
    */
/**
     * 百度墨卡托坐标系
     *//*

    public static final String COORTYPE_BDMKT = "bd09";
    */
/**
     * 百度经纬度坐标系
     *//*

    public static final String COORTYPE_BDJW = "bd09ll";
    //当位置变化是通知所有注册监听该事件的组件,位置已经变化
    public List<BDLocationChangeListener> listeners;

    public interface BDLocationChangeListener {
        void setLocation(BDLocation loc);
    }

    public void addChangeListner(BDLocationChangeListener listner) {
        listeners.add(listner);
    }

    public void removeChangeListner(BDLocationChangeListener listner) {
        listeners.remove(listner);
    }

    private void notifyLocChange(BDLocation loc) {
        //这里明明回调了，为什么size是0；
        boolean iiii=loc==null;
        Log.e("BDLocationChangeListener", " is notify"+listeners.size()+";"+listeners.isEmpty());
        for (BDLocationChangeListener listner:listeners) {
            listner.setLocation(loc);
        }
    }

    public BDGeoLocation(Context context) {
        locClient = new LocationClient(context);
        listeners = new ArrayList<>();
        locListnner = new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation loc) {
                // TODO Auto-generated method stub
                location = loc;
                if (location != null) {

                    Log.e("BDGeoLocation", " is notify" + listeners.size());
                    notifyLocChange(location);
                }
            }

//			@Override
//			public void onReceivePoi(BDLocation loc) {
//				// TODO Auto-generated method stub
//				location = loc;
//				if (location != null) {
//					Log.d(App.LOG_TAG, "addr:" + location.getAddrStr() + "");
//				}
//			}
        };

        // locClient.setLocOption(option);
        locClient.registerLocationListener(locListnner);
    }

    public void requestByBDOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        // option.setAddrType("detail");
        option.setCoorType(BDGeoLocation.COORTYPE_BDJW);
        option.setScanSpan(120000); // 定位间隔
        option.setAddrType("all");
        option.disableCache(true);
        option.setPriority(LocationClientOption.GpsFirst);
        locClient.setLocOption(option);
    }

    public void requestByGBOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        // option.setAddrType("detail");
        option.setCoorType(BDGeoLocation.COORTYPE_GCJ);
        option.setScanSpan(120000); // 定位间隔
        option.setAddrType("all");
        option.disableCache(true);
        option.setPriority(LocationClientOption.GpsFirst);
        locClient.setLocOption(option);
    }

    */
/**
     * 算两经纬度间方位角
     *
     * @param lat_a
     * @param lng_a
     * @param lat_b
     * @param lng_b
     * @return
     *//*

    public static Double getGpsAzimuth(double lng_a, double lat_a,
                                       double lng_b, double lat_b) {
        double d = 0;
        lat_a = lat_a * Math.PI / 180;
        lng_a = lng_a * Math.PI / 180;
        lat_b = lat_b * Math.PI / 180;
        lng_b = lng_b * Math.PI / 180;
        d = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a)
                * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
        d = Math.sqrt(1 - d * d);
        d = Math.cos(lat_b) * Math.sin(lng_b - lng_a) / d;
        d = Math.asin(d) * 180 / Math.PI;
        return d;

    }

    static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI = 6.28318530712; // 2*PI
    static double DEF_PI180 = 0.01745329252; // PI/180.0
    static double DEF_R = 6370693.5; // radius of earth

    */
/**
     * 两经纬度间距离，用勾股定理计算，适用于两点距离很近
     *
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     *//*

    public static double getShortDistance(double lon1, double lat1,
                                          double lon2, double lat2) {
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 经度差
        dew = ew1 - ew2;
        // 若跨东经和西经180 度，进行调整
        if (dew > DEF_PI)
            dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
            dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
        dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    */
/**
     * 两经纬度间距离，按标准的球面大圆劣弧长度计算，适用于距离较远的情况
     *
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     *//*

    public static double getLongDistance(double lon1, double lat1, double lon2,
                                         double lat2) {
        double ew1, ns1, ew2, ns2;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 求大圆劣弧与球心所夹的角(弧度)
        distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
                * Math.cos(ns2) * Math.cos(ew1 - ew2);
        // 调整到[-1..1]范围内，避免溢出
        if (distance > 1.0)
            distance = 1.0;
        else if (distance < -1.0)
            distance = -1.0;
        // 求大圆劣弧长度
        distance = DEF_R * Math.acos(distance);
        return distance;
    }
}
*/
