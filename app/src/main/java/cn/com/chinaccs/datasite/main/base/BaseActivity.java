/*
 * Created by AndyHua on 2017/8/5.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2016-08-19 10:53:26.
 */

package cn.com.chinaccs.datasite.main.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.com.chinaccs.datasite.main.R;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressWarnings("ConstantConditions")
    protected void initToolbar(String title) {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle(title);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
