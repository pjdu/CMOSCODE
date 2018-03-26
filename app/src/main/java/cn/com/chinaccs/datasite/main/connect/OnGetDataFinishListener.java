package cn.com.chinaccs.datasite.main.connect;

import org.json.JSONObject;

import java.util.List;

public interface OnGetDataFinishListener {
	public void onFinished(String output);
	public void onFinishJSON(JSONObject output);
	public void onFinishList(List list);
}
