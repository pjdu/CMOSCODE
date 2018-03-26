package cn.com.chinaccs.datasite.main.ui.cmos;



import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.QAAnswerListAdapter;


public class QAQuestionAnswerAcitivity extends BaseActivity {
	
	private String questionId;
	private String tiwenUserId;
	private String userId;
	private String questionStatus;
	private String answerStatus;
	private String answerId;
	private int adIndex;
	private Context context;
	private Integer pagestart = 0;
	private Integer total = 0;
	private Boolean isRequestState = false;
	private ProgressDialog proDialog;
	private QAAnswerListAdapter qaAdapter;
	private TextView answerState;
	private ListView answerLv;
	private ProgressBar answerPB;
	private List<JSONArray> answerListRes;
	private LinearLayout isUserTiwen;
	private TextView questionName;
	private TextView questionUserName;
	private TextView questionCreateTime;
	private Button answerBtn;
	private Button answerClBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_qa_question_wendaye);
		Bundle be = getIntent().getExtras();
		questionId = be.getString("questionId");
		this.findViews();
		initToolbar(getResources().getString(R.string.question_wendaye));
		String questionDescrib = be.getString("questionDescrib");
		questionName.setText(questionDescrib);
	    tiwenUserId = be.getString("questionUserId");
	    String userName = be.getString("userName");
		questionUserName.setText(userName);
		String questionTime = be.getString("questionTime");
		questionCreateTime.setText(questionTime);	
		questionStatus = be.getString("questionStatus");
		adIndex = be.getInt("adIndex");
		this.buildList();		
		answerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Bundle be = new Bundle();
					be.putString("questionId", questionId);
					Intent i = new Intent(context,QAAnswerAcitivity.class);
					i.putExtras(be);
					startActivityForResult(i, 1);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
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
	
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Bundle be = new Bundle();
			be.putString("questionStatus", questionStatus);
			be.putInt("adIndex", adIndex);
			Intent intent = new Intent(context,QAQuestionActivity.class);
			intent.putExtras(be);
			((Activity) context).setResult(RESULT_OK,intent);
			((Activity) context).finish();	
			Log.d("监听返回键", "是否执行到此处");
		}
		return true;
	}
	
	private OnItemClickListener lrList = new OnItemClickListener() {
         
		String [] judge = new String[]{"最佳答案","最酱油答案"};
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int index,
				long arg3) {
			// TODO Auto-generated method stub
			JSONArray array = answerListRes.get(index);
			try {
				answerId = array.getString(0);
				if (Integer.parseInt(questionStatus) == 1) {
					AlertDialog dialog=new AlertDialog.Builder(context).setItems(judge, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (which == 0) {
								answerStatus = "2";
								judgeAnswer();
							} else if (which == 1) {
								answerStatus = "3";
								judgeAnswer();
							} 							
							dialog.dismiss();
						}
					}).create();
					dialog.setTitle("评价");
					dialog.show();
				} 
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	private OnClickListener closedQuestion = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final AlertDialog ad = App.alertDialog(context, "提示！", "确定关闭问题？",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							closedQa();
						}
					}, null);
			ad.show();
		}
	};
	
	private void judgeAnswer(){
	
		final QaHandler hr = new QaHandler(this, false);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				connJugde(hr);
			}
		}).start();
	}
	
	private void closedQa(){
		
		final QaHandler hr = new QaHandler(this, false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				connClose(hr);
			}
		}).start();
	}
	
	private void findViews(){
		answerState = (TextView)findViewById(R.id.answer_state);
		answerPB = (ProgressBar)findViewById(R.id.answer_pb);
		isUserTiwen = (LinearLayout)findViewById(R.id.is_user_tiwen);
		questionName = (TextView)findViewById(R.id.question_name);
		questionUserName = (TextView)findViewById(R.id.question_userId);
		questionCreateTime = (TextView)findViewById(R.id.question_createTime);
		answerBtn = (Button)findViewById(R.id.answer_btn_datac);
		answerLv = (ListView)findViewById(R.id.answer_lv);
		answerClBtn = (Button)findViewById(R.id.answer_btn_closed);
	}
	
	private void buildList(){
		answerState.setVisibility(View.VISIBLE);
		answerState.setText(getResources().getString(R.string.common_request));
		answerListRes = null;
		answerListRes = new ArrayList<JSONArray>();
		proDialog = null;
		proDialog = App.progressDialog(context,
				getResources().getString(R.string.common_request));
		proDialog.show();
		final QaHandler hr = new QaHandler(this, false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				conn(hr);
			}
		}).start();
		
		setListBottomEvent();
		
	}
	
	
	private void setListBottomEvent() {
		answerLv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
					if (total <= pagestart && pagestart != 0) {
						// 起始页从零开始，累加10，当大于总数时说明已无数据
						return;
					}
					if (isRequestState) {
						// 避免重复请求，若正在请求数据时返回
						return;
					}
					isRequestState = true;
					answerPB.setVisibility(View.VISIBLE);
					final Handler hr = new QaHandler(
							QAQuestionAnswerAcitivity.this, true);
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							conn(hr);
						}
					}).start();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				pagestart = 0;
				this.buildList();
			}			
			break;
		

		default:
			break;
		}
	}
	
	private void conn(Handler hr){
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
	    userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
				+ questionId + pagestart);
		try {
			url.append("QAQuestionAnswer.do?userid=").append(userId)
			.append("&quetionId=").append(questionId)
			.append("&pagestart=").append(pagestart).append("&sign=")
			.append(sign);
			AppHttpConnection conn = new AppHttpConnection(context,
					url.toString());
			String conResult = conn.getConnectionResult();
			if (conResult.equals("fail")) {
				msg = hr.obtainMessage(500);
				hr.sendMessage(msg);
				return;
			}
			JSONObject resJson = new JSONObject(conResult);
			String result = resJson.getString("result");
			if (!result.equals("1")) {
				msg = hr.obtainMessage(501, resJson.getString("msg"));
				hr.sendMessage(msg);
				return;
			}
			pagestart += 10;
			String totalStr = resJson.getString("total");
			total = Integer.parseInt(totalStr);
			JSONArray array = resJson.getJSONArray("data");
			for (int i = 0; i < array.length(); i++) {
				JSONArray data = array.getJSONArray(i);
				answerListRes.add(data);
			}
			
			//String testString = array.toJSONObject(array).toString();
			Log.d("---最佳答案---", answerListRes.toString());
			msg = hr.obtainMessage(200);
			hr.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = hr.obtainMessage(500);
			hr.sendMessage(msg);
		}
		
	}
	
	private void connClose(Handler hr) {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
	    userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
				+ questionId);
		try {
			url.append("QAClosedQuestion.do?userid=").append(userId)
			.append("&quetionId=").append(questionId)
			.append("&sign=").append(sign);
			AppHttpConnection conn = new AppHttpConnection(context,
					url.toString());
			String conResult = conn.getConnectionResult();
			Log.d("--conResult--", conResult);
			if (conResult.equals("fail")) {
				msg = hr.obtainMessage(500);
				hr.sendMessage(msg);
				return;
			}
			JSONObject resJson = new JSONObject(conResult);
			String result = resJson.getString("result");
			if (!result.equals("1")) {
				msg = hr.obtainMessage(502, resJson.getString("msg"));
				hr.sendMessage(msg);
				return;
			}
			msg = hr.obtainMessage(400);
			hr.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = hr.obtainMessage(500);
			hr.sendMessage(msg);
		}
	}
	
	private void connJugde(Handler hr) {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
	    userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		Message msg = null;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
				+ questionId + answerId + answerStatus);
		Log.d("---test---", answerId+answerStatus);
		try {
			url.append("QAJudgeAnswer.do?userid=").append(userId)
			.append("&quetionId=").append(questionId)
			.append("&answerId=").append(answerId)
			.append("&answerStatus=").append(answerStatus)
			.append("&sign=").append(sign);
			AppHttpConnection conn = new AppHttpConnection(context,
					url.toString());
			String conResult = conn.getConnectionResult();
			Log.d("--conResult--", conResult);
			if (conResult.equals("fail")) {
				msg = hr.obtainMessage(500);
				hr.sendMessage(msg);
				return;
			}
			JSONObject resJson = new JSONObject(conResult);
			String result = resJson.getString("result");
			if (!result.equals("1")) {
				msg = hr.obtainMessage(502, resJson.getString("msg"));
				hr.sendMessage(msg);
				return;
			}
			msg = hr.obtainMessage(300);
			hr.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = hr.obtainMessage(500);
			hr.sendMessage(msg);
		}
	}
	
	static class QaHandler extends Handler{
		private QAQuestionAnswerAcitivity activity;
        private boolean isPageRequest;
		
		public QaHandler(Activity activity, boolean isPageRequest) {
			this.activity = (QAQuestionAnswerAcitivity) activity;
			this.isPageRequest = isPageRequest;
		}
		
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 200:
				if (!isPageRequest) {
						activity.qaAdapter = null;
						activity.qaAdapter = new QAAnswerListAdapter(activity,
								activity.answerListRes);
						activity.answerLv.setVisibility(View.VISIBLE);
						activity.answerState.setVisibility(View.GONE);
						activity.answerLv.setAdapter(activity.qaAdapter);
						if (activity.tiwenUserId.equals(activity.userId)) {
							activity.answerLv.setOnItemClickListener(activity.lrList);
							activity.answerClBtn.setVisibility(Button.VISIBLE);
							activity.answerClBtn.setOnClickListener(activity.closedQuestion);
						}
						if (Integer.parseInt(activity.questionStatus) == 1) {
							activity.isUserTiwen.setVisibility(LinearLayout.VISIBLE);
						}
						Log.d("---userId--", activity.tiwenUserId+"----"+activity.userId);										
				} else {
					if (activity.qaAdapter != null)
						activity.qaAdapter.notifyDataSetChanged();
				}
				break;
			case 300:
				Toast.makeText(activity, "问题评价成功 ！", Toast.LENGTH_LONG).show();			
				activity.pagestart = 0;				
				activity.buildList();
				break;
			case 400:
				Toast.makeText(activity, "问题关闭成功 ！", Toast.LENGTH_LONG).show();
				activity.pagestart = 0;
				activity.questionStatus = "3";
				activity.buildList();
				activity.isUserTiwen.setVisibility(LinearLayout.GONE);
				break;
			case 500:
				activity.answerState.setText("连接失败：请检查网络连接！");
				Toast.makeText(activity.context, "连接失败：请检查网络连接！",
						Toast.LENGTH_LONG).show();
				break;
			case 501:
				String info = (String) msg.obj;
				if (info.equals("无数据")&& !activity.questionStatus.equals("3")) {
					activity.answerState.setText("提示：" + "暂时还没有人回答此问题！");
					activity.isUserTiwen.setVisibility(LinearLayout.VISIBLE);
					if (activity.tiwenUserId.equals(activity.userId)) {
						activity.answerClBtn.setVisibility(Button.VISIBLE);
						activity.answerClBtn.setOnClickListener(activity.closedQuestion);
					}
				}
				if (info.equals("无数据")&& activity.questionStatus.equals("3")) {
					activity.answerState.setText("提示：" + "该问题没有答案且已被主人关闭！");
				}
				break;
			case 502:
				String info1 = (String) msg.obj;
				activity.answerState.setText("提示：" + info1);
				Toast.makeText(activity.context, "提示：" + info1,
						Toast.LENGTH_LONG).show();
				break;
			default:
				activity.answerState.setText("未知错误");
				Toast.makeText(activity.context, "未知错误", Toast.LENGTH_LONG)
						.show();
				break;
			}
			if (activity.proDialog != null && activity.proDialog.isShowing()) {
				activity.proDialog.dismiss();
				activity.proDialog = null;
			}
			if (isPageRequest) {
				activity.answerPB.setVisibility(View.GONE);
			}
			activity.isRequestState = false;
		}
	}
	
}
