package cn.com.chinaccs.datasite.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import cn.com.chinaccs.datasite.main.R;
import cn.com.chinaccs.datasite.main.common.App;

public class CheckBoxButton extends Button {
	private Boolean checked = false;
	private Boolean enabled = true;
	private OnClickListener clickListener;

	public CheckBoxButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setGravity(Gravity.LEFT | Gravity.CENTER);
		setTextColor(getResources().getColor(R.color.black));
		TypedArray typeArray = context.obtainStyledAttributes(attrs,
				R.styleable.CheckBoxLinearButton);
		checked = typeArray.getBoolean(
				R.styleable.CheckBoxLinearButton_checked, false);
		setText(typeArray.getString(R.styleable.CheckBoxLinearButton_label));
		enabled = typeArray.getBoolean(R.styleable.CheckBoxLinearButton_enable,
				true);
		if (checked) {
			setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
					.getDrawable(R.drawable.checkbox_sq_checked), null);
		} else {
			setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
					.getDrawable(R.drawable.checkbox_sq_unchecked), null);
		}
		this.setOnChangedListener(new OnChangedListener() {

			@Override
			public void onChanged(View v, Boolean checked) {
				// TODO Auto-generated method stub

			}
		});
		Log.d(App.LOG_TAG, String.valueOf(enabled));
		if (!enabled) {
			setTextColor(getResources().getColor(R.color.gray));
			setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
					.getDrawable(R.drawable.checkbox_sq_unable), null);
		}
		setEnabled(enabled);
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		if (!enabled)
			return;
		this.checked = checked;
		if (checked) {
			setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
					.getDrawable(R.drawable.checkbox_sq_checked), null);
		} else {
			setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
					.getDrawable(R.drawable.checkbox_sq_unchecked), null);
		}
	}

	public void setCheckEnabled(Boolean enabled) {
		this.enabled = enabled;
		if (!enabled) {
			setTextColor(getResources().getColor(R.color.gray));
			setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
					.getDrawable(R.drawable.checkbox_sq_unable), null);
		} else {
			setTextColor(getResources().getColor(R.color.black));
			if (checked) {
				setCompoundDrawablesWithIntrinsicBounds(
						null,
						null,
						getResources().getDrawable(
								R.drawable.checkbox_sq_checked), null);
			} else {
				setCompoundDrawablesWithIntrinsicBounds(
						null,
						null,
						getResources().getDrawable(
								R.drawable.checkbox_sq_unchecked), null);
			}
		}
		setEnabled(enabled);
	}

	public void setOnChangedListener(final OnChangedListener onChangedListener) {
		clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checked) {
					setCompoundDrawablesWithIntrinsicBounds(
							null,
							null,
							getResources().getDrawable(
									R.drawable.checkbox_sq_unchecked), null);
					checked = false;
				} else {
					setCompoundDrawablesWithIntrinsicBounds(
							null,
							null,
							getResources().getDrawable(
									R.drawable.checkbox_sq_checked), null);
					checked = true;
				}
				onChangedListener.onChanged(v, checked);
			}
		};
		setOnClickListener(clickListener);
	}

	public interface OnChangedListener {
		public void onChanged(View v, Boolean checked);
	}
}
