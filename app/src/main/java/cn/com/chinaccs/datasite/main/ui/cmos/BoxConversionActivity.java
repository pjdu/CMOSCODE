package cn.com.chinaccs.datasite.main.ui.cmos;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.base.BaseActivity;

public class BoxConversionActivity extends BaseActivity {
	
	private Context context;
	private Button btn_0;
	private Button btn_1;
	private Button btn_2;
	private Button btn_3;
	private Button btn_4;
	private Button btn_5;
	private Button btn_6;
	private Button btn_7;
	private Button btn_8;
	private Button btn_9;
	private Button btn_dot; //小数点
	private Button btn_bs;  //退格
	private EditText edtl;
	private EditText edtr;
	private StringBuffer str_display;
	private Spinner spinnerl;
	private Spinner spinnerr;
	private Gallery gallery;
	private String[] unitWeight;
	private String[] unitWeightConstant;
	private String[] unitLength;
	private String[] unitLengthConstant;
	private String[] unitArea;
	private String[] unitAreaConstant;
	private String[] unitVolume;
	private String[] unitVolumeConstant;
	private String[] unitTemperature;
	private String[] unitTemperatureConstant;
	private String[] unitPower;
	private String[] unitPowerConstant;
	private String[] unitSpeed;
	private String[] unitSpeedConstant;
	private String[] unitPressure;
	private String[] unitPressureConstant;
	private String[] unitComputer;
	private String[] unitComputerConstant;
	private boolean flag = true; 
	private int type = 0;
	private int positionL;
	private int positionR;
	private BigDecimal result;
	private BigDecimal resultL;
	private BigDecimal resultR;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversion);
		this.initToolbar("单位换算");
		this.context = this;
		this.findViews();
		
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtl.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(edtr.getWindowToken(), 0);
		
		gallery.setAdapter(new GalleryAdapter(this));
		gallery.setOnItemSelectedListener(itemcl);
		
		str_display = new StringBuffer();
		
		btn_0.setOnClickListener(btnll);
		btn_1.setOnClickListener(btnll);
		btn_2.setOnClickListener(btnll);
		btn_3.setOnClickListener(btnll);
		btn_4.setOnClickListener(btnll);
		btn_5.setOnClickListener(btnll);
		btn_6.setOnClickListener(btnll);
		btn_7.setOnClickListener(btnll);
		btn_8.setOnClickListener(btnll);
		btn_9.setOnClickListener(btnll);
		btn_bs.setOnClickListener(btnll);
		btn_dot.setOnClickListener(btnll);
		edtl.setOnFocusChangeListener(editFocusLl);
		edtr.setOnFocusChangeListener(editFocusLr);
		edtl.setInputType(InputType.TYPE_NULL);
		edtr.setInputType(InputType.TYPE_NULL);
		
		
		unitWeight = getResources().getStringArray(R.array.unit_weight);
		unitWeightConstant = getResources().getStringArray(R.array.unit_weight_constant);
		unitLength = getResources().getStringArray(R.array.unit_length);
		unitLengthConstant = getResources().getStringArray(R.array.unit_length_constant);
		unitArea = getResources().getStringArray(R.array.unit_area);
		unitAreaConstant = getResources().getStringArray(R.array.unit_area_constant);
		unitVolume = getResources().getStringArray(R.array.unit_volume);
		unitVolumeConstant = getResources().getStringArray(R.array.unit_volume_constant);
		unitTemperature = getResources().getStringArray(R.array.unit_temperature);
		unitTemperatureConstant = getResources().getStringArray(R.array.unit_temperature_constant);
		unitPower = getResources().getStringArray(R.array.unit_power);
		unitPowerConstant = getResources().getStringArray(R.array.unit_power_constant);
		unitSpeed = getResources().getStringArray(R.array.unit_speed);
		unitSpeedConstant = getResources().getStringArray(R.array.unit_speed_constant);
		unitPressure = getResources().getStringArray(R.array.unit_pressure);
		unitPressureConstant = getResources().getStringArray(R.array.unit_pressure_constant);
		unitComputer = getResources().getStringArray(R.array.unit_computer);
		unitComputerConstant = getResources().getStringArray(R.array.unit_computer_constant);
		
		spinnerl.setOnItemSelectedListener(splr);
		spinnerr.setOnItemSelectedListener(splr);
		
	}
	/**
	 * spinner选择监听
	 */
	private OnItemSelectedListener splr = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (!edtl.getText().toString().equals("") && !edtr.getText().toString().equals("")) {
				if (flag) {
					cResult();
					edtr.setText(String.valueOf(result));
				} else {
                    cResult();
                    edtl.setText(String.valueOf(result));
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	/**
	 * gallery选择监听
	 */
	private OnItemSelectedListener itemcl = new OnItemSelectedListener() {
		

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			switch (arg2) {
			case 0:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter0 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitWeight);
				unitAdapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setPrompt("请选择单位");
				spinnerr.setPrompt("请选择单位");
				spinnerl.setAdapter(unitAdapter0);
				spinnerr.setAdapter(unitAdapter0);
				spinnerl.setSelection(1);
				spinnerr.setSelection(15);
				type = 0;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			case 1:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter1 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitLength);
				unitAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setPrompt("请选择单位");
				spinnerr.setPrompt("请选择单位");
				spinnerl.setAdapter(unitAdapter1);
				spinnerr.setAdapter(unitAdapter1);
				spinnerl.setSelection(9);
				spinnerr.setSelection(8);
				type = 1;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			case 2:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter2 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitArea);
				unitAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setPrompt("请选择单位");
				spinnerr.setPrompt("请选择单位");
				spinnerl.setAdapter(unitAdapter2);
				spinnerr.setAdapter(unitAdapter2);
				spinnerl.setSelection(4);
				spinnerr.setSelection(3);
				type = 2;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			case 3:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter3 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitVolume);
				unitAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setAdapter(unitAdapter3);
				spinnerr.setAdapter(unitAdapter3);
				spinnerl.setSelection(1);
				spinnerr.setSelection(0);
				type = 3;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			case 4:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter4 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitTemperature);
				unitAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setAdapter(unitAdapter4);
				spinnerr.setAdapter(unitAdapter4);
				spinnerl.setSelection(0);
				spinnerr.setSelection(1);
				type = 4;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			case 5:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter5 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitPower);
				unitAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setAdapter(unitAdapter5);
				spinnerr.setAdapter(unitAdapter5);
				spinnerl.setSelection(1);
				spinnerr.setSelection(0);
				type = 5;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			case 6:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter6 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitSpeed);
				unitAdapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setAdapter(unitAdapter6);
				spinnerr.setAdapter(unitAdapter6);
				spinnerl.setSelection(1);
				spinnerr.setSelection(0);
				type = 6;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			case 7:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter7 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitPressure);
				unitAdapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setAdapter(unitAdapter7);
				spinnerr.setAdapter(unitAdapter7);
				spinnerl.setSelection(1);
				spinnerr.setSelection(0);
				type = 7;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			case 8:
				spinnerl.setAdapter(null);
				spinnerr.setAdapter(null);
				ArrayAdapter<String> unitAdapter8 = new ArrayAdapter<String>(context, R.layout.item_spinner,unitComputer);
				unitAdapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerl.setAdapter(unitAdapter8);
				spinnerr.setAdapter(unitAdapter8);
				spinnerl.setSelection(1);
				spinnerr.setSelection(0);
				type = 8;
				edtl.setText("");
				edtr.setText("");
				str_display = new StringBuffer();
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	/**
	 * 获取view
	 */
	private void findViews(){
		btn_0 = (Button)findViewById(R.id.btn_num0);
		btn_1 = (Button)findViewById(R.id.btn_num1);
		btn_2 = (Button)findViewById(R.id.btn_num2);
		btn_3 = (Button)findViewById(R.id.btn_num3);
		btn_4 = (Button)findViewById(R.id.btn_num4);
		btn_5 = (Button)findViewById(R.id.btn_num5);
		btn_6 = (Button)findViewById(R.id.btn_num6);
		btn_7 = (Button)findViewById(R.id.btn_num7);
		btn_8 = (Button)findViewById(R.id.btn_num8);
		btn_9 = (Button)findViewById(R.id.btn_num9);
		btn_dot = (Button)findViewById(R.id.btn_dot);
		btn_bs = (Button)findViewById(R.id.btn_bs);
		edtl = (EditText)findViewById(R.id.etl);
		edtr = (EditText)findViewById(R.id.etr);
		spinnerl = (Spinner)findViewById(R.id.spinnerl);
		spinnerr = (Spinner)findViewById(R.id.spinnerr);
		gallery = (Gallery)findViewById(R.id.gallery);
		
	}
	/**
	 * 左边输入框获得焦点
	 */
	private OnFocusChangeListener editFocusLl = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (hasFocus) {
			    flag = true;
			}
		}
	};
	/**
	 * 右边输入框获得焦点
	 */
	private OnFocusChangeListener editFocusLr = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (hasFocus) {
                flag =false;
			} 
		}
	};
	/**
	 * 数字键监听
	 */
	private OnClickListener btnll = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_num0:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("0");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("0");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("0");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("0");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}				
				break;
			case R.id.btn_num1:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("1");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("1");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("1");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("1");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_num2:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("2");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("2");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("2");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("2");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_num3:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("3");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("3");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("3");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("3");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_num4:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("4");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("4");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("4");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("4");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_num5:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("5");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("5");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("5");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("5");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_num6:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("6");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("6");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("6");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("6");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_num7:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("7");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("7");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("7");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("7");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_num8:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("8");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("8");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("8");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("8");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_num9:
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						str_display.append("9");
						edtl.setText(str_display.toString());
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					} else {
                        str_display.append("9");
                        edtl.setText(str_display.toString());
                        cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}
				}else {
					if (!edtr.getText().toString().equals("")) {
						str_display = str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						str_display.append("9");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					} else {
						str_display.append("9");
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}
				}
				break;
			case R.id.btn_bs:
					if (flag) {
						if (!edtl.getText().toString().equals("")) {
							str_display.delete(0, str_display.length());
							str_display.append(edtl.getText().toString());
							str_display.deleteCharAt(str_display.length() - 1);
							edtl.setText(str_display.toString());
							edtl.setSelection(str_display.length());
							if (str_display.length() > 0) {
								cResult();
								edtr.setText(String.valueOf(result));
								edtr.setSelection(String.valueOf(result).length());
							} else {
                                edtr.setText("");
							}							
						}						
					} else {
						if (!edtr.getText().toString().equals("")) {
							str_display.delete(0, str_display.length());
							str_display.append(edtr.getText().toString());
							str_display.deleteCharAt(str_display.length() - 1);
							edtr.setText(str_display.toString());
							edtr.setSelection(str_display.length());
							if (str_display.length() > 0) {
								cResult();
								edtl.setText(String.valueOf(result));
								edtl.setSelection(String.valueOf(result).length());
							} else {
                                edtl.setText("");
							}							
						}	
					}				
				break;
			case R.id.btn_dot:
				Pattern pattern = Pattern.compile("^\\d*\\.\\d*");
				if (flag) {
					if (!edtl.getText().toString().equals("")) {
						str_display.delete(0, str_display.length());
						str_display.append(edtl.getText().toString());
						Matcher isPoint = pattern.matcher(str_display.toString());
						if (!isPoint.matches()) {
							str_display.append(".");
						}
						edtl.setText(str_display.toString());					
						cResult();
						edtr.setText(String.valueOf(result));
						edtl.setSelection(str_display.length());
						edtr.setSelection(String.valueOf(result).length());
					}														
				} else {
					if (!edtr.getText().toString().equals("")) {
						str_display.delete(0, str_display.length());
						str_display.append(edtr.getText().toString());
						Matcher isPoint = pattern.matcher(str_display.toString());
						if (!isPoint.matches()) {
							str_display.append(".");						
						}
						edtr.setText(str_display.toString());
						cResult();
						edtl.setText(String.valueOf(result));	
						edtl.setSelection(String.valueOf(result).length());
						edtr.setSelection(str_display.length());
					}								
				}				
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 计算换算后的值
	 */
	private BigDecimal cResult(){
		
		positionL = spinnerl.getSelectedItemPosition();
	    positionR = spinnerr.getSelectedItemPosition();	    
		switch (type) {
		case 0:
			if (flag) {
			    resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitWeightConstant[positionL]))
			              .divide(new BigDecimal(unitWeightConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);			     
			    result = resultR;
			} else {
				resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitWeightConstant[positionR]))
						  .divide(new BigDecimal(unitWeightConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
				result = resultL;
			}			
			break;
		case 1:
			if (flag) {
				resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitLengthConstant[positionL]))
						 .divide(new BigDecimal(unitLengthConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);
				result = resultR;
			} else {
                resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitLengthConstant[positionR]))
                		 .divide(new BigDecimal(unitLengthConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
                result = resultL;
			}
			break;
		case 2:
			
			if (flag) {
			    resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitAreaConstant[positionL]))
			    		 .divide(new BigDecimal(unitAreaConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);
			    result = resultR;
			} else {
				resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitAreaConstant[positionR]))
						 .divide(new BigDecimal(unitAreaConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
	            result = resultL;
			}
			break;
		case 3:
			if (flag) {
				resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitVolumeConstant[positionL]))
						 .divide(new BigDecimal(unitVolumeConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);
				result = resultR;
			} else {
				resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitVolumeConstant[positionR]))
						 .divide(new BigDecimal(unitVolumeConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
				result = resultL;
			}
			break;
		case 4:
			if (flag) {
				resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitTemperatureConstant[positionL]))
						 .divide(new BigDecimal(unitTemperatureConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);
				result = resultR;
			} else {
                resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitTemperatureConstant[positionR]))
                		 .divide(new BigDecimal(unitTemperatureConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
                result = resultL;
			}
			break;
		case 5:
			if (flag) {
				resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitPowerConstant[positionL]))
						 .divide(new BigDecimal(unitPowerConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);
				result = resultR;
			} else {
                resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitPowerConstant[positionR]))
                		 .divide(new BigDecimal(unitPowerConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
	            result = resultL;
			}
			break;
		case 6:
			if (flag) {
				resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitSpeedConstant[positionL]))
						 .divide(new BigDecimal(unitSpeedConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);
				result = resultR;
			} else {
                resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitSpeedConstant[positionR]))
                		 .divide(new BigDecimal(unitSpeedConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
                result = resultL;
			}
			break;
		case 7:
			if (flag) {
				resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitPressureConstant[positionL]))
						 .divide(new BigDecimal(unitPressureConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);
				result = resultR;
			} else {
                resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitPressureConstant[positionR]))
                		 .divide(new BigDecimal(unitPressureConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
                result = resultL;
			}
			break;
		case 8:
			if (flag) {
				resultR = BigDecimal.valueOf(Double.valueOf(edtl.getText().toString())).multiply(new BigDecimal(unitComputerConstant[positionL]))
						 .divide(new BigDecimal(unitComputerConstant[positionR]),4,BigDecimal.ROUND_HALF_UP);
				result = resultR;
			} else {
                resultL = BigDecimal.valueOf(Double.valueOf(edtr.getText().toString())).multiply(new BigDecimal(unitComputerConstant[positionR]))
                		 .divide(new BigDecimal(unitComputerConstant[positionL]),4,BigDecimal.ROUND_HALF_UP);
                result = resultL;
			}
			break;

		default:
			break;
		}
		return result;
	}
	/**
	 * 设置gallery的高度和宽度
	 * @param paramInt
	 * @param paramContext
	 * @return
	 */
	public static int galleryWH(int paramInt,Context paramContext){
		if (getApilevel() >= 4) {
			float f1 = paramInt;
			float f2 = paramContext.getResources().getDisplayMetrics().densityDpi / 160.F;
			//paramInt = (int)FloatMath.floor(f1*f2);原代码因为api提高而弃用，换为Math
			paramInt= (int) Math.floor(f1*f2);
        }
		return paramInt;
	}
	
	public static int getApilevel(){
		return Integer.parseInt(Build.VERSION.SDK);
	}

}

