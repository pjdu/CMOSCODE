/*
 * Created by AndyHua on 2017/11/3.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-28 17:55:54.
 */

package cn.com.chinaccs.datasite.main.ui.asset;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.blankj.ALog;
import com.ytf.asky.library.zbar.BarcodeFormat;
import com.ytf.asky.library.zbar.Result;
import com.ytf.asky.library.zbar.ZBarScannerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.AESCryto;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.MainApp;
import cn.com.chinaccs.datasite.main.ui.asset.function.FuncGetCardLoc;
import cn.com.chinaccs.datasite.main.ui.utils.MapUtils;
import cn.com.chinaccs.datasite.main.zbar.BaseScannerActivity;
import cn.com.chinaccs.datasite.main.zbar.CameraSelectorDialog;
import cn.com.chinaccs.datasite.main.zbar.FormatSelectorDialog;
import cn.com.chinaccs.datasite.main.zbar.MessageDialogFragment;


public class AssetScannerActivity extends BaseScannerActivity implements MessageDialogFragment.MessageDialogListener,
        ZBarScannerView.ResultHandler, FormatSelectorDialog.FormatSelectorDialogListener,
        CameraSelectorDialog.CameraSelectorDialogListener {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZBarScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private TextView scanFormat;
    private TextView scanResult;
    private TextView scanFocus;
    private TextView scanFlash;
    private TextView scanClose;

    private ProgressDialog proDialog;
    private Context context;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        context = this;

        setContentView(R.layout.activity_scanner);
        initView();
        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        setupFormats();
        contentFrame.addView(mScannerView);
    }

    private void initView() {
        scanResult = (TextView) findViewById(R.id.scan_result);
        scanFormat = (TextView) findViewById(R.id.scan_format);

        scanFocus = (TextView) findViewById(R.id.scan_focus);
        scanFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    scanFocus.setText(R.string.auto_focus_on);
                } else {
                    scanFocus.setText(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
            }
        });

        scanClose = (TextView) findViewById(R.id.scan_close);
        scanClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        scanFlash = (TextView) findViewById(R.id.scan_flash);
        scanFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlash = !mFlash;
                if (mFlash) {
                    scanFlash.setText(R.string.flash_on);
                } else {
                    scanFlash.setText(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuItem menuItem;

        if (mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);


        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if (mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialog.newInstance(this, mSelectedIndices);
                fragment.show(getSupportFragmentManager(), "format_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanResult.setText(rawResult.getContents());
        scanFormat.setText(rawResult.getBarcodeFormat().getName());
        // showMessageDialog("内容 = " + rawResult.getContents() + ", 编码 = " + rawResult.getBarcodeFormat().getName());
        //Toast.makeText(this, "内容 = " + rawResult.getContents() +
        //        ", 编码 = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        String result = rawResult.getContents();
        if (result.contains("蜊惹ｸｺ")) {
            result = result.replace("蜊惹ｸｺ", "华为");
        } else if (result.contains("荳ｭ蜈ｴ")) {
            result = result.replace("荳ｭ蜈ｴ", "中兴");
        } else if (result.contains("辷ｱ遶倶ｿ｡")) {
            result = result.replace("辷ｱ遶倶ｿ｡", "爱立信");
        }
        ALog.d(result);
        int rIndex = result.indexOf("RNO:");
        int aIndex = result.indexOf("ANO:");
        /* if (aIndex >= 0) {
            String ANO = result.substring(aIndex + 4);
            getAssItemInfo(ANO);
        } else if (rIndex >= 0) {
            int eIndex = result.indexOf(";ANO:");
            String RNO = result.substring(rIndex + 4, eIndex);
            getResItemInfo(RNO);
        } else {
            String[] codes = result.split("-");
            if (codes.length == 3) {
                getAssItemInfo(result);
            } else {
                getResItemInfo(result);
            }
        }*/

        if (rIndex >= 0 && aIndex >= 0) {   // RNO:且ANO:
            String ANO = result.substring(aIndex + 4);
            int eIndex = result.indexOf(";ANO:");
            String RCO = result.substring(rIndex + 4, eIndex);
            gotoResAssInfo(ANO, RCO);
        } else if (aIndex >= 0 && rIndex < 0) {// ANO:
            String ANO = result.substring(aIndex + 4);
            getAssItemInfo(ANO);
        } else if (rIndex >= 0 && aIndex < 0) {// RNO:
            String RNO = result.substring(rIndex + 4);
            getResItemInfo(RNO);
        } else {
            String[] codes = result.split("-");
            if (codes.length == 3) {
                getAssItemInfo(result);
            } else {
                getResItemInfo(result);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            this.setResult(RESULT_OK, data);
            this.finish();
        }
    }

    public void showMessageDialog(String message) {
        DialogFragment fragment = MessageDialogFragment.newInstance("Scan Results", message, this);
        fragment.show(getSupportFragmentManager(), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }


    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<>();
            for (int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(BarcodeFormat.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
    }

    private void getResItemInfo(final String resCode) {
        proDialog = null;
        proDialog = App.progressDialog(context,
                getResources().getString(R.string.common_request));
        proDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                resConn(resCode);
            }
        }).start();
    }

    private void resConn(String resCode) {
        StringBuilder url = new StringBuilder(DataSiteStart.HTTP_SERVER_URL);
        String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + resCode);
        try {
            resCode = URLEncoder.encode(resCode, App.ENCODE_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url.append("AssetsResInfo.do?rescode=").append(resCode).append("&sign=").append(sign);
        AppHttpConnection conn = new AppHttpConnection(context, url.toString());
        ALog.d(url.toString());
        String conResult = conn.getConnectionResult();
        if (proDialog != null && proDialog.isShowing()) {
            proDialog.dismiss();
        }
        if (conResult.equals("fail")) {
            return;
        }
        try {
            conResult = AESCryto.decrypt(DataSiteStart.AES_KEY, conResult);
            conResult = URLDecoder.decode(conResult, App.ENCODE_UTF8);
            JSONObject resJson = new JSONObject(conResult);
            ALog.d(resJson.toString());
            String result = resJson.getString("result");
            final String msg = resJson.getString("msg");
            if (!result.equals("1")) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        scanResult.setText("");
                        scanFormat.setText("");
                    }
                });
                // 重新扫描
                mScannerView.setResultHandler(this);
                mScannerView.startCamera(mCameraId);
                mScannerView.setFlash(mFlash);
                mScannerView.setAutoFocus(mAutoFocus);
                return;
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
            JSONArray array = resJson.getJSONArray("data");
            Bundle bundle = new Bundle();
            bundle.putString("json", array.get(0).toString());
            Intent intent = new Intent(context, AssetResItemInfoActivity_.class);
            intent.putExtras(bundle);
            startActivity(intent);
            this.finish();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void getAssItemInfo(final String assNo) {
        // added by wuhua for card loc
        FuncGetCardLoc func = new FuncGetCardLoc(context);
        LatLng loc = MapUtils.bdLnChangeToGpsLn(new LatLng(MainApp.geoBD.location.getLatitude(), MainApp.geoBD.location.getLongitude()));
        func.getCardLoc(assNo, loc.longitude, loc.latitude);
        // end by wuhua
        Bundle bundle = new Bundle();
        bundle.putString("result", assNo);
        Intent intent = new Intent(context, AssetCardItemInfoActivity_.class);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    private void gotoResAssInfo(String assNo, String resCode) {
        // added by wuhua for card loc
        FuncGetCardLoc func = new FuncGetCardLoc(context);
        LatLng loc = MapUtils.bdLnChangeToGpsLn(new LatLng(MainApp.geoBD.location.getLatitude(), MainApp.geoBD.location.getLongitude()));
        func.getCardLoc(assNo, loc.longitude, loc.latitude);
        // end by wuhua
        Bundle bundle = new Bundle();
        bundle.putString("ASS_NO", assNo);
        bundle.putString("RES_CODE", resCode);
        Intent intent = new Intent(context, AssetResAssInfoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

}
