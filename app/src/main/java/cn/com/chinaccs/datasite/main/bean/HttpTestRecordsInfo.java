package cn.com.chinaccs.datasite.main.bean;

/**
 * Created by andyhua on 15-6-11.
 */
public class HttpTestRecordsInfo {

    // 测试时间
    public String testTime;
    // 测试地址
    public String testAddr;

    public String http_effective_url;
    public String http_response_code;
    public String http_namelookup_time;
    public String http_connect_time;
    public String http_pretransfer_time;
    public String http_starttransfer_time;
    public String http_first_delay;
    public String http_total_time;
    public String siteType;
    public String http_primary_ip;
    public String http_local_ip;
    public String http_size_download;
    public String http_speed_download;

    public HttpTestRecordsInfo() {
    }

    public HttpTestRecordsInfo(String testTime,
                               String testAddr,
                               String http_effective_url,
                               String http_response_code,
                               String http_namelookup_time,
                               String http_connect_time,
                               String http_pretransfer_time,
                               String http_starttransfer_time,
                               String http_first_delay,
                               String http_total_time,
                               String siteType,
                               String http_primary_ip,
                               String http_local_ip,
                               String http_size_download,
                               String http_speed_download) {
        this.testTime = testTime;
        this.testAddr = testAddr;
        this.http_effective_url = http_effective_url;
        this.http_response_code = http_response_code;
        this.http_namelookup_time = http_namelookup_time;
        this.http_connect_time = http_connect_time;
        this.http_pretransfer_time = http_pretransfer_time;
        this.http_starttransfer_time = http_starttransfer_time;
        this.http_first_delay = http_first_delay;
        this.http_total_time = http_total_time;
        this.siteType = siteType;
        this.http_primary_ip = http_primary_ip;
        this.http_local_ip = http_local_ip;
        this.http_size_download = http_size_download;
        this.http_speed_download = http_speed_download;
    }

    @Override
    public String toString() {
        return "HttpTestRecordsInfo{" +
                "testTime='" + testTime + '\'' +
                ", testAddr='" + testAddr + '\'' +
                ", http_effective_url='" + http_effective_url + '\'' +
                ", http_response_code='" + http_response_code + '\'' +
                ", http_namelookup_time='" + http_namelookup_time + '\'' +
                ", http_connect_time='" + http_connect_time + '\'' +
                ", http_pretransfer_time='" + http_pretransfer_time + '\'' +
                ", http_starttransfer_time='" + http_starttransfer_time + '\'' +
                ", http_first_delay='" + http_first_delay + '\'' +
                ", http_total_time='" + http_total_time + '\'' +
                ", siteType='" + siteType + '\'' +
                ", http_primary_ip='" + http_primary_ip + '\'' +
                ", http_local_ip='" + http_local_ip + '\'' +
                ", http_size_download='" + http_size_download + '\'' +
                ", http_speed_download='" + http_speed_download + '\'' +
                '}';
    }
}
