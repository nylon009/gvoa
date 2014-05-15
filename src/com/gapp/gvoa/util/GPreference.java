package com.gapp.gvoa.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class GPreference {
	
	private final static String tag = "GPreference";
	
	static Activity mainActivity=null;
	
	public static void init(Activity activity)
	{
		mainActivity = activity;
	}
	
	
	public static int getNetWork(){
		int ret = 0XFFFF;
		
		ConnectivityManager connectMgr = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectMgr.getActiveNetworkInfo();
			
			if(info!=null)
			{
				ret = info.getType();
			}
			Log.e(tag, "networkStatus="+ret);
			return ret; 
	}
	
	public static String downloadMp3Pref()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);    	
		String downLoadMp3Str = prefs.getString("pref_autodownload_mp3", "WIFI_ONLY");
		 Log.e(tag, "downLoadMp3Str="+downLoadMp3Str);
    	return downLoadMp3Str;
	}
	
	
	public static float getPreferredTextSize()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);    	
    	String textSizeStr= prefs.getString("pref_detail_text_size", "20");
    	return Float.valueOf(textSizeStr);
	}
	

	public static int getPreferredExpireDays()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);    	
    	String expireStr= prefs.getString("pref_remove_mp3_x_days_before", "7");
    	return Integer.valueOf(expireStr);
	}
	
}
