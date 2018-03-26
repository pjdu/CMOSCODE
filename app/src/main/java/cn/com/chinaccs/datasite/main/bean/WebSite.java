package cn.com.chinaccs.datasite.main.bean;

import java.io.Serializable;

public class WebSite implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String STATE_WAIT = "Wait";
	public static final String STATE_TEST = "Test";
	public static final String STATE_FINISH = "Finish";
	public static final String STATE_ERROR = "Error";
	private int id;
	private String Name;
	private String url;
	private int iconRes;
	private boolean isChecked;
	private long ping = -1l;
	private long speed = -1;
	private int classify;
	private String state;

	public WebSite() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public long getPing() {
		return ping;
	}

	public void setPing(long ping) {
		this.ping = ping;
	}

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public int getClassify() {
		return classify;
	}

	public void setClassify(int classify) {
		this.classify = classify;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public static String getStateWait() {
		return STATE_WAIT;
	}

	public static String getStateTest() {
		return STATE_TEST;
	}

	public static String getStateFinish() {
		return STATE_FINISH;
	}

	public static String getStateError() {
		return STATE_ERROR;
	}

}
