package cn.com.chinaccs.datasite.main.ui.lte;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.adapter.SiteGridAdapter;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.bean.Collector;
import cn.com.chinaccs.datasite.main.bean.HttpTestRecordsInfo;
import cn.com.chinaccs.datasite.main.bean.InfoCell;
import cn.com.chinaccs.datasite.main.bean.InfoConnectivity;
import cn.com.chinaccs.datasite.main.bean.WebSite;
import cn.com.chinaccs.datasite.main.common.AppNetWork;
import cn.com.chinaccs.datasite.main.connect.GetWebSite;
import cn.com.chinaccs.datasite.main.connect.OnGetDataFinishListener;
import cn.com.chinaccs.datasite.main.connect.PostBusiSiteData;
import cn.com.chinaccs.datasite.main.data.BusiSitesResult;
import cn.com.chinaccs.datasite.main.data.FuncGetSysDatas;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.ui.functions.NetworkTest;

@SuppressLint("HandlerLeak")
public class HttpTestActivity extends BaseActivity {
    private static final String TAG = HttpTestActivity.class.getSimpleName();

    Context context;
    private View v;
    private GridView gridSites;
    private Button btnStart;
    private ArrayList<WebSite> listSites;
    private SiteGridAdapter saMenuItems;
    //	private TextView tvTestResult;
    private String testResult;
    private BusiSitesResult bsr;
    public static BDGeoLocation geoGB;
    public static BDGeoLocation geo;
    public JSONObject busiJsonDatas;
    private TableLayout layout;

    private Button moreInfoBtn;
    private String address;
    private String testTime;
    private String addr;
    private List<HttpTestRecordsInfo> upLoadList;

    //网站测试上传状态为通过功能上传
    private final int upLoadState = 0;
    //限制只能上传一次
    private int httptestTime = 0;

    private JSONObject LTE;
    private NetworkTest networkTest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        this.setContentView(R.layout.activity_sites);
        initToolbar(getResources().getString(R.string.busi_site));
        geoGB = MainApp.geoBD;
        findViews();
        networkTest = new NetworkTest(context);
        btnStart.setOnClickListener(clr);
        buildList();
        // 缓存的view需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
        /*ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null) {
            parent.removeView(v);
        }*/


        /*tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(celllistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);*/
        list = new ArrayList<Collector>();


