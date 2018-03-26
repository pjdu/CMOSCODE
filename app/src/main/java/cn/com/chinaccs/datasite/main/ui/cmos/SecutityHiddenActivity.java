package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.datasite.function.CompressImage;
import cn.com.chinaccs.datasite.main.datasite.function.FuncServerInset;
import cn.com.chinaccs.datasite.main.datasite.function.PostDataByAsync;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.ui.MainApp;

/**
 * Created by AndyHua on 15-8-13.
 */
public class SecutityHiddenActivity extends BaseActivity {
    private static final String TAG = SecutityHiddenActivity.class.getSimpleName();

    private Context context;
    private EditText etDes;
    private EditText etPotentialDesc;
    private ImageButton[] btnImgs;
    private Button btnSub;
    private TextView tvBs;
    private String ci;
    private String sid;
    private String iBsId;
    private String iBsName;
    private String iBsLongitude;
    private String iBsLatitude;
    private String iCellId;
    private String iCellName;
    private String iCellLongitude;
    private String iCellLatitude;
    private String bsBtsId;
    private String bsBsc;
    private String isSingleRru;
    private String[] imgPaths;
    private static final int[] RESULT_CAMERAS = {101, 102, 103};
    private static final int[] RESULT_CAMERA_LOCALS = {201, 202, 203};
    private SharedPreferences share;
    private String bsName;  //如果是从作业计划点过来的，标题应该显示相应的基站名称，而不是直接显示当前接入基站
    private String bsId;
    Bundle be;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.menu_map){
            Intent i = new Intent(context, BoxMapActivity.class);
            startActivity(i);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secutity_hidden);
        initToolbar("巡检隐患");
        context = SecutityHiddenActivity.this;
        be= getIntent().getExtras();
        if (be != null) {
        bsName = be.getString("bsName");
            bsId = be.getString("bsId");
        }
        findViews();
        share = getSharedPreferences(App.SHARE_TAG, 0);
        imgPaths = new String[3];
        if (btnImgs != null) {
            for (int i = 0; i < btnImgs.length; i++) {
                btnImgs[i].setOnClickListener(imglr);
            }
        }
        btnSub.setOnClickListener(clr);
        ci = AppCDMABaseStation.getCi(context);
        sid = AppCDMABaseStation.getCDMASid(context);
        this.buildInsertBs();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        btnImgs = null;
        imgPaths = null;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Uri uri = null;
        if (data != null) {
            uri = data.getData();
        }
        Log.d(App.LOG_TAG, "resultCode" + resultCode);
        boolean isRequest = false;
        for (int i = 0; i < RESULT_CAMERAS.length; i++) {
            if (requestCode == RESULT_CAMERAS[i]) {
                if (MainApp.app.imagePath != null && resultCode == RESULT_OK) {
                    imgPaths[i] = MainApp.app.imagePath;
                    addContentByFile(i);
                } else {
                    toast("图片选取失败！");
                }
                isRequest = true;
                break;
            }
        }
        if (!isRequest) {
            for (int i = 0; i < RESULT_CAMERAS.length; i++) {
                if (requestCode == RESULT_CAMERA_LOCALS[i]) {
                    if (resultCode == RESULT_OK) {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uri,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor
                                .getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        imgPaths[i] = picturePath;
                        Log.i(App.LOG_TAG, "imgPaths : " + i + " " + imgPaths[i]);
                        addContentByFile(i);
                    } else {
                        toast("图片选取失败！");
                    }
                    isRequest = true;
                    break;
                }
            }
        }
    }

    private View.OnClickListener clr = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_close:
