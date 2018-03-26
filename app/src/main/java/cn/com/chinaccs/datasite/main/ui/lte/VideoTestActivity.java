package cn.com.chinaccs.datasite.main.ui.lte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.common.AppNetWork;
import cn.com.chinaccs.datasite.main.connect.GetESFileList;
import cn.com.chinaccs.datasite.main.connect.PostTestData;
import cn.com.chinaccs.datasite.main.data.FuncGetSysDatas;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.ui.functions.NetworkTest;
import cn.com.chinaccs.datasite.main.widget.DynamicLineChart;

/**
 * 网络信令
 *
 * @author fddi
 */
@SuppressLint("ShowToast")
public class VideoTestActivity extends BaseActivity {
    Context context;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private TextView tvState;
    private TextView tvMaxs;
    private ImageButton btnStart;
    private LinearLayout layoutChart;
    private Button btnAy;
    private static final String TAG = "VideoTestActivity";
    private String URLstring = "";
    private DynamicLineChart speedChart;
    private float speed = 0;
    private float totalFlow = 0;
    private float maxSpeed = 0;
    private float avgSpeed = 0;
    private float totalSpeed, speedCount = 0;
    private Date startTime;
    private int position = 0;
    private Handler handler = new Handler();
    private boolean pause = false;
    ;
    private WakeLock mWakeLock;

