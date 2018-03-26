package cn.com.chinaccs.datasite.main.ui.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Asky on 2016/6/17.
 */
public class MapUtils {
    /**
     * 判断字符串是否是一个数
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 百度坐标系转换成GPS坐标系
     *
     * @param latLng 百度坐标系
     * @return
     */
    public static LatLng bdLnChangeToGpsLn(LatLng latLng) {
        if (latLng == null) {
            return new LatLng(25.049153, 102.714601);
        }
        //将百度坐标转为GPS坐标
        //把百度坐标当成一个GPS坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // latLng待转换坐标
        converter.coord(latLng);
        //模拟纠偏得到百度地图坐标
        LatLng desLatLng = converter.convert();
        double longitude = 2 * latLng.longitude - desLatLng.longitude;
        double latitude = 2 * latLng.latitude - desLatLng.latitude;
        return new LatLng(latitude, longitude);
    }

    /**
     * GPS坐标系转换成百度坐标系
     *
     * @param latLng GPS坐标系
     * @return
     */
    public static LatLng gpsLnChangeToBdLn(LatLng latLng) {
        if (latLng == null) {
            return null;
        }
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(latLng);
        return converter.convert();
    }
}
