package cn.com.chinaccs.datasite.main.datasite.inspect.function;

import android.widget.EditText;

/**
 * @author Fddi
 * 
 */
public class PlanItem {
	private String planItemId;
	private String state;
	private EditText et;

	public String getPlanItemId() {
		return planItemId;
	}

	public void setPlanItemId(String planItemId) {
		this.planItemId = planItemId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public EditText getEt() {
		return et;
	}

	public void setEt(EditText et) {
		this.et = et;
	}

}
