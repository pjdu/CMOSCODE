package cn.com.chinaccs.datasite.main;

import org.json.JSONArray;

/**
 * @author Fddi
 * 
 */
public class DataModify{
	private int index;
	private String title;
	private String tips;
	private String type;
	private boolean optional;
	private boolean custom;
	private boolean checkbox;
	private JSONArray options;
	private String value;
	private String fieldName;
	private String fieldBelong;
	public static final String TYPE_NORMAL = "normal";
	public static final String TYPE_LONGITUDE = "longitude";
	public static final String TYPE_LATITUDE = "latitude";
	public static final String TYPE_AZIMUTH = "azimuth";
	public static final String TYPE_DOWNTILT = "downtilt";
	public static final String TYPE_QRCODE = "qrcode";

	public DataModify() {
	}

	public DataModify(int index, String title, String tips, String type,
					  boolean optional, boolean custom, boolean checkbox,
					  JSONArray options, String value) {
		this.title = title;
		this.tips = tips;
		this.type = type;
		this.optional = optional;
		this.custom = custom;
		this.checkbox = checkbox;
		this.options = options;
		this.value = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public JSONArray getOptions() {
		return options;
	}

	public void setOptions(JSONArray options) {
		this.options = options;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public boolean isCheckbox() {
		return checkbox;
	}

	public void setCheckbox(boolean checkbox) {
		this.checkbox = checkbox;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldBelong() {
		return fieldBelong;
	}

	public void setFieldBelong(String fieldBelong) {
		this.fieldBelong = fieldBelong;
	}

}
