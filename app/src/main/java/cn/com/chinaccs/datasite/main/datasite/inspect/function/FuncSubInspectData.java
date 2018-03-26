package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;
import cn.com.chinaccs.datasite.main.common.AppCheckLogin;
import cn.com.chinaccs.datasite.main.common.AppHttpClient;
import cn.com.chinaccs.datasite.main.common.AppHttpConnection;
import cn.com.chinaccs.datasite.main.common.WGSTOGCJ02;
import cn.com.chinaccs.datasite.main.datasite.database.DBInspectHandler;
import cn.com.chinaccs.datasite.main.datasite.database.IDBHandler;
import cn.com.chinaccs.datasite.main.datasite.function.BDGeoLocation;
import cn.com.chinaccs.datasite.main.datasite.function.FuncUploadFile;
import cn.com.chinaccs.datasite.main.ui.MainActivity;
import cn.com.chinaccs.datasite.main.ui.cmos.InspectSignActivity;

/**
 * @author Fddi
 *
 */
public class FuncSubInspectData {
	private Context context;
	private PopupWindow pop;
	private View popState;
	private ProgressBar pb;
	private TextView textState;
	private Button btnClose;
	private Button btnHome;
	private LinearLayout layoutAction;
	private Button btnPlan;
	private static final String DATA_TYPE = "101";

