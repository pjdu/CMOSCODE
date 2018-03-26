package cn.com.chinaccs.datasite.main.ui.models;

import android.content.Context;
import android.support.annotation.DrawableRes;

import cn.com.chinaccs.datasite.main.R;

/**
 * 站点规划功能列表显示
 * <p/>
 * Created by asky on 16-6-13.
 */
public class PhysicalSiteListBean {
    private String bsId;
    private String title;
    private String bsCode;
    private String bsType;
    private String note;
    private String info;
    @DrawableRes
    private int infoImage;
    private int color;

    public PhysicalSiteListBean(String bsId, String title, String note, String info, int infoImage, int color) {
        this.bsId = bsId;
        this.title = title;
        this.note = note;
        this.info = info;
        this.infoImage = infoImage;
        this.color = color;
    }

    public PhysicalSiteListBean(String bsId, String title, String bsCode, String bsType,
                                String note, String info, int infoImage, int color) {
        this.bsId = bsId;
        this.title = title;
        this.bsCode = bsCode;
        this.bsType = bsType;
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

    public static int getBgColor(Context context, String bsState) {
        int color;
        if (bsState == null) {
            color = context.getResources().getColor(R.color.background_card);
            return color;
        }
        if (bsState.equals("0")) {
            color = context.getResources().getColor(R.color.bsstate_normal);
        } else if (bsState.equals("1") || bsState.equals("2")) {
            color = context.getResources().getColor(R.color.bsstate_new_checking);
        } else if (bsState.equals("3") || bsState.equals("4")) {
            color = context.getResources().getColor(R.color.bsstate_new_applying);
        } else if (bsState.equals("5")) {
            color = context.getResources().getColor(R.color.bsstate_new_paying);
        } else {
            color = context.getResources().getColor(R.color.background_card);
        }
        return color;
    }

    public String getBsCode() {
        return bsCode;
    }

    public void setBsCode(String bsCode) {
        this.bsCode = bsCode;
    }

    public String getBsType() {
        return bsType;
    }

    public void setBsType(String bsType) {
        this.bsType = bsType;
    }
}
