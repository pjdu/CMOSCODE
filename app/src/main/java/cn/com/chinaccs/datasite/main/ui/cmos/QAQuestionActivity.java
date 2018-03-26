package cn.com.chinaccs.datasite.main.ui.cmos;

import java.net.URLEncoder;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import cn.com.chinaccs.datasite.main.datasite.function.QAQuestionListAdapter;

public class QAQuestionActivity extends BaseActivity {

    private Context context;
    private Button questionTJ;
    private Integer pagestart = 0;
    private Integer total = 0;
    private Boolean isRequestState = false;
    private ProgressDialog proDialog;
    private QAQuestionListAdapter qaAdapter;
    // private ImageButton personalCenter;
    private EditText questionEdit;
    private Button questionQuery;
    private TextView questionState;
    private ListView questionLv;
    private ProgressBar questionPB;
    private List<JSONArray> listRes;
    private TextView syNum;
    private int num = 140;
    private String questionTag;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_qa_question);
        Bundle be = getIntent().getExtras();
        questionTag = be.getString("questionTag");
        this.findViews();
        initToolbar(getResources().getString(R.string.question_content));
        this.buildList();
        syNum.setText(num + "字");
        questionQuery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pagestart = 0;
                total = 0;
                if (!isRequestState) {
                    isRequestState = true;
                    buildList();
                }
            }
        });

        questionTJ.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    Bundle be = new Bundle();
                    be.putString("questionTag", questionTag);
                    Intent i = new Intent(context, QAQuestionTiWenActivity.class);
                    i.putExtras(be);
                    startActivityForResult(i, 2);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
/*
        personalCenter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    Intent i = new Intent(context, QAPersonalCenter.class);
                    startActivityForResult(i, 4);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });*/

        questionEdit.addTextChangedListener(new TextWatcher() {
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
                syNum.setText("还剩" + number + "字");
                selectionStart = questionEdit.getSelectionStart();
                selectionEnd = questionEdit.getSelectionEnd();
                if (temp.length() > num) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    questionEdit.setText(s);
                    questionEdit.setSelection(tempSelection);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_personal) {
            // TODO Auto-generated method stub
            try {
                Intent i = new Intent(context, QAPersonalCenter.class);
                startActivityForResult(i, 4);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        // personalCenter = (ImageButton)findViewById(R.id.question_personal_center);
        questionEdit = (EditText) findViewById(R.id.question_edit);
        questionQuery = (Button) findViewById(R.id.question_query);
        questionState = (TextView) findViewById(R.id.question_state);
        questionLv = (ListView) findViewById(R.id.question_lv);
        questionPB = (ProgressBar) findViewById(R.id.question_pb);
        questionTJ = (Button) findViewById(R.id.question_datac);
        syNum = (TextView) findViewById(R.id.shen_yu_zi_shu_question);
    }

    private void buildList() {
        questionState.setVisibility(View.VISIBLE);
        questionState.setText(getResources().getString(R.string.common_request));
        listRes = null;
        listRes = new ArrayList<JSONArray>();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    pagestart = 0;
                    this.qaAdapter = null;
                    this.buildList();
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    pagestart = 0;
                    this.qaAdapter = null;
                    this.buildList();
                }
                break;
            case 4:
                if (resultCode == RESULT_OK) {
                    pagestart = 0;
                    this.qaAdapter = null;
                    this.buildList();
                }
                break;

            default:
                break;
        }
    }

    private OnItemClickListener lrList = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index,
                                long arg3) {
            // TODO Auto-generated method stub
            JSONArray array = listRes.get(index);
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
                be.putInt("adIndex", index);
                Intent i = new Intent(context, QAQuestionAnswerAcitivity.class);
                i.putExtras(be);
                startActivityForResult(i, 3);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private void setListBottomEvent() {
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
                    final Handler hr = new QaHandler(
                            QAQuestionActivity.this, true);
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


    private void conn(Handler hr) {
        SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
        String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
        Message msg = null;
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        String name = questionEdit.getText().toString();
        name = name == null ? "" : name;
        String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
                + name + questionTag + pagestart);
        try {
            name = URLEncoder.encode(name, App.ENCODE_UTF8);
            url.append("QAQuestion.do?userid=").append(userId)
                    .append("&name=").append(name).append("&questionTag=").append(questionTag)
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
                listRes.add(data);
            }
            String testString = array.toJSONObject(array).toString();
            Log.d("test", testString);
            msg = hr.obtainMessage(200);
            hr.sendMessage(msg);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            msg = hr.obtainMessage(500);
            hr.sendMessage(msg);
        }

    }

    static class QaHandler extends Handler {
        private QAQuestionActivity activity;
        private boolean isPageRequest;

        public QaHandler(Activity activity, boolean isPageRequest) {
            this.activity = (QAQuestionActivity) activity;
            this.isPageRequest = isPageRequest;
        }

        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 200:
                    if (!isPageRequest) {
                        activity.qaAdapter = null;
                        activity.qaAdapter = new QAQuestionListAdapter(activity,
                                activity.listRes);
                        activity.questionLv.setVisibility(View.VISIBLE);
                        activity.questionState.setVisibility(View.GONE);
                        activity.questionLv.setAdapter(activity.qaAdapter);
                        activity.questionLv.setOnItemClickListener(activity.lrList);
                    } else {
                        if (activity.qaAdapter != null)
                            activity.qaAdapter.notifyDataSetChanged();
                    }
                    break;
                case 500:
                    activity.questionState.setText("连接失败：请检查网络连接！");
                    Toast.makeText(activity.context, "连接失败：请检查网络连接！",
                            Toast.LENGTH_LONG).show();
                    break;
                case 501:
                    String info = (String) msg.obj;
                    activity.questionState.setText("提示：" + info);
                    Toast.makeText(activity.context, "提示：" + info,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    activity.questionState.setText("未知错误");
                    Toast.makeText(activity.context, "未知错误", Toast.LENGTH_LONG)
                            .show();
                    break;
            }
            if (activity.proDialog != null && activity.proDialog.isShowing()) {
                activity.proDialog.dismiss();
                activity.proDialog = null;
            }
            if (isPageRequest) {
                activity.questionPB.setVisibility(View.GONE);
            }
            activity.isRequestState = false;
        }

    }

}
