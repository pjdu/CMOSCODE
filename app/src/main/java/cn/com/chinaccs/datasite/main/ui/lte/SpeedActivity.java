package cn.com.chinaccs.datasite.main.ui.lte;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.XFastFactory.XDbUtils.XDbUtils;
import com.baidu.location.BDLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.bean.Collector;
import cn.com.chinaccs.datasite.main.bean.InfoCell;
import cn.com.chinaccs.datasite.main.bean.InfoConnectivity;
import cn.com.chinaccs.datasite.main.bean.InfoSpeedRecords;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.common.AppNetWork;
import cn.com.chinaccs.datasite.main.connect.GetESFileList;
import cn.com.chinaccs.datasite.main.data.DoPingTask;
import cn.com.chinaccs.datasite.main.data.FuncGetSysDatas;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;

import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.util.MultiDownLoadUtil;
import cn.com.chinaccs.datasite.main.util.UploadUtil;
import cn.com.chinaccs.datasite.main.util.UtilMemory;
import cn.com.chinaccs.datasite.main.widget.DynamicCoverChart;

public class SpeedActivity extends BaseActivity {
    private static final String TAG = "SpeedActivity";
    public static BDGeoLocation geoGB;
    public static BDGeoLocation geo;
    Context context;
    TextView tvNm;
    ImageView imgNeedle;
    TextView tvUnit;
    Button btnStart;
    ProgressBar pbState;
    TextView tvState;
    TextView tvDownMaxs;
    TextView tvUpMaxs;
    TextView tvDown;
    TextView tvUp;
    // add ping test
    TextView tvDelay;
    LinearLayout viewChart;
    // 动态数据流图
    DynamicCoverChart dChart;
    PopupWindow pop;
    ImageView imgClear;
    //	ImageView imgLevel;
//	ImageView imgLc;
    //测试textview
    TextView test;
    String netType;
    int mTotal = 0;
    float maxDownSpeed = 0.0f;
    float avgDownSpeed = 0.0f;
    float maxUpSpeed = 0.0f;
    float avgUpSpeed = 0.0f;
    private View v;
    private DoPingTask pTask;
    private float lastDegree = 0.0F;
    private float count = 0.0f;
    private float speed = 0.0f;
    private int usedTime = 0;
    private float upCount = 0.0f;
    private float upStart = 0.0f;
    private JSONObject speedResult;

    private FrameLayout lyNeedle;

    // 再次测试时恢复默认测速界面
    private View stateFlyResultView;
    // 通常的测试界面图形
    private FrameLayout commonLayout;

