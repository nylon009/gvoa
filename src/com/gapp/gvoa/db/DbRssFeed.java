package com.gapp.gvoa.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gapp.gvoa.datatype.RssFeed;

public class DbRssFeed {

 
    // Contacts table name
    public static final String TABLE_RSSFEED = "trssfeed";
 
    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LINK = "feedUrl";
    public static final String KEY_DESCRIPTION = "feedDescription";
	
    public static final String CREATE_TABLE_RSS_FEED = "CREATE TABLE " + TABLE_RSSFEED + "(" 
                                 + KEY_ID  + " INTEGER PRIMARY KEY," 
    		                     + KEY_TITLE + " TEXT," 
                                 + KEY_LINK  + " TEXT," 
    		                     + KEY_DESCRIPTION + " TEXT" + ")";
	
    
    /**
     * Reading all rows from database
     * */
    public static List<RssFeed> getAllFeeds() {
        List<RssFeed> feedList = new ArrayList<RssFeed>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RSSFEED  + " ORDER BY id DESC"; 
        SQLiteDatabase db = GRSSDbHandler.getInstance().getMyDataBase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {           	
            	RssFeed feed = new RssFeed(Integer.parseInt(cursor.getString(0))
                		                		   ,cursor.getString(1)
                		                		   ,cursor.getString(2)
                		                		   ,cursor.getString(3));
                feedList.add(feed);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return feedList;
    }
    
}