    public static BDGeoLocation geoGB;
    public static BDGeoLocation geo;
    private JSONObject videoResult;
    private String netType;
    private HashMap<String, Object> map;
    private JSONObject video;
    private NetworkTest networkTest;
    private JSONObject LTE;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        // 显示标题
        /*getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        buildList();
        this.acquireWakeLock();

        //String name = (String) map.get("ItemText");

        //if (name.indexOf("4G") == -1) {
        //	setContentView(R.layout.activity_videotest_narrow);
        //} else {
        setContentView(R.layout.activity_videotest);
        this.initToolbar(getResources().getString(R.string.busi_video));
        //}

        findViews();
        networkTest = new NetworkTest(context);
        //getSupportActionBar().setTitle(name);

        geoGB = MainApp.geoBD;
        //geo = ((MainApplication) ((Activity) context).getApplication()).geo;

        speedChart = new DynamicLineChart(context);
        layoutChart.addView(speedChart.execute("流量动态图", "", "", ""));
        updateSpeedText(0, 0);
        mediaPlayer = new MediaPlayer();

        buildListener();

        // 把输送给surfaceView的视频画面，直接显示到屏幕上,不要维持它自身的缓冲区
        surfaceView.getHolder()
                .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(
                getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight());
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        btnStart = (ImageButton) findViewById(R.id.btn_testing);
        surfaceView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnStart.setVisibility(View.VISIBLE);
                    btnStart.setBackgroundColor(getResources().getColor(
                            R.color.black_op1));
                    pause = true;
                    handler.removeCallbacks(flowLisntener);
                    position = mediaPlayer.getCurrentPosition();
                }
            }
        });
        btnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

//				if (mediaPlayer != null && position > 0) {
//					play(position);
//				}
//				if (mediaPlayer != null && position == 0) {
//					play(0);
//					tvState.setText("玩命加载中...");
//				}
                if (URLstring != null) {
                    if (pause && mediaPlayer != null) {
                        mediaPlayer.start();
                        pause = false;
                        handler.postDelayed(flowLisntener, 1000);
                    } else {
                        play(position);
                        tvState.setText("加载中...");
                    }
                } else {
                    Toast.makeText(VideoTestActivity.this, "视频地址加载中", Toast.LENGTH_LONG).show();
                }
                btnStart.setVisibility(View.GONE);
            }
        });
        btnAy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (speedCount > 0 && totalSpeed > 0) {
                    // TODO Auto-generated method stub
                    Intent i = new Intent(context, TestResultActivity.class);
                    Bundle be = new Bundle();
                    avgSpeed = totalSpeed / speedCount;
                    getSysInfo();
                    video = new JSONObject();

                    try {
                        //video存储视频测试所需上传的数据
                        video.put("VideoPeakSpeed", maxSpeed);
                        video.put("VideoAvgSpeed", avgSpeed);
                        video.put("VideoName", map.get("ItemText"));
                        video.put("VideoURL", map.get("url"));
                        video.put("VideoIP", CoConfig.HTTP_SERVER_URL);
                        video.put("TCLASS", "");
                        video.put("BufferCounter", "");
                        video.put("VideoSize", "");
                        video.put("VideoTotleTraffic", totalFlow);

                        videoResult.put("video", video);

                        Log.d(CoConfig.LOG_TAG + "--视频测试数据--", video.toString());
                        //MainActivity.mainTestResult.put("video", video);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    postSpeedData();
                    Log.d(TAG, "postSpeedData()");

                    be.putFloat("maxDownSpeed", maxSpeed);
                    be.putFloat("avgDownSpeed", avgSpeed);
                    be.putFloat("avgUpSpeed", 0);

                    i.putExtras(be);
                    startActivity(i);
                } else {
                    Toast.makeText(context, "请先开始播放视频！", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void postSpeedData() {
        LTE = networkTest.getLTE();
        Log.i(TAG,"LTE"+LTE.toString());
        PostTestData post = new PostTestData(context);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                networkTest.stopListener();
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

				/*if (output.equals(AppHttpClient.RESULT_FAIL)) {

				} else {
					Toast.makeText(context, "测试完成，诚邀您进行一个简单的小调查，" +
							"以便我们对网络质量进行改善，为您提供更好的网络体验！", Toast.LENGTH_LONG)
					.show();
				}*/
            }

        };
        post.postData(lr, video, LTE);
        Log.d(TAG, "postData()");
    }

    private String doUploc() {
        String addr = "";
        if (geoGB.locClient != null && geoGB.locClient.isStarted()) {
            geoGB.locClient.requestLocation();
            BDLocation loc = geoGB.location;
            if (loc != null && loc.hasAddr()) {
                addr = loc.getAddrStr();
            }
        }
        return addr;
    }

    private void getSysInfo() {
        networkTest.setLTE();
        FuncGetSysDatas fgsd = new FuncGetSysDatas(context);
        videoResult = fgsd.getSysJSON();
        String addr = doUploc();
        try {
            addr = URLEncoder.encode(addr, CoConfig.ENCODE_UTF8);
            videoResult.put("addr", addr);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mediaPlayer.release();
        mediaPlayer = null;
        handler.removeCallbacks(preparePause);
        handler.removeCallbacks(flowLisntener);
        this.releaseWakeLock();
        super.onDestroy();
        networkTest.stopListener();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        handler.removeCallbacks(preparePause);
        handler.removeCallbacks(flowLisntener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        totalFlow = getUidRxBytes();
        super.onResume();
    }

    private Runnable preparePause = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            totalFlow = getUidRxBytes();
            handler.post(flowLisntener);
        }
    };

    private Runnable flowLisntener = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (totalFlow != 0) {
                speed = getUidRxBytes() - totalFlow;
                totalFlow = getUidRxBytes();
                totalSpeed += speed;
                speedCount++;
                if (speed > maxSpeed) {
                    maxSpeed = speed;
                }
                if (speed <= 100 && mediaPlayer != null
                        && mediaPlayer.getCurrentPosition() > 0) {
                    tvState.setText("加载完成");
                    if (totalSpeed > 14000) {

                        avgSpeed = totalSpeed / speedCount;
                        getSysInfo();
                        JSONObject video = new JSONObject();

                        try {
                            video.put("maxDownSpeed", maxSpeed);
                            video.put("avgDownSpeed", avgSpeed);
                            videoResult.put("video", video);
                            //MainActivity.mainTestResult.put("video", video);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        tvState.setText("加载完成!可以点击【测试分析】查看测试结果，然后进行下一步测试");
                    }
                } else {
                    updateSpeedText(speed, maxSpeed);
                }
                handler.postDelayed(flowLisntener, 1000);
            }
        }
    };

    public void updateSpeedText(float speed, float maxSpeed) {
        String unit = getString(R.string.speed_unit_kbps);
        if (speed / 1024 >= 1) {
            speed = speed / 1024;
            unit = getString(R.string.speed_unit_mbps);
        }
        BigDecimal bd = new BigDecimal(speed);
        speed = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        String text = "视频加载中..." + speed + unit;
        tvState.setText(text);

        String unit1 = getString(R.string.speed_unit_kbps);
        if (maxSpeed / 1024 >= 1) {
            maxSpeed = maxSpeed / 1024;
            unit1 = getString(R.string.speed_unit_mbps);
        }
        BigDecimal bd1 = new BigDecimal(maxSpeed);
        maxSpeed = bd1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        String text1 = maxSpeed + unit1;
        tvMaxs.setText(getString(R.string.speed_max) + text1);

        if (maxSpeed == 0) {
            speedChart.updateChart(speed, 150);
        } else {
            if (unit1.equals(unit))
                speedChart.updateChart(speed, maxSpeed);
            else
                speedChart.updateChart(speed / 1024, maxSpeed);
        }
    }

    public float getUidRxBytes() { // 获取总的接受字节数，包含Mobile和WiFi等
        PackageManager pm = getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0
                : (TrafficStats.getTotalRxBytes() * 8 / 1024);
    }

    private void buildListener() {
        mediaPlayer.setOnInfoListener(new OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (totalFlow == 0)
                            totalFlow = getUidRxBytes();
                        handler.postDelayed(flowLisntener, 1000);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        handler.removeCallbacks(flowLisntener);
//					 tvState.setText("加载完成");
                        break;
                }
                return false;
            }

        });
        mediaPlayer
                .setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        // TODO Auto-generated method stub
