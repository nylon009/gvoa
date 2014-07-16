package com.gapp.gvoa.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.util.Log;

public class GvoaUtil {

	private final static String TAG = "GvoaUtil";
	
	public static boolean isFileExists(String filePath)
	{
		File file = new File(filePath);
		if(file.exists())
		{
			 return true;
		}		    
		return false;
	}
	
	
	public static String datefmtNoDash(String pubDate)
	{
		String inputDateStr= pubDate.trim();		
		java.util.Date date = null; 		
		try
		{		
		    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟   
		    date=sdf.parse(inputDateStr); 		 
		}
		catch (Exception e)
		{
			Log.w(TAG, pubDate +" convert to date error");
			date = new java.util.Date(); 
		}	
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMdd");
		return sdf2.format(date);  
	}
	
	public static String getLocalMp3Path(String pubDate, String url)
	{
		String sdRoot=Environment.getExternalStorageDirectory().getPath();		
		
		File gvoaFile = new File(sdRoot + "/" +"gvoa");
		if(!gvoaFile.exists()){
			gvoaFile.mkdir(); 
		}	
		
		
		String path=null;
		try {
			path = new URL(url).getPath();
		} catch (MalformedURLException e) {
			Log.w(TAG,e.getMessage());
			path = MD5.getMD5(url.getBytes());
		}	
		String fileName = GvoaUtil.datefmtNoDash(pubDate)+"_"+path.substring(path.lastIndexOf("/")+1);
		
		
		String destFile = sdRoot + "/" +"gvoa" + "/"+ fileName;	
		
		return destFile; 	
	}
	
}
