package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.WGSTOGCJ02;
import cn.com.chinaccs.datasite.main.datasite.function.CompressImage;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncGetPlanItemList;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.FuncSubInspectData;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.InspectViewFactory;
import cn.com.chinaccs.datasite.main.datasite.inspect.function.PlanItem;
import cn.com.chinaccs.datasite.main.datasite.listener.OnGetDataFinishedListener;
import cn.com.chinaccs.datasite.main.gps.GpsHandler;
import cn.com.chinaccs.datasite.main.ui.MainApp;


/**
 * @author Fddi
 *
 */
public class InspectContentActivity extends BaseActivity {
	private static final int[] RESULT_CAMERAS = { 101, 102, 103 };
	private static final int[] RESULT_CAMERA_LOCALS = { 201, 202, 203 };
	private Context context;
	private TextView tvBs;   //显示在文件头的基站名称
	private LinearLayout layout;  //母布局，用于加入巡检内容
	private Button btnSub;   //提交
	private ImageButton btnMap;   //签到
	private EditText etDes;   //巡检备注输入框
	private String bsId;   //基站id
	private Long planId;    //巡检内容id,从1开始，从后台获取
	private List<PlanItem> listItems;  //巡检内容实体类，在factory赋值后，提交到后台
	private ImageButton[] btnImgs;    //上传照片
	private ProgressDialog pd;
	private String[] imgPaths;   //图片路径
	private String bsLongitude;   //经度
	private String bsLatitude;  //纬度
	private ScrollView svc;
	private boolean isSign;
	private double lng = 4.9E-324;
	private double lat = 4.9E-324;
	private GpsHandler gpsHr;  //获取位置工具类
	private boolean isRRu;   //是否是独立物理站点
	private String cellId;   //小区id（扇区）
	private String eqName;   //小区名
	//是否有待上传的照片
	private boolean isHidden=false;
	boolean isExistImag=false;
	//private TextView title;

