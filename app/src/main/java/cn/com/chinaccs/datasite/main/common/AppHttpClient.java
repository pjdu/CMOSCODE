package cn.com.chinaccs.datasite.main.common;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author fddi
 * 
 */
public class AppHttpClient {
	public static final String RESULT_FAIL = "fail";
	private HttpClient client;
	private Context context;

	public AppHttpClient(Context context) {
		this.context = context;
	}

	public String getResultByGET(String url) {
		StringBuffer strBr = new StringBuffer(AppHttpClient.RESULT_FAIL);
		try {
			if (!AppNetWork.isNetWork(context)) {
				return strBr.toString();
			}
			HttpGet get = new HttpGet(url);
			this.buildHttpClient();
			HttpResponse resp = client.execute(get);
			if (resp == null
					|| resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				get.abort();// 断开通信
				strBr = new StringBuffer(AppHttpClient.RESULT_FAIL);
				return strBr.toString();
			}
			HttpEntity entity = resp.getEntity();
			InputStreamReader inputReader = new InputStreamReader(
					entity.getContent(), App.ENCODE_UTF8); // 读取数据；
			BufferedReader reader = new BufferedReader(inputReader);
			String inputLine = null;
			strBr = new StringBuffer();
			while ((inputLine = reader.readLine()) != null) {
				strBr.append(inputLine).append("\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return strBr.toString();
	}

	public String getResultByPOST(String url, String output) {
		StringBuffer strBr = new StringBuffer(AppHttpClient.RESULT_FAIL);
		try {
			if (!AppNetWork.isNetWork(context)) {
				return strBr.toString();
			}
			HttpPost post = new HttpPost(url);
			StringEntity se = new StringEntity(output);
			post.setEntity(se);
			this.buildHttpClient();
			HttpResponse resp = client.execute(post);
			if (resp == null
					|| resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				post.abort();// 断开通信
				strBr = new StringBuffer(AppHttpClient.RESULT_FAIL);
				return strBr.toString();
			}
			HttpEntity entity = resp.getEntity();
			InputStreamReader inputReader = new InputStreamReader(
					entity.getContent(), App.ENCODE_UTF8); // 读取数据；
			BufferedReader reader = new BufferedReader(inputReader);
			String inputLine = null;
			strBr = new StringBuffer();
			while ((inputLine = reader.readLine()) != null) {
				strBr.append(inputLine).append("\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return strBr.toString();
	}

	public void buildHttpClient() {
		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
		HttpConnectionParams.setSoTimeout(params, 10 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		// 设置重定向，缺省为true
		HttpClientParams.setRedirecting(params, true);
		client = new DefaultHttpClient(params);
	}

	public HttpClient getClient() {
		return client;
	}

}
