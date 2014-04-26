package com.gapp.gvoa.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;

import com.gapp.gvoa.datatype.RssItem;

public class DbRssItem {

    // Contacts table name
    public static final String TABLE_RSSITEM = "trssitem";
 
    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_FEED_ID = "feedid";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_LINK = "link";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_FULL_TEXT = "fullText";
    public static final String KEY_MP3_URL = "mp3url";
    public static final String KEY_LOCAL_MP3 = "localmp3";
    public static final String KEY_STATUS = "status";
	
    public static final String CREATE_TABLE_RSS_ITEM = "CREATE TABLE " + TABLE_RSSITEM + "(" 
                  	      + KEY_ID    + " INTEGER PRIMARY KEY," 
                  	      + KEY_FEED_ID + " INTEGER," 
    		              + KEY_TITLE + " TEXT," 
    		              + KEY_DATE + " TEXT," 
                          + KEY_LINK  + " TEXT," 
    		              + KEY_DESCRIPTION + " TEXT, "
    		              + KEY_FULL_TEXT + " TEXT, "
    		              + KEY_MP3_URL + " TEXT, "
                          + KEY_LOCAL_MP3 + " TEXT ,"
    		              + KEY_STATUS + " INTEGER" 
                          + ")";
	    
    /**
     * Adding a new RssItem in RssItems table Function will check if a site
     * already existed in database. If existed will update the old one else
     * creates a new row
     * */
    public static void addRssItem(RssItem item) {
    	//Log.i("GVOA", "addRssItem "+item); 
        SQLiteDatabase db = GRSSDbHandler.getInstance().getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_FEED_ID, item.getFeedID()); // site title
        values.put(KEY_TITLE, item.getTitle()); // site title
        values.put(KEY_DATE, item.getPubDate());        
        values.put(KEY_LINK, item.getLink()); // site url
        values.put(KEY_DESCRIPTION, item.getDescription()); // site description
        values.put(KEY_FULL_TEXT, item.getFullText());
        values.put(KEY_MP3_URL, item.getMp3url());
        values.put(KEY_LOCAL_MP3,item.getLocalmp3());
        values.put(KEY_STATUS,item.getStatus());
        // Check if row already existed in database
        if (!isItemExists(db, item.getLink())) {
            // site not existed, create a new row
            db.insert(TABLE_RSSITEM, null, values);
        } else {
            // site already existed update the row
            updateItem(item);
        }
       //db.close();
    }
 
    /**
     * Reading all rows from database
     * */
    public static List<RssItem> getAllItems(Integer feedid) {
        List<RssItem> itemList = new ArrayList<RssItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RSSITEM
                + " where "+ KEY_FEED_ID + "=" + feedid + " ORDER BY id DESC";
 
        SQLiteDatabase db = GRSSDbHandler.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {           	
                RssItem site = new RssItem(Integer.parseInt(cursor.getString(0))
                		                   ,Integer.parseInt(cursor.getString(1))
                		                		   ,cursor.getString(2)
                		                		   ,cursor.getString(3)
                		                		   ,cursor.getString(4)
                		                		   ,cursor.getString(5)
                		                		   ,cursor.getString(6)
                		                		   ,cursor.getString(7)
                		                		   ,cursor.getString(8)
                		                		   ,Integer.parseInt(cursor.getString(9)));
                itemList.add(site);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }
 
    /**
     * Updating a single row row will be identified by rss link
     * */
    public static int updateItem(RssItem item) {
        SQLiteDatabase db = GRSSDbHandler.getInstance().getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_FEED_ID, item.getFeedID()); // site feedid
        values.put(KEY_TITLE, item.getTitle()); // site title
        values.put(KEY_DATE, item.getPubDate());        
        values.put(KEY_LINK, item.getLink()); // site url
        values.put(KEY_DESCRIPTION, item.getDescription()); // site description
        values.put(KEY_FULL_TEXT, item.getFullText()); 
        values.put(KEY_MP3_URL, item.getMp3url());
        values.put(KEY_LOCAL_MP3, item.getLocalmp3());
        values.put(KEY_STATUS, item.getStatus());
        
        // updating row return
        int update = db.update(TABLE_RSSITEM, values, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId()) });
        //db.close();
        return update;
 
    }
 
 
    /**
     * Deleting single row
     * */
    public static void deleteItem(RssItem item) {
        SQLiteDatabase db = GRSSDbHandler.getInstance().getWritableDatabase();
        db.delete(TABLE_RSSITEM, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId())});
        db.close();
    }
 
    /**
     * Checking whether a site is already existed check is done by matching rss
     * link
     * */
    public static boolean isItemExists(SQLiteDatabase db, String item_link) { 
    	boolean bexists = false; 
    	
    	String queryStr = "SELECT 1 FROM " + TABLE_RSSITEM + " WHERE " + KEY_LINK + " = ?";
        
        try{
            SQLiteStatement stmt = db.compileStatement(queryStr);
            stmt.bindString(1, item_link);
            long ret = stmt. simpleQueryForLong();
            bexists = true; 
        } 
        catch (SQLiteDoneException e)
        {
        	bexists = false;
        }
        return bexists;
    }
    
}
