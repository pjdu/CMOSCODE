package cn.com.chinaccs.datasite.main.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.com.chinaccs.datasite.main.R;

public class AppCheckVersion {

	public JSONObject getAppVersionCheck(Context context, String url) {
		JSONObject json = new JSONObject();
		AppHttpConnection con = new AppHttpConnection(context, url);
		String res = con.getConnectionResult();
		try {
			if (res == "fail") {
				json.put(App.JSON_MAP_RESULT, false);
				json.put(
						App.JSON_MAP_MSG,
						context.getResources().getString(
								R.string.common_not_network));
			} else {
				JSONObject input = new JSONObject(res);
				String result = input.getString("result");
				String msg = input.getString("msg");
				if (!result.equals("-1")) {
					json.put(App.JSON_MAP_RESULT, true);
					JSONArray datas = input.getJSONArray("data");
					StringBuffer versionInfo = new StringBuffer(
							"软件有新版本，是否更新？\n");
					versionInfo.append("软件版本:").append(datas.getString(0));
					versionInfo.append("\n更新时间:").append(datas.getString(1));
					versionInfo.append("\n更新内容:").append(datas.getString(2));
					json.put(App.JSON_MAP_MSG, versionInfo.toString());// 最新版本简介
					json.put(App.JSON_MAP_DOWNLOADURL, datas.getString(3));// 更新地址
				} else {
					json.put(App.JSON_MAP_RESULT, false);
					json.put(App.JSON_MAP_MSG, msg);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(App.LOG_TAG, json.toString());
		return json;
	}

	/**
	 * 下载并安装apk
	 * 
	 * @param downLoadUrl
	 * @param apkName
	 */
	public void downLoadApk(final Context context, final String downLoadUrl,
							final String apkName) {
		final Handler hr = new Handler();
		final ProgressDialog pdBar = App.progressDialog(context, context
				.getResources().getString(R.string.common_request));
		pdBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//设置进度条点击屏幕其他地方不可取消
		pdBar.setCanceledOnTouchOutside(false);
		//设置按返回键不可取消
		pdBar.setCancelable(false);

		pdBar.show();
		String sdm = Environment.getExternalStorageState();
		Boolean tSd = false;
		if (sdm.equals(Environment.MEDIA_MOUNTED)) {
			tSd = true;
		}
		final Boolean isSd = tSd;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(downLoadUrl);
				HttpResponse resp;
				try {
					resp = client.execute(get);
					if (resp == null
							|| resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						get.abort();// 断开通信
						return;
					}
					HttpEntity entity = resp.getEntity();
					long length = entity.getContentLength();
					int maxLength = (int) (length / 1024);
					pdBar.setMax(maxLength);
					InputStream is = entity.getContent();
					File file = null;
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						if (isSd) {
							file = new File(Environment
									.getExternalStorageDirectory()
									.getAbsolutePath(), apkName);
							fileOutputStream = new FileOutputStream(file);
						} else {
							file = new File(context.getFilesDir(), apkName);
							fileOutputStream = context.openFileOutput(apkName,
									Context.MODE_WORLD_READABLE);
						}
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							// baos.write(buf, 0, ch);
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {
								if (count < 1024) {
									pdBar.setProgress(1);
								} else {
									int load = count / 1024;
									pdBar.setProgress(load);
								}
							}
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}

					// 安装apk;
					hr.post(new Runnable() {
						public void run() {
							pdBar.cancel();
							((Activity) context).finish();
							Intent intent = new Intent(Intent.ACTION_VIEW);
							File file = null;
							if (isSd) {
								file = new File(Environment
										.getExternalStorageDirectory()
										.getAbsolutePath(), apkName);
							} else {
								file = new File(context.getFilesDir(), apkName);
							}
							intent.setDataAndType(Uri.fromFile(file),
									"application/vnd.android.package-archive");
							context.startActivity(intent);
						}
					});
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
}
