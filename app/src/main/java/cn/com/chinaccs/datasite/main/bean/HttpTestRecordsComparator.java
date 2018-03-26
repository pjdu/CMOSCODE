package cn.com.chinaccs.datasite.main.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by andyhua on 15-6-14.
 */
public class HttpTestRecordsComparator implements Comparator<HttpTestRecords> {
    private static final String TAG = "HttpTestRecordsComparator";

    @Override
    public int compare(HttpTestRecords lhs, HttpTestRecords rhs) {
        if (lhs == null)
            return -1;
        if (rhs == null)
            return 1;
        if (compareTime(lhs.testTime, rhs.testTime))
            return 1;
        else if (lhs.testTime.equals(rhs.testTime))
            return 0;
        else
            return -1;
    }

    private boolean compareTime(String lTime, String rTime) {
        Date lDate, rDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            lDate = sdf.parse(lTime);
            rDate = sdf.parse(rTime);

            if (lDate.getTime() < rDate.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}

