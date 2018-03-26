package cn.com.chinaccs.datasite.main.datasite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author fddi 创建一个android内sqlite数据库，由android完成数据库建立
 * 
 */
public class DataBaseHelper extends SQLiteOpenHelper {
	/**
	 * 数据库命名
	 */
	public static final String DATEBASE_NAME = "DATASITE_DATABASE";

	/**
	 * 数据库版本
	 */
	public static final int DATABASE_VERSION = 1;

	public DataBaseHelper(Context context, String name) {
		// TODO Auto-generated constructor stub
		super(context, name, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据库空间时执行
		String sql = this.createTableInspectRecodeSQL();
		db.execSQL(sql);// 创建巡检任务表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 更新数据库版本时执行
		db.execSQL("DROP TABLE IF EXISTS T_INSPECT_PLAN_RECODE");
		onCreate(db);
	}

	private String createTableInspectRecodeSQL() {
		StringBuffer sb = new StringBuffer(
				"create table T_INSPECT_PLAN_RECODE (");
		sb.append(" IT_TIME TEXT,IS_RRU TEXT,BS_ID TEXT,CELL_ID TEXT,PLAN_ID TEXT)");
		return sb.toString();
	}

	/**
	 * 彻底删除数据库
	 * 
	 * @return
	 */
	public static Boolean deleteDataBase(Context context) {
		return context.deleteDatabase(DATEBASE_NAME);
	}
}
