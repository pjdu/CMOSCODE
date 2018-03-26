/*
 * Created by AndyHua on 2017/10/23.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-10-20 10:51:47.
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
 * 接入传输设备信息
 * 该功能由于时间急迫、因此没有进行模块封装、采用面向功能的思想开发、代码冗余极大
 */
@EActivity(R.layout.activity_asset_mobile_transfer)
public class MobileTransferActivity extends BaseActivity {

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
        initToolbar("接入传输设备");
    }

    @Click(R.id.btn_close)
    void closeBtnClick() {
        onBackPressed();
    }

    @Click(R.id.txt_item_transfer_device)
    void transferDeviceBtnClick() {
        Intent intent = new Intent(context, MobileTransferDeviceActivity_.class);
        be.putString("title", "传输设备");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.txt_item_transfer_general)
    void transferGeneralBtnClick() {
        Intent intent = new Intent(context, MobileTransferGeneralActivity_.class);
        be.putString("title", "综合柜");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.txt_item_insert_device)
    void insertDeviceBtnClick() {
        Intent intent = new Intent(context, MobileConItemActivity.class);
        be.putString("title", "接入设备(A)");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.txt_item_converge_device)
    void convergeDeviceBtnClick() {
        Intent intent = new Intent(context, MobileConItemActivity.class);
        be.putString("title", "汇聚设备(B)");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.txt_item_pdh_device)
    void pdhDeviceBtnClick() {
        Intent intent = new Intent(context, MobileConItemActivity.class);
        be.putString("title", "PDH复合设备");
        intent.putExtras(be);
        startActivityForResult(intent, 0);
    }

    @Click(R.id.btn_assets_link)
    void transferAssetsLinkBtnCLick() {
        Bundle be = new Bundle();
        be.putString("id", id);
        be.putString("type", "传输");
        be.putString("title", "接入传输设备资产卡片");
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
