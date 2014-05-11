package com.gapp.gvoa.ui;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gapp.gvoa.R;
import com.gapp.gvoa.datatype.RssItem;
import com.gapp.gvoa.db.DbRssItem;
import com.gapp.gvoa.parser.ItemHtmlParser;
import com.gapp.gvoa.util.GPreference;
import com.gapp.gvoa.util.NetworkUtil;

public class ShowDetailActivity extends Activity 
{
	private final String tag = "ShowDetailActivity";
	public static final int MSG_SUCCESS = 0;
	public static final int MSG_FAILURE = 1;
	public static final int MSG_MP3_PROGRESS = 2;
	public static final int MSG_MP3 = 3;
	
	
	private static final int EXTRA_SHOW_NONE=0;
	private static final int EXTRA_SHOW_DOWNLOADING=1;
	private static final int EXTRA_SHOW_PLAYCONTROL=2;
    
	private RelativeLayout mPlayView=null; 
	private View mDownloadView=null;
	
    private ImageButton buttonPlayStop; 
    private SeekBar seekBar;
    private ProgressBar mProgressBar;
    
    private MediaPlayer mediaPlayer=null;
    
    
	private RssItem rssItem; 
	
	private Thread mThread;  
	
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        
        
        setContentView(R.layout.rss_detail);

    	
        rssItem = getIntent().getParcelableExtra(RssItem.class.getName()); 
        Log.i(tag, "load rssitem url="+rssItem.getLink());
        
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
       	     detail.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, GPreference.getPreferredTextSize());
             detail.setText(rssItem.getFullText());
             if(rssItem.getStatus()<RssItem.E_DOWN_MP3_OK)
             {
            	 if(null==rssItem.getMp3url())
            	 {
            		 Toast.makeText(getApplication(), getApplication().getString(R.string.no_mp3_content), Toast.LENGTH_LONG).show(); 
            	 }else if(GPreference.isWiFi() &&GPreference.isAutoDownloadMp3()){
                 	
                     mThread = new Thread(runnableMp3);  
                     mThread.start();
                 }
             }
        } 
        
        if(null==rssItem.getMp3url())
        {
        	showExtra(EXTRA_SHOW_NONE);
        }
        else if(null==rssItem.getLocalmp3())
        {
        	Log.i(tag, "localmp3 is null");
        	showExtra(EXTRA_SHOW_DOWNLOADING);
        }
        else
        {
        	showExtra(EXTRA_SHOW_PLAYCONTROL);
            initMp3Player();
        }
    }
    
    
    void showExtra(int showType)
    {    	
    	LinearLayout detail_linear_layout_view = (LinearLayout) findViewById(R.id.detail_linear_layout); 
    	switch( showType)
    	{
    		case EXTRA_SHOW_NONE:
    			break;
    		case EXTRA_SHOW_DOWNLOADING:
        		if(null!=mPlayView)
        		{
        		    detail_linear_layout_view.removeView(mPlayView);
        		    mPlayView.setVisibility(View.GONE);
        		}
        		mDownloadView = (View) LayoutInflater.from(this).inflate(R.layout.download_mp3, null);     	
            	detail_linear_layout_view.addView(mDownloadView);
            	mProgressBar = (ProgressBar)  findViewById(R.id.downprogressloadbar); 
    			break;
    		case EXTRA_SHOW_PLAYCONTROL:           	
            	if(null!=mDownloadView)
            	{
        		    Log.i(tag,"remove mDownloadView");
            		detail_linear_layout_view.removeView(mDownloadView);
        		    mDownloadView.setVisibility(View.GONE);
            	}
        		mPlayView =(RelativeLayout) LayoutInflater.from(this).inflate(R.layout.audio_play, null);      		       	
            	detail_linear_layout_view.addView(mPlayView);
            	buttonPlayStop = (ImageButton) findViewById(R.id.play_pause);
            	seekBar = (SeekBar) findViewById(R.id.SeekBar01);  
    			break;
    			
    	}
  	
    }
    
    private void initMp3Player() {  	
    	
    	try {

    		mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(rssItem.getLocalmp3());
			mediaPlayer.prepare();
        	buttonPlayStop.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {buttonClick();}});
        	seekBar.setMax(mediaPlayer.getDuration());
        	seekBar.setOnTouchListener(new OnTouchListener() {@Override public boolean onTouch(View v, MotionEvent event) {
             	        seekChange(v);
        	            return false; }
                	});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

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
            	detail.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, GPreference.getPreferredTextSize());
                detail.setText(rssItem.getFullText()); 
                DbRssItem.updateItem(rssItem);                 
                
                if(null!=rssItem.getMp3url())
                {
                	showExtra(EXTRA_SHOW_DOWNLOADING);
                }                
                
                if(GPreference.isWiFi() &&GPreference.isAutoDownloadMp3()){
                	
                    mThread = new Thread(runnableMp3);  
                    mThread.start();
                }
                else
                {
                	Log.i(tag, "Don't download mp3");
                }
                
            	
                break;    
            case MSG_FAILURE:  
                Toast.makeText(getApplication(), getApplication().getString(R.string.get_rss_failure), Toast.LENGTH_LONG).show(); 
               
                mThread = null;
                break;  
             
                
            case MSG_MP3_PROGRESS:
    			Integer  downloadedSize =msg.arg1;
    			Integer  totalSize =msg.arg2;
    			RssItem item = (RssItem) msg.obj;
    		    if(item == rssItem  && null!=mProgressBar)
    		    {
    		    	mProgressBar.setProgress(downloadedSize*100/totalSize);
    		    }           	
            	
            	break;
            case MSG_MP3:

                if(rssItem.getStatus()==RssItem.E_DOWN_MP3_OK)
                {
                	Toast.makeText(getApplication(), getApplication().getString(R.string.get_mp3_success), Toast.LENGTH_LONG).show();
                   	showExtra(EXTRA_SHOW_PLAYCONTROL);               	
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
            NetworkUtil.downloadMp3 (rssItem, mHandler);
            
        }  
    }; 
    
    @Override  
    protected void onStart() {  
        super.onStart();  
        Log.i(tag, "onStart");  
    } 
    
    @Override  
    protected void onResume() {  
        super.onResume();  
        
        Log.i(tag, "onResume");  
    }  
      
    @Override  
    protected void onPause() {  	
    	
        super.onPause();  
        Log.i(tag, "onPause");  
    }  
 
    @Override  
    protected void onStop() {      	
        super.onStop();  
        Log.i(tag, "onStop");  
    }  
    @Override  
    protected void onDestroy() {  
    	if(mediaPlayer!=null && mediaPlayer.isPlaying())
    	{
    		mediaPlayer.stop();
    	} 
        super.onDestroy();  
        Log.i(tag, "onDestroy");  
    }  
}
