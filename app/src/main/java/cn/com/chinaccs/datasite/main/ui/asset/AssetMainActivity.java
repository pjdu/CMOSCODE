/*
 * Created by AndyHua on 2017/10/20.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 09:34:50.
 */

package cn.com.chinaccs.datasite.main.ui.asset;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.ui.asset.mobile.MobileInfoActivity;
import cn.com.chinaccs.datasite.main.ui.asset.optical.OpticalInfoActivity;

public class AssetMainActivity extends BaseActivity {

    private Context context;
    private ImageView assetMobile;
    private ImageView assetOptical;
    private TextView nameText;
    private TextView numberText;

    private ImageView setting;
    private FloatingActionButton scanBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_main);

        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        String name = share.getString(AppCheckLogin.SHARE_USER_NAME, "");
        nameText = (TextView) findViewById(R.id.name);
        numberText = (TextView) findViewById(R.id.number);
        nameText.setText("用户   " + name);
        numberText.setText("账号   " + userId);

        this.context = this;

        // 移动网络类
        assetMobile = (ImageView) findViewById(R.id.assetMobile);
        assetMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toThreeAssets = new Intent(context, MobileInfoActivity.class);
                startActivity(toThreeAssets);
            }
        });

        // 光网络类
        assetOptical = (ImageView) findViewById(R.id.assetOptical);
        assetOptical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toThreeAssets = new Intent(context, OpticalInfoActivity.class);
                startActivity(toThreeAssets);
            }
        });

        setting = (ImageView) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toChangePW = new Intent(context, AssetSettingActivity.class);
                startActivity(toChangePW);
            }
        });

        scanBar = (FloatingActionButton) findViewById(R.id.scanBarBtn);
        scanBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toScan = new Intent(context, AssetScannerActivity.class);
                startActivity(toScan);
            }
        });
    }
}
