package com.gapp.gvoa.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GRSSDbHandler extends SQLiteOpenHelper {
	private static final String TAG = "GVOA";
	
	// Database Version
	private static final int DATABASE_VERSION = 1;

	private static String DB_PATH = "/data/data/com.gapp.gvoa/databases/";

	// Database Name
	private static final String DATABASE_NAME = "rssReader.db";

	private final Context myContext;
	
	private SQLiteDatabase myDataBase; 
	
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
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
		
		Log.d(TAG,DbRssFeed.CREATE_TABLE_RSS_FEED);
		Log.d(TAG,DbRssItem.CREATE_TABLE_RSS_ITEM);
		
		createDataBase();
		openDataBase();
	}

	public void createDataBase() {
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getReadableDatabase();			
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		//return checkDB != null ? true : false;
		return false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		Log.d(TAG,"copyDataBase");
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DATABASE_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() {

		// Open the database
		String myPath = DB_PATH + DATABASE_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,	SQLiteDatabase.OPEN_READWRITE);

	}

	@Override
	public synchronized void close() {
		Log.d(TAG,"Close myDataBase");
		if (myDataBase != null)
		{
			myDataBase.close();
			myDataBase = null;
		}
		super.close();

	}

	public SQLiteDatabase getMyDataBase() {
		return myDataBase;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL(DbRssFeed.CREATE_TABLE_RSS_FEED);
		//db.execSQL(DbRssItem.CREATE_TABLE_RSS_ITEM);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
