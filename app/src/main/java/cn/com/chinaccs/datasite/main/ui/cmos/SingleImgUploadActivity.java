package cn.com.chinaccs.datasite.main.ui.cmos;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.datasite.function.CompressImage;
import cn.com.chinaccs.datasite.main.datasite.function.FuncUploadFile;
import cn.com.chinaccs.datasite.main.datasite.function.ImageLoadTask;
import cn.com.chinaccs.datasite.main.ui.MainApp;

public class SingleImgUploadActivity extends BaseActivity {
	private static final int RESULT_CAMERA = 1;
	private static final int RESULT_CAMERA_LOCAL = 2;
	private Context context;
	private ImageButton ibAdd;
	private EditText etDes;
	private Button btnAdd;
	private int type;
	private String id;
	private boolean isSub;
	private String subType = "";
	private final String fileType = "1";
	
	private String imgURL = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ds_upload);
		initToolbar("图片上传");
		context = this;
		this.findViews();
		type = getIntent().getIntExtra("type", 0);
		id = getIntent().getStringExtra("id");
		isSub = getIntent().getBooleanExtra("isSub", false);
		ibAdd.setOnClickListener(affixLr);
		btnAdd.setOnClickListener(affixLr);
		if (isSub) {
			subType = getIntent().getStringExtra("subType");
			String desc = getIntent().getStringExtra("desc");
			etDes.setText(desc);
			etDes.setEnabled(false);
		}
		
		imgURL = getIntent().getStringExtra("imgUrl");
		
		showSingleImage(imgURL);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MainApp.app.imagePath = null;
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
		Uri uri = null;
		if (data != null) {
			uri = data.getData();
		}
		switch (requestCode) {
		case RESULT_CAMERA:
			try {
				if (resultCode == RESULT_OK) {
					addContentByFile();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case RESULT_CAMERA_LOCAL:
			if (uri != null) {
				try {
					if (resultCode == RESULT_OK) {
						String[] filePathColumn = { Media.DATA };
						Cursor cursor = getContentResolver().query(uri,
								filePathColumn, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor
								.getColumnIndex(filePathColumn[0]);
						String picturePath = cursor.getString(columnIndex);
						cursor.close();
						MainApp.app.imagePath = picturePath;
						addContentByFile();
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					toast("选择文件失败！");
				}
			}
			break;
		}
	}

	private void addContentByFile() {
		if (MainApp.app.imagePath == null || MainApp.app.imagePath.equals("")) {
			toast("选择文件失败！");
		} else {
			File file = new File(MainApp.app.imagePath);
			CompressImage ci = new CompressImage();
			Bitmap bm = ci.setCompress(file, 600, 800, 300);
			// Options opts = new Options();
			// opts.inSampleSize = 4;
			// Bitmap bm = BitmapFactory.decodeFile(file.getPath(), opts);
			BitmapDrawable bd = new BitmapDrawable(bm);
			ibAdd.setBackgroundDrawable(bd);
			bm = null;
		}
	}

	private void getImg() {
		String[] imgItems = { "开始拍照", "相册选取" };
		new AlertDialog.Builder(context).setTitle("上传图片")
				.setItems(imgItems, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							Intent i = new Intent(context,
									CameraLoadActivity.class);
							startActivityForResult(i, RESULT_CAMERA);
							dialog.dismiss();
							break;
						case 1:
							startActivityForResult(new Intent(
									Intent.ACTION_PICK,
									Media.EXTERNAL_CONTENT_URI),
									RESULT_CAMERA_LOCAL);
							dialog.dismiss();
							break;
						}
					}
				}).show();
	}

	private OnClickListener affixLr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.img_add:
				String state = Environment.getExternalStorageState();
				if (!state.equals(Environment.MEDIA_MOUNTED)) {
					toast("抱歉，您的手机存储不可用！");
					return;
				}
				getImg();
				break;
			case R.id.btn_img_add:
				submit();
				break;
			default:
				break;
			}
		}
	};

	private void findViews() {
		ibAdd = (ImageButton) findViewById(R.id.img_add);
		etDes = (EditText) findViewById(R.id.et_des);
		btnAdd = (Button) findViewById(R.id.btn_img_add);
	}

	private void submit() {
		if (MainApp.app.imagePath != null && !MainApp.app.imagePath.equals("")) {
			final ProgressDialog pd = App.progressDialog(context, context
					.getResources().getString(R.string.common_response));
			pd.show();
			final Handler hr = new Handler();
			new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					FuncUploadFile func = new FuncUploadFile(context);
					String des = etDes.getText().toString();
					final String result = func.uploadFileToServer(
							MainApp.app.imagePath, fileType, des,
							String.valueOf(type), subType, id,
							DataSiteStart.HTTP_SERVER_URL,
							DataSiteStart.HTTP_KEYSTORE);
					hr.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();
							try {
								if (result.equals(AppHttpClient.RESULT_FAIL)) {
									toast(getResources().getString(
											R.string.common_not_network));
								} else {
									JSONObject json = new JSONObject(result);
									String resCode = json.getString("result");
									String msg = json.getString("msg");
									if (resCode.equals("1")) {
										if (MainApp.app.imagePath != null) {
											File file = new File(
													MainApp.app.imagePath);
											file.delete();// 删除临时文件
										}
										setResult(RESULT_OK);
										finish();
									}
									toast(msg);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			}.start();
		} else {
			toast("尚未选择文件！");
		}
	}

	private void toast(String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}
	
	//资产清查已上传照片
	private void showSingleImage(final String url) {

		ImageView img = (ImageView) findViewById(R.id.img_single);
		ProgressBar pb = (ProgressBar) findViewById(R.id.pb_state);
		TextView tv = (TextView) findViewById(R.id.tv_state);

		pb.setVisibility(View.VISIBLE);
		new ImageLoadTask(img, pb, tv).execute(url);
		
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDetailImage(url);
			}
		});
	}
	
	private void showDetailImage(String url) {
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = li.inflate(R.layout.pop_image, null);
		ImageView img = (ImageView) layout.findViewById(R.id.img_single);
		ProgressBar pb = (ProgressBar) layout.findViewById(R.id.pb_state);
		TextView tv = (TextView) layout.findViewById(R.id.tv_state);
		Button btnc = (Button) layout.findViewById(R.id.btn_close);
		final PopupWindow pop = new PopupWindow(layout,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		pop.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.draw_bg_opactiy));
		pop.setFocusable(true);
		pop.showAtLocation(((Activity) context).findViewById(R.id.main),Gravity.CENTER, 0, 0);
		pop.update();
		pb.setVisibility(View.VISIBLE);
		new ImageLoadTask(img, pb, tv).execute(url);
		btnc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pop != null)
					pop.dismiss();
			}
		});
	}
}
