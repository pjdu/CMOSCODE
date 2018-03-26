/*
 * Created by AndyHua on 2017/8/22.
 * Copyright © 2017年 ytf. All rights reserved.
 *
 * Last Modified on 2017-08-19 15:22:50.
 */

package cn.com.chinaccs.datasite.main.zbar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import cn.com.chinaccs.datasite.main.R;

public class BaseScannerActivity extends AppCompatActivity {
    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.scanner_activity);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
