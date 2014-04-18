package com.gapp.gvoa.ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gapp.gvoa.R;
import com.gapp.gvoa.datatype.RssItem;
import com.gapp.gvoa.db.DbRssItem;
import com.gapp.gvoa.parser.FeedParser;
import com.gapp.gvoa.parser.ItemHtmlParser;

public class ShowDetailActivity extends Activity 
{
	public final String tag = "ShowDetailActivity";
	private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    
	private RssItem rssItem; 
	
	private Thread mThread;  
	
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.rss_detail);
        
        rssItem = getIntent().getParcelableExtra(RssItem.class.getName()); 
        
        TextView title= (TextView) findViewById(R.id.title);
        title.setText(rssItem.getTitle());  
        
        if(rssItem.getFullText()==null || rssItem.getFullText() =="")
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
            	mThread = null;
                break;    
            case MSG_FAILURE:  
                Toast.makeText(getApplication(), getApplication().getString(R.string.get_rss_failure), Toast.LENGTH_LONG).show();  
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
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                return;  
            }  
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }  
    }; 
    
}