//						 tvState.setText("buffer..."+percent);
                    }
                });
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub

            }
        });
    }

    private final class SurfaceCallback implements Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            if (position > 0 && URLstring != null) {
                play(position);
                position = 0;
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                position = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }
    }

    private void play(int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(URLstring);
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.prepareAsync();// 缓冲
            mediaPlayer.start();
            mediaPlayer.setOnPreparedListener(new PrepareListener(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final class PrepareListener implements OnPreparedListener {
        private int position;

        public PrepareListener(int position) {
            this.position = position;
        }

        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();// 播放视频
            if (position > 0)
                mediaPlayer.seekTo(position);
            if (totalFlow == 0)
                totalFlow = getUidRxBytes();
            if (startTime == null)
                startTime = new Date();
            handler.postDelayed(preparePause, 2000);
        }
    }

    @SuppressWarnings("deprecation")
    private void acquireWakeLock() {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm
                    .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "");
            if (null != mWakeLock) {
                mWakeLock.acquire();
            }
        }
    }

    private void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void findViews() {
        // TODO Auto-generated method stub
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        tvState = (TextView) findViewById(R.id.txt_state);
        tvMaxs = (TextView) findViewById(R.id.tv_max);
        layoutChart = (LinearLayout) findViewById(R.id.chart_speed);
        btnAy = (Button) findViewById(R.id.btn_analysis);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildList() {
        // TODO Auto-generated method stub
        Log.d(TAG, "buildList");
        netType = "file_video_3G";
        String model = AppNetWork.getConnectModel(context);
        if (model.equals(AppNetWork.MODEL_1x)
                || model.equals(AppNetWork.MODEL_3G)) {
            netType = "file_video_3G";
        } else if (model.equals(AppNetWork.MODEL_WIFI)
                || model.equals(AppNetWork.MODEL_LTE)
                || model.equalsIgnoreCase("LTE")) {
            netType = "file_video";
        }

        //pbTop.setVisibility(View.VISIBLE);
        //tvState.setVisibility(View.INVISIBLE);
        //listVideo = new ArrayList<HashMap<String, Object>>();
        //listVideo.clear();
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                if (output.equals(AppHttpConnection.RESULT_FAIL)) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    //tvState.setText(getResources().getString(
                    //		R.string.app_loadfail));
                    //pbTop.setVisibility(View.GONE);
                    //tvState.setVisibility(View.VISIBLE);
                } else {
                    try {
                        JSONObject json = new JSONObject(output);
                        String result = json.getString("result");
                        String msg = json.getString("msg");
                        if (result.equals("1")) {
                            JSONArray array = json.getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {
                                JSONArray data = array.getJSONArray(i);
                                String title = data.getString(1);
                                String url = data.getString(2);
                                map = new HashMap<String, Object>();
                                //map.put("ItemImage", R.drawable.ic_tysx);
                                map.put("ItemText", title);
                                map.put("url", url);
                                //listVideo.add(map);
                                Log.d(CoConfig.LOG_TAG, data.toString());
                                Log.d(TAG, "获取url地址");
                            }
                            URLstring = (String) map.get("url");
                            /*SimpleAdapter saMenuItem = new SimpleAdapter(
                                    context, listVideo, // 数据源
									R.layout.item_grid, // xml实现
									new String[] { "ItemImage", "ItemText" }, // 对应map的Key
									new int[] { R.id.image_griditem,
											R.id.text_griditem }); // 对应R的Id

							// 添加Item到网格中
							gvVideo.setAdapter(saMenuItem);
							gvVideo.setOnItemClickListener(gridClt);
							tvState.setVisibility(View.GONE);
							pbTop.setVisibility(View.GONE);*/
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG)
                                    .show();
							/*pbTop.setVisibility(View.GONE);
							tvState.setVisibility(View.VISIBLE);
							tvState.setText(getResources().getString(
									R.string.app_loadfail));*/
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }


        };
        GetESFileList func = new GetESFileList(context);
        func.getData(lr, netType);
        Log.d(TAG, "执行lr");
    }

}
