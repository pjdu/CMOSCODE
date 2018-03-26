package cn.com.chinaccs.datasite.main.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by andyhua on 15-4-29.
 */
public class SpeedRecordsComparator implements Comparator<InfoSpeedRecords> {
    private static final String TAG = "SpeedRecordsComparator";

    @Override
    public int compare(InfoSpeedRecords lhs, InfoSpeedRecords rhs) {
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            lDate = sdf.parse(lTime);
            rDate = sdf.parse(rTime);

            if (lDate.getTime() < rDate.getTime()){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
