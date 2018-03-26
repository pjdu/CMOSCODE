/*
 * Created by AndyHua on 2017/10/27.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2016-07-21 12:50:47.
 */

package cn.com.chinaccs.datasite.main.ui.asset;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.MessageService;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppCheckVersion;
import cn.com.chinaccs.datasite.main.common.AppNetWork;
import cn.com.chinaccs.datasite.main.ui.cmos.AboutActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.ChangePwdActivity;
import cn.com.chinaccs.datasite.main.widget.CheckBoxButton;


/**
 * @author Fddi
 */
public class AssetSettingActivity extends BaseActivity {
    private Context context;
    private TextView tvVersion;
    private Button btnPw;
    private Button btnUpdate;
    private Button btnAl;
    private Button btnA4g;
    private Button btnWifi;
    private Button btnAbout;
    private SharedPreferences share;
    private boolean isTest;
    private boolean isLogin;
    private boolean is4g;
    private boolean isWifi;
    private boolean isTeach;
    private CheckBoxButton cbbMsg;
    private CheckBoxButton cbbmIt;
    private CheckBoxButton cbbmData;
    private CheckBoxButton cbbmQa;
    private Button btnHttpTest;
    private Button btnTeach;

    //检查更新
    private Myhandler hr;
    private AppCheckVersion appCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_setting);
        initToolbar("设置");
        this.context = AssetSettingActivity.this;
        this.findViews();
        tvVersion.setText("版本号：" + App.getAppVersionName(context));
        share = getSharedPreferences(App.SHARE_TAG, 0);
        isTeach = share.getBoolean(AppNetWork.SHARE_IF_AUTOTEACH, true);
        isTest = share.getBoolean(AppNetWork.SHARE_IF_AUTOTEST, true);
        isLogin = share.getBoolean(AppCheckLogin.SHARE_IF_AUTOLOGIN, true);
        is4g = share.getBoolean(AppNetWork.SHARE_IF_AUTO4G, true);
        isWifi = share.getBoolean(AppNetWork.SHARE_IF_AUTOWIFI, true);
        this.initButtonState();
        btnPw.setOnClickListener(lr);
        btnUpdate.setOnClickListener(lr);
        btnAl.setOnClickListener(lr);
        btnA4g.setOnClickListener(lr);
        btnWifi.setOnClickListener(lr);
        btnAbout.setOnClickListener(lr);
        btnHttpTest.setOnClickListener(lr);
        btnTeach.setOnClickListener(lr);
        initCbbsMsg();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void initCbbsMsg() {
        boolean isMsg = share.getBoolean(DataSiteStart.SHARE_IS_MSG, false);
        cbbMsg.setChecked(isMsg);
        cbbMsg.setOnChangedListener(clr);

        cbbmIt.setCheckEnabled(isMsg);
        boolean ismIt = share.getBoolean(DataSiteStart.SHARE_IS_MSG_IT, false);
        cbbmIt.setChecked(ismIt);
        cbbmIt.setOnChangedListener(clr);

        cbbmData.setCheckEnabled(isMsg);
        boolean ismData = share.getBoolean(DataSiteStart.SHARE_IS_MSG, false);
        cbbmData.setChecked(ismData);
        cbbmData.setOnChangedListener(clr);

        cbbmQa.setCheckEnabled(isMsg);
        boolean ismQa = share.getBoolean(DataSiteStart.SHARE_IS_MSG, false);
        cbbmQa.setChecked(ismQa);
        cbbmQa.setOnChangedListener(clr);
    }

    private CheckBoxButton.OnChangedListener clr = new CheckBoxButton.OnChangedListener() {

        @Override
        public void onChanged(View v, Boolean checked) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.cbl_msg:
                    cbbmIt.setCheckEnabled(checked);
                    cbbmData.setCheckEnabled(checked);
                    cbbmQa.setCheckEnabled(checked);
                    share.edit().putBoolean(DataSiteStart.SHARE_IS_MSG, checked)
                            .commit();
                    Intent i = new Intent(AssetSettingActivity.this,
                            MessageService.class);
                    if (checked) {
                        startService(i);
                    } else {
                        stopService(i);
                    }
                    break;
                case R.id.cbl_msg_inspect:
                    share.edit().putBoolean(DataSiteStart.SHARE_IS_MSG_IT, checked)
                            .commit();
                    break;
                case R.id.cbl_msg_data:
                    share.edit()
                            .putBoolean(DataSiteStart.SHARE_IS_MSG_DATA, checked)
                            .commit();
                    break;
                case R.id.cbl_msg_qa:
                    share.edit().putBoolean(DataSiteStart.SHARE_IS_MSG_QA, checked)
                            .commit();
                    break;
                default:
                    break;
            }
        }
    };

    private OnClickListener lr = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_config_teach:
                    isTeach = !isTeach;
                    share.edit().putBoolean(AppNetWork.SHARE_IF_AUTOTEACH, (isTeach)).commit();
                    initButtonState();
                    break;
                case R.id.btn_config_httptest:
                    isTest = !isTest;
                    share.edit()
                            .putBoolean(AppNetWork.SHARE_IF_AUTOTEST, (isTest))
                            .commit();
                    initButtonState();
                    break;
                case R.id.btn_config_changepw:
                    Intent toChangePW = new Intent(context, ChangePwdActivity.class);
                    startActivity(toChangePW);
                    break;
                case R.id.btn_config_update:
                    appCv = new AppCheckVersion();
                    hr = new Myhandler(context, appCv);
                    versionCheck();
                    break;
                case R.id.btn_config_autologin:
                    isLogin = !isLogin;
                    share.edit()
                            .putBoolean(AppCheckLogin.SHARE_IF_AUTOLOGIN, (isLogin))
                            .commit();
                    initButtonState();
                    break;
                case R.id.btn_config_4g:
                    is4g = !is4g;
                    share.edit().putBoolean(AppNetWork.SHARE_IF_AUTO4G, (is4g))
                            .commit();
                    initButtonState();
                    break;
                case R.id.btn_config_wifi:
                    isWifi = !isWifi;
                    share.edit().putBoolean(AppNetWork.SHARE_IF_AUTOWIFI, (isWifi))
                            .commit();
                    initButtonState();
                    break;
                case R.id.btn_config_about:
                    Intent toAbout = new Intent(context, AboutActivity.class);
                    startActivity(toAbout);
                    break;
                default:
                    break;
            }
        }
    };

    private void initButtonState() {
        if (isTeach) {
            btnTeach.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_on), null);
        } else {
            btnTeach.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_off),
                    null);
        }
        if (isTest) {
            btnHttpTest.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_on), null);
        } else {
            btnHttpTest.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_off),
                    null);
        }
        if (isLogin) {
            btnAl.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_on), null);
        } else {
            btnAl.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_off),
                    null);
        }
        if (is4g) {
            btnA4g.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_on), null);
        } else {
            btnA4g.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_off),
                    null);
        }
        if (isWifi) {
            btnWifi.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_on), null);
        } else {
            btnWifi.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_slide_cb_off),
                    null);
        }
    }

    private void findViews() {
        btnTeach = (Button) findViewById(R.id.btn_config_teach);
        btnHttpTest = (Button) findViewById(R.id.btn_config_httptest);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        btnPw = (Button) findViewById(R.id.btn_config_changepw);
        btnUpdate = (Button) findViewById(R.id.btn_config_update);
        btnAl = (Button) findViewById(R.id.btn_config_autologin);
        btnA4g = (Button) findViewById(R.id.btn_config_4g);
        btnWifi = (Button) findViewById(R.id.btn_config_wifi);
        cbbMsg = (CheckBoxButton) findViewById(R.id.cbl_msg);
        cbbmIt = (CheckBoxButton) findViewById(R.id.cbl_msg_inspect);
        cbbmData = (CheckBoxButton) findViewById(R.id.cbl_msg_data);
        cbbmQa = (CheckBoxButton) findViewById(R.id.cbl_msg_qa);
        btnAbout = (Button) findViewById(R.id.btn_config_about);
    }

    //检查更新

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
            //final Intent intent = new Intent(context, LoginActivity.class);
            Timer timer = new Timer(); // 建立一个timer，控制3秒后跳转activity；
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    //context.startActivity(intent);
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
                                            //sleepTimes(100);
                                        }
                                    }).show();
                        } else {
                            Dialog alertDialog = new AlertDialog.Builder(context).
                                    setTitle("检查更新").setMessage("已是最新版本").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //sleepTimes(100);
                                }
                            }).create();
                            alertDialog.show();


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
