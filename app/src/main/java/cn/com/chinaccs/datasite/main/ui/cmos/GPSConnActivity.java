package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.gps.GPSChart;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.gps.GpsSatellite;
import cn.com.chinaccs.datasite.main.ui.MainApp;

public class GPSConnActivity extends Activity {
	Context context;
	private Button btnState;
	private TextView tvEle;
	private TextView tvStar;
	private LinearLayout layoutStar;
	private TextView tvTime;
	private TextView tvLon;
	private TextView tvLat;
	private GpsHandler gpsHandler;
	private Handler hr;
	private LocationManager lm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_gps);
		this.findViews();
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gpsHandler = ((MainApp) getApplication()).gpsHandler;
		btnState.setOnClickListener(blr);
		String sstars = String.format(getString(R.string.gps_star_count), "0",
				"0");
		tvStar.setText(sstars);
		hr = new Handler();
	}

	private Runnable locChange = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (gpsHandler == null) {
				Log.e("GPSConnActivity", "gpsHandler is null");
				return;
			}
			Location loc = gpsHandler.getLastLocation();
			List<GpsSatellite> gss = gpsHandler.getGpsSatellites();
			if (loc != null && gss != null) {
				updateLocation(loc);
				updateSatellites(gss);
				String[] prns = new String[gss.size()];
				double[] snrs = new double[gss.size()];
				for (int i = 0; i < gss.size(); i++) {
					prns[i] = String.valueOf(gss.get(i).getPrn());
					snrs[i] = gss.get(i).getSnr();
					BigDecimal bdl = new BigDecimal(snrs[i]);
					snrs[i] = bdl.setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue();
				}
				layoutStar.removeAllViews();
				layoutStar.addView(new GPSChart().execute(context, prns, snrs));
				layoutStar.invalidate();
			}
			gpsState();
			hr.postDelayed(locChange, 1000);
		}
	};

	private OnClickListener blr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_gps:
				startActivityForResult(new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
				break;
			}
		}
	};

	private void updateSatellites(List<GpsSatellite> GpsSatellites) {
		if (GpsSatellites != null && GpsSatellites.size() > 0) {
			int used = 0;
			for (GpsSatellite sate : GpsSatellites) {
				if (sate.isUse()) {
					used++;
				}
			}
			Log.d(App.LOG_TAG, "used------" + used);
			String sstars = String.format(getString(R.string.gps_star_count),
					String.valueOf(GpsSatellites.size()), String.valueOf(used));
			tvStar.setText(sstars);
		}
	}

	private void updateLocation(Location loc) {
		if (loc != null) {
			tvEle.setText(String.valueOf(loc.getAltitude()));
			Date date = new Date(loc.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",
					Locale.CHINA);
			tvTime.setText(sdf.format(date));
			tvLon.setText(String.valueOf(loc.getLongitude()));
			tvLat.setText(String.valueOf(loc.getLatitude()));
		}
	}

	private void gpsState() {
		if (lm != null && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			btnState.setText(getString(R.string.gps_close));
			btnState.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_state_off), null, null, null);
		} else {
			btnState.setText(getString(R.string.gps_open));
			btnState.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_state_on), null, null, null);
		}
	}

	private void findViews() {
		tvEle = (TextView) findViewById(R.id.tv_elevation);
		tvStar = (TextView) findViewById(R.id.tv_star);
		tvTime = (TextView) findViewById(R.id.tv_gps_time);
		tvLon = (TextView) findViewById(R.id.tv_lon);
		tvLat = (TextView) findViewById(R.id.tv_lat);
		btnState = (Button) findViewById(R.id.btn_gps);
		layoutStar = (LinearLayout) findViewById(R.id.layout_star);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		gpsHandler = null;
		hr = null;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (gpsHandler != null)
			gpsHandler.unregisterGpsReceiver();
		hr.removeCallbacks(locChange);
		if (gpsHandler != null) {
			gpsHandler.stopGps();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gpsState();
		if (gpsHandler != null)
			gpsHandler.startGps(GpsHandler.TIME_INTERVAL_GPS);
		hr.postDelayed(locChange, 1000);
	}

}
