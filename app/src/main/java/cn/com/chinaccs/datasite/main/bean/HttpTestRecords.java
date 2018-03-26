package cn.com.chinaccs.datasite.main.bean;

/**
 * Created by andyhua on 15-6-11.
 */
public class HttpTestRecords {
    // 测试时间
    public String testTime;
    // 测试地点
    public String testAddr;
    // 平均首包时延
    public String avgFirDefer;
    // 平均打开时延
    public String avgFuDefer;
    // 平均访问速率
    public String avgSpeed;

    public HttpTestRecords() {
    }

    public HttpTestRecords(String testTime,
                           String testAddr,
                           String avgFirDefer,
                           String avgFuDefer,
                           String avgSpeed) {
        this.testTime = testTime;
        this.testAddr = testAddr;
        this.avgFirDefer = avgFirDefer;
        this.avgFuDefer = avgFuDefer;
        this.avgSpeed = avgSpeed;
    }

    @Override
    public String toString() {
        return "HttpTestRecords{" +
                "testTime='" + testTime + '\'' +
                ", testAddr='" + testAddr + '\'' +
                ", avgFirDefer='" + avgFirDefer + '\'' +
                ", avgFuDefer='" + avgFuDefer + '\'' +
                ", avgSpeed='" + avgSpeed + '\'' +
                '}';
    }
}
