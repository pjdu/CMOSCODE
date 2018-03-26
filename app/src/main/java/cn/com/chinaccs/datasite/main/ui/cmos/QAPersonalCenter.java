package cn.com.chinaccs.datasite.main.ui.cmos;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.QAQuestionListAdapter;
import cn.com.chinaccs.datasite.main.widget.TabDrawTop;


public class QAPersonalCenter extends BaseActivity {
	
	private TabHost personalHost;
	private Context context;
	private ListView questionLv;
	private ListView answerLv;
	private TextView scoreState;
	private TextView questionState;
	private TextView answerState;
	private TextView userName;
	private TextView answerTimes;
	private TextView txtAnswerTimes;
	private TextView txtAnswerCaina;
	private TextView txtAnswerDjy;
	private TextView question;
	private TextView answer;
	private Integer pagestart = 0;
	private Integer total = 0;
	private Boolean isRequestState = false;
	private ProgressDialog proDialog;
	private ProgressBar questionPB;
	private ProgressBar scorePB;
	private ProgressBar answerPB;
	private List<JSONArray> scoreListRes;
	private List<JSONArray> questionListRes;
	private List<JSONArray> answerListRes;
	private QAQuestionListAdapter qaAdapter;
	

	 protected void onCreate(Bundle savedInstanceState) 
	    { 
	        super.onCreate(savedInstanceState); 
	        this.context = this;
	        setContentView(R.layout.activity_qa_personal_center);
	        this.findView();
			initToolbar(getResources().getString(R.string.qa_personal_center));
	        this.buildScoreList();
	        this.buildAnswerList();
	        this.buildQuestionList();

	        View myScore = (View)LayoutInflater.from(context).inflate(R.layout.item_tab_widget, null);
	        TextView score = (TextView)myScore.findViewById(R.id.tab_label);
	        score.setText("我的成绩");
	        score.setTextColor(getResources().getColor(R.color.green_deep));
	        
	        View myQuestion = (View)LayoutInflater.from(context).inflate(R.layout.item_tab_widget, null);
	        question = (TextView)myQuestion.findViewById(R.id.tab_label);
	        question.setText("提问");
	        
	        View myAnswer = (View)LayoutInflater.from(context).inflate(R.layout.item_tab_widget, null);
	        answer = (TextView)myAnswer.findViewById(R.id.tab_label);
	        answer.setText("回答");
	        personalHost.setup();
	        personalHost.addTab(personalHost.newTabSpec("score").setIndicator(myScore)
	        		    .setContent(R.id.my_score));
	        personalHost.addTab(personalHost.newTabSpec("question").setIndicator(myQuestion)
	        		    .setContent(R.id.my_question));
	        personalHost.addTab(personalHost.newTabSpec("answer").setIndicator(myAnswer)
	        		    .setContent(R.id.my_answer));
	        TabDrawTop td=new TabDrawTop(personalHost);
	        td.getTabWidget();
	        personalHost.setCurrentTab(0);
	        personalHost.setOnTabChangedListener(new OnTabChangeListener() {
				
				@Override
				public void onTabChanged(String tabId) {
					// TODO Auto-generated method stub
					TabWidget tabWidget = personalHost.getTabWidget();
					for (int i = 0; i < tabWidget.getChildCount(); i++) {
						final TextView tv = (TextView) tabWidget.getChildAt(i)
								.findViewById(R.id.tab_label);
						if (personalHost.getCurrentTab() == i) {
							tv.setTextColor(getResources().getColor(
									R.color.green_deep));
						} else {
							tv.setTextColor(getResources().getColor(R.color.black));
						}
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
				Intent intent = new Intent(context,QAQuestionActivity.class);
				((Activity) context).setResult(RESULT_OK,intent);
				((Activity) context).finish();	
				Log.d("监听返回键", "是否执行到此处");
			}
			return true;
		}
		
		private void findView(){
			personalHost = (TabHost)findViewById(R.id.tab_personal);
			questionLv = (ListView)findViewById(R.id.lv_my_question);
			answerLv = (ListView)findViewById(R.id.lv_my_answer);
			scoreState = (TextView)findViewById(R.id.txt_my_score_state);
			questionState = (TextView)findViewById(R.id.txt_my_question_state);
			answerState = (TextView)findViewById(R.id.txt_my_answer_state);
			scorePB = (ProgressBar)findViewById(R.id.pb_my_score);
			questionPB = (ProgressBar)findViewById(R.id.pb_my_question);
			answerPB = (ProgressBar)findViewById(R.id.pb_my_answer);
			userName = (TextView)findViewById(R.id.qa_personal_user_name);
			answerTimes = (TextView)findViewById(R.id.qa_personal_answerTimes);
			txtAnswerTimes = (TextView)findViewById(R.id.txt_my_answertimes);
			txtAnswerCaina = (TextView)findViewById(R.id.txt_my_answercaina);
			txtAnswerDjy = (TextView)findViewById(R.id.txt_my_answerdjy);
		}
		
		private OnItemClickListener lrList = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
				String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
				JSONArray array = questionListRes.get(index);
				try {
					String questionId = array.getString(0);
					String questionDescrib = array.getString(1);
					String questionUserId = userId;
					String questionTime = array.getString(3);
					String userName = array.getString(5);
					String questionStatus = array.getString(6);
					Bundle be = new Bundle();
					be.putString("questionId", questionId);
					be.putString("questionDescrib", questionDescrib);
					be.putString("questionUserId", questionUserId);
					be.putString("userName", userName);
					be.putString("questionTime", questionTime);
					be.putString("questionStatus", questionStatus);
					Intent i = new Intent(context, QAQuestionAnswerAcitivity.class);
					i.putExtras(be);
					startActivity(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		private OnItemClickListener answerLrList = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				JSONArray array = answerListRes.get(index);
				try {
					String questionId = array.getString(0);
					String questionDescrib = array.getString(1);
					String questionUserId = array.getString(2);
					String questionTime = array.getString(3);
					String userName = array.getString(5);
					String questionStatus = array.getString(6);
					Bundle be = new Bundle();
					be.putString("questionId", questionId);
					be.putString("questionDescrib", questionDescrib);
					be.putString("questionUserId", questionUserId);
					be.putString("userName", userName);
					be.putString("questionTime", questionTime);
					be.putString("questionStatus", questionStatus);
					Intent i = new Intent(context, QAQuestionAnswerAcitivity.class);
					i.putExtras(be);
					startActivity(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		private void setListBottomEvent(){
			questionLv.setOnScrollListener(new OnScrollListener() {

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
						questionPB.setVisibility(View.VISIBLE);
						final Handler hr = new PcHandler(
								QAPersonalCenter.this, true);
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								questionConn(hr);
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
						final Handler hr = new PcHandler(
								QAPersonalCenter.this, true);
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								answerConn(hr);
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
		
		private void buildScoreList(){
			scoreState.setVisibility(View.VISIBLE);
			scoreState.setText(getResources().getString(R.string.common_request));
			scoreListRes = null;
			scoreListRes = new ArrayList<JSONArray>();	
			proDialog = null;
			proDialog = App.progressDialog(context,
					getResources().getString(R.string.common_request));
			proDialog.show();
			final PcHandler hr = new PcHandler(this, false);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					scoreConn(hr);
				}
			}).start();
		}
		
		private void buildQuestionList(){
			
			questionListRes = null;
			questionListRes = new ArrayList<JSONArray>();
			final PcHandler hr = new PcHandler(this, false);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					questionConn(hr);
				}
			}).start();
			setListBottomEvent();
		}
		
		private void buildAnswerList(){
			answerListRes = null;
			answerListRes = new ArrayList<JSONArray>();
			final PcHandler hr = new PcHandler(this, false);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					answerConn(hr);
				}
			}).start();
			setListBottomEvent();
		}
		
		private void scoreConn(Handler hr){
			SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
			String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
			Message msg = null;
			StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
			String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId );
			
			try {
				url.append("QAPersonalCenter.do?userid=").append(userId)
                .append("&sign=").append(sign);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				String conResult = conn.getConnectionResult();
				if (conResult.equals("fail")) {
					msg = hr.obtainMessage(201);
					hr.sendMessage(msg);
					return;
				}
				JSONObject resJson = new JSONObject(conResult);
				String result = resJson.getString("result");
				if (!result.equals("1")) {
					msg = hr.obtainMessage(202, resJson.getString("msg"));
					hr.sendMessage(msg);
					return;
				}
				String totalStr = resJson.getString("total");
				total = Integer.parseInt(totalStr);
				JSONArray array = resJson.getJSONArray("data");
                JSONArray data = array.getJSONArray(0);
			    scoreListRes.add(data);
				String testString = array.toJSONObject(array).toString();
				Log.d("score--test", testString);
				msg = hr.obtainMessage(200);
				hr.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				msg = hr.obtainMessage(201);
				hr.sendMessage(msg);
			}
			
		}
		
