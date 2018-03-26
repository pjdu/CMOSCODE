package cn.com.chinaccs.datasite.main.connect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.blankj.ALog;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecordsInfo;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.lte.LteSignalMainActivity;

public class PostBusiSiteData {
    private Context context;
    private final String TAG = PostBusiSiteData.class.getSimpleName();
    //HttpTestActivity传过来的数据
    // 测试地址
    public String testAddr;
    //浏览页面名称
    private String WebsiteName;
    //浏览页面地址
    private String PageURL;
    //浏览页面IP地址
    private String PageIP;
    // 测试时间
    public String PageSurfTime;
    //首包延时，单位毫秒
    private String FirstByteDelay;
    //页面打开延时，单位秒
    private String PageOpenDelay;
    //空口建立时延,集团传空值
    private String RRCSetupDelay;
    //DNS解析时延，单位毫秒
    private String DnsDelay;
    //建立连接时延
    private String ConnDelay;
    //发送请求时延
    private String ReqDelay;
    //接收响应时延
    private String ResDelay;
    //业务类型
    private String TCLASS;
    //访问是否成功
    private String Success;
    //业务测试时的DNS服务器的IP地址
    private String DnsIP;
    //页面大小
    private String PageSize;
    //页面平均下载速率
    private String PageAvgSpeed;

    //上传的公用属性
    private JSONObject httpTest = new JSONObject();
    private String IMSI;
    private String MEID;
    private String PhoneType;
    private String OSVersion;
    private String BaseBand;
    private String Kernel;
    private String InnerVersion;
    private String RamUsage;
    private String CpuUsage;
    private String Longitude;
    private String Latitude;
    private String LocationDesc;
    private String Province;
    private String City;
    private String Source;
    private String NetType;
    private String APN;
    private String CdmaSid;
    private String CdmaNid;
    private String CdmaBsid;
    private String CdmadBm;
    private String LteCi;
    private String LtePci;
    private String LteTac;
    private String LteRsrp;
    private String LteSinr;
    private String InnerIP;
    private String OuterIP;
    private String Ecio;
    private String Snr;
    private String LteRsrq;

    //拼接的json
    private JSONObject PhoneInfo;
    private JSONObject PositionInfo;
    private JSONObject NetInfo;
    private JSONObject TestResult;
    private JSONObject outPut;


    private String http_response_code;
    private String http_namelookup_time;
    private String siteType;
    private String http_local_ip;
    private String http_size_download;
    private String http_speed_download;

    //上传状态
    private final int upLoadToFunction = 1;
    private final int upLoadForce = 0;

    @Override
    public String toString() {
        return "PostBusiSiteData{" +
                "context=" + context +
                ", TAG='" + TAG + '\'' +
                ", testAddr='" + testAddr + '\'' +
                ", WebsiteName='" + WebsiteName + '\'' +
                ", PageURL='" + PageURL + '\'' +
                ", PageIP='" + PageIP + '\'' +
                ", PageSurfTime='" + PageSurfTime + '\'' +
                ", FirstByteDelay='" + FirstByteDelay + '\'' +
                ", PageOpenDelay='" + PageOpenDelay + '\'' +
                ", RRCSetupDelay='" + RRCSetupDelay + '\'' +
                ", DnsDelay='" + DnsDelay + '\'' +
                ", ConnDelay='" + ConnDelay + '\'' +
                ", ReqDelay='" + ReqDelay + '\'' +
                ", ResDelay='" + ResDelay + '\'' +
                ", TCLASS='" + TCLASS + '\'' +
                ", Success='" + Success + '\'' +
                ", DnsIP='" + DnsIP + '\'' +
                ", PageSize='" + PageSize + '\'' +
                ", PageAvgSpeed='" + PageAvgSpeed + '\'' +
                ", httpTest=" + httpTest +
                ", IMSI='" + IMSI + '\'' +
                ", MEID='" + MEID + '\'' +
                ", PhoneType='" + PhoneType + '\'' +
                ", OSVersion='" + OSVersion + '\'' +
                ", BaseBand='" + BaseBand + '\'' +
                ", Kernel='" + Kernel + '\'' +
                ", InnerVersion='" + InnerVersion + '\'' +
                ", RamUsage='" + RamUsage + '\'' +
                ", CpuUsage='" + CpuUsage + '\'' +
                ", Longitude='" + Longitude + '\'' +
                ", Latitude='" + Latitude + '\'' +
                ", LocationDesc='" + LocationDesc + '\'' +
                ", Province='" + Province + '\'' +
                ", City='" + City + '\'' +
                ", Source='" + Source + '\'' +
                ", NetType='" + NetType + '\'' +
                ", APN='" + APN + '\'' +
                ", CdmaSid='" + CdmaSid + '\'' +
                ", CdmaNid='" + CdmaNid + '\'' +
                ", CdmaBsid='" + CdmaBsid + '\'' +
                ", CdmadBm='" + CdmadBm + '\'' +
                ", LteCi='" + LteCi + '\'' +
                ", LtePci='" + LtePci + '\'' +
                ", LteTac='" + LteTac + '\'' +
                ", LteRsrp='" + LteRsrp + '\'' +
                ", LteSinr='" + LteSinr + '\'' +
                ", InnerIP='" + InnerIP + '\'' +
                ", OuterIP='" + OuterIP + '\'' +
                ", Ecio='" + Ecio + '\'' +
                ", Snr='" + Snr + '\'' +
                ", LteRsrq='" + LteRsrq + '\'' +
                ", PhoneInfo=" + PhoneInfo +
                ", PositionInfo=" + PositionInfo +
                ", NetInfo=" + NetInfo +
                ", TestResult=" + TestResult +
                ", outPut=" + outPut +
                ", http_response_code='" + http_response_code + '\'' +
                ", http_namelookup_time='" + http_namelookup_time + '\'' +
                ", siteType='" + siteType + '\'' +
                ", http_local_ip='" + http_local_ip + '\'' +
                ", http_size_download='" + http_size_download + '\'' +
                ", http_speed_download='" + http_speed_download + '\'' +
                '}';
    }

