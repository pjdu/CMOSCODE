/*
 * Created by AndyHua on 2017/12/4.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-12-04 17:15:28.
 */

package cn.com.chinaccs.datasite.main.ui.asset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;

public class AssetResAssInfoActivity extends BaseActivity {

    private FragmentTabHost mTabHost;

    private Bundle extras;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_res_ass_info);

        initToolbar("条码信息");

        extras = getIntent().getExtras();

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null); // 去掉分割线

        // 添加资产信息页面
        mTabHost.addTab(mTabHost.newTabSpec("res").setIndicator("资产信息"),
                AssetAssInfoFragment_.class, extras);
        // 添加资源信息页面
        mTabHost.addTab(mTabHost.newTabSpec("ass").setIndicator("资源信息"),
                AssetResInfoFragment_.class, extras);
    }
}
