package cn.com.chinaccs.datasite.main.db.dao;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import cn.com.chinaccs.datasite.main.db.DatabaseHelper;
import cn.com.chinaccs.datasite.main.db.model.AssetsPhotosModel;

public class DAOAssetsPhoto {
	public DatabaseHelper helper;
	public Dao<AssetsPhotosModel, Integer> dao;
	public static DAOAssetsPhoto manager = null;

	public DAOAssetsPhoto(Context context) {
		helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		dao = helper.getAssetsPhotoDao();
	}

	/**
	 * 获取管理器
	 * 
	 * @param context
	 * @return
	 */
	public static DAOAssetsPhoto getInstance(Context context) {
		if (manager == null) {
			manager = new DAOAssetsPhoto(context);
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
	public void add(AssetsPhotosModel obj) {
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
	public void delete(AssetsPhotosModel obj) {
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
	public void update(AssetsPhotosModel obj) {
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
	public List<AssetsPhotosModel> quary() {
		List<AssetsPhotosModel> list = null;
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
	public void  insertCache(AssetsPhotosModel obj){
		if(obj!=null){
			try {
				List<AssetsPhotosModel> assets = dao.queryBuilder().where()
						.eq("dsId", obj.getDsId()).and()
						.eq("type", obj.getType()).and()
						.eq("subType", obj.getSubType()).query();
				if(assets!=null & assets.size()>0){
					dao.delete(assets);
				}
				add(obj);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// 按数据id查询
	public List<AssetsPhotosModel> searchDsId(String dsId) {
		try {
			// 查询的query 返回值是一个列表
			List<AssetsPhotosModel> assets = dao.queryBuilder().where()
					.eq("dsId", dsId).query();
			if (assets.size() > 0)
				return assets;
		} catch (SQLException e) {
			e.getStackTrace();
		}
		return null;
	}

	// 按数据subType查询
	public List<AssetsPhotosModel> searchAssetsDsId(String dsId, boolean isUpload) {
		try {
			// 查询的query 返回值是一个列表
			List<AssetsPhotosModel> assets = dao.queryBuilder().where().eq("dsId", dsId)
					.and().eq("isUpload", isUpload).query();
			if (assets.size() > 0)
				return assets;
		} catch (SQLException e) {
			e.getStackTrace();
		}
		return null;
	}
	// 按数据subType查询
			public List<AssetsPhotosModel> searchAssetsSubType(String dsId, String subType, boolean isUpload) {
				try {
					// 查询的query 返回值是一个列表
					List<AssetsPhotosModel> assets = dao.queryBuilder().where().eq("dsId", dsId).and()
							.eq("subType", subType).and().eq("isUpload", false).query();
					if (assets.size() > 0)
						return assets;
				} catch (SQLException e) {
					e.getStackTrace();
				}
				return null;
			}

	// 按数据subType查询
		public List<AssetsPhotosModel> searchAssetsSubName(String dsId, String subTypeName, boolean isUpload) {
			try {
				// 查询的query 返回值是一个列表
				List<AssetsPhotosModel> assets = dao.queryBuilder().where().eq("dsId", dsId).and()
						.like("desc", "%"+subTypeName+"%").and().eq("isUpload", isUpload).query();
				if (assets.size() > 0)
					return assets;
			} catch (SQLException e) {
				e.getStackTrace();
			}
			return null;
		}

}
