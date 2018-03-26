package cn.com.chinaccs.datasite.main.ui.models;

/**
 * Created by Administrator on 2016-6-16.
 */
public class BaseSiteBean {
    private String bsIds;//物理站址id
    private String bsNames;//物理站址名称
    private String bsLongitude; //物理站址经度
    private String bsLatitude;//物理站址纬度
    private String bsLocation;//地址
    private String bsType;//物理站址状态
    private String bsCode;//物理站址编号
    private String towerCode;//铁塔编号


    public BaseSiteBean(String bsIds, String bsNames, String bsLongitude,
                        String bsLatitude, String bsLocation, String bsType) {
        this.bsIds = bsIds;
        this.bsNames = bsNames;
        this.bsLongitude = bsLongitude;
        this.bsLatitude = bsLatitude;
        this.bsLocation = bsLocation;
        this.bsType = bsType;
    }

    public BaseSiteBean(String bsIds, String bsNames, String bsLongitude, String bsLatitude,
                        String bsLocation, String bsType, String bsNumber) {
        this.bsIds = bsIds;
        this.bsNames = bsNames;
        this.bsLongitude = bsLongitude;
        this.bsLatitude = bsLatitude;
        this.bsLocation = bsLocation;
        this.bsType = bsType;
        this.bsCode = bsNumber;
    }

    public BaseSiteBean(String bsIds, String bsNames, String bsLongitude,
                        String bsLatitude, String bsLocation, String bsType,
                        String bsNumber, String towerCode) {
        this.bsIds = bsIds;
        this.bsNames = bsNames;
        this.bsLongitude = bsLongitude;
        this.bsLatitude = bsLatitude;
        this.bsLocation = bsLocation;
        this.bsType = bsType;
        this.bsCode = bsNumber;
        this.towerCode = towerCode;
    }

    public BaseSiteBean() {

    }

    @Override
    public String toString() {
        return "BaseSiteBean{" +
                "bsIds='" + bsIds + '\'' +
                ", bsNames='" + bsNames + '\'' +
                ", bsLongitude='" + bsLongitude + '\'' +
                ", bsLatitude='" + bsLatitude + '\'' +
                ", bsLocation='" + bsLocation + '\'' +
                ", bsType='" + bsType + '\'' +
                ", bsCode='" + bsCode + '\'' +
                ", towerCode='" + towerCode + '\'' +
                '}';
    }

    public String getBsNumber() {
        return bsCode;
    }

    public void setBsNumber(String bsNumber) {
        this.bsCode = bsNumber;
    }

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

    public String getBsLocation() {
        return bsLocation;
    }

    public void setBsLocation(String bsLocation) {
        this.bsLocation = bsLocation;
    }

    public String getBsType() {
        return bsType;
    }

    public void setBsType(String bsType) {
        this.bsType = bsType;
    }

    public String getBsCode() {
        return bsCode;
    }

    public void setBsCode(String bsCode) {
        this.bsCode = bsCode;
    }

    public String getTowerCode() {
        return towerCode;
    }

    public void setTowerCode(String towerCode) {
        this.towerCode = towerCode;
    }
}
