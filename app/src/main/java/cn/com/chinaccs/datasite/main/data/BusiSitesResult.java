package cn.com.chinaccs.datasite.main.data;

import android.os.AsyncTask;
import android.widget.TextView;

public class BusiSitesResult extends AsyncTask<String, Void, Void> {
	StringBuffer sr;
	Process p;
	TextView tv;
	String str;
	public boolean isFinish = false;

	public BusiSitesResult(TextView tv, String s) {
		this.tv = tv;
		this.str = s;
	}

	public void stop() {
		if (p != null) {
			p.destroy();
			p = null;
		}
		cancel(true);
	}

	@Override
	protected Void doInBackground(String... params) {
//		tv.setText(str);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		tv.setText(str);
		isFinish = true;
	}

}