package cn.com.chinaccs.datasite.main.data;

import android.os.AsyncTask;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.com.chinaccs.datasite.main.nettools.NetModel;


public class DoPingTask extends AsyncTask<String, Void, Void> {
	StringBuffer sr;
	Process p;
	TextView tv;
	RatingBar rbState;
	public boolean isFinish = false;
	public JSONObject pingResult;
	public String lastLine;
	public String ip;
	public float loss;
	public float delay;

	public DoPingTask(TextView tv, JSONObject json, RatingBar rbState) {
		this.tv = tv;
		this.pingResult = json;
		this.rbState = rbState;
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
		try {
			String[] command = { "/system/bin/ping", "-c", params[1], params[0] };
			p = new ProcessBuilder().command(command).redirectErrorStream(true)
					.start();
			try {
				InputStream in = p.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				sr = new StringBuffer();
				String s = null;
				
				while ((s = br.readLine()) != null) {
					sr.append(s + "\n");
					lastLine = s;
					publishProgress();
				}
				in.close();
			} finally {
				p.destroy();
				p = null;
			}
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		try {
			rbState.setVisibility(View.GONE);
			if (tv != null) {
				tv.setText(sr.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		String str = lastLine.split(" ")[4];
		String timeStr = lastLine.split(" ")[3];
		String[] time = timeStr.split("/");
		JSONObject ping = new JSONObject();
		try {
			ping.put("ping_min", time[0] + str);
			ping.put("ping_avg", time[1] + str);
			ping.put("ping_max", time[2] + str);
			ping.put("ping_mdev", time[3] + str);
			pingResult.put("ping", ping);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Double mdev = Double.valueOf(time[3]);
		float level = 0;
		if (mdev < 50) {
			level = 5;
		} else if (mdev >= 50 && mdev < 80) {
			level = 4.5f;
		} else if (mdev >= 80 && mdev < 120) {
			level = 4;
		} else if (mdev >= 120 && mdev < 160) {
			level = 3.5f;
		} else if (mdev >= 160 && mdev < 200) {
			level = 3;
		} else {
			level = 2;
		}

		this.ip = parseIp(sr.toString());
		this.loss = parseLoss(sr.toString());
		this.delay = parseDelay(sr.toString());

		rbState.setVisibility(View.VISIBLE);
		rbState.setRating(level);
		isFinish = true;
	}

	private String parseIp(String ping) {
		String ip = null;
		try {
			if (ping.contains(NetModel.PING)) {
				int indexOpen = ping.indexOf(NetModel.PING_PAREN_THESE_OPEN);
				int indexClose = ping.indexOf(NetModel.PING_PAREN_THESE_CLOSE);
				ip = ping.substring(indexOpen + 1, indexClose);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}

	private float parseLoss(String ping) {
		float transmit = 0f, error = 0f, receive = 0f, lossRate = 0f;
		try {
			if (ping.contains(NetModel.PING_STATISTICS)) {
				String lossStr = ping.substring(ping.indexOf(NetModel.PING_BREAK_LINE, ping.indexOf(NetModel.PING_STATISTICS)) + 1);
				lossStr = lossStr.substring(0, lossStr.indexOf(NetModel.PING_BREAK_LINE));
				String strArray[] = lossStr.split(NetModel.PING_COMMA);
				for (String str : strArray) {
					if (str.contains(NetModel.PING_TRANSMIT))
						transmit = Float.parseFloat(str.substring(0, str.indexOf(NetModel.PING_TRANSMIT)));
					else if (str.contains(NetModel.PING_RECEIVED))
						receive = Float.parseFloat(str.substring(0, str.indexOf(NetModel.PING_RECEIVED)));
					else if (str.contains(NetModel.PING_ERRORS))
						error = Float.parseFloat(str.substring(0, str.indexOf(NetModel.PING_ERRORS)));
					else if (str.contains(NetModel.PING_LOSS))
						lossRate = Float.parseFloat(str.substring(0, str.indexOf(NetModel.PING_RATE)));
				}
			}
			if (transmit != 0)
				lossRate = error / transmit;
			else if (lossRate == 0)
				lossRate = error / (error + receive);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lossRate;
	}

	private float parseDelay(String ping) {
		float delay = 0;
		try {
			if (ping.contains(NetModel.PING_RTT)) {
				String lossStr = ping.substring(ping.indexOf(NetModel.PING_RTT));
				lossStr = lossStr.substring(lossStr.indexOf(NetModel.PING_EQUAL) + 2);
				String strArray[] = lossStr.split(NetModel.PING_SLASH);
				delay = Float.parseFloat(strArray[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return delay;
	}

	@Override
	public String toString() {
		return "DoPingTask{" +
				"ip='" + ip + '\'' +
				", loss=" + loss +
				", delay=" + delay +
				'}';
	}
}