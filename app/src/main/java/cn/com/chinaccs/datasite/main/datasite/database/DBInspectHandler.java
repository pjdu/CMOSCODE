package cn.com.chinaccs.datasite.main.datasite.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.chinaccs.datasite.main.DataSiteStart;
import cn.com.chinaccs.datasite.main.common.App;


/**
 * @author fddi
 * 
 */
public class DBInspectHandler implements IDBHandler {
	Context context;
	private SQLiteDatabase db;

	public DBInspectHandler(Context context, int mode) {
		this.context = context;
		DataBaseHelper hp = new DataBaseHelper(context,
				DataBaseHelper.DATEBASE_NAME);
		switch (mode) {
		case MODE_READ_DATABASE:
			db = hp.getReadableDatabase();
			break;
		case MODE_WRITE_DATABASE:
			db = hp.getWritableDatabase();
			break;
		default:
			db = hp.getWritableDatabase();
			break;
		}
	}

	/**
	 * 清除缓存
	 * 
	 * @return
	 */
	public boolean clearDayCache() {
		try {
			if (db.inTransaction())
				db.endTransaction();
			db.beginTransaction();// 手动设置开始事务
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",
					Locale.CHINA);
			String dateItem = sdf.format(new Date());
			String[] args = { dateItem };
			db.delete("T_INSPECT_PLAN_RECODE", "IT_TIME<?", args);
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); // 处理完成
			SharedPreferences share = context.getSharedPreferences(
					App.SHARE_TAG, 0);
			share.edit()
					.putString(DataSiteStart.SHARE_DATEITEM_CLEAR_IT_CACHE,
							dateItem).commit();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (db.inTransaction())
				db.endTransaction();
			return false;
		}
	}

	/**
	 * 检查作业是否当天已巡检
	 * 
	 * @return
	 */
	public boolean dbCheckIsPlaned(boolean isRRu, String bsId, String cellId,
			String planId) {
		boolean isPlan = false;
		String[] args = null;
		StringBuffer sb = new StringBuffer(
				"select * from T_INSPECT_PLAN_RECODE where");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
		String dateItem = sdf.format(new Date());
		if (isRRu) {
			args = new String[4];
			args[0] = dateItem;
			args[1] = bsId;
			args[2] = cellId;
			args[3] = planId;
			sb.append(" IT_TIME=? and BS_ID=? and CELL_ID=? and PLAN_ID=? ");
		} else {
			args = new String[4];
			args[0] = dateItem;
			args[1] = bsId;
			args[2] = planId;
			args[3] = String.valueOf(isRRu);
			sb.append(" IT_TIME=? and BS_ID=? and PLAN_ID=? and IS_RRU=? ");
		}
		Cursor cursor = db.rawQuery(sb.toString(), args);
		if (cursor.getCount() > 0)
			isPlan = true;
		cursor.close();
		cursor = null;
		return isPlan;
	}

	/**
	 * 保存巡检成功记录
	 * 
	 * @param list
	 * @return
	 */
	public boolean dbSaveInpectRecodes(List<String> list) {
		try {
			if (db.inTransaction())
				db.endTransaction();
			db.beginTransaction();// 手动设置开始事务
			if (list == null || list.size() <= 0) {
				return false;
			}
			ContentValues cv = new ContentValues();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",
					Locale.CHINA);
			cv.put("IT_TIME", sdf.format(date));
			cv.put("BS_ID", list.get(0));
			cv.put("IS_RRU", list.get(1));
			cv.put("CELL_ID", list.get(2));
			cv.put("PLAN_ID", list.get(3));
			db.insert("T_INSPECT_PLAN_RECODE", null, cv);
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); // 处理完成
			cv.clear();
			cv = null;
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (db.inTransaction())
				db.endTransaction();
			return false;
		}
	}

	@Override
	public SQLiteDatabase getDataBase() {
		// TODO Auto-generated method stub
		return db;
	}

	@Override
	public void closeDataBase() {
		// TODO Auto-generated method stub
		if (db != null && db.isOpen())
			db.close();
	}

}
