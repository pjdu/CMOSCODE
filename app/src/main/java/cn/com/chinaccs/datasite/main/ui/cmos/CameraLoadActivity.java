package cn.com.chinaccs.datasite.main.ui.cmos;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ProgressBar;

import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.ui.MainApp;


/**
 * @author Fddi
 * 
 */
public class CameraLoadActivity extends Activity {
	private Context context;
	private static final int RESULT_CAMERA = 1;
	private static final String fileDir = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/temp_cw/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		ProgressBar pb = new ProgressBar(context);
		setContentView(pb);
		MainApp.app.imagePath = null;
		try {
			File file = new File(fileDir);
			if (!file.exists()) {
				file.mkdirs();
			}
			String path = "temp_" + new Date().getTime() + ".jpg";
			file = new File(fileDir, path);
			Log.d(App.LOG_TAG, file.getPath());
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}
			MainApp.app.imagePath = fileDir + path;
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivityForResult(i, RESULT_CAMERA);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_CAMERA) {
			if (MainApp.app.imagePath != null) {
				File file = new File(MainApp.app.imagePath);
				if (file != null && file.exists() && file.length() > 0) {
					resultCode = RESULT_OK;
				} else {
					resultCode = RESULT_CANCELED;
				}
			} else {
				resultCode = RESULT_CANCELED;
			}
			setResult(resultCode);
			finish();
		}
	}

}
