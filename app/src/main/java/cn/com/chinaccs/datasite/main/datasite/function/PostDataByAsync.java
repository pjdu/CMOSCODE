package cn.com.chinaccs.datasite.main.datasite.function;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.datasite.listener.ProgressListener;
import cn.com.chinaccs.datasite.main.datasite.utils.ProgressOutHttpEntity;


/**
 * Created by AndyHua on 15/8/13.
 */
public class PostDataByAsync extends AsyncTask<String, Integer, String> {
    private final Handler hr = new Handler();
    /**
     * 服务器路径*
     */
    private String url;
    /**
     * 上传的参数*
     */
    private Map<String, String> paramMap;
    /**
     * 要上传的文件*
     */
    private ArrayList<File> files;
    private long totalSize;
    private Context context;
    private ProgressDialog progressDialog;
    private OnGetDataFinishedListener lr;

    public PostDataByAsync(Context context, OnGetDataFinishedListener lr, String url, Map<String, String> paramMap, ArrayList<File> files) {
        this.context = context;
        this.url = url;
        this.paramMap = paramMap;
        this.files = files;
        this.lr = lr;
    }

    @Override
    protected void onPreExecute() {//执行前的初始化
        // TODO Auto-generated method stub
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("请稍等...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {//执行任务
        // TODO Auto-generated method stub
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName(HTTP.UTF_8));//设置请求的编码格式
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
        int count = 0;
        for (File file : files) {
//			FileBody fileBody = new FileBody(file);//把文件转换成流对象FileBody
//			builder.addPart("file"+count, fileBody);
            builder.addBinaryBody("file" + count, file);
            count++;
        }
        Log.i(App.LOG_TAG, " mobileUserId : " + paramMap.get("mobileUserId"));
        Log.i(App.LOG_TAG, " bsId : " + paramMap.get("bsId"));
        builder.addTextBody("mobileUserId", paramMap.get("mobileUserId"), ContentType.create("text/plain", Consts.UTF_8));//设置请求参数
        builder.addTextBody("bsId", paramMap.get("bsId"), ContentType.create("text/plain", Consts.UTF_8));//设置请求参数
        builder.addTextBody("potentialContent", paramMap.get("potentialContent"), ContentType.create("text/plain", Consts.UTF_8));//设置请求参数
        builder.addTextBody("potentialDesc", paramMap.get("potentialDesc"), ContentType.create("text/plain", Consts.UTF_8));//设置请求参数

        Log.i(App.LOG_TAG, "builder : " + builder.toString());
        HttpEntity entity = builder.build();// 生成 HTTP POST 实体
        totalSize = entity.getContentLength();//获取上传文件的大小
        ProgressOutHttpEntity progressHttpEntity = new ProgressOutHttpEntity(
                entity, new ProgressListener() {
            @Override
            public void transferred(long transferedBytes) {
                publishProgress((int) (100 * transferedBytes / totalSize));//更新进度
            }
        });
        return  uploadFile(url, progressHttpEntity);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {//执行进度
        // TODO Auto-generated method stub
        Log.i("info", "values:" + values[0]);
        progressDialog.setProgress((int) values[0]);//更新进度条
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {//执行结果
        // TODO Auto-generated method stub
        Log.i("info", result);
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        super.onPostExecute(result);
    }

    /**
     * 向服务器上传文件
     *
     * @param url
     * @param entity
     * @return
     */
    public String uploadFile(String url, ProgressOutHttpEntity entity) {
        HttpClient httpClient = new DefaultHttpClient();// 开启一个客户端 HTTP 请求
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);// 设置连接超时时间
        HttpPost httpPost = new HttpPost(url);//创建 HTTP POST 请求
        httpPost.setEntity(entity);
        String msg = "";
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.i(App.LOG_TAG, "code = " + httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                final String conResult = EntityUtils.toString(resEntity, "UTF-8");
                if (conResult.equals(AppHttpClient.RESULT_FAIL)) {

                } else {
                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(conResult);
                        String result = jsonRes.getString("result");
                        msg = jsonRes.getString("msg");
                        if (result.equals("-1")) {
                            msg += ",请添加图片!";
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                Log.i(App.LOG_TAG, conResult);
                hr.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        lr.onFinished(conResult);
                    }
                });
            }
            return msg;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null && httpClient.getConnectionManager() != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
        return "文件上传失败";
    }
}