        //geo = ((MainApp) ((Activity) context).getApplication()).geo;
    }


    private void buildList() {
        // TODO Auto-generated method stub
        listSites = new ArrayList<WebSite>();
        String[] sitenames = context.getResources().getStringArray(
                R.array.sitesBusiName);
        String[] urls = context.getResources().getStringArray(R.array.sitesBusiUrls);

        int[] imgs = {R.drawable.baidu, R.drawable.leshi, R.drawable.taobao,
                R.drawable.xinlang, R.drawable.wangyi_news, R.drawable.ctchandhold};
        for (int i = 0; i < sitenames.length; i++) {
            WebSite site = new WebSite();
            site.setName(sitenames[i]);
            site.setIconRes(imgs[i]);
            site.setUrl(urls[i]);
            site.setChecked(true);
            site.setClassify(0);
            site.setId(i);
            site.setState(WebSite.STATE_WAIT);
            listSites.add(site);
        }
        saMenuItems = new SiteGridAdapter(context, listSites);
        // 添加Item到网格中
        gridSites.setAdapter(saMenuItems);
        gridSites.setOnItemClickListener(gridClt);
    }

    OnItemClickListener gridClt = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> ad, View v, int index, long arg3) {
//			WebSite site = new WebSite();
//			site = listSites.get(index);
//			Intent it = new Intent(context, SiteContentActivity.class);
//			it.putExtra("url", site.getUrl());
//			it.putExtra("title", site.getName());
//			startActivity(it);
        }
    };

    private String doUploc() {
        String addr = "";
        if (geoGB.locClient.isStarted()) {
            geoGB.locClient.requestLocation();
            BDLocation loc = geoGB.location;
            if (loc != null && loc.hasAddr()) {
                addr = loc.getAddrStr();
            }
            Log.d(TAG, "doUploc" + addr);
        }
        return addr;
    }

    private Map<String, String> signalMap;
    private List<Collector> list;
    private TelephonyManager tm;
    private PhoneStateListener celllistener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signal) {
            // TODO Auto-generated method stub
            super.onSignalStrengthsChanged(signal);
            try {
                Method[] methods = android.telephony.SignalStrength.class
                        .getMethods();
                signalMap = new HashMap<String, String>();
                for (Method mthd : methods) {
                    if (mthd.getName().equals("getLteSignalStrength")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        if (sig != 0)
                            sig = -113 + 2 * sig;
                        // Log.d(CoConfig.LOG_TAG, "lte--signal---" + sig);
                        signalMap.put("LTE_Signal", Integer.toString(sig));
                    } else if (mthd.getName().equals("getLteRsrp")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        signalMap.put("LTE_RSRP", Integer.toString(sig));
                    } else if (mthd.getName().equals("getLteRsrq")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        signalMap.put("LTE_RSRQ", Integer.toString(sig));
                    } else if (mthd.getName().equals("getLteRssnr")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        signalMap.put("LTE_SINR", Integer.toString(sig / 10));
                    } else if (mthd.getName().equals("getGsmSignalStrength")) {
                        int sig = (Integer) mthd
                                .invoke(signal, new Object[]{});
                        if (sig != 0)
                            sig = -113 + 2 * sig;
                        signalMap.put("Signal", Integer.toString(sig));
                    }
                }
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (tm != null
                    && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                signalMap.put("CDMA_Signal",
                        Integer.toString(signal.getCdmaDbm()));
                signalMap.put("CDMA_Ecio",
                        Integer.toString(signal.getCdmaEcio() / 10));
                signalMap.put("EVDO_Signal",
                        Integer.toString(signal.getEvdoDbm()));
                signalMap.put("EVDO_Ecio",
                        Integer.toString(signal.getEvdoEcio() / 10));
                signalMap
                        .put("EVDO_Snr", Integer.toString(signal.getEvdoSnr()));
                int level = (signal.getCdmaDbm() + 113) / 2;
                signalMap.put("Asu_level", Integer.toString(level));
            }

            singalChange();
        }

        @Override
        public void onCellLocationChanged(CellLocation location) {
            // TODO Auto-generated method stub
            super.onCellLocationChanged(location);
            initCollectInfo();
            singalChange();
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            // TODO Auto-generated method stub
            super.onDataConnectionStateChanged(state);
            initCollectInfo();
            singalChange();
        }
    };

    private void initCollectInfo() {
        if (list == null) {
            list = new ArrayList<Collector>();
        }
        list.clear();

        // add the location
        BDGeoLocation location = new BDGeoLocation(context);
        addr = "";
        double lat = 0;
        double lon = 0;
        if (geoGB.locClient != null) {
            geoGB.locClient.requestLocation();
            BDLocation loc = geoGB.location;
            if (loc != null && loc.hasAddr()) {
                addr = loc.getAddrStr();
                lat = loc.getLatitude();
                lon = loc.getLongitude();
                Log.d(TAG, "获取到位置信息" + addr + "" + lat + "" + lon);
            }
        }
        list.add(new Collector("坐标位置信息", "", 0, Collector.DATA_TYPE_TITLE));
        list.add(new Collector("Lat", lat + "", 0, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("Lon", lon + "", 0, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("Addr", addr, 0, Collector.DATA_TYPE_UPLOAD));
        InfoCell cell = new InfoCell(context);
        list = cell.getCellInfo(list);
        InfoConnectivity ct = new InfoConnectivity(context);
        list = ct.getConectiviyInfo(list);
        Log.d(TAG, "显示获取到的位置信息" + addr + "" + lat + "" + lon);
    }

    private void singalChange() {
        if (signalMap == null)
            return;
        if (list == null)
            return;
        if (tm != null && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            list.get(getPosition("CDMA_Signal")).setDataValue(
                    signalMap.get("CDMA_Signal"));
            list.get(getPosition("CDMA_Ecio")).setDataValue(
                    signalMap.get("CDMA_Ecio"));
            list.get(getPosition("EVDO_Signal")).setDataValue(
                    signalMap.get("EVDO_Signal"));
            list.get(getPosition("EVDO_Ecio")).setDataValue(
                    signalMap.get("EVDO_Ecio"));
            list.get(getPosition("EVDO_Snr")).setDataValue(
                    signalMap.get("EVDO_Snr"));
            list.get(getPosition("Asu_level")).setDataValue(signalMap.get("Asu_level"));

        } else {
            list.get(getPosition("Signal")).setDataValue(
                    signalMap.get("Signal"));
        }
        if (signalMap.containsKey("LTE_Signal")
                && signalMap.containsKey("LTE_RSRP")
                && signalMap.containsKey("LTE_RSRQ")
                && signalMap.containsKey("LTE_SINR")) {
            list.get(getPosition("LTE_Signal")).setDataValue(
                    signalMap.get("LTE_Signal"));
            list.get(getPosition("LTE_RSRP")).setDataValue(
                    signalMap.get("LTE_RSRP"));
            list.get(getPosition("LTE_RSRQ")).setDataValue(
                    signalMap.get("LTE_RSRQ"));
            list.get(getPosition("LTE_SINR")).setDataValue(
                    signalMap.get("LTE_SINR"));
        }
    }

    private int getPosition(String name) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (name.equalsIgnoreCase(list.get(i).getDataName())) {
                position = i;
                break;
            }
        }
        return position;
    }

    private OnClickListener clr = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_site_start:
                    getSysInfo();
                    startTest();
                    break;
            }
        }
    };

    private void getSysInfo() {
        networkTest.setLTE();
        busiJsonDatas = new JSONObject();
        FuncGetSysDatas fgsd = new FuncGetSysDatas(context);
        busiJsonDatas = fgsd.getSysJSON();
        /*JSONObject CDMA = new JSONObject();
        try {
			CDMA.put("CDMA_SID", list.get(getPosition("sid")).getDataValue());
			CDMA.put("CDMA_NID", list.get(getPosition("nid")).getDataValue());
			CDMA.put("CMMA_BSID", list.get(getPosition("CI")).getDataValue());
			CDMA.put("CDMA_Signal", list.get(getPosition("CDMA_Signal")).getDataValue() + "dBm");

			busiJsonDatas.put("CDMA", CDMA);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject LTE = new JSONObject();
		try {
			LTE.put("LTE_dBm", list.get(getPosition("LTE_RSRP")).getDataValue());
			LTE.put("LTE_RSRP", list.get(getPosition("LTE_RSRP")).getDataValue());
			LTE.put("LTE_RSRQ", list.get(getPosition("LTE_RSRQ")).getDataValue());
			LTE.put("LTE_SINR", list.get(getPosition("LTE_SINR")).getDataValue());
			LTE.put("LTE_CI", list.get(getPosition("LTE_CI")).getDataValue());
			LTE.put("LTE_Pci", list.get(getPosition("LTE_Pci")).getDataValue());
			LTE.put("LTE_Tac", list.get(getPosition("LTE_Tac")).getDataValue());

			busiJsonDatas.put("LTE", LTE);
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
        //String addr = doUploc();
        address = addr;
        Log.d(TAG, "getSysInfo" + address);
        try {
            addr = URLEncoder.encode(addr, CoConfig.ENCODE_UTF8);
            busiJsonDatas.put("addr", addr);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (testTime == null) {
            Calendar cr = Calendar.getInstance(Locale.CHINA);
            testTime = CoConfig.getDateToStr(cr, 0);
        }
        try {
            testTime = busiJsonDatas.getJSONObject("sim").getString("dateItem");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startTest() {

//		tvTestResult.setText("");
        moreInfoBtn.setVisibility(View.GONE);
        layout.removeAllViews();
        if (!AppNetWork.isNetWork(context)) {
            Toast.makeText(context, getString(R.string.common_not_network),
                    Toast.LENGTH_LONG).show();
            return;
        }
        GetWebSite func = new GetWebSite(this.getApplicationContext());
        for (int i = 0; i < listSites.size(); i++) {
            listSites.get(i).setState(WebSite.STATE_WAIT);
        }

        OnGetDataFinishListener lr = new OnGetDataFinishListener() {

            public void onFinishJSON(JSONObject output) {
                // TODO Auto-generated method stub
                try {
                    busiJsonDatas.put("busiResult", output);
                    Log.d(CoConfig.LOG_TAG + "-网站测试数据-", output.toString());
                    //MainActivity.mainTestResult.put("busiResult", output);
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinishList(List list) {
                if (!(list == null)) {
                    upLoadList = list;
                }

            }

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                testResult = output;
                handler.sendEmptyMessage(0);
//				bsr = new BusiSitesResult(tvTestResult, testResult);
//				bsr.execute();
            }
        };
        func.getData(lr, listSites, saMenuItems, testTime, address);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0:
                    buildResultTable();
                    break;
                case 1: {
                    LTE = networkTest.getLTE();
                    Log.i(TAG, "LTE" + LTE.toString());
                    moreInfoBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "网站测试完成！",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, busiJsonDatas.toString());
                    PostBusiSiteData post = new PostBusiSiteData(context);
                    //服务端返回的数据在这里回调
                    OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

                        @Override
                        public void onFinished(String output) {
                            // TODO Auto-generated method stub
                            networkTest.stopListener();
                            if (httptestTime == 5) {
                                JSONObject responseJson = null;
                                String result = null;
                                String msg = null;
                                Log.d(TAG, "output:" + output);
                                if (output.equals("fail")) {
                                    Toast.makeText(
                                            context,
                                            getResources().getString(
                                                    R.string.common_not_network),
                                            Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    try {
                                        responseJson = new JSONObject(output);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        result = responseJson.getString("result");
                                        msg = responseJson.getString("msg");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (result.equals("1")) {
                                        Toast.makeText(context, msg,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "上传失败",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }


/*                            if (output.equals(AppHttpClient.RESULT_FAIL)) {

                            } else {
                                Toast.makeText(context, "测试结果上传成功！下一步请进行视频测试！",
                                        Toast.LENGTH_LONG).show();
                            }*/
                                httptestTime = 0;
                            } else {
                                httptestTime++;
                            }
                        }


                    };
                    post.postDataBaidu(lr, LTE, upLoadList, upLoadState);
                    post.postDataLeshiwang(lr, LTE, upLoadList, upLoadState);
                    post.postDataTaobao(lr, LTE, upLoadList, upLoadState);
                    post.postDataXinglang(lr, LTE, upLoadList, upLoadState);
                    post.postDataWangyi(lr, LTE, upLoadList, upLoadState);
                    post.postDataZhangshangyingyeting(lr, LTE, upLoadList, upLoadState);


                    /*Intent intent = new Intent(HttpTestActivity.this, HttpTestRecordsDetailActivity.class);
                    intent.putExtra("data", testTime);
                    startActivity(intent);*/
                }
                break;
                default:
                    break;
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    private void buildResultTable() {
        String[] sitenames = context.getResources().getStringArray(
                R.array.sitesBusiName);
        try {
            JSONObject busiResult = new JSONObject(testResult);

            LayoutInflater littitle = LayoutInflater.from(context);
            View viewItemt = littitle.inflate(R.layout.busi_sites_result, null);
            layout.addView(viewItemt);

            for (int i = 0; i < sitenames.length + 1; i++) {
                if (i < sitenames.length) {
                    LayoutInflater li = LayoutInflater.from(context);
                    View vi = li.inflate(R.layout.busi_sites_eva, null);
                    LinearLayout ly = (LinearLayout) vi.findViewById(R.id.lleft);

                    TextView tv1 = (TextView) vi.findViewById(R.id.tv_busi_eva_1);
                    TextView tv2 = (TextView) vi.findViewById(R.id.tv_busi_eva_2);
                    TextView tv3 = (TextView) vi.findViewById(R.id.tv_busi_eva_3);
                    TextView tv4 = (TextView) vi.findViewById(R.id.tv_busi_eva_4);
                    TextView tv5 = (TextView) vi.findViewById(R.id.tv_busi_eva_5);

                    if (i % 2 == 0) {
                        ly.setBackground(getResources().getDrawable(R.drawable.tv_border));
//						tv1.setBackground(getResources().getDrawable(R.drawable.tv_border));
                        tv2.setBackground(getResources().getDrawable(R.drawable.tv_border));
                        tv3.setBackground(getResources().getDrawable(R.drawable.tv_border));
                        tv4.setBackground(getResources().getDrawable(R.drawable.tv_border));
                        tv5.setBackground(getResources().getDrawable(R.drawable.tv_border));
                    } else {
                        ly.setBackground(getResources().getDrawable(R.drawable.tv_border1));
//						tv1.setBackground(getResources().getDrawable(R.drawable.tv_border1));
                        tv2.setBackground(getResources().getDrawable(R.drawable.tv_border1));
                        tv3.setBackground(getResources().getDrawable(R.drawable.tv_border1));
                        tv4.setBackground(getResources().getDrawable(R.drawable.tv_border1));
                        tv5.setBackground(getResources().getDrawable(R.drawable.tv_border1));
                    }

//					tv1.setBackgroundColor(R.color.green_deep);

                    tv1.setText(sitenames[i]);
                    tv2.setText(busiResult.getString("http_first_delay" + i));
                    tv3.setText(busiResult.getString("http_total_time" + i));
                    tv4.setText(busiResult.getString("http_speed_download" + i));
                    String type = busiResult.getString("siteType" + i);
                    tv5.setText(level(i, type));
                    layout.addView(vi);
                } else {
                    LayoutInflater lit = LayoutInflater.from(context);
                    View viewItem = lit.inflate(R.layout.busi_sites_result, null);
                    TextView result1 = (TextView) viewItem.findViewById(R.id.tv_busi_result_1);
                    TextView result2 = (TextView) viewItem.findViewById(R.id.tv_busi_result_2);
                    TextView result3 = (TextView) viewItem.findViewById(R.id.tv_busi_result_3);
                    TextView result4 = (TextView) viewItem.findViewById(R.id.tv_busi_result_4);

                    result1.setText("平均值");
                    result2.setText(busiResult.getString("avgFirDefer"));
                    result3.setText(busiResult.getString("avgFuDefer"));
                    result4.setText(busiResult.getString("avgSpeed"));

                    layout.addView(viewItem);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String level(int i, String type) {
        String str = "";
        if (i == 0) {
            if (type.equals("1")) {
                str = getString(R.string.busi_baidu_ave4);
            } else if (type.equals("2")) {
                str = getString(R.string.busi_baidu_ave3);
            } else if (type.equals("3")) {
                str = getString(R.string.busi_baidu_ave2);
            } else {
                str = getString(R.string.busi_baidu_ave1);
            }
        } else if (i == 1) {
            if (type.equals("1")) {
                str = getString(R.string.busi_leshi_ave4);
            } else if (type.equals("2")) {
                str = getString(R.string.busi_leshi_ave3);
            } else if (type.equals("3")) {
                str = getString(R.string.busi_leshi_ave2);
            } else {
                str = getString(R.string.busi_leshi_ave1);
            }
        } else if (i == 2) {
            if (type.equals("1")) {
                str = getString(R.string.busi_taobao_ave4);
            } else if (type.equals("2")) {
                str = getString(R.string.busi_taobao_ave3);
            } else if (type.equals("3")) {
                str = getString(R.string.busi_taobao_ave2);
            } else {
                str = getString(R.string.busi_taobao_ave1);
            }
        } else if (i == 3 || i == 4) {
            if (type.equals("1")) {
                str = getString(R.string.busi_zx_ave4);
            } else if (type.equals("2")) {
                str = getString(R.string.busi_zx_ave3);
            } else if (type.equals("3")) {
                str = getString(R.string.busi_zx_ave2);
            } else {
                str = getString(R.string.busi_zx_ave1);
            }
        } else if (i == 5) {
            if (type.equals("1")) {
                str = getString(R.string.busi_zt_ave4);
            } else if (type.equals("2")) {
                str = getString(R.string.busi_zt_ave3);
            } else if (type.equals("3")) {
                str = getString(R.string.busi_zt_ave2);
            } else {
                str = getString(R.string.busi_zt_ave1);
            }
        }
        return str;
    }

    private void findViews() {
        // TODO Auto-generated method stub
        gridSites = (GridView) findViewById(R.id.grid_sites);
        btnStart = (Button) findViewById(R.id.btn_site_start);
//		tvTestResult = (TextView) v.findViewById(R.id.tv_test_result);
        layout = (TableLayout) findViewById(R.id.tl_busi_result);
        moreInfoBtn = (Button) findViewById(R.id.btn_site_test_records);
        moreInfoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HttpTestActivity.this, HttpTestRecordsActivity.class);
                startActivity(intent);
            }
        });
        initCollectInfo();

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        networkTest.stopListener();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

}
