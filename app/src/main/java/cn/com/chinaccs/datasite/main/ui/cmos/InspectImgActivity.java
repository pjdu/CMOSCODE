package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.datasite.function.ImageLoadTask;
import cn.com.chinaccs.datasite.main.widget.pager.HorizontalPager;


/**
 * @author Fddi
 * 
 */
public class InspectImgActivity extends BaseActivity {

	private Context context;
	private ImageButton btnUpload;
	private HorizontalPager pager;
	private TextView tvDes;
	private ProgressBar pb;
	private int type;
	private String id;
	private final String fileType = "1";
	private List<Map<String, String>> listData;
	private List<ImageView> listImgs;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_upload,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		if (id==R.id.menu_upload){
			Toast.makeText(context,"该功能暂未开发",Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ds_slide);
		initToolbar("浏览所有图片");
		context = this;
		type = getIntent().getIntExtra("type", 0);
		id = getIntent().getStringExtra("id");
		this.findViews();
		
		this.getFileList();
		//btnUpload.setVisibility(View.GONE);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		listData = null;
		listImgs = null;
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
		if (resultCode == RESULT_OK) {
			getFileList();
		}
	}

	private void getFileList() {
		final ProgressDialog pd = App.progressDialog(context, context
				.getResources().getString(R.string.common_response));
		pd.show();
		final Handler hr = new Handler();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
				String userId = share
						.getString(AppCheckLogin.SHARE_USER_ID, "");
				String sign = App.signMD5(DataSiteStart.HTTP_KEYSTORE + userId
						+ fileType + type + id);
				String urlStr = DataSiteStart.HTTP_SERVER_URL
						+ "FileList.do?userid=" + userId + "&filetype="
						+ fileType + "&dstype=" + type + "&dsid=" + id
						+ "&sign=" + sign;
				AppHttpConnection conn = new AppHttpConnection(context, urlStr);
				final String result = conn.getConnectionResult();
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
									buildImageList(json.getJSONArray("data"));
								} else {
									toast(msg);
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		}.start();
	}

	private void buildImageList(JSONArray json) {
		pager.removeAllViews();
		listData = null;
		listData = new ArrayList<Map<String, String>>();
		listImgs = new ArrayList<ImageView>();
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		for (int i = 0; i < json.length(); i++) {
			try {
				JSONArray data = json.getJSONArray(i);
				String url = data.getString(2);
				if (url != null && !url.equals("")) {
					ImageView img = new ImageView(context);
					img.setLayoutParams(lp);
					pager.addView(img);
					Map<String, String> map = new HashMap<String, String>();
					map.put("url", url);
					String des = data.getString(3) + "\n上传人："
							+ data.getString(4) + "\n日期：" + data.getString(5);
					map.put("des", des);
					map.put("load", "0");
					listData.add(map);
					listImgs.add(img);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (listData != null && listImgs != null && listData.size() > 0) {
			tvDes.setText("【1/" + listData.size() + "】  ");
			tvDes.append(listData.get(0).get("des"));
			new ImageLoadTask(listImgs.get(0), pb, tvDes).execute(listData.get(
					0).get("url"));
		}
		HorizontalPager.OnScrollListener sl = new HorizontalPager.OnScrollListener() {

			@Override
			public void onViewScrollFinished(int currentPage) {
				// TODO Auto-generated method stub
				if (listData != null && listImgs != null
						&& listData.size() > currentPage) {
					tvDes.setText("【" + (currentPage + 1) + "/"
							+ listData.size() + "】  ");
					tvDes.append(listData.get(currentPage).get("des"));
					String lc = listData.get(currentPage).get("load");
					if (lc.equals("0")) {
						new ImageLoadTask(listImgs.get(currentPage), pb, tvDes)
								.execute(listData.get(currentPage).get("url"));
						listData.get(currentPage).put("load", "1");
					}
				}
			}

			@Override
			public void onScroll(int scrollX) {
				// TODO Auto-generated method stub

			}
		};
		pager.removeOnScrollListener(sl);
		pager.addOnScrollListener(sl);
		pager.setCurrentPage(0);
	}

	private void toast(String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	private void findViews() {
		pager = (HorizontalPager) findViewById(R.id.pager);
		//btnUpload = (ImageButton) findViewById(R.id.btn_upload);
		tvDes = (TextView) findViewById(R.id.tv_img_des);
		pb = (ProgressBar) findViewById(R.id.pb_state);
	}
}
