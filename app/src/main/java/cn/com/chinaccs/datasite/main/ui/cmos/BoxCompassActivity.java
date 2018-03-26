package cn.com.chinaccs.datasite.main.ui.cmos;

import java.math.BigDecimal;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.widget.CompassView;

/**
 * @author Fddi
 *
 */
public class BoxCompassActivity extends BaseActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_help) {
            funcHelp();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help,menu);
        return true;
    }

    private final float MAX_ROATE_DEGREE = 1.0f;// 最多旋转一周，即360°
    private SensorManager mSensorManager;// 传感器管理对象
    private Sensor mOrientationSensor;// 传感器对象
    private float mDirection;// 当前浮点方向
    private float mTargetDirection;// 目标浮点方向
    private AccelerateInterpolator mInterpolator;// 动画从开始到结束，变化率是一个加速的过程,就是一个动画速率
    protected final Handler mHandler = new Handler();
    private boolean mStopDrawing;// 是否停止指南针旋转的标志位
    private Bundle be;

    Context context;
    //ImageButton btnHelp;
    TextView tvTips;
    CompassView mPointer;// 指南针view
    Button btnGet;
    float angle = 0f;
    // 这个是更新指南针旋转的线程，handler的灵活使用，每20毫秒检测方向变化值，对应更新指南针旋转
    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mPointer != null && !mStopDrawing) {
                if (mDirection != mTargetDirection) {

                    // calculate the short routine
                    float to = mTargetDirection;
                    if (to - mDirection > 180) {
                        to -= 360;
                    } else if (to - mDirection < -180) {
                        to += 360;
                    }

                    // limit the max speed to MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE
                                : (-1.0f * MAX_ROATE_DEGREE);
                    }

                    // need to slow down if the distance is short
                    mDirection = normalizeDegree(mDirection
                            + ((to - mDirection) * mInterpolator
                            .getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f
                                    : 0.3f)));// 用了一个加速动画去旋转图片，很细致
                    mPointer.updateDirection(mDirection);// 更新指南针旋转
                    angle = normalizeDegree(mTargetDirection * -1.0f);
                    BigDecimal b = new BigDecimal(angle);
                    angle = b.setScale(0, BigDecimal.ROUND_HALF_UP)
                            .floatValue();
                    tvTips.setText(angle + "°");
                }
                mHandler.postDelayed(mCompassViewUpdater, 20);// 20毫米后重新执行自己，比定时器好
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        initToolbar("方位角测量");
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageButton imageButton = new ImageButton(this);
        imageButton.setBackground(getResources().getDrawable(R.drawable.ic_help));
        imageButton.setId(R.id.btn_help);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.leftMargin=50;
        toolbar.addView(imageButton,layoutParams);*/
        this.context = this;
        initResources();
        initServices();
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
                    be.putString("angle", String.valueOf(angle));
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

    // 初始化view
    private void initResources() {
        mDirection = 0.0f;// 初始化起始方向
        mTargetDirection = 0.0f;// 初始化目标方向
        mInterpolator = new AccelerateInterpolator();// 实例化加速动画对象
        mStopDrawing = true;
        mPointer = (CompassView) findViewById(R.id.compass_pointer);// 自定义的指南针view
        tvTips = (TextView) findViewById(R.id.tv_tips);
        //btnHelp = (ImageButton) findViewById(R.id.btn_help);
        btnGet = (Button) findViewById(R.id.btn_get);
        be = getIntent().getExtras();
        //btnHelp.setOnClickListener(lr);
        btnGet.setOnClickListener(lr);
    }

    // 初始化传感器和位置服务
    private void initServices() {
        // sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    // 方向传感器变化监听
    private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float direction = event.values[0] * -1.0f;
            mTargetDirection = normalizeDegree(direction);// 赋值给全局变量，让指南针旋转
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        mStopDrawing = true;
        unRegisterSensor();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerSensor();
        mStopDrawing = false;
        mHandler.postDelayed(mCompassViewUpdater, 20);// 20毫秒执行一次更新指南针图片旋转
    }

    /**
     * 启动传感器监听
     */
    private void registerSensor() {
        if (mOrientationSensor != null) {
            mSensorManager.registerListener(mOrientationSensorEventListener,
                    mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
            btnGet.setEnabled(true);
        } else {
            Toast.makeText(this, "抱歉，您的手机不支持电子罗盘功能", Toast.LENGTH_SHORT).show();
            btnGet.setEnabled(false);
        }
    }

    /**
     * 注销传感器监听
     */
    private void unRegisterSensor() {
        if (mOrientationSensor != null) {
            mSensorManager.unregisterListener(mOrientationSensorEventListener);
        }
    }

    // 调整方向传感器获取的值
    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }
}
