package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.content.Context;
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
import android.view.View;

import cn.com.chinaccs.datasite.main.common.App;


public class BoxAngleActivity extends Activity {
	/** Called when the activity is first created. */
	// 声明一个SensorManager管理传感器，一个自定义的类MyView，在myView中绘制自己想要的图像
	private SensorManager sensorManager;
	private MyView myView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 实例化SensorManager
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		// DisplayMetrics用于获取屏幕大小，再传递给myView方便绘制图形界面；
		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		// 构造一个MyView，display.widthPixels是当前屏幕的宽度，display.heightPixels是当前屏幕的高度
		myView = new MyView(this, display.widthPixels, display.heightPixels);
		// 这里就不是在layout文件夹里面的布局文件了，直接就是我们的myView。
		setContentView(myView);
	}

	// 在onResume()，onPause()中注册和解除监听器
	@Override
	protected void onResume() {
		super.onResume();

		sensorManager.registerListener(myView,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {

		sensorManager.unregisterListener(myView);
		Log.i("unregister", "ok");
		super.onStop();
	}

}

// 自定义的类MyView 因为要感应传感器所以实现SensorEventListener。
class MyView extends View implements SensorEventListener {

	// float x = 0;
	// float y = 0;

	// z轴上的值是我们所需要的，z轴就是垂直于水平面的方向，当你水平放置手机是它的数值为10，当你垂直放置时它就为0.
	float z = 0;
	private float width;
	private float height;
	Paint p;

	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyView(Context context, float width, float height) {
		super(context);
		this.width = width;
		this.height = height;

		// 得到一支画笔，设置基本属性
		p = new Paint();
		p.setStyle(Paint.Style.STROKE);

	}

	// 在onDraw()方法中才是我们真正要画的东西，也就是真正显示在屏幕上的图像
	@Override
	protected void onDraw(Canvas canvas) {
		// 画背景后还要画文字，再对画笔进行设置。
		p.setTextSize(50);
		// 所画的文字就是实际测得的角度，在这之前需要对z值进行转化也就是todegree（）方法。
		canvas.drawText(todegree(z) + "°", width / 2 - 20, height / 2, p);
		// 画完后再画一个圆圈，这个圆圈随着角度变化而变大变小。
		p.setColor(Color.RED);
		p.setStrokeWidth(2);
		canvas.drawCircle(width / 2, height / 2, 20 * z, p);

	}

	// 如何把当前加速度的值转化为当前角度值呢？这需要一定的硬件基础才能明白其中的原理，不懂得同学可以看一些加速度传感器方面的书，关于加速度
	// 传感器还有很多应用，比如速度的测量，位移的测量，这就需要更加复杂的算法了，这里就不再介绍
	private String todegree(float zz) {
		// 首先判断加速度的值是否大于10，小于-10，这是因为在运动过程中加速是不稳定的，而我们要测的是在静止状态下的稳定值。
		if (zz > 10) {
			zz = 10;
		} else if (zz < -10) {
			zz = -10;
		}
		// acos（zz / 10）就能求出倾斜角度的弧度值。
		double r = Math.acos(zz / 10);
		// 然后将弧度值转化为角度值
		int degree = (int) (r * 180 / Math.PI);
		// 最后返回一个String
		return String.valueOf(degree);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// x = event.values[0];
		// y = event.values[1];

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// 获得z轴的加速度值
			z = event.values[2];
			Log.d(App.LOG_TAG, "event-size:------" + event.values.length);
		}

		// 调用此方法进行重绘
		invalidate();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}