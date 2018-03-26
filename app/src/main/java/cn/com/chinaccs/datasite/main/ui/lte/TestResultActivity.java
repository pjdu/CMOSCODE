package cn.com.chinaccs.datasite.main.ui.lte;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import java.math.BigDecimal;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;

/**
 * 网络信令
 * 
 * @author fddi
 * 
 */
public class TestResultActivity extends BaseActivity {
	Context context;
	ImageView imgLc;
	ImageView imgLevel;
	TextView tvUp;
	TextView tvDown;
	TextView tvMax;
	float avgSpeed = 0.0f;
	float maxSpeed = 0.0f;
	float avgUpSpeed = 0.0f;
	double rank;
	double total;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		// 显示标题
		/*getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(getString(R.string.speed_result));*/

		setContentView(R.layout.activity_test_result);
		initToolbar(getResources().getString(R.string.site_test_records));
		findViews();
		avgSpeed = getIntent().getExtras().getFloat("avgDownSpeed");
		maxSpeed = getIntent().getExtras().getFloat("maxDownSpeed");
		avgUpSpeed = getIntent().getExtras().getFloat("avgUpSpeed");
		updateText();
		updateImg();
	}

	private void updateText() {
		float v1 = avgSpeed;
		String unit1 = getString(R.string.speed_unit_kbps);
		if (avgSpeed / 1024 >= 1) {
			v1 = avgSpeed / 1024;
			unit1 = getString(R.string.speed_unit_mbps);
		}
		BigDecimal bd1 = new BigDecimal(v1);
		v1 = bd1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		String text1 = v1 + unit1;
		tvDown.setText(text1);
		float v2 = maxSpeed;
		String unit2 = getString(R.string.speed_unit_kbps);
		if (maxSpeed / 1024 >= 1) {
			v2 = maxSpeed / 1024;
			unit2 = getString(R.string.speed_unit_mbps);
		}
		BigDecimal bd2 = new BigDecimal(v2);
		v2 = bd2.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		String text2 = v2 + unit2;
		tvMax.setText(text2);

		if (avgUpSpeed != 0) {
			float v3 = avgUpSpeed;
			String unit3 = getString(R.string.speed_unit_kbps);
			if (avgUpSpeed / 1024 >= 1) {
				v3 = avgUpSpeed / 1024;
				unit3 = getString(R.string.speed_unit_mbps);
			}
			BigDecimal bd3 = new BigDecimal(v3);
			v3 = bd3.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			String text3 = v3 + unit3;
			tvUp.setText(text3);
		}
	}

	private void updateImg() {
		if (avgSpeed <= 300) {
			imgLevel.setImageResource(R.drawable.ic_level_walker);
			rotateAnimation(30);
		} else if (avgSpeed <= 500) {
			imgLevel.setImageResource(R.drawable.ic_level_cycle);
			rotateAnimation(60);
		} else if (avgSpeed <= 2000) {
			imgLevel.setImageResource(R.drawable.ic_level_motor);
			rotateAnimation(90);
		} else if (avgSpeed <= 5500) {
			imgLevel.setImageResource(R.drawable.ic_level_car);
			rotateAnimation(120);
		} else if (avgSpeed <= 10000) {
			imgLevel.setImageResource(R.drawable.ic_level_jet);
			rotateAnimation(180);
		} else {
			imgLevel.setImageResource(R.drawable.ic_level_rocket);
			rotateAnimation(210);
		}
	}

	private void rotateAnimation(float paramFloat) {
		RotateAnimation ra = new RotateAnimation(0, paramFloat, 1, 0.5F, 1,
				0.5F);
		ra.setDuration(1000);
		ra.setFillAfter(true);
		imgLc.startAnimation(ra);
		imgLc.invalidate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void findViews() {
		imgLc = (ImageView) findViewById(R.id.img_level_circle);
		imgLevel = (ImageView) findViewById(R.id.img_level);
		tvUp = (TextView) findViewById(R.id.tv_up);
		tvDown = (TextView) findViewById(R.id.tv_down);
		tvMax = (TextView) findViewById(R.id.tv_max);
	}
}
