/*
 * Created by AndyHua on 2017/10/20.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 09:31:25.
 */

package cn.com.chinaccs.datasite.main.ui.asset.mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;


/**
 * 资产信息
 * 空调电源设备信息
 * <p>
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
@EActivity(R.layout.activity_asset_mobile_power_content)
public class MobilePowerActivity extends BaseActivity {

    private Context context = null;
    private Bundle be = null;
    private String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        be = getIntent().getExtras();
        id = be.getString("id");
    }

    @AfterViews
    public void init() {
        initViews();
    }

    private void initViews() {
        initToolbar("空调电源设备");
    }

    @Click(R.id.txt_item_power_onoff)
    void powerOnOffBtnClick() {
        Intent intent = new Intent(context, MobilePowerOnOffActivity_.class);
        be.putString("title", "开关电源柜");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.txt_item_power_battery)
    void powerBatteryBtnClick() {
        Intent intent = new Intent(context, MobilePowerBatteryActivity_.class);
        be.putString("title", "蓄电池");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.txt_item_power_heater)
    void powerHeaterBtnClick() {
        Intent intent = new Intent(context, MobilePowerHeaterActivity_.class);
        be.putString("title", "空调");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.txt_item_power_dynamic)
    void powerDynamicBtnClick() {
        Intent intent = new Intent(context, MobilePowerDynamicActivity_.class);
        be.putString("title", "动环监控");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.txt_item_power_shock)
    void powerShockBtnClick() {
        Intent intent = new Intent(context, MobilePowerShockProofActivity_.class);
        be.putString("title", "防雷和配电箱");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.btn_assets_link)
    void powerAssetsLinkBtnCLick() {
        Bundle be = new Bundle();
        be.putString("id", id);
        be.putString("type", "电源");
        be.putString("title", "空调电源设备资产卡片");
        Intent intent = new Intent(context, MobileLinkedCardsActivity.class);
        intent.putExtras(be);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            onBackPressed();
        }
    }
}
