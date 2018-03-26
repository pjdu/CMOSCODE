package cn.com.chinaccs.datasite.main.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;

import cn.com.chinaccs.datasite.main.db.model.AssetsModel;
import cn.com.chinaccs.datasite.main.db.model.AssetsPhotosModel;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{  
 
    // 数据库名称   
    private static final String DATABASE_NAME = SDBHelper.DB_DIR + File.separator + "wdatasite.db";
    // 数据库version   
    private static final int DATABASE_VERSION = 2;  
  
    private Dao<AssetsModel, Integer> assetsDao = null;
    private Dao<AssetsPhotosModel, Integer> assetsPhotoDao = null;
    
  
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
     }


    @Override
     public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
        	
       
         TableUtils.createTable(connectionSource, AssetsModel.class);
         TableUtils.createTable(connectionSource, AssetsPhotosModel.class);
        
         
       } catch (SQLException e) {
         e.printStackTrace();
      }
                  
     }



  @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int arg2, int arg3) {
       try {
       
        TableUtils.dropTable(connectionSource, AssetsModel.class, true);
        TableUtils.dropTable(connectionSource, AssetsPhotosModel.class, true);
       
        
        onCreate(db, connectionSource);
      } catch (SQLException e) {
        e.printStackTrace();
      }
  }

  
   
    
    public Dao<AssetsModel, Integer> getAssetsDao()
    {  
        if (assetsDao == null)
			try {
				assetsDao = getDao(AssetsModel.class);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}  
        return assetsDao;  
    }  
    
    public Dao<AssetsPhotosModel, Integer> getAssetsPhotoDao()
    {  
        if (assetsPhotoDao == null)
			try {
				assetsPhotoDao = getDao(AssetsPhotosModel.class);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}  
        return assetsPhotoDao;  
    }  
  
      
    /** 
     * 释放 DAO 
     */  
    @Override
    public void close() {  
        super.close();      
        assetsDao = null;
        assetsPhotoDao = null;
    }  
  
}  

