package cn.com.chinaccs.datasite.main.bean;

/**
 * Created by andyhua on 15-4-28.
 */
public class InfoSpeedRecords {
    private static final String TAG = "SpeedRecordsData";
    /**
     * 测试详情
     */
    // 测试时间
    public String testTime;
    // 测试服务器
    public String testServer;
    // 下行峰值
    public String speedDownMax;
    // 下载平均速度
    public String speedDown;
    // 上行峰值
    public String speedUpMax;
    // 上传平均速度
    public String speedUp;
    // ping时延
    public String pingDelay;
    /**
     * 无线环境信息
     */
    // 网络类型
    public String networkType;
    // 接入点名称
    public String extra;
    // 系统标识
    public String sid;
    // 网络类型
    public String nid;
    // 基站小区号
    public String ci;
    // CDMA 信号强度
    public String CDMA_Signal;
    // 内部IP地址
    public String innerIP;
    // 外部IP地址
    public String outerIP;
    // LTE RSRP
    public String LTE_RSRP;
    // LTE RSRQ
    public String LTE_RSRQ;
    // LTE RSSNR
    public String LTE_SINR;
    // Ci
    public String LTE_CI;
    // Pci
    public String LTE_Pci;
    // Tac
    public String LTE_Tac;

    /**
     * 坐标位置信息
     */
    // 经度
    public String lon;
    // 纬度
    public String lat;
    // 地址
    public String addr;

    public InfoSpeedRecords() {

    }

    public InfoSpeedRecords(String testTime, String testServer, String speedDownMax, String speedDown,
                            String speedUpMax, String speedUp, String pingDelay,
                            String networkType, String extra, String sid, String nid, String ci, String CDMA_Signal, String innerIP, String outerIP,
                            String LTE_RSRP, String LTE_RSRQ, String LTE_SINR, String LTE_CI,
                            String LTE_Pci, String LTE_Tac,
                            String lon, String lat, String addr) {
        this.testTime = testTime;
        this.testServer = testServer;
        this.speedDownMax = speedDownMax;
        this.speedDown = speedDown;
        this.speedUpMax = speedUpMax;
        this.speedUp = speedUp;
        this.pingDelay = pingDelay;
        this.networkType = networkType;
        this.extra = extra;
        this.sid = sid;
        this.nid = nid;
        this.ci = ci;
        this.CDMA_Signal = CDMA_Signal;
        this.innerIP = innerIP;
        this.outerIP = outerIP;
        this.LTE_RSRP = LTE_RSRP;
        this.LTE_RSRQ = LTE_RSRQ;
        this.LTE_SINR = LTE_SINR;
        this.LTE_CI = LTE_CI;
        this.LTE_Pci = LTE_Pci;
        this.LTE_Tac = LTE_Tac;
        this.lon = lon;
        this.lat = lat;
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "InfoSpeedRecords{" +
                "testTime='" + testTime + '\'' +
                ", testServer='" + testServer + '\'' +
                ", speedDownMax='" + speedDownMax + '\'' +
                ", speedDown='" + speedDown + '\'' +
                ", speedUpMax='" + speedUpMax + '\'' +
                ", speedUp='" + speedUp + '\'' +
                ", pingDelay='" + pingDelay + '\'' +
                ", networkType='" + networkType + '\'' +
                ", extra='" + extra + '\'' +
                ", sid='" + sid + '\'' +
                ", nid='" + nid + '\'' +
                ", ci='" + ci + '\'' +
                ", CDMA_Signal='" + CDMA_Signal + '\'' +
                ", innerIP='" + innerIP + '\'' +
                ", outerIP='" + outerIP + '\'' +
                ", LTE_RSRP='" + LTE_RSRP + '\'' +
                ", LTE_RSRQ='" + LTE_RSRQ + '\'' +
                ", LTE_SINR='" + LTE_SINR + '\'' +
                ", LTE_CI='" + LTE_CI + '\'' +
                ", LTE_Pci='" + LTE_Pci + '\'' +
                ", LTE_Tac='" + LTE_Tac + '\'' +
                ", lon='" + lon + '\'' +
                ", lat='" + lat + '\'' +
                ", addr='" + addr + '\'' +
                '}';
    }
}
