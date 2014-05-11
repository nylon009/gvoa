package com.gapp.gvoa.util;

import java.io.File;

import android.os.Environment;

public class GvoaUtil {

	public static boolean isFileExists(String filePath)
	{
		File file = new File(filePath);
		if(file.exists())
		{
			 return true;
		}		    
		return false;
	}
	
	public static String getLocalMp3Path(String url)
	{
		String md5str=MD5.getMD5(url.getBytes());
		String sdRoot=Environment.getExternalStorageDirectory().getPath();
		String destFile = sdRoot + "/" +"gvoa" + "/"+ md5str +".mp3";
		
		
		
		return destFile; 	
	}
	
}
