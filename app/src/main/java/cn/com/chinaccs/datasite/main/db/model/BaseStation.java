package cn.com.chinaccs.datasite.main.db.model;

import java.io.Serializable;

/**
 * 基站实体类
 *
 * @author Asky
 */
public class BaseStation implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6710545920813194749L;
    private String bsIds;//基站id
    private String bsNames;//基站名称
    private String bsLongitude; //经度
    private String bsLatitude;//纬度
    private String bsBtsId;//基站收发台id
    private String bsBsc;//基站控制台id
    private String type;//类型，0--基站巡检；1---独立RRU巡检；2--隐患
    private String otherInfo;//所属基站id，针对独立rru，隐患
    private String generation;

    public BaseStation() {

    }

    public BaseStation(String bsIds, String bsNames, String bsLongitude, String bsLatitude, String bsBtsId, String bsBsc, String type, String otherInfo, String generation) {
        this.bsIds = bsIds;
        this.bsNames = bsNames;
        this.bsLongitude = bsLongitude;
        this.bsLatitude = bsLatitude;
        this.bsBtsId = bsBtsId;
        this.bsBsc = bsBsc;
        this.type = type;
        this.otherInfo = otherInfo;
        this.generation = generation;
    }

    public BaseStation(String bsIds, String bsNames, String bsLongitude,
                       String bsLatitude, String bsBtsId, String bsBsc, String type,
                       String otherInfo) {
        super();
        this.bsIds = bsIds;
        this.bsNames = bsNames;
        this.bsLongitude = bsLongitude;
        this.bsLatitude = bsLatitude;
        this.bsBtsId = bsBtsId;
        this.bsBsc = bsBsc;
        this.type = type;
        this.otherInfo = otherInfo;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
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

    public String getBsBtsId() {
        return bsBtsId;
    }

    public void setBsBtsId(String bsBtsId) {
        this.bsBtsId = bsBtsId;
    }

    public String getBsBsc() {
        return bsBsc;
    }

    public void setBsBsc(String bsBsc) {
        this.bsBsc = bsBsc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getotherInfo() {
        return otherInfo;
    }

    public void setotherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    @Override
    public String toString() {
        return "BaseStation{" +
                "bsIds='" + bsIds + '\'' +
                ", bsNames='" + bsNames + '\'' +
                ", bsLongitude='" + bsLongitude + '\'' +
                ", bsLatitude='" + bsLatitude + '\'' +
                ", bsBtsId='" + bsBtsId + '\'' +
                ", bsBsc='" + bsBsc + '\'' +
                ", type='" + type + '\'' +
                ", otherInfo='" + otherInfo + '\'' +
                '}';
    }
}