		private void questionConn(Handler hr){
			SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
			String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
			Message msg = null;
			StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
			String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId+ pagestart);			
			try {
				url.append("QAPcQuestion.do?userid=").append(userId)
				.append("&pagestart=").append(pagestart).append("&sign=")
				.append(sign);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				String conResult = conn.getConnectionResult();
				if (conResult.equals("fail")) {
					msg = hr.obtainMessage(301);
					hr.sendMessage(msg);
					return;
				}
				JSONObject resJson = new JSONObject(conResult);
				String result = resJson.getString("result");
				if (!result.equals("1")) {
					msg = hr.obtainMessage(302, resJson.getString("msg"));
					hr.sendMessage(msg);
					return;
				}
				pagestart += 10;
				String totalStr = resJson.getString("total");
				total = Integer.parseInt(totalStr);
				JSONArray array = resJson.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONArray data = array.getJSONArray(i);
					questionListRes.add(data);
				}
				String testString = array.toJSONObject(array).toString();
				Log.d("question--test", testString);
				msg = hr.obtainMessage(300);
				hr.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				msg = hr.obtainMessage(301);
				hr.sendMessage(msg);
			}
		}
		
		private void answerConn(Handler hr){
			SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
			String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
			Message msg = null;
			StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
			String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId+ pagestart);
			
			try {
				url.append("QAPcAnswer.do?userid=").append(userId)
				.append("&pagestart=").append(pagestart).append("&sign=")
				.append(sign);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				String conResult = conn.getConnectionResult();
				if (conResult.equals("fail")) {
					msg = hr.obtainMessage(401);
					hr.sendMessage(msg);
					return;
				}
				JSONObject resJson = new JSONObject(conResult);
				String result = resJson.getString("result");
				if (!result.equals("1")) {
					msg = hr.obtainMessage(402, resJson.getString("msg"));
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
				String testString = array.toJSONObject(array).toString();
				Log.d("answer--test", testString);
				msg = hr.obtainMessage(400);
				hr.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				msg = hr.obtainMessage(401);
				hr.sendMessage(msg);
			}
		}
		
		static class PcHandler extends Handler{
			
			private QAPersonalCenter activity;
			private boolean isPageRequest;
			
			public PcHandler(Activity activity, boolean isPageRequest){
				this.activity = (QAPersonalCenter) activity;
				this.isPageRequest = isPageRequest;
			}
			
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 200://scoreList成功
					if (!isPageRequest) {
						activity.scoreState.setVisibility(View.GONE);
						String answerTimes = "0";
						String answerCaina = "0";
						String answerDjy = "0";
						JSONArray array = activity.scoreListRes.get(0);
						try {
							if (!activity.scoreListRes.equals("")) {
								activity.txtAnswerTimes.setText(array.getString(1)+"次");
								activity.txtAnswerCaina.setText(array.getString(2)+"次");
								activity.txtAnswerDjy.setText(array.getString(3)+"次");
								activity.answerTimes.setText(array.getString(1)+"次");
								activity.userName.setText(array.getString(0));
							}else if (activity.scoreListRes.equals("")) {
								activity.txtAnswerTimes.setText(answerTimes+"次");
								activity.txtAnswerCaina.setText(answerCaina+"次");
								activity.answerTimes.setText(answerTimes+"次");
								activity.txtAnswerDjy.setText(answerDjy+"次");
							}
							
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						
					} 
					break;
				case 201:
					activity.scoreState.setText("连接失败：请检查网络连接！");
					Toast.makeText(activity.context, "连接失败：请检查网络连接！",
							Toast.LENGTH_LONG).show();
					break;
				case 202:
					String info = (String) msg.obj;
					String answerTimes = "0";
					String answerCaina = "0";
					String answerDjy = "0";
					SharedPreferences share = activity.getSharedPreferences(App.SHARE_TAG, 0);
					String userName = share.getString(AppCheckLogin.SHARE_USER_NAME, "");
					if (info.equals("无数据")) {
						activity.scoreState.setVisibility(View.GONE);
						activity.txtAnswerTimes.setText(answerTimes+"次");
						activity.txtAnswerCaina.setText(answerCaina+"次");
						activity.answerTimes.setText(answerTimes+"次");
						activity.txtAnswerDjy.setText(answerDjy+"次");
						activity.userName.setText(userName);
					} else {
						activity.scoreState.setText("提示：" + info);
						Toast.makeText(activity.context, "提示：" + info,
								Toast.LENGTH_LONG).show();	
					}
													
					break;
				case 300://questionList成功
					if (!isPageRequest) {
						activity.qaAdapter = null;
						activity.qaAdapter = new QAQuestionListAdapter(activity, activity.questionListRes);
						activity.questionLv.setVisibility(View.VISIBLE);
						activity.questionState.setVisibility(View.GONE);
						activity.questionLv.setAdapter(activity.qaAdapter);
						activity.questionLv.setOnItemClickListener(activity.lrList);
						Log.d("是否执行到这里", "questionlist");
					}
					else {
						if (activity.qaAdapter != null) {
							activity.qaAdapter.notifyDataSetChanged();
						}
					}
					break;
				case 301:
					activity.questionState.setText("连接失败：请检查网络连接！");
					Toast.makeText(activity.context, "连接失败：请检查网络连接！",
							Toast.LENGTH_LONG).show();
					break;
				case 302:
					String info1 = (String) msg.obj;
					if (info1.equals("无数据")) {
						activity.questionState.setText("你还没有提过任何问题哦！");
					} else {
						activity.questionState.setText("提示：" + info1);
						Toast.makeText(activity.context, "提示：" + info1,
								Toast.LENGTH_LONG).show();
					}
					
					break;
				case 400://answeList成功
					if (!isPageRequest) {
						activity.qaAdapter = null;
						activity.qaAdapter = new QAQuestionListAdapter(activity, activity.answerListRes);
						activity.answerLv.setVisibility(View.VISIBLE);
						activity.answerState.setVisibility(View.GONE);
						activity.answerLv.setAdapter(activity.qaAdapter);
						activity.answerLv.setOnItemClickListener(activity.answerLrList);
						Log.d("是否执行到这里", "answerlist");
					}
					else {
						if (activity.qaAdapter != null) {
							activity.qaAdapter.notifyDataSetChanged();
						}
					}
					break;
				case 401:
					activity.answerState.setText("连接失败：请检查网络连接！");
					Toast.makeText(activity.context, "连接失败：请检查网络连接！",
							Toast.LENGTH_LONG).show();
					break;
				case 402:
					String info2 = (String) msg.obj;
					if (info2.equals("无数据")) {
						activity.answerState.setText("你还没有回答过任何问题哦！");
					} else {
						activity.answerState.setText("提示：" + info2);
						Toast.makeText(activity.context, "提示：" + info2,
								Toast.LENGTH_LONG).show();
					}					
					break;

				default:
					activity.scoreState.setText("未知错误");
					activity.questionState.setText("未知错误");
					activity.answerState.setText("未知错误");
					break;
				}
				if (activity.proDialog != null && activity.proDialog.isShowing()) {
					activity.proDialog.dismiss();
					activity.proDialog = null;
				}
				if (isPageRequest) {
					activity.scorePB.setVisibility(View.GONE);
					activity.questionPB.setVisibility(View.GONE);
					activity.answerPB.setVisibility(View.GONE);
				}
				activity.isRequestState = false;
			}
		}

}
