package com.gapp.gvoa.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.gapp.gvoa.R;
import com.gapp.gvoa.datatype.RssItem;
import com.gapp.gvoa.db.DbRssItem;
import com.gapp.gvoa.parser.ItemHtmlParser;
import com.gapp.gvoa.util.NetworkUtil;

public class ShowDetailActivity extends Activity 
{
	public final String tag = "ShowDetailActivity";
	private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    private static final int MSG_MP3 = 2;
    
    
	private RssItem rssItem; 
	
	private Thread mThread;  
	
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.rss_detail);
        
        rssItem = getIntent().getParcelableExtra(RssItem.class.getName()); 
        
        TextView title= (TextView) findViewById(R.id.title);
        title.setText(rssItem.getTitle());  
        
        if(rssItem.getStatus()<RssItem.E_PARSE_TXT_OK)
        {
        	if(mThread == null) {  
                mThread = new Thread(runnable);  
                mThread.start();
            }  
            else {  
                Toast.makeText(getApplication(), getApplication().getString(R.string.thread_started), Toast.LENGTH_LONG).show();  
            }  
        } 
        else
        {
       	     TextView detail= (TextView) findViewById(R.id.detail);
             detail.setText(rssItem.getFullText());  
        }    
    }
    
    
    
    private Handler mHandler = new Handler() {  
        public void handleMessage (Message msg) {
            switch(msg.what) {  
            case MSG_SUCCESS:  
                //reload date from db
            	Log.i(tag, "Parse rssItem SUCCESS");
            	TextView detail= (TextView) findViewById(R.id.detail);
                detail.setText(rssItem.getFullText()); 
                DbRssItem.updateItem(rssItem);                 
                
                //TODO: go on download mp3 according to settings   
                if(isWiFi() &&isAutoDownloadMp3()){
                	
                	
                }
                
            	mThread = null;
                break;    
            case MSG_FAILURE:  
                Toast.makeText(getApplication(), getApplication().getString(R.string.get_rss_failure), Toast.LENGTH_LONG).show(); 
               
                mThread = null;
                break;  
             
            case MSG_MP3:  
                if(rssItem.getStatus()==RssItem.E_DOWN_MP3_OK)
                {
                	mThread = new Thread(runnableMp3);  
                    mThread.start();
                }
                else
                {
                	Toast.makeText(getApplication(), getApplication().getString(R.string.get_rss_failure), Toast.LENGTH_LONG).show(); 
                }           
                mThread = null;
                break;  
            } 
        }  
    };    
    
    
	public boolean isWiFi(){
		ConnectivityManager connectMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectMgr.getActiveNetworkInfo();
			if(info !=null && info.getType() ==  ConnectivityManager.TYPE_MOBILE){
				return true;
			}
			return false;
	}
	
	public boolean isAutoDownloadMp3()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);    	
    	boolean flag = prefs.getBoolean("pref_autodownload_mp3", false);
    	return flag;
	}
	
    
     Runnable runnable = new Runnable() {  
        
        @Override  
        public void run() {  
            try {
            	ItemHtmlParser.parseItemDetail(rssItem);
            } catch (Exception e) {  
            	 Log.e("GVOA", "Connect or parse Error", e);
            	 rssItem.setStatus(RssItem.E_PARSE_TXT_FAIL);
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                return;  
            }  
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }  
    }; 
    
     Runnable runnableMp3 = new Runnable() {  
          
        @Override  
        public void run() {             
            NetworkUtil.downloadMp3 (rssItem);
            mHandler.obtainMessage(MSG_MP3).sendToTarget();
        }  
    }; 
    
    
}
