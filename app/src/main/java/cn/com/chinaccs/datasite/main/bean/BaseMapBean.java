package cn.com.chinaccs.datasite.main.bean;

import java.io.Serializable;

/**
 * Created by ytf on 16/3/18.
 */
public class BaseMapBean implements Serializable {
    protected String bsIds;//基站id
    protected String bsNames;//基站名称
    protected String bsLongitude; //经度
    protected String bsLatitude;//纬度
    protected String type;//类型，0--基站巡检；1---独立RRU巡检；2--隐患

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    protected String otherInfo;//所属基站id，针对独立rru，隐患
    public int getMainType() {
        return mainType;
    }

    public void setMainType(int mainType) {
        this.mainType = mainType;
    }

    protected int mainType;

    public String getBsIds() {
        return bsIds;
    }

    public void setBsIds(String bsIds) {
        this.bsIds = bsIds;
    }

    public String getBsNames() {
        return bsNames;
    }

    public void setBsNames(String bsNames) {
        this.bsNames = bsNames;
    }

    public String getBsLongitude() {
        return bsLongitude;
    }

    public void setBsLongitude(String bsLongitude) {
        this.bsLongitude = bsLongitude;
    }

    public String getBsLatitude() {
        return bsLatitude;
    }

    public void setBsLatitude(String bsLatitude) {
        this.bsLatitude = bsLatitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BaseMapBean{" +
                "bsIds='" + bsIds + '\'' +
                ", bsNames='" + bsNames + '\'' +
                ", bsLongitude='" + bsLongitude + '\'' +
                ", bsLatitude='" + bsLatitude + '\'' +
                ", type='" + type + '\'' +
                ", otherInfo='" + otherInfo + '\'' +
                ", mainType=" + mainType +
                '}';
    }
}
