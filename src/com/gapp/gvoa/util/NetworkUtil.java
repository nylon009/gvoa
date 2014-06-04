package com.gapp.gvoa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gapp.gvoa.datatype.RssItem;
import com.gapp.gvoa.db.DbRssItem;
import com.gapp.gvoa.ui.ShowDetailActivity;

public class NetworkUtil {

	public static final String tag = "NetworkUtil";
	
    public  static void downloadMp3(RssItem rssItem, Handler handler)
    {
    	
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

    		String mp3FilePath = GvoaUtil.getLocalMp3Path(rssItem.getMp3url());
    		Log.w(tag,"save mp3 to "+mp3FilePath);
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
    			
    			Message msg = handler.obtainMessage(ShowDetailActivity.MSG_MP3_PROGRESS); 
    			msg.arg1=downloadedSize;
    			msg.arg2=totalSize;
    			msg.obj=rssItem; 
    			msg.sendToTarget();    			

    		}
    		Log.i(tag, "download "+downloadedSize+" of "+totalSize);
    		//close the output stream when done
    		fileOutput.close();
    		rssItem.setLocalmp3(mp3FilePath);
    		rssItem.setStatus(RssItem.E_DOWN_MP3_OK);
        	int updatedmp3=DbRssItem.updateItem(rssItem);         	
        	Log.i(tag, "updatedmp3="+updatedmp3);
        	
        	//handler.obtainMessage(ShowDetailActivity.MSG_MP3).sendToTarget();
        	Message audioOKMsg= Message.obtain();
        	audioOKMsg.what=ShowDetailActivity.MSG_MP3;  
        	audioOKMsg.obj=rssItem;
        	MsgCenter.instance().postMessage(audioOKMsg);
        	
        	

    	//catch some possible errors...
    	} catch (MalformedURLException e) {
    		rssItem.setStatus(RssItem.E_DOWN_MP3_FAIL);
    		e.printStackTrace();
    	} catch (IOException e) {
    		rssItem.setStatus(RssItem.E_DOWN_MP3_FAIL);
    		e.printStackTrace();
    	}
    }
	

    
    public static boolean isReachable(String url){
        try {
          HttpURLConnection.setFollowRedirects(false);
          // note : you may also need
          //        HttpURLConnection.setInstanceFollowRedirects(false)
          HttpURLConnection con =
             (HttpURLConnection) new URL(url).openConnection();
          con.setRequestMethod("HEAD");
          return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
           e.printStackTrace();
           return false;
        }
      }
    
    public static String httpGetContent(String urlstr) throws  Exception
    {
    	String retStr = null;
 		
		URL url = new URL(urlstr);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("GET");
    	urlConnection.connect();
    		
 
        InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());  
        BufferedReader buffer = new BufferedReader(in);  
        String inputLine = null;  
        while (((inputLine = buffer.readLine()) != null))  
        {  
        	retStr += inputLine + "\n";  
        }    		

    	
    	return retStr;
    }
	
	
}
