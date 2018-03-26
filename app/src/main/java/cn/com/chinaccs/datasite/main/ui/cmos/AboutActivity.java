package cn.com.chinaccs.datasite.main.ui.cmos;

import android.os.Bundle;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;

/**
 * @author fddi
 * 
 */
public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initToolbar(getResources().getString(R.string.common_about));
	}

}
