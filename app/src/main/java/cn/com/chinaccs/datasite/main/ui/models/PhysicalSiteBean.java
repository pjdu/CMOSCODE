package cn.com.chinaccs.datasite.main.ui.models;

import android.content.Context;
import android.support.annotation.DrawableRes;

import cn.com.chinaccs.datasite.main.R;

/**
 * Created by asky on 16-6-13.
 */
public class PhysicalSiteBean {
    private String bsId;
    private String bsAlarmType;
    private String title;
    private String note;
    private String info;
    @DrawableRes
    private int infoImage;
    private int color;

    /**
     * @param title
     * @param note
     * @param info
     * @param infoImage
     * @param color
     */
    public PhysicalSiteBean(String title, String note, String info, int infoImage, int color) {
        this.title = title;
        this.note = note;
        this.info = info;
        this.infoImage = infoImage;
        this.color = color;
    }

    /**
     * @param bsId
     * @param bsAlarmType
     * @param title
     * @param note
     * @param info
     * @param infoImage
     * @param color
     */
    public PhysicalSiteBean(String bsId, String bsAlarmType, String title, String note, String info, int infoImage, int color) {
        this.bsId = bsId;
        this.bsAlarmType = bsAlarmType;
        this.title = title;
        this.note = note;
        this.info = info;
        this.infoImage = infoImage;
        this.color = color;
    }

    public String getBsId() {
        return bsId;
    }

    public void setBsId(String bsId) {
        this.bsId = bsId;
    }

    public String getBsAlarmType() {
        return bsAlarmType;
    }

    public void setBsAlarmType(String bsAlarmType) {
        this.bsAlarmType = bsAlarmType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getInfoImage() {
        return infoImage;
    }

    public void setInfoImage(int infoImage) {
        this.infoImage = infoImage;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static int getBgColor(Context context, String alarmType) {
        int color;
        if (alarmType == null) {
            color = context.getResources().getColor(R.color.background_card);
            return color;
        }
        if (alarmType.equals("critical")) {
            color = context.getResources().getColor(R.color.bsstate_critical);
        } else if (alarmType.equals("major")) {
            color = context.getResources().getColor(R.color.bsstate_major);
        } else if (alarmType.equals("minor")) {
            color = context.getResources().getColor(R.color.bsstate_minor);
        } else if (alarmType.equals("warning")) {
            color = context.getResources().getColor(R.color.bsstate_warning);
        } else {
            color = context.getResources().getColor(R.color.bsstate_normal);
        }
        return color;
    }

    @Override
    public String toString() {
        return "PhysicalSiteBean{" +
                "bsId='" + bsId + '\'' +
                ", bsAlarmType='" + bsAlarmType + '\'' +
                '}';
    }
}
