package cn.com.chinaccs.datasite.main.ui.models;

import java.io.Serializable;

/**
 * Created by Asky on 2016/8/30.
 */
public class BaseSite implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6710545920813194709L;
    private String bsId;//物理站址id
    private String bsName;//物理站址名称
    private String bsLongitude; //物理站址经度
    private String bsLatitude;//物理站址纬度
    private String bsAddress;//物理站址地址
    private String bsType;//物理站址类型
    private String bsState;//物理站址状态
    private String bsAlarmInfo;//物理站址告警信息
    private String bsNetType;//物理站址网络制式类型

    public BaseSite() {

    }

    public String getBsId() {
        return bsId;
    }

    public void setBsId(String bsId) {
        this.bsId = bsId;
    }

    public String getBsName() {
        return bsName;
    }

    public void setBsName(String bsName) {
        this.bsName = bsName;
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

    public String getBsAddress() {
        return bsAddress;
    }

    public void setBsAddress(String bsAddress) {
        this.bsAddress = bsAddress;
    }

    public String getBsType() {
        return bsType;
    }

    public void setBsType(String bsType) {
        this.bsType = bsType;
    }

    public String getBsState() {
        return bsState;
    }

    public void setBsState(String bsState) {
        this.bsState = bsState;
    }

    public String getBsAlarmInfo() {
        return bsAlarmInfo;
    }

    public void setBsAlarmInfo(String bsAlarmInfo) {
        this.bsAlarmInfo = bsAlarmInfo;
    }

    public String getBsNetType() {
        return bsNetType;
    }

    public void setBsNetType(String bsNetType) {
        this.bsNetType = bsNetType;
    }

    @Override
    public String toString() {
        return "BaseStation{" +
                "bsId='" + bsId + '\'' +
                ", bsName='" + bsName + '\'' +
                ", bsLongitude='" + bsLongitude + '\'' +
                ", bsLatitude='" + bsLatitude + '\'' +
                ", bsAddress='" + bsAddress + '\'' +
                ", bsType='" + bsType + '\'' +
                ", bsState='" + bsState + '\'' +
                ", bsAlarmInfo='" + bsAlarmInfo + '\'' +
                '}';
    }
}
