package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.CameraFlowAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.ImageLoadTask;

/**
 * @author Fddi
 * 
 */
public class DSCameraFlowActivity extends Activity {
	private static final int REQUESTCODE_UPLOAD_IMG = 1;
	private Context context;
	private int type;
	private String id;
	private ListView lvCf;
	private Button btnShow;
	private ImageButton btnUp;
	private ImageButton btnHelp;
	private String userid;
	private ProgressDialog pd;
	private CameraFlowAdapter ad;
	private List<Map<String, String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ds_camera);
		context = this;
		type = getIntent().getIntExtra("type", 0);
		id = getIntent().getStringExtra("id");
		this.findViews();
		SharedPreferences share = context
				.getSharedPreferences(App.SHARE_TAG, 0);
		userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		buildCameraList();
		btnShow.setOnClickListener(lr);
		btnUp.setOnClickListener(lr);
		btnHelp.setOnClickListener(lr);
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
		// super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUESTCODE_UPLOAD_IMG:
			if (resultCode == RESULT_OK) {
				buildCameraList();
			}
			break;
		}
	}

	private OnClickListener lr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_img_show:
				Intent i1 = new Intent(context, DSCommonSlideActivity.class);
				i1.putExtra("type", type);
				i1.putExtra("id", id);
				startActivity(i1);
				break;
			case R.id.btn_upload:
				Intent i = new Intent(context, SingleImgUploadActivity.class);
				i.putExtra("type", type);
				i.putExtra("id", id);
				i.putExtra("isSub", false);
				startActivityForResult(i, REQUESTCODE_UPLOAD_IMG);
				break;
			case R.id.btn_help:
				new AlertDialog.Builder(context)
						.setTitle("拍照流程说明：")
						.setMessage(
								getResources().getString(R.string.camera_flow))
						.setNegativeButton("关闭", null).show();
				break;
			default:
				break;
			}
		}
	};

	private void buildCameraList() {
		pd = null;
		pd = App.progressDialog(context,
				getResources().getString(R.string.common_request));
		pd.show();
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userid
						+ type);
				Log.d(App.LOG_TAG, "type------" + type);
				StringBuffer url = new StringBuffer(
						DataSiteStart.HTTP_SERVER_URL);
				url.append("CameraFlowList.do?userid=").append(userid)
						.append("&dstype=").append(type).append("&dsid=")
						.append(id).append("&sign=").append(sign);
				AppHttpConnection conn = new AppHttpConnection(context,
						url.toString());
				final String conResult = conn.getConnectionResult();
				hr.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.dismiss();
						if (conResult.equals(AppHttpConnection.RESULT_FAIL)) {
							toast(getResources().getString(
									R.string.common_not_network));
						} else {
							try {
								JSONObject json = new JSONObject(conResult);
								if (("1").equals(json.getString("result"))) {
									buildList(json.getJSONArray("data"));
								} else {
									toast(json.getString("msg"));
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}

		}.start();
	}

	private void buildList(JSONArray array) {
		list = new ArrayList<Map<String, String>>();
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONArray datas = array.getJSONArray(i);
				Map<String, String> data = new HashMap<String, String>();
				data.put("subType", datas.getString(0));
				data.put("desc", datas.getString(1));
				data.put("imgUrl", datas.getString(2));
				list.add(data);
			}
			ad = new CameraFlowAdapter(context, list);
			lvCf.setAdapter(ad);
			OnItemClickListener icl = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					// TODO Auto-generated method stub
					Map<String, String> data = list.get(position);
					getItemAction(data);
				}
			};
			lvCf.setOnItemClickListener(icl);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getItemAction(final Map<String, String> data) {
		//弹出提示
//		String[] imgItems = { "查看图片", "上传图片" };
//		new AlertDialog.Builder(context).setTitle("功能")
//				.setItems(imgItems, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						switch (which) {
//						case 0:
//							showSingleImage(data.get("imgUrl"));
//							dialog.dismiss();
//							break;
//						case 1:
//							Intent i = new Intent(context,
//									SingleImgUploadActivity.class);
//							i.putExtra("type", type);
//							i.putExtra("id", id);
//							i.putExtra("isSub", true);
//							i.putExtra("desc", data.get("desc"));
//							i.putExtra("subType", data.get("subType"));
//							startActivityForResult(i, REQUESTCODE_UPLOAD_IMG);
//							dialog.dismiss();
//							break;
//						}
//					}
//				}).show();
		
		//资产清查无提示
		Intent i = new Intent(context, SingleImgUploadActivity.class);
		i.putExtra("type", type);
		i.putExtra("id", id);
		i.putExtra("isSub", true);
		i.putExtra("desc", data.get("desc"));
		i.putExtra("subType", data.get("subType"));
		i.putExtra("imgUrl", data.get("imgUrl"));//照片URL
		startActivityForResult(i, REQUESTCODE_UPLOAD_IMG);
	}

	private void showSingleImage(String url) {
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
		pop.showAtLocation(((Activity) context).findViewById(R.id.main),
				Gravity.CENTER, 0, 0);
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

	private void toast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	private void findViews() {
		lvCf = (ListView) findViewById(R.id.lv_camera_flow);
		btnShow = (Button) findViewById(R.id.btn_img_show);
		btnUp = (ImageButton) findViewById(R.id.btn_upload);
		btnHelp = (ImageButton) findViewById(R.id.btn_help);
	}
}
