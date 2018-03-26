package cn.com.chinaccs.datasite.main.ui.cmos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.datasite.database.DBInspectHandler;
import cn.com.chinaccs.datasite.main.datasite.database.IDBHandler;
import cn.com.chinaccs.datasite.main.datasite.function.FuncBuildGrid;
import cn.com.chinaccs.datasite.main.datasite.function.FuncMenu;
import cn.com.chinaccs.datasite.main.ui.LoginActivity;
import cn.com.chinaccs.datasite.main.widget.PromptDailog;
import cn.com.chinaccs.datasite.main.widget.pager.HorizontalPager;
import cn.com.chinaccs.datasite.main.widget.pager.PagerControl;


/**
 * @author fddi
 * 
 */
public class DataSiteActivity extends Activity {
	private Context context;
	private HorizontalPager pager;
	private PagerControl control;
	private TextView txt0;
	private TextView txt1;
	private TextView txt2;
	private TextView txt3;
	private GridView gridIt;
	private GridView gridSite;
	private GridView gridProduct;
//	private GridView gridBox;
	private GridView gridAgent;
	private ImageButton btnMore;
	public static int RESTART_CODE = 0;
	/**
	 * 退出code
	 */
	public static final int CODE_EXIT = 1;
	/**
	 * 重新登录code
	 */
	public static final int CODE_RELOGIN = 2;

	// 退出时间
	private long currentBackPressedTime = 0;
	// 退出间隔
	private static final int BACK_PRESSED_INTERVAL = 3000;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 截取返回按钮事件，弹出提示框
			if ((System.currentTimeMillis() - currentBackPressedTime) > BACK_PRESSED_INTERVAL) {
				currentBackPressedTime = System.currentTimeMillis();
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			} else {
				finish();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.activity_datasite);
		this.findViews();
		this.buildPager();
		this.buildGrids();
		btnMore.setOnClickListener(moreListener);
		this.checkGPS();
		this.deleteCache();
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
		switch (RESTART_CODE) {
		case CODE_EXIT:
			RESTART_CODE = 0;
			finish();
			break;
		case CODE_RELOGIN:
			RESTART_CODE = 0;
			finish();
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
	}