/**
 * gallery图片适配器
 * @author xing
 *
 */
class GalleryAdapter extends BaseAdapter{
 private Context context;
 int imageHeightPx;
 int imageWidthPx;
 private Integer[] imageIntegers = {
     R.drawable.ic_weight,
     R.drawable.ic_length,
     R.drawable.ic_area,
     R.drawable.ic_volume,
     R.drawable.ic_temperature,
     R.drawable.ic_power,
     R.drawable.ic_speed,
     R.drawable.ic_pressure,
     R.drawable.ic_computer
 };
 public GalleryAdapter(Context context){
     this.context = context;
     int i = BoxConversionActivity.galleryWH(90, context);
     this.imageWidthPx = i;
     int j = BoxConversionActivity.galleryWH(70, context);
     this.imageHeightPx = j;
 }
 @Override
 public int getCount() {
     // TODO Auto-generated method stub
     return imageIntegers.length;
 }

 @Override
 public Object getItem(int position) {
     // TODO Auto-generated method stub
     return position;
 }

 @Override
 public long getItemId(int position) {
     // TODO Auto-generated method stub
     return position;
 }

 @Override
 public View getView(int position, View convertView, ViewGroup parent) {
     // TODO Auto-generated method stub
     int k = this.imageWidthPx;
     int m = this.imageHeightPx;
     ImageView imageView = new ImageView(context);
     imageView.setImageResource(imageIntegers[position]);
     imageView.setScaleType(ImageView.ScaleType.CENTER);
     imageView.setLayoutParams(new Gallery.LayoutParams(k, m));
     return imageView;
 }

}