//                    AlertDialog ad = App.alertDialog(context, "提示！", "确定提交数据？",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    // TODO Auto-generated method stub
//                                    FuncSubSecurityData func = new FuncSubSecurityData(
//                                            context);
//                                    String desc = etDes.getText().toString();
//                                    String poDesc = etPotentialDesc.getText().toString();
//                                    func.subData(desc, poDesc, imgPaths);
//                                }
//                            }, null);
//                    ad.show();
                    postData();
                    break;

                default:
                    break;
            }
        }
    };

    private void buildInsertBs() {
        final ProgressDialog pd = App.progressDialog(context, getResources()
                .getString(R.string.common_request));
        pd.show();
        FuncServerInset func = new FuncServerInset(context);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                Log.i(App.LOG_TAG, output);
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
                    JSONObject json = new JSONObject(output);
                    String result = json.getString("result");
                    String msg = json.getString("msg");
                    if (result.equals("1")) {
                        JSONArray abs = json.getJSONArray("bs");
                        iBsId = abs.getString(0);
                        iBsName = abs.getString(1);
                        tvBs.setText("当前基站：");
                        if (bsName != null) {
                            iBsName=bsName;
                        }
                        if (bsId != null) {
                            iBsId=bsId;
                        }
                            tvBs.append(iBsName);

                        iBsLongitude = abs.getString(4);
                        iBsLatitude = abs.getString(5);
                        JSONArray cellOption = json.getJSONArray("cell");
                        bsBtsId = cellOption.getString(2);
                        bsBsc = cellOption.getString(3);
                        iCellId = cellOption.getString(0);
                        iCellName = cellOption.getString(1);
                        iCellLongitude = cellOption.getString(4);
                        iCellLatitude = cellOption.getString(5);
                        isSingleRru = cellOption.getString(6);
                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        Log.i(App.LOG_TAG, "ci : " + ci + "----------------" + "sid : " + sid);
        func.insetQuery(lr, ci, sid);
    }


    private void postData() {
        if (iBsId == null) {
            Toast.makeText(SecutityHiddenActivity.this, "获取基站失败，提交功能不能用", Toast.LENGTH_SHORT).show();
            return;
        }
        String desc = etDes.getText().toString();
        if (desc.equals("")) {
            Toast.makeText(SecutityHiddenActivity.this, "请输入隐患描述....", Toast.LENGTH_SHORT).show();
            return;
        }
        String poDesc = etPotentialDesc.getText().toString();
        if (poDesc.equals("")) {
            Toast.makeText(SecutityHiddenActivity.this, "请输入隐患备注....", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<File> files = new ArrayList<File>();
        Log.i(TAG, "imgPaths length = " + imgPaths.length);
        for (int j = 0; j < imgPaths.length; ++j) {
            String path = imgPaths[j];
            if (path != null) {
                Log.i(App.LOG_TAG, path);
                files.add(new File(path));
            }
        }
        if (files.size() <= 0) {
            Toast.makeText(SecutityHiddenActivity.this, "请输添加图片....", Toast.LENGTH_SHORT).show();
            return;
        }
        // PostFourGFindGoodData post = new PostFourGFindGoodData(this);
        OnGetDataFinishedListener lr = new OnGetDataFinishedListener() {

            @Override
            public void onFinished(String output) {
                // TODO Auto-generated method stub
                Log.i(App.LOG_TAG, output.toString());
                boolean isSuccess = false;
                String msg = null;
                if (output.equals(AppHttpClient.RESULT_FAIL)) {
                    isSuccess = false;
                } else {
                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(output);
                        String result = jsonRes.getString("result");
                        msg = jsonRes.getString("msg");
                        if (result.equals("-1")) {
                            isSuccess = false;
                        } else {
                            isSuccess = true;
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        isSuccess = false;
                    }
                }
                Toast.makeText(SecutityHiddenActivity.this, msg, Toast.LENGTH_SHORT);
                Log.e("SecutityHiddenActivity", "msg=" + msg);
                if (!isSuccess) {
                } else {
                    finish();
                }
            }
        };
        String url = DataSiteStart.HTTP_SERVER_URL + "TInspectPotential.do";
        Map<String, String> paraMap = new Hashtable<String, String>();
        SharedPreferences share = context.getSharedPreferences(
                App.SHARE_TAG, 0);
        String userid = share.getString(
                AppCheckLogin.SHARE_USER_ID, "");
        paraMap.put("mobileUserId", userid);
        paraMap.put("bsId", iBsId);
        paraMap.put("potentialContent", desc);
        paraMap.put("potentialDesc", poDesc);

        PostDataByAsync postDataByAsync = new PostDataByAsync(this, lr, url, paraMap, files);
        postDataByAsync.execute();
       /* SubmitFourGFindGoodInfo info = new SubmitFourGFindGoodInfo(userName, userId,
                scene, item.testTime, item.speed, lon, lat, item.addr);
        post.postData(lr, url, info, files);*/
    }


    private View.OnClickListener imglr = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                toast("抱歉，您的手机存储不可用！");
                return;
            }
            for (int i = 0; i < btnImgs.length; i++) {
                if (v.equals(btnImgs[i])) {
                    getImg(i);
                    break;
                }
            }
        }
    };

    private void getImg(final int codeIndex) {
        String[] imgItems = {"开始拍照", "相册选取"};
        new AlertDialog.Builder(context).setTitle("上传图片")
                .setItems(imgItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                try {
                                    Intent i = new Intent(context,
                                            CameraLoadActivity.class);
                                    startActivityForResult(i,
                                            RESULT_CAMERAS[codeIndex]);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                                break;
                            case 1:
                                startActivityForResult(new Intent(
                                                Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                                        RESULT_CAMERA_LOCALS[codeIndex]);
                                dialog.dismiss();
                                break;
                        }
                    }
                }).show();
    }

    private void toast(String info) {
        Toast.makeText(context, info, Toast.LENGTH_LONG).show();
    }

    private void addContentByFile(final int codeIndex) {
        if (imgPaths[codeIndex] == null || ("").equals(imgPaths[codeIndex])) {
            Log.d(App.LOG_TAG, "选择图片失败-------" + codeIndex);
        } else {
            File file = new File(imgPaths[codeIndex]);
            if (file != null && file.exists() && file.length() > 0) {
                CompressImage ci = new CompressImage();
                Bitmap bm = ci.setCompress(file, 600, 800, 300);
                BitmapDrawable bd = new BitmapDrawable(bm);
                btnImgs[codeIndex].setBackgroundDrawable(bd);
                bm = null;
            } else {
                Log.d(App.LOG_TAG, "选择图片失败-------" + codeIndex);
            }
        }
    }

    private void findViews() {
        // TODO Auto-generated method stub
        // tvInsState = (TextView) findViewById(R.id.tv_inspectstate);
        etDes = (EditText) findViewById(R.id.et_itdesc);
        etPotentialDesc = (EditText) findViewById(R.id.et_potentialDesc);
        btnImgs = new ImageButton[3];
        btnImgs[0] = (ImageButton) findViewById(R.id.img_it_add_a1);
        btnImgs[1] = (ImageButton) findViewById(R.id.img_it_add_a2);
        btnImgs[2] = (ImageButton) findViewById(R.id.img_it_add_a3);
        btnSub = (Button) findViewById(R.id.btn_close);
        tvBs = (TextView) findViewById(R.id.tv_bsname);
        //btnIns = (Button) findViewById(R.id.btn_map);
    }

}