	public FuncSubInspectData(Context context) {
		this.context = context;
	}
	/**
	 *
	 * @param isRRu
	 * @param cellId
	 * @param desc
	 * @param bsId
	 * @param bsName
	 * @param bsLongitude
	 * @param bsLatitude
	 * @param lng
	 * @param lat
	 * @param planId
	 * @param inspectionType add by wuhua for 用物理基站进行巡检; 基站巡检进去的填1，新基站巡检进去的填2 ended by wuhua 20151104
	 * @param list
	 * @param imgPaths
	 */
	public void subData(Boolean isRRu, String cellId, String desc, String bsId,
			String bsName, String bsLongitude, String bsLatitude, Double lng,
			Double lat, Long planId,String inspectionType, List<PlanItem> list, String[] imgPaths) {
		if (list == null) {
			Toast.makeText(context, "获取巡检项失败！", Toast.LENGTH_LONG).show();
			return;
		}
		popStateFindViews();
		pop = new PopupWindow(popState, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		pop.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.draw_bg_opactiy));
		pop.setFocusable(true);
		// pop.setAnimationStyle(R.style.style_popupAnim);
		pop.showAtLocation(((Activity) context).findViewById(R.id.main),
				Gravity.CENTER, 0, 0);
		pop.update();
		// changed by wuhua for
		// 用物理基站进行巡检
		// 基站巡检进去的填1，新基站巡检进去的填2
		// this.subExcute(isRRu, cellId, desc, bsId, bsName, bsLongitude,
		//		bsLatitude, lng, lat, planId, list, imgPaths);
		this.subExcute(isRRu, cellId, desc, bsId, bsName, bsLongitude,
				bsLatitude, lng, lat, planId, inspectionType, list, imgPaths);
		// ended by wuhua 20151104
	}

	//执行隐患提交
	public void subData(Boolean isRRu, String cellId, String desc, String bsId,
						String bsName, String bsLongitude, String bsLatitude, Double lng,
						Double lat, Long planId,Long toubleId, List<PlanItem> list, String[] imgPaths) {
		if (list == null) {
			Toast.makeText(context, "获取巡检项失败！", Toast.LENGTH_LONG).show();
			return;
		}
		popStateFindViews();
		pop = new PopupWindow(popState, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		pop.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.draw_bg_opactiy));
		pop.setFocusable(true);
		// pop.setAnimationStyle(R.style.style_popupAnim);
		pop.showAtLocation(((Activity) context).findViewById(R.id.main),
				Gravity.CENTER, 0, 0);
		pop.update();
		this.subExcute(isRRu, cellId, desc, bsId, bsName, bsLongitude,
				bsLatitude, lng, lat, planId,toubleId,list, imgPaths);
	}

	/**
	 *
	 * @param isRRu
	 * @param cellId
	 * @param desc
	 * @param bsId
	 * @param bsName
	 * @param bsLongitude
	 * @param bsLatitude
	 * @param lng
	 * @param lat
	 * @param planId
	 * @param inspectionType add by wuhua for 用物理基站进行巡检; 基站巡检进去的填1，新基站巡检进去的填2 ended by wuhua 20151104
	 * @param list
	 * @param imgPaths
	 */
	private void subExcute(Boolean isRRu, String cellId, String desc,
			String bsId, final String bsName, final String bsLongitude,
			final String bsLatitude, final Double lng, final Double lat,
			Long planId, String inspectionType, List<PlanItem> list, final String[] imgPaths) {
		final StringBuffer sr = new StringBuffer("提交巡检数据中...");
		textState.setText(sr.toString());
		pb.setProgress(1);
		final JSONObject json = new JSONObject();
		try {
			json.put("isRRu", isRRu);
			if (isRRu) {
				json.put("cellId", cellId);
			}
			json.put("bsId", bsId);
			json.put("planId", planId);
			// add by wuhua for
			// 用物理基站进行巡检
			// 基站巡检进去的填1，新基站巡检进去的填2
			json.put("inspectionType", inspectionType);
			// ended by wuhua 20151104
			json.put("desc", desc);
			JSONArray array = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				JSONArray data = new JSONArray();
				PlanItem pi = list.get(i);
				data.put(pi.getPlanItemId());
				data.put(pi.getState());
				if (pi.getEt() != null) {
					data.put(pi.getEt().getText().toString());
				} else {
					data.put("");
				}
				array.put(data);
			}
			json.put("items", array);
			final ICHandler hr = new ICHandler(context, pb, textState,
					layoutAction, bsId, isRRu, cellId, planId);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message m;
					m = hr.obtainMessage(2, "获取定位信息..");
					hr.sendMessage(m);
					if (lng == null || lng == 4.9E-324 || lat == null) {
						m = hr.obtainMessage(501, "定位失败！请开启GPS和无线网络后重试");
						hr.sendMessage(m);
						return;
					}
                    double dv = 0;
                    if (bsLongitude.equals("") || bsLatitude.equals("")){
                        dv = BDGeoLocation.getShortDistance(lng, lat,
                                Double.valueOf(lng),
                                Double.valueOf(lat));
                    } else {
                        dv = BDGeoLocation.getShortDistance(lng, lat,
                                Double.valueOf(bsLongitude),
                                Double.valueOf(bsLatitude));
                    }
					BigDecimal bdl = new BigDecimal(dv);
					dv = bdl.setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue();
					WGSTOGCJ02 wg = new WGSTOGCJ02();
					Map<String, Double> wgloc = wg.transform(lng, lat);
					SharedPreferences share = context.getSharedPreferences(
							App.SHARE_TAG, 0);
					String userid = share.getString(
							AppCheckLogin.SHARE_USER_ID, "");
					try {
						json.put("userid", userid);
						json.put("longitude", String.valueOf(wgloc.get("lon")));
						json.put("latitude", String.valueOf(wgloc.get("lat")));
						json.put("range", String.valueOf(dv));
						m = hr.obtainMessage(3, "定位成功");
						hr.sendMessage(m);
						m = hr.obtainMessage(4, "上传数据中...");
						hr.sendMessage(m);
						String subRes = submitToServer(json);
						if (subRes.equals(AppHttpClient.RESULT_FAIL)) {
							m = hr.obtainMessage(501, "上传数据失败！请检查手机网络是否有效");
							hr.sendMessage(m);
							return;
						}

						JSONObject jr = new JSONObject(subRes);
						if (("-1").equals(jr.getString("result"))) {
							m = hr.obtainMessage(501, jr.getString("msg"));
							hr.sendMessage(m);
							return;
						}
						m = hr.obtainMessage(5, "上传成功");
						hr.sendMessage(m);
						m = hr.obtainMessage(6, "上传图片中...");
						hr.sendMessage(m);
						String infoid = jr.getString("infoid");
						if (imgPaths == null) {
							m = hr.obtainMessage(200, "恭喜，提交巡检数据成功！");
							hr.sendMessage(m);
							return;
						}
						FuncUploadFile ff = new FuncUploadFile(context);
						for (int i = 0; i < imgPaths.length; i++) {
							if (imgPaths[i] != null) {
								String res = ff.uploadFileToServer(imgPaths[i],
										"1", "巡检-" + bsName, DATA_TYPE, "",
										infoid, DataSiteStart.HTTP_SERVER_URL,
										DataSiteStart.HTTP_KEYSTORE);
								if (res.equals(AppHttpConnection.RESULT_FAIL)) {
									m = hr.obtainMessage(201, "上传图片："
											+ imgPaths[i] + "--失败");
									hr.sendMessage(m);
								} else {
									JSONObject imgjr = new JSONObject(res);
									m = hr.obtainMessage(201,
											"上传图片：" + imgPaths[i] + "--"
													+ imgjr.getString("msg"));
									hr.sendMessage(m);
									String resCode = imgjr.getString("result");
									if (resCode.equals("1")
											&& imgPaths[i] != null) {
										File file = new File(imgPaths[i]);
										file.delete();// 删除临时文件
									}
								}
							}
						}
						m = hr.obtainMessage(200, "恭喜，提交巡检数据成功！");
						hr.sendMessage(m);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//执行隐患提交
	private void subExcute(Boolean isRRu, String cellId, String desc,
						   String bsId, final String bsName, final String bsLongitude,
						   final String bsLatitude, final Double lng, final Double lat,
						   Long planId,Long troubleId, List<PlanItem> list, final String[] imgPaths) {
		final StringBuffer sr = new StringBuffer("提交巡检数据中...");
		textState.setText(sr.toString());
		pb.setProgress(1);
		final JSONObject json = new JSONObject();
		try {
			json.put("isRRu", isRRu);
			if (isRRu) {
				json.put("cellId", cellId);
			}
			json.put("bsId", bsId);
			json.put("planId", planId);
			json.put("desc", desc);
			json.put("troubleid", troubleId);
			JSONArray array = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				JSONArray data = new JSONArray();
				PlanItem pi = list.get(i);
				data.put(pi.getPlanItemId());
				data.put(pi.getState());
				if (pi.getEt() != null) {
					data.put(pi.getEt().getText().toString());
				} else {
					data.put("");
				}
				array.put(data);
			}
			json.put("items", array);
			final ICHandler hr = new ICHandler(context, pb, textState,
					layoutAction, bsId, isRRu, cellId, planId);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message m;
					m = hr.obtainMessage(2, "获取定位信息..");
					hr.sendMessage(m);
					if (lng == null || lng == 4.9E-324 || lat == null) {
						m = hr.obtainMessage(501, "定位失败！请开启GPS和无线网络后重试");
						hr.sendMessage(m);
						return;
					}

					double dv = BDGeoLocation.getShortDistance(lng, lat,
							Double.valueOf(bsLongitude),
							Double.valueOf(bsLatitude));
					BigDecimal bdl = new BigDecimal(dv);
					dv = bdl.setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue();
					WGSTOGCJ02 wg = new WGSTOGCJ02();
					Map<String, Double> wgloc = wg.transform(lng, lat);
					SharedPreferences share = context.getSharedPreferences(
							App.SHARE_TAG, 0);
					String userid = share.getString(
							AppCheckLogin.SHARE_USER_ID, "");
					try {
						json.put("userid", userid);
						json.put("longitude", String.valueOf(wgloc.get("lon")));
						json.put("latitude", String.valueOf(wgloc.get("lat")));
						json.put("range", String.valueOf(dv));
						m = hr.obtainMessage(3, "定位成功");
						hr.sendMessage(m);
						m = hr.obtainMessage(4, "上传数据中...");
						hr.sendMessage(m);
						String subRes = submitTroubleToServer(json);
						if (subRes.equals(AppHttpClient.RESULT_FAIL)) {
							m = hr.obtainMessage(501, "上传数据失败！请检查手机网络是否有效");
							hr.sendMessage(m);
							return;
						}
						JSONObject jr = new JSONObject(subRes);
						if (("-1").equals(jr.getString("result"))) {
							m = hr.obtainMessage(501, jr.getString("msg"));
							hr.sendMessage(m);
							return;
						}
						m = hr.obtainMessage(5, "上传成功");
						hr.sendMessage(m);
						m = hr.obtainMessage(6, "上传图片中...");
						hr.sendMessage(m);
						String infoid = jr.getString("infoid");
						if (imgPaths == null) {
							m = hr.obtainMessage(200, "恭喜，提交巡检数据成功！");
							hr.sendMessage(m);
							return;
						}
						FuncUploadFile ff = new FuncUploadFile(context);
						for (int i = 0; i < imgPaths.length; i++) {
							if (imgPaths[i] != null) {
								String res = ff.uploadFileToServer(imgPaths[i],
										"1", "巡检-" + bsName, DATA_TYPE, "",
										infoid, DataSiteStart.HTTP_SERVER_URL,
										DataSiteStart.HTTP_KEYSTORE);
								if (res.equals(AppHttpConnection.RESULT_FAIL)) {
									m = hr.obtainMessage(201, "上传图片："
											+ imgPaths[i] + "--失败");
									hr.sendMessage(m);
								} else {
									JSONObject imgjr = new JSONObject(res);
									m = hr.obtainMessage(201,
											"上传图片：" + imgPaths[i] + "--"
													+ imgjr.getString("msg"));
									hr.sendMessage(m);
									String resCode = imgjr.getString("result");
									if (resCode.equals("1")
											&& imgPaths[i] != null) {
										File file = new File(imgPaths[i]);
										file.delete();// 删除临时文件
									}
								}
							}
						}
						m = hr.obtainMessage(200, "恭喜，提交巡检数据成功！");
						hr.sendMessage(m);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String submitToServer(JSONObject json) {
		String result = AppHttpClient.RESULT_FAIL;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		url.append("InspectInfoSub.do");
		AppHttpClient conn = new AppHttpClient(context);
		try {
			String str = URLEncoder.encode(json.toString(), App.ENCODE_UTF8);
			result = conn.getResultByPOST(url.toString(), str);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String submitTroubleToServer(JSONObject json) {
		String result = AppHttpClient.RESULT_FAIL;
		StringBuffer url = new StringBuffer(DataSiteStart.HTTP_SERVER_URL);
		url.append("HiddenTroubleInfoSub.do");
		AppHttpClient conn = new AppHttpClient(context);
		Log.e("submitTroubleToServer", ""+url);
		try {
			String str = URLEncoder.encode(json.toString(), App.ENCODE_UTF8);
			result = conn.getResultByPOST(url.toString(), str);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private void popStateFindViews() {
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popState = li.inflate(R.layout.pop_updata_state, null);
		pb = (ProgressBar) popState.findViewById(R.id.pb_state);
		textState = (TextView) popState.findViewById(R.id.text_state);
		btnClose = (Button) popState.findViewById(R.id.btn_pop_close);
		btnHome = (Button) popState.findViewById(R.id.btn_pop_home);
		btnPlan = (Button) popState.findViewById(R.id.btn_pop_plan);
		layoutAction = (LinearLayout) popState.findViewById(R.id.layout_action);
		pb.setMax(5);
		layoutAction.setVisibility(View.GONE);
		OnClickListener lr = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.btn_pop_close:
					if (pop != null)
						pop.dismiss();
					break;
				case R.id.btn_pop_home:
					((Activity) context).finish();
					context.startActivity(new Intent(context,
							MainActivity.class));
					break;
				case R.id.btn_pop_plan:
					((Activity) context).finish();
					break;
				}
			}
		};
		btnClose.setOnClickListener(lr);
		btnHome.setOnClickListener(lr);
		btnPlan.setOnClickListener(lr);
	}

	/**
	 * @author Fddi
	 *
	 */
	static class ICHandler extends Handler {
		private ProgressBar pb;
		private TextView textState;
		private Context context;
		private LinearLayout layout;
		private String bsId;
		private Boolean isRRu;
		private String cellId;
		private Long planId;

		public ICHandler(Context context, ProgressBar pb, TextView textState,
				LinearLayout layout, String bsId, Boolean isRRu, String cellId,
				Long planId) {
			this.context = context;
			this.pb = pb;
			this.textState = textState;
			this.layout = layout;
			this.bsId = bsId;
			this.isRRu = isRRu;
			this.cellId = cellId;
			this.planId = planId;
		}

		private void saveRecodes() {
			DBInspectHandler dbh = new DBInspectHandler(context,
					IDBHandler.MODE_WRITE_DATABASE);
			List<String> list = new ArrayList<String>();
			list.add(bsId);
			list.add(String.valueOf(isRRu));
			list.add(cellId);
			list.add(String.valueOf(planId));
			dbh.dbSaveInpectRecodes(list);
			dbh.closeDataBase();
		}

		private void clearSign() {
			SharedPreferences share = context.getSharedPreferences(
					App.SHARE_TAG, 0);
			share.edit().putFloat(InspectSignActivity.SHARE_SIGN_LNG, 0)
					.commit();
			share.edit().putFloat(InspectSignActivity.SHARE_SIGN_LAT, 0)
					.commit();
			share.edit().putLong(InspectSignActivity.SHARE_SIGN_DATE, 0)
					.commit();
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			String reslutInfo = (String) msg.obj;
			Log.d(App.LOG_TAG, reslutInfo);
			switch (msg.what) {
			case 200:
				pb.setProgress(7);
				textState.append("\n");
				textState.append(reslutInfo);
				layout.setVisibility(View.VISIBLE);
				clearSign();
				saveRecodes();
				break;
			case 201:
				textState.append("\n");
				textState.append(reslutInfo);
				break;
			case 501:
				pb.setProgress(7);
				textState.append("\n");
				textState.append(reslutInfo);
				break;
			default:
				textState.append("\n");
				textState.append(reslutInfo);
				pb.setProgress(msg.what);
				break;
			}
		}
	}
}
