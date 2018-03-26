package cn.com.chinaccs.datasite.main.db.dao;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import cn.com.chinaccs.datasite.main.db.DatabaseHelper;
import cn.com.chinaccs.datasite.main.db.model.AssetsModel;


public class DAOAssets {
	public DatabaseHelper helper;
	public Dao<AssetsModel, Integer> dao;
	public static DAOAssets manager = null;
	

	public DAOAssets(Context context) {
		helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		dao = helper.getAssetsDao();
	}

	/**
	 * 获取管理器
	 * @param context
	 * @return
	 */
	public static DAOAssets getInstance(Context context) {
		if (manager == null) {
			manager = new DAOAssets(context);
		}
		return manager;
	}

	/**
	 * 关闭管理器
	 */
	public void close() {
		if (helper != null) {
			helper.close();
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}

	/**
	 * 增加集合
	 * 
	 * @param 
	 * @throws SQLException
	 */
	public void add(AssetsModel obj) {
		if (obj != null) {

			try {
				dao.create(obj);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删
	 * 
	 * @param 
	 * @throws SQLException
	 */
	public void delete(AssetsModel obj) {
		if (obj != null) {
			try {
				dao.delete(obj);
			} catch (SQLException e) {
				 e.getStackTrace();
			}
		}
	}

	/**
	 * 改
	 * 
	 * @param 
	 * @throws SQLException
	 */
	public void update(AssetsModel obj) {
		if (obj != null) {
			try {
				dao.update(obj);
			} catch (SQLException e) {
				 e.getStackTrace();
			}
		}
	}

	/**
	 * 查
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<AssetsModel> quary() {
		List<AssetsModel> list = null;
		try {
			list = dao.queryForAll();
		} catch (SQLException e) {
			 e.getStackTrace();
		}
		return list;
	}
	
	public String[] array() {
		
		String[] data = null;
		try {
			data = (String[]) dao.queryForAll().toArray();
		} catch (SQLException e) {
			 e.getStackTrace();
		}
		return data;
	}
	
	/** 
     * 删除全部 
     */  
	public void deleteAll() {  
		try {
			dao.delete(quary());
		} catch (SQLException e) {
			 e.getStackTrace();
		}
    } 
	
	//按资产编号查询
		public List<AssetsModel> searchAssetsId(String assetsId) {
			try {
				// 查询的query 返回值是一个列表
				List<AssetsModel> assets = dao.queryBuilder().where()
						.eq("assetsid", assetsId)
						.query();
				if (assets.size() > 0)
					return assets;
			} catch (SQLException e) {
				e.getStackTrace();
			}
			return null;
		}

}
