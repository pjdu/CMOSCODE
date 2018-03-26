package cn.com.chinaccs.datasite.main.datasite.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author Fddi
 * 
 */
public interface IDBHandler {

	public static final int MODE_READ_DATABASE = 1;
	public static final int MODE_WRITE_DATABASE = 2;

	/**
	 * 获取当前数据库实例
	 * 
	 * @return
	 */
	public SQLiteDatabase getDataBase();

	/**
	 * 关闭数据库
	 */
	public void closeDataBase();
}
