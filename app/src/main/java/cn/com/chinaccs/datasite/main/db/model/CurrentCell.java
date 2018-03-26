package cn.com.chinaccs.datasite.main.db.model;

import java.io.Serializable;

/**
 * 当前接入小区实体类
 * @author liu
 *
 */
public class CurrentCell implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bsBtsId;
	private String bsBsc;
	private String iCellId;
	private String iCellName;
	private String cellCi;
	private String iCellLongitude;
	private String iCellLatitude;
	private String isSingleRru;
	
	public CurrentCell(){
		
	}
	public CurrentCell(String bsBtsId, String bsBsc, String iCellId,
					   String iCellName, String iCellLongitude, String iCellLatitude,
					   String isSingleRru) {
		super();
		this.bsBtsId = bsBtsId;
		this.bsBsc = bsBsc;
		this.iCellId = iCellId;
		this.iCellName = iCellName;
		this.iCellLongitude = iCellLongitude;
		this.iCellLatitude = iCellLatitude;
		this.isSingleRru = isSingleRru;
	}
	public String getBsBtsId() {
		return bsBtsId;
	}
	public void setBsBtsId(String bsBtsId) {
		this.bsBtsId = bsBtsId;
	}
	public String getBsBsc() {
		return bsBsc;
	}
	public void setBsBsc(String bsBsc) {
		this.bsBsc = bsBsc;
	}
	public String getiCellId() {
		return iCellId;
	}
	public void setiCellId(String iCellId) {
		this.iCellId = iCellId;
	}
	public String getiCellName() {
		return iCellName;
	}
	public void setiCellName(String iCellName) {
		this.iCellName = iCellName;
	}
	public String getiCellLongitude() {
		return iCellLongitude;
	}
	public void setiCellLongitude(String iCellLongitude) {
		this.iCellLongitude = iCellLongitude;
	}
	public String getiCellLatitude() {
		return iCellLatitude;
	}
	public void setiCellLatitude(String iCellLatitude) {
		this.iCellLatitude = iCellLatitude;
	}
	public String getIsSingleRru() {
		return isSingleRru;
	}
	public void setIsSingleRru(String isSingleRru) {
		this.isSingleRru = isSingleRru;
	}
	public String getCellCi() {
		return cellCi;
	}
	public void setCellCi(String cellCi) {
		this.cellCi = cellCi;
	}
	
	
}
