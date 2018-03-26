package cn.com.chinaccs.datasite.main.ui.cmos;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetUserArea;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * @author Fddi 代维人员信息详细界面
 */
public class AgentUserInfoActivity extends Activity {
	private Context context;
	private String id;
	private String name;
	private TextView tvAUName;
	private TextView tvState;
	private ProgressDialog proDialog;
	private JSONObject jsonRes;
	private ListView lvAUTable;
	private Button btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_agent_user_info);
		Bundle be = getIntent().getExtras();
		id = be.getString("id");
		name = be.getString("name");
		this.findViews();
		tvAUName.setText(name);
		this.getData();
		btnClose.setOnClickListener(btnLr);
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

	private void buildItems(int[] ps, JSONObject json) {
		
	}
	
	private void getData() {
		FuncGetUserArea func = new FuncGetUserArea(context);
		proDialog = null;
		proDialog = App.progressDialog(context,
				getResources().getString(R.string.common_request));
		proDialog.show();
		OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

			@Override
			public void onFinished(String output) {
//				// TODO Auto-generated method stub
//				proDialog.dismiss();
//				try {
//					
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			};
		};
		func.getData(lr);
	}

	private OnClickListener btnLr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = null;
			switch (v.getId()) {
			case R.id.btn_close:
				finish();
				break;
			default:
				break;
			}
		}
	};

	private void findViews() {
		tvAUName = (TextView) findViewById(R.id.txt_agent_user_name);
		tvState = (TextView) findViewById(R.id.txt_state);
		lvAUTable = (ListView) findViewById(R.id.lv_au_info);
		btnClose = (Button) findViewById(R.id.btn_close);
	}


}
