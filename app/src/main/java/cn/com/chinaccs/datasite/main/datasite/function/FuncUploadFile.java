package cn.com.chinaccs.datasite.main.datasite.function;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppNetWork;


/**
 * @author Fddi
 * 
 */
public class FuncUploadFile {
	private Context context;

	public FuncUploadFile(Context context) {
		this.context = context;
	}

	public String uploadFileToServer(String filePath, String fileType,
			String fileDes, String dataType, String dataSubtype, String dataId,
			String ServerUrl, String keyStore) {
		String result = "fail";
		if (filePath == null || filePath.equals("")) {
			return result;
		}
		SharedPreferences share = context
				.getSharedPreferences(App.SHARE_TAG, 0);
		String userId = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		String[] temp = filePath.replaceAll("\\\\", "/").split("/");
		String tempName = "";
		if (temp != null && temp.length > 1) {
			tempName = temp[temp.length - 1];
		}
		String suffix = ".";
		String[] temps = tempName.split("\\.");
		if (temps != null && temps.length > 1) {
			suffix += temps[temps.length - 1];
		}
		Date date = new Date();
		String fileName = "file_" + dataType + "_" + dataId + "_"
				+ date.getTime() + suffix;
		String fileUrl = ServerUrl + "ds_file/" + fileName;
		String sign = App.signMD5(keyStore + userId + fileName + fileUrl
				+ fileType + fileDes + dataType + dataSubtype + dataId);
		try {
			fileName = URLEncoder.encode(fileName, App.ENCODE_UTF8);
			fileDes = URLEncoder.encode(fileDes, App.ENCODE_UTF8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String urlStr = ServerUrl + "FileUpload.do?userid=" + userId
				+ "&filename=" + fileName + "&fileurl=" + fileUrl
				+ "&filetype=" + fileType + "&filedes=" + fileDes + "&dstype="
				+ dataType + "&subtype=" + dataSubtype + "&dsid=" + dataId
				+ "&sign=" + sign;
		try {
			if (!AppNetWork.isNetWork(context)) {
				return result;
			}
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			DataOutputStream dateInput = new DataOutputStream(
					con.getOutputStream());
			File file = new File(filePath);
			if (file == null || !file.exists()) {
				return result;
			}
			FileInputStream fileInput = new FileInputStream(file);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			while ((length = fileInput.read(buffer)) != -1) {
				dateInput.write(buffer, 0, length);
			}
			dateInput.close();
			dateInput.flush();
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			dateInput.close();
			result = b.toString();
			result = URLDecoder.decode(result, App.ENCODE_UTF8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