    public PostBusiSiteData(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void postDataBaidu(final OnGetDataFinishedListener lr, final JSONObject json, List<HttpTestRecordsInfo> list, int upLoadState) {
        // TODO Auto-generated method stub
        PostBusiSiteData post = new PostBusiSiteData(context);
        for (HttpTestRecordsInfo testResult : list) {
            if (testResult.http_effective_url.equals("百度")) {

                //this.WebsiteName = new String(testResult.http_effective_url.getBytes("UTF-8"), "UTF-8");

                //this.WebsiteName = URLEncoder.encode(testResult.http_effective_url, App.ENCODE_UTF8);
                this.WebsiteName = "百度";
                this.PageURL = "http://www.baidu.com";
                this.PageIP = testResult.http_primary_ip;
                this.PageSurfTime = testResult.testTime;
                this.FirstByteDelay = testResult.http_first_delay;
                this.PageOpenDelay = testResult.http_total_time;
                this.RRCSetupDelay = "";
                this.DnsDelay = testResult.http_namelookup_time;
                this.ConnDelay = testResult.http_connect_time;
                this.ReqDelay = testResult.http_pretransfer_time;
                this.ResDelay = testResult.http_starttransfer_time;
                this.TCLASS = "1";
                this.Success = "1";
                this.DnsIP = "";
                this.PageSize = testResult.http_size_download;
                this.PageAvgSpeed = testResult.http_speed_download;
                this.OuterIP = testResult.http_primary_ip;
            }


        }
        if (upLoadState == upLoadToFunction) {
            try {
                this.IMSI = LteSignalMainActivity.LTE.getString("IMSI");
                this.MEID = LteSignalMainActivity.LTE.getString("MEID");
                this.PhoneType = LteSignalMainActivity.LTE.getString("PhoneType");
                this.OSVersion = LteSignalMainActivity.LTE.getString("OSVersion");
                this.BaseBand = LteSignalMainActivity.LTE.getString("BaseBand");
                this.Kernel = LteSignalMainActivity.LTE.getString("Kernel");
                this.InnerVersion = LteSignalMainActivity.LTE.getString("InnerVersion");
                this.RamUsage = LteSignalMainActivity.LTE.getString("RamUsage");
                this.CpuUsage = LteSignalMainActivity.LTE.getString("CpuUsage");
                this.Longitude = LteSignalMainActivity.LTE.getString("Longitude");
                this.Latitude = LteSignalMainActivity.LTE.getString("Latitude");
                //this.LocationDesc = "China";
                //服务器无法识别中文
                // this.LocationDesc= URLEncoder.encode(LteSignalMainActivity.LTE.getString("LocationDesc"), App.ENCODE_UTF8);
                this.LocationDesc = LteSignalMainActivity.LTE.getString("LocationDesc");
                // this.Province=URLEncoder.encode(LteSignalMainActivity.LTE.getString("Province"),App.ENCODE_UTF8);
                this.Province = LteSignalMainActivity.LTE.getString("Province");
                // this.City=URLEncoder.encode(LteSignalMainActivity.LTE.getString("City"),App.ENCODE_UTF8);
                this.City = LteSignalMainActivity.LTE.getString("City");
                this.Source = LteSignalMainActivity.LTE.getString("Source");
                this.NetType = LteSignalMainActivity.LTE.getString("NetType");
                this.APN = LteSignalMainActivity.LTE.getString("APN");
                this.CdmaSid = LteSignalMainActivity.LTE.getString("CdmaSid");
                this.CdmaNid = LteSignalMainActivity.LTE.getString("CdmaNid");
                this.CdmaBsid = LteSignalMainActivity.LTE.getString("CdmaBsid");
                this.CdmadBm = LteSignalMainActivity.LTE.getString("CdmadBm");
                this.LteCi = LteSignalMainActivity.LTE.getString("LteCi");
                this.LtePci = LteSignalMainActivity.LTE.getString("LtePci");
                this.LteTac = LteSignalMainActivity.LTE.getString("LteTac");
                this.LteRsrp = LteSignalMainActivity.LTE.getString("LteRsrp");
                this.LteSinr = LteSignalMainActivity.LTE.getString("LteSinr");
                this.InnerIP = LteSignalMainActivity.LTE.getString("InnerIP");

                this.Ecio = LteSignalMainActivity.LTE.getString("Ecio");
                this.Snr = LteSignalMainActivity.LTE.getString("Snr");
                this.LteRsrq = LteSignalMainActivity.LTE.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (upLoadState == upLoadForce) {
            try {
                this.IMSI = json.getString("IMSI");
                this.MEID = json.getString("MEID");
                this.PhoneType = json.getString("PhoneType");
                this.OSVersion = json.getString("OSVersion");
                this.BaseBand = json.getString("BaseBand");
                this.Kernel = json.getString("Kernel");
                this.InnerVersion = json.getString("InnerVersion");
                this.RamUsage = json.getString("RamUsage");
                this.CpuUsage = json.getString("CpuUsage");
                this.Longitude = json.getString("Longitude");
                this.Latitude = json.getString("Latitude");
                //this.LocationDesc = "China";
                this.LocationDesc = json.getString("LocationDesc");
                //服务器无法识别中文
                //this.LocationDesc=URLEncoder.encode(json.getString("LocationDesc"),App.ENCODE_UTF8);
                this.Province = json.getString("Province");
                this.City = json.getString("City");
                this.Source = json.getString("Source");
                this.NetType = json.getString("NetType");
                this.APN = json.getString("APN");
                this.CdmaSid = json.getString("CdmaSid");
                this.CdmaNid = json.getString("CdmaNid");
                this.CdmaBsid = json.getString("CdmaBsid");
                this.CdmadBm = json.getString("CdmadBm");
                this.LteCi = json.getString("LteCi");
                this.LtePci = json.getString("LtePci");
                this.LteTac = json.getString("LteTac");
                this.LteRsrp = json.getString("LteRsrp");
                this.LteSinr = json.getString("LteSinr");
                this.InnerIP = json.getString("InnerIP");

                this.Ecio = json.getString("Ecio");
                this.Snr = json.getString("Snr");
                this.LteRsrq = json.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        PhoneInfo = new JSONObject();
        PositionInfo = new JSONObject();
        NetInfo = new JSONObject();
        TestResult = new JSONObject();
        outPut = new JSONObject();

        try {
            PhoneInfo.put("IMSI", IMSI);
            PhoneInfo.put("MEID", MEID);
            PhoneInfo.put("PhoneType", PhoneType);
            PhoneInfo.put("OSVersion", OSVersion);
            PhoneInfo.put("BaseBand", BaseBand);
            PhoneInfo.put("Kernel", Kernel);
            PhoneInfo.put("InnerVersion", InnerVersion);
            PhoneInfo.put("RamUsage", RamUsage);
            PhoneInfo.put("CpuUsage", CpuUsage);

            PositionInfo.put("Longitude", Longitude);
            PositionInfo.put("Latitude", Latitude);
            PositionInfo.put("LocationDesc", LocationDesc);
            PositionInfo.put("Province", Province);
            PositionInfo.put("City", City);
            PositionInfo.put("Source", Source);


            NetInfo.put("NetType", NetType);
            NetInfo.put("APN", APN);
            NetInfo.put("CdmaSid", CdmaSid);
            NetInfo.put("CdmaNid", CdmaNid);
            NetInfo.put("CdmaBsid", CdmaBsid);
            NetInfo.put("CdmadBm", CdmadBm);
            NetInfo.put("LteCi", LteCi);
            NetInfo.put("LtePci", LtePci);
            NetInfo.put("LteTac", LteTac);
            NetInfo.put("LteRsrp", LteRsrp);
            NetInfo.put("LteSinr", LteSinr);
            NetInfo.put("InnerIP", InnerIP);
            NetInfo.put("OuterIP", OuterIP);
            NetInfo.put("Ecio", Ecio);
            NetInfo.put("Snr", Snr);
            NetInfo.put("LteRsrq", LteRsrq);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());


            TestResult.put("WebsiteName", WebsiteName);
            TestResult.put("PageURL", PageURL);
            TestResult.put("PageIP", PageIP);
            TestResult.put("PageSurfTime", time);
            TestResult.put("FirstByteDelay", FirstByteDelay);
            TestResult.put("PageOpenDelay", PageOpenDelay);
            TestResult.put("RRCSetupDelay", RRCSetupDelay);
            TestResult.put("DnsDelay", DnsDelay);
            TestResult.put("ConnDelay", ConnDelay);
            TestResult.put("ReqDelay", ReqDelay);
            TestResult.put("ResDelay", ResDelay);
            TestResult.put("TCLASS", TCLASS);
            TestResult.put("Success", Success);
            TestResult.put("DnsIP", DnsIP);
            TestResult.put("PageSize", PageSize);
            TestResult.put("PageAvgSpeed", PageAvgSpeed);

            outPut.put("PhoneInfo", PhoneInfo);
            outPut.put("PositionInfo", PositionInfo);
            outPut.put("NetInfo", NetInfo);
            outPut.put("TestResult", TestResult);
            Log.i(TAG, "output" + outPut.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }


        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringBuffer url = new StringBuffer(CoConfig.HTTP_SERVER_URL).append("CommonTestData.do");
                //.append("BusiSiteData.do?");
                SharedPreferences share = context.getSharedPreferences(CoConfig.SHARE_TAG, 0);
                String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
                //url.append("userId=").append(userId);
                AppHttpClient client = new AppHttpClient(context);
                String outPutUTF8 = outPut.toString();
                /*try {
                    outPutUTF8 = URLEncoder.encode(outPut.toString(), App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                final String conResult = client.getResultByPOST(url.toString(),
                        outPutUTF8);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
        }.start();
    }

    public void postDataLeshiwang(final OnGetDataFinishedListener lr, final JSONObject json, List<HttpTestRecordsInfo> list, int upLoadState) {
        // TODO Auto-generated method stub
        PostBusiSiteData post = new PostBusiSiteData(context);
        for (HttpTestRecordsInfo testResult : list
                ) {
            if (testResult.http_effective_url.equals("乐视网")) {

                //this.WebsiteName = new String(testResult.http_effective_url.getBytes("UTF-8"), "UTF-8");

                //this.WebsiteName = URLEncoder.encode(testResult.http_effective_url, App.ENCODE_UTF8);
                this.WebsiteName = "乐视网";
                this.PageURL = "http://www.letv.com";
                this.PageIP = testResult.http_primary_ip;
                this.PageSurfTime = testResult.testTime;
                this.FirstByteDelay = testResult.http_first_delay;
                this.PageOpenDelay = testResult.http_total_time;
                this.RRCSetupDelay = "";
                this.DnsDelay = testResult.http_namelookup_time;
                this.ConnDelay = testResult.http_connect_time;
                this.ReqDelay = testResult.http_pretransfer_time;
                this.ResDelay = testResult.http_starttransfer_time;
                this.TCLASS = "1";
                this.Success = "1";
                this.DnsIP = "";
                this.PageSize = testResult.http_size_download;
                this.PageAvgSpeed = testResult.http_speed_download;
                this.OuterIP = testResult.http_primary_ip;
            }


        }
        if (upLoadState == upLoadToFunction) {
            try {
                this.IMSI = LteSignalMainActivity.LTE.getString("IMSI");
                this.MEID = LteSignalMainActivity.LTE.getString("MEID");
                this.PhoneType = LteSignalMainActivity.LTE.getString("PhoneType");
                this.OSVersion = LteSignalMainActivity.LTE.getString("OSVersion");
                this.BaseBand = LteSignalMainActivity.LTE.getString("BaseBand");
                this.Kernel = LteSignalMainActivity.LTE.getString("Kernel");
                this.InnerVersion = LteSignalMainActivity.LTE.getString("InnerVersion");
                this.RamUsage = LteSignalMainActivity.LTE.getString("RamUsage");
                this.CpuUsage = LteSignalMainActivity.LTE.getString("CpuUsage");
                this.Longitude = LteSignalMainActivity.LTE.getString("Longitude");
                this.Latitude = LteSignalMainActivity.LTE.getString("Latitude");
                //this.LocationDesc = "China";
                //服务器无法识别中文
                // this.LocationDesc= URLEncoder.encode(LteSignalMainActivity.LTE.getString("LocationDesc"), App.ENCODE_UTF8);
                this.LocationDesc = LteSignalMainActivity.LTE.getString("LocationDesc");
                // this.Province=URLEncoder.encode(LteSignalMainActivity.LTE.getString("Province"),App.ENCODE_UTF8);
                this.Province = LteSignalMainActivity.LTE.getString("Province");
                // this.City=URLEncoder.encode(LteSignalMainActivity.LTE.getString("City"),App.ENCODE_UTF8);
                this.City = LteSignalMainActivity.LTE.getString("City");
                this.Source = LteSignalMainActivity.LTE.getString("Source");
                this.NetType = LteSignalMainActivity.LTE.getString("NetType");
                this.APN = LteSignalMainActivity.LTE.getString("APN");
                this.CdmaSid = LteSignalMainActivity.LTE.getString("CdmaSid");
                this.CdmaNid = LteSignalMainActivity.LTE.getString("CdmaNid");
                this.CdmaBsid = LteSignalMainActivity.LTE.getString("CdmaBsid");
                this.CdmadBm = LteSignalMainActivity.LTE.getString("CdmadBm");
                this.LteCi = LteSignalMainActivity.LTE.getString("LteCi");
                this.LtePci = LteSignalMainActivity.LTE.getString("LtePci");
                this.LteTac = LteSignalMainActivity.LTE.getString("LteTac");
                this.LteRsrp = LteSignalMainActivity.LTE.getString("LteRsrp");
                this.LteSinr = LteSignalMainActivity.LTE.getString("LteSinr");
                this.InnerIP = LteSignalMainActivity.LTE.getString("InnerIP");

                this.Ecio = LteSignalMainActivity.LTE.getString("Ecio");
                this.Snr = LteSignalMainActivity.LTE.getString("Snr");
                this.LteRsrq = LteSignalMainActivity.LTE.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (upLoadState == upLoadForce) {
            try {
                this.IMSI = json.getString("IMSI");
                this.MEID = json.getString("MEID");
                this.PhoneType = json.getString("PhoneType");
                this.OSVersion = json.getString("OSVersion");
                this.BaseBand = json.getString("BaseBand");
                this.Kernel = json.getString("Kernel");
                this.InnerVersion = json.getString("InnerVersion");
                this.RamUsage = json.getString("RamUsage");
                this.CpuUsage = json.getString("CpuUsage");
                this.Longitude = json.getString("Longitude");
                this.Latitude = json.getString("Latitude");
                //this.LocationDesc = "China";
                this.LocationDesc = json.getString("LocationDesc");
                //服务器无法识别中文
                //this.LocationDesc=URLEncoder.encode(json.getString("LocationDesc"),App.ENCODE_UTF8);
                this.Province = json.getString("Province");
                this.City = json.getString("City");
                this.Source = json.getString("Source");
                this.NetType = json.getString("NetType");
                this.APN = json.getString("APN");
                this.CdmaSid = json.getString("CdmaSid");
                this.CdmaNid = json.getString("CdmaNid");
                this.CdmaBsid = json.getString("CdmaBsid");
                this.CdmadBm = json.getString("CdmadBm");
                this.LteCi = json.getString("LteCi");
                this.LtePci = json.getString("LtePci");
                this.LteTac = json.getString("LteTac");
                this.LteRsrp = json.getString("LteRsrp");
                this.LteSinr = json.getString("LteSinr");
                this.InnerIP = json.getString("InnerIP");

                this.Ecio = json.getString("Ecio");
                this.Snr = json.getString("Snr");
                this.LteRsrq = json.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        PhoneInfo = new JSONObject();
        PositionInfo = new JSONObject();
        NetInfo = new JSONObject();
        TestResult = new JSONObject();
        outPut = new JSONObject();


        try {
            PhoneInfo.put("IMSI", IMSI);
            PhoneInfo.put("MEID", MEID);
            PhoneInfo.put("PhoneType", PhoneType);
            PhoneInfo.put("OSVersion", OSVersion);
            PhoneInfo.put("BaseBand", BaseBand);
            PhoneInfo.put("Kernel", Kernel);
            PhoneInfo.put("InnerVersion", InnerVersion);
            PhoneInfo.put("RamUsage", RamUsage);
            PhoneInfo.put("CpuUsage", CpuUsage);

            PositionInfo.put("Longitude", Longitude);
            PositionInfo.put("Latitude", Latitude);
            PositionInfo.put("LocationDesc", LocationDesc);
            PositionInfo.put("Province", Province);
            PositionInfo.put("City", City);
            PositionInfo.put("Source", Source);


            NetInfo.put("NetType", NetType);
            NetInfo.put("APN", APN);
            NetInfo.put("CdmaSid", CdmaSid);
            NetInfo.put("CdmaNid", CdmaNid);
            NetInfo.put("CdmaBsid", CdmaBsid);
            NetInfo.put("CdmadBm", CdmadBm);
            NetInfo.put("LteCi", LteCi);
            NetInfo.put("LtePci", LtePci);
            NetInfo.put("LteTac", LteTac);
            NetInfo.put("LteRsrp", LteRsrp);
            NetInfo.put("LteSinr", LteSinr);
            NetInfo.put("InnerIP", InnerIP);
            NetInfo.put("OuterIP", OuterIP);
            NetInfo.put("Ecio", Ecio);
            NetInfo.put("Snr", Snr);
            NetInfo.put("LteRsrq", LteRsrq);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());


            TestResult.put("WebsiteName", WebsiteName);
            TestResult.put("PageURL", PageURL);
            TestResult.put("PageIP", PageIP);
            TestResult.put("PageSurfTime", time);
            TestResult.put("FirstByteDelay", FirstByteDelay);
            TestResult.put("PageOpenDelay", PageOpenDelay);
            TestResult.put("RRCSetupDelay", RRCSetupDelay);
            TestResult.put("DnsDelay", DnsDelay);
            TestResult.put("ConnDelay", ConnDelay);
            TestResult.put("ReqDelay", ReqDelay);
            TestResult.put("ResDelay", ResDelay);
            TestResult.put("TCLASS", TCLASS);
            TestResult.put("Success", Success);
            TestResult.put("DnsIP", DnsIP);
            TestResult.put("PageSize", PageSize);
            TestResult.put("PageAvgSpeed", PageAvgSpeed);

            outPut.put("PhoneInfo", PhoneInfo);
            outPut.put("PositionInfo", PositionInfo);
            outPut.put("NetInfo", NetInfo);
            outPut.put("TestResult", TestResult);
            Log.i(TAG, "output" + outPut.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }


        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringBuffer url = new StringBuffer(CoConfig.HTTP_SERVER_URL).append("CommonTestData.do");
                //.append("BusiSiteData.do?");
                SharedPreferences share = context.getSharedPreferences(CoConfig.SHARE_TAG, 0);
                String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
                //url.append("userId=").append(userId);
                AppHttpClient client = new AppHttpClient(context);
                String outPutUTF8 = outPut.toString();
                /*try {
                    outPutUTF8 = URLEncoder.encode(outPut.toString(), App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                final String conResult = client.getResultByPOST(url.toString(),
                        outPutUTF8);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
        }.start();
    }

    public void postDataTaobao(final OnGetDataFinishedListener lr, final JSONObject json, List<HttpTestRecordsInfo> list, int upLoadState) {
        // TODO Auto-generated method stub
        PostBusiSiteData post = new PostBusiSiteData(context);
        for (HttpTestRecordsInfo testResult : list
                ) {
            if (testResult.http_effective_url.equals("淘宝")) {

                //this.WebsiteName = new String(testResult.http_effective_url.getBytes("UTF-8"), "UTF-8");

                //this.WebsiteName = URLEncoder.encode(testResult.http_effective_url, App.ENCODE_UTF8);
                this.WebsiteName = "淘宝";
                this.PageURL = "http://www.taobao.com";
                this.PageIP = testResult.http_primary_ip;
                this.PageSurfTime = testResult.testTime;
                this.FirstByteDelay = testResult.http_first_delay;
                this.PageOpenDelay = testResult.http_total_time;
                this.RRCSetupDelay = "";
                this.DnsDelay = testResult.http_namelookup_time;
                this.ConnDelay = testResult.http_connect_time;
                this.ReqDelay = testResult.http_pretransfer_time;
                this.ResDelay = testResult.http_starttransfer_time;
                this.TCLASS = "1";
                this.Success = "1";
                this.DnsIP = "";
                this.PageSize = testResult.http_size_download;
                this.PageAvgSpeed = testResult.http_speed_download;
                this.OuterIP = testResult.http_primary_ip;

            }


        }
        if (upLoadState == upLoadToFunction) {
            try {
                this.IMSI = LteSignalMainActivity.LTE.getString("IMSI");
                this.MEID = LteSignalMainActivity.LTE.getString("MEID");
                this.PhoneType = LteSignalMainActivity.LTE.getString("PhoneType");
                this.OSVersion = LteSignalMainActivity.LTE.getString("OSVersion");
                this.BaseBand = LteSignalMainActivity.LTE.getString("BaseBand");
                this.Kernel = LteSignalMainActivity.LTE.getString("Kernel");
                this.InnerVersion = LteSignalMainActivity.LTE.getString("InnerVersion");
                this.RamUsage = LteSignalMainActivity.LTE.getString("RamUsage");
                this.CpuUsage = LteSignalMainActivity.LTE.getString("CpuUsage");
                this.Longitude = LteSignalMainActivity.LTE.getString("Longitude");
                this.Latitude = LteSignalMainActivity.LTE.getString("Latitude");
                //this.LocationDesc = "China";
                //服务器无法识别中文
                // this.LocationDesc= URLEncoder.encode(LteSignalMainActivity.LTE.getString("LocationDesc"), App.ENCODE_UTF8);
                this.LocationDesc = LteSignalMainActivity.LTE.getString("LocationDesc");
                // this.Province=URLEncoder.encode(LteSignalMainActivity.LTE.getString("Province"),App.ENCODE_UTF8);
                this.Province = LteSignalMainActivity.LTE.getString("Province");
                // this.City=URLEncoder.encode(LteSignalMainActivity.LTE.getString("City"),App.ENCODE_UTF8);
                this.City = LteSignalMainActivity.LTE.getString("City");
                this.Source = LteSignalMainActivity.LTE.getString("Source");
                this.NetType = LteSignalMainActivity.LTE.getString("NetType");
                this.APN = LteSignalMainActivity.LTE.getString("APN");
                this.CdmaSid = LteSignalMainActivity.LTE.getString("CdmaSid");
                this.CdmaNid = LteSignalMainActivity.LTE.getString("CdmaNid");
                this.CdmaBsid = LteSignalMainActivity.LTE.getString("CdmaBsid");
                this.CdmadBm = LteSignalMainActivity.LTE.getString("CdmadBm");
                this.LteCi = LteSignalMainActivity.LTE.getString("LteCi");
                this.LtePci = LteSignalMainActivity.LTE.getString("LtePci");
                this.LteTac = LteSignalMainActivity.LTE.getString("LteTac");
                this.LteRsrp = LteSignalMainActivity.LTE.getString("LteRsrp");
                this.LteSinr = LteSignalMainActivity.LTE.getString("LteSinr");
                this.InnerIP = LteSignalMainActivity.LTE.getString("InnerIP");
                this.Ecio = LteSignalMainActivity.LTE.getString("Ecio");
                this.Snr = LteSignalMainActivity.LTE.getString("Snr");
                this.LteRsrq = LteSignalMainActivity.LTE.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (upLoadState == upLoadForce) {
            try {
                this.IMSI = json.getString("IMSI");
                this.MEID = json.getString("MEID");
                this.PhoneType = json.getString("PhoneType");
                this.OSVersion = json.getString("OSVersion");
                this.BaseBand = json.getString("BaseBand");
                this.Kernel = json.getString("Kernel");
                this.InnerVersion = json.getString("InnerVersion");
                this.RamUsage = json.getString("RamUsage");
                this.CpuUsage = json.getString("CpuUsage");
                this.Longitude = json.getString("Longitude");
                this.Latitude = json.getString("Latitude");
                //this.LocationDesc = "China";
                this.LocationDesc = json.getString("LocationDesc");
                //服务器无法识别中文
                //this.LocationDesc=URLEncoder.encode(json.getString("LocationDesc"),App.ENCODE_UTF8);
                this.Province = json.getString("Province");
                this.City = json.getString("City");
                this.Source = json.getString("Source");
                this.NetType = json.getString("NetType");
                this.APN = json.getString("APN");
                this.CdmaSid = json.getString("CdmaSid");
                this.CdmaNid = json.getString("CdmaNid");
                this.CdmaBsid = json.getString("CdmaBsid");
                this.CdmadBm = json.getString("CdmadBm");
                this.LteCi = json.getString("LteCi");
                this.LtePci = json.getString("LtePci");
                this.LteTac = json.getString("LteTac");
                this.LteRsrp = json.getString("LteRsrp");
                this.LteSinr = json.getString("LteSinr");
                this.InnerIP = json.getString("InnerIP");
                this.Ecio = json.getString("Ecio");
                this.Snr = json.getString("Snr");
                this.LteRsrq = json.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        PhoneInfo = new JSONObject();
        PositionInfo = new JSONObject();
        NetInfo = new JSONObject();
        TestResult = new JSONObject();
        outPut = new JSONObject();

        try {
            PhoneInfo.put("IMSI", IMSI);
            PhoneInfo.put("MEID", MEID);
            PhoneInfo.put("PhoneType", PhoneType);
            PhoneInfo.put("OSVersion", OSVersion);
            PhoneInfo.put("BaseBand", BaseBand);
            PhoneInfo.put("Kernel", Kernel);
            PhoneInfo.put("InnerVersion", InnerVersion);
            PhoneInfo.put("RamUsage", RamUsage);
            PhoneInfo.put("CpuUsage", CpuUsage);

            PositionInfo.put("Longitude", Longitude);
            PositionInfo.put("Latitude", Latitude);
            PositionInfo.put("LocationDesc", LocationDesc);
            PositionInfo.put("Province", Province);
            PositionInfo.put("City", City);
            PositionInfo.put("Source", Source);


            NetInfo.put("NetType", NetType);
            NetInfo.put("APN", APN);
            NetInfo.put("CdmaSid", CdmaSid);
            NetInfo.put("CdmaNid", CdmaNid);
            NetInfo.put("CdmaBsid", CdmaBsid);
            NetInfo.put("CdmadBm", CdmadBm);
            NetInfo.put("LteCi", LteCi);
            NetInfo.put("LtePci", LtePci);
            NetInfo.put("LteTac", LteTac);
            NetInfo.put("LteRsrp", LteRsrp);
            NetInfo.put("LteSinr", LteSinr);
            NetInfo.put("InnerIP", InnerIP);
            NetInfo.put("OuterIP", OuterIP);
            NetInfo.put("Ecio", Ecio);
            NetInfo.put("Snr", Snr);
            NetInfo.put("LteRsrq", LteRsrq);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());


            TestResult.put("WebsiteName", WebsiteName);
            TestResult.put("PageURL", PageURL);
            TestResult.put("PageIP", PageIP);
            TestResult.put("PageSurfTime", time);
            TestResult.put("FirstByteDelay", FirstByteDelay);
            TestResult.put("PageOpenDelay", PageOpenDelay);
            TestResult.put("RRCSetupDelay", RRCSetupDelay);
            TestResult.put("DnsDelay", DnsDelay);
            TestResult.put("ConnDelay", ConnDelay);
            TestResult.put("ReqDelay", ReqDelay);
            TestResult.put("ResDelay", ResDelay);
            TestResult.put("TCLASS", TCLASS);
            TestResult.put("Success", Success);
            TestResult.put("DnsIP", DnsIP);
            TestResult.put("PageSize", PageSize);
            TestResult.put("PageAvgSpeed", PageAvgSpeed);

            outPut.put("PhoneInfo", PhoneInfo);
            outPut.put("PositionInfo", PositionInfo);
            outPut.put("NetInfo", NetInfo);
            outPut.put("TestResult", TestResult);
            Log.i(TAG, "output" + outPut.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }


        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringBuffer url = new StringBuffer(CoConfig.HTTP_SERVER_URL).append("CommonTestData.do");
                //.append("BusiSiteData.do?");
                SharedPreferences share = context.getSharedPreferences(CoConfig.SHARE_TAG, 0);
                String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
                //url.append("userId=").append(userId);
                AppHttpClient client = new AppHttpClient(context);
                String outPutUTF8 = outPut.toString();
                /*try {
                    outPutUTF8 = URLEncoder.encode(outPut.toString(), App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                final String conResult = client.getResultByPOST(url.toString(),
                        outPutUTF8);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
        }.start();
    }

    public void postDataXinglang(final OnGetDataFinishedListener lr, final JSONObject json, List<HttpTestRecordsInfo> list, int upLoadState) {
        // TODO Auto-generated method stub
        PostBusiSiteData post = new PostBusiSiteData(context);
        for (HttpTestRecordsInfo testResult : list
                ) {
            if (testResult.http_effective_url.equals("新浪")) {

                //this.WebsiteName = new String(testResult.http_effective_url.getBytes("UTF-8"), "UTF-8");

                //this.WebsiteName = URLEncoder.encode(testResult.http_effective_url, App.ENCODE_UTF8);
                this.WebsiteName = "新浪";
                this.PageURL = "http://www.sina.com";
                this.PageIP = testResult.http_primary_ip;
                this.PageSurfTime = testResult.testTime;
                this.FirstByteDelay = testResult.http_first_delay;
                this.PageOpenDelay = testResult.http_total_time;
                this.RRCSetupDelay = "";
                this.DnsDelay = testResult.http_namelookup_time;
                this.ConnDelay = testResult.http_connect_time;
                this.ReqDelay = testResult.http_pretransfer_time;
                this.ResDelay = testResult.http_starttransfer_time;
                this.TCLASS = "1";
                this.Success = "1";
                this.DnsIP = "";
                this.PageSize = testResult.http_size_download;
                this.PageAvgSpeed = testResult.http_speed_download;
                this.OuterIP = testResult.http_primary_ip;
            }


        }
        if (upLoadState == upLoadToFunction) {
            try {
                this.IMSI = LteSignalMainActivity.LTE.getString("IMSI");
                this.MEID = LteSignalMainActivity.LTE.getString("MEID");
                this.PhoneType = LteSignalMainActivity.LTE.getString("PhoneType");
                this.OSVersion = LteSignalMainActivity.LTE.getString("OSVersion");
                this.BaseBand = LteSignalMainActivity.LTE.getString("BaseBand");
                this.Kernel = LteSignalMainActivity.LTE.getString("Kernel");
                this.InnerVersion = LteSignalMainActivity.LTE.getString("InnerVersion");
                this.RamUsage = LteSignalMainActivity.LTE.getString("RamUsage");
                this.CpuUsage = LteSignalMainActivity.LTE.getString("CpuUsage");
                this.Longitude = LteSignalMainActivity.LTE.getString("Longitude");
                this.Latitude = LteSignalMainActivity.LTE.getString("Latitude");
                //this.LocationDesc = "China";
                //服务器无法识别中文
                // this.LocationDesc= URLEncoder.encode(LteSignalMainActivity.LTE.getString("LocationDesc"), App.ENCODE_UTF8);
                this.LocationDesc = LteSignalMainActivity.LTE.getString("LocationDesc");
                // this.Province=URLEncoder.encode(LteSignalMainActivity.LTE.getString("Province"),App.ENCODE_UTF8);
                this.Province = LteSignalMainActivity.LTE.getString("Province");
                // this.City=URLEncoder.encode(LteSignalMainActivity.LTE.getString("City"),App.ENCODE_UTF8);
                this.City = LteSignalMainActivity.LTE.getString("City");
                this.Source = LteSignalMainActivity.LTE.getString("Source");
                this.NetType = LteSignalMainActivity.LTE.getString("NetType");
                this.APN = LteSignalMainActivity.LTE.getString("APN");
                this.CdmaSid = LteSignalMainActivity.LTE.getString("CdmaSid");
                this.CdmaNid = LteSignalMainActivity.LTE.getString("CdmaNid");
                this.CdmaBsid = LteSignalMainActivity.LTE.getString("CdmaBsid");
                this.CdmadBm = LteSignalMainActivity.LTE.getString("CdmadBm");
                this.LteCi = LteSignalMainActivity.LTE.getString("LteCi");
                this.LtePci = LteSignalMainActivity.LTE.getString("LtePci");
                this.LteTac = LteSignalMainActivity.LTE.getString("LteTac");
                this.LteRsrp = LteSignalMainActivity.LTE.getString("LteRsrp");
                this.LteSinr = LteSignalMainActivity.LTE.getString("LteSinr");
                this.InnerIP = LteSignalMainActivity.LTE.getString("InnerIP");
                this.Ecio = LteSignalMainActivity.LTE.getString("Ecio");
                this.Snr = LteSignalMainActivity.LTE.getString("Snr");
                this.LteRsrq = LteSignalMainActivity.LTE.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (upLoadState == upLoadForce) {
            try {
                this.IMSI = json.getString("IMSI");
                this.MEID = json.getString("MEID");
                this.PhoneType = json.getString("PhoneType");
                this.OSVersion = json.getString("OSVersion");
                this.BaseBand = json.getString("BaseBand");
                this.Kernel = json.getString("Kernel");
                this.InnerVersion = json.getString("InnerVersion");
                this.RamUsage = json.getString("RamUsage");
                this.CpuUsage = json.getString("CpuUsage");
                this.Longitude = json.getString("Longitude");
                this.Latitude = json.getString("Latitude");
                //this.LocationDesc = "China";
                this.LocationDesc = json.getString("LocationDesc");
                //服务器无法识别中文
                //this.LocationDesc=URLEncoder.encode(json.getString("LocationDesc"),App.ENCODE_UTF8);
                this.Province = json.getString("Province");
                this.City = json.getString("City");
                this.Source = json.getString("Source");
                this.NetType = json.getString("NetType");
                this.APN = json.getString("APN");
                this.CdmaSid = json.getString("CdmaSid");
                this.CdmaNid = json.getString("CdmaNid");
                this.CdmaBsid = json.getString("CdmaBsid");
                this.CdmadBm = json.getString("CdmadBm");
                this.LteCi = json.getString("LteCi");
                this.LtePci = json.getString("LtePci");
                this.LteTac = json.getString("LteTac");
                this.LteRsrp = json.getString("LteRsrp");
                this.LteSinr = json.getString("LteSinr");
                this.InnerIP = json.getString("InnerIP");
                this.Ecio = json.getString("Ecio");
                this.Snr = json.getString("Snr");
                this.LteRsrq = json.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        PhoneInfo = new JSONObject();
        PositionInfo = new JSONObject();
        NetInfo = new JSONObject();
        TestResult = new JSONObject();
        outPut = new JSONObject();


        try {
            PhoneInfo.put("IMSI", IMSI);
            PhoneInfo.put("MEID", MEID);
            PhoneInfo.put("PhoneType", PhoneType);
            PhoneInfo.put("OSVersion", OSVersion);
            PhoneInfo.put("BaseBand", BaseBand);
            PhoneInfo.put("Kernel", Kernel);
            PhoneInfo.put("InnerVersion", InnerVersion);
            PhoneInfo.put("RamUsage", RamUsage);
            PhoneInfo.put("CpuUsage", CpuUsage);

            PositionInfo.put("Longitude", Longitude);
            PositionInfo.put("Latitude", Latitude);
            PositionInfo.put("LocationDesc", LocationDesc);
            PositionInfo.put("Province", Province);
            PositionInfo.put("City", City);
            PositionInfo.put("Source", Source);


            NetInfo.put("NetType", NetType);
            NetInfo.put("APN", APN);
            NetInfo.put("CdmaSid", CdmaSid);
            NetInfo.put("CdmaNid", CdmaNid);
            NetInfo.put("CdmaBsid", CdmaBsid);
            NetInfo.put("CdmadBm", CdmadBm);
            NetInfo.put("LteCi", LteCi);
            NetInfo.put("LtePci", LtePci);
            NetInfo.put("LteTac", LteTac);
            NetInfo.put("LteRsrp", LteRsrp);
            NetInfo.put("LteSinr", LteSinr);
            NetInfo.put("InnerIP", InnerIP);
            NetInfo.put("OuterIP", OuterIP);
            NetInfo.put("Ecio", Ecio);
            NetInfo.put("Snr", Snr);
            NetInfo.put("LteRsrq", LteRsrq);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());


            TestResult.put("WebsiteName", WebsiteName);
            TestResult.put("PageURL", PageURL);
            TestResult.put("PageIP", PageIP);
            TestResult.put("PageSurfTime", time);
            TestResult.put("FirstByteDelay", FirstByteDelay);
            TestResult.put("PageOpenDelay", PageOpenDelay);
            TestResult.put("RRCSetupDelay", RRCSetupDelay);
            TestResult.put("DnsDelay", DnsDelay);
            TestResult.put("ConnDelay", ConnDelay);
            TestResult.put("ReqDelay", ReqDelay);
            TestResult.put("ResDelay", ResDelay);
            TestResult.put("TCLASS", TCLASS);
            TestResult.put("Success", Success);
            TestResult.put("DnsIP", DnsIP);
            TestResult.put("PageSize", PageSize);
            TestResult.put("PageAvgSpeed", PageAvgSpeed);

            outPut.put("PhoneInfo", PhoneInfo);
            outPut.put("PositionInfo", PositionInfo);
            outPut.put("NetInfo", NetInfo);
            outPut.put("TestResult", TestResult);
            Log.i(TAG, "output" + outPut.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }


        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringBuffer url = new StringBuffer(CoConfig.HTTP_SERVER_URL).append("CommonTestData.do");
                //.append("BusiSiteData.do?");
                SharedPreferences share = context.getSharedPreferences(CoConfig.SHARE_TAG, 0);
                String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
                //url.append("userId=").append(userId);
                AppHttpClient client = new AppHttpClient(context);
                String outPutUTF8 = outPut.toString();
                /*try {
                    outPutUTF8 = URLEncoder.encode(outPut.toString(), App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                final String conResult = client.getResultByPOST(url.toString(),
                        outPutUTF8);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
        }.start();
    }

    public void postDataWangyi(final OnGetDataFinishedListener lr, final JSONObject json, List<HttpTestRecordsInfo> list, int upLoadState) {
        // TODO Auto-generated method stub
        PostBusiSiteData post = new PostBusiSiteData(context);
        for (HttpTestRecordsInfo testResult : list
                ) {
            if (testResult.http_effective_url.equals("新浪")) {

                //this.WebsiteName = new String(testResult.http_effective_url.getBytes("UTF-8"), "UTF-8");

                //this.WebsiteName = URLEncoder.encode(testResult.http_effective_url, App.ENCODE_UTF8);
                this.WebsiteName = "网易";
                this.PageURL = "http://www.163.com";
                this.PageIP = testResult.http_primary_ip;
                this.PageSurfTime = testResult.testTime;
                this.FirstByteDelay = testResult.http_first_delay;
                this.PageOpenDelay = testResult.http_total_time;
                this.RRCSetupDelay = "";
                this.DnsDelay = testResult.http_namelookup_time;
                this.ConnDelay = testResult.http_connect_time;
                this.ReqDelay = testResult.http_pretransfer_time;
                this.ResDelay = testResult.http_starttransfer_time;
                this.TCLASS = "1";
                this.Success = "1";
                this.DnsIP = "";
                this.PageSize = testResult.http_size_download;
                this.PageAvgSpeed = testResult.http_speed_download;
                this.OuterIP = testResult.http_primary_ip;
            }


        }
        if (upLoadState == upLoadToFunction) {
            try {
                this.IMSI = LteSignalMainActivity.LTE.getString("IMSI");
                this.MEID = LteSignalMainActivity.LTE.getString("MEID");
                this.PhoneType = LteSignalMainActivity.LTE.getString("PhoneType");
                this.OSVersion = LteSignalMainActivity.LTE.getString("OSVersion");
                this.BaseBand = LteSignalMainActivity.LTE.getString("BaseBand");
                this.Kernel = LteSignalMainActivity.LTE.getString("Kernel");
                this.InnerVersion = LteSignalMainActivity.LTE.getString("InnerVersion");
                this.RamUsage = LteSignalMainActivity.LTE.getString("RamUsage");
                this.CpuUsage = LteSignalMainActivity.LTE.getString("CpuUsage");
                this.Longitude = LteSignalMainActivity.LTE.getString("Longitude");
                this.Latitude = LteSignalMainActivity.LTE.getString("Latitude");
                //this.LocationDesc = "China";
                //服务器无法识别中文
                // this.LocationDesc= URLEncoder.encode(LteSignalMainActivity.LTE.getString("LocationDesc"), App.ENCODE_UTF8);
                this.LocationDesc = LteSignalMainActivity.LTE.getString("LocationDesc");
                // this.Province=URLEncoder.encode(LteSignalMainActivity.LTE.getString("Province"),App.ENCODE_UTF8);
                this.Province = LteSignalMainActivity.LTE.getString("Province");
                // this.City=URLEncoder.encode(LteSignalMainActivity.LTE.getString("City"),App.ENCODE_UTF8);
                this.City = LteSignalMainActivity.LTE.getString("City");
                this.Source = LteSignalMainActivity.LTE.getString("Source");
                this.NetType = LteSignalMainActivity.LTE.getString("NetType");
                this.APN = LteSignalMainActivity.LTE.getString("APN");
                this.CdmaSid = LteSignalMainActivity.LTE.getString("CdmaSid");
                this.CdmaNid = LteSignalMainActivity.LTE.getString("CdmaNid");
                this.CdmaBsid = LteSignalMainActivity.LTE.getString("CdmaBsid");
                this.CdmadBm = LteSignalMainActivity.LTE.getString("CdmadBm");
                this.LteCi = LteSignalMainActivity.LTE.getString("LteCi");
                this.LtePci = LteSignalMainActivity.LTE.getString("LtePci");
                this.LteTac = LteSignalMainActivity.LTE.getString("LteTac");
                this.LteRsrp = LteSignalMainActivity.LTE.getString("LteRsrp");
                this.LteSinr = LteSignalMainActivity.LTE.getString("LteSinr");
                this.InnerIP = LteSignalMainActivity.LTE.getString("InnerIP");
                this.Ecio = LteSignalMainActivity.LTE.getString("Ecio");
                this.Snr = LteSignalMainActivity.LTE.getString("Snr");
                this.LteRsrq = LteSignalMainActivity.LTE.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (upLoadState == upLoadForce) {
            try {
                this.IMSI = json.getString("IMSI");
                this.MEID = json.getString("MEID");
                this.PhoneType = json.getString("PhoneType");
                this.OSVersion = json.getString("OSVersion");
                this.BaseBand = json.getString("BaseBand");
                this.Kernel = json.getString("Kernel");
                this.InnerVersion = json.getString("InnerVersion");
                this.RamUsage = json.getString("RamUsage");
                this.CpuUsage = json.getString("CpuUsage");
                this.Longitude = json.getString("Longitude");
                this.Latitude = json.getString("Latitude");
                //this.LocationDesc = "China";
                this.LocationDesc = json.getString("LocationDesc");
                //服务器无法识别中文
                //this.LocationDesc=URLEncoder.encode(json.getString("LocationDesc"),App.ENCODE_UTF8);
                this.Province = json.getString("Province");
                this.City = json.getString("City");
                this.Source = json.getString("Source");
                this.NetType = json.getString("NetType");
                this.APN = json.getString("APN");
                this.CdmaSid = json.getString("CdmaSid");
                this.CdmaNid = json.getString("CdmaNid");
                this.CdmaBsid = json.getString("CdmaBsid");
                this.CdmadBm = json.getString("CdmadBm");
                this.LteCi = json.getString("LteCi");
                this.LtePci = json.getString("LtePci");
                this.LteTac = json.getString("LteTac");
                this.LteRsrp = json.getString("LteRsrp");
                this.LteSinr = json.getString("LteSinr");
                this.InnerIP = json.getString("InnerIP");
                this.Ecio = json.getString("Ecio");
                this.Snr = json.getString("Snr");
                this.LteRsrq = json.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        PhoneInfo = new JSONObject();
        PositionInfo = new JSONObject();
        NetInfo = new JSONObject();
        TestResult = new JSONObject();
        outPut = new JSONObject();

        try {
            PhoneInfo.put("IMSI", IMSI);
            PhoneInfo.put("MEID", MEID);
            PhoneInfo.put("PhoneType", PhoneType);
            PhoneInfo.put("OSVersion", OSVersion);
            PhoneInfo.put("BaseBand", BaseBand);
            PhoneInfo.put("Kernel", Kernel);
            PhoneInfo.put("InnerVersion", InnerVersion);
            PhoneInfo.put("RamUsage", RamUsage);
            PhoneInfo.put("CpuUsage", CpuUsage);

            PositionInfo.put("Longitude", Longitude);
            PositionInfo.put("Latitude", Latitude);
            PositionInfo.put("LocationDesc", LocationDesc);
            PositionInfo.put("Province", Province);
            PositionInfo.put("City", City);
            PositionInfo.put("Source", Source);


            NetInfo.put("NetType", NetType);
            NetInfo.put("APN", APN);
            NetInfo.put("CdmaSid", CdmaSid);
            NetInfo.put("CdmaNid", CdmaNid);
            NetInfo.put("CdmaBsid", CdmaBsid);
            NetInfo.put("CdmadBm", CdmadBm);
            NetInfo.put("LteCi", LteCi);
            NetInfo.put("LtePci", LtePci);
            NetInfo.put("LteTac", LteTac);
            NetInfo.put("LteRsrp", LteRsrp);
            NetInfo.put("LteSinr", LteSinr);
            NetInfo.put("InnerIP", InnerIP);
            NetInfo.put("OuterIP", OuterIP);
            NetInfo.put("Ecio", Ecio);
            NetInfo.put("Snr", Snr);
            NetInfo.put("LteRsrq", LteRsrq);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());


            TestResult.put("WebsiteName", WebsiteName);
            TestResult.put("PageURL", PageURL);
            TestResult.put("PageIP", PageIP);
            TestResult.put("PageSurfTime", time);
            TestResult.put("FirstByteDelay", FirstByteDelay);
            TestResult.put("PageOpenDelay", PageOpenDelay);
            TestResult.put("RRCSetupDelay", RRCSetupDelay);
            TestResult.put("DnsDelay", DnsDelay);
            TestResult.put("ConnDelay", ConnDelay);
            TestResult.put("ReqDelay", ReqDelay);
            TestResult.put("ResDelay", ResDelay);
            TestResult.put("TCLASS", TCLASS);
            TestResult.put("Success", Success);
            TestResult.put("DnsIP", DnsIP);
            TestResult.put("PageSize", PageSize);
            TestResult.put("PageAvgSpeed", PageAvgSpeed);

            outPut.put("PhoneInfo", PhoneInfo);
            outPut.put("PositionInfo", PositionInfo);
            outPut.put("NetInfo", NetInfo);
            outPut.put("TestResult", TestResult);
            Log.i(TAG, "output" + outPut.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }


        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringBuffer url = new StringBuffer(CoConfig.HTTP_SERVER_URL).append("CommonTestData.do");
                //.append("BusiSiteData.do?");
                SharedPreferences share = context.getSharedPreferences(CoConfig.SHARE_TAG, 0);
                String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
                //url.append("userId=").append(userId);
                AppHttpClient client = new AppHttpClient(context);
                String outPutUTF8 = outPut.toString();
                /*try {
                    outPutUTF8 = URLEncoder.encode(outPut.toString(), App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                final String conResult = client.getResultByPOST(url.toString(),
                        outPutUTF8);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
        }.start();
    }

    public void postDataZhangshangyingyeting(final OnGetDataFinishedListener lr, final JSONObject json, List<HttpTestRecordsInfo> list, int upLoadState) {
        // TODO Auto-generated method stub
        PostBusiSiteData post = new PostBusiSiteData(context);
        for (HttpTestRecordsInfo testResult : list
                ) {
            if (testResult.http_effective_url.equals("掌上营业厅")) {

                //this.WebsiteName = new String(testResult.http_effective_url.getBytes("UTF-8"), "UTF-8");

                //this.WebsiteName = URLEncoder.encode(testResult.http_effective_url, App.ENCODE_UTF8);
                this.WebsiteName = "掌上营业厅";
                this.PageURL = "http://wap.zj.ct10000.com";
                this.PageIP = testResult.http_primary_ip;
                this.PageSurfTime = testResult.testTime;
                this.FirstByteDelay = testResult.http_first_delay;
                this.PageOpenDelay = testResult.http_total_time;
                this.RRCSetupDelay = "";
                this.DnsDelay = testResult.http_namelookup_time;
                this.ConnDelay = testResult.http_connect_time;
                this.ReqDelay = testResult.http_pretransfer_time;
                this.ResDelay = testResult.http_starttransfer_time;
                this.TCLASS = "1";
                this.Success = "1";
                this.DnsIP = "";
                this.PageSize = testResult.http_size_download;
                this.PageAvgSpeed = testResult.http_speed_download;
                this.OuterIP = testResult.http_primary_ip;
            }


        }
        if (upLoadState == upLoadToFunction) {
            try {
                this.IMSI = LteSignalMainActivity.LTE.getString("IMSI");
                this.MEID = LteSignalMainActivity.LTE.getString("MEID");
                this.PhoneType = LteSignalMainActivity.LTE.getString("PhoneType");
                this.OSVersion = LteSignalMainActivity.LTE.getString("OSVersion");
                this.BaseBand = LteSignalMainActivity.LTE.getString("BaseBand");
                this.Kernel = LteSignalMainActivity.LTE.getString("Kernel");
                this.InnerVersion = LteSignalMainActivity.LTE.getString("InnerVersion");
                this.RamUsage = LteSignalMainActivity.LTE.getString("RamUsage");
                this.CpuUsage = LteSignalMainActivity.LTE.getString("CpuUsage");
                this.Longitude = LteSignalMainActivity.LTE.getString("Longitude");
                this.Latitude = LteSignalMainActivity.LTE.getString("Latitude");
                //this.LocationDesc = "China";
                //服务器无法识别中文
                // this.LocationDesc= URLEncoder.encode(LteSignalMainActivity.LTE.getString("LocationDesc"), App.ENCODE_UTF8);
                this.LocationDesc = LteSignalMainActivity.LTE.getString("LocationDesc");
                // this.Province=URLEncoder.encode(LteSignalMainActivity.LTE.getString("Province"),App.ENCODE_UTF8);
                this.Province = LteSignalMainActivity.LTE.getString("Province");
                // this.City=URLEncoder.encode(LteSignalMainActivity.LTE.getString("City"),App.ENCODE_UTF8);
                this.City = LteSignalMainActivity.LTE.getString("City");
                this.Source = LteSignalMainActivity.LTE.getString("Source");
                this.NetType = LteSignalMainActivity.LTE.getString("NetType");
                this.APN = LteSignalMainActivity.LTE.getString("APN");
                this.CdmaSid = LteSignalMainActivity.LTE.getString("CdmaSid");
                this.CdmaNid = LteSignalMainActivity.LTE.getString("CdmaNid");
                this.CdmaBsid = LteSignalMainActivity.LTE.getString("CdmaBsid");
                this.CdmadBm = LteSignalMainActivity.LTE.getString("CdmadBm");
                this.LteCi = LteSignalMainActivity.LTE.getString("LteCi");
                this.LtePci = LteSignalMainActivity.LTE.getString("LtePci");
                this.LteTac = LteSignalMainActivity.LTE.getString("LteTac");
                this.LteRsrp = LteSignalMainActivity.LTE.getString("LteRsrp");
                this.LteSinr = LteSignalMainActivity.LTE.getString("LteSinr");
                this.InnerIP = LteSignalMainActivity.LTE.getString("InnerIP");
                this.Ecio = LteSignalMainActivity.LTE.getString("Ecio");
                this.Snr = LteSignalMainActivity.LTE.getString("Snr");
                this.LteRsrq = LteSignalMainActivity.LTE.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (upLoadState == upLoadForce) {
            try {
                this.IMSI = json.getString("IMSI");
                this.MEID = json.getString("MEID");
                this.PhoneType = json.getString("PhoneType");
                this.OSVersion = json.getString("OSVersion");
                this.BaseBand = json.getString("BaseBand");
                this.Kernel = json.getString("Kernel");
                this.InnerVersion = json.getString("InnerVersion");
                this.RamUsage = json.getString("RamUsage");
                this.CpuUsage = json.getString("CpuUsage");
                this.Longitude = json.getString("Longitude");
                this.Latitude = json.getString("Latitude");
                //this.LocationDesc = "China";
                this.LocationDesc = json.getString("LocationDesc");
                //服务器无法识别中文
                //this.LocationDesc=URLEncoder.encode(json.getString("LocationDesc"),App.ENCODE_UTF8);
                this.Province = json.getString("Province");
                this.City = json.getString("City");
                this.Source = json.getString("Source");
                this.NetType = json.getString("NetType");
                this.APN = json.getString("APN");
                this.CdmaSid = json.getString("CdmaSid");
                this.CdmaNid = json.getString("CdmaNid");
                this.CdmaBsid = json.getString("CdmaBsid");
                this.CdmadBm = json.getString("CdmadBm");
                this.LteCi = json.getString("LteCi");
                this.LtePci = json.getString("LtePci");
                this.LteTac = json.getString("LteTac");
                this.LteRsrp = json.getString("LteRsrp");
                this.LteSinr = json.getString("LteSinr");
                this.InnerIP = json.getString("InnerIP");
                this.Ecio = json.getString("Ecio");
                this.Snr = json.getString("Snr");
                this.LteRsrq = json.getString("LteRsrq");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        PhoneInfo = new JSONObject();
        PositionInfo = new JSONObject();
        NetInfo = new JSONObject();
        TestResult = new JSONObject();
        outPut = new JSONObject();


        try {
            PhoneInfo.put("IMSI", IMSI);
            PhoneInfo.put("MEID", MEID);
            PhoneInfo.put("PhoneType", PhoneType);
            PhoneInfo.put("OSVersion", OSVersion);
            PhoneInfo.put("BaseBand", BaseBand);
            PhoneInfo.put("Kernel", Kernel);
            PhoneInfo.put("InnerVersion", InnerVersion);
            PhoneInfo.put("RamUsage", RamUsage);
            PhoneInfo.put("CpuUsage", CpuUsage);

            PositionInfo.put("Longitude", Longitude);
            PositionInfo.put("Latitude", Latitude);
            PositionInfo.put("LocationDesc", LocationDesc);
            PositionInfo.put("Province", Province);
            PositionInfo.put("City", City);
            PositionInfo.put("Source", Source);


            NetInfo.put("NetType", NetType);
            NetInfo.put("APN", APN);
            NetInfo.put("CdmaSid", CdmaSid);
            NetInfo.put("CdmaNid", CdmaNid);
            NetInfo.put("CdmaBsid", CdmaBsid);
            NetInfo.put("CdmadBm", CdmadBm);
            NetInfo.put("LteCi", LteCi);
            NetInfo.put("LtePci", LtePci);
            NetInfo.put("LteTac", LteTac);
            NetInfo.put("LteRsrp", LteRsrp);
            NetInfo.put("LteSinr", LteSinr);
            NetInfo.put("InnerIP", InnerIP);
            NetInfo.put("OuterIP", OuterIP);
            NetInfo.put("Ecio", Ecio);
            NetInfo.put("Snr", Snr);
            NetInfo.put("LteRsrq", LteRsrq);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());


            TestResult.put("WebsiteName", WebsiteName);
            TestResult.put("PageURL", PageURL);
            TestResult.put("PageIP", PageIP);
            TestResult.put("PageSurfTime", time);
            TestResult.put("FirstByteDelay", FirstByteDelay);
            TestResult.put("PageOpenDelay", PageOpenDelay);
            TestResult.put("RRCSetupDelay", RRCSetupDelay);
            TestResult.put("DnsDelay", DnsDelay);
            TestResult.put("ConnDelay", ConnDelay);
            TestResult.put("ReqDelay", ReqDelay);
            TestResult.put("ResDelay", ResDelay);
            TestResult.put("TCLASS", TCLASS);
            TestResult.put("Success", Success);
            TestResult.put("DnsIP", DnsIP);
            TestResult.put("PageSize", PageSize);
            TestResult.put("PageAvgSpeed", PageAvgSpeed);

            outPut.put("PhoneInfo", PhoneInfo);
            outPut.put("PositionInfo", PositionInfo);
            outPut.put("NetInfo", NetInfo);
            outPut.put("TestResult", TestResult);

            Log.i(TAG, "output" + outPut.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }


        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringBuffer url = new StringBuffer(CoConfig.HTTP_SERVER_URL).append("CommonTestData.do");
                //.append("BusiSiteData.do?");
                SharedPreferences share = context.getSharedPreferences(CoConfig.SHARE_TAG, 0);
                String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
                //url.append("userId=").append(userId);
                AppHttpClient client = new AppHttpClient(context);
                ALog.json(outPut.toString());
                String outPutUTF8 = outPut.toString();
                /*try {
                    outPutUTF8 = URLEncoder.encode(outPut.toString(), App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                ALog.json(outPutUTF8);
                final String conResult = client.getResultByPOST(url.toString(),
                        outPutUTF8);
                ALog.json(conResult);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
        }.start();
    }
}
