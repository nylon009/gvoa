package com.gapp.gvoa.db;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GRSSDbHandler extends SQLiteOpenHelper {
	private static final String TAG = "GVOA";
	
	// Database Version
	private static final int DATABASE_VERSION = 3;

	private static String DB_PATH = "/data/data/com.gapp.gvoa/databases/";
	private static final String DATABASE_NAME = "rssReader.db";

	private static GRSSDbHandler instance = null;

	public static GRSSDbHandler getInstance() {
		return instance;
	}

	public static synchronized void initInstance(Context context) {
		if (null == instance) {
			instance = new GRSSDbHandler(context);
		}
	}

	public GRSSDbHandler(Context context) {		
		super(context, DB_PATH+DATABASE_NAME, null, DATABASE_VERSION);
	}

	
	@Override
	public synchronized void close() {
		Log.d(TAG,"Close myDataBase");
		super.close();

	}


	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		    db.execSQL(DbRssFeed.CREATE_TABLE_RSS_FEED);
		    db.execSQL(DbRssItem.CREATE_TABLE_RSS_ITEM);	 
		    
		    List<String> sqlList = DbRssFeed.firstTimeInitSql(); 
            for(String sql :sqlList)
            {
            	db.execSQL(sql);
            }	    
	}
	

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ com.gapp.gvoa.db.DbRssFeed.TABLE_RSSFEED);
		db.execSQL("DROP TABLE IF EXISTS "+ com.gapp.gvoa.db.DbRssItem.TABLE_RSSITEM);
        onCreate(db);
	}

}
