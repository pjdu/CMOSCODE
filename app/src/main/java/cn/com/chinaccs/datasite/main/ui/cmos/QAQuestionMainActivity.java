package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.function.FuncGetSystemNotice;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;

/**
 * @author Fddi
 */
public class QAQuestionMainActivity extends BaseActivity {
    private Context context;
    private Button btnSystem;
    private Button btnSpe;
    // private ImageButton personalCenter;
    private TextView txtSysNotice;
    private String sysNotice;
    private Button btnSysNotice;

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
            Intent toPersonal = new Intent(this, QAPersonalCenter.class);
            startActivity(toPersonal);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_qa_question_main);
        this.findViews();
        initToolbar(getResources().getString(R.string.question_content));
        this.getSystemNotice();
        btnSystem.setOnClickListener(ocl);
        btnSpe.setOnClickListener(ocl);
        btnSysNotice.setOnClickListener(ocl);
        // personalCenter.setOnClickListener(ocl);
    }

    private OnClickListener ocl = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent i = null;
            Bundle be = new Bundle();
            String questionTag = "";
            switch (v.getId()) {
                case R.id.btn_question_system:
                    questionTag = "sys_qt";
                    be.putString("questionTag", questionTag);
                    i = new Intent(context, QAQuestionActivity.class);
                    i.putExtras(be);
                    startActivity(i);
                    break;
                case R.id.btn_question_spe:
                    questionTag = "sys_spe";
                    be.putString("questionTag", questionTag);
                    i = new Intent(context, QAQuestionActivity.class);
                    i.putExtras(be);
                    startActivity(i);
                    break;
                case R.id.btn_sys_notice:
                    i = new Intent(context, SystemNoticeListActivity.class);
                    startActivity(i);
                    break;
               /* case R.id.question_personal_center:
                    i = new Intent(context, QAPersonalCenter.class);
                    startActivity(i);
                    break;*/
                default:
                    break;
            }
        }
    };

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

    private void getSystemNotice() {
        final ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_request));
        pd.show();
        String type = "last";
        String pagestart = "0";
        FuncGetSystemNotice fieldData = new FuncGetSystemNotice(context);
        OnGetDataFinishedListener glr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                pd.dismiss();
                if (output.equals("fail")) {
                    Toast.makeText(
                            context,
                            getResources().getString(
                                    R.string.common_not_network),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject resJson = new JSONObject(output);
                    String result = resJson.getString("result");
                    String msg = resJson.getString("msg");
                    if (result.equals("1")) {
                        JSONArray data = resJson.getJSONArray("data");
                        sysNotice = data.getJSONArray(0).getString(1);
                        txtSysNotice.setText(sysNotice);
                        return;
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        fieldData.getData(glr, type, pagestart);
    }

    private void findViews() {
        btnSystem = (Button) findViewById(R.id.btn_question_system);
        btnSpe = (Button) findViewById(R.id.btn_question_spe);
        txtSysNotice = (TextView) findViewById(R.id.text_sys_notice);
        // personalCenter = (ImageButton) findViewById(R.id.question_personal_center);
        btnSysNotice = (Button) findViewById(R.id.btn_sys_notice);
    }

}
