package com.gapp.gvoa.ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gapp.gvoa.R;
import com.gapp.gvoa.datatype.RssFeed;
import com.gapp.gvoa.datatype.RssItem;
import com.gapp.gvoa.db.DbRssItem;
import com.gapp.gvoa.parser.FeedParser;
import com.gapp.gvoa.util.MsgCenter;
import com.gapp.gvoa.util.MsgCenter.GSubscriber;
import com.gapp.gvoa.util.NetworkUtil;

public class RssItemListActivity extends  Activity implements OnItemClickListener, GSubscriber {
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    
	public static final String TAG = "RssItemListActivity";
    private List<RssItem> rssItemList = null;
    
    private RssFeed  rssFeed = null;    

    private  ListView itemListView= null; 
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);            
        
        MsgCenter.instance().register(this);
        
        
        setContentView(R.layout.rss_item_list);
     
        rssFeed = getIntent().getParcelableExtra(RssFeed.class.getName()); 
        
        TextView titleView =(TextView) findViewById(R.id.rss_list_title);
        titleView.setText(rssFeed.getTitle());
        
        //get item from db
        rssItemList = DbRssItem.getAllItems(rssFeed.getId());
        
        itemListView=(ListView)findViewById(android.R.id.list);
       
        this.refreshList();
        
		this.addListenerOnButton();

    }

	void refreshList()
	{
        ItemListAdapter adapter = new ItemListAdapter(this, rssItemList);
        itemListView.setAdapter(adapter);        
        itemListView.setClickable(true);
        itemListView.setOnItemClickListener(this);    

	}
	
	
    private Thread mThread;  
    
    private Handler mHandler = new Handler() {  
        public void handleMessage (Message msg) {//姝ゆ柟娉曞湪ui绾跨▼杩愯  
            switch(msg.what) {  
            case MSG_SUCCESS:  
                //reload date from db
            	Log.i(TAG, "Reload rss from db now");
            	 rssItemList = DbRssItem.getAllItems(rssFeed.getId());
            	 refreshList();            	
            	mThread = null;
                break;    
            case MSG_FAILURE:  
                Toast.makeText(getApplication(), getApplication().getString(R.string.get_rss_failure), Toast.LENGTH_LONG).show();  
                mThread = null;
                break;  
            }  
        }  
    }; 

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		
		Log.i(TAG, "item clicked! [" + rssItemList.get(arg2).getTitle()
				+ "]");
		//get the content from feed and parse the content
		RssItem rssItem = rssItemList.get(arg2);
		
		Intent itemintent = new Intent(this, ShowDetailActivity.class);

		Bundle bundle = new Bundle();
		bundle.putParcelable(RssItem.class.getName(), rssItem);  
		itemintent.putExtras(bundle);
        startActivity(itemintent);		
	}
	
	
	public void addListenerOnButton() {		 
		ImageButton imageButton = (ImageButton) findViewById(R.id.refresh);
 
		imageButton.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg0) { 
			  Log.i(TAG, "refresh clicked"); 
              if(mThread == null) {  
                  mThread = new Thread(runnable);  
                  mThread.start();
              }  
              else {  
                  Toast.makeText(getApplication(), getApplication().getString(R.string.thread_started), Toast.LENGTH_LONG).show();  
              }  
			} 
		});
 
	}
	
    Runnable runnable = new Runnable() {  
        
        @Override  
        public void run() {
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(rssFeed.getFeedUrl());
                HttpResponse  response = httpclient.execute(request);                   
                if(response.getStatusLine().getStatusCode() == 200){ 
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                    	
                    	InputStream inputStream = entity.getContent();                   	

                    	FeedParser feedFarser = new FeedParser(rssFeed);
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser parser = factory.newSAXParser();
                        XMLReader xmlreader = parser.getXMLReader();
                        xmlreader.setContentHandler(feedFarser);
                        
                        Reader reader = new InputStreamReader(inputStream,"UTF-8");                        
                        InputSource is = new InputSource(reader);
                        is.setEncoding("UTF-8");                       
                        xmlreader.parse(is);
                        
                        Log.i(TAG, "end parse"); 
                        
                        List<RssItem> latestList = feedFarser.getRssFeed().getItemList();
                        
                        for(RssItem item : latestList) {
                        	boolean bExist = false;
                        	for(RssItem oldItem: rssItemList){
                        		if(item.getLink().equals(oldItem.getLink())){                        			
                        			bExist = true;
                        			Log.i(TAG,"Already in DB:"+item.getTitle());
                        			break;
                        		}
                        	}                        	
                        	
                        	if(!bExist)	{
                        		if(NetworkUtil.isReachable(item.getLink()))	{
                        			DbRssItem.addRssItem(item);
                        		}   else{
                            		Log.w(TAG,"Not reachable:" +item.getLink());
                            	}                   		
                        	}
                        	
                        }
                    }                	
                }
            } catch (Exception e) {  
            	 Log.e(TAG, "Connect or parse Error", e);
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                return;  
            }  
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }  
    }; 
	
	@Override
	public void onMessage(final Message msg) {
    	Log.i(TAG, "Get registered message from MsgCenter");
    	
    	this.runOnUiThread(new Runnable() {
    		  public void run() {
    			  handleMsg(msg);
    		  }
    		});	

	}
	
	
	private void handleMsg(Message msg){
		
		RssItem updatedItem = (RssItem) msg.obj;
		
		int itemIndex = 0;
		
		boolean bFound = false; 
		Log.i(TAG, ""+updatedItem);

		for(RssItem item : rssItemList){
			Log.i(TAG, ""+item);
			if(item.getId()==updatedItem.getId()){				
				item.updateValues(updatedItem);
				bFound=true;
				break;
			}
			itemIndex++;
		}
		
		if (!bFound){		
			Log.i(TAG,"cannt find updatedItem");
			return;
		}	

		int start = itemListView.getFirstVisiblePosition();
		int end = itemListView.getLastVisiblePosition(); 
		Log.i(TAG,"start="+start+",end="+end+",itemIndex="+itemIndex);
		if(start<=itemIndex && itemIndex<=end){
			View view = itemListView.getChildAt(itemIndex-start);
			itemListView.getAdapter().getView(itemIndex, view, itemListView);
		}
		

	}
	
    	
    @Override  
    protected void onDestroy() {  
    	MsgCenter.instance().unRegister(this); 
        super.onDestroy();  
        Log.i(TAG, "onDestroy");  
    }

}
