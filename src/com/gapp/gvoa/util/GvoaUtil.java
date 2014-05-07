package com.gapp.gvoa.util;

import java.io.File;

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
	
}
