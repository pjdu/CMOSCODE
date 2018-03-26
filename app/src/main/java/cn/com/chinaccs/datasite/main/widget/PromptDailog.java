package cn.com.chinaccs.datasite.main.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cn.com.chinaccs.datasite.main.R;

public class PromptDailog extends Dialog {
	Context context;
	private String title;
	private String msg;
	private String btext;
	private OnClickListener lr;

	public PromptDailog(Context context, String title, String msg,
			String btext, OnClickListener lr) {
		super(context);
		this.context = context;
		this.title = title;
		this.msg = msg;
		this.btext = btext;
		this.lr = lr;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pop_prompt);
		initDialog();
		setCanceledOnTouchOutside(false);
		getWindow().setGravity(Gravity.BOTTOM);
		getWindow().setBackgroundDrawable(null);
		getWindow().setWindowAnimations(R.style.anim_slide);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		getWindow().setAttributes(lp);
	}

	@Override
	public void show() {
		super.show();
	}

	private void initDialog() {
		TextView tvTitle = (TextView) findViewById(R.id.tv_prompt_title);
		TextView tvMsg = (TextView) findViewById(R.id.tv_prompt_msg);
		Button btnSure = (Button) findViewById(R.id.btn_prompt_sure);
		Button btnCancel = (Button) findViewById(R.id.btn_prompt_cancel);
		if (title != null && !title.equals("")) {
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(title);
		}
		if (msg != null && !msg.equals("")) {
			tvMsg.setVisibility(View.VISIBLE);
			tvMsg.setText(msg);
		} else {
			tvMsg.setVisibility(View.GONE);
		}
		if (btext != null && !btext.equals("")) {
			btnSure.setVisibility(View.VISIBLE);
			btnSure.setText(btext);
		}
		btnSure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (lr != null)
					lr.onClick(v);
				dismiss();
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}

	public interface OnClickListener {
		public void onClick(View v);
	}
}
