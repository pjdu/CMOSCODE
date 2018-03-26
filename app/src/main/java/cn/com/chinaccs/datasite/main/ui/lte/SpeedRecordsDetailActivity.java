package cn.com.chinaccs.datasite.main.ui.lte;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.XFastFactory.XJson.XJsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.bean.InfoSpeedRecords;


/**
 * Created by andyhua on 15-4-29.
 */
public class SpeedRecordsDetailActivity extends BaseActivity {
    private static final String TAG = "SpeedRecordsDetailActivity";

    private Context context;
    private TextView testTimeTv, testServerTv, downMaxSpeedTv, downAvgSpeedTv, upMaxSpeedTv, upAvgSpeedTv,
            pingDelayTv, testAddressLonTv, testAddressLatTv,
            testAddressDescriptionTv, netTypeTv, connectTypeTv, testSidTv,
            testNid, testBsid, testDbm, innerIp, outerIp, lteRsrp, lteRsrq,
            lteRssnr, lteCi, ltePci, lteTac;
    private InfoSpeedRecords item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 显示标题
        /*getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.speed_records_detail));*/
        setContentView(R.layout.activity_speed_records_detail);
        initToolbar(getResources().getString(R.string.speed_records_detail));
        this.findViews();

        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.containsKey("data")) {
            try {
                item = (InfoSpeedRecords) XJsonUtils.toClass(new JSONObject(extra.getString("data")),
                        InfoSpeedRecords.class);
                if (item != null) {
                    this.setTextToView(item);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void setTextToView(InfoSpeedRecords item) {
        testTimeTv.setText(item.testTime);
        testServerTv.setText(item.testServer);
        downMaxSpeedTv.setText(item.speedDownMax);
        downAvgSpeedTv.setText(item.speedDown);
        upMaxSpeedTv.setText(item.speedUpMax);
        upAvgSpeedTv.setText(item.speedUp);
        pingDelayTv.setText(item.pingDelay);
        testAddressLonTv.setText(item.lon);
        testAddressLatTv.setText(item.lat);
        testAddressDescriptionTv.setText(item.addr);
        netTypeTv.setText(item.networkType);
        connectTypeTv.setText(item.extra);
        testSidTv.setText(item.sid);
        testNid.setText(item.nid);
        testBsid.setText(item.ci);
        testDbm.setText(item.CDMA_Signal);
        innerIp.setText(item.innerIP);
        outerIp.setText(item.outerIP);
        lteRsrp.setText(item.LTE_RSRP);
        lteRsrq.setText(item.LTE_RSRQ);
        lteRssnr.setText(item.LTE_SINR);
        lteCi.setText(item.LTE_CI);
        ltePci.setText(item.LTE_Pci);
        lteTac.setText(item.LTE_Tac);
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

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void findViews() {
        testTimeTv = (TextView) findViewById(R.id.test_time);
        testServerTv = (TextView) findViewById(R.id.test_server);
        downMaxSpeedTv = (TextView) findViewById(R.id.down_max_speed);
        downAvgSpeedTv = (TextView) findViewById(R.id.down_avg_speed);
        upMaxSpeedTv = (TextView) findViewById(R.id.up_max_speed);
        upAvgSpeedTv = (TextView) findViewById(R.id.up_avg_speed);
        pingDelayTv = (TextView) findViewById(R.id.ping_delay);
        testAddressLonTv = (TextView) findViewById(R.id.test_adress_longitude);
        testAddressLatTv = (TextView) findViewById(R.id.test_adress_latitude);
        testAddressDescriptionTv = (TextView) findViewById(R.id.test_address_description);
        netTypeTv = (TextView) findViewById(R.id.net_type);
        connectTypeTv = (TextView) findViewById(R.id.connect_type);
        testSidTv = (TextView) findViewById(R.id.test_sid);
        testNid = (TextView) findViewById(R.id.test_nid);
        testBsid = (TextView) findViewById(R.id.test_bsid);
        testDbm = (TextView) findViewById(R.id.test_dbm);
        innerIp = (TextView) findViewById(R.id.inner_ip);
        outerIp = (TextView) findViewById(R.id.outer_ip);
        lteRsrp = (TextView) findViewById(R.id.lte_rsrp);
        lteRsrq = (TextView) findViewById(R.id.lte_rsrq);
        lteRssnr = (TextView) findViewById(R.id.lte_rssnr);
        lteCi = (TextView) findViewById(R.id.lte_ci);
        ltePci = (TextView) findViewById(R.id.lte_pci);
        lteTac = (TextView) findViewById(R.id.lte_tac);
    }
}
