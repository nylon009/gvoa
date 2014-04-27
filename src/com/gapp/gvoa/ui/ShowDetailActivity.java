package com.gapp.gvoa.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
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
    
    private ImageButton buttonPlayStop; 
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    
    
	private RssItem rssItem; 
	
	private Thread mThread;  
	
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        
        
        setContentView(R.layout.rss_detail);
    	buttonPlayStop = (ImageButton) findViewById(R.id.play_pause);
    	seekBar = (SeekBar) findViewById(R.id.SeekBar01);  
    	
        rssItem = getIntent().getParcelableExtra(RssItem.class.getName()); 
        Log.i(tag, "load rssitem url="+rssItem.getLink());
        
        TextView title= (TextView) findViewById(R.id.title);
        title.setText(rssItem.getTitle());  
        
        if(rssItem.getStatus()<RssItem.E_PARSE_TXT_OK || null==rssItem.getMp3url())
        {
        	if(mThread == null) {  
                mThread = new Thread(runnable);  
                mThread.start();
            }  
            else {  
                Toast.makeText(getApplication(), getApplication().getString(R.string.thread_started), Toast.LENGTH_LONG).show();  
            }  
        	buttonPlayStop.setEnabled(false);
        	seekBar.setEnabled(false);        	
        } 
        else
        {
       	     TextView detail= (TextView) findViewById(R.id.detail);
             detail.setText(rssItem.getFullText());
             if(rssItem.getStatus()<RssItem.E_DOWN_MP3_OK)
             {
                 if(isWiFi() &&isAutoDownloadMp3()){
                 	
                     mThread = new Thread(runnableMp3);  
                     mThread.start();
                 }
             }
        }   
        initMp3Player();
    }
    
    
    private void initMp3Player() {   
        if(null==rssItem.getLocalmp3())
        {
        	Log.i(tag, "localmp3 is null");
        	buttonPlayStop.setEnabled(false);
        	seekBar.setEnabled(false);
        	return; 
        }    	
    	
    	try {
    		mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(rssItem.getLocalmp3());
			mediaPlayer.prepare();
			buttonPlayStop.setEnabled(true);
			seekBar.setEnabled(true);
        	buttonPlayStop.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {buttonClick();}});
        	seekBar.setMax(mediaPlayer.getDuration());
        	seekBar.setOnTouchListener(new OnTouchListener() {@Override public boolean onTouch(View v, MotionEvent event) {
             	        seekChange(v);
        	            return false; }
                	});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
        	buttonPlayStop.setEnabled(false);
        	seekBar.setEnabled(false);
		} catch (IllegalStateException e) {
			e.printStackTrace();
        	buttonPlayStop.setEnabled(false);
        	seekBar.setEnabled(false);
		} catch (IOException e) {
			e.printStackTrace();
        	buttonPlayStop.setEnabled(false);
        	seekBar.setEnabled(false);
		}
    }
    
    // This is event handler thumb moving event
    private void seekChange(View v){
    	if(mediaPlayer.isPlaying()){
	    	SeekBar sb = (SeekBar)v;
			mediaPlayer.seekTo(sb.getProgress());
		}
    }
 
    // This is event handler for buttonClick event
    private void buttonClick(){
    	if (mediaPlayer.isPlaying())
    	{
    		buttonPlayStop.setImageResource(R.drawable.ic_media_play);
    		mediaPlayer.pause();
    	}
    	else
    	{
    		buttonPlayStop.setImageResource(R.drawable.ic_media_pause);
    		 try{
             	 mediaPlayer.start();
                 startPlayProgressUpdater(); 
             }catch (IllegalStateException e) {
             	mediaPlayer.pause();
             }
    	}
    }
    
    
    public void startPlayProgressUpdater() {
    	seekBar.setProgress(mediaPlayer.getCurrentPosition());
    	
		if (mediaPlayer.isPlaying()) {
			Runnable notification = new Runnable() {
		        public void run() {
		        	startPlayProgressUpdater();
				}
		    };		    
		    mHandler.postDelayed(notification,1000);
    	}else{
    		mediaPlayer.pause();
    		buttonPlayStop.setImageResource(R.drawable.ic_media_play);
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
                	
                    mThread = new Thread(runnableMp3);  
                    mThread.start();
                }
                
            	
                break;    
            case MSG_FAILURE:  
                Toast.makeText(getApplication(), getApplication().getString(R.string.get_rss_failure), Toast.LENGTH_LONG).show(); 
               
                mThread = null;
                break;  
             
            case MSG_MP3:
            	DbRssItem.updateItem(rssItem); 
                if(rssItem.getStatus()==RssItem.E_DOWN_MP3_OK)
                {
                	Toast.makeText(getApplication(), getApplication().getString(R.string.get_mp3_success), Toast.LENGTH_LONG).show(); 
                	initMp3Player();
                }
                else
                {
                	Toast.makeText(getApplication(), getApplication().getString(R.string.get_mp3_failure), Toast.LENGTH_LONG).show(); 
                }           
                mThread = null;
                break;  
            } 
        }  
    };    
    
    
	public boolean isWiFi(){
		ConnectivityManager connectMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectMgr.getActiveNetworkInfo();
			if(info !=null && info.getType() ==  ConnectivityManager.TYPE_WIFI){
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
        	Log.i(tag, "Start download mp3");
            NetworkUtil.downloadMp3 (rssItem);
            mHandler.obtainMessage(MSG_MP3).sendToTarget();
        }  
    }; 
    
    @Override  
    protected void onResume() {  
        super.onResume();  
        Log.e(tag, "start onResume~~~");  
    }  
      
    @Override  
    protected void onPause() {  
        super.onPause();  
        Log.e(tag, "start onPause~~~");  
    }  
      
}
