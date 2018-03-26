package cn.com.chinaccs.datasite.main.datasite.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCDMABaseStation;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.ui.MainActivity;
import cn.com.chinaccs.datasite.main.ui.MainApp;

/**
 * Created by andyhua on 15-8-13.
 */
public class FuncSubSecurityData {
    private Context context;
    private PopupWindow pop;
    private View popState;
    private ProgressBar pb;
    private TextView textState;
    private Button btnClose;
    private Button btnHome;
    private String ci;
    private static final String DATA_TYPE = "101";

    public FuncSubSecurityData(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void subData(String desc, String poDesc, String[] imgPaths) {
        // TODO Auto-generated method stub
        popStateFindViews();
        pop = new PopupWindow(popState, ViewPager.LayoutParams.MATCH_PARENT,
                ViewPager.LayoutParams.MATCH_PARENT);
        pop.setBackgroundDrawable(context.getResources().getDrawable(
                R.drawable.draw_bg_opactiy));
        pop.setFocusable(true);
        // pop.setAnimationStyle(R.style.style_popupAnim);
        pop.showAtLocation(((Activity) context).findViewById(R.id.main),
                Gravity.CENTER, 0, 0);
        pop.update();
        this.subExcute(desc, poDesc, imgPaths);
    }

    private void subExcute(final String desc, final String poDesc,
                           final String[] imgPaths) {
        // TODO Auto-generated method stub
        final StringBuffer sr = new StringBuffer("提交巡检数据中...");
        textState.setText(sr.toString());
        pb.setProgress(1);
        final JSONObject json = new JSONObject();
        SharedPreferences share = context.getSharedPreferences(
                App.SHARE_TAG, 0);
        String userid = share.getString(
                AppCheckLogin.SHARE_USER_ID, "");
        try {
            json.put("mobileUserId", userid);
//            json.put("desc", desc);
//            json.put("taskId", taskId);
            final ICHandler hr = new ICHandler(pb, textState, btnClose, btnHome);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Message m;
                    m = hr.obtainMessage(2, "获取基站信息....");
                    hr.sendMessage(m);
                    ci = AppCDMABaseStation.getCi(context);
//                    BDLocation loc = getLocation();
//                    if (loc == null) {
//                        m = hr.obtainMessage(501, "定位失败！请开启GPS和无线网络后重试");
//                        hr.sendMessage(m);
//                        return;
//                    }
//                    String longitude = String.valueOf(loc.getLongitude());
//                    String latitude = String.valueOf(loc.getLatitude());
//                    String locDesc = loc.getAddrStr();
//                    if (locDesc == null || locDesc.equals("")) {
//                        locDesc = "GPS定位";
//                    }
//                    SharedPreferences share = context.getSharedPreferences(
//                            App.SHARE_TAG, 0);
//                    String userid = share.getString(
//                            AppCheckLogin.SHARE_USER_ID, "");
                    try {
//                        json.put("userid", userid);
//                        json.put("longitude", longitude);
//                        json.put("latitude", latitude);
                        json.put("bsId", ci);
                        json.put("potentialContent", desc);
                        json.put("potentialDesc", poDesc);
                        Log.d(App.LOG_TAG, "-------------------" + "CI : " + ci + "------------------");
                        m = hr.obtainMessage(3, "獲取基站成功: CI = " + ci);
                        hr.sendMessage(m);
                        m = hr.obtainMessage(4, "上传数据中...");
                        hr.sendMessage(m);
                        String subRes = submitToServer(json);
                        if (subRes.equals(AppHttpClient.RESULT_FAIL)) {
                            m = hr.obtainMessage(501, "上传数据失败！请检查手机网络是否有效");
                            hr.sendMessage(m);
                            return;
                        }
                        JSONObject jr = new JSONObject(subRes);
                        if (("-1").equals(jr.getString("result"))) {
                            m = hr.obtainMessage(501, jr.getString("msg"));
                            hr.sendMessage(m);
                            return;
                        }
                        m = hr.obtainMessage(5, "上传成功");
                        hr.sendMessage(m);
                        m = hr.obtainMessage(6, "上传图片中....");
                        hr.sendMessage(m);
                        String infoid = jr.getString("infoid");
                        if (imgPaths == null) {
                            m = hr.obtainMessage(200, "恭喜，提交隐患点数据成功！");
                            hr.sendMessage(m);
                            return;
                        }
                        FuncUploadFile ff = new FuncUploadFile(context);
                        for (int i = 0; i < imgPaths.length; i++) {
                            if (imgPaths[i] != null) {
                                String fileDesc = "隐患点拍照";
//                                if (null != taskId && !taskId.equals(""))
//                                    fileDesc = "巡检任务-" + taskId + "中的隐患点";
                                String res = ff.uploadFileToServer(imgPaths[i],
                                        "1", fileDesc, DATA_TYPE, "", infoid,
                                        DataSiteStart.HTTP_SERVER_URL,
                                        DataSiteStart.HTTP_KEYSTORE);
                                if (res.equals(AppHttpConnection.RESULT_FAIL)) {
                                    m = hr.obtainMessage(201, "上传图片："
                                            + imgPaths[i] + "--失败");
                                    hr.sendMessage(m);
                                } else {
                                    JSONObject imgjr = new JSONObject(res);
                                    m = hr.obtainMessage(201,
                                            "上传图片：" + imgPaths[i] + "--"
                                                    + imgjr.getString("msg"));
                                    hr.sendMessage(m);
                                    String resCode = imgjr.getString("result");
                                    if (resCode.equals("1")
                                            && imgPaths[i] != null) {
                                        File file = new File(imgPaths[i]);
                                        file.delete();// 删除临时文件
                                    }
                                }
                            }
                        }
                        m = hr.obtainMessage(200, "恭喜，提交隐患点数据成功！");
                        hr.sendMessage(m);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String submitToServer(JSONObject json) {
        Log.i(App.LOG_TAG, json.toString());
        String result = AppHttpClient.RESULT_FAIL;
        StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
        url.append("TInspectPotential.do");
        AppHttpClient conn = new AppHttpClient(context);
        try {
            String str = URLEncoder.encode(json.toString(), App.ENCODE_UTF8);
            // StringBuffer str = new StringBuffer();
            Log.i(App.LOG_TAG, str);
//            try {
//                str.append("mobileUserId=" + json.getString("mobileUserId"));
//                str.append("&bsId=" + json.getString("bsId"));
//                str.append("&potentialContent=" + URLEncoder.encode(json.getString("potentialContent"), App.ENCODE_UTF8));
//                str.append("&potentialDesc=" + URLEncoder.encode(json.getString("potentialDesc"), App.ENCODE_UTF8));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.i(App.LOG_TAG, str.toString());
            result = conn.getResultByPOST(url.toString(), str);
            Log.i(App.LOG_TAG, result);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private BDLocation getLocation() {
        if (MainApp.geoBD != null && MainApp.geoBD.locClient != null
                && MainApp.geoBD.locClient.isStarted()) {
            MainApp.geoBD.locClient.requestLocation();
            BDLocation loc = MainApp.geoBD.location;
            return loc;
        } else {
            return null;
        }
    }

    private void popStateFindViews() {
        LayoutInflater li = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popState = li.inflate(R.layout.pop_updata_state, null);
        pb = (ProgressBar) popState.findViewById(R.id.pb_state);
        textState = (TextView) popState.findViewById(R.id.text_state);
        btnClose = (Button) popState.findViewById(R.id.btn_pop_close);
        btnHome = (Button) popState.findViewById(R.id.btn_pop_home);
        pb.setMax(5);
        btnClose.setEnabled(false);
        btnHome.setEnabled(false);
        View.OnClickListener lr = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.btn_pop_close:
                        if (pop != null)
                            pop.dismiss();
                        break;
                    case R.id.btn_pop_home:
                        ((Activity) context).finish();
                        context.startActivity(new Intent(context,
                                MainActivity.class));
                        break;
                }
            }
        };
        btnClose.setOnClickListener(lr);
        btnHome.setOnClickListener(lr);
    }

    /**
     * @author Fddi
     */
    static class ICHandler extends Handler {
        private ProgressBar pb;
        private TextView textState;
        private Button btnClose, btnHome;

        public ICHandler(ProgressBar pb, TextView textState, Button btnClose,
                         Button btnHome) {
            this.pb = pb;
            this.textState = textState;
            this.btnClose = btnClose;
            this.btnHome = btnHome;
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            String reslutInfo = (String) msg.obj;
            Log.d(App.LOG_TAG, reslutInfo);
            switch (msg.what) {
                case 200:
                    pb.setProgress(7);
                    textState.append("\n");
                    textState.append(reslutInfo);
                    btnClose.setEnabled(true);
                    btnHome.setEnabled(true);
                    break;
                case 201:
                    textState.append("\n");
                    textState.append(reslutInfo);
                    break;
                case 501:
                    pb.setProgress(7);
                    textState.append("\n");
                    textState.append(reslutInfo);
                    btnClose.setEnabled(true);
                    btnHome.setEnabled(true);
                    break;
                default:
                    textState.append("\n");
                    textState.append(reslutInfo);
                    pb.setProgress(msg.what);
                    break;
            }
        }
    }

}
