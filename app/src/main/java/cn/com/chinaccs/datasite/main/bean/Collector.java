package cn.com.chinaccs.datasite.main.bean;

import java.util.List;

/**
 * 定义数据
 * 
 * @author fddi
 * 
 */
public class Collector {
	public static final Integer DATA_TYPE_UPLOAD = 1;
	public static final Integer DATA_TYPE_TITLE = 2;
	public static final Integer DATA_TYPE_LIST = 4;
	public static final Integer DATA_TYPE_IP = 8;
	public static final Integer DATA_TYPE_FULLLINE = 16;
	public static final Integer DATA_TYPE_NBCELL = 32;
	public static final Integer DATA_TYPE_DHCP = 64;
	public static final Integer DATA_TYPE_WIFISCAN = 128;
	public static final String UNIT_SIGNAL_DBM = "dBm";
	public static final String UNIT_SIGNAL_DB = "dB";
	public static final String UNIT_FREQUENCY = "MHz";
	public static final String UNIT_SPEED = "Mbps";
	public static final String UNIT_SECONDS = "seconds";
	public static final String UNIT_RATE = "%";
	private String dataName;
	private String dataValue;
	private Integer groupId;
	private Integer dataType;
	private String dataUnit;
	private List<Collector> valueList;

	public Collector(String dataName, String dataValue, Integer groupId) {
		this.dataName = dataName;
		this.dataValue = dataValue == null ? "" : dataValue;
		this.groupId = groupId;
		this.dataUnit = "";
	}

	public Collector(String dataName, String dataValue, Integer groupId,
					 Integer dataType) {
		this.dataName = dataName;
		this.dataValue = dataValue == null ? "" : dataValue;
		this.groupId = groupId;
		this.dataType = dataType;
		this.dataUnit = "";
	}

	public Collector(String dataName, String dataValue, Integer groupId,
					 String unit) {
		this.dataName = dataName;
		this.dataValue = dataValue == null ? "" : dataValue;
		this.groupId = groupId;
		this.dataUnit = unit;
	}

	public Collector(String dataName, List<Collector> valueList,
					 Integer groupId, Integer dataType) {
		this.dataName = dataName;
		this.valueList = valueList;
		this.groupId = groupId;
		this.dataType = dataType;
		this.dataValue = dataValue == null ? "" : dataValue;
		this.dataUnit = "";
	}

	public Collector(String dataName, String dataValue, Integer groupId,
					 Integer dataType, String unit) {
		this.dataName = dataName;
		this.dataValue = dataValue == null ? "" : dataValue;
		this.groupId = groupId;
		this.dataType = dataType;
		this.dataUnit = unit;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public String getDataValue() {
		return dataValue;
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public List<Collector> getValueList() {
		return valueList;
	}

	public void setValueList(List<Collector> valueList) {
		this.valueList = valueList;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	/**
	 * for log
	 * @return
	 */
	@Override
	public String toString() {
		return "Collector{" +
				"dataName='" + dataName + '\'' +
				", dataValue='" + dataValue + '\'' +
				", groupId=" + groupId +
				", dataType=" + dataType +
				", dataUnit='" + dataUnit + '\'' +
				", valueList=" + valueList +
				'}';
	}
}
