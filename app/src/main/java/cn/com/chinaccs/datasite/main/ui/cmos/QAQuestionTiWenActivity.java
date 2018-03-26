package cn.com.chinaccs.datasite.main.ui.cmos;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;

public class QAQuestionTiWenActivity extends BaseActivity {
	private Context context;
	private Button questionBtnImg;
	private Button questionBtnDatac;
	private EditText editQuestion;
//	private EditText editBiaoQian;
	private TextView syNum;
	private int num = 140;
	private String questionTag;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_qa_question_tiwen);
		Bundle be = getIntent().getExtras();
		questionTag = be.getString("questionTag");
		this.findViews();
		initToolbar(getResources().getString(R.string.qa_question));
		questionBtnImg.setOnClickListener(btnLr);
		questionBtnDatac.setOnClickListener(btnLr);
		syNum.setText(num+"字");
		editQuestion.addTextChangedListener(new TextWatcher() {
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
				selectionStart = editQuestion.getSelectionStart();
				selectionEnd = editQuestion.getSelectionEnd();
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					editQuestion.setText(s);
					editQuestion.setSelection(tempSelection);
				}
			}
		});
	}
	
	private void findViews(){
		questionBtnImg = (Button) findViewById(R.id.question_btn_img);
		questionBtnDatac = (Button) findViewById(R.id.question_btn_datac);
		editQuestion = (EditText)findViewById(R.id.edit_question);
//		editBiaoQian = (EditText)findViewById(R.id.edit_question_biaoqian);
		syNum = (TextView)findViewById(R.id.shen_yu_zi_shu);
	}
	
	private OnClickListener btnLr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Intent i = null;
			switch (v.getId()) {
			case R.id.question_btn_img:
				
				break;
			case R.id.question_btn_datac:
				    Pattern pattern = Pattern.compile("^\\s*$");
				    Matcher isNull = pattern.matcher(editQuestion.getText().toString());
				    if (isNull.matches()) {
				    	Toast.makeText(context, "提问不能为空，请输入你的问题！", Toast.LENGTH_SHORT).show();						
					} else {
						AlertDialog ad = App.alertDialog(context, "提示！", "确定提交问题？",
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
		ProgressDialog pd = App.progressDialog(context, getResources()
				.getString(R.string.common_response));
		pd.show();
		final JSONObject output = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			String question = editQuestion.getText().toString();
//			String biaoqian = editBiaoQian.getText().toString();
			array.put(question);
//			array.put(biaoqian);
			array.put(questionTag);
			SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
			output.put("userid",
					share.getString(AppCheckLogin.SHARE_USER_ID, ""));
			output.put("datatype", DataSiteStart.TYPE_QA_QUESTION);
			output.put("data", array);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		final DsHandler hr = new DsHandler(context, pd);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				conn(hr, output.toString());
			}
		}).start();
		
	}
	
	private void conn(Handler hr, String output) {
		Message msg = null;
		StringBuffer br = new StringBuffer(DataSiteStart.HTTP_SERVER_URL)
				.append("DSSaveCommon.do");
		String url = br.toString();
		AppHttpClient client = new AppHttpClient(context);
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
	
	static class DsHandler extends Handler {
		private Context context;
		private ProgressDialog pd;

		public DsHandler(Context context, ProgressDialog pd) {
			this.context = context;
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
				Toast.makeText(context, "数据提交成功 ！", Toast.LENGTH_LONG).show();		
				Intent intent = new Intent(((Activity) context),QAQuestionActivity.class);
				((Activity) context).setResult(RESULT_OK,intent);
				((Activity) context).finish();					
				break;
			case 500:
				Toast.makeText(context, "连接失败：请检查网络连接！", Toast.LENGTH_LONG)
						.show();
				break;
			case 501:
				String info = (String) msg.obj;
				Toast.makeText(context, "提示：" + info, Toast.LENGTH_LONG).show();
				break;
			default:
				Toast.makeText(context, "未知错误", Toast.LENGTH_LONG).show();
				break;
			}
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
		}
	}

}
