package cn.com.chinaccs.datasite.main.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tencent.stat.StatConfig;
import com.tencent.stat.StatReportStrategy;
import com.tencent.stat.StatService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckVersion;

public class WelcomeActivity extends BaseActivity {
    private Context context;
    private Myhandler hr;
    private AppCheckVersion appCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 清除临时数据
        this.context = this;
        appCv = new AppCheckVersion();
        hr = new Myhandler(context, appCv);
        setContentView(R.layout.activity_welcome);
        // 禁止MTA打印日志
        StatConfig.setDebugEnable(false);
        // 根据情况，决定是否开启MTA对app未处理异常的捕获
        StatConfig.setAutoExceptionCaught(true);
        // 选择默认的上报策略
        StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);
        StatService.trackCustomEvent(context, "onCreate", "");
        this.versionCheck();
    }

    private void versionCheck() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                int v = App.getAppVersionCode(context);
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + v);
                final StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL)
                        .append("VersionCheck.do").append("?version=")
                        .append(v).append("&sign=").append(sign);
                JSONObject json = appCv.getAppVersionCheck(context,
                        url.toString());
                Message m = hr.obtainMessage(100, json);
                hr.sendMessage(m);
            }
        }).start();
    }

    static class Myhandler extends Handler {
        private Context context;
        private AppCheckVersion appCv;

        public Myhandler(Context context, AppCheckVersion appCv) {
            this.context = context;
            this.appCv = appCv;
        }

        private void sleepTimes(int times) {
            final Intent intent = new Intent(context, LoginActivity.class);
            Timer timer = new Timer(); // 建立一个timer，控制3秒后跳转activity；
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            };
            timer.schedule(task, times);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 100:
                    final JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json.getBoolean(App.JSON_MAP_RESULT)) {
                            final String dUrl = json
                                    .getString(App.JSON_MAP_DOWNLOADURL);
                            App.alertDialog(context, "有新版本",
                                    json.getString(App.JSON_MAP_MSG),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated
                                            // method stub
                                            appCv.downLoadApk(context, dUrl,
                                                    DataSiteStart.APP_NAME);
                                            dialog.cancel();
                                        }
                                    }, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                            sleepTimes(2000);
                                        }
                                    }).show();
                        } else {
                            sleepTimes(2000);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
