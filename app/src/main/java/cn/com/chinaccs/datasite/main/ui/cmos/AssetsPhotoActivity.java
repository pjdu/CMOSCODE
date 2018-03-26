package cn.com.chinaccs.datasite.main.ui.cmos;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.ContentListAdapter;
import cn.com.chinaccs.datasite.main.datasite.function.FuncSubAssetsPhotos;
import cn.com.chinaccs.datasite.main.db.dao.DAOAssetsPhoto;
import cn.com.chinaccs.datasite.main.db.model.AssetsPhotosModel;

/**
 * @author fddi
 * 
 */
public class AssetsPhotoActivity extends BaseActivity {
	private Context context;
	private String id;
	private String name;
	private TextView tvName;
	private TextView tvDate;
	private TextView tvUser;
	private ListView elv;
	private Button btnImgShow;
	private Button btnImgUpload;
	private ProgressDialog proDialog;
	private TextView tvState;
	private List<Map<String, String>> list;
	private ProgressDialog pd;
	private String userid;
	private int type;
	private JSONArray jsonRes;
	private final String fileType = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_assets_photos);
		initToolbar("资产清查照片");
		Bundle be = getIntent().getExtras();
		id = be.getString("id");
		name = be.getString("name");
		type = DataSiteStart.TYPE_ASSETS;
		this.findView();
		tvName.setText(name);
		SharedPreferences share = context
				.getSharedPreferences(App.SHARE_TAG, 0);
		userid = share.getString(AppCheckLogin.SHARE_USER_ID, "");
		btnImgShow.setOnClickListener(btnlr);
		btnImgUpload.setOnClickListener(btnlr);
		this.buildList();
		buildCameraList();
	}

	private void buildList() {
		final String[] parent = {
				getResources().getString(R.string.assets_photo_device),
				getResources().getString(R.string.assets_photo_tk),
				getResources().getString(R.string.assets_photo_gt),
				getResources().getString(R.string.assets_photo_jf),
				getResources().getString(R.string.assets_photo_jlpdx),
				getResources().getString(R.string.assets_photo_kgdy),
				getResources().getString(R.string.assets_photo_xdc),
				getResources().getString(R.string.assets_photo_kt),
				getResources().getString(R.string.assets_photo_cssb),
				getResources().getString(R.string.assets_photo_other),
				getResources().getString(R.string.assets_photo_aqyhz) };


		ContentListAdapter ad = new ContentListAdapter(context, parent);
		elv.setAdapter(ad);
		elv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (jsonRes == null) {
					Log.d(App.LOG_TAG, "attributes geted failed!");
					return;
				}
				Intent i = new Intent(context,AssetsCameraFlowActivity.class);
				i.putExtra("id", id);
				i.putExtra("data", jsonRes.toString());
				i.putExtra("type", type);
				i.putExtra("typeName", parent[position]);
				startActivity(i);
			}
		});

	}
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
//				Log.i(App.LOG_TAG, url.toString());
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
//									System.out.println(json.getJSONArray("data"));
									jsonRes = json.getJSONArray("data");
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


	private OnClickListener btnlr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_imgshow:
				Intent i1 = new Intent(context, DSCommonSlideActivity.class);
				i1.putExtra("type", type);
				i1.putExtra("id", id);
				startActivity(i1);
				break;
			case R.id.btn_imgupload:
				uploadImgs();
				break;
			default:
				break;
			}
		}
		
	};
	protected void uploadImgs() {
		List<AssetsPhotosModel> list = DAOAssetsPhoto.getInstance(context)
				.searchAssetsDsId(id,false);
		if (list != null && list.size() > 0) {
			if(list.size()<10){
				toast("每个基站必须上传不少于10张照片");
			}else{
				FuncSubAssetsPhotos func = new FuncSubAssetsPhotos(context);
				func.subData(id, fileType, list);
			}
		} else {
			toast("您没有本地缓存未上传的图片");
		}
	}
	private void toast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	private void findView() {
		tvName = (TextView) findViewById(R.id.txt_name_assets);
		tvDate = (TextView) findViewById(R.id.txt_date_assets);
		tvUser = (TextView) findViewById(R.id.txt_user_assets);
		elv = (ListView) findViewById(R.id.elv_assets);
		btnImgShow = (Button) findViewById(R.id.btn_imgshow);
		btnImgUpload = (Button) findViewById(R.id.btn_imgupload);
		tvState = (TextView) findViewById(R.id.txt_state_assets);
	}

}
