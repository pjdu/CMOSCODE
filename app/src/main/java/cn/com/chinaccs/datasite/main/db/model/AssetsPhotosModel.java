package cn.com.chinaccs.datasite.main.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

//表名称   
@DatabaseTable(tableName = "assetsphoto")
public class AssetsPhotosModel {
	// 主键 id 自增长
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(canBeNull = false)
	private String dsId;
	@DatabaseField(canBeNull = false)
	private int type;
	@DatabaseField(canBeNull = false)
	private String subType;
	@DatabaseField(canBeNull = false)
	private String desc;
	@DatabaseField(canBeNull = true)
	private String imgUrl;
	@DatabaseField(canBeNull = false)
	private String localImgUrl;
	@DatabaseField(canBeNull = false)
	private Date creatDate;
	@DatabaseField(canBeNull = false)
	private boolean isUpload;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDsId() {
		return dsId;
	}
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getLocalImgUrl() {
		return localImgUrl;
	}
	public void setLocalImgUrl(String localImgUrl) {
		this.localImgUrl = localImgUrl;
	}
	public Date getCreatDate() {
		return creatDate;
	}
	public void setCreatDate(Date creatDate) {
		this.creatDate = creatDate;
	}
	public boolean isUpload() {
		return isUpload;
	}
	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}
	
	
}
