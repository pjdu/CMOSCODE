package cn.com.chinaccs.datasite.main.ui.cmos;

import java.math.BigDecimal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;

/**
 * @author Fddi
 * 
 */
public class BoxDowntiltActivity extends BaseActivity implements
		SensorEventListener {
	private Context context;

    private ImageButton btnHelp;
    private TextView tvTips;
    private FrameLayout vAngle;
    private Button btnGet;
    private DowntiltView dv;
    private SensorManager sensorManager;
    float x = 0;
    float y = 0;
    float z = 0;
    private String angle;
    private float currentDegree;
    private float TargetDegree;
    private Bundle be;
    private Sensor sensor;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_help){
            funcHelp();
        }
        return true;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downtilt);
        initToolbar("下倾角测量");
		be = getIntent().getExtras();
		context = this;
		// 实例化SensorManager
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		currentDegree = 0.0f;
		TargetDegree = 0.0f;
		findViews();
		// DisplayMetrics用于获取屏幕大小，再传递给myView方便绘制图形界面；
		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		dv = new DowntiltView(context, display.widthPixels,
				display.heightPixels);
		vAngle.addView(dv);
		vAngle.invalidate();
		//btnHelp.setOnClickListener(lr);
		btnGet.setOnClickListener(lr);
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
				.setMessage(getResources().getString(R.string.measure_dway))
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unRegisterSensor();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerSensor();
	}

	/**
	 * 注销传感器监听
	 */
	private void unRegisterSensor() {
		if (sensor != null) {
			sensorManager.unregisterListener(this);
		}
		sensor = null;
	}

	/**
	 * 启动传感器监听
	 */
	private void registerSensor() {
		// 类型为加速度传感器
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (sensor == null) {
			Toast.makeText(context, "无法找到手机加速度感应器！", Toast.LENGTH_LONG).show();
		} else {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	private void findViews() {
		//btnHelp = (ImageButton) findViewById(R.id.btn_help);
		tvTips = (TextView) findViewById(R.id.tv_tips);
		vAngle = (FrameLayout) findViewById(R.id.view_angle);
		btnGet = (Button) findViewById(R.id.btn_get);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// TYPE_ACCELEROMETER--加速度传感器类型
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
		}
		double ga = Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);
		double gb = Math.pow(9.81, 2);
		Log.d(App.LOG_TAG, x + "/" + y + "/" + z + "/" + (x + y + z));
		if (Math.abs(ga - gb) > 8.0) {// 如果两值相差8以上则说明手机在运动中，无法测量 可在这里修改
			btnGet.setEnabled(false);
		} else {
			TargetDegree = countAngle(x, y, z);
			if (Math.abs(currentDegree - TargetDegree) > 1) {
				angle = String.valueOf(TargetDegree);
				dv.reDraw(angle, z);
				dv.invalidate(); // 调用此方法进行重绘
				currentDegree = TargetDegree;
			}
			btnGet.setEnabled(true);
		}
		tvTips.setText(angle + "°");
	}

	private float countAngle(float x, float y, float z) {
		Float angle = 0.0f;
		if (x == 0 && y == 0 && z == 0) {
			return angle;
		}
		angle = (float) (Math.atan(Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2))
				/ y) * (180 / Math.PI));
		if (angle != null) {
			BigDecimal b = new BigDecimal(angle);
			angle = b.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
		}
		return angle;
	}
}

// 自定义的类MyView 因为要感应传感器所以实现SensorEventListener。
class DowntiltView extends View {
	Context context;
	private float width;
	private float height;
	private String angle = "0.0";
	private float z;
	Paint p;

	public DowntiltView(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public DowntiltView(Context context, float width, float height) {
		super(context);
		this.context = context;
		this.width = width;
		this.height = height;
		// 得到一支画笔，设置基本属性
		p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(Color.RED);
	}

	// 在onDraw()方法中才是我们真正要画的东西，也就是真正显示在屏幕上的图像
	@Override
	protected void onDraw(Canvas canvas) {
		// 画背景后还要画文字，再对画笔进行设置。
		p.setTextSize(50);
		// 所画的文字就是实际测得的角度，在这之前需要对z值进行转化也就是todegree（）方法。
		if (angle != null) {
			canvas.drawText(angle, width / 2 - 40, height / 2 - 80, p);
			// 画完后再画一个圆圈，这个圆圈随着角度变化而变大变小。
			p.setColor(Color.RED);
			p.setStrokeWidth(2);
			canvas.drawCircle(width / 2, height / 2 - 80, 20 * z, p);
		}
	}

	/**
	 * @param angle
	 * @param z
	 */
	public void reDraw(String angle, float z) {
		this.angle = angle;
		this.z = z;
		invalidate();
	}
}