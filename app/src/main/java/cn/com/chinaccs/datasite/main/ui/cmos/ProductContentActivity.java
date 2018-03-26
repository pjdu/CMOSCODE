package cn.com.chinaccs.datasite.main.ui.cmos;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;
import cn.com.chinaccs.datasite.main.datasite.function.ImageLoadTask;

/**
 * @author Fddi
 * 
 */
public class ProductContentActivity extends BaseActivity {
	private Context context;
	// private TextView tvTitle;
	private TextView tvSize;
	private ImageView imgStructure;
	private ImageView imgAppearance;
	private TextView tvPerformance;
	private TextView tvFeature;
	private TextView tvMaintenance;
	private String array;
	private Button btnQueryOffer;
	private String productId;
	private TextView tvUserName;
	private TextView tvCreateTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_content);
		this.context = this;
		this.findViews();
		Bundle be = getIntent().getExtras();
		String title = be.getString("title");
		initToolbar(title);
		// tvTitle.setText(title);
		array = be.getString("array");
		this.buildItems();
		btnQueryOffer.setOnClickListener(lr);
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

	private OnClickListener lr = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle be = new Bundle();
			be.putString("productId", productId);
			Intent i = new Intent(context, ProductOfferListActivity.class);
			i.putExtras(be);
			startActivity(i);
		}
	};

	private void buildItems() {
		try {
			JSONArray ja = new JSONArray(array);
			productId = ja.getString(0);
			tvSize.setText(Html.fromHtml(ja.getString(1)));
			tvPerformance.setText(Html.fromHtml(ja.getString(4)));
			tvFeature.setText(Html.fromHtml(ja.getString(5)));
			tvMaintenance.setText(Html.fromHtml(ja.getString(6)));
			tvUserName.setText(Html.fromHtml(ja.getString(7)));
			tvCreateTime.setText(Html.fromHtml(ja.getString(8)));
			String urls = ja.getString(2);
			if (urls != null && !urls.equals("")) {
				new ImageLoadTask(imgStructure, null, null).execute(urls);
			}
			String urla = ja.getString(3);
			if (urla != null && !urla.equals("")) {
				new ImageLoadTask(imgAppearance, null, null).execute(urla);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void findViews() {
		// tvTitle = (TextView) findViewById(R.id.txt_title);
		tvSize = (TextView) findViewById(R.id.txt_pd_size);
		tvPerformance = (TextView) findViewById(R.id.txt_pd_performance);
		tvFeature = (TextView) findViewById(R.id.txt_pd_feature);
		tvMaintenance = (TextView) findViewById(R.id.txt_pd_maintenance);
		imgStructure = (ImageView) findViewById(R.id.img_pd_structure);
		imgAppearance = (ImageView) findViewById(R.id.img_pd_appearance);
		btnQueryOffer = (Button) findViewById(R.id.btn_query_offer);
		tvUserName = (TextView) findViewById(R.id.txt_pd_user_name);
		tvCreateTime = (TextView) findViewById(R.id.txt_pd_create_time);
	}
}
