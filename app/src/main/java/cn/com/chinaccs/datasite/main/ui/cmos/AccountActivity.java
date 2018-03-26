package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.ui.MainApp;

public class AccountActivity extends Activity {
	Context context;
	private TextView tvUserName;
	private TextView tvUserId;
	private Button btnCpwd;
	private Button btnExit;
	private SharedPreferences share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		this.findViews();
		this.context = AccountActivity.this;
		share = getSharedPreferences(App.SHARE_TAG, 0);
		String userName = share.getString(AppCheckLogin.SHARE_USER_NAME, "");
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		tvUserName.setText("当前用户：" + userName);
		tvUserId.setText("当前帐号：" + userId);
		btnCpwd.setOnClickListener(lr);
		btnExit.setOnClickListener(lr);
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

	private OnClickListener lr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_changepwd:
				Intent i = new Intent(context, ChangePwdActivity.class);
				startActivity(i);
				break;
			case R.id.btn_exitaccount:
				DialogInterface.OnClickListener dlr = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						share.edit()
								.putString(AppCheckLogin.SHARE_USER_NAME, "")
								.commit();
						share.edit()
								.putString(AppCheckLogin.SHARE_ORG_CODE, "")
								.commit();
						share.edit().putString(AppCheckLogin.SHARE_USER_ID, "")
								.commit();
						share.edit()
								.putString(AppCheckLogin.SHARE_USER_PWD, "")
								.commit();
						MainApp.RESTART_CODE = MainApp.CODE_RELOGIN;
						finish();
					}
				};
				AlertDialog ad = App.alertDialog(context, "提示！", "退出并注销当前帐号？",
						dlr, null);
				ad.show();
				break;
			default:
				break;
			}
		}
	};

	private void findViews() {
		tvUserName = (TextView) findViewById(R.id.tv_user_name);
		tvUserId = (TextView) findViewById(R.id.tv_user_id);
		btnCpwd = (Button) findViewById(R.id.btn_changepwd);
		btnExit = (Button) findViewById(R.id.btn_exitaccount);
	}
}
