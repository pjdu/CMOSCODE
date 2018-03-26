package cn.com.chinaccs.datasite.main.widget;

import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.lang.reflect.Field;

import cn.com.chinaccs.datasite.main.R;

/**
 * @author fddi 自定义选项卡
 */
public class TabDrawTop {
	private TabHost tab;
	private int version;

	public TabDrawTop(TabHost tab) {
		this.tab = tab;
	}

	public void getTabWidget() {
		final TabWidget tabWidget = tab.getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			/**
			 * 此方法是为了去掉系统默认的色白的底角
			 * 
			 */
			Field mBottomLeftStrip = null;
			Field mBottomRightStrip = null;

			// android版本判断；
			Class<android.os.Build.VERSION> build_version_class = android.os.Build.VERSION.class;
			Field field = null;
			try {
				field = build_version_class.getField("SDK_INT");
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchFieldException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				version = (Integer) field.get(new android.os.Build.VERSION());
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d("android-version:", Integer.toString(version));
			// 兼容2.2以前的版本；version为androidAPI的版本号；
			if (version < 8) {
				try {
					mBottomLeftStrip = tabWidget.getClass().getDeclaredField(
							"mBottomLeftStrip");
					mBottomRightStrip = tabWidget.getClass().getDeclaredField(
							"mBottomRightStrip");
					if (!mBottomLeftStrip.isAccessible()) {
						mBottomLeftStrip.setAccessible(true);
					}
					if (!mBottomRightStrip.isAccessible()) {
						mBottomRightStrip.setAccessible(true);
					}
					mBottomLeftStrip.set(tabWidget, tab.getResources()
							.getDrawable(R.drawable.nothing));
					mBottomRightStrip.set(tabWidget, tab.getResources()
							.getDrawable(R.drawable.nothing));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					mBottomLeftStrip = tabWidget.getClass().getDeclaredField(
							"mLeftStrip");
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					mBottomRightStrip = tabWidget.getClass().getDeclaredField(
							"mRightStrip");
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!mBottomLeftStrip.isAccessible()) {
					mBottomLeftStrip.setAccessible(true);
				}
				if (!mBottomRightStrip.isAccessible()) {
					mBottomRightStrip.setAccessible(true);
				}
				try {
					mBottomLeftStrip.set(tabWidget, tab.getResources()
							.getDrawable(R.drawable.nothing));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					mBottomRightStrip.set(tabWidget, tab.getResources()
							.getDrawable(R.drawable.nothing));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
