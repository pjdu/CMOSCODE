package cn.com.chinaccs.datasite.main.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.util.Log;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import cn.com.chinaccs.datasite.main.CoConfig;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.common.AppNetWork;


public class UploadUtil {
	Context context;
	public long length = 0;
	public float avgspeed = 0;

	public UploadUtil(Context context) {
		this.context = context;
	}

	public void uploadFile(String type) {
		try {
			InputStream in = context.getClass().getClassLoader().getResourceAsStream("assets/"+"speedUpLoad.file");
			if (type.equals("file_speed_3G")) {
				in = context.getClass().getClassLoader().getResourceAsStream("assets/"+"speedUpLoad3g.file");
			}
			length = in.available() * 8 / 1024;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024 * 256];
			int readed = -1;
			while ((readed = in.read(bytes)) != -1) {
				out.write(bytes, 0, readed);
			}
			if (!AppNetWork.isNetWork(context)) {
				avgspeed = -1;
				in.close();
				out.close();
				return;

			}

			AppHttpClient ac = new AppHttpClient(context);
			ac.buildHttpClient();
			HttpClient client = ac.getClient();
			HttpPost post = new HttpPost(CoConfig.HTTP_SERVER_URL
					+ "SpeedTest.do");
			ByteArrayEntity be = new ByteArrayEntity(out.toByteArray());
			post.setEntity(be);
			long first = new Date().getTime();
			HttpResponse resp = client.execute(post);
			if (resp == null
					|| resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				post.abort();// 断开通信
				avgspeed = -1;
				in.close();
				out.close();
				return;
			}
			HttpEntity entity = resp.getEntity();

			InputStreamReader inputReader = new InputStreamReader(
					entity.getContent(), AppScaffolding.ENCODE_UTF8); // 读取数据；
			BufferedReader reader = new BufferedReader(inputReader);
			String inputLine = null;
			StringBuffer strBr = new StringBuffer();
			while ((inputLine = reader.readLine()) != null) {
				strBr.append(inputLine);
			}
			JSONObject json = new JSONObject(strBr.toString());
			Log.d(CoConfig.LOG_TAG, json.toString());
			if (!json.getString("result").equals("-1")) {
				int usedTime = (int) (new Date().getTime() - first);
				Log.i(CoConfig.LOG_TAG, "usedTime = " + usedTime);
				int size = Integer.parseInt(json.getString("data"));
				if ((usedTime / 1000) == 0) {
					avgspeed = (size / 1024) * 8;
				} else {
					avgspeed = (((float)size / 1024.0f) / ((float)usedTime / 1000.0f)) * 8.0f;
				}
				Log.d(CoConfig.LOG_TAG, size+"------"+usedTime+"------"+avgspeed);
			} else {
				avgspeed = -1;
			}
			in.close();
			out.close();
			Log.d("SpeedActivity","uploadFile执行完毕");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			avgspeed = -1;
			Log.d("SpeedActivity","uploadFile出错");
			Log.d("SpeedActivity", String.valueOf(e));
		}
	}

	public float getUidTxBytes() { // 获取总的上行字节数，包含Mobile和WiFi等
		PackageManager pm = ((Activity) context).getPackageManager();
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.i(CoConfig.LOG_TAG, "UidTxBytes : " + (TrafficStats.getUidTxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0
				: (TrafficStats.getTotalTxBytes() * 8 / 1024)));
		return TrafficStats.getUidTxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0
				: (TrafficStats.getTotalTxBytes() * 8 / 1024);
	}

}
