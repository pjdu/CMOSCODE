package cn.com.chinaccs.datasite.main.common;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http连接
 *
 * @author fddi
 */
public class AppHttpConnection {
    public static final String RESULT_FAIL = "fail";
    private String targetUrl;
    private Context context;

    public AppHttpConnection(Context context, String targetUrl) {
        this.targetUrl = targetUrl;
        this.context = context;
    }

    public String getConnectionResult() {
        URL url = null;
        StringBuffer strBr = null;
        String httpResult = AppHttpConnection.RESULT_FAIL;
        try {
            url = new URL(targetUrl);
            if (url != null && AppNetWork.isNetWork(context)) {
                HttpURLConnection urlCon = (HttpURLConnection) url
                        .openConnection(); // 实例化一个通信；
                urlCon.setRequestMethod("POST");
                urlCon.setConnectTimeout(60000);
                urlCon.setReadTimeout(60000);
                urlCon.connect(); // 打开通信；
                InputStream input = urlCon.getInputStream();
                if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    urlCon.disconnect();
                    httpResult = AppHttpConnection.RESULT_FAIL;
                } else {
                    InputStreamReader inputReader = new InputStreamReader(
                            input, App.ENCODE_UTF8); // 读取数据；
                    BufferedReader reader = new BufferedReader(inputReader);
                    String inputLine = null;
                    strBr = new StringBuffer();
                    while ((inputLine = reader.readLine()) != null) {
                        strBr.append(inputLine).append("\n");
                    }
                    reader.close();
                    inputReader.close();
                    input.close();
                    urlCon.disconnect();
                    httpResult = strBr.toString();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return httpResult;
    }
}