	// add by wuhua for
	// 用物理基站进行巡检
	// 基站巡检进去的填1，新基站巡检进去的填2
	private String inspectionType;
	// ended by wuhua 20151104

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_map,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		if (id==R.id.menu_map){
			getLocs();
			Bundle be = new Bundle();
			be.putString("bsLng", bsLongitude);
			be.putString("bsLat", bsLatitude);
			be.putString("ctLng", String.valueOf(lng));
			be.putString("ctLat", String.valueOf(lat));
			be.putString("bsName", eqName);
			Intent i = new Intent(context, ContratsMapActivity.class);
			i.putExtras(be);
			startActivity(i);

		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspect_content);
		initToolbar("巡检内容");
		this.context = this;
		this.findViews();
		gpsHr = ((MainApp) getApplication()).gpsHandler;
		listItems = new ArrayList<PlanItem>();
		Bundle be = getIntent().getExtras();
		Log.i(App.LOG_TAG, "isRRu");
		Log.i(App.LOG_TAG, be.toString());
		// add by wuhua for
		// 用物理基站进行巡检
		// 基站巡检进去的填1，新基站巡检进去的填2
		inspectionType = be.getString("inspectionType","2");
		// ended by wuhua 20151104
        //这里就传过来一个planid，那么其他的都是上传null到服务器吗
		bsId = be.getString("bsId");
		planId = be.getLong("planId");
		bsLongitude = be.getString("longitude");
		bsLatitude = be.getString("latitude");
		isHidden=be.getBoolean("isHidden");
		if (isHidden) {
			//title.setText("隐患处理");
			initToolbar("隐患处理");
		}
		isRRu = be.getBoolean("isRRU");
		if (isRRu) {
			cellId = be.getString("cellId");
			// eqName = be.getString("cellName");
			eqName = be.getString("bsName");
			tvBs.setText("当前RRU：");
			tvBs.append(eqName);
		} else {
			eqName = be.getString("bsName");
			tvBs.setText("当前基站：");
			tvBs.append(eqName);
		}
		this.buildForm();
		imgPaths = new String[3];
		if (btnImgs != null) {
			for (int i = 0; i < btnImgs.length; i++) {
				btnImgs[i].setOnClickListener(imglr);
			}
		}
		btnSub.setOnClickListener(clr);
		//btnMap.setOnClickListener(clr);
		onTipState();
	}
	//签到的状态改变
	private void onTipState() {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		long cd = share.getLong(InspectSignActivity.SHARE_SIGN_DATE, 0);
		long inv = new Date().getTime() - cd;
		Log.d(App.LOG_TAG, cd + "/" + inv);
		if (cd == 0 || inv > InspectSignActivity.TIME_SIGN_OUT_LOC) {
			isSign = false;
			tvBs.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_sign_off), null);
		} else {
			isSign = true;
			tvBs.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_sign_on), null);
			lng = share.getFloat(InspectSignActivity.SHARE_SIGN_LNG, 0);
			lat = share.getFloat(InspectSignActivity.SHARE_SIGN_LAT, 0);
		}
	}

	@Override
	protected void onDestroy() {
		listItems = null;
		btnImgs = null;
		imgPaths = null;
		pd = null;
		gpsHr = null;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (gpsHr != null)
			gpsHr.unregisterGpsReceiver();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gpsHr != null)
			gpsHr.registerGpsReceiver();
	}
	/**
	 * 照相结束回传到界面，等待上传
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		Uri uri = null;
		if (data != null) {
			uri = data.getData();
		}
		Log.d(App.LOG_TAG, "resultCode" + resultCode);
		boolean isRequest = false;
		//照相
		for (int i = 0; i < RESULT_CAMERAS.length; i++) {
			if (requestCode == RESULT_CAMERAS[i]) {
				if (MainApp.app.imagePath != null && resultCode == RESULT_OK) {
					imgPaths[i] = MainApp.app.imagePath;
					addContentByFile(i);
				} else {
					toast("图片选取失败！");
				}
				isRequest = true;
				break;
			}
		}
		//选取相册
		if (!isRequest) {
			for (int i = 0; i < RESULT_CAMERAS.length; i++) {
				if (requestCode == RESULT_CAMERA_LOCALS[i]) {
					if (resultCode == RESULT_OK) {
						String[] filePathColumn = { MediaStore.Images.Media.DATA };
						Cursor cursor = getContentResolver().query(uri,
								filePathColumn, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor
								.getColumnIndex(filePathColumn[0]);
						String picturePath = cursor.getString(columnIndex);
						cursor.close();
						imgPaths[i] = picturePath;
						addContentByFile(i);
					} else {
						toast("图片选取失败！");
					}
					isRequest = true;
					break;
				}
			}
		}
	}

	private OnClickListener clr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_close:
				//检查是否有照片，
				if (isExistImag) {
					AlertDialog ad = App.alertDialog(context, "提示！", "确定提交数据？",
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							getLocs();
							FuncSubInspectData func = new FuncSubInspectData(
									context);
							String desc = etDes.getText().toString();
							// changed by wuhua for
							// 用物理基站进行巡检
							// 基站巡检进去的填1，新基站巡检进去的填2
							//func.subData(isRRu, cellId, desc, bsId, eqName,
							//		bsLongitude, bsLatitude, lng, lat,
							//		planId, listItems, imgPaths);
							func.subData(isRRu, cellId, desc, bsId, eqName,
									bsLongitude, bsLatitude, lng, lat,
									planId, inspectionType, listItems, imgPaths);
							// ended by wuhua 20151104
						}
					}, null);
					ad.show();
				}else{
					toast("抱歉，提交失败，请您选择需要上传的照片！");
				}
					break;
/*			case R.id.btn_map:
				getLocs();
				Bundle be = new Bundle();
				be.putString("bsLng", bsLongitude);
				be.putString("bsLat", bsLatitude);
				be.putString("ctLng", String.valueOf(lng));
				be.putString("ctLat", String.valueOf(lat));
				be.putString("bsName", eqName);
				Intent i = new Intent(context, ContratsMapActivity.class);
				i.putExtras(be);
				startActivity(i);
				break;*/
			default:
				break;
			}
		}
	};
	/**
	 * 获取签到人位置
	 */
	private void getLocs() {
		if (isSign) {
			Log.d(App.LOG_TAG, "location by SIGN --" + lng + "/" + lat);
			return;
		}
		Location loc = gpsHr.getLastLocation();
		if (loc != null) {
			long ts = (new Date()).getTime() - loc.getTime();
			if (ts <= GpsHandler.TIME_OUT_LOC) {
				lng = loc.getLongitude();
				lat = loc.getLatitude();
				Log.d(App.LOG_TAG, "location by GPS --" + lng + "/" + lat);
				return;
			}
		}
		if (MainApp.geoBD != null && MainApp.geoBD.locClient != null
				&& MainApp.geoBD.locClient.isStarted()) {
			MainApp.geoBD.locClient.requestLocation();
			BDLocation BDloc = MainApp.geoBD.location;
			if (BDloc != null) {
				lng = BDloc.getLongitude();
				lat = BDloc.getLatitude();
				WGSTOGCJ02 wg = new WGSTOGCJ02();
				Map<String, Double> wgsloc = wg.gcj2wgs(lng, lat);
				lng = wgsloc.get("lon");
				lat = wgsloc.get("lat");
				Log.d(App.LOG_TAG, "location by BaiDu --" + lng + "/" + lat);
				return;
			}
		}
	}

	private OnClickListener imglr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String state = Environment.getExternalStorageState();
			if (!state.equals(Environment.MEDIA_MOUNTED)) {
				toast("抱歉，您的手机存储不可用！");
				return;
			}
			for (int i = 0; i < btnImgs.length; i++) {
				if (v.equals(btnImgs[i])) {
					getImg(i);
					break;
				}
			}
		}
	};
	/**
	 * 以动态的方式添加巡检内容
	 */
	private void buildForm() {
		pd = null;
		pd = App.progressDialog(context,
				getResources().getString(R.string.common_request));
		pd.show();
		FuncGetPlanItemList func = new FuncGetPlanItemList(context);
		OnGetDataFinishedListener glr = new OnGetDataFinishedListener() {

			@Override
			public void onFinished(String output) {
				try {
					pd.dismiss();
					JSONObject json = new JSONObject(output);
					String result = json.getString("result");
					String msg = json.getString("msg");
					if (result.equals("1")) {
						InspectViewFactory factory = new InspectViewFactory(
								context);
						JSONArray arrays = json.getJSONArray("data");
						for (int i = 0; i < arrays.length(); i++) {
							JSONArray array = arrays.getJSONArray(i);
							factory.addView(array, layout, listItems);
						}
						factory = null;
					} else {
						Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				svc.post(new Runnable() {

					@Override
					public void run() {
						svc.smoothScrollTo(0, 10);
					}
				});
			}
		};
		func.getData(glr, String.valueOf(planId));
	}
	//将图片加入到布局Imagebtn中去
	private void addContentByFile(final int codeIndex) {
		if (imgPaths[codeIndex] == null || ("").equals(imgPaths[codeIndex])) {
			Log.d(App.LOG_TAG, "选择图片失败-------" + codeIndex);
		} else {
			File file = new File(imgPaths[codeIndex]);
			if (file != null && file.exists() && file.length() > 0) {
				isExistImag=true;
				CompressImage ci = new CompressImage();
				Bitmap bm = ci.setCompress(file, 600, 800, 300);
				BitmapDrawable bd = new BitmapDrawable(bm);
				btnImgs[codeIndex].setBackgroundDrawable(bd);
				bm = null;
			} else {
				Log.d(App.LOG_TAG, "选择图片失败-------" + codeIndex);
			}
		}
	}

	private void getImg(final int codeIndex) {
		String[] imgItems = { "开始拍照", "相册选取" };
		new AlertDialog.Builder(context).setTitle("上传图片")
		.setItems(imgItems, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					try {
						Intent i = new Intent(context,
								CameraLoadActivity.class);
						startActivityForResult(i,
								RESULT_CAMERAS[codeIndex]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					dialog.dismiss();
					break;
				case 1:
					startActivityForResult(new Intent(
							Intent.ACTION_PICK,
							Media.EXTERNAL_CONTENT_URI),
							RESULT_CAMERA_LOCALS[codeIndex]);
					dialog.dismiss();
					break;
				}
			}
		}).show();
	}

	private void toast(String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	private void findViews() {
		tvBs = (TextView) findViewById(R.id.tv_bsname);
		layout = (LinearLayout) findViewById(R.id.layout_it);
		btnSub = (Button) findViewById(R.id.btn_close);
		//btnMap = (ImageButton) findViewById(R.id.btn_map);
		etDes = (EditText) findViewById(R.id.et_itdesc);
		btnImgs = new ImageButton[3];
		btnImgs[0] = (ImageButton) findViewById(R.id.img_it_add_a1);
		btnImgs[1] = (ImageButton) findViewById(R.id.img_it_add_a2);
		btnImgs[2] = (ImageButton) findViewById(R.id.img_it_add_a3);
		svc = (ScrollView) findViewById(R.id.sv_itcontent);
		//title=(TextView) findViewById(R.id.inspect_content_title);
	}

}
