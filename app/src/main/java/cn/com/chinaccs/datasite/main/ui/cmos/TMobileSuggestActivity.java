package cn.com.chinaccs.datasite.main.ui.cmos;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;

public class TMobileSuggestActivity extends BaseActivity {
	
	private Activity act;
	private Button sgBtn;
	private EditText sgEdit;
	private TextView syNum;
	private int num = 200;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.act = this;
		setContentView(R.layout.activity_mobile_suggest);
		this.findViews();
		initToolbar(getResources().getString(R.string.suggest));
		sgBtn.setOnClickListener(btnLr);
		syNum.setText(num+"字");
		sgEdit.addTextChangedListener(textLr);
	}
	
	private void findViews(){
		sgBtn = (Button)findViewById(R.id.sg_submit_btn);
		sgEdit = (EditText)findViewById(R.id.sg_edite);
		syNum = (TextView)findViewById(R.id.sg_shen_yu_zi_shu);
	}
	
	private TextWatcher textLr = new TextWatcher() {
		private CharSequence temp;
		private int selectionStart;
		private int selectionEnd;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			temp = s;
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			int number = num - s.length();
			syNum.setText("还剩"+number+"字");
			selectionStart = sgEdit.getSelectionStart();
			selectionEnd = sgEdit.getSelectionEnd();
			if (temp.length() > num) {
				s.delete(selectionStart - 1, selectionEnd);
				int tempSelection = selectionEnd;
				sgEdit.setText(s);
				sgEdit.setSelection(tempSelection);
			}
		}
	};
	
	private OnClickListener btnLr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Intent i = null;
			switch (v.getId()) {
			
			case R.id.sg_submit_btn:
				    Pattern pattern = Pattern.compile("^\\s*$");
				    Matcher isNull = pattern.matcher(sgEdit.getText().toString());
				    if (isNull.matches()) {
				    	Toast.makeText(act, "意见或建议不能为空，请输入您的意见或建议！", Toast.LENGTH_SHORT).show();						
					} else {
						AlertDialog ad = App.alertDialog(act, "提示！", "确定发送您的意见或建议吗？",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										saveDatas();
									}
								}, null);
						ad.show();
					}
					
				break;
			default:
				break;
			}
		}
	};
	
	private void saveDatas(){
		ProgressDialog pd = App.progressDialog(act, getResources()
				.getString(R.string.common_response));
		pd.show();
	
		final JSONObject output = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			String suggest = sgEdit.getText().toString();
			array.put(suggest);
			SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
			output.put("userid",
					share.getString(AppCheckLogin.SHARE_USER_ID, ""));
			output.put("data", array);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		final DsHandler hr = new DsHandler(act, pd);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				conn(hr,output.toString());
			}
		}).start();
	}
	
	private void conn(Handler hr,String output){
		Message msg = null;
		StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
				.append("TMobileSuggest.do");
		String url = br.toString();
		AppHttpClient client = new AppHttpClient(act);
		try {
			output = URLEncoder.encode(output, App.ENCODE_UTF8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String res = client.getResultByPOST(url, output);
		Log.d(App.LOG_TAG, res);
		if (res.equals(AppHttpClient.RESULT_FAIL)) {
			msg = hr.obtainMessage(500);
			hr.sendMessage(msg);
			return;
		}
		try {
			JSONObject resJson = new JSONObject(res);
			String result = resJson.getString("result");
			if (!result.equals("1")) {
				msg = hr.obtainMessage(501, resJson.getString("msg"));
				hr.sendMessage(msg);
				return;
			}
			msg = hr.obtainMessage(200);
			hr.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = hr.obtainMessage(500);
			hr.sendMessage(msg);
		}
	}
	
	static class DsHandler extends Handler{
		private TMobileSuggestActivity activity;
		private ProgressDialog pd;
        
		public DsHandler(Activity activity, ProgressDialog pd) {
			this.activity = (TMobileSuggestActivity) activity;
			this.pd = pd;
		}
		
		public DsHandler(OnClickListener onClickListener) {
			// TODO Auto-generated constructor stub
		}
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 200:
				Toast.makeText(activity, "意见或建议发送成功 ！", Toast.LENGTH_LONG).show();					
				break;
			case 500:
				Toast.makeText(activity, "连接失败：请检查网络连接！", Toast.LENGTH_LONG).show();				
				break;
			case 501:
				String info = (String) msg.obj;
				Toast.makeText(activity, "提示：" + info, Toast.LENGTH_LONG).show();				
				break;
			default:
				Toast.makeText(activity, "未知错误", Toast.LENGTH_LONG).show();				
				break;
			}
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
		}
	}

}
