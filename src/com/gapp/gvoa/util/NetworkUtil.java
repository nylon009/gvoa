package com.gapp.gvoa.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

import com.gapp.gvoa.datatype.RssItem;

public class NetworkUtil {

	public static final String tag = "NetworkUtil";
	
	public static boolean isFileExists(String filePath)
	{
		File file = new File(filePath);
		if(file.exists())
		{
			 return true;
		}		    
		return false;
	}
	
	private static String getLocalMp3Path(String url)
	{
		String md5str=MD5.getMD5(url.getBytes());
		String sdRoot=Environment.getExternalStorageDirectory().getPath();
		String destFile = sdRoot + "/" +"gvoa" + "/"+ md5str;
		
		
		
		return destFile; 	
	}
	
	
    public  static void downloadMp3(RssItem rssItem)
    {
		
		
		//file already downloaded,no need download again
		if (rssItem.getLocalmp3()!=null && isFileExists(rssItem.getLocalmp3()))
		{
			Log.i(tag, "mp3 is already downloaded");
			return;
		}
		
		if (rssItem.getMp3url()==null)
		{
			Log.w(tag,"mp3url is null");
			return;
		}	
    	
    	try { 		
    		URL url = new URL(rssItem.getMp3url());
    		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    		urlConnection.setRequestMethod("GET");
    		//urlConnection.setDoOutput(true);
    		urlConnection.connect();

    		String mp3FilePath = getLocalMp3Path(rssItem.getMp3url());
    		
    		File file = new File(mp3FilePath);
    		if(file.isDirectory())
    		{
    			file.delete();
    		}
    		
    		//file.mkdirs(); 
    		//this will be used to write the downloaded data into the file we created
    		FileOutputStream fileOutput = new FileOutputStream(file);

    		//this will be used in reading the data from the internet
    		InputStream inputStream = urlConnection.getInputStream();

    		//this is the total size of the file
    		int totalSize = urlConnection.getContentLength();
    		//variable to store total downloaded bytes
    		int downloadedSize = 0;

    		//create a buffer...
    		byte[] buffer = new byte[1<<14];
    		int bufferLength = 0; //used to store a temporary size of the buffer

    		//now, read through the input buffer and write the contents to the file
    		while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
    			//add the data in the buffer to the file in the file output stream (the file on the sd card
    			fileOutput.write(buffer, 0, bufferLength);
    			//add up the size so we know how much is downloaded
    			downloadedSize += bufferLength;
    			//Log.i(tag, "download "+downloadedSize+" of "+totalSize);

    		}
    		Log.i(tag, "download "+downloadedSize+" of "+totalSize);
    		//close the output stream when done
    		fileOutput.close();
    		rssItem.setLocalmp3(mp3FilePath);
    		rssItem.setStatus(RssItem.E_DOWN_MP3_OK);
    	//catch some possible errors...
    	} catch (MalformedURLException e) {
    		rssItem.setStatus(RssItem.E_DOWN_MP3_FAIL);
    		e.printStackTrace();
    	} catch (IOException e) {
    		rssItem.setStatus(RssItem.E_DOWN_MP3_FAIL);
    		e.printStackTrace();
    	}
    	// see http://androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
    }
	

	
	
}
