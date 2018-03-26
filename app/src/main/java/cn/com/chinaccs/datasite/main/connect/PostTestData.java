package cn.com.chinaccs.datasite.main.connect;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;


public class PostTestData {
    private Context context;
    private final String TAG = PostTestData.class.getSimpleName();

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

    public PostTestData(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void postData(final OnGetDataFinishedListener lr, final JSONObject json,
                         final JSONObject LTE) {
        // TODO Auto-generated method stub
        PhoneInfo = new JSONObject();
        PositionInfo = new JSONObject();
        NetInfo = new JSONObject();
        TestResult = new JSONObject();
        outPut = new JSONObject();
        try {
            this.IMSI = LTE.getString("IMSI");
            this.MEID = LTE.getString("MEID");
            this.PhoneType = LTE.getString("PhoneType");
            this.OSVersion = LTE.getString("OSVersion");
            this.BaseBand = LTE.getString("BaseBand");
            this.Kernel = LTE.getString("Kernel");
            this.InnerVersion = LTE.getString("InnerVersion");
            this.RamUsage = LTE.getString("RamUsage");
            this.CpuUsage = LTE.getString("CpuUsage");
            this.Longitude = LTE.getString("Longitude");
            this.Latitude = LTE.getString("Latitude");
            this.LocationDesc = LTE.getString("LocationDesc");
            //服务器无法识别中文
            //this.LocationDesc=URLEncoder.encode(LteSignalMainActivity.LTE.getString("IMSI"),App.ENCODE_UTF8);
            this.Province = LTE.getString("Province");
            this.City = LTE.getString("City");
            this.Source = LTE.getString("Source");
            this.NetType = LTE.getString("NetType");
            this.APN = LTE.getString("APN");
            this.CdmaSid = LTE.getString("CdmaSid");
            this.CdmaNid = LTE.getString("CdmaNid");
            this.CdmaBsid = LTE.getString("CdmaBsid");
            this.CdmadBm = LTE.getString("CdmadBm");
            this.LteCi = LTE.getString("LteCi");
            this.LtePci = LTE.getString("LtePci");
            this.LteTac = LTE.getString("LteTac");
            this.LteRsrp = LTE.getString("LteRsrp");
            this.LteSinr = LTE.getString("LteSinr");
            this.InnerIP = LTE.getString("InnerIP");
            this.OuterIP = LTE.getString("OuterIP");
            this.Ecio = LTE.getString("Ecio");
            this.Snr = LTE.getString("Snr");
            this.LteRsrq = LTE.getString("LteRsrq");

            Log.d(TAG, "Ltemain" +LTE.toString());
            Log.d(TAG, "json" + json.toString());

            Log.d(TAG, "开始拼接json");
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

            TestResult.put("VideoName", json.get("VideoName"));
            TestResult.put("VideoURL", json.get("VideoURL"));
            TestResult.put("VideoIP", json.get("VideoIP"));
            TestResult.put("VideoTestTime", time);
            TestResult.put("VideoAvgSpeed", json.get("VideoAvgSpeed"));
            TestResult.put("VideoPeakSpeed", json.get("VideoPeakSpeed"));
            TestResult.put("TCLASS", json.get("TCLASS"));
            TestResult.put("BufferCounter", json.get("BufferCounter"));
            TestResult.put("VideoSize", json.get("VideoSize"));
            TestResult.put("VideoTotleTraffic", json.get("VideoTotleTraffic"));

            outPut.put("PhoneInfo", PhoneInfo);
            outPut.put("PositionInfo", PositionInfo);
            outPut.put("NetInfo", NetInfo);
            outPut.put("TestResult", TestResult);

            Log.d(TAG, "PhoneInfo" + PhoneInfo);
            Log.d(TAG, "PositionInfo" + PositionInfo);
            Log.d(TAG, "NetInfo" + NetInfo);
            Log.d(TAG, "TestResult" + TestResult);


            Log.i(TAG, "outPut" + outPut.toString());

        } catch (Exception e) {

            Log.d(TAG, "exception" + e.toString());
        }


        final Handler hr = new Handler();
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringBuffer url = new StringBuffer(CoConfig.HTTP_SERVER_URL)
                        .append("BusiSiteData.do");

                //SharedPreferences share = context.getSharedPreferences(CoConfig.SHARE_TAG, 0);
                //String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
                //.append("&userId=").append(userId);
                String outPutUTF8 = null;
                try {
                    outPutUTF8 = URLEncoder.encode(outPut.toString(), App.ENCODE_UTF8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                AppHttpClient client = new AppHttpClient(context);
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
}
