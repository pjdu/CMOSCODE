package cn.com.chinaccs.datasite.main.ui.cmos;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.datasite.function.FuncServerChangePwd;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * @author Fddi
 * 
 */
public class ChangePwdActivity extends BaseActivity {
	Context context;
	private TextView tvUserId;
	private EditText etPwd;
	private EditText etNpwd;
	private EditText etApwd;
	private Button btnChange;
	private Button btnReset;
	private Dialog dialog;
	private String result;
	private SharedPreferences share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);
		findViews();
		initToolbar(getResources().getString(R.string.common_changepwd));
		this.context = ChangePwdActivity.this;
		share = getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		tvUserId.setText("当前帐号：" + userId);
		btnChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean notNull = ifNull();
				boolean isSame = contrastPwd();
				if (!notNull) {
					String info = "输入框不能为空";
					dialog = creatDialog(info);
					dialog.show();
				} else if (!isSame) {
					String info = "两次新密码输入不一致，请重新输入";
					dialog = creatDialog(info);
					dialog.show();
				} else {
					String pwd = etPwd.getText().toString();
					String newPwd = etNpwd.getText().toString();
					conn(pwd, newPwd);
				}
			}

		});
		btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				etPwd.setText("");
				etNpwd.setText("");
				etApwd.setText("");
			}

		});
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

	private void findViews() {
		tvUserId = (TextView) findViewById(R.id.tv_user_id);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		etNpwd = (EditText) findViewById(R.id.et_new_pwd);
		etApwd = (EditText) findViewById(R.id.et_again_pwd);
		btnChange = (Button) findViewById(R.id.btn_pwd_changed);
		btnReset = (Button) findViewById(R.id.btn_pwd_reset);
	}

	private Boolean contrastPwd() {// 判断两次输入是否一致
		boolean temp = false;
		String pwd_a = etNpwd.getText().toString();
		String pwd_b = etApwd.getText().toString();
		if (pwd_a.equals(pwd_b)) {
			temp = true;
		}
		return temp;
	}

	private Boolean ifNull() {// 判断是否用户未输入
		boolean temp = true;
		String pwd_a = etPwd.getText().toString();
		String pwd_b = etNpwd.getText().toString();
		String pwd_c = etApwd.getText().toString();
		boolean dataEmpty_a = TextUtils.isEmpty(pwd_a);
		boolean dataEmpty_b = TextUtils.isEmpty(pwd_b);
		boolean dataEmpty_c = TextUtils.isEmpty(pwd_c);
		if (dataEmpty_a || dataEmpty_b || dataEmpty_c) {
			temp = false;
		}
		return temp;
	}

	private Dialog creatDialog(String str) { // 调用对话框；
		Dialog normal;
		normal = new AlertDialog.Builder(ChangePwdActivity.this)
				.setTitle("提示！").setMessage(str)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create();
		return normal;

	}

	private void conn(String pwd, String newPwd) {
		final ProgressDialog pd = App.progressDialog(context, getResources()
				.getString(R.string.common_request));
		pd.show();
		FuncServerChangePwd server = new FuncServerChangePwd(context);
		OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

			@Override
			public void onFinished(String output) {
				// TODO Auto-generated method stub
				pd.dismiss();
				if (output.equals("fail")) {
					new AlertDialog.Builder(ChangePwdActivity.this)
							.setTitle("连接失败")
							.setMessage("无法连接到服务器，请检查你的手机网络设置")
							.setPositiveButton("确定", null).show();
					return;
				}
				try {
					JSONObject json = new JSONObject(output);
					result = json.getString("result");
					String msg = json.getString("msg");
					if (result.equals("1")) {
						new AlertDialog.Builder(ChangePwdActivity.this)
								.setTitle("提示！")
								.setMessage(msg)
								.setPositiveButton("关闭",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												finish();
											}
										}).show();
					} else {
						dialog = creatDialog(msg);
						dialog.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		server.changePwd(lr, pwd, newPwd);
	}
}