    private Handler hrNm;
    private boolean isFindGood = false;
    private boolean isThreeG = false;
    private MultiDownLoadUtil download;
    private String dlFileName;
    private String downloadUrl;
    private UploadUtil upUtil;
    private Map<String, String> signalMap;
    private List<Collector> list;
    private TelephonyManager tm;
    private Button moreInfoBtn;
    private PhoneStateListener celllistener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signal) {
            // TODO Auto-generated method stub
            super.onSignalStrengthsChanged(signal);
            try {
                Method[] methods = SignalStrength.class
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
    private OnClickListener lr = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_speed:
                    updateSpeedText(0, 0, 0, 3);
                    getSysInfo();
                    startTest();
                    break;
            }
            Log.d(TAG, "开始测试");
        }
    };
    private Handler hrClear = new Handler();
    private Runnable rbClear = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            RotateAnimation ra = new RotateAnimation(0, 360, 1, 0.5F, 1, 0.5F);
            ra.setDuration(500);
            ra.setFillAfter(true);
            if (imgClear != null) {
                imgClear.setAnimation(ra);
                imgClear.invalidate();
            }
            hrClear.postDelayed(rbClear, 100);
        }
    };
    private Handler pHandler = new Handler();
    private Runnable ping = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                if (pTask.isFinish) {
                    tvDelay.setText(pTask.delay + "ms");
                    downloadTest(dlFileName, downloadUrl);
                    Log.d(TAG, "excute downloadTest");
                } else {
                    pHandler.postDelayed(ping, 100);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    };
    private int updateDownTime = 1000;
    private Handler hrDown = new Handler();
    private Runnable rbDown = new Runnable() {// 下载速率监听

        @Override
        public void run() {
            // TODO Auto-generated method stub
            float mc = (float) download.getCompleteValue();
            Log.d(TAG, "MC : " + mc);
            float total = (float) download.getTotal();
            Log.d(TAG, "TOTAL : " + total);

            if (total <= 0 && usedTime < 50000) {
                usedTime += updateDownTime;
                hrDown.postDelayed(rbDown, updateDownTime);
                return;
            }
            speed = (mc - count) * 1000 / updateDownTime;
            count = mc;
            if (maxDownSpeed < speed) {
                if (isThreeG) {
                    if (speed < 3.0f * 1024) {
                        maxDownSpeed = speed;
                    }
                } else {
                    maxDownSpeed = speed;
                }
            }
            /**
             * 网络异常直接跳过测试
             */
            if (total <= mc || download.isException()) {
                avgDownSpeed = (float) (total / (usedTime + updateDownTime) * 1000);
                Log.d(CoConfig.LOG_TAG, "avgDownSpeed--" + avgDownSpeed);
                updateSpeedText(avgDownSpeed, maxDownSpeed, maxUpSpeed, 1);
                uploadTest();
                usedTime = 0;
                Log.d(TAG, "执行上行测速");
            } else {
                float f = calculateAngle(speed);
                updateNeedle(f, 500);
                updateSpeedText(speed, maxDownSpeed, maxUpSpeed, 1);
                updateState(mc, 1);
                usedTime += updateDownTime;
                hrDown.postDelayed(rbDown, updateDownTime);
                Log.d(TAG, "执行rbDown");
            }
        }
    };

    private int updateUpTime = 1000;
    private Runnable refreshNm = new Runnable() {// 5秒刷新一次联网方式

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String type = AppNetWork.getConnectModel(context);
            if (type.equals(AppNetWork.MODEL_LTE)) {
                isFindGood = true;
            } else {
                isFindGood = false;
            }
            if (type.equals(AppNetWork.MODEL_3G)) {
                isThreeG = true;
                updateDownTime = 1000;
                updateUpTime = 1000;
            } else {
                isThreeG = false;
                updateDownTime = 200;
                updateUpTime = 200;
            }
            int phoneType = tm.getPhoneType();
            String gsm = "";
            if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
                gsm = "GSM/";
            }
            tvNm.setText(gsm + type);
            initCollectInfo();
            singalChange();
            hrNm.postDelayed(refreshNm, 5000);
        }
    };
    private boolean isGet = false;
    private Handler hrUp = new Handler();
    private Runnable rbUp = new Runnable() {// 上传速率监听

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (!isGet && upUtil.length != 0) {
                isGet = true;
                pbState.setMax((int) upUtil.length);
                pbState.setProgress(0);
            }
            /**
             * 网络异常直接跳过测试
             */
            if (upUtil.avgspeed == -1 || upUtil.avgspeed != 0 || download.isException()) {
                avgUpSpeed = upUtil.avgspeed;
                Log.d(CoConfig.LOG_TAG, "avgUpSpeed--" + avgUpSpeed);
                updateSpeedText(avgUpSpeed, maxDownSpeed, maxUpSpeed, 2);
                btnStart.setVisibility(View.VISIBLE);
                moreInfoBtn.setVisibility(View.VISIBLE);
                pbState.setVisibility(View.GONE);
                tvState.setVisibility(View.GONE);
                usedTime = 0;
                isGet = false;
                finishTest();
                delFile();
                Log.d(TAG, "测试结束");

            } else {
                float mc = upUtil.getUidTxBytes();
                float upSum = mc - upStart;
                speed = (mc - upCount) * 1000 / updateUpTime;
                if (maxUpSpeed < speed) {
                    maxUpSpeed = speed;
                }
                upCount = mc;
                float f = calculateAngle(speed);
                updateNeedle(f, 500);
                updateSpeedText(speed, maxDownSpeed, maxUpSpeed, 2);
                updateState(upSum, 2);
                hrUp.postDelayed(rbUp, updateUpTime);
                Log.d(TAG, "上行测试中");
            }
            usedTime++;
        }
    };
    private Runnable delayShowDialog = new Runnable() {
        @Override
        public void run() {
            showFindGood();
        }
    };
    private String testTime;
    private String maxDownloadSpeed;

   /* private Runnable delayScreenHot = new Runnable() {
        @Override
        public void run() {
            screenHot(testTime, maxDownloadSpeed);
        }
    };*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_speed);
        this.initToolbar(getResources().getString(R.string.tab_speed));
        geoGB = MainApp.geoBD;
        findViews();
        btnStart.setOnClickListener(lr);
        moreInfoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpeedActivity.this, SpeedRecordsActivity.class);
                startActivity(intent);
            }
        });
        dChart = new DynamicCoverChart(context);
        viewChart.addView(dChart.execute("实时流量", "", "", ""));
        hrNm = new Handler();
        hrNm.postDelayed(refreshNm, 10);
        this.updateSpeedText(0, 0, 0, 3);

        // 缓存的view需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
       /* ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null) {
            parent.removeView(v);
        }*/
        tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(celllistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        list = new ArrayList<Collector>();
        // geoBD = ((MainApplication) ((Activity) context).getApplication()).geoBD;
        //geo = ((MainApp) ((Activity) context).getApplication()).geo;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }


    private void initCollectInfo() {
        if (list == null) {
            list = new ArrayList<Collector>();
        }
        list.clear();

        // add the location
        BDGeoLocation location = new BDGeoLocation(context);
        String addr = "";
        double lat = 0;
        double lon = 0;
        if (geoGB.locClient != null) {
            geoGB.locClient.requestLocation();
            BDLocation loc = geoGB.location;
            if (loc != null && loc.hasAddr()) {
                addr = loc.getAddrStr();
                lat = loc.getLatitude();
                lon = loc.getLongitude();
            }
            Log.d(TAG, "获取到的位置信息：" + addr + "" + lat + "" + lon);
        }
        list.add(new Collector("坐标位置信息", "", 0, Collector.DATA_TYPE_TITLE));
        list.add(new Collector("Lat", lat + "", 0, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("Lon", lon + "", 0, Collector.DATA_TYPE_UPLOAD));
        list.add(new Collector("Addr", addr, 0, Collector.DATA_TYPE_UPLOAD));
        InfoCell cell = new InfoCell(context);
        list = cell.getCellInfo(list);
        InfoConnectivity ct = new InfoConnectivity(context);
        list = ct.getConectiviyInfo(list);
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

    private void insertRecords(InfoSpeedRecords records) {
        XDbUtils db = new XDbUtils(this.context);
        try {
            db.createTable(InfoSpeedRecords.class);
            db.insert(records);

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String doUploc() {
        String addr = "";
        if (geoGB.locClient != null) {
            geoGB.locClient.requestLocation();
            BDLocation loc = geoGB.location;
            if (loc != null && loc.hasAddr()) {
                addr = loc.getAddrStr();
            }
        }
        return addr;
    }

    private void getSysInfo() {
        Log.d(TAG, "getSysInfo");

        FuncGetSysDatas fgsd = new FuncGetSysDatas(context);
        speedResult = fgsd.getSysJSON();
        String addr = doUploc();
        try {
            addr = URLEncoder.encode(addr, CoConfig.ENCODE_UTF8);
            speedResult.put("addr", addr);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "getSysInfo" + addr);
    }

    private void startTest() {
        // 初始化网速测试界面指针转盘
        Log.d(TAG, "startTest");
        if (commonLayout != null) {
            updateNeedle(0.0f, 500);
            commonLayout.setVisibility(View.VISIBLE);
        }
        if (stateFlyResultView != null) {
            stateFlyResultView.setVisibility(View.GONE);
        }
        if (tvUnit != null) {
            tvUnit.setVisibility(View.VISIBLE);
        }
        if (moreInfoBtn != null) {
            moreInfoBtn.setVisibility(View.VISIBLE);
        }
        if (!AppNetWork.isNetWork(context)) {
            Toast.makeText(context, getString(R.string.common_not_network),
                    Toast.LENGTH_LONG).show();
            return;
        }
        netType = "file_speed_3G";
        String model = AppNetWork.getConnectModel(context);
        if (model.equals(AppNetWork.MODEL_1x)
                || model.equals(AppNetWork.MODEL_3G)) {
            netType = "file_speed_3G";
        } else if (model.equals(AppNetWork.MODEL_WIFI)
                || model.equals(AppNetWork.MODEL_LTE)
                || model.equalsIgnoreCase("LTE")) {
            netType = "file_speed_4G";
        }
        pop = new PopupWindow(context);
        LayoutInflater li = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.pop_clear, null);
        pop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.showAtLocation(((Activity) context).findViewById(R.id.main),
                Gravity.CENTER, 0, 0);
        imgClear = (ImageView) view.findViewById(R.id.img_clear);
        final TextView tvPs = (TextView) view.findViewById(R.id.tv_state);
        final Handler hr = new Handler();
        tvPs.setText("清理手机内存...");
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                hrClear.postDelayed(rbClear, 100);
                try {
                    Thread.sleep(2500);
                    mTotal = UtilMemory.clearMemory(context);
                    hrClear.removeCallbacks(rbClear);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "共清理" + mTotal + "个占用手机内存的进程！",
                                Toast.LENGTH_LONG).show();
                        tvPs.setText("获取测试地址...");
                        GetESFileList ft = new GetESFileList(context);
                        ft.getData(new OnGetDataFinishedListener() {

                            @Override
                            public void onFinished(String output) {
                                // TODO Auto-generated method stub
                                if (output
                                        .equals(AppHttpConnection.RESULT_FAIL)) {
                                    Toast.makeText(
                                            context,
                                            getString(R.string.common_not_network),
                                            Toast.LENGTH_LONG).show();
                                    pop.dismiss();
                                    return;
                                }
                                try {
                                    JSONObject json = new JSONObject(output);
                                    String result = json.getString("result");
                                    if (!result.equals("1")) {
                                        Toast.makeText(context,
                                                json.getString("msg"),
                                                Toast.LENGTH_LONG).show();
                                        pop.dismiss();
                                        return;
                                    }
                                    JSONArray datas = json.getJSONArray("data");
                                    JSONArray data = datas.getJSONArray(0);
                                    dlFileName = data.getString(1) + ".file";
                                    downloadUrl = data.getString(2);
                                    Log.i(CoConfig.LOG_TAG, "DOWNLOAD_URL : " + downloadUrl);
                                    // downloadTest(dlFileName, downloadUrl);

                                    // ping test
                                    pTask = new DoPingTask(null, new JSONObject(), new RatingBar(context));
                                    pTask.execute(CoConfig.PING_DEFAULT_IP, "3");
                                    hr.post(ping);
                                    Log.d(TAG, "开始ping测试");
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                pop.dismiss();
                            }
                        }, netType);
                    }
                });
            }
        }.start();
    }

    private void downloadTest(final String fileName, final String downloadUrl) {
        maxDownSpeed = 0.0f;
        avgDownSpeed = 0.0f;
        maxUpSpeed = 0.0f;
        avgUpSpeed = 0.0f;
        count = 0.0f;
        speed = 0.0f;
        usedTime = 0;
        // updateSpeedText(avgDownSpeed, maxDownSpeed, 1);
        pbState.setVisibility(View.VISIBLE);
        tvState.setVisibility(View.VISIBLE);
        tvState.setText("");
        pbState.setProgress(0);
        btnStart.setVisibility(View.GONE);
        download = new MultiDownLoadUtil(downloadUrl, context.getFilesDir()
                + "/" + fileName, CoConfig.DOWNLOAD_THREAD);
        try {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        download.download();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    pbState.setMax((int) download.getTotal());
                    pbState.setProgress(0);
                    hrDown.postDelayed(rbDown, updateDownTime);
                    Log.d(TAG, "开始下载速率测试");
                }
            }).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void uploadTest() {
        avgUpSpeed = 0.0f;
        count = 0.0f;
        speed = 0.0f;
        usedTime = 0;
        upUtil = new UploadUtil(context);
        upCount = upUtil.getUidTxBytes();
        upStart = upUtil.getUidTxBytes();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //此处报错
                upUtil.uploadFile(netType);
                Log.d(TAG, "uploadFile:netType" + netType);
            }
        }).start();
        hrUp.postDelayed(rbUp, updateUpTime);
        Log.d(TAG, "uploadTest:netType" + netType);
        Log.d(TAG, "oploadTest执行完毕");
    }

    /**
     * 更新指针指向值
     *
     * @param paramFloat
     * @param paramInt
     */
    private void updateNeedle(float paramFloat, int paramInt) {
        if (paramFloat != this.lastDegree) {
            RotateAnimation ra = new RotateAnimation(this.lastDegree,
                    paramFloat, 1, 0.5F, 1, 0.5F);
            ra.setDuration(paramInt);
            ra.setFillAfter(true);
            this.imgNeedle.startAnimation(ra);
            this.imgNeedle.invalidate();
            this.lastDegree = paramFloat;
        }
    }

    /**
     * @param paramFloat 实时网速
     * @return
     */
    public float calculateAngle(float paramFloat) {
        float f = 0;
        // float pi = 3.14159265358979324f;// 圆周率
        /* 一格代表6度 */
        if (paramFloat <= 100.0F)
            f = 0.0F + (paramFloat / (100 / 5)) * 6;
        else if (paramFloat <= 800.0F)
            f = 30.0F + ((paramFloat - 100) / (700 / 5)) * 6;
        else if (paramFloat <= 2048.0F)
            f = 60.0F + ((paramFloat - 800.0F) / (1248 / 5)) * 6;
        else if (paramFloat <= 5120.0F)
            f = 90.0F + ((paramFloat - 2048.0F) / (3072 / 5)) * 6;
        else if (paramFloat <= 20480.0F)
            f = 120.0F + ((paramFloat - 5120.0F) / (15430 / 5)) * 6;
        else if (paramFloat <= 51200.0F)
            f = 150.0F + ((paramFloat - 20480.0F) / (30720 / 5)) * 6;
        else if (paramFloat <= 102400.0F)
            f = 180.0F + ((paramFloat - 51200.0F) / (51200 / 5)) * 6;
        else if (paramFloat <= 153600.0F)
            f = 210.0F + ((paramFloat - 102400.0F) / (51200 / 5)) * 6;
        else
            f = 240.0f;
        return f;
    }

    public void updateSpeedText(float speed, float maxDownSpeed, float maxUpSpeed, int type) {
        String unit = getString(R.string.speed_unit_kbps);
        if (speed / 1024 >= 1) {
            speed = speed / 1024;
            unit = getString(R.string.speed_unit_mbps);
        }
        BigDecimal bd = new BigDecimal(speed);
        speed = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        String text = speed + "\n" + unit;
        tvUnit.setText(text);

        String unit1 = getString(R.string.speed_unit_kbps);
        if (maxDownSpeed / 1024 >= 1) {
            maxDownSpeed = maxDownSpeed / 1024;
            unit1 = getString(R.string.speed_unit_mbps);
        }
        String unit2 = getString(R.string.speed_unit_kbps);
        if (maxUpSpeed / 1000 >= 1) {
            maxUpSpeed = maxUpSpeed / 1024;
            unit2 = getString(R.string.speed_unit_mbps);
        }
        BigDecimal bd1 = new BigDecimal(maxDownSpeed);
        maxDownSpeed = bd1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

        BigDecimal bd2 = new BigDecimal(maxUpSpeed);
        maxUpSpeed = bd2.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

        String text1 = maxDownSpeed + unit1;
        tvDownMaxs.setText(/*getString(R.string.speed_max) +*/ text1);
        String text2 = maxUpSpeed + unit2;
        tvUpMaxs.setText(text2);
        String text3 = speed + unit;
        switch (type) {
            case 1:
                tvDown.setText(/*getString(R.string.speed_down) +*/ text3);
                tvUp.setText(/*getString(R.string.speed_up) +*/ "--");
                break;
            case 2:
                tvUp.setText(/*getString(R.string.speed_up) +*/ text3);
                break;
            case 3:
                tvDelay.setText("--");
                tvUp.setText(/*getString(R.string.speed_up) +*/ "--");
                tvDown.setText(/*getString(R.string.speed_down) +*/ "--");
                tvDownMaxs.setText(/*getString(R.string.speed_max) +*/ "--");
                tvUpMaxs.setText("--");
                break;
        }

        if (maxDownSpeed == 0) {
            dChart.updateChart(speed, 150);
        } else {
            if (unit1.equals(unit))
                dChart.updateChart(speed, maxDownSpeed);
            else
                dChart.updateChart(speed / 1024, maxDownSpeed);
        }
    }

    private void updateState(float size, int type) {
        String tip = "";
        switch (type) {
            case 1:
                tip = getString(R.string.speed_down);
                break;
            case 2:
                tip = getString(R.string.speed_up);
                break;
        }
        if (size >= pbState.getMax()) {
            pbState.setProgress(pbState.getMax());
            tvState.setText(tip + "：100%");
        } else {
            pbState.setProgress((int) size);
            int pec = (int) (size * 10000) / (pbState.getMax() * 100);
            Log.i(TAG, tip + ":" + pec + "%");
            tvState.setText(tip + "：" + Math.abs(pec) + "%");
        }
    }

    public void finishTest() {
        // 无需删除子View
        // lyNeedle.removeAllViews();
        // 把网速测试界面隐藏掉
        commonLayout.setVisibility(View.GONE);
        tvUnit.setVisibility(View.GONE);
        moreInfoBtn.setVisibility(View.VISIBLE);
//		Intent i = new Intent(context, TestResultActivity.class);
//		Bundle be = new Bundle();
//		be.putFloat("maxDownSpeed", maxDownSpeed);
//		be.putFloat("avgDownSpeed", avgDownSpeed);
//		be.putFloat("avgUpSpeed", avgUpSpeed);
//		i.putExtras(be);
//		startActivity(i);
        updateImg();
        JSONObject speed = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.testTime = dateFormat.format(new Date());
        try {
            String unit1 = getString(R.string.speed_unit_kbps);
            if (maxDownSpeed / 1024 >= 1) {
                maxDownSpeed = maxDownSpeed / 1024;
                unit1 = getString(R.string.speed_unit_mbps);
            }

            String unit2 = getString(R.string.speed_unit_kbps);
            if (avgDownSpeed / 1024 >= 1) {
                avgDownSpeed = avgDownSpeed / 1024;
                unit2 = getString(R.string.speed_unit_mbps);
            }

            String unit3 = getString(R.string.speed_unit_kbps);
            if (maxUpSpeed / 1024 >= 1) {
                maxUpSpeed = maxUpSpeed / 1024;
                unit3 = getString(R.string.speed_unit_mbps);
            }

            String unit4 = getString(R.string.speed_unit_kbps);
            if (avgUpSpeed / 1024 >= 1) {
                avgUpSpeed = avgUpSpeed / 1024;
                unit4 = getString(R.string.speed_unit_mbps);
            }

            this.maxDownloadSpeed = maxDownSpeed + unit1;
            speed.put("maxSpeed", maxDownSpeed + unit1);
            speed.put("avgDownSpeed", avgDownSpeed + unit2);
            speed.put("avgUpSpeed", avgUpSpeed + unit4);
            speedResult.put("speed", speed);
            Log.i(CoConfig.LOG_TAG, "-----网速测试结果-----" + speed.toString());
            //MainActivity.mainTestResult.put("speed", speed);
            // insert records

            insertRecords(new InfoSpeedRecords(testTime,
                    downloadUrl,
                    maxDownSpeed + unit1,
                    avgDownSpeed + unit2,
                    maxUpSpeed + unit3,
                    avgUpSpeed + unit4,
                    tvDelay.getText().toString(),
                    tvNm.getText().toString(),
                    list.get(getPosition("Extra")).getDataValue(),
                    list.get(getPosition("sid")).getDataValue(),
                    list.get(getPosition("nid")).getDataValue(),
                    list.get(getPosition("CI")).getDataValue(),
                    list.get(getPosition("CDMA_Signal")).getDataValue() + "dBm",
                    list.get(getPosition("ip")).getDataValue(),
                    pTask.ip,
                    list.get(getPosition("LTE_RSRP")).getDataValue(),
                    list.get(getPosition("LTE_RSRQ")).getDataValue(),
                    list.get(getPosition("LTE_SINR")).getDataValue(),
                    list.get(getPosition("LTE_CI")).getDataValue(),
                    list.get(getPosition("LTE_Pci")).getDataValue(),
                    list.get(getPosition("LTE_Tac")).getDataValue(),
                    list.get(getPosition("Lon")).getDataValue(),
                    list.get(getPosition("Lat")).getDataValue(),
                    list.get(getPosition("Addr")).getDataValue()));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //takeFindGood();

//		postSpeedData();
    }

    /**
     * 是否参加4G找优
     */
    private void takeFindGood() {
        int phoneType = tm.getPhoneType();
        if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
            if (isFindGood) {
                hrNm.postDelayed(delayShowDialog, 2000);
            } else {
                Toast.makeText(this, "对不请,无法证实您当前有4G网络,请切换到4G网络下测速,才能参加4G找优活动！",
                        Toast.LENGTH_SHORT).show();
            }
            // hrNm.postDelayed(delayShowDialog, 2000);
        } else {
            Toast.makeText(this, "对不请,您测速的是移动或联通手机，请用拍照截图的方式参加4G找优活动！",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void showFindGood() {
        String dialogTitle;
        if (maxDownSpeed > 50.0f) {
            dialogTitle = "您的下载速率峰值超过50Mbps,需要参加高下载速率的4G找优活动吗？";
        } else {
            dialogTitle = "您的下载速率峰值未超过50Mbps,需要参加人有我优、人无我有的4G找优活吗？";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogTitle)
                .setCancelable(false)
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(SpeedActivity.this, "正在截取测速结果...",
                                Toast.LENGTH_SHORT).show();
                        //hrNm.postDelayed(delayScreenHot, 500);
                    }
                })
                .setNegativeButton("不", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*private void screenHot(String testTime, String maxDownloadSpeed) {

        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmssSSS");//显示格式
        Date date = new Date();//获取当前时间
        String fileName = f.format(date);//将获取的当前时间转化为String型，即作为现在照片的名称

        fileName = UUID.randomUUID().toString().replace("-", "");
        // 为照片名字加判别
        String fileNamePrefix = "telecom_";

        String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + "/"
                + SubmitFourGFindGoodDataActivity.DEFAULT_TELECOM_PIC_DIR
                + "/";
        //照片保存路径
        ScreenShot.screenHot(getActivity(), filePath, fileNamePrefix + fileName + ".jpg");

        String addr = "";
        String lat = "";
        String lon = "";
        if (list.get(getPosition("Lon")).getDataValue().equals("") ||
                list.get(getPosition("Lat")).getDataValue().equals("") ||
                list.get(getPosition("Addr")).getDataValue().equals("")) {
            // add the location
            BDGeoLocation location = new BDGeoLocation(context);
            if (geoBD.locClient != null && geoBD.locClient.isStarted()) {
                geoBD.locClient.requestLocation();
                BDLocation loc = geoBD.location;
                if (loc != null && loc.hasAddr()) {
                    addr = loc.getAddrStr();
                    lat = loc.getLatitude() + "";
                    lon = loc.getLongitude() + "";
                }
            }

        } else {
            addr = list.get(getPosition("Addr")).getDataValue();
            lon = list.get(getPosition("Lon")).getDataValue();
            lat = list.get(getPosition("Lat")).getDataValue();
        }
        if (CoConfig.isGpsOpen(this.context)){
            lon = list.get(getPosition("Longitude")).getDataValue();
            lat = list.get(getPosition("Latitude")).getDataValue();
        }
        FourGFindGoodInfo item = new FourGFindGoodInfo(testTime, maxDownloadSpeed, lon, lat, addr);
        Intent intent = new Intent(context, SubmitFourGFindGoodDataActivity.class);
        try {
            intent.putExtra("data", XJsonUtils.toJson(item).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }*/

    /**
     *
     */
    private void updateImg() {
        // 如果不是首次登录测试，那么就无需执行下面代码
        if (stateFlyResultView != null) {
            if (stateFlyResultView.getParent().getParent() == lyNeedle) {
                return;
            }
        }
        LayoutInflater littitle = LayoutInflater.from(context);
        View viewItemt = littitle.inflate(R.layout.speed_test_result, null);
        // 实例化状态飞机结果界面
        stateFlyResultView = viewItemt;

        ImageView imgLevel = (ImageView) viewItemt.findViewById(R.id.iv_level);
        ImageView imgLc = (ImageView) viewItemt.findViewById(R.id.iv_level_circle);
//		RotateAnimation ra;
        if (avgDownSpeed <= 500) {
            imgLevel.setImageResource(R.drawable.ic_level_walker);

//			rotateAnimation(30);
            RotateAnimation ra = new RotateAnimation(0, 30, 1, 0.5F, 1,
                    0.5F);
            ra.setDuration(1000);
            ra.setFillAfter(true);
            imgLc.startAnimation(ra);
            imgLc.invalidate();
        } else if (avgDownSpeed <= 1000) {
            imgLevel.setImageResource(R.drawable.ic_level_cycle);
//			rotateAnimation(60);
            RotateAnimation ra = new RotateAnimation(0, 60, 1, 0.5F, 1,
                    0.5F);
            ra.setDuration(1000);
            ra.setFillAfter(true);
            imgLc.startAnimation(ra);
            imgLc.invalidate();
        } else if (avgDownSpeed <= 4000) {
            imgLevel.setImageResource(R.drawable.ic_level_motor);
//			rotateAnimation(90);
            RotateAnimation ra = new RotateAnimation(0, 90, 1, 0.5F, 1,
                    0.5F);
            ra.setDuration(1000);
            ra.setFillAfter(true);
            imgLc.startAnimation(ra);
            imgLc.invalidate();
        } else if (avgDownSpeed <= 9000) {
            imgLevel.setImageResource(R.drawable.ic_level_car);
//			rotateAnimation(120);
            RotateAnimation ra = new RotateAnimation(0, 120, 1, 0.5F, 1,
                    0.5F);
            ra.setDuration(1000);
            ra.setFillAfter(true);
            imgLc.startAnimation(ra);
            imgLc.invalidate();
        } else if (avgDownSpeed <= 40000) {
            imgLevel.setImageResource(R.drawable.ic_level_jet);
//			rotateAnimation(180);
            RotateAnimation ra = new RotateAnimation(0, 180, 1, 0.5F, 1,
                    0.5F);
            ra.setDuration(1000);
            ra.setFillAfter(true);
            imgLc.startAnimation(ra);
            imgLc.invalidate();
        } else {
            imgLevel.setImageResource(R.drawable.ic_level_rocket);
//			rotateAnimation(210);
            RotateAnimation ra = new RotateAnimation(0, 210, 1, 0.5F, 1,
                    0.5F);
            ra.setDuration(1000);
            ra.setFillAfter(true);
            imgLc.startAnimation(ra);
            imgLc.invalidate();
        }

        lyNeedle.addView(viewItemt);
    }

//	private void rotateAnimation(float paramFloat) {
//		RotateAnimation ra = new RotateAnimation(0, paramFloat, 1, 0.5F, 1,
//				0.5F);
//		ra.setDuration(1000);
//		ra.setFillAfter(true);
//		imgLc.startAnimation(ra);
//		imgLc.invalidate();
//	}

   /* private void postSpeedData() {
        PostTestData post = new PostTestData(context);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                if (output.equals(AppHttpClient.RESULT_FAIL)) {

                } else {
                    Toast.makeText(context, "测试完成，下一步请进行ping测试！", Toast.LENGTH_LONG)
                            .show();
                }
            }

        };
        post.postData(lr, speedResult.toString(), "speed");
    }*/

    private void delFile() {
        if (dlFileName != null) {
            context.deleteFile(dlFileName);
        }
    }

    private void findViews() {
        tvNm = (TextView) findViewById(R.id.tv_nm);
        imgNeedle = (ImageView) findViewById(R.id.img_needle);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        btnStart = (Button) findViewById(R.id.btn_speed);
        pbState = (ProgressBar) findViewById(R.id.pb_state);
        tvState = (TextView) findViewById(R.id.tv_state);
        tvDownMaxs = (TextView) findViewById(R.id.tv_max_down);
        tvUpMaxs = (TextView) findViewById(R.id.tv_max_up);
        tvDown = (TextView) findViewById(R.id.tv_down);
        tvUp = (TextView) findViewById(R.id.tv_up);
        viewChart = (LinearLayout) findViewById(R.id.chart_speed);
        lyNeedle = (FrameLayout) findViewById(R.id.layout_needle);

        tvDelay = (TextView) findViewById(R.id.tv_ping);
        // 实例化测试转盘界面
        commonLayout = (FrameLayout) findViewById(R.id.layout_common);

        moreInfoBtn = (Button) findViewById(R.id.btn_more_info);
        initCollectInfo();

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        hrNm.removeCallbacks(refreshNm);
        hrDown.removeCallbacks(rbDown);
        hrClear.removeCallbacks(rbClear);
        pHandler.removeCallbacks(ping);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        tm.listen(celllistener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        tm.listen(celllistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }


}
