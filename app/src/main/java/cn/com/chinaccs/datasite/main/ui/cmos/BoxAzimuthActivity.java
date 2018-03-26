package cn.com.chinaccs.datasite.main.ui.cmos;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;

/**
 * 方位角测量
 * 
 * @author Fddi
 * 
 */
public class BoxAzimuthActivity extends Activity implements SensorEventListener {
	private Context context;
	private ImageButton btnHelp;
	private TextView tvTips;
	private ImageView imgCompass;
	private Button btnGet;
	private SensorManager sensorManager;
	private float[] avs;
	private float[] mvs;
	private String angle;
	private float currentDegree;
	private float TargetDegree;
	private Bundle be;
	private Sensor sensorAcce;
	private Sensor sensorMag;
	private boolean stopDrawing;
	protected final Handler hr = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_azimuth);
		be = getIntent().getExtras();
		context = this;
		// 实例化SensorManager
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		currentDegree = 0.0f;
		TargetDegree = 0.0f;
		stopDrawing = false;
		findViews();
		btnHelp.setOnClickListener(lr);
		btnGet.setOnClickListener(lr);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		stopDrawing = true;
		unRegisterSensor();
		hr.removeCallbacks(mCompassViewUpdater);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerSensor();
		stopDrawing = false;
		hr.postDelayed(mCompassViewUpdater, 50);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		int type = event.sensor.getType();
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
			avs = event.values;
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			mvs = event.values;
			getAzimuth();
			break;
		default:
			break;
		}

	}

	private void getAzimuth() {
		try {
			if (avs != null && mvs != null) {
				float[] values = new float[3];
				float[] R = new float[9];
				SensorManager.getRotationMatrix(R, null, avs, mvs);
				SensorManager.getOrientation(R, values);
				TargetDegree = (float) Math.toDegrees(values[0]);
				if (TargetDegree < 0) {
					TargetDegree = 360 + TargetDegree;
				}
				BigDecimal b = new BigDecimal(TargetDegree);
				TargetDegree = b.setScale(0, BigDecimal.ROUND_HALF_UP)
						.floatValue();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private OnClickListener lr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_help:
				funcHelp();
				break;
			case R.id.btn_get:
				unRegisterSensor();
				funcGet();
				break;
			default:
				break;
			}
		}
	};

	private void funcHelp() {
		new AlertDialog.Builder(context).setTitle("测量方法与步骤：")
				.setMessage(getResources().getString(R.string.measure_away))
				.setNegativeButton("关闭", null).show();
	}

	private void funcGet() {
		AlertDialog dialog = App.alertDialog(context, "提示", "成功测出角度：" + angle
				+ "°", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (be != null) { // 判断是否是可返回值
					be.putString("angle", angle);
					Intent i = new Intent();
					i.putExtras(be);
					setResult(RESULT_OK, i);
				}
				finish();
			}
		}, "确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				registerSensor();
			}
		}, "重新测量");
		dialog.show();
	}

	/**
	 * 注销传感器监听
	 */
	private void unRegisterSensor() {
		if (sensorAcce != null && sensorMag != null) {
			sensorManager.unregisterListener(this);
		}
		sensorAcce = null;
		sensorMag = null;
	}

	/**
	 * 启动传感器监听
	 */
	private void registerSensor() {
		// 类型为加速度传感器和磁场传感器，混合调用 SensorManager.getOrientation()获取方向角
		sensorAcce = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (sensorAcce == null || sensorMag == null) {
			Toast.makeText(context, "抱歉，您的手机不支持电子罗盘功能", Toast.LENGTH_LONG)
					.show();
			btnGet.setEnabled(false);
		} else {
			sensorManager.registerListener(this, sensorAcce,
					SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(this, sensorMag,
					SensorManager.SENSOR_DELAY_GAME);
			btnGet.setEnabled(true);
		}
	}

	private void findViews() {
		btnHelp = (ImageButton) findViewById(R.id.btn_help);
		tvTips = (TextView) findViewById(R.id.tv_tips);
		imgCompass = (ImageView) findViewById(R.id.img_compass);
		btnGet = (Button) findViewById(R.id.btn_get);
	}

	protected Runnable mCompassViewUpdater = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (imgCompass != null && !stopDrawing) {
				if (Math.abs(currentDegree - TargetDegree) > 1) {// 误差在3度内不做测量
					angle = String.valueOf(TargetDegree);
					tvTips.setText(angle + "°");
					RotateAnimation ra = new RotateAnimation(-currentDegree,
							-TargetDegree, Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					ra.setDuration(100);
					imgCompass.startAnimation(ra);
					currentDegree = TargetDegree;
				} else {
					RotateAnimation ra = new RotateAnimation(-currentDegree,
							-TargetDegree, Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					ra.setDuration(100);
					imgCompass.startAnimation(ra);
				}
				hr.postDelayed(mCompassViewUpdater, 50);
			}
		}
	};
}
