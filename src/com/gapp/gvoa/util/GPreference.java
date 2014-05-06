package com.gapp.gvoa.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class GPreference {
	
	static Activity mainActivity=null;
	
	public static void init(Activity activity)
	{
		mainActivity = activity;
	}
	
	
	public static boolean isWiFi(){
		ConnectivityManager connectMgr = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectMgr.getActiveNetworkInfo();
			if(info !=null && info.getType() ==  ConnectivityManager.TYPE_WIFI){
				return true;
			}
			return false;
	}
	
	public static boolean isAutoDownloadMp3()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);    	
    	boolean flag = prefs.getBoolean("pref_autodownload_mp3", false);
    	return flag;
	}
	
	
	public static float getPreferredTextSize()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);    	
    	String textSizeStr= prefs.getString("pref_detail_text_size", "20");
    	return Float.valueOf(textSizeStr);
	}
	

}