	private OnClickListener moreListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			FuncMenu menu = new FuncMenu(context);
			menu.buildMenu(btnMore);
		}
	};

	private void buildPager() {
		control.setNumPages(pager.getChildCount());

		pager.addOnScrollListener(new HorizontalPager.OnScrollListener() {
			public void onScroll(int scrollX) {
				// Log.d("TestActivity", "scrollX=" + scrollX);
				float scale = (float) (pager.getPageWidth() * pager
						.getChildCount()) / (float) control.getWidth();
				control.setPosition((int) (scrollX / scale));
			}

			public void onViewScrollFinished(int currentPage) {
				control.setCurrentPage(currentPage);
				switch (currentPage) {
				case 0:
					txt0.setTextColor(getResources().getColor(R.color.main_app));
					txt1.setTextColor(getResources().getColor(R.color.white));
					txt2.setTextColor(getResources().getColor(R.color.white));
					txt3.setTextColor(getResources().getColor(R.color.white));
					break;
				case 1:
					txt1.setTextColor(getResources().getColor(R.color.main_app));
					txt0.setTextColor(getResources().getColor(R.color.white));
					txt2.setTextColor(getResources().getColor(R.color.white));
					txt3.setTextColor(getResources().getColor(R.color.white));
					break;
				case 2:
					txt2.setTextColor(getResources().getColor(R.color.main_app));
					txt0.setTextColor(getResources().getColor(R.color.white));
					txt1.setTextColor(getResources().getColor(R.color.white));
					txt3.setTextColor(getResources().getColor(R.color.white));
					break;
				case 3:
					txt3.setTextColor(getResources().getColor(R.color.main_app));
					txt0.setTextColor(getResources().getColor(R.color.white));
					txt1.setTextColor(getResources().getColor(R.color.white));
					txt2.setTextColor(getResources().getColor(R.color.white));
					break;
				default:
					break;
				}
			}
		});
		OnClickListener lr = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.txt_page_0:
					pager.snapToPage(0);
					break;
				case R.id.txt_page_1:
					pager.snapToPage(1);
					break;
				case R.id.txt_page_2:
					pager.snapToPage(2);
					break;
				case R.id.txt_page_3:
					pager.snapToPage(3);
					break;
				default:
					break;
				}
			}
		};
		txt0.setOnClickListener(lr);
		txt1.setOnClickListener(lr);
		txt2.setOnClickListener(lr);
		txt3.setOnClickListener(lr);
	}

	private void buildGrids() {
		// 巡检列表
		final int[] itItems = { R.string.it_sign,
				// R.string.it_bs,
				R.string.it_bs_new,
				R.string.it_record,
				// R.string.it_trouble,
				R.string.it_bs_new_info};
		final int[] itImgs = { R.drawable.ic_item_pen,
				// R.drawable.ic_item_cell_it,
				R.drawable.ic_item_cell_it,
				R.drawable.ic_item_history,
				// R.drawable.ic_item_trouble,
				R.drawable.ic_item_cell};
		final Intent[] itIntent = {
				new Intent(context, InspectSignActivity.class),
				// new Intent(context, InspectBSActivity.class),
				new Intent(context, NewInspectBSActivity.class),
				new Intent(context, InspectHistoryActivity.class),
				// new Intent(context, SecutityHiddenActivity.class),
				new Intent(context, BSDataSiteInfoActivity.class)};
		FuncBuildGrid itBg = new FuncBuildGrid(context, gridIt);
		itBg.buildGrid(itItems, itImgs);
		itBg.attachEvent(itIntent);

		// 数据站 列表
		final int[] siteItems = { 
//				R.string.ds_cdma_basestation,
//				R.string.ds_cdma_cell, R.string.ds_cdma_indoor,
//				R.string.ds_cdma_repeater, R.string.ds_bericher,
//				R.string.ds_wifi_hot, R.string.ds_pinset, R.string.lte_bs,
//				R.string.lte_cell, R.string.ds_cdma_real_basestation,
//				R.string.maintain_resource
				R.string.assets_main
				};
		final int[] siteImgs = { 
//				R.drawable.ic_item_cell,
//				R.drawable.ic_item_sq, R.drawable.ic_item_sf,
//				R.drawable.ic_item_zf, R.drawable.ic_item_zft,
//				R.drawable.ic_item_rd, R.drawable.ic_item_pinset,
//				R.drawable.ic_item_lte_bs, R.drawable.ic_item_lte_cell,
//				R.drawable.ic_item_cell, R.drawable.ic_ziyuanpeizhi,
				R.drawable.ic_item_cell
				};
		final Intent[] siteIntent = {
//				new Intent(context, DSCdmaBaseStationActivity.class),
//				new Intent(context, DSCdmaCellActivity.class),
//				new Intent(context, DSCdmaIndoorActivity.class),
//				new Intent(context, DSCdmaRepeaterActivity.class), null,
//				new Intent(context, DSWiFiHotActivity.class),
//				new Intent(context, DSInsetActivity.class), 
//				new Intent(context, DSLTEBaseStationActivity.class), 
//				new Intent(context, DSLTECellActivity.class),
//				new Intent(context, DSCdmaRealBSActivity.class),
//				new Intent(context, AgentUserListActivity.class) 
				new Intent(context, AssetsInfoActivity.class)
				};
		FuncBuildGrid siteBg = new FuncBuildGrid(context, gridSite);
		siteBg.buildGrid(siteItems, siteImgs);
		siteBg.attachEvent(siteIntent);

		// 知识库 列表
		final int[] productItems = { R.string.know_master,
				R.string.know_assort, R.string.know_iquery, R.string.know_qa,
				R.string.suggest };
		final int[] productImgs = { R.drawable.ic_item_master,
				R.drawable.ic_item_assort, R.drawable.ic_item_qy,
				R.drawable.ic_item_know, R.drawable.ic_item_suggest };
		Intent ipdMain = new Intent(context, ProductMainActivity.class);
		ipdMain.putExtra("type", DataSiteStart.TYPE_EQ_MAIN);
		Intent ipdAssort = new Intent(context, ProductMainActivity.class);
		ipdAssort.putExtra("type", DataSiteStart.TYPE_EQ_ASSORT);
		Intent ipdModuleIndex = new Intent(context,
				ProductModelIndexActivity.class);
		//Intent ipdKnow = new Intent(context, QAQuestionActivity.class);
		Intent ipdKnow = new Intent(context, QAQuestionMainActivity.class);
		Intent ipdSuggest = new Intent(context, TMobileSuggestActivity.class);
		final Intent[] pdIntent = { ipdMain, ipdAssort, ipdModuleIndex,
				ipdKnow, ipdSuggest };
		FuncBuildGrid productBg = new FuncBuildGrid(context, gridProduct);
		productBg.buildGrid(productItems, productImgs);
		productBg.attachEvent(pdIntent);

		// 工具箱 列表
		final int[] boxItems = { R.string.box_unitc, R.string.box_calculator,
				R.string.box_azimuth, R.string.box_downtilt, R.string.box_lac,
				R.string.box_gps };
		final int[] boxImgs = { R.drawable.ic_item_unitc,
				R.drawable.ic_item_calculator, R.drawable.ic_item_fwj,
				R.drawable.ic_item_xqj, R.drawable.ic_item_loc,
				R.drawable.ic_item_gps };
//		final int[] boxItems = { R.string.maintain_resource, R.string.maintain_capacity,
//				R.string.maintain_orders, R.string.maintain_assess };
//		final int[] boxImgs = { R.drawable.ic_ziyuanpeizhi,
//				R.drawable.ic_daiweizizhi, R.drawable.ic_gongdan,
//				R.drawable.ic_daiweikaohe };
		FuncBuildGrid boxBg = new FuncBuildGrid(context, gridAgent);
		boxBg.buildGrid(boxItems, boxImgs);
		Intent cit = new Intent();
		cit.setClassName("com.android.calculator2",
				"com.android.calculator2.Calculator");
		Intent[] boxIntent = {
				new Intent(context, BoxConversionActivity.class), cit,
				new Intent(context, BoxCompassActivity.class),
				new Intent(context, BoxDowntiltActivity.class),
				new Intent(context, BoxMapActivity.class),
				new Intent(context, GPSConnActivity.class) };
//		Intent[] boxIntent = {
//				new Intent(context, AgentUserListActivity.class),
//				null, null, null };
		boxBg.attachEvent(boxIntent);
	}

	private void findViews() {
		control = (PagerControl) findViewById(R.id.control);
		pager = (HorizontalPager) findViewById(R.id.pager);
		txt0 = (TextView) findViewById(R.id.txt_page_0);
		txt1 = (TextView) findViewById(R.id.txt_page_1);
		txt2 = (TextView) findViewById(R.id.txt_page_2);
		txt3 = (TextView) findViewById(R.id.txt_page_3);
		gridIt = (GridView) findViewById(R.id.grid_inspect);
		gridSite = (GridView) findViewById(R.id.grid_site);
		gridProduct = (GridView) findViewById(R.id.grid_product);
		gridAgent = (GridView) findViewById(R.id.grid_knowledge);
		btnMore = (ImageButton) findViewById(R.id.btn_more);
	}

	private void checkGPS() {
		if (!App.isGpsOpen(context)) {
			PromptDailog prompt = new PromptDailog(context, null,
					"GPS定位功能未开启，为了使用本软件相关功能，请打开手机GPS功能！", "设置",
					new PromptDailog.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent i = new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(i);
						}
					});
			prompt.show();
		}
	}

	private void deleteCache() {
		SharedPreferences share = getSharedPreferences(App.SHARE_TAG, 0);
		String dateItem = share.getString(
				DataSiteStart.SHARE_DATEITEM_CLEAR_IT_CACHE, "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
		if (!dateItem.equals(sdf.format(new Date()))) {
			DBInspectHandler dbh = new DBInspectHandler(context,
					IDBHandler.MODE_WRITE_DATABASE);
			dbh.clearDayCache();
		}
	}
}
