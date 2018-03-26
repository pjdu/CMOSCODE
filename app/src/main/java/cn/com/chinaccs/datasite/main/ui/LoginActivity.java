package cn.com.chinaccs.datasite.main.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppNetWork;

public class LoginActivity extends BaseActivity {
    private Context context;
    private AppCheckLogin appCl;
    private EditText edUserId;
    private EditText edPwd;
    private CheckBox cbAuto;
    private Button btnSubmit;
    private Button btnExit;
    private SharedPreferences share;
    private String userId;
    private String pwd;
    private Boolean isAuto;
    private Boolean is4g;
    private Boolean isWifi;

    // for 6.0 检测系统权限安全性
    /**
     * Id to identity READ_PHONE_STATE permission request
     */
    private static final int REQUEST_READ_PHONE_STATE = 0;
    /**
     * Id to identity ACCESS_LOCATION permission request
     */
    private static final int REQUEST_ACCESS_LOCATION = 1;
    /**
     * Id to identity WRITE_EXTERNAL_STORAGE permission request
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    /**
     * Permission required to read phone state, Used by the {@link LoginActivity}
     */
    private static final String[] PERMISSION_PHONE_STATE = {
            Manifest.permission.READ_PHONE_STATE
    };
    /**
     * Permissions required to access and fine location, Used by the {@link LoginActivity}
     */
    private static final String[] PERMISSION_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    /**
     * Permission required to read and write storage, Used by the {@link MainActivity}
     */
    private static final String[] PERMISSION_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        appCl = new AppCheckLogin();
        setContentView(R.layout.activity_login);
        this.findViews();
        this.autoLogin();
        btnSubmit.setOnClickListener(ol);
        btnExit.setOnClickListener(ol);
        share = getSharedPreferences(App.SHARE_TAG, 0);
        userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        pwd = share.getString(AppCheckLogin.SHARE_USER_PWD, "");
        isAuto = share.getBoolean(AppCheckLogin.SHARE_IF_AUTOLOGIN, true);
        is4g = share.getBoolean(AppNetWork.SHARE_IF_AUTO4G, true);
        isWifi = share.getBoolean(AppNetWork.SHARE_IF_AUTOWIFI, true);
        edUserId.setText(userId);
        edPwd.setText(pwd);
        cbAuto.setChecked(isAuto);
        if (is4g) {
            AppNetWork.setMobileNetChange(context, true);
        }
        if (isWifi) {
            AppNetWork.openWifi(context);
        }
        // for 6.0 检测系统权限安全性
        /*if (!requestPermissions()) {
            Snackbar.make(edUserId, R.string.permission_error, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok,
                    new View.OnClickListener() {

                        *//**
                         * Called when a view has been clicked.
                         *
                         * @param v The view that was clicked.
                         *//*
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
        }*/
    }

    /**
     * 检测系统访问权限 for 6.0
     */
    private boolean requestPermissions() {
        if (!mayRequestPhoneState()
                && !mayRequestLocation()
                && !mayRequestExternalStorage()) {
            return false;
        }
        return true;
    }

    /**
     * 获取定位权限
     *
     * @return
     */
    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;

        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(edUserId, R.string.permission_success, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok,
                    new View.OnClickListener() {

                        /**
                         * Called when a view has been clicked.
                         *
                         * @param v The view that was clicked.
                         */
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(PERMISSION_LOCATION, REQUEST_ACCESS_LOCATION);
                        }
                    });
        } else {
            requestPermissions(PERMISSION_LOCATION, REQUEST_ACCESS_LOCATION);
        }
        return false;
    }

    /**
     * Callback received when a permission request has been completed
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // request permission
                requestPermissions();
            }
        } else if (requestCode == REQUEST_ACCESS_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // request permission
                requestPermissions();
            }
        } else if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // request permission
                requestPermissions();
            }
        }
    }

    /**
     * @return
     */
    private boolean mayRequestPhoneState() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
            Snackbar.make(edUserId, R.string.permission_success, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok,
                    new View.OnClickListener() {

                        /**
                         * Called when a view has been clicked.
                         *
                         * @param v The view that was clicked.
                         */
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(PERMISSION_PHONE_STATE, REQUEST_READ_PHONE_STATE);
                        }
                    });
        } else {
            requestPermissions(PERMISSION_PHONE_STATE, REQUEST_READ_PHONE_STATE);
        }
        return false;
    }

    /**
     * @return
     */
    private boolean mayRequestExternalStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(edUserId, R.string.permission_success, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok,
                    new View.OnClickListener() {

                        /**
                         * Called when a view has been clicked.
                         *
                         * @param v The view that was clicked.
                         */
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(PERMISSION_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
                        }
                    });
        } else {
            requestPermissions(PERMISSION_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        return false;
    }

    private OnClickListener ol = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_login_exit:
                    finish();
                    break;
                case R.id.btn_login_submit:
                    // startActivity(new Intent(context, MainActivity.class));
                    funcSubmit();
                    break;
                default:
                    break;
            }
        }
    };

    private void funcSubmit() {
        userId = edUserId.getText().toString();
        pwd = edPwd.getText().toString();
        if (userId.equals("") || pwd.equals("")) {
            Toast.makeText(context,
                    getResources().getString(R.string.common_vad_empty),
                    Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_response));
        pd.show();
        final Handler hr = new Handler();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String imsi = App.getIMSI(context);
                String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
                        + pwd + imsi);
                String clearUserId = "";
                String clearPwd = "";
                try {
                    clearUserId = AESCryto.encrypt(DataSiteStart.AES_KEY,
                            userId);
                    clearPwd = AESCryto.encrypt(DataSiteStart.AES_KEY, pwd);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                StringBuffer url = new StringBuffer(
                        DataSiteStart.HTTP_SERVER_URL).append("LoginCheck.do")
                        .append("?userid=").append(clearUserId).append("&pwd=")
                        .append(clearPwd).append("&imsi=").append(imsi)
                        .append("&sign=").append(sign);
                Log.i(App.LOG_TAG, url.toString());
                final JSONObject json = appCl.loginCheck(context,
                        url.toString());
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            pd.cancel();
                            Log.i(App.LOG_TAG, json.toString());
                            if (json.getBoolean(App.JSON_MAP_RESULT)) {
                                share.edit()
                                        .putString(AppCheckLogin.SHARE_USER_ID,
                                                userId).commit();
                                share.edit()
                                        .putString(
                                                AppCheckLogin.SHARE_USER_PWD,
                                                pwd).commit();
                                share.edit()
                                        .putBoolean(
                                                AppCheckLogin.SHARE_IF_AUTOLOGIN,
                                                cbAuto.isChecked()).commit();
                                share.edit()
                                        .putString(
                                                AppCheckLogin.SHARE_USER_NAME,
                                                json.getString(App.JSON_MAP_USERNAME))
                                        .commit();
                                share.edit()
                                        .putString(
                                                AppCheckLogin.SHARE_ORG_CODE,
                                                json.getString(App.JSON_MAP_ORGCODE))
                                        .commit();
                                share.edit()
                                        .putString(
                                                AppCheckLogin.SHARE_USER_TYPE,
                                                json.getString(App.JSON_MAP_USERTYPE))
                                        .commit();
                                String name = json
                                        .getString(App.JSON_MAP_USERNAME);
                                Toast.makeText(context, "登录成功，欢迎您：" + name,
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context,
                                        MainActivity.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            } else {
                                String info = json.getString(App.JSON_MAP_MSG);
                                Toast.makeText(context, info, Toast.LENGTH_LONG)
                                        .show();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void findViews() {
        edUserId = (EditText) findViewById(R.id.edit_login_user);
        edPwd = (EditText) findViewById(R.id.edit_login_pwd);
        cbAuto = (CheckBox) findViewById(R.id.cb_login_auto);
        btnSubmit = (Button) findViewById(R.id.btn_login_submit);
        btnExit = (Button) findViewById(R.id.btn_login_exit);
    }

    private void autoLogin() {
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        String pwd = share.getString(AppCheckLogin.SHARE_USER_PWD, "");
        if (share.getBoolean(AppCheckLogin.SHARE_IF_AUTOLOGIN, false)
                && !userId.equals("") && !pwd.equals("")) {
            String imsi = App.getIMSI(context);
            String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
                    + pwd + imsi);
            String clearUserId = "";
            String clearPwd = "";
            try {
                clearUserId = AESCryto.encrypt(DataSiteStart.AES_KEY, userId);
                clearPwd = AESCryto.encrypt(DataSiteStart.AES_KEY, pwd);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
                    .append("LoginCheck.do").append("?userid=")
                    .append(clearUserId).append("&pwd=").append(clearPwd)
                    .append("&imsi=").append(imsi).append("&sign=")
                    .append(sign);
            Intent intent = new Intent(context, MainActivity.class);
            appCl.autoLogin(context, url.toString(), intent);
        }
    }

}
